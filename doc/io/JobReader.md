# JobReader.java

## Overview

- Dedicated thread responsible for parsing `job.txt` and populating the job queue.
- Enforces global constraints on job count and total memory usage.

## Responsibilities

- Read the job file line by line, skipping comments and blank lines.
- Validate format: `Process_ID:Burst_Time:Priority;Memory_Required`.
- Enforce `MAX_JOBS` and `MAX_TOTAL_MEMORY` limits, throwing when the input violates assumptions.
- Instantiate `ProcessControlBlock` objects, invoke `SystemCallHandler.createProcess`, mark them as queued, and enqueue them.
- Maintain atomic counters (`totalJobs`, `finished`) to signal completion to other components.

## Key Methods

- Constructor: Accepts the job file path, target queue, system call handler, job counter, and completion flag.
- `run()`: Main loop that parses, validates, and loads jobs; throws a runtime exception on I/O or interruption failures.
- `parseLine(String line, int arrivalOrder)`: Splits and parses a well-formed row into a new PCB.

## Interactions

- Supplies work to the blocking job queue consumed by `ProcessLoader`.
- Updates `AtomicInteger totalJobs` for `SimulationContext` termination logic.
- Signals completion through the `AtomicBoolean finished` flag observed by `ProcessLoader` and schedulers.
