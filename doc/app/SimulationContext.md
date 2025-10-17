# SimulationContext.java

## Overview

- Central coordination object shared between the scheduler thread and the background loader.
- Exposes controlled access to the ready queue, memory manager, clock, and system call handler.

## Responsibilities

- Provide thread-safe access to ready queue contents (`drainReadyQueue`, `takeNextReady`).
- Surface state queries that let schedulers know when work is finished (`canTerminate`, `isLoaderFinished`, etc.).
- Relay references to supporting services such as `MemoryManager`, `SystemCallHandler`, and `SimulationClock`.
- Support orderly shutdown of the loader once scheduling concludes (`shutdownLoader`).

## Key Methods

- `drainReadyQueue(List<ProcessControlBlock> target)`: Atomically transfers all currently ready PCBs into a caller-supplied buffer.
- `takeNextReady(long timeoutMillis)`: Blocks for a bounded period waiting for the next ready process.
- `canTerminate(int completedProcesses)`: Determines whether the scheduler can halt based on loader status and job counts.
- Accessors: `getMemoryManager()`, `getSystemCalls()`, `getClock()`, `getUsedMemory()`, `getTotalMemory()` provide shared services and metrics.
- State checks: `isReaderFinished()`, `isLoaderFinished()`, `isReadyQueueEmpty()` help detect completion criteria.
- `shutdownLoader()`: Signals the loader thread to stop accepting new work.

## Interactions

- Consumed by all `Scheduler` implementations to fetch work and observe system state.
- Receives references to `ProcessLoader`, `MemoryManager`, `SimulationClock`, and concurrency primitives instantiated in `SimulationRunner`.
