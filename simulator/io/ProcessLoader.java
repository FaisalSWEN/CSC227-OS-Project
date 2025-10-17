package simulator.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import simulator.core.ProcessControlBlock;
import simulator.core.SimulationClock;
import simulator.core.SystemCallHandler;
import simulator.memory.MemoryManager;

/**
 * Moves PCBs from the job queue to the ready queue while respecting available memory.
 */
public class ProcessLoader extends Thread {
    private final BlockingQueue<ProcessControlBlock> jobQueue;
    private final BlockingQueue<ProcessControlBlock> readyQueue;
    private final MemoryManager memoryManager;
    private final SystemCallHandler systemCalls;
    private final SimulationClock clock;
    private final AtomicBoolean readerFinished;

    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    private final AtomicBoolean loadingComplete = new AtomicBoolean(false);

    public ProcessLoader(BlockingQueue<ProcessControlBlock> jobQueue,
                         BlockingQueue<ProcessControlBlock> readyQueue,
                         MemoryManager memoryManager,
                         SystemCallHandler systemCalls,
                         SimulationClock clock,
                         AtomicBoolean readerFinished) {
        super("process-loader");
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.systemCalls = systemCalls;
        this.clock = clock;
        this.readerFinished = readerFinished;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ProcessControlBlock pcb = jobQueue.poll(100, TimeUnit.MILLISECONDS);
                if (pcb == null) {
                    if (readerFinished.get() && jobQueue.isEmpty()) {
                        break;
                    }
                    if (shutdownRequested.get()) {
                        break;
                    }
                    continue;
                }
                // Block until memory is available, ensuring the ready queue never over-commits RAM.
                memoryManager.allocateBlocking(pcb);
                int degree = memoryManager.getAllocatedProcessCount();
                int readyTime = clock.getTime();
                pcb.markReady(readyTime, degree, true);
                systemCalls.admitToReady(pcb, readyTime, degree);
                readyQueue.put(pcb);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } finally {
            loadingComplete.set(true);
        }
    }

    public void requestShutdown() {
        shutdownRequested.set(true);
        interrupt();
    }

    public boolean isLoadingComplete() {
        return loadingComplete.get();
    }
}
