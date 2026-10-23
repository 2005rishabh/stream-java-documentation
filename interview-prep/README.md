# Interview Preparation

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


---

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
