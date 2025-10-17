# MemoryManager.java

## Overview

- Simplified main-memory allocator that coordinates admission of jobs into the ready queue.
- Ensures the simulator never exceeds the configured memory footprint (2048 MB).

## Responsibilities

- Track per-process memory allocations in a map keyed by PID.
- Synchronize allocation and release operations across multiple threads.
- Block callers until enough free memory exists to admit a new process.
- Log allocation and release events through `SystemCallHandler`.

## Key Methods

- Constructor `MemoryManager(int totalMemory, SystemCallHandler systemCalls)`: Captures the memory limit and logging dependency.
- `allocateBlocking(ProcessControlBlock pcb)`: Waits (via `wait()`) until sufficient memory is free, then reserves it and logs the operation; throws `InterruptedException` if interrupted.
- `release(ProcessControlBlock pcb)`: Frees the process allocation, adjusts accounting, logs the release, and wakes waiting threads with `notifyAll()`.
- Accessors: `getUsedMemory()`, `getTotalMemory()`, `getAllocatedProcessCount()` expose current state.

## Interactions

- `ProcessLoader` calls `allocateBlocking` before moving a PCB to the ready queue.
- Schedulers invoke `release` after a process completes to make memory available for others.
- `SimulationContext` surfaces usage stats to reporting routines.
