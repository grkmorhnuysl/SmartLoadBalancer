(1) Load Balancing Strategies you have implemented
We implemented two load balancing strategies:

Dynamic Load Balancing: Each server sends its status (IDLE or BUSY) to the Load Balancer via UDP at regular intervals. The Load Balancer checks these statuses and routes the client to the first available IDLE server.

Static Load Balancing: If all servers are marked as BUSY, the Load Balancer uses a round-robin algorithm to distribute the incoming requests fairly among all servers.

(2) High Level Approach
Protocols used:

UDP: For communication between servers and the Load Balancer (status reporting), and between clients and the Load Balancer (request for routing).

TCP: For the actual data transfer between clients and servers.

Mechanisms implemented in application layer:

Multi-threaded server request handling

Periodic server status reporting to Load Balancer

Intelligent routing based on real-time load

Four request types handled at the server:

COMPUTE:x → simulates CPU usage for x seconds

VIDEO:x → simulates streaming output for x seconds

FILE:filename.txt → simulates file transfer

LIST → returns all file names in the server directory

Key properties and features:

Dynamic load awareness

Fair fallback strategy using round robin

Real-time routing decisions

Extensible request handling structure

Works on Java 8 and above

(3) Challenges you have faced
Ensuring proper thread safety while allowing multiple clients to connect simultaneously

Designing a robust UDP-based status reporting system with minimal latency

Managing multiple Java entry points within the same NetBeans project

Creating a hybrid load balancer logic that can fall back to static strategy when dynamic selection fails

(4) Testing
Testing was performed with multiple server and client instances.

Servers were started on ports 9001, 9002, and 9003

The Load Balancer was started on port 8888 for TCP and 9999 for UDP

Clients sent various types of requests (COMPUTE, VIDEO, FILE, LIST)

Behavior was observed to ensure:

Clients were routed to available servers correctly

Load Balancer used round robin when all servers were busy

Responses were sent back accurately and without delay

(5) How to run project
Compile the source files in your IDE or using the terminal:
javac SmartServer.java SmartLoadBalancer.java SmartClient.java

Start one or more SmartServer instances:
java SmartServer

Start the SmartLoadBalancer:
java SmartLoadBalancer

Start a SmartClient and enter a command when prompted, such as:
COMPUTE:5
VIDEO:3
LIST
FILE:test.txt

Servers will process the request and respond according to the command.
