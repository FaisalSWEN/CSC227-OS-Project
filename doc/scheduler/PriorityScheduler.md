# PriorityScheduler.java

## Overview

- Implements a non-preemptive priority scheduling algorithm with starvation detection and aging.
- Chooses the ready process with the highest dynamic priority; breaks ties using wait time and arrival order.

## Responsibilities

- Continuously refill the local ready list from `SimulationContext` and coordinate with the shared clock.
- Apply aging to prevent starvation by periodically boosting priorities of long-waiting processes.
- Dispatch the selected process, run it to completion in one burst, and release its memory.
- Capture execution slices and update completion metrics for reporting.

## Key Methods

- `getName()`: Returns the human-readable name used in menus and reports.
- `run(SimulationContext context)`: Core scheduling loop that manages ready queues, aging, dispatch, and completion until termination conditions are met.
- `applyAging(List<ProcessControlBlock> ready, SimulationClock clock, SystemCallHandler sys)`: Detects starvation and increments dynamic priority based on the `AGING_INTERVAL` (5 time units).

## Interactions

- Reads ready processes through `SimulationContext.drainReadyQueue` and `takeNextReady`.
- Uses `ProcessControlBlock` APIs (`markDispatched`, `consumeCpu`, `markCompleted`, `markStarvation`, `boostPriority`).
- Logs actions (`dispatch`, `complete`, `reportStarvation`, `boostPriority`) through `SystemCallHandler`.
- Releases memory via `MemoryManager` when a process finishes execution.
