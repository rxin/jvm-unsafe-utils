# JVM Unsafe Util Library

[![Build Status](https://travis-ci.org/rxin/fast-jvm-collection.svg?branch=master)](https://travis-ci.org/rxin/jvm-unsafe-utils)

This work is work-in-progress and yet to be named properly.

A high-performance library designed for Big Data analytics, including:
- Explicit (in-heap and off-heap) memory allocator to avoid JVM garbage collections and improve cache locality
- Collection library
  - Array abstraction that can go beyond 2GB limit (with 64-bit index)
  - Fast BitSet abstraction (with 64-bit index)
  - Fast, cache-friendly open addressing hash map

Future TODOs include:
- jemalloc-like memory allocator to improve memory allocation performance for small blocks and reduce fragmentation
- Cache-friendly sorting
- In-memory layout definition of tuples
- Fast alternatives of common data types: String, Date, Timestamp, Decimal


When used incorrectly (and with assert turned off), a programming error can crash the JVM process.

Twitter: [@rxin](https://twitter.com/rxin)
