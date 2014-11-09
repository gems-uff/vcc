package br.uff.vcc.exp.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import br.uff.vcc.exp.git.RepositoryEvaluation;
import br.uff.vcc.exp.report.ReportWriter;
import br.uff.vcc.exp.report.TxtReportWriter;

public class RunExperimentHandler extends AbstractHandler {

	private void sl4j() {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Users\\Luiz\\git\\commons-codec\\";
		String headCommit = "c84250ad41a943c2d998f0f774d6044a4a1e1b45";
		String unitName = "/commons-codec/src/java/org/slf4j/helpers/BasicMarker.java";
		String eclipseProjectName = "slf4j-api";
		String innerProjectName = "slf4j-api";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Integer periodicReportInterval = 5;
		
		//Integer[] amountSuggestionsProvidedPerQueryArray = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void junit() {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\junit\\";
		String headCommit = "95f6c4158812f03705f0f8088fa81ae791351cfe";
		String unitName = "/junit/src/main/java/junit/extensions/ActiveTestSuite.java";
		String eclipseProjectName = "junit";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Integer periodicReportInterval = 5;
		
		Integer[] fixedCommitEvaluationWindow = {321,375,407,422,441,460,514,543,633,659,680,694,711};
		
		//Integer[] amountSuggestionsProvidedPerQueryArray = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
		
		Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedCommitEvaluationWindow);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/*for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
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
		Integer periodicReportInterval = 5;
		
		//Integer[] amountSuggestionsProvidedPerQueryArray = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
		
		Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/*for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void commonsIo() {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\commons-io\\";
		String headCommit = "4aac0d007611e68ba1e31fe7b2bd69de9c537868";
		String unitName = "/commons-io/src/java/org/apache/commons/io/CopyUtils.java";
		String eclipseProjectName = "commons-io";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Integer periodicReportInterval = 5;
		

		Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, 1, confidenceThreshold, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Integer[] amountSuggestionsProvidedPerQueryArray = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
		
		/*for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void commonsCodec() {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Users\\Luiz\\git\\commons-codec\\";
		String headCommit = "c84250ad41a943c2d998f0f774d6044a4a1e1b45";
		String unitName = "/commons-codec/src/java/org/apache/commons/codec/BinaryDecoder.java";
		String eclipseProjectName = "commons-codec";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Integer periodicReportInterval = 5;
		

		Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, 1, confidenceThreshold, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//Integer[] amountSuggestionsProvidedPerQueryArray = {1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
		
		/*for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName, amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateRepository();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		System.out.println(System.currentTimeMillis() - timeIni);
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		//sl4j();
		//springSecurity();
		junit();
		//commonsIo();
		//commonsCodec();
		
		return null;

	}
}