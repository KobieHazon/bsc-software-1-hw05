# Software 1 - Homework 5

A 2018 CS BSc Java assignment submission implementing a bigram language model under `il.ac.tau.cs.sw1.ex5`. The model builds a vocabulary, counts adjacent word pairs, persists and reloads model files, checks sentence legality, computes cosine similarity between word vectors, and finds the closest word by context vector.

## Behavior

`BigramModel` exposes the API for:

- vocabulary extraction with a maximum vocabulary size
- bigram count matrix construction
- model save/load using `.voc` and `.counts` files
- word-index and bigram-count lookup
- most-frequent proceeding-word lookup
- probable sentence checks
- cosine similarity between count vectors
- closest-word lookup by outgoing bigram vector

## Build

```bash
make
```

This compiles the Java sources into `build/` using `javac -Xlint:all -Werror`.

## Testing

```bash
make test
```

The validation run compiles the project, executes the maintained regression harness, and runs the `BigramModelTester` against synthetic fixture files.

## Repository Structure

- `src/il/ac/tau/cs/sw1/ex5/BigramModel.java`: my implementation, maintained for current toolchains
- `src/il/ac/tau/cs/sw1/ex5/BigramModelTester.java`: small tester from the submitted source tree
- `tests/RunHw5Checks.java`: maintained Java regression harness
