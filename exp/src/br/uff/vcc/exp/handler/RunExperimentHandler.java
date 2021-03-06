package br.uff.vcc.exp.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import br.uff.vcc.exp.git.RepositoryEvaluation;
import br.uff.vcc.exp.report.ReportWriter;
import br.uff.vcc.exp.report.TxtReportWriter;

public class RunExperimentHandler extends AbstractHandler {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private void junit() throws ParseException {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\junit\\";
		String headCommit = "95f6c4158812f03705f0f8088fa81ae791351cfe";
		Date headCommitDate = sdf.parse("2011-08-11 23:42:58");
		String unitName = "/junit/src/main/java/junit/extensions/ActiveTestSuite.java";
		String eclipseProjectName = "junit";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Float periodicReportInterval = 5F;
		
		List<Integer> fixedEvaluatedCommitIndexes = null;
		
		Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		/*for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de quantidade de sugestoes", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		periodicReportInterval = 9.8F;
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado e limitado por metodos", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, null, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void springSecurity() throws ParseException {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\spring-security\\";
		String headCommit = "563dabda2fc27d589c0ccf04b7de2ad8e8680ec7";
		Date headCommitDate = sdf.parse("2008-03-21 20:47:09");
		String unitName = "/spring-security/core/src/main/java/org/springframework/security/util/AntUrlPathMatcher.java";
		String eclipseProjectName = "spring-security";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Float periodicReportInterval = 5F;
		
		List<Integer> fixedEvaluatedCommitIndexes = null;
		
		/*Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de quantidade de sugestoes", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		periodicReportInterval = 10.5F;
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado e limitado por metodos", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void commonsIo() throws ParseException {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Desenvolvimento\\repositorios\\commons-io\\";
		String headCommit = "4aac0d007611e68ba1e31fe7b2bd69de9c537868";
		Date headCommitDate = sdf.parse("2008-05-30 16:12:01");
		String unitName = "/commons-io/src/java/org/apache/commons/io/CopyUtils.java";
		String eclipseProjectName = "commons-io";
		String innerProjectName = "";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Float periodicReportInterval = 5F;
		
		List<Integer> fixedEvaluatedCommitIndexes = null;

		Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		/*for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de quantidade de sugestoes", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		periodicReportInterval = 10.5F;
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado e limitado por metodos", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void rxjava() throws ParseException {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Users\\Luiz\\git\\RxJava\\";
		String headCommit = "68d7600c39f75982ae9074c8deff7f3779ea2e58";
		Date headCommitDate = sdf.parse("2014-01-14 01:50:59");
		String unitName = "/rxjava-core/src/main/java/rx/Notification.java";
		String eclipseProjectName = "rxjava-core";
		String innerProjectName = "rxjava-core/src/main";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Float periodicReportInterval = 5F;
		
		List<Integer> fixedEvaluatedCommitIndexes = null;
		
		/*Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de quantidade de sugestoes", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		periodicReportInterval = 10.5F;
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado e limitado por metodos", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		/*for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de confian�a com 20 sugest�es", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, 20, confidenceThreshold, fixedEvaluatedCommitIndexes);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de quantidade de sugestoes com confian�a 0.2",  amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, .2D, null);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		System.out.println(System.currentTimeMillis() - timeIni);
	}
	
	private void guava() throws ParseException {

		Long timeIni = System.currentTimeMillis();
		String repositoryPath = "C:\\Users\\Luiz\\git\\guava\\";
		String headCommit = "8e98c0f9af588a9a6458a68c2d9ddba5452d4f64";
		Date headCommitDate = sdf.parse("2012-10-23 21:25:51");
		String unitName = "/guava/src/com/google/common/annotations/Beta.java";
		String eclipseProjectName = "guava";
		String innerProjectName = "guava/;guava-gwt/";
		Boolean evaluateOnlyNewMethods = Boolean.TRUE;
		Float periodicReportInterval = 5F;
		
		List<Integer> fixedEvaluatedCommitIndexes = null;
		
		/*Double[] confidenceThresholds = {0D, .1D, .2D, .3D, .4D, .5D, .6D, .7D, .8D, .9D, 1D};
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite de quantidade de sugestoes", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		periodicReportInterval = 10.5F;
		for(int i = 0; i < confidenceThresholds.length; i++){
			Double confidenceThreshold = confidenceThresholds[i];
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite confian�a - scatter", confidenceThreshold + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, -1, confidenceThreshold, fixedEvaluatedCommitIndexes);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				fixedEvaluatedCommitIndexes = r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			try {
				r.evaluateCommits();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		
		for(int i = 1; i <= 20; i++){
			Integer amountSuggestionsProvidedPerQuery = i;
			
			ReportWriter writer = new TxtReportWriter(eclipseProjectName + "\\limite quantidade - agrupado e limitado por metodos", amountSuggestionsProvidedPerQuery + "_");
		
			RepositoryEvaluation r= new RepositoryEvaluation(repositoryPath, headCommit, headCommitDate, innerProjectName, unitName, eclipseProjectName, writer, evaluateOnlyNewMethods, periodicReportInterval, amountSuggestionsProvidedPerQuery, null, null);
			r.setSingleReportInterval(Boolean.TRUE);
			r.setEvaluatedMethodsLimit(728);
			try {
				r.evaluateCommitsDelimitedByValidMethods();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println(System.currentTimeMillis() - timeIni);
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		try {
			//guava();
			//springSecurity();
			//junit();
			commonsIo();
			//rxjava();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}
}