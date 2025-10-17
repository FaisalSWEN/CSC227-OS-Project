package simulator.core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Collects simulated system call invocations to narrate the scheduling workflow.
 */
public class SystemCallHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<String> systemCallLog = Collections.synchronizedList(new ArrayList<>());
    private final List<String> starvationLog = Collections.synchronizedList(new ArrayList<>());

    private String timestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }

    private void record(String message) {
        systemCallLog.add("[" + timestamp() + "] " + message);
    }

    /**
     * Simulates the creation of a PCB by the operating system.
     */
    public void createProcess(ProcessControlBlock pcb) {
        record("sys_create: PCB " + pcb.getId() + " created with burst " + pcb.getBurstTime() + " and priority " + pcb.getBasePriority());
    }

    /**
     * Simulates placing the PCB into the job queue.
     */
    public void enqueueJob(ProcessControlBlock pcb, int queueSize) {
        record("sys_enqueue_job: PCB " + pcb.getId() + " added to job queue (size=" + queueSize + ")");
    }

    /**
     * Simulates reserving memory for the process in main memory.
     */
    public void allocateMemory(ProcessControlBlock pcb, int usedMemory, int totalMemory) {
        record("sys_alloc_mem: PCB " + pcb.getId() + " allocated " + pcb.getMemoryRequired() + "MB (used=" + usedMemory + "/" + totalMemory + "MB)");
    }

    /**
     * Simulates moving a job from the job queue into the ready queue.
     */
    public void admitToReady(ProcessControlBlock pcb, int readyTime, int degree) {
        record("sys_admit_ready: PCB " + pcb.getId() + " admitted to ready queue at t=" + readyTime + " (degree=" + degree + ")");
    }

    /**
     * Simulates dispatching a process onto the CPU.
     */
    public void dispatch(ProcessControlBlock pcb, int dispatchTime) {
        record("sys_dispatch: PCB " + pcb.getId() + " dispatched at t=" + dispatchTime);
    }

    /**
     * Simulates a context switch after a time slice expires.
     */
    public void yield(ProcessControlBlock pcb, int currentTime, int remainingTime) {
        record("sys_yield: PCB " + pcb.getId() + " yielded at t=" + currentTime + " (remaining=" + remainingTime + ")");
    }

    /**
     * Logs the completion of a process and releases its resources.
     */
    public void complete(ProcessControlBlock pcb, int completionTime) {
        record("sys_complete: PCB " + pcb.getId() + " completed at t=" + completionTime);
    }

    /**
     * Simulates freeing memory after a process terminates.
     */
    public void releaseMemory(ProcessControlBlock pcb, int usedMemory, int totalMemory) {
        record("sys_release_mem: PCB " + pcb.getId() + " memory released (used=" + usedMemory + "/" + totalMemory + "MB)");
    }

    /**
     * Records a detected starvation event for later reporting.
     */
    public void reportStarvation(ProcessControlBlock pcb, int waitingTime, int degree) {
        String message = "starvation_detected: PCB " + pcb.getId() + " waited " + waitingTime + " units (degree=" + degree + ")";
        starvationLog.add(message);
        record(message + " -> applying aging");
    }

    /**
     * Logs an aging boost applied to a process.
     */
    public void boostPriority(ProcessControlBlock pcb) {
        record("sys_age: PCB " + pcb.getId() + " boosted to priority " + pcb.getDynamicPriority());
    }

    /**
     * Retrieves an immutable view of the system call trace.
     */
    public List<String> getSystemCallLog() {
        return List.copyOf(systemCallLog);
    }

    /**
     * Retrieves starvation observations for reporting.
     */
    public List<String> getStarvationLog() {
        return List.copyOf(starvationLog);
    }
}
