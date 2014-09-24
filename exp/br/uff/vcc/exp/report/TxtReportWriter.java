package br.uff.vcc.exp.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import br.uff.vcc.exp.entity.AddedMethod;
import br.uff.vcc.exp.entity.EvaluatedMethod;
import br.uff.vcc.util.Suggestion;

public class TxtReportWriter implements ReportWriter {

	private static final String METHOD_CALL_STATUS_MANUAL = "Manual";
	private static final String METHOD_CALL_STATUS_FIRST = "Primeira Chamada";
	private static final String METHOD_CALL_STATUS_AUTOMATIC = "Automatizada";

	protected static final String reportPath = "C:\\VCC\\relatorios\\";
	protected String reportNamePrefix;
	protected String eclipseProjectName;
	
	private Integer suggestionsProvided = 0;
	private Integer suggestionsAccepted = 0;
	private Integer totalEvaluatedMethods = 0;
	
	//Percentual de automatização
	private Integer globalManualCodedMethods = 0;
	private Integer globalAutomaticCodedMethods = 0;
	private Integer totalCodedMethods = 0;
	private List<Integer> acceptedSuggestionIndexes;
	
	//Precision e Recall
	private List<Double> automatizationPercValues;
	private List<Double> correctnessValues;
	private List<Double> fMeasureValues;
	
	private Integer periodicReportInterval;
	
	public TxtReportWriter(String eclipseProjectName, String reportNamePrefix, Integer periodicReportInterval) {
		this.eclipseProjectName = eclipseProjectName;
		this.reportNamePrefix = reportNamePrefix;
		this.periodicReportInterval = periodicReportInterval;
		
		acceptedSuggestionIndexes = new ArrayList<Integer>();
		automatizationPercValues = new ArrayList<Double>();
		correctnessValues = new ArrayList<Double>();
		fMeasureValues = new ArrayList<Double>();
		
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Completo.txt").delete();
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Periodico.txt").delete();
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "PercRecomendacao.txt").delete();
		new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Grafico.txt").delete();
	}

	/**
	 * O primeiro experimento tem por objetivo avaliar os valores de precision, recall e f-measure para a execução do experimento
	 */
	public void executarPrimeiroExperimento(List<EvaluatedMethod> evaluatedMethods){
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "PrimeiroExperimento.txt"), Boolean.TRUE);
		
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				if(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls().size() <= 1){
					continue;
				}
				writer.write("************************ Método Avaliado: " +  evaluatedMethod.getMethodName() + " ************************\n");
				writer.write("*Commit: " + evaluatedMethod.getCommitId() + "\n");
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls(), "novas", writer);
				
				
				//evaluatedMethod.getAddedMethods().get(0).
				
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
	public void printFullReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Completo.txt"), Boolean.TRUE);
		
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				totalEvaluatedMethods++;
				printMethodReport(evaluatedMethod, writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getOldMethodCalls(), "antigas", writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls(), "novas", writer);
				printAddedMethodCalls(evaluatedMethod, writer);

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
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void printPercRecommendationReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "PercRecomendacao.txt"), Boolean.TRUE);
		
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				printMethodReport(evaluatedMethod, writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getOldMethodCalls(), "antigas", writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls(), "novas", writer);
				printAddedMethodCallsPercRecommendation(evaluatedMethod, writer);
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
	public void printTotalsPeriodicReport(Integer commitPosition, String commitId) {
		if(totalEvaluatedMethods == 0){
			return;
		}
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
			BigDecimal correctness = new BigDecimal(calculateMeanPeriodic(correctnessValues)).setScale(3, RoundingMode.CEILING);
			writer.write("*Corretude Média: " + correctness + "\n");
			BigDecimal stdDevCorrectness = new BigDecimal(calculateStdDevPeriod(correctnessValues, correctness.doubleValue())).setScale(3, RoundingMode.CEILING);
			writer.write("*Desvio Padrão Corretude: " + stdDevCorrectness + "\n");
			BigDecimal automatizationPerc = new BigDecimal(calculateMeanPeriodic(automatizationPercValues)).setScale(3, RoundingMode.CEILING);
			writer.write("*Percentual de Automatização Médio: " + automatizationPerc + "\n");
			BigDecimal stdDevAutomatizationPerc = new BigDecimal(calculateStdDevPeriod(automatizationPercValues, correctness.doubleValue())).setScale(3, RoundingMode.CEILING);
			writer.write("*Desvio Padrão Percentual de Automatização: " + stdDevAutomatizationPerc + "\n");
			BigDecimal fMeasure = new BigDecimal(calculateMeanPeriodic(fMeasureValues)).setScale(3, RoundingMode.CEILING);
			writer.write("*F-Measure Médio: " + fMeasure + "\n");
			BigDecimal stdDevFMeasure = new BigDecimal(calculateStdDevPeriod(fMeasureValues, correctness.doubleValue())).setScale(3, RoundingMode.CEILING);
			writer.write("*Desvio Padrão F-Measure: " + stdDevFMeasure + "\n");
			//BigDecimal harmonicMean = new BigDecimal(2D*precision.doubleValue()*recall.doubleValue()/ (precision.doubleValue() + recall.doubleValue())).setScale(3, RoundingMode.CEILING);
			//writer.write("*Média Harmônica(Precision e Recall): " + harmonicMean + "\n");
			writer.write("**************************************************************************************************************\n");
			
			
			printTotalsPercRecommendationPeriodicReport();
			printInputGraphic(correctness, stdDevCorrectness, automatizationPerc, stdDevAutomatizationPerc, fMeasure, stdDevFMeasure);
			
			
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
	
	private void printSuggestion(AddedMethod addedMethod, Suggestion suggestion, FileWriter writer) throws IOException {
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

	private void printAddedMethodCalls(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
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
				if(addedMethod.getAcceptedSugestionPosition() == i){
					writer.write("SUGESTÃO APROVADA!\n");
				}
				Suggestion suggestion = addedMethod.getSuggestionsProvided().get(i);
				printSuggestion(addedMethod, suggestion, writer);
				if(addedMethod.getAcceptedSugestionPosition() == i ||
						((addedMethod.getAcceptedSugestionPosition() == -1) && i == 9)){
					break;
				}
			}
		}
	}

	private void printMethodCalls(List<String> methodCalls, String type, FileWriter writer) throws IOException {
		writer.write("----------------Chamadas de método " + type + ":---------------\n");
		if(methodCalls == null){
			return;
		}
		for (String string : methodCalls) {
			writer.write(string + "\n");
		}
	}

	private void printMethodReport(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
		writer.write("************************ Método Avaliado: " +  evaluatedMethod.getMethodName() + " ************************\n");
		writer.write("*Commit: " + evaluatedMethod.getCommitId() + "\n");
		writer.write("*Alguma Sugestão Fornecida: " + (evaluatedMethod.isSuggestionsProvided() ? "Sim" : "Não") + "\n");
		writer.write("*Alguma Sugestão Aprovada: " + (evaluatedMethod.isSuggestionAccepted() ? "Sim" : "Não") + "\n");
		writer.write("**************************************************************************************************************\n");
	}

	private void printAddedMethodCallsPercRecommendation(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
		/**
		 * This array stores each method call situation from evaluated Method
		 * 0 : methodCallName
		 * 1 : method situation [Manual(we couldn't foreseen it), Automático(we could foreseen it)]
		 * 2 : methodCall index from where the suggestion that foreseen this method came. ....
		 * 3 : suggestion index
		 */
		
		int usefulSuggestionCount = 0;
		int uselessSuggestionCount = 0;
		
		String[][] methodReport = new String[evaluatedMethod.getAddedMethods().size() + 1][4];
		methodReport[0][0] = evaluatedMethod.getMethodCallsDiff().getNewMethodCalls().get(0);
		methodReport[0][1] = METHOD_CALL_STATUS_FIRST;
		//writer.write("---------------- Chamadas de método adicionadas ---------------\n");
		for (int i = 0; i < evaluatedMethod.getAddedMethods().size(); i++){
			AddedMethod addedMethod = evaluatedMethod.getAddedMethods().get(i);
			methodReport[i+1][0] = addedMethod.getAddedMethodName();
			methodReport[i+1][1] = METHOD_CALL_STATUS_MANUAL;
		}
		for (int i = 0; i < evaluatedMethod.getAddedMethods().size(); i++) {
			AddedMethod addedMethod = evaluatedMethod.getAddedMethods().get(i);
			if(addedMethod.getSuggestionsProvided().size() == 0){
				continue;
			}
			
			for (int s = 0; s < addedMethod.getSuggestionsProvided().size(); s++) {
				Suggestion suggestion = addedMethod.getSuggestionsProvided().get(s);
				
				for(int k = 0; k < suggestion.getSuggestedMethods().size(); k++){
					String suggestedMethod = suggestion.getSuggestedMethods().get(k);
					boolean usefulSuggestion = false;
					
					for (int j = i+1; j < methodReport.length; j++){
						if(suggestedMethod.equals(methodReport[j][0])){
							usefulSuggestion = true;
							if(!METHOD_CALL_STATUS_AUTOMATIC.equals(methodReport[j][1])){
								methodReport[j][1] = METHOD_CALL_STATUS_AUTOMATIC;
								methodReport[j][2] = String.valueOf(i);
								methodReport[j][3] = String.valueOf(s);
							}
							break;
						}
					}
					
					if(usefulSuggestion){
						usefulSuggestionCount++;
					}else{
						uselessSuggestionCount++;
					}
				}
			}
		}
		
		int automaticCodedMethods = 0;
		int manualCodedMethods = 0;
		for (int j = 0; j < methodReport.length; j++) {
			String[] methodSituation = methodReport[j];
			writer.write("Chamada: " + methodSituation[0] + "\n");
			writer.write("Situação: " + methodSituation[1] + "\n");
			
			if(j > 0){
				totalCodedMethods++;
				if(METHOD_CALL_STATUS_AUTOMATIC.equals(methodSituation[1])){
					Integer suggestionIndex = Integer.valueOf(methodSituation[3]);
					Integer methodCallPositionSuggestionCameFromIndex = Integer.valueOf(methodSituation[2]);
					//Suggestion index starts in 0. Show it starting in 1
					writer.write("Índice da Sugestão: " + (suggestionIndex+1) + "\n");
					acceptedSuggestionIndexes.add(suggestionIndex+1);
					Suggestion suggestion = evaluatedMethod.getAddedMethods().get(methodCallPositionSuggestionCameFromIndex).getSuggestionsProvided().get(suggestionIndex);
					writer.write("Confiança: " + suggestion.getConfidence() + "\n");
					writer.write("Suporte: " + suggestion.getSupport() + "\n");
					
					BigDecimal harmonicMean = new BigDecimal(2D*suggestion.getConfidence()*suggestion.getSupport() / (suggestion.getConfidence() + suggestion.getSupport()));
					writer.write("F-Measure Suporte e Confiança: " + harmonicMean.setScale(3, RoundingMode.CEILING) + "\n");
					
					automaticCodedMethods++;
				}else{
					manualCodedMethods++;
				}
			}
		}
		
		globalAutomaticCodedMethods += automaticCodedMethods;
		globalManualCodedMethods += manualCodedMethods;
		
		Double recall = new Double(automaticCodedMethods) / new Double(methodReport.length-1);
		automatizationPercValues.add(recall);
		writer.write("Recall: " + recall + "\n");
		
		Double correctness;
		if(usefulSuggestionCount > 0 || uselessSuggestionCount > 0){
			correctness = new Double(usefulSuggestionCount) / new Double(uselessSuggestionCount + usefulSuggestionCount); 
		}else{
			correctness = 0D;
		}
		correctnessValues.add(correctness);
		writer.write("Precision: " + correctness + "\n");
		
		Double fMeasure;
		if(correctness.doubleValue() + recall.doubleValue() != 0d){
			fMeasure = (2D*correctness.doubleValue()*recall.doubleValue())/ (correctness.doubleValue() + recall.doubleValue());
		}else{
			fMeasure = 0D;
		}
		fMeasureValues.add(fMeasure);
		writer.write("F-Measure: " + fMeasure + "\n");
	}
	
	private void printTotalsPercRecommendationPeriodicReport() {
		if(totalCodedMethods == 0){
			return;
		}
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Periodico.txt"), Boolean.TRUE);
			writer.write("**************************************************************************************************************\n");
			writer.write("*Métodos codificados: " + totalCodedMethods + "\n");
			writer.write("*Métodos codificados automaticamente: " + globalAutomaticCodedMethods + "\n");
			writer.write("*Métodos codificados manualmente: " + globalManualCodedMethods + "\n");
			BigDecimal automatizationRate = new BigDecimal(globalAutomaticCodedMethods.doubleValue()/totalCodedMethods.doubleValue());
			writer.write("*Percentual de Automatização: " + automatizationRate.setScale(3, RoundingMode.CEILING)+ "\n");
			
			BigDecimal averageSugIndex = new BigDecimal(calculateMeanPeriodic(acceptedSuggestionIndexes));
			writer.write("*Posição média da sugestão: " + averageSugIndex.setScale(3, RoundingMode.CEILING).toString() + "\n");
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
	
	private void printInputGraphic(BigDecimal correctness, BigDecimal stdDevCorrectness, BigDecimal automatizationPerc, BigDecimal stdDevAutomatizationPerc, BigDecimal fMeasure, BigDecimal stdDevFMeasure) {
		FileWriter writer = null;
		try {
			printHeaderInputGraphic();
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Grafico.txt"), Boolean.TRUE);
			writer.write(globalAutomaticCodedMethods + "\t\t");
			writer.write(globalManualCodedMethods + "\t\t");
			writer.write(correctness + "\t\t");
			writer.write(stdDevCorrectness + "\t\t");
			writer.write(automatizationPerc + "\t\t");
			writer.write(stdDevAutomatizationPerc + "\t\t");
			writer.write(fMeasure + "\t\t");
			writer.write(stdDevFMeasure.toString());
			writer.write("\n");
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

	private void printHeaderInputGraphic() {
		File graphicFile = new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Grafico.txt");
		if(!graphicFile.exists()){
			FileWriter writer = null;
			try {
				printHeaderInputGraphic();
				writer = new FileWriter(graphicFile);
				writer.write("Métodos Codif. Automaticamente \t\t");
				writer.write("Métodos Codif. Manualmente \t\t");
				writer.write("Correture \t\t");
				writer.write("StdDev Corretude \t\t");
				writer.write("Perc. de Automatização \t\t");
				writer.write("StdDev Perc. de Automatização \t\t");
				writer.write("F-Measure \t\t");
				writer.write("StdDev F-Measure \t\t");
				
				writer.write("\n");
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

	private double calculateMeanPeriodic(List<? extends Number> numbersList) {
		Double sum = 0D;
		if (!numbersList.isEmpty()) {
			for (Number value : numbersList) {
				sum += value.doubleValue();
			}
			return sum.doubleValue() / numbersList.size();
		}
		return sum;
	}
	
    double calculateVariance(List<? extends Number> numbersList, double mean)
    {
        double temp = 0;
        for (Number value : numbersList) {
            temp += (mean-value.doubleValue())*(mean-value.doubleValue());
        }
        return temp/numbersList.size();
    }

    double calculateStdDevPeriod(List<? extends Number> numbersList, double mean)
    {
        return Math.sqrt(calculateVariance(numbersList, mean));
    }
}
