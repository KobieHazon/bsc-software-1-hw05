# Software 1 - Homework 5

A 2018 CS BSc Java assignment submission implementing a bigram language model under `il.ac.tau.cs.sw1.ex5`. The model builds a vocabulary, counts adjacent word pairs, persists and reloads model files, checks sentence legality, computes cosine similarity between word vectors, and finds the closest word by context vector.

## Behavior

`BigramModel` exposes the recovered API for:

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

The validation run compiles the project, executes the maintained regression harness, and runs the recovered `BigramModelTester` against synthetic fixture files.

## Repository Structure

- `assignment/MISSING_HANDOUT.md`: background note explaining that the exact matching handout and original fixtures were not recovered
- `resources/hw5/`: small synthetic validation fixtures, not recovered course fixtures
- `src/il/ac/tau/cs/sw1/ex5/BigramModel.java`: my recovered implementation, maintained for current toolchains
- `src/il/ac/tau/cs/sw1/ex5/BigramModelTester.java`: recovered small tester from the submitted source tree
- `tests/RunHw5Checks.java`: maintained Java regression harness

## Implementation notes

The maintained version keeps the recovered package path and public API, cleans up implementation structure, adds synthetic validation fixtures, fixes invalid-token handling, single-word sentence legality, and zero-vector cosine similarity.

## License

No repository-wide license is declared because the exact supplied exercise terms were not recovered.
