package br.uff.vcc.exp.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import br.uff.vcc.exp.entity.AddedMethod;
import br.uff.vcc.exp.entity.EvaluatedMethod;
import br.uff.vcc.util.Suggestion;

public class TxtReportWriter implements ReportWriter {

	protected static final String reportPath = "C:\\VCC\\relatorios\\";
	protected String reportNamePrefix;
	
	protected String eclipseProjectName;
	
	protected Integer suggestionsProvided = 0;
	protected Integer suggestionsAccepted = 0;
	protected Integer totalEvaluatedMethods = 0;
	
	public TxtReportWriter(String eclipseProjectName, String reportNamePrefix) {
		this.eclipseProjectName = eclipseProjectName;
		this.reportNamePrefix = reportNamePrefix;
		
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Completo.txt").delete();
		new File(reportPath + eclipseProjectName + "\\"  + reportNamePrefix + "Medio.txt").delete();
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Resumido.txt").delete();
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Sintetico.txt").delete();
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Periodico.txt").delete();
	}

	@Override
	public void printFullReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Completo.txt"), Boolean.TRUE);
		
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				printMethodReport(evaluatedMethod, writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getOldMethodCalls(), "antigas", writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls(), "novas", writer);
				printAddedMethodCalls(evaluatedMethod, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void printMediumReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\"  + reportNamePrefix + "Medio.txt"), Boolean.TRUE);
		
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				printMethodReport(evaluatedMethod, writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getOldMethodCalls(), "antigas", writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls(), "novas", writer);
				printResumedAddedMethodCalls(evaluatedMethod, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void printShortReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Resumido.txt"), Boolean.TRUE);
		
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				printMethodReport(evaluatedMethod, writer);
				printResumedAddedMethodCalls(evaluatedMethod, writer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void printSuperShortReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Sintetico.txt"), Boolean.TRUE);
			
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				totalEvaluatedMethods++;
				printMethodReport(evaluatedMethod, writer);
				if(evaluatedMethod.isSuggestionsProvided()){
					printResumedAddedMethodCalls(evaluatedMethod, writer);
				}
				if(evaluatedMethod.isSuggestionAccepted()){
					suggestionsAccepted++;
					suggestionsProvided++;
				}
				else if(evaluatedMethod.isSuggestionsProvided()){
					suggestionsProvided++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	protected void printResumedAddedMethodCalls(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
		writer.write("----------------Chamadas de método adicionadas ---------------\n");
		for (AddedMethod addedMethod: evaluatedMethod.getAddedMethods()) {
			writer.write("Chamada: " + addedMethod.getAddedMethodName() + "\n");
			writer.write("Alguma Sugestão Fornecida: " + (addedMethod.getSuggestionsProvided().size() > 0 ? "Sim" : "Não") + "\n");
			if(addedMethod.getAcceptedSugestionPosition() == -1){
				writer.write("Nenhuma sugestão aprovada.\n");
				continue;
			}
			writer.write("SUGESTÃO APROVADA!\n");
			Suggestion suggestion = addedMethod.getSuggestionsProvided().get(addedMethod.getAcceptedSugestionPosition());
			printSuggestion(addedMethod, suggestion, writer);
		}
		
	}

	
	protected void printSuggestion(AddedMethod addedMethod, Suggestion suggestion, FileWriter writer) throws IOException {
		writer.write("Usuários que chamam:\n");
		for (String query : suggestion.getInvocatedMethods()) {
			writer.write(query + "\n");
		}
		writer.write("Também chamam:\n");
		for (String output : suggestion.getSuggestedMethods()) {
			writer.write(output + "\n");
		}
		
		writer.write("Confiança: " + suggestion.getConfidence() + "\n");
		writer.write("Suporte: " + suggestion.getSupport() + "\n");
		writer.write("Posição da Sugestão: " + (addedMethod.getAcceptedSugestionPosition()+1) + "\n");
		writer.write("**************************\n");
	}

	protected void printAddedMethodCalls(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
		writer.write("---------------- Chamadas de método adicionadas ---------------\n");
		for (AddedMethod addedMethod: evaluatedMethod.getAddedMethods()) {
			writer.write("Chamada: " + addedMethod.getAddedMethodName() + "\n");
			if(addedMethod.getSuggestionsProvided().size() == 0){
				writer.write("Nenhuma sugestão fornecida.\n");
				writer.write("**************************\n");
				continue;
			}
			writer.write("Sugestão Aprovada: " + (addedMethod.getAcceptedSugestionPosition() != -1 ? "Sim" : "Não") + "\n");
			writer.write("** Sugestões fornecidas **\n");
			for (int i = 0; i < addedMethod.getSuggestionsProvided().size(); i++) {
				Suggestion suggestion = addedMethod.getSuggestionsProvided().get(i);
				printSuggestion(addedMethod, suggestion, writer);
				if(addedMethod.getAcceptedSugestionPosition() == i ||
						((addedMethod.getAcceptedSugestionPosition() == -1) && i == 9)){
					break;
				}
			}
		}
	}

	protected void printMethodCalls(List<String> methodCalls, String type, FileWriter writer) throws IOException {
		writer.write("----------------Chamadas de método " + type + ":---------------\n");
		if(methodCalls == null){
			return;
		}
		for (String string : methodCalls) {
			writer.write(string + "\n");
		}
	}

	protected void printMethodReport(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
		writer.write("************************ Método Avaliado: " +  evaluatedMethod.getMethodName() + " ************************\n");
		writer.write("*Commit: " + evaluatedMethod.getCommitId() + "\n");
		writer.write("*Alguma Sugestão Fornecida: " + (evaluatedMethod.isSuggestionsProvided() ? "Sim" : "Não") + "\n");
		writer.write("*Alguma Sugestão Aprovada: " + (evaluatedMethod.isSuggestionAccepted() ? "Sim" : "Não") + "\n");
		writer.write("**************************************************************************************************************\n");
	}

	public void printTotalsPeriodicReport(Integer commitPosition, String commitId) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Periodico.txt"), Boolean.TRUE);
			writer.write("**************************************************************************************************************\n");
			if(commitPosition != null){
				writer.write("TOTAIS APÓS " + commitPosition + " COMMITS (Commit ID: " + commitId + "):\n");
			}else{
				writer.write("RESULTADO FINAL:\n");
			}
			writer.write("**************************************************************************************************************\n");
			writer.write("*Métodos avaliados: " + totalEvaluatedMethods + "\n");
			writer.write("*Métodos onde alguma sugestão foi fornecida: " + suggestionsProvided + "\n");
			writer.write("*Métodos onde uma sugestão foi aceita: " + suggestionsAccepted + "\n");
			BigDecimal precision = new BigDecimal(suggestionsAccepted.doubleValue()/suggestionsProvided.doubleValue());
			writer.write("*Precision: " + precision.setScale(3, RoundingMode.CEILING) + "\n");
			BigDecimal recall = new BigDecimal(suggestionsAccepted.doubleValue()/totalEvaluatedMethods.doubleValue());
			writer.write("*Recall: " + recall.setScale(3, RoundingMode.CEILING) + "\n");
			BigDecimal harmonicMean = new BigDecimal(2D/ (1D/precision.doubleValue() + 1D/recall.doubleValue()));
			writer.write("*Média Harmônica(Precision e Recall): " + harmonicMean.setScale(3, RoundingMode.CEILING) + "\n");
			writer.write("**************************************************************************************************************\n");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
