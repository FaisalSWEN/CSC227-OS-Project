# SchedulingResult.java

## Overview

- Aggregate report produced by each scheduler run.
- Encapsulates execution slices, completed processes, starvation notices, and the system call log for downstream consumers.

## Responsibilities

- Retain immutable snapshots of runtime data using defensive copies of lists.
- Provide convenience analytics such as average waiting/turnaround times and process-level reports.
- Generate text representations like the ASCII Gantt chart.
- Offer filtered views of starvation data keyed by process ID.

## Key Methods

- Constructor: Accepts the scheduler name and lists of `ExecutionSlice`, `ProcessControlBlock`, and log strings.
- Accessors: `getSchedulerName()`, `getSlices()`, `getCompletedProcesses()`, `getStarvationNotices()`, `getSystemCallLog()`.
- Metrics: `getAverageWaitingTime()`, `getAverageTurnaroundTime()` use streams to compute statistics.
- Reporting helpers: `buildProcessReports()`, `buildGanttChart()`, `getStarvationEventsByProcess()`.

## Interactions

- Returned by every `Scheduler` implementation and consumed by `Main` for console output.
- Uses data produced by schedulers (`ExecutionSlice` instances) and PCBs to drive summaries and charts.
