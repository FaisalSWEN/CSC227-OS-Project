# ExecutionSlice.java

## Overview

- Immutable record describing a contiguous CPU execution window for a single process.
- Used to reconstruct Gantt charts and execution timelines in reporting.

## Responsibilities

- Hold the process identifier and the start/end timestamps (inclusive of start, exclusive of end).
- Provide derived metrics such as the executed duration.

## Key Methods

- Constructor `ExecutionSlice(int processId, int startTime, int endTime)`: Captures immutable slice data.
- Accessors `getProcessId()`, `getStartTime()`, `getEndTime()` expose individual fields.
- `getDuration()`: Computes how many time units elapsed during the slice.

## Interactions

- Created by schedulers (SJF, Round Robin, Priority) when they record execution fragments.
- Aggregated inside `SchedulingResult` for visualization and metrics.
