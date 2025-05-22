#Smart Load Balancer

A multi-threaded client-server architecture built with Java sockets, implementing both **static** and **dynamic** load balancing algorithms. Developed for COMP3334 — Computer Networks (Spring 2025) as a socket programming project.

---

##  Features

-  Dynamic load balancing: Based on real-time server status (IDLE/BUSY).
-  Static fallback: Uses round-robin when all servers are busy.
-  Handles 4 request types:
  - `COMPUTE:x` → simulate CPU-bound task for x seconds
  - `VIDEO:x` → simulate video streaming for x seconds
  - `FILE:filename.txt` → simulate file transfer
  - `LIST` → returns directory contents

---

## 🛠️ Setup

### 1. Requirements

- Java JDK 8 or higher
- NetBeans or any IDE
- (Optional) Terminal for multi-instance testing

### 2. Compile

You can compile manually:

```bash
javac src/*.java
