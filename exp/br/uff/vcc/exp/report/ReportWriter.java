package br.uff.vcc.exp.report;

import java.util.List;

import br.uff.vcc.exp.entity.EvaluatedMethod;

public interface ReportWriter {

	public void printFullReport(List<EvaluatedMethod> evaluatedMethods);
	
	public void printAutomatizationPercAndCorrectnessReport(List<EvaluatedMethod> evaluatedMethods, int commitIndex);
	
	public void printTotalsPeriodicReport(Integer validCommitIndex, String commitId, Integer evaluatedWindowSize, Integer realCommitIndex);
	
}
