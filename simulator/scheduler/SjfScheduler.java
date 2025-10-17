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
 * Non-preemptive shortest job first scheduler.
 */
public class SjfScheduler implements Scheduler {
    @Override
    public String getName() {
        return "Shortest Job First";
    }

    @Override
    public SchedulingResult run(SimulationContext context) throws InterruptedException {
        List<ProcessControlBlock> ready = new ArrayList<>();
        List<ProcessControlBlock> completed = new ArrayList<>();
        List<ExecutionSlice> slices = new ArrayList<>();

        SimulationClock clock = context.getClock();
        SystemCallHandler sys = context.getSystemCalls();
        MemoryManager memory = context.getMemoryManager();

        // Continue running until the loader finishes and no more ready work exists.
        while (!context.canTerminate(completed.size()) || !ready.isEmpty()) {
            context.drainReadyQueue(ready);

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
                    .min(Comparator
                            .comparingInt(ProcessControlBlock::getRemainingTime)
                            .thenComparingInt(ProcessControlBlock::getArrivalOrder))
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
}
