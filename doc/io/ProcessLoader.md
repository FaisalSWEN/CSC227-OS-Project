# ProcessLoader.java

## Overview

- Background thread that transfers PCBs from the job queue into the ready queue once memory is available.
- Bridges the gap between offline job ingestion (`JobReader`) and online scheduling.

## Responsibilities

- Poll the job queue, waiting for work until both the reader is finished and the queue is empty.
- Block on `MemoryManager.allocateBlocking` to enforce the 2048 MB memory constraint before admitting a process.
- Timestamp ready admissions via `SimulationClock`, update PCB state, and log the event through `SystemCallHandler`.
- Place PCBs on the ready queue where schedulers can pick them up.
- Support cooperative shutdown through `requestShutdown()` and mark completion with `loadingComplete`.

## Key Methods

- Constructor: Accepts job/ready queues, memory manager, system call handler, clock, and the reader-finished flag.
- `run()`: Main worker loop that continues until shutdown is requested or all jobs are processed; handles interruption gracefully.
- `requestShutdown()`: Sets the shutdown flag and interrupts the thread to prompt exit.
- `isLoadingComplete()`: Indicates that no additional jobs will be enqueued in the ready queue.

## Interactions

- Consumes PCBs created by `JobReader` and produces ready jobs for schedulers via the shared queue.
- Collaborates with `MemoryManager` to honor memory limits and with `SystemCallHandler` to record admissions.
- Observed by `SimulationContext` and `SimulationRunner` to decide when scheduling can terminate.
