Readme.txt                                    asivak01@syr.edu
----------------------------------------------------------------


1. VM_usage.txt file stores the output of CPU(MHz) and memory(MHz) usage of virtual machine for every 15 seconds.

Command Line:
128.230.247.52 AD\asivak01 4Pk-Lt74 1

2. To run requirement 2 please change command line to below format and rerun the program.
The server_usage.txt file stores the output of CPU(MHz) and memory(MHz) usage of the server for every 15 seconds.

Command Line:
128.230.247.52 AD\asivak01 4Pk-Lt74 2

3. The VM migrates when the CPU or memory utilization exceeds the threshold defined in History.java.
The current threshold for CPU = 1000, threshold for memory = 40 defined in History.java.
The migration happens after 3 minutes when the requirement 2 is executed. To perform another migraiton please 
stop the program and edit the sourceHost, targetHost and targetPool in VMotion.java.