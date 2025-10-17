package simulator.scheduler;

import simulator.app.SimulationContext;
import simulator.core.SchedulingResult;

/**
 * Contract implemented by each scheduling algorithm.
 */
public interface Scheduler {
    /**
     * Returns the human readable name of the scheduler.
     */
    String getName();

    /**
     * Runs the scheduling algorithm using the shared simulation context.
     */
    SchedulingResult run(SimulationContext context) throws InterruptedException;
}
