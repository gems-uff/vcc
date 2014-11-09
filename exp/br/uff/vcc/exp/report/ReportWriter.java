package br.uff.vcc.exp.report;

import java.util.List;

import br.uff.vcc.exp.entity.EvaluatedMethod;
import br.uff.vcc.exp.git.CommitNode;

public interface ReportWriter {

	public void printFullReport(List<EvaluatedMethod> evaluatedMethods);
	
	public void printAutomatizationPercAndCorrectnessReport(List<EvaluatedMethod> evaluatedMethods, int commitIndex);
	
	public void printTotalsPeriodicReport(Integer validCommitIndex, CommitNode commit, Integer evaluatedWindowSize, Integer realCommitIndex, Integer validMethodsCount);
	
}
