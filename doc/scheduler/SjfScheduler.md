# SjfScheduler.java

## Overview

- Implements a non-preemptive shortest-job-first scheduling algorithm.
- Selects the ready process with the smallest remaining CPU burst and runs it to completion.

## Responsibilities

- Continuously pull ready processes from `SimulationContext` and maintain a local candidate list.
- Choose the next process based on remaining time (tie-breaking by arrival order).
- Dispatch, execute to completion, and finalize PCBs while recording execution slices and metrics.
- Respect termination criteria exposed by the shared context (reader/loader completion and finished job count).

## Key Methods

- `getName()`: Returns the display name used in the UI.
- `run(SimulationContext context)`: Scheduling loop that drains the ready queue, handles idle waits, selects the shortest job, and finalizes it.

## Interactions

- Uses PCB APIs (`markDispatched`, `consumeCpu`, `markCompleted`) to manage lifecycle.
- Logs dispatch and completion through `SystemCallHandler` and frees memory with `MemoryManager`.
- Returns a `SchedulingResult` comprising execution slices, completed PCBs, starvation notices, and system call logs.
