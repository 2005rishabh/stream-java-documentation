# Java Collections & Stream API

## Last-Minute DSA and Interview Revision

**Target:** Java 8+ (examples are compatible with Java 21 unless
noted).\
**Goal:** Quick syntax recall, common patterns, and interview traps.

------------------------------------------------------------------------

## 1. Quick decision guide

  Need                                    Prefer
  --------------------------------------- --------------------------------
  Unique elements, no guaranteed order    `HashSet`
  Unique elements, insertion order        `LinkedHashSet`
  Unique elements, sorted order           `TreeSet`
  Key-value lookup                        `HashMap`
  Key-value lookup with insertion order   `LinkedHashMap`
  Sorted keys                             `TreeMap`
  Fast indexed reads                      `ArrayList`
  Frequent insert/remove at both ends     `ArrayDeque`
  Priority-based retrieval                `PriorityQueue`
  FIFO queue                              `Queue` backed by `ArrayDeque`
  LIFO stack                              `Deque` backed by `ArrayDeque`

### Typical time complexity

These are average/typical costs, not guarantees for every
implementation.

  ---------------------------------------------------------------------------------------------
  Structure           Access/search             Add          Remove Notes
  ----------------- --------------- --------------- --------------- ---------------------------
  `ArrayList`          Index: O(1),  Amortized O(1)         O(n) by Resizing occasionally costs
                       search: O(n)          at end  value/index in O(n)
                                                            general 

  `LinkedList`                 O(n)   O(1) at known   O(1) at known Finding a position is O(n)
                                           node/end        node/end 

  `HashSet`            O(1) average    O(1) average    O(1) average Uses `equals()` and
                                                                    `hashCode()`

  `HashMap`            O(1) average    O(1) average    O(1) average Worst-case behavior depends
                                                                    on
                                                                    collisions/implementation

  `TreeSet`                O(log n)        O(log n)        O(log n) Sorted elements

  `TreeMap`                O(log n)        O(log n)        O(log n) Sorted keys

  `PriorityQueue`        Peek: O(1)        O(log n)  Poll: O(log n) Head is smallest by default

  `ArrayDeque`           Ends: O(1) Ends: amortized      Ends: O(1) Good stack/queue choice
                                               O(1)                 
  ---------------------------------------------------------------------------------------------

------------------------------------------------------------------------

## 2. Collections framework essentials

``` java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
```

### List

``` java
List<Integer> list = new ArrayList<>(List.of(4, 2, 2, 7));

list.add(9);
list.get(0);                 // 4
list.set(0, 10);
list.contains(7);
list.remove(Integer.valueOf(2)); // Removes the value 2, not index 2
list.remove(0);              // Removes index 0
list.sort(Comparator.naturalOrder());
```

**Trap:** `list.remove(2)` removes index `2` for a `List<Integer>`. Use
`remove(Integer.valueOf(2))` to remove the value `2`.

### Set

``` java
Set<Integer> hash = new HashSet<>(List.of(3, 1, 3));
Set<Integer> ordered = new LinkedHashSet<>(List.of(3, 1, 3));
Set<Integer> sorted = new TreeSet<>(List.of(3, 1, 3));

System.out.println(hash);    // Order is not guaranteed
System.out.println(ordered); // [3, 1]
System.out.println(sorted);  // [1, 3]
```

### Map

``` java
Map<String, Integer> map = new HashMap<>();
map.put("apple", 3);
map.put("apple", 5); // Replaces the previous value

map.get("apple");                   // 5
map.getOrDefault("pear", 0);        // 0
map.containsKey("apple");
map.putIfAbsent("pear", 1);
map.merge("apple", 2, Integer::sum); // Adds 2 to existing value
map.computeIfAbsent("banana", k -> 0);
map.remove("pear");

for (var entry : map.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

**Frequency map pattern:**

``` java
Map<Integer, Integer> freq = new HashMap<>();
for (int x : nums) {
    freq.merge(x, 1, Integer::sum);
}
```

Stream alternative:

``` java
Map<Integer, Long> freq = Arrays.stream(nums)
    .boxed()
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));
```

### Queue, deque, stack, and heap

``` java
Queue<Integer> queue = new ArrayDeque<>();
queue.offer(10);       // Enqueue
queue.offer(20);
queue.peek();          // Front without removing
queue.poll();          // Remove front; null if empty

Deque<Integer> deque = new ArrayDeque<>();
deque.push(10);        // Stack push at front
deque.push(20);
deque.pop();           // Stack pop from front
deque.peek();

PriorityQueue<Integer> minHeap = new PriorityQueue<>();
PriorityQueue<Integer> maxHeap =
    new PriorityQueue<>(Comparator.reverseOrder());

minHeap.offer(5);
minHeap.offer(1);
minHeap.offer(3);
minHeap.peek();        // 1
minHeap.poll();        // 1
```

**Important:** Iterating over a `PriorityQueue` does not produce sorted
order. Repeatedly call `poll()` to retrieve elements in priority order.

### Sorting and comparators

``` java
Collections.sort(list);
list.sort(Comparator.naturalOrder());
list.sort(Comparator.reverseOrder());

int[][] pairs = {{2, 9}, {1, 8}, {2, 3}};
Arrays.sort(pairs, (a, b) -> Integer.compare(a[0], b[0]));

// Sort by first field, then second field
Arrays.sort(pairs,
    Comparator.<int[]>comparingInt(a -> a[0])
              .thenComparingInt(a -> a[1]));
```

Prefer `Integer.compare(a, b)` over `a - b`. Subtraction can overflow.

For objects:

``` java
record Student(String name, int marks) {}

List<Student> students = new ArrayList<>();
students.sort(Comparator.comparingInt(Student::marks).reversed()
    .thenComparing(Student::name));
```

### Arrays and collections conversion

``` java
int[] a = {1, 2, 3};
List<Integer> boxed = Arrays.stream(a).boxed().toList();

List<String> names = new ArrayList<>(List.of("A", "B"));
String[] array = names.toArray(new String[0]);

List<Integer> mutable = new ArrayList<>(List.of(1, 2, 3));
List<Integer> fixedSize = Arrays.asList(1, 2, 3); // set allowed; add/remove not allowed
List<Integer> unmodifiable = List.of(1, 2, 3);    // modification not allowed
```

`Stream.toList()` returns an unmodifiable list. Use
`collect(Collectors.toCollection(ArrayList::new))` if you need a mutable
result.

------------------------------------------------------------------------

## 3. Stream API: mental model

A stream pipeline has three parts:

1.  **Source:** collection, array, or generated values.
2.  **Intermediate operations:** transform/filter the stream. Usually
    lazy.
3.  **Terminal operation:** produce a result or side effect. This
    triggers processing.

``` java
List<Integer> result = nums.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * n)
    .sorted()
    .toList();
```

This keeps even numbers, squares them, sorts the results, and returns an
unmodifiable list.

### Stream syntax at a glance

  --------------------------------------------------------------------------------
  Operation               Purpose                 Example
  ----------------------- ----------------------- --------------------------------
  `filter`                Keep matching items     `.filter(x -> x > 0)`

  `map`                   Transform each item     `.map(String::length)`

  `mapToInt`              Convert to primitive    `.mapToInt(Integer::intValue)`
                          int stream              

  `flatMap`               Flatten nested streams  `.flatMap(List::stream)`

  `distinct`              Remove duplicates       `.distinct()`

  `sorted`                Sort elements           `.sorted()`

  `limit`                 Keep first N            `.limit(5)`

  `skip`                  Skip first N            `.skip(2)`

  `peek`                  Inspect while debugging `.peek(System.out::println)`

  `count`                 Count elements          `.count()`

  `min` / `max`           Find extreme value      `.max(Integer::compareTo)`

  `findFirst`             First element as        `.findFirst()`
                          `Optional`              

  `anyMatch`              At least one matches    `.anyMatch(x -> x < 0)`

  `allMatch`              Every element matches   `.allMatch(x -> x > 0)`

  `noneMatch`             No element matches      `.noneMatch(x -> x == 0)`

  `reduce`                Combine into one value  `.reduce(0, Integer::sum)`

  `collect`               Build a result          `.collect(Collectors.toSet())`
                          container               

  `toList`                Collect to unmodifiable `.toList()`
                          list                    
  --------------------------------------------------------------------------------

**Do not reuse a stream.** A stream is consumed by a terminal operation.
Create a new stream from the source for another pipeline.

------------------------------------------------------------------------

## 4. Core Stream recipes

Assume:

``` java
List<Integer> nums = List.of(5, 2, 8, 2, 1, 6, 5);
List<String> words = List.of("java", "stream", "map", "java");
```

### Filter, transform, and sort

``` java
List<Integer> evenSquares = nums.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * n)
    .sorted()
    .toList();
```

Descending order:

``` java
List<Integer> descending = nums.stream()
    .sorted(Comparator.reverseOrder())
    .toList();
```

### Remove duplicates

``` java
List<Integer> unique = nums.stream().distinct().toList();
```

### Sum, average, minimum, maximum

``` java
int sum = nums.stream().mapToInt(Integer::intValue).sum();

OptionalDouble average =
    nums.stream().mapToInt(Integer::intValue).average();

OptionalInt min = nums.stream().mapToInt(Integer::intValue).min();
OptionalInt max = nums.stream().mapToInt(Integer::intValue).max();
```

`average()`, `min()`, and `max()` can be empty. Handle that possibility.

``` java
int maxOrDefault = nums.stream()
    .mapToInt(Integer::intValue)
    .max()
    .orElse(-1);
```

### Second-highest distinct value

``` java
Optional<Integer> secondHighest = nums.stream()
    .distinct()
    .sorted(Comparator.reverseOrder())
    .skip(1)
    .findFirst();
```

This sorts all distinct values, so it is often not the best choice for
performance. For an interview, also consider a one-pass algorithm.

### Top K values

``` java
int k = 3;
List<Integer> topK = nums.stream()
    .sorted(Comparator.reverseOrder())
    .limit(k)
    .toList();
```

This is simple but costs O(n log n). A size-K heap can be better when K
is much smaller than n.

### Convert strings to uppercase

``` java
List<String> upper = words.stream()
    .map(String::toUpperCase)
    .toList();
```

### Flatten a nested list

``` java
List<List<Integer>> nested = List.of(
    List.of(1, 2),
    List.of(3, 4)
);

List<Integer> flat = nested.stream()
    .flatMap(List::stream)
    .toList();
```

### Join strings

``` java
String joined = words.stream()
    .distinct()
    .sorted()
    .collect(Collectors.joining(", "));
```

### Group items by a property

``` java
Map<Integer, List<String>> byLength = words.stream()
    .collect(Collectors.groupingBy(String::length));
```

### Count items by a property

``` java
Map<Integer, Long> countByLength = words.stream()
    .collect(Collectors.groupingBy(
        String::length,
        Collectors.counting()
    ));
```

### Partition into true and false groups

``` java
Map<Boolean, List<Integer>> partitioned = nums.stream()
    .collect(Collectors.partitioningBy(n -> n % 2 == 0));

List<Integer> evens = partitioned.get(true);
List<Integer> odds = partitioned.get(false);
```

### Build a map

``` java
Map<String, Integer> lengthByWord = words.stream()
    .distinct()
    .collect(Collectors.toMap(
        Function.identity(),
        String::length
    ));
```

If keys can repeat, provide a merge function:

``` java
Map<String, Integer> counts = words.stream()
    .collect(Collectors.toMap(
        Function.identity(),
        word -> 1,
        Integer::sum
    ));
```

### Frequency of each element

``` java
Map<String, Long> wordFrequency = words.stream()
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));
```

### Reduce

``` java
int total = nums.stream().reduce(0, Integer::sum);
Optional<Integer> product = nums.stream().reduce((a, b) -> a * b);
```

For numeric totals, prefer `mapToInt(...).sum()` when it fits the task.
`reduce` is useful for custom combinations.

------------------------------------------------------------------------

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

## 6. Primitive streams and arrays

``` java
int[] numsArray = {3, 1, 4, 1, 5};

int sum = Arrays.stream(numsArray).sum();
int max = Arrays.stream(numsArray).max().orElseThrow();

List<Integer> list = Arrays.stream(numsArray)
    .boxed()
    .toList();

int[] sorted = Arrays.stream(numsArray)
    .sorted()
    .toArray();
```

For a `String`:

``` java
List<String> chars = "hello".chars()
    .mapToObj(c -> String.valueOf((char) c))
    .toList();
```

`chars()` returns integer code units. For full Unicode code-point
handling, use `codePoints()`.

------------------------------------------------------------------------

## 7. `Optional` quick reference

Some stream operations return `Optional`, `OptionalInt`, or
`OptionalDouble` because a result may not exist.

``` java
Optional<Integer> first = nums.stream().filter(n -> n > 100).findFirst();

first.isPresent();
first.orElse(-1);
first.orElseGet(() -> computeFallback());
first.ifPresent(System.out::println);
```

-   `orElse(value)` evaluates `value` before the call.
-   `orElseGet(supplier)` evaluates the supplier only when the optional
    is empty.
-   Avoid calling `.get()` without checking presence. Prefer `orElse`,
    `orElseThrow`, or `ifPresent`.

------------------------------------------------------------------------

## 8. Interview traps

1.  **`==` vs `equals()`:** `==` compares object references. `equals()`
    checks logical equality when implemented appropriately.
2.  **Hash-based collections:** If two objects are equal according to
    `equals()`, they must return the same `hashCode()`.
3.  **Mutable map keys:** Do not change fields used by `equals()` or
    `hashCode()` while an object is a key in a hash map or element in a
    hash set.
4.  **`HashMap` order:** It does not guarantee iteration order.
5.  **`TreeSet` / `TreeMap`:** Ordering comes from natural ordering or
    the comparator. Comparator equality can cause distinct objects to be
    treated as the same set element or key.
6.  **Nulls:** `ArrayDeque` rejects null. `List.of(...)` and
    `Map.of(...)` reject null elements/keys/values. `HashMap` allows one
    null key and null values; `TreeMap` null behavior depends on
    ordering/comparator.
7.  **Stream reuse:** A stream cannot be operated on after a terminal
    operation.
8.  **Side effects:** Avoid modifying external state inside `map`,
    `filter`, or parallel stream operations.
9.  **Parallel streams:** They are not automatically faster. Use them
    only after considering workload, ordering, shared state, and
    measurement.
10. **`peek()`:** Use mainly for debugging, not as the place for
    required business logic.
11. **`Stream.toList()`:** Returns an unmodifiable list.
    `Collectors.toList()` does not promise a specific list
    implementation or mutability contract.
12. **Integer division:** `5 / 2` is `2`. Use `5.0 / 2` for
    floating-point division.
13. **Comparator overflow:** Use `Integer.compare(a, b)`, not `a - b`.
14. **Priority queue order:** Only the head is guaranteed to be the
    current minimum (or maximum under a reverse comparator).
15. **Concurrent modification:** Do not structurally modify most
    collections during an enhanced `for` loop. Use an iterator's
    `remove()` or an appropriate alternative.

------------------------------------------------------------------------

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

## 10. Last-minute checklist

Before an interview or timed DSA session, confirm that you can write
these without looking:

-   [ ] `HashMap` frequency counting with `merge`.
-   [ ] `HashSet` membership and duplicate detection.
-   [ ] `ArrayDeque` for stack and queue.
-   [ ] `PriorityQueue` min-heap and max-heap comparators.
-   [ ] Sorting objects or arrays with `Comparator`.
-   [ ] Stream `filter`, `map`, `flatMap`, `distinct`, and `sorted`.
-   [ ] Stream `groupingBy`, `counting`, `toMap`, and `joining`.
-   [ ] Safe handling of empty `Optional` results.
-   [ ] Complexity of map/set lookup, sorting, heaps, BFS, and monotonic
    stacks.
-   [ ] The difference between mutable, fixed-size, and unmodifiable
    lists.

**Final rule:** Use Streams when they make data transformation clearer.
Use loops when the algorithm needs indices, early exits, complex state,
or tight control. Interviewers care about correctness, complexity, and
clarity, not how many lambdas fit on one line.
