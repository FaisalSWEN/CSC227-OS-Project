# Scheduler.java

## Overview

- Defines the contract implemented by all CPU scheduling strategies in the simulator.

## Responsibilities

- Provide a human-readable name for UI and reporting through `getName()`.
- Execute the scheduling algorithm against a shared `SimulationContext`, returning a `SchedulingResult`.

## Key Methods

- `String getName()`: Identifies the scheduler in menus and summaries.
- `SchedulingResult run(SimulationContext context) throws InterruptedException`: Runs the algorithm to completion, potentially propagating interruptions from the caller.

## Interactions

- Implemented by `SjfScheduler`, `RoundRobinScheduler`, and `PriorityScheduler`.
- `Main` uses the interface to treat the different algorithms uniformly when executing and reporting results.
