JGroupsAnalyser
===============
	by Francois Billard <francois@alyseo.com>
	
	JGroupsAnalyser allow you to get and display JGroups disassembled packets 


Features
========

- Scan network interface for JGroups packets like wireshark (with IP defragmentation)
- Load and decode pcap file
- Display payload message (binary to string, uncompress)
- Load externals jars to unserialize object found in the payload and display .toString() method
- Save decoded content as CSV file

Prerequisites
=============

- Java jre 1.5+
- pcap library, version 0.9 if possible, it works with v0.8 and v0.9 symbolic link
- libjnetpcap.so version 1.3, available on http://jnetpcap.com/download 

Usage
=====

Just launch binary (in target directory) :
- on Linux : <JGroupsAnalyser dir>/linux.gtk.x86/JGroupsAnalyser/JgroupsAnalyser
- on Windows (a setup is coming soon): <JGroupsAnalyser dir>/win32.win32.x86/JGroupsAnalyser\JGroupsAnalyser
	
Compatibility
=============

- JGroups 2.10
- JGroups 2.11
- JGroups 2.12

JGroupsAnalyser will work with all next JGroups versions as long as the parse class will be present in the JGroups distrib. (Thank you Bela to keep it :)

TODO
====

- JGroups FRAG2 protocol defragmentation
- filter and sorting on column (workaround today: export to CSV and sort/filter with programm like Excel)
- find why Linux version is slower than Windows version (a pcap load is five time faster)


