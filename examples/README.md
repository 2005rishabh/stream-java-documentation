# Runnable Java Examples

These standalone programs complement the reference notes. They require a
Java 21 JDK and use only the standard library.

Run commands from the repository root:

```sh
javac -d out examples/CollectionsExample.java
java -cp out CollectionsExample

javac -d out examples/StreamsExample.java
java -cp out StreamsExample

javac -d out examples/DsaPatternsExample.java
java -cp out DsaPatternsExample
```

The examples print results to standard output. Remove the generated `out/`
directory when you are finished.

## Files

- [`CollectionsExample.java`](./CollectionsExample.java) — insertion-order
  uniqueness, frequency counting, and queue operations.
- [`StreamsExample.java`](./StreamsExample.java) — filtering, mapping,
  grouping, and flattening.
- [`DsaPatternsExample.java`](./DsaPatternsExample.java) — two-sum lookup,
  breadth-first search, and a size-K heap.
