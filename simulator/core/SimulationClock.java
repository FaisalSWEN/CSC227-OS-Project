package simulator.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Maintains the simulated CPU time that all components consult.
 */
public class SimulationClock {
    private final AtomicInteger time = new AtomicInteger(0);

    /**
     * Returns the current logical time.
     */
    public int getTime() {
        return time.get();
    }

    /**
     * Advances the logical clock by the specified delta.
     */
    public int advance(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Delta must be non-negative");
        }
        return time.addAndGet(delta);
    }
}
