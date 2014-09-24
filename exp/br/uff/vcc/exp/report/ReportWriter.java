package br.uff.vcc.exp.report;

import java.util.List;

import br.uff.vcc.exp.entity.EvaluatedMethod;

public interface ReportWriter {

	public void printFullReport(List<EvaluatedMethod> evaluatedMethods);
	
	public void printPercRecommendationReport(List<EvaluatedMethod> evaluatedMethods);
	
	public void printTotalsPeriodicReport(Integer commitPosition, String commitId);
	
}
