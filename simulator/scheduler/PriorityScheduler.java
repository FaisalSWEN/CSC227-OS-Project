package simulator.scheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import simulator.app.SimulationContext;
import simulator.core.ExecutionSlice;
import simulator.core.ProcessControlBlock;
import simulator.core.SimulationClock;
import simulator.core.SchedulingResult;
import simulator.core.SystemCallHandler;
import simulator.memory.MemoryManager;

/**
 * Non-preemptive priority scheduler with starvation detection and aging.
 */
public class PriorityScheduler implements Scheduler {
    private static final int AGING_INTERVAL = 5;

    @Override
    public String getName() {
        return "Priority Scheduling";
    }

    @Override
    public SchedulingResult run(SimulationContext context) throws InterruptedException {
        List<ProcessControlBlock> ready = new ArrayList<>();
        List<ProcessControlBlock> completed = new ArrayList<>();
        List<ExecutionSlice> slices = new ArrayList<>();

        SimulationClock clock = context.getClock();
        SystemCallHandler sys = context.getSystemCalls();
        MemoryManager memory = context.getMemoryManager();

        List<ProcessControlBlock> buffer = new ArrayList<>();

        // Loop until every admitted job is dispatched and the loader reports completion.
        while (!context.canTerminate(completed.size()) || !ready.isEmpty()) {
            buffer.clear();
            context.drainReadyQueue(buffer);
            for (ProcessControlBlock pcb : buffer) {
                if (!ready.contains(pcb)) {
                    ready.add(pcb);
                }
            }

            applyAging(ready, clock, sys);

            if (ready.isEmpty()) {
                ProcessControlBlock pending = context.takeNextReady(100);
                if (pending != null) {
                    ready.add(pending);
                } else if (context.canTerminate(completed.size())) {
                    break;
                }
                continue;
            }

            ProcessControlBlock next = ready.stream()
                    .max(Comparator
                            .comparingInt(ProcessControlBlock::getDynamicPriority)
                            .thenComparingInt(pcb -> clock.getTime() - pcb.getLastReadyTimestamp())
                            .thenComparingInt(pcb -> -pcb.getArrivalOrder()))
                    .orElseThrow();
            ready.remove(next);

            int start = clock.getTime();
            next.markDispatched(start);
            sys.dispatch(next, start);

            int executed = next.consumeCpu(next.getRemainingTime());
            clock.advance(executed);

            next.markCompleted(clock.getTime());
            sys.complete(next, clock.getTime());
            memory.release(next);

            slices.add(new ExecutionSlice(next.getId(), start, clock.getTime()));
            completed.add(next);
        }

        return new SchedulingResult(
                getName(),
                slices,
                completed,
                context.getSystemCalls().getStarvationLog(),
                context.getSystemCalls().getSystemCallLog()
        );
    }

    private void applyAging(List<ProcessControlBlock> ready, SimulationClock clock, SystemCallHandler sys) {
        int now = clock.getTime();
        // Each pass both detects starvation and incrementally increases priority for long-waiting jobs.
        for (ProcessControlBlock pcb : ready) {
            int waited = now - pcb.getLastReadyTimestamp();
            if (pcb.getReadyAdmissionTime() >= 0 && waited > pcb.getAdmissionDegree()) {
                if (!pcb.hasSufferedStarvation()) {
                    pcb.markStarvation(waited);
                    sys.reportStarvation(pcb, waited, pcb.getAdmissionDegree());
                }
            }
            int expectedBoosts = waited / AGING_INTERVAL;
            if (expectedBoosts > pcb.getPriorityBoostCount()) {
                int delta = expectedBoosts - pcb.getPriorityBoostCount();
                pcb.boostPriority(delta);
                if (delta > 0) {
                    sys.boostPriority(pcb);
                }
            }
        }
    }
}
