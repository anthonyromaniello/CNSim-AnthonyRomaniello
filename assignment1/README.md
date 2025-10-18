# Gas Station Simulation

A discrete event simulation of a gas station with multiple pumps, customer arrivals, and queue management.

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Quick Start

### Option 1: Using Maven (Recommended and most effective)

1. **Compile the project:**
   ```bash
   mvn compile
   ```

2. **Run with sample input attached to the project:**
   
   **Windows PowerShell (or terminal in IDE):**
   ```powershell
   Get-Content input.txt | mvn exec:java
   ```

3. **Run with custom input (example interval and numbers from tutorial):**
   ```bash
   mvn exec:java
   ```
   Then enter the following values when prompted:
   ```
   20000     # Report interval (seconds)
   200000    # Simulation end time (seconds)
   3         # Number of pumps
   1         # Random seed for arrivals
   2         # Random seed for fuel amounts
   3         # Random seed for balking decisions
   4         # Random seed for service times
   ```

### Option 2: Using Java directly

1. **Compile:**
   ```bash
   mvn compile
   ```

2. **Run:**
   
   **Windows PowerShell:**
   ```powershell
   Get-Content input.txt | java -cp target/classes assignment1.Sim
   ```
   
   **Windows Command Prompt (cmd):**
   ```cmd
   java -cp target/classes assignment1.Sim < input.txt
   ```
   
   **Mac/Linux/Git Bash:**
   ```bash
   java -cp target/classes assignment1.Sim < input.txt
   ```

## Input Format

The program expects 7 input values (one per line):

1. **Report Interval** (double): How often to print statistics (in seconds)
2. **Ending Time** (double): When to stop the simulation (in seconds)  
3. **Number of Pumps** (int): How many gas pumps are available
4. **Arrival Stream Seed** (int): Random seed for customer arrival times
5. **Litre Stream Seed** (int): Random seed for fuel amount needed
6. **Balking Stream Seed** (int): Random seed for customer balking decisions
7. **Service Stream Seed** (int): Random seed for service times

## Sample Input File

The included `input.txt` file contains:
```
10.0
100.0
3
12345
23456
34567
45678
```

## Output

The simulation outputs a table showing:
- **Current Time**: Simulation time in seconds
- **Total Cars**: Number of cars that have arrived
- **NoQueue Fraction**: Proportion of time the queue was empty
- **Car->Car Time**: Average time between car arrivals
- **Average Litres**: Average fuel needed per car
- **Number Balked**: Number of cars that left without service
- **Average Wait**: Average waiting time for served customers
- **Pump Usage**: Pump utilization rate
- **Total Profit**: Current profit/loss from operations
- **Lost Profit**: Profit lost from customers who balked

## Project Structure

```
assignment1/
├── pom.xml                 # Maven configuration
├── README.md              # This file
├── input.txt              # Sample input data
└── src/
    └── main/
        └── java/
            └── assignment1/
                ├── Main.java      # Simple Hello World
                └── Sim.java       # Gas station simulation
```

## Simulation Parameters

The simulation uses these built-in parameters:
- **Profit per litre**: $0.025
- **Pump operating cost**: $20 per day
- **Fuel demand**: 10-60 litres per car (uniform distribution)
- **Service time**: Base 150s + 0.5s per litre + random variation
- **Mean arrival time**: 50 seconds between customers
- **Balking probability**: Increases with queue length, decreases with fuel needed

## Troubleshooting

- **"Invalid target release: 17"**: Make sure you have Java 11+ installed
- **"Class not found"**: Run `mvn compile` first
- **Input format errors**: Ensure input values are on separate lines and properly formatted
- **PowerShell `<` operator error**: Use `Get-Content input.txt | java -cp target/classes assignment1.Sim` instead
- **"RedirectionNotSupported"**: You're in PowerShell - use the `Get-Content` command shown above