package simulator.core;

/**
 * Collects per-process statistics for reporting.
 */
public class ProcessReport {
    private final int processId;
    private final int waitingTime;
    private final int turnaroundTime;
    private final int responseTime;

    public ProcessReport(int processId, int waitingTime, int turnaroundTime, int responseTime) {
        this.processId = processId;
        this.waitingTime = waitingTime;
        this.turnaroundTime = turnaroundTime;
        this.responseTime = responseTime;
    }

    public int getProcessId() {
        return processId;
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
}
