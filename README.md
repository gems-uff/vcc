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
All the pattern trees are available at [exp/results/pattern trees](https://github.com/gems-uff/vcc/tree/master/exp/results/pattern%20trees). Each evaluated repository contains 4 different pattern trees and they are used according to the RQ experiment (more details on each RQ section below). For the paper **Sequential coding patterns: How to use them effectively in code recommendation** paper, we only used `Ignoring Control Structures` folder. The `25` and `75` folders were used in a RQ4 and the `Considering Control Structures` in a RQ5, that are not available in this paper.

**Results data**

All the gross data is available in .csv files generated in [exp/results/reports](https://github.com/gems-uff/vcc/tree/master/exp/results/reports). Which folder is relevant for which RQ is detailed in the following sections of this README. 

The .txt files were used only for manual validation of the calculated metrics. They provide fine grain details of each evaluated method body: 
* Which method calls were evaluated in each method body; 
* Which ones would have been suggested;
* Which ones would have been accepted/rejected.

## Experiment reports

### RQ1

In this experiment we evaluated **how many frequent coding patterns should be presented in a code recommendation.**

The files used for this RQ evaluation are available at:
* (exp/results/reports/commons-io/limite quantidade-agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/commons-io/limite%20quantidade%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/guava/limite quantidade-agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/guava/limite%20quantidade%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/junit/limite quantidade-agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/junit/limite%20quantidade%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/rxjava-core/limite quantidade-agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/rxjava-core/limite%20quantidade%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/spring-security/limite quantidade-agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/spring-security/limite%20quantidade%20-%20agrupado%20e%20limitado%20por%20metodos]

#### Individual execution files

Each file, starting with a number between 1 and 20, contains gross data for each evaluated amount of suggested methods. The .txt files are the fine grain details, as explained above, while the X_Grafico.csv, 1<=X<=20, ones contain the calculated metrics: 
* Automatization Percentage
* Correctness
* F-Measure

We calculated both global and per method body metrics. For the correctness for instance, global values are calculated through: 
`globalCorrectness = usefulSuggestionsCount / uselessSuggestionsCount + usefulSuggestionsCount;` as can be seen in: [global correctness calculation source code].(https://github.com/gems-uff/vcc/blob/bef9b6313cbbc44b2f136f3101a71070ac65d4b8/exp/src/br/uff/vcc/exp/report/TxtReportWriter.java#L493) 

Per body method metrics were calculated through:
`correctness = calculateMean(eachMethodBodyCorrectnessValues);` as can be seen in: [per method body correctness calculation source code](https://github.com/gems-uff/vcc/blob/bef9b6313cbbc44b2f136f3101a71070ac65d4b8/exp/src/br/uff/vcc/exp/report/TxtReportWriter.java#L204).

#### Final metrics files

Grouped data of all the 20 different executions are available in the following .csv files:
* automatizationPerc.csv
* correctness.csv
* fMeasure.csv
* globalAutomatizationPerc.csv
* globalCorrectness.csv
* globalFfMeasure.csv

For the RQs we decided to use always global values, since they are less prone to validity threats (small methods with too big or too small metric values having great impact on the results).

#### Reports

The reports are available in the .XLSX files of each metric.

### RQ2

In this experiment we evaluated **what is the impact of filtering suggestions by their confidence insteadof only ranking them.**

The files used for this RQ evaluation are available at:
* (exp/results/reports/commons-io/limite confiança - scatter - agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/commons-io/limite%20confiança%20-%20scatter%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/guava/limite confiança - scatter - agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/guava/limite%20confiança%20-%20scatter%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/junit/limite confiança - scatter - agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/junit/limite%20confiança%20-%20scatter%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/rxjava-core/limite confiança - scatter - agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/rxjava-core/limite%20confiança%20-%20scatter%20-%20agrupado%20e%20limitado%20por%20metodos]
* (exp/results/reports/spring-security/limite confiança - scatter - agrupado e limitado por metodos)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/spring-security/limite%20confiança%20-%20scatter%20-%20agrupado%20e%20limitado%20por%20metodos]

#### Individual execution files

The structure is pretty similar to the one of the previous RQ. This time, the individual execution files start with values between 0.0 and 1.0, with 0.1 increments, representing the minimum confidence values varying between 0% and 100%.

#### Final metrics files and reports

Following the same structure of the previous RQ. Metrics are available on .csv files and reports in .xlsx files.

### RQ3

In this experiment we evaluated if **the  effectiveness  of  the  sequential  coding  patterns degrade over time**.

The files used for this RQ evaluation are available at:
* (exp/results/reports/commons-io/analise temporal)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/commons-io/analise%20temporal]
* (exp/results/reports/guava/analise temporal)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/guava/analise%20temporal]
* (exp/results/reports/junit/analise temporal)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/junit/analise%20temporal]
* (exp/results/reports/rxjava-core/analise temporal)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/rxjava-core/analise%20temporal]
* (exp/results/reports/spring-security/analise temporal)[https://github.com/gems-uff/vcc/tree/master/exp/results/reports/spring-security/analise%20temporal]

#### Individual execution files

In this case we evaluated the patterns performance over time, using always a limit of five suggestions. That is why there are only individual execution files with the name starting with `5_`. Inside 5_Grafico.csv[https://github.com/gems-uff/vcc/blob/master/exp/results/reports/commons-io/analise%20temporal/5_Grafico.csv] for instance, all the calculated metrics for each evaluated commit window can be seen. 


#### Final metrics files and reports

Following the same structure of the previous RQ. Metrics are available on .csv files and reports in .xlsx files.
