# SimulationClock.java

## Overview

- Thread-safe logical clock shared by schedulers and support threads.
- Tracks simulated CPU time in integer units.

## Responsibilities

- Expose the current simulation time through an atomic integer.
- Provide a safe method to advance time by a non-negative delta.
- Guard against invalid (negative) time adjustments.

## Key Methods

- `getTime()`: Returns the current simulated time.
- `advance(int delta)`: Adds the supplied delta after validating it is non-negative; returns the updated time.

## Interactions

- Updated by schedulers whenever CPU time elapses during execution.
- Consulted by loader and schedulers to timestamp events, compute waiting durations, and support priority aging.
