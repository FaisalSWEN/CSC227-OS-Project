package simulator.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import simulator.core.ProcessControlBlock;
import simulator.core.SystemCallHandler;

/**
 * Dedicated thread that loads job definitions from the job file into the job queue.
 */
public class JobReader extends Thread {
    private static final int MAX_JOBS = 30;
    private static final int MAX_TOTAL_MEMORY = 2048;

    private final Path jobFilePath;
    private final BlockingQueue<ProcessControlBlock> jobQueue;
    private final SystemCallHandler systemCalls;
    private final AtomicInteger totalJobs;
    private final AtomicBoolean finished;

    public JobReader(Path jobFilePath,
                     BlockingQueue<ProcessControlBlock> jobQueue,
                     SystemCallHandler systemCalls,
                     AtomicInteger totalJobs,
                     AtomicBoolean finished) {
        super("job-reader");
        this.jobFilePath = jobFilePath;
        this.jobQueue = jobQueue;
        this.systemCalls = systemCalls;
        this.totalJobs = totalJobs;
        this.finished = finished;
    }

    @Override
    public void run() {
        int cumulativeMemory = 0;
        try (BufferedReader reader = Files.newBufferedReader(jobFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int currentCount = totalJobs.get();
                if (currentCount >= MAX_JOBS) {
                    throw new IllegalStateException("job.txt exceeds maximum supported job count of " + MAX_JOBS);
                }
                ProcessControlBlock pcb = parseLine(line, currentCount);
                cumulativeMemory += pcb.getMemoryRequired();
                if (cumulativeMemory > MAX_TOTAL_MEMORY) {
                    throw new IllegalStateException("job.txt requires more than " + MAX_TOTAL_MEMORY + "MB of memory");
                }
                systemCalls.createProcess(pcb);
                pcb.markQueued();
                jobQueue.put(pcb);
                systemCalls.enqueueJob(pcb, jobQueue.size());
                totalJobs.incrementAndGet();
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Failed to read job file", ex);
        } finally {
            finished.set(true);
        }
    }

    private ProcessControlBlock parseLine(String line, int arrivalOrder) {
        String[] parts = line.split(";");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid job entry: " + line);
        }
        String[] fields = parts[0].split(":");
        if (fields.length != 3) {
            throw new IllegalArgumentException("Invalid job definition: " + line);
        }
        int id = Integer.parseInt(fields[0].trim());
        int burst = Integer.parseInt(fields[1].trim());
        int priority = Integer.parseInt(fields[2].trim());
        int memory = Integer.parseInt(parts[1].trim());
        return new ProcessControlBlock(id, burst, priority, memory, 0, arrivalOrder);
    }
}
