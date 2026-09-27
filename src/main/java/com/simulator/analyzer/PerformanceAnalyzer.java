package com.simulator.analyzer;

import com.simulator.algorithm.SimulationResult;
import com.simulator.engine.MemoryEngine.EngineRun;

import java.util.List;
import java.util.Locale;

/**
 * Turns raw {@link SimulationResult}s into human-readable reports: per-run
 * summaries, a step-by-step trace table, and a side-by-side comparison of
 * multiple algorithms run against the same reference string.
 */
public class PerformanceAnalyzer {

    /** One-line summary: faults, hits, hit rate, elapsed time. */
    public String summarize(EngineRun run) {
        SimulationResult r = run.getResult();
        return String.format(Locale.US,
                "%-6s | frames=? | faults=%-4d hits=%-4d hitRate=%6.2f%% faultRate=%6.2f%% time=%.3fms",
                r.getAlgorithmName(), r.getPageFaults(), r.getPageHits(),
                r.getHitRate() * 100, r.getFaultRate() * 100, run.getMetrics().getElapsedMillis());
    }

    /** Renders a full step-by-step trace table, similar to a textbook worked example. */
    public String renderTrace(SimulationResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Step | Ref | Fault | Evicted | Frames\n");
        sb.append("-----+-----+-------+---------+-----------------------\n");
        for (SimulationResult.StepSnapshot step : result.getSteps()) {
            sb.append(String.format(Locale.US, "%4d | %3d | %5s | %7s | %s%n",
                    step.index + 1,
                    step.referencedPage,
                    step.fault ? "FAULT" : "hit",
                    step.evictedPage == -1 ? "-" : String.valueOf(step.evictedPage),
                    step.frameState));
        }
        return sb.toString();
    }

    /** Builds a comparison table ranking algorithms by page-fault count (ascending). */
    public String compare(List<EngineRun> runs) {
        StringBuilder sb = new StringBuilder();
        sb.append("Algorithm | Faults | Hits | Hit Rate | Time (ms)\n");
        sb.append("----------+--------+------+----------+----------\n");

        runs.stream()
                .sorted((a, b) -> Integer.compare(a.getResult().getPageFaults(), b.getResult().getPageFaults()))
                .forEach(run -> {
                    SimulationResult r = run.getResult();
                    sb.append(String.format(Locale.US, "%-9s | %6d | %4d | %7.2f%% | %8.3f%n",
                            r.getAlgorithmName(), r.getPageFaults(), r.getPageHits(),
                            r.getHitRate() * 100, run.getMetrics().getElapsedMillis()));
                });

        return sb.toString();
    }
}
