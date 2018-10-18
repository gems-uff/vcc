# vcc
Vertical Code Completion

First of all, it's necessary to import the source code as an Eclipse Plugin. After that, a menu will be added to Eclipse menu bar called 'Vertical Code Completion'. This menu contains three actions: "Generate Tree",  "Search for Patterns" and "Run Experiment".

The next step is to setup the PLWAP, the data mining software we use to identify source code patterns. We need to generate an EXE from its source file PLWAP/plwap.cpp (if you want to analyse the control structures influence over the patterns, you must use PLWAP/plwap-estruturas-controle.cpp file). The generated EXE file will be called by the action "Generate Tree". This action is responsible for analyzing the code and generate the source code pattern tree.

In order to query the pattern tree, the user should open a Java file, using Eclipse editor, place the mouse cursor after a line of code, inside a method body, and call the action "Search for Patterns". If any pattern is detected by VCC, they will be shown in a popup window.

The last action available is "Run Experiment". This action will call the pattern tree multiple times, validating the commits of a Git repository.


