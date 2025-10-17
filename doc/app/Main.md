# Main.java

## Overview

- Console entry point that drives the CPU scheduler simulator.
- Reads `job.txt`, presents an interactive menu, and dispatches the selected scheduling workloads.

## Responsibilities

- Validate that the required `job.txt` file exists before running.
- Build the list of available `Scheduler` implementations and route user selections to them.
- Invoke `SimulationRunner` for each chosen scheduler and stream the resulting reports to the console.
- Provide detailed per-run reporting, including Gantt charts, metrics, starvation notices, and system call traces.

## Key Methods

- `main(String[] args)`: Validates the environment, loops over user input, and triggers scheduler execution.
- `printMenu()`: Renders the menu shown on each iteration of the REPL loop.
- `runSchedulers(SimulationRunner runner, List<Scheduler> schedulers)`: Executes one or more schedulers sequentially and accumulates their `SchedulingResult` objects.
- `printResult(SchedulingResult result)`: Formats statistics and event logs for a single scheduler run.
- `printComparison(List<SchedulingResult> results)`: Summarizes average waiting and turnaround times when multiple schedulers are executed together.

## Interactions

- Constructs `SimulationRunner`, which manages supporting threads and shared context.
- Uses `Scheduler` implementations from `simulator.scheduler` (SJF, Round Robin, Priority).
- Consumes `SchedulingResult` and `ProcessReport` data from `simulator.core` for presentation.
