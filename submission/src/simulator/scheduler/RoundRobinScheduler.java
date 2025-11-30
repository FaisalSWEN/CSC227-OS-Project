package simulator.scheduler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import simulator.app.SimulationContext;
import simulator.core.ExecutionSlice;
import simulator.core.ProcessControlBlock;
import simulator.core.SimulationClock;
import simulator.core.SchedulingResult;
import simulator.core.SystemCallHandler;
import simulator.memory.MemoryManager;

/**
 * Preemptive round-robin scheduler with a fixed quantum.
 */
public class RoundRobinScheduler implements Scheduler {
    private static final int TIME_QUANTUM = 7;

    @Override
    public String getName() {
        return "Round Robin (q=" + TIME_QUANTUM + ")";
    }

    @Override
    public SchedulingResult run(SimulationContext context) throws InterruptedException {
        Deque<ProcessControlBlock> rrQueue = new ArrayDeque<>();
        List<ProcessControlBlock> completed = new ArrayList<>();
        List<ExecutionSlice> slices = new ArrayList<>();

        SimulationClock clock = context.getClock();
        SystemCallHandler sys = context.getSystemCalls();
        MemoryManager memory = context.getMemoryManager();

        List<ProcessControlBlock> buffer = new ArrayList<>();

        // Round robin rotates through the queue until the loader and ready lists are empty.
        while (!context.canTerminate(completed.size()) || !rrQueue.isEmpty()) {
            buffer.clear();
            context.drainReadyQueue(buffer);
            for (ProcessControlBlock pcb : buffer) {
                rrQueue.add(pcb);
            }
            if (rrQueue.isEmpty()) {
                ProcessControlBlock next = context.takeNextReady(100);
                if (next != null) {
                    rrQueue.add(next);
                } else if (context.canTerminate(completed.size())) {
                    break;
                }
                continue;
            }
            ProcessControlBlock current = rrQueue.pollFirst();
            int start = clock.getTime();
            current.markDispatched(start);
            sys.dispatch(current, start);

            int executed = current.consumeCpu(TIME_QUANTUM);
            clock.advance(executed);
            slices.add(new ExecutionSlice(current.getId(), start, clock.getTime()));

            if (current.getRemainingTime() > 0) {
                sys.yield(current, clock.getTime(), current.getRemainingTime());
                current.markRequeued(clock.getTime(), memory.getAllocatedProcessCount());
                rrQueue.addLast(current);
            } else {
                current.markCompleted(clock.getTime());
                sys.complete(current, clock.getTime());
                memory.release(current);
                completed.add(current);
            }

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
