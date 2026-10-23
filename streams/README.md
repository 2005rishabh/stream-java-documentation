# Stream API

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


---

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


---

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


---

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
