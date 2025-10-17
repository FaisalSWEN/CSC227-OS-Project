package simulator.app;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import simulator.core.ProcessControlBlock;
import simulator.core.SimulationClock;
import simulator.core.SchedulingResult;
import simulator.core.SystemCallHandler;
import simulator.io.JobReader;
import simulator.io.ProcessLoader;
import simulator.memory.MemoryManager;
import simulator.scheduler.Scheduler;

/**
 * Orchestrates the reader, loader, and scheduler threads for each run.
 */
public class SimulationRunner {
    private final Path jobFile;

    public SimulationRunner(Path jobFile) {
        this.jobFile = jobFile;
    }

    /**
     * Executes the scheduler end-to-end and returns the aggregated result.
     */
    public SchedulingResult execute(Scheduler scheduler) throws InterruptedException {
        SystemCallHandler systemCalls = new SystemCallHandler();
        SimulationClock clock = new SimulationClock();
        MemoryManager memoryManager = new MemoryManager(2048, systemCalls);

        BlockingQueue<ProcessControlBlock> jobQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ProcessControlBlock> readyQueue = new LinkedBlockingQueue<>();

        AtomicBoolean readerFinished = new AtomicBoolean(false);
        AtomicInteger totalJobs = new AtomicInteger(0);

        JobReader jobReader = new JobReader(jobFile, jobQueue, systemCalls, totalJobs, readerFinished);
        ProcessLoader loader = new ProcessLoader(jobQueue, readyQueue, memoryManager, systemCalls, clock, readerFinished);

        SimulationContext context = new SimulationContext(
                readyQueue,
                memoryManager,
                systemCalls,
                clock,
                loader,
                readerFinished,
                totalJobs
        );

        // Spin up the supporting threads before invoking the scheduler.
        jobReader.start();
        loader.start();

        awaitInitialAdmission(readyQueue, loader, readerFinished, totalJobs);

        SchedulingResult result = scheduler.run(context);

        context.shutdownLoader();
        loader.join();
        jobReader.join();

        return result;
    }

    private void awaitInitialAdmission(BlockingQueue<ProcessControlBlock> readyQueue,
                                        ProcessLoader loader,
                                        AtomicBoolean readerFinished,
                                        AtomicInteger totalJobs) throws InterruptedException {
        while (readyQueue.isEmpty()) {
            if (readerFinished.get() && loader.isLoadingComplete()) {
                break;
            }
            if (readerFinished.get() && totalJobs.get() == 0) {
                break;
            }
            Thread.sleep(5);
        }
    }
}
