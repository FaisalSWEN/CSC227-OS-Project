package simulator.core;

/**
 * Represents a contiguous execution window for a process on the simulated CPU.
 */
public class ExecutionSlice {
    private final int processId;
    private final int startTime;
    private final int endTime;

    public ExecutionSlice(int processId, int startTime, int endTime) {
        this.processId = processId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getProcessId() {
        return processId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getDuration() {
        return endTime - startTime;
    }
}
