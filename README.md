# Java Collections, Streams & DSA Revision

A practical Java reference for collections, Stream API operations, common data-structure and algorithm patterns, and interview preparation. The notes retain the original revision material and include runnable examples.

**Target:** Java 21 for the runnable examples. Most concepts apply to Java 8+, though APIs such as `Stream.toList()` require Java 16+.

## Topics

- [Collections framework](./collections/README.md) — choosing data structures, complexity, and collection operations.
- [Stream API](./streams/README.md) — pipeline fundamentals, common recipes, primitive streams, and `Optional`.
- [DSA patterns](./dsa-patterns/README.md) — hash maps, BFS, heaps, sliding windows, and complexity reminders.
- [Interview preparation](./interview-prep/README.md) — common Java traps and a last-minute checklist.
- [Runnable examples](./examples/README.md) — standalone programs demonstrating collections, streams, and DSA patterns.

## Quick start

Compile and run an example from the repository root, for example:

```sh
javac -d out examples/StreamsExample.java
java -cp out StreamsExample
```

Each example is self-contained and prints representative output. See the [examples guide](./examples/README.md) for all commands.
