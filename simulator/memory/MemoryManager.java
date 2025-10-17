package simulator.memory;

import java.util.HashMap;
import java.util.Map;

import simulator.core.ProcessControlBlock;
import simulator.core.SystemCallHandler;

/**
 * Provides a simple contiguous memory manager that coordinates with the loader and scheduler.
 */
public class MemoryManager {
    private final int totalMemory;
    private final SystemCallHandler systemCalls;

    private final Map<Integer, Integer> allocationByPid = new HashMap<>();
    private int usedMemory;

    public MemoryManager(int totalMemory, SystemCallHandler systemCalls) {
        this.totalMemory = totalMemory;
        this.systemCalls = systemCalls;
    }

    /**
     * Blocks until sufficient memory is available for the process.
     */
    public synchronized void allocateBlocking(ProcessControlBlock pcb) throws InterruptedException {
        int requested = pcb.getMemoryRequired();
        while (usedMemory + requested > totalMemory) {
            wait();
        }
        usedMemory += requested;
        allocationByPid.put(pcb.getId(), requested);
        systemCalls.allocateMemory(pcb, usedMemory, totalMemory);
    }

    /**
     * Releases the memory held by a terminated process.
     */
    public synchronized void release(ProcessControlBlock pcb) {
        Integer allocated = allocationByPid.remove(pcb.getId());
        if (allocated != null) {
            usedMemory -= allocated;
            if (usedMemory < 0) {
                usedMemory = 0;
            }
            systemCalls.releaseMemory(pcb, usedMemory, totalMemory);
            notifyAll();
        }
    }

    public synchronized int getUsedMemory() {
        return usedMemory;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public synchronized int getAllocatedProcessCount() {
        return allocationByPid.size();
    }
}
