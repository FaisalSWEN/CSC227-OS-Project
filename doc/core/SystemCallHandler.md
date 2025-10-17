# SystemCallHandler.java

## Overview

- Collects a narrative of simulated system calls and starvation events.
- Centralizes all user-visible logging for process lifecycle actions.

## Responsibilities

- Timestamp and record system-level events in chronological order.
- Provide specialized log entries for creation, queueing, dispatch, yielding, completion, and memory operations.
- Track starvation detections and priority boosts triggered by the Priority scheduler.
- Expose immutable views of both the system call log and the starvation log to consumers.

## Key Methods

- `createProcess`, `enqueueJob`, `allocateMemory`, `admitToReady`: Called by reader/loader to reflect early lifecycle steps.
- `dispatch`, `yield`, `complete`, `releaseMemory`: Invoked by schedulers when CPU state changes or processes finish.
- `reportStarvation`: Generates a starvation message and appends it to both logs.
- `boostPriority`: Records the result of applying an aging adjustment.
- `getSystemCallLog()`, `getStarvationLog()`: Return thread-safe snapshots of the collected entries.

## Interactions

- `JobReader`, `ProcessLoader`, and scheduler implementations call the relevant logging methods.
- `Main.printResult` and `SchedulingResult` consume the returned logs to show execution traces to the user.
