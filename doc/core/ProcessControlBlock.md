# ProcessControlBlock.java

## Overview

- Rich domain model for simulated processes, mirroring an OS process control block (PCB).
- Tracks lifecycle state, timing statistics, priority adjustments, and starvation history.

## Responsibilities

- Capture immutable workload attributes (ID, burst time, base priority, memory footprint, arrival metadata).
- Maintain mutable scheduling state: remaining time, dynamic priority, execution timestamps, and metrics.
- Provide state transition helpers invoked by loaders and schedulers (`markQueued`, `markReady`, `markDispatched`, `markCompleted`).
- Support preemptive workflows via `consumeCpu`, `markRequeued`, and priority aging (`boostPriority`).
- Record starvation events and generate user-facing messages for reporting.

## Key Methods & Fields

- `ProcessState` enum: Defines lifecycle stages (`NEW`, `JOB_QUEUED`, `READY`, `RUNNING`, `TERMINATED`).
- Constructor: Initializes dynamic priority to the base priority and remaining time to the burst length.
- `markReady(int currentTime, int degree, boolean initialAdmission)`: Updates ready timestamps, multiprogramming degree, and ensures the first admission is recorded.
- `markDispatched(int currentTime)`: Sets running state, captures response time, and accumulates waiting time.
- `consumeCpu(int requested)`: Deducts runtime, guarding against overconsumption by returning the actual amount executed.
- `markRequeued(int currentTime, int degree)`: Re-enters the ready state without overwriting initial admission data.
- `markCompleted(int currentTime)`: Finalizes completion and turnaround metrics.
- Starvation/Aging: `markStarvation(int waitingDuration)`, `boostPriority(int delta)`, plus tracking of `priorityBoostCount`.
- Accessors expose metrics for reporting (`getWaitingTime`, `getTurnaroundTime`, `getResponseTime`, etc.).

## Interactions

- `JobReader` and `ProcessLoader` call `markQueued`/`markReady` when jobs progress through the pipeline.
- Schedulers drive execution via `markDispatched`, `consumeCpu`, `markRequeued`, and `markCompleted`.
- `SystemCallHandler` reads PCB properties to log system calls and starvation notices.
- `SchedulingResult` inspects completed PCBs to compute averages and emission data.
