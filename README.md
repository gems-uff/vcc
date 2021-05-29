# vcc
Vertical Code Completion

First of all, it's necessary to import the source code as an Eclipse Plugin. After that, a menu will be added to Eclipse menu bar called 'Vertical Code Completion'. This menu contains three actions: "Generate Tree",  "Search for Patterns" and "Run Experiment".

The next step is to setup the PLWAP, the data mining software we use to identify source code patterns. We need to generate an EXE from its source file PLWAP/plwap.cpp (if you want to analyse the control structures influence over the patterns, you must use PLWAP/plwap-estruturas-controle.cpp file). The generated EXE file will be called by the action "Generate Tree". This action is responsible for analyzing the code and generate the source code pattern tree.

In order to query the pattern tree, the user should open a Java file, using Eclipse editor, place the mouse cursor after a line of code, inside a method body, and call the action "Search for Patterns". If any pattern is detected by VCC, they will be shown in a popup window.

The last action available is "Run Experiment". This action will call the pattern tree multiple times, validating the commits of a Git repository.

# Experiments

## Experiments infrastructure

The source code, developed to run the experiments that aim to answer the paper Research Questions, is available at [exp/src](https://github.com/gems-uff/vcc/tree/master/exp/src/br/uff/vcc/exp). The main class is [br.uff.vcc.exp.handler.RunExperimentHandler.java](https://github.com/gems-uff/vcc/blob/master/exp/src/br/uff/vcc/exp/handler/RunExperimentHandler.java) and in order to run the experiments again, it is required to clone the project repositories to a local computer (the repository path is hard-coded in the main class and can be easily adjusted).

In order to evaluate the Research Questions (RQ), we devised a 2-steps approach, similar to the way VCC works:
1. Pattern tree generation (mining): using an old commit of the evaluated repository.
2. Patterns evaluation: using commits that followed the commit used for pattern tree generation.

**Pattern trees location**
All the pattern trees are available at [exp/results/pattern trees](https://github.com/gems-uff/vcc/tree/master/exp/results/pattern%20trees). Each evaluated repository contains 4 different pattern trees and they are used according to the RQ experiment (more details on each RQ section below).

**Results data**

All the gross data is available in .csv files generated in [exp/results/reports](https://github.com/gems-uff/vcc/tree/master/exp/results/reports). Which folder is relevant for which RQ is detailed in the following sections of this README. 

The .txt files were used only for manual validation of the calculated metrics. They provide fine grain details of each evaluated method body: 
* Which method calls were evaluated in each method body; 
* Which ones would have been suggested;
* Which ones would have been accepted/rejected.

## Experiment reports

### RQ1

In this experiment we evaluated 
