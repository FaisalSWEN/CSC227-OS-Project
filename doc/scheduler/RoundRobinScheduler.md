# RoundRobinScheduler.java

## Overview

- Implements a preemptive round-robin CPU scheduler with a fixed quantum of 7 time units.
- Ensures fair CPU sharing by rotating through the ready queue in FIFO order.

## Responsibilities

- Maintain an internal `ArrayDeque` that mirrors the ready queue pulled from `SimulationContext`.
- Dispatch the head process, run it for up to one quantum, and record an `ExecutionSlice`.
- Either requeue the process (if it still has remaining time) or finalize it and release memory.
- Cooperate with context termination conditions, pulling new jobs when the queue becomes empty.

## Key Methods

- `getName()`: Returns a descriptive label including the configured quantum.
- `run(SimulationContext context)`: Executes the round-robin loop, handling dispatch, preemption, requeueing, and completion until all work is done.

## Interactions

- Consumes ready processes via `SimulationContext.drainReadyQueue`/`takeNextReady` and requeues them locally.
- Invokes PCB methods (`markDispatched`, `consumeCpu`, `markRequeued`, `markCompleted`) to manage state transitions.
- Uses `SystemCallHandler.dispatch`, `yield`, and `complete` to log preemption and completion events.
- Releases memory through `MemoryManager` when a process finishes execution.
