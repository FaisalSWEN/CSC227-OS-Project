# CSC227 Operating Systems: CPU Scheduler Simulator

This project is a Java-based simulation of a single-CPU scheduling system, developed for the CSC227 Operating Systems course. The primary objective is to implement and compare three different CPU scheduling algorithms using multithreading to manage processes.

## 📋 Project Overview

The simulator reads process information from an external file (`job.txt`), loads them into a ready queue based on memory availability, and executes them according to a user-selected scheduling algorithm. It then calculates and displays performance metrics like average waiting time and turnaround time.

## ✨ Features Implemented

* **Three Scheduling Algorithms**:
    * **Shortest Job First (SJF)**: A non-preemptive algorithm that selects the process with the smallest burst time.
    * **Round-Robin (RR)**: A preemptive algorithm with a time quantum of **7ms**.
    * **Priority Scheduling**: A non-preemptive algorithm where a higher number indicates a higher priority (1 = Lowest, 128 = Highest).

* **Multithreading**: The application utilizes multiple threads for concurrent operations:
    * **File Reader Thread**: An independent thread that reads process data from `job.txt`, creates a Process Control Block (PCB) for each, and places them in the job queue.
    * **Process Loader Thread**: A thread that continuously monitors memory and moves processes from the job queue to the ready queue if sufficient memory is available.
    * **Main Thread**: Responsible for running the chosen scheduling algorithm.

* **Memory Management**: Simulates a main memory of **2048 MB**. A process is only loaded into the ready queue if there's enough space for it.

* **Starvation Handling**: For the Priority Scheduling algorithm, the simulator detects and resolves starvation using an **aging** technique.

* **System Call Simulation**: The program proposes and simulates a set of system calls for process control, memory management, and information maintenance.

## 🚀 How to Run

1.  **Prerequisites**:
    * Java Development Kit (JDK) installed.

2.  **Compilation**:
    * Open your terminal or command prompt.
    * Navigate to the source code directory.
    * Compile the Java files:
        ```bash
        javac *.java
        ```

3.  **Execution**:
    * Make sure the `job.txt` file is in the same directory.
    * Run the main class:
        ```bash
        java MainClassName
        ```
    * The program will then prompt you to choose a scheduling algorithm.

## 📁 Input File Format (`job.txt`)

The program reads process data from a file named `job.txt`. Each line in the file represents a single job and must follow this format:

`Process_ID:Burst_Time_ms:Priority;Memory_Required_MB`

**Example `job.txt`:**
```
1:25:4;500
2:13:3;700
3:20:3;100
```

## 📊 Expected Output

The program will:
1.  Display a step-by-step execution trace for the selected algorithm, preferably as a **Gantt chart**.
2.  Show the start and end times for each process.
3.  For Priority scheduling, it will indicate if any process suffered from starvation.
4.  Finally, it will print a comparison of the **average waiting time** and **average turnaround time** for all jobs.
