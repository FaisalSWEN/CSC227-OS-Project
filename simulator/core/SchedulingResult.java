package simulator.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregates the outcome of running a scheduling algorithm.
 */
public class SchedulingResult {
    private final String schedulerName;
    private final List<ExecutionSlice> slices;
    private final List<ProcessControlBlock> completedProcesses;
    private final List<String> starvationNotices;
    private final List<String> systemCallLog;

    public SchedulingResult(String schedulerName,
                            List<ExecutionSlice> slices,
                            List<ProcessControlBlock> completedProcesses,
                            List<String> starvationNotices,
                            List<String> systemCallLog) {
        this.schedulerName = schedulerName;
        this.slices = List.copyOf(slices);
        this.completedProcesses = List.copyOf(completedProcesses);
        this.starvationNotices = List.copyOf(starvationNotices);
        this.systemCallLog = List.copyOf(systemCallLog);
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public List<ExecutionSlice> getSlices() {
        return slices;
    }

    public List<ProcessControlBlock> getCompletedProcesses() {
        return completedProcesses;
    }

    public List<String> getStarvationNotices() {
        return starvationNotices;
    }

    public List<String> getSystemCallLog() {
        return systemCallLog;
    }

    public double getAverageWaitingTime() {
        return completedProcesses.stream()
                .mapToInt(ProcessControlBlock::getWaitingTime)
                .average()
                .orElse(0.0);
    }

    public double getAverageTurnaroundTime() {
        return completedProcesses.stream()
                .mapToInt(ProcessControlBlock::getTurnaroundTime)
                .average()
                .orElse(0.0);
    }

    public List<ProcessReport> buildProcessReports() {
        List<ProcessReport> reports = new ArrayList<>();
        for (ProcessControlBlock pcb : completedProcesses) {
            reports.add(new ProcessReport(
                    pcb.getId(),
                    pcb.getWaitingTime(),
                    pcb.getTurnaroundTime(),
                    pcb.getResponseTime()
            ));
        }
        return reports;
    }

    /**
     * Formats a simple ASCII Gantt chart for the execution timeline.
     */
    public String buildGanttChart() {
        if (slices.isEmpty()) {
            return "(no execution)";
        }
        StringBuilder border = new StringBuilder("+");
        StringBuilder labels = new StringBuilder("|");
        StringBuilder times = new StringBuilder();

        int start = slices.get(0).getStartTime();
        times.append(String.format("%-8d", start));

        for (ExecutionSlice slice : slices) {
            border.append("-------+");
            labels.append(String.format(" P%-5d|", slice.getProcessId()));
            times.append(String.format("%-8d", slice.getEndTime()));
        }

        StringBuilder chart = new StringBuilder();
        chart.append(border).append(System.lineSeparator());
        chart.append(labels).append(System.lineSeparator());
        chart.append(border).append(System.lineSeparator());
        chart.append(times);
        return chart.toString();
    }

    public Map<Integer, List<String>> getStarvationEventsByProcess() {
        return completedProcesses.stream()
                .filter(ProcessControlBlock::hasSufferedStarvation)
                .collect(Collectors.toMap(ProcessControlBlock::getId, ProcessControlBlock::getStarvationEvents));
    }
}
