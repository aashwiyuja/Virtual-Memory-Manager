# Virtual Memory Manager
Virtual Memory Manager
This is a Virtual Memory Managed written in Java that is used to demonstrate page replacement using the LRU and FIFO method.
It implements the common components of memory managers, like a TLB, Page Table, dirty bits, and address translation.
The Physical Memory is represented by the Backing Store binary file.
The address files are lists of addresses to be read and written.

The code is compiled by typing "javac *.java".
The main function is in class "VirtualMemoryManager". 
The application is run by typing in "java VirtualMemoryManager <address_file>" where <address_file> is addresses.txt or addresses2.txt
The user can use custom address files if necessary.
The correct output is shown in correct.txt for verification purposes.
Read/Write shows 2 additional columns - for R/W and for Writeback indication.

The menu presents 5 options
1. Mem size = 258
2. Mem size = 128
3. Read/Write
4. FIFO Physical frames =128
5. LRU Physical frames =128
