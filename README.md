# Fast JVM collection library

This work is work-in-progress and yet to be named properly.

A high-performance library designed for Big Data analytics:
- Low-level fast collection libraries that can run on memory blocks (in-heap or off-heap)
- Support for large primitive arrays and collections (64 bit index)
- Cache-friendly sorting (TimSort) and hash table implementations
- In-memory layout definition of tuples
- Fast, mutable strings as well as string operations that do not require expensive decoding
- Explicit memory management to avoid garbage collections and improve cache locality

Twitter: [@rxin](https://twitter.com/rxin)
