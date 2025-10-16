# CSC227 Operating Systems: CPU Scheduler Simulator

[cite_start]This project is a Java-based simulation of a single-CPU scheduling system, developed for the CSC227 Operating Systems course[cite: 1]. [cite_start]The primary objective is to implement and compare three different CPU scheduling algorithms using multithreading to manage processes[cite: 5].

## 📋 Project Overview

The simulator reads process information from an external file (`job.txt`), loads them into a ready queue based on memory availability, and executes them according to a user-selected scheduling algorithm. [cite_start]It then calculates and displays performance metrics like average waiting time and turnaround time[cite: 14, 16, 18, 33].

## ✨ Features Implemented

* **Three Scheduling Algorithms**:
    * [cite_start]**Shortest Job First (SJF)**: A non-preemptive algorithm that selects the process with the smallest burst time[cite: 9].
    * [cite_start]**Round-Robin (RR)**: A preemptive algorithm with a time quantum of **7ms**[cite: 10].
    * [cite_start]**Priority Scheduling**: A non-preemptive algorithm where a higher number indicates a higher priority (1 = Lowest, 128 = Highest)[cite: 11].

* [cite_start]**Multithreading**: The application utilizes multiple threads for concurrent operations[cite: 31]:
    * [cite_start]**File Reader Thread**: An independent thread that reads process data from `job.txt`, creates a Process Control Block (PCB) for each, and places them in the job queue[cite: 15].
    * [cite_start]**Process Loader Thread**: A thread that continuously monitors memory and moves processes from the job queue to the ready queue if sufficient memory is available[cite: 16].
    * [cite_start]**Main Thread**: Responsible for running the chosen scheduling algorithm[cite: 18].

* **Memory Management**: Simulates a main memory of **2048 MB**. [cite_start]A process is only loaded into the ready queue if there's enough space for it[cite: 17, 29].

* [cite_start]**Starvation Handling**: For the Priority Scheduling algorithm, the simulator detects and resolves starvation using an **aging** technique[cite: 20].

* [cite_start]**System Call Simulation**: The program proposes and simulates a set of system calls for process control, memory management, and information maintenance[cite: 19].

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

[cite_start]`Process_ID:Burst_Time_ms:Priority;Memory_Required_MB` [cite: 22]

**Example `job.txt`:**
```
1:25:4;500
2:13:3;700
3:20:3;100
```
[cite_start][cite: 23, 24, 25]

## 📊 Expected Output

The program will:
1.  [cite_start]Display a step-by-step execution trace for the selected algorithm, preferably as a **Gantt chart**[cite: 34, 35].
2.  Show the start and end times for each process.
3.  [cite_start]For Priority scheduling, it will indicate if any process suffered from starvation[cite: 34].
4.  [cite_start]Finally, it will print a comparison of the **average waiting time** and **average turnaround time** for all jobs[cite: 33].
