package br.uff.vcc.exp.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import br.uff.vcc.exp.git.RepositoryEvaluation;
import br.uff.vcc.exp.report.ReportWriter;
import br.uff.vcc.exp.report.TxtPercentageOfRecommendationReportWriter;

public class RunExperimentHandler extends AbstractHandler {

	private void sl4j() {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\slf4j\\";
		String headCommit = "0e70e23f8301223b677dbf3f4720c0f22969ac4a";
		String unitName = "/slf4j-api/src/main/java/org/slf4j/helpers/BasicMarker.java";
		String eclipseProjectName = "slf4j-api";
		String innerProjectName = "slf4j-api";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Integer periodicReportInterval = 50;
		Integer amountSuggestionsProvidedPerQuery = 20;
		
		ReportWriter writer = new TxtPercentageOfRecommendationReportWriter(eclipseProjectName, "relatorioPercRecomendacao");
		
		RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery);
		try {
			r.evaluateRepository();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void springSecurity() {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\spring-security\\";
		String headCommit = "563dabda2fc27d589c0ccf04b7de2ad8e8680ec7";
		String unitName = "/spring-security/core/src/main/java/org/springframework/security/util/AntUrlPathMatcher.java";
		String eclipseProjectName = "spring-security";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Integer periodicReportInterval = 50;
		Integer amountSuggestionsProvidedPerQuery = 10;
		
		ReportWriter writer = new TxtPercentageOfRecommendationReportWriter(eclipseProjectName, "relatorioPercRecomendacao");
		
		RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery);
		try {
			r.evaluateRepository();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - timeIni);
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		//sl4j();
		springSecurity();

		return null;

	}
}