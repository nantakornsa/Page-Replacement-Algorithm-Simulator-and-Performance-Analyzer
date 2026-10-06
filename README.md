# Page Replacement Simulator

A small Java CLI tool that simulates classic OS page-replacement algorithms —
**FIFO**, **LRU**, **OPT (Optimal/Belady)**, and **LFU (Least-Frequently-Used)** —
over a reference string, reports page faults / hit rate, and lets you compare
algorithms side by side.

## Project layout

```
page-replacement-simulator/
├── src/main/java/com/simulator/
│   ├── Main.java                     # CLI entry point
│   ├── model/
│   │   ├── Page.java                 # Virtual page model
│   │   ├── Frame.java                # Physical frame model
│   │   └── PageTableEntry.java       # Valid/reference bits, frame mapping
│   ├── algorithm/                    # Strategy pattern
│   │   ├── PageReplacementAlgorithm.java
│   │   ├── SimulationResult.java
│   │   ├── FifoAlgorithm.java
│   │   ├── LruAlgorithm.java
│   │   ├── OptAlgorithm.java
│   │   └── LfuAlgorithm.java
│   ├── engine/
│   │   ├── MemoryEngine.java         # Drives a simulation run
│   │   └── TraceGenerator.java       # Reads/generates reference strings
│   └── analyzer/
│       ├── PerformanceAnalyzer.java  # Reports & comparison tables
│       └── SystemMetrics.java        # Timing / heap usage
├── src/test/java/com/simulator/algorithm/   # JUnit 5 tests per algorithm
├── traces/                           # Sample reference-string files
├── web/
│   └── index.html                    # Standalone visualizer (no build step)
├── pom.xml
└── README.md
```

## Web visualizer

`web/index.html` is a self-contained, dependency-free HTML page that
simulates FIFO / LRU / OPT / LFU step-by-step in the browser and shows a
live execution log alongside the frame state. It's a visual companion to the
CLI, not a replacement for it — the two are independent (the page reimplements
the same algorithm logic in JavaScript, it doesn't call the Java code).

Open it directly, no server needed:

```bash
# any of these work
open web/index.html          # macOS
xdg-open web/index.html      # Linux
start web/index.html         # Windows
```

Or serve it if you'd rather not open files directly from disk:

```bash
cd web && python3 -m http.server 8000
# then visit http://localhost:8000
```

## Requirements

- Java 17+
- Maven 3.8+

## Build

```bash
mvn clean package
```

This produces two jars in `target/`:
- `page-replacement-simulator.jar` — thin jar (needs classpath)
- `page-replacement-simulator-jar-with-dependencies.jar` — runnable standalone jar

## Run

Using a trace file:

```bash
java -jar target/page-replacement-simulator-jar-with-dependencies.jar \
  --frames 3 --trace traces/sample.txt --algo all --verbose
```

Using a generated synthetic trace:

```bash
java -jar target/page-replacement-simulator-jar-with-dependencies.jar \
  --frames 4 --generate 30 --pages 8 --locality 0.8 --seed 7 --algo fifo,lru,opt,lfu
```

### CLI options

| Flag         | Description                                              | Default |
|--------------|------------------------------------------------------------|---------|
| `--frames N` | Number of physical frames                                  | 3       |
| `--trace F`  | Path to a trace file (overrides `--generate`)               | -       |
| `--algo L`   | Comma list: `fifo,lru,opt,lfu` or `all`                  | all     |
| `--generate N` | Length of synthetic reference string to generate         | 20      |
| `--pages N`  | Distinct virtual pages used when generating                 | 10      |
| `--locality D` | Locality bias 0.0–1.0 when generating                     | 0.7     |
| `--seed N`   | RNG seed for reproducible generation                        | 42      |
| `--verbose`  | Print the full step-by-step trace table per algorithm       | off     |

### Trace file format

Plain text, page numbers separated by commas/whitespace/newlines; lines
starting with `#` are comments. See `traces/sample.txt`.

## Testing

```bash
mvn test
```

Tests validate each algorithm against the classic textbook reference string
`7,0,1,2,0,3,0,4,2,3,0,3,2,1,2,0,1,7,0,1` with 3 frames, which has well-known
expected fault counts: FIFO = 15, LRU = 12, OPT = 9 (the theoretical minimum).

## Notes on the algorithms

- **FIFO** evicts the page that has been resident longest, ignoring recency of use.
- **LRU** evicts the page whose most recent access is furthest in the past.
- **OPT** (Belady's algorithm) evicts the page whose *next* use is furthest in
  the future (or never happens again). It requires full foreknowledge of the
  reference string and is used only as a theoretical lower bound — no real OS
  can implement it online.
- **LFU** evicts the resident page with the fewest references since it entered
  memory. If frequencies tie, the page that entered memory earliest is evicted
  (FIFO tie-breaker).