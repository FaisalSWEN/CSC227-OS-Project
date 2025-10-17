package simulator.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import simulator.core.ProcessReport;
import simulator.core.SchedulingResult;
import simulator.scheduler.PriorityScheduler;
import simulator.scheduler.RoundRobinScheduler;
import simulator.scheduler.Scheduler;
import simulator.scheduler.SjfScheduler;

/**
 * Entry point that lets the user choose which scheduling algorithms to simulate.
 */
public class Main {
    private static final List<Scheduler> SCHEDULERS = List.of(
            new SjfScheduler(),
            new RoundRobinScheduler(),
            new PriorityScheduler()
    );

    public static void main(String[] args) {
        Path jobFile = Path.of("job.txt");
        if (!Files.exists(jobFile)) {
            System.err.println("Missing job.txt file in working directory: " + jobFile.toAbsolutePath());
            return;
        }
        SimulationRunner runner = new SimulationRunner(jobFile);
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1":
                    case "2":
                    case "3":
                        int index = Integer.parseInt(choice) - 1;
                        runSchedulers(runner, List.of(SCHEDULERS.get(index)));
                        break;
                    case "4":
                        runSchedulers(runner, SCHEDULERS);
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please choose again.");
                }
            }
        }
        System.out.println("Simulation finished.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("=== CPU Scheduler Simulator ===");
        System.out.println("1) Shortest Job First");
        System.out.println("2) Round Robin (q=7)");
        System.out.println("3) Priority Scheduling");
        System.out.println("4) Run All & Compare");
        System.out.println("5) Exit");
        System.out.print("Select an option: ");
    }

    private static void runSchedulers(SimulationRunner runner, List<Scheduler> schedulers) {
        List<SchedulingResult> results = new ArrayList<>();
        for (Scheduler scheduler : schedulers) {
            try {
                SchedulingResult result = runner.execute(scheduler);
                results.add(result);
                printResult(result);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("Simulation interrupted: " + ex.getMessage());
                return;
            } catch (RuntimeException ex) {
                System.err.println("Simulation failed: " + ex.getMessage());
                return;
            }
        }
        if (results.size() > 1) {
            printComparison(results);
        }
    }

    private static void printResult(SchedulingResult result) {
        System.out.println();
        System.out.println("=== " + result.getSchedulerName() + " ===");
        System.out.println("Summary:");
        System.out.printf("  - Average waiting time   : %.2f%n", result.getAverageWaitingTime());
        System.out.printf("  - Average turnaround time: %.2f%n", result.getAverageTurnaroundTime());

        System.out.println();
        System.out.println("Gantt Chart:");
        System.out.println(result.buildGanttChart());

        System.out.println();
        System.out.println("Per-Process Metrics:");
        System.out.printf("  %-10s %-12s %-15s %-12s%n", "Process", "Waiting", "Turnaround", "Response");
        for (ProcessReport report : result.buildProcessReports()) {
            System.out.printf("  %-10d %-12d %-15d %-12d%n",
                    report.getProcessId(),
                    report.getWaitingTime(),
                    report.getTurnaroundTime(),
                    report.getResponseTime());
        }

        Map<Integer, List<String>> starvationEvents = result.getStarvationEventsByProcess();
        if (!starvationEvents.isEmpty()) {
            System.out.println("Starvation notices:");
            starvationEvents.forEach((pid, events) -> {
                for (String event : events) {
                    System.out.println("  " + event);
                }
            });
        }
        System.out.println();
        List<String> systemCalls = result.getSystemCallLog();
        System.out.println("System Call Trace (" + systemCalls.size() + " entries):");
        for (String entry : systemCalls) {
            System.out.println("  - " + entry);
        }
    }

    private static void printComparison(List<SchedulingResult> results) {
        System.out.println();
        System.out.println("=== Comparison Summary ===");
        System.out.printf("%-25s %-20s %-20s%n", "Scheduler", "Avg Waiting", "Avg Turnaround");
        for (SchedulingResult result : results) {
            System.out.printf("%-25s %-20.2f %-20.2f%n",
                    result.getSchedulerName(),
                    result.getAverageWaitingTime(),
                    result.getAverageTurnaroundTime());
        }
    }
}
