package simulator.app;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import simulator.core.ProcessControlBlock;
import simulator.core.SimulationClock;
import simulator.core.SystemCallHandler;
import simulator.memory.MemoryManager;
import simulator.io.ProcessLoader;

/**
 * Shared view of the simulation environment that allows schedulers to cooperate with loader threads.
 */
public class SimulationContext {
    private final BlockingQueue<ProcessControlBlock> readyQueue;
    private final MemoryManager memoryManager;
    private final SystemCallHandler systemCalls;
    private final SimulationClock clock;
    private final ProcessLoader loader;
    private final AtomicBoolean readerFinished;
    private final AtomicInteger totalJobs;

    public SimulationContext(BlockingQueue<ProcessControlBlock> readyQueue,
    MemoryManager memoryManager,
    SystemCallHandler systemCalls,
    SimulationClock clock,
    ProcessLoader loader,
    AtomicBoolean readerFinished,
    AtomicInteger totalJobs) {
        this.readyQueue = readyQueue;
        this.memoryManager = memoryManager;
        this.systemCalls = systemCalls;
        this.clock = clock;
        this.loader = loader;
        this.readerFinished = readerFinished;
        this.totalJobs = totalJobs;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }

    public SystemCallHandler getSystemCalls() {
        return systemCalls;
    }

    public SimulationClock getClock() {
        return clock;
    }

    /**
     * Transfers any currently available ready processes into the provided collection.
     */
    public void drainReadyQueue(List<ProcessControlBlock> target) {
        readyQueue.drainTo(target);
    }

    /**
     * Blocks for a limited interval waiting for the next ready process.
     */
    public ProcessControlBlock takeNextReady(long timeoutMillis) throws InterruptedException {
        return readyQueue.poll(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public int getUsedMemory() {
        return memoryManager.getUsedMemory();
    }

    public int getTotalMemory() {
        return memoryManager.getTotalMemory();
    }

    public boolean isReaderFinished() {
        return readerFinished.get();
    }

    public int getTotalJobs() {
        return totalJobs.get();
    }

    public boolean isLoaderFinished() {
        return loader.isLoadingComplete();
    }

    public boolean isReadyQueueEmpty() {
        return readyQueue.isEmpty();
    }

    /**
     * Determines whether the scheduler has completed processing all work.
     */
    public boolean canTerminate(int completedProcesses) {
        if (completedProcesses < totalJobs.get()) {
            return false;
        }
        if (!readerFinished.get()) {
            return false;
        }
        if (!loader.isLoadingComplete()) {
            return false;
        }
        return readyQueue.isEmpty();
    }

    /**
     * Ensures that resources are shut down cleanly after scheduling.
     */
    public void shutdownLoader() {
        loader.requestShutdown();
    }

}
