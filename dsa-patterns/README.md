# Common DSA Patterns

## 5. Common DSA patterns

### Two-sum lookup with a HashMap

``` java
static int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> indexByValue = new HashMap<>();

    for (int i = 0; i < nums.length; i++) {
        int need = target - nums[i];

        if (indexByValue.containsKey(need)) {
            return new int[] { indexByValue.get(need), i };
        }

        indexByValue.put(nums[i], i);
    }

    return new int[] {-1, -1};
}
```

Average time: O(n). Extra space: O(n).

### Character frequency

``` java
static Map<Character, Integer> charFrequency(String s) {
    Map<Character, Integer> freq = new HashMap<>();

    for (char c : s.toCharArray()) {
        freq.merge(c, 1, Integer::sum);
    }

    return freq;
}
```

### First non-repeating character

``` java
static Optional<Character> firstUniqueChar(String s) {
    Map<Character, Long> freq = s.chars()
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(
            Function.identity(),
            LinkedHashMap::new,
            Collectors.counting()
        ));

    return freq.entrySet().stream()
        .filter(e -> e.getValue() == 1)
        .map(Map.Entry::getKey)
        .findFirst();
}
```

`LinkedHashMap` preserves insertion order. A plain `HashMap` does not
promise that order.

### Remove duplicates while preserving order

``` java
List<Integer> uniqueInOrder = new ArrayList<>(new LinkedHashSet<>(nums));
```

### Sliding-window frequency map reminder

For substring/subarray problems, update counts as the window moves.
Remove a key when its count becomes zero if the algorithm relies on the
map size.

``` java
freq.merge(value, 1, Integer::sum); // add to window

freq.computeIfPresent(value, (k, count) ->
    count == 1 ? null : count - 1
); // remove one occurrence; null removes the key
```

### BFS queue

``` java
Queue<Integer> queue = new ArrayDeque<>();
boolean[] visited = new boolean[n];

queue.offer(start);
visited[start] = true;

while (!queue.isEmpty()) {
    int node = queue.poll();

    for (int next : graph.get(node)) {
        if (!visited[next]) {
            visited[next] = true;
            queue.offer(next);
        }
    }
}
```

Mark a node visited when you enqueue it. This prevents duplicate queue
entries in standard BFS.

### Monotonic stack skeleton

``` java
Deque<Integer> stack = new ArrayDeque<>();

for (int i = 0; i < nums.length; i++) {
    while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
        int index = stack.pop();
        // nums[i] is the next greater element for index
    }

    stack.push(i);
}
```

Choose `<` or `<=` based on whether equal values should remain on the
stack.

### K-th largest using a size-K min-heap

``` java
static int kthLargest(int[] nums, int k) {
    if (k < 1 || k > nums.length) {
        throw new IllegalArgumentException("Invalid k");
    }

    PriorityQueue<Integer> minHeap = new PriorityQueue<>();

    for (int x : nums) {
        minHeap.offer(x);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }

    return minHeap.peek();
}
```

Time: O(n log k). Extra space: O(k). Duplicates count as separate
elements.

------------------------------------------------------------------------


---

## 9. Complexity reminders for common patterns

  Pattern                             Typical time              Extra space
  ------------------------------- ---------------- ------------------------
  Frequency map                       O(n) average                     O(k)
  Two sum with hash map               O(n) average                     O(n)
  Sort then scan                        O(n log n)          Depends on sort
  Stream `distinct()`               Typically O(n)                     O(n)
  Stream `sorted()`                     O(n log n)                     O(n)
  Top K using full sort                 O(n log n)   Depends on sort/result
  Top K using size-K heap               O(n log k)                     O(k)
  BFS                                     O(V + E)                     O(V)
  Stack-based next-greater scan               O(n)                     O(n)

`k` is the number of distinct keys or requested top elements. Complexity
depends on the chosen data structure and implementation.

------------------------------------------------------------------------
