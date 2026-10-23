# Collections

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


---

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
