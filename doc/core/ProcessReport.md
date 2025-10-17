# ProcessReport.java

## Overview

- Lightweight DTO that captures per-process metrics for final reporting.
- Derived from `ProcessControlBlock` instances after scheduling completes.

## Responsibilities

- Store the process identifier and the key delay metrics needed for user output (waiting, turnaround, response times).
- Provide simple accessor methods for use in formatted tables.

## Key Methods

- Constructor `ProcessReport(int processId, int waitingTime, int turnaroundTime, int responseTime)`: Populates all immutable fields.
- Accessors: `getProcessId()`, `getWaitingTime()`, `getTurnaroundTime()`, `getResponseTime()`.

## Interactions

- Created within `SchedulingResult.buildProcessReports()` to present tabular metrics in `Main.printResult`.
