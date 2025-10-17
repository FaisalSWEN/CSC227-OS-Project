# SimulationRunner.java

## Overview

- Bootstraps a complete simulation cycle around a chosen `Scheduler` implementation.
- Creates and manages the supporting worker threads (`JobReader` and `ProcessLoader`).
- Provides each run with fresh infrastructure instances to avoid cross-run contamination.

## Responsibilities

- Instantiate core services (`SystemCallHandler`, `SimulationClock`, `MemoryManager`) and the shared job/ready queues.
- Launch the reader and loader threads, then wait for the first processes to become ready before scheduling begins.
- Call `Scheduler.run(SimulationContext)` and return the resulting `SchedulingResult` to the caller.
- Handle orderly teardown by requesting loader shutdown and joining worker threads, even on interruption.

## Key Methods

- Constructor `SimulationRunner(Path jobFile)`: Captures the path to the job definition file.
- `execute(Scheduler scheduler)`: Orchestrates the full lifecycle of a simulation run and returns the aggregated results; throws `InterruptedException` if the caller is interrupted.
- `awaitInitialAdmission(...)`: Internal helper that waits until at least one process is admitted (or the loader finishes) before invoking the scheduler.

## Interactions

- Spawns `JobReader` to parse `job.txt` into the job queue and track total jobs.
- Spawns `ProcessLoader` to move jobs into the ready queue once memory becomes available.
- Supplies `SimulationContext` with references to queues, loader, clock, system call handler, and accounting counters.
- Invokes `Scheduler.run(...)`, expecting the scheduler to adhere to the shared context contract.
