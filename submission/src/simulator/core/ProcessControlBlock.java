package simulator.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a process control block (PCB) that tracks the lifecycle and statistics of a job.
 */
public class ProcessControlBlock {
    /**
     * Distinct lifecycle stages for a process in the simulator.
     */
    public enum ProcessState {
        NEW,
        JOB_QUEUED,
        READY,
        RUNNING,
        TERMINATED
    }

    private final int id;
    private final int burstTime;
    private final int basePriority;
    private final int memoryRequired;
    private final int arrivalTime;
    private final int arrivalOrder;

    private ProcessState state = ProcessState.NEW;
    private int dynamicPriority;
    private int remainingTime;
    private int executedTime;

    private int waitingTime;
    private int turnaroundTime;
    private int responseTime = -1;

    private int startTime = -1;
    private int completionTime = -1;
    private int readyAdmissionTime = -1;
    private int admissionDegree;
    private int lastReadyTimestamp;
    private int priorityBoostCount;

    private boolean sufferedStarvation;
    private final List<String> starvationEvents = new ArrayList<>();

    /**
     * Builds a PCB with immutable workload details.
     */
    public ProcessControlBlock(int id, int burstTime, int basePriority, int memoryRequired, int arrivalTime, int arrivalOrder) {
        this.id = id;
        this.burstTime = burstTime;
        this.basePriority = basePriority;
        this.memoryRequired = memoryRequired;
        this.arrivalTime = arrivalTime;
        this.arrivalOrder = arrivalOrder;
        this.dynamicPriority = basePriority;
        this.remainingTime = burstTime;
    }

    /**
     * Marks the process as queued in the job list.
     */
    public void markQueued() {
        this.state = ProcessState.JOB_QUEUED;
    }

    /**
     * Marks the process as ready and records timing metadata.
     */
    public void markReady(int currentTime, int degreeOfMultiprogramming, boolean initialAdmission) {
        this.state = ProcessState.READY;
        this.lastReadyTimestamp = currentTime;
        if (initialAdmission && this.readyAdmissionTime < 0) {
            this.readyAdmissionTime = currentTime;
            this.admissionDegree = degreeOfMultiprogramming;
        }
    }

    /**
     * Records the transition to running and the experienced wait time.
     */
    public void markDispatched(int currentTime) {
        this.state = ProcessState.RUNNING;
        if (this.startTime < 0) {
            this.startTime = currentTime;
        }
        if (this.responseTime < 0) {
            this.responseTime = currentTime - this.arrivalTime;
        }
        this.waitingTime += currentTime - this.lastReadyTimestamp;
    }

    /**
     * Consumes CPU time and returns how many units were actually executed.
     */
    public int consumeCpu(int requested) {
        int granted = Math.min(requested, this.remainingTime);
        this.remainingTime -= granted;
        this.executedTime += granted;
        return granted;
    }

    /**
     * Marks a process as re-queued after partial execution.
     */
    public void markRequeued(int currentTime, int degreeOfMultiprogramming) {
        markReady(currentTime, degreeOfMultiprogramming, false);
    }

    /**
     * Finalizes bookkeeping for a completed job.
     */
    public void markCompleted(int currentTime) {
        this.state = ProcessState.TERMINATED;
        this.completionTime = currentTime;
        this.turnaroundTime = this.completionTime - this.arrivalTime;
    }

    /**
     * Flags a starvation incident for later reporting.
     */
    public void markStarvation(int waitingDuration) {
        if (!this.sufferedStarvation) {
            this.sufferedStarvation = true;
        }
        this.starvationEvents.add("Process " + id + " waited " + waitingDuration + " units before aging");
    }

    /**
     * Applies an aging boost to the dynamic priority, respecting the upper bound.
     */
    public void boostPriority(int delta) {
        if (delta <= 0) {
            return;
        }
        for (int i = 0; i < delta; i++) {
            if (this.dynamicPriority < 128) {
                this.dynamicPriority++;
                this.priorityBoostCount++;
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getBasePriority() {
        return basePriority;
    }

    public int getMemoryRequired() {
        return memoryRequired;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getArrivalOrder() {
        return arrivalOrder;
    }

    public ProcessState getState() {
        return state;
    }

    public int getDynamicPriority() {
        return dynamicPriority;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getReadyAdmissionTime() {
        return readyAdmissionTime;
    }

    public int getAdmissionDegree() {
        return admissionDegree;
    }

    public int getLastReadyTimestamp() {
        return lastReadyTimestamp;
    }

    public int getExecutedTime() {
        return executedTime;
    }

    public boolean hasSufferedStarvation() {
        return sufferedStarvation;
    }

    public List<String> getStarvationEvents() {
        return Collections.unmodifiableList(starvationEvents);
    }

    public int getPriorityBoostCount() {
        return priorityBoostCount;
    }

    public void resetDynamicPriority() {
        this.dynamicPriority = this.basePriority;
        this.priorityBoostCount = 0;
    }
}
