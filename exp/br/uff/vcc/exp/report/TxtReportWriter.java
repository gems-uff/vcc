package br.uff.vcc.exp.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.uff.vcc.exp.entity.AddedMethod;
import br.uff.vcc.exp.entity.CommitsEvaluationWindow;
import br.uff.vcc.exp.entity.EvaluatedMethod;
import br.uff.vcc.exp.git.CommitNode;
import br.uff.vcc.util.Suggestion;

public class TxtReportWriter implements ReportWriter {

	private static final String METHOD_CALL_STATUS_MANUAL = "Manual";
	private static final String METHOD_CALL_STATUS_FIRST = "Primeira Chamada";
	private static final String METHOD_CALL_STATUS_AUTOMATIC = "Automatizada";

	protected static final String reportPath = "C:\\VCC\\relatorios\\";
	protected String reportNamePrefix;
	protected String projectEvaluationFolder;
	
	private Integer suggestionsProvided = 0;
	private Integer suggestionsAccepted = 0;
	private Integer totalEvaluatedMethods = 0;
	
	//Percentual de automatização
	private List<Integer> acceptedSuggestionIndexes;
	
	//Percentual de automatização vs Corretude
	private Map<Integer, CommitsEvaluationWindow> commitsEvaluationWindows;
	
	public TxtReportWriter(String projectEvaluationFolder, String reportNamePrefix) {
		this.projectEvaluationFolder = projectEvaluationFolder;
		this.reportNamePrefix = reportNamePrefix;
		
		acceptedSuggestionIndexes = new ArrayList<Integer>();
		commitsEvaluationWindows = new HashMap<Integer, CommitsEvaluationWindow>();
		
		new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Completo.txt").delete();
		new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Periodico.txt").delete();
		new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "PercRecomendacao.txt").delete();
		new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Grafico.txt").delete();
	}

	/**
	 * O primeiro experimento tem por objetivo avaliar os valores de precision, recall e f-measure para a execução do experimento
	 */
	/*public void executarPrimeiroExperimento(List<EvaluatedMethod> evaluatedMethods){
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
	}*/
	
	@Override
	public void printFullReport(List<EvaluatedMethod> evaluatedMethods) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Completo.txt"), Boolean.TRUE);
		
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
	public void printAutomatizationPercAndCorrectnessReport(List<EvaluatedMethod> evaluatedMethods, int commitIndex) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "PercRecomendacao.txt"), Boolean.TRUE);
		
			CommitsEvaluationWindow commitsEvaluationWindow = new CommitsEvaluationWindow();
			
			for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
				printMethodReport(evaluatedMethod, writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getOldMethodCalls(), "antigas", writer);
				printMethodCalls(evaluatedMethod.getMethodCallsDiff().getNewMethodCalls(), "novas", writer);
				printAddedMethodCallsAutomatizationPercAndCorrectness(evaluatedMethod, writer, commitsEvaluationWindow);
			}
			
			if(evaluatedMethods.size() > 0){
				commitsEvaluationWindow.setCommitId(evaluatedMethods.get(0).getCommitId());
			}
			
			commitsEvaluationWindows.put(commitIndex, commitsEvaluationWindow);
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
	public void printTotalsPeriodicReport(Integer validCommitIndex, CommitNode commit, Integer evaluatedWindowSize, Integer realCommitIndex, Integer validMethodsCount) {
		if(totalEvaluatedMethods == 0){
			return;
		}
		
		FileWriter writer = null;
		
		try {
		
			//Extrai os dados que serão avaliados nesse relatorio periodico( Janela de avaliação)
			List<Double> automatizationPercValues = new ArrayList<Double>();
			List<Double> correctnessValues = new ArrayList<Double>();
			List<Double> fMeasureValues = new ArrayList<Double>();
			
			Integer manualCodedMethods = 0;
			Integer automaticCodedMethods = 0;
			Integer usefulSuggestionCount = 0;
			Integer uselessSuggestionCount = 0;
			
			int windowBegin = (validCommitIndex - evaluatedWindowSize) + 1;
			int windowEnd = validCommitIndex+1;
			if(windowBegin < 0){
				windowBegin = 0;
			}
			for(int i = windowBegin; i < windowEnd; i++){
				CommitsEvaluationWindow commitsEvaluationWindow = commitsEvaluationWindows.get(i);
				
				automatizationPercValues.addAll(commitsEvaluationWindow.getAutomatizationPercValues());
				correctnessValues.addAll(commitsEvaluationWindow.getCorrectnessValues());
				fMeasureValues.addAll(commitsEvaluationWindow.getfMeasureValues());
				
				manualCodedMethods += commitsEvaluationWindow.getManualCodedMethods();
				automaticCodedMethods += commitsEvaluationWindow.getAutomaticCodedMethods();
				
				usefulSuggestionCount += commitsEvaluationWindow.getUsefulSuggestionCount();
				uselessSuggestionCount += commitsEvaluationWindow.getUselessSuggestionCount();
			}
			
		
			writer = new FileWriter(new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Periodico.txt"), Boolean.TRUE);
			writer.write("**************************************************************************************************************\n");
			writer.write("TOTAIS APÓS " + (validCommitIndex+1) + " COMMITS (Commit Date: " + (commit != null ? commit.getDate() : "ULTIMO") + "):\n");
			writer.write("**************************************************************************************************************\n");
			writer.write("*Métodos válidos: " + validMethodsCount + "\n");
			writer.write("*Métodos avaliados(alguma sugestão fornecida): " + totalEvaluatedMethods + "\n");
			writer.write("*Métodos onde alguma sugestão foi fornecida: " + suggestionsProvided + "\n");
			writer.write("*Métodos onde uma sugestão foi aceita: " + suggestionsAccepted + "\n");
			BigDecimal correctness = new BigDecimal(calculateMean(correctnessValues)).setScale(3, RoundingMode.CEILING);
			writer.write("*Corretude Média: " + correctness + "\n");
			BigDecimal stdDevCorrectness = new BigDecimal(calculateStdDev(correctnessValues, correctness.doubleValue())).setScale(3, RoundingMode.CEILING);
			writer.write("*Desvio Padrão Corretude: " + stdDevCorrectness + "\n");
			BigDecimal automatizationPerc = new BigDecimal(calculateMean(automatizationPercValues)).setScale(3, RoundingMode.CEILING);
			writer.write("*Percentual de Automatização Médio: " + automatizationPerc + "\n");
			BigDecimal stdDevAutomatizationPerc = new BigDecimal(calculateStdDev(automatizationPercValues, correctness.doubleValue())).setScale(3, RoundingMode.CEILING);
			writer.write("*Desvio Padrão Percentual de Automatização: " + stdDevAutomatizationPerc + "\n");
			BigDecimal fMeasure = new BigDecimal(calculateMean(fMeasureValues)).setScale(3, RoundingMode.CEILING);
			writer.write("*F-Measure Médio: " + fMeasure + "\n");
			BigDecimal stdDevFMeasure = new BigDecimal(calculateStdDev(fMeasureValues, correctness.doubleValue())).setScale(3, RoundingMode.CEILING);
			writer.write("*Desvio Padrão F-Measure: " + stdDevFMeasure + "\n");
			//BigDecimal harmonicMean = new BigDecimal(2D*precision.doubleValue()*recall.doubleValue()/ (precision.doubleValue() + recall.doubleValue())).setScale(3, RoundingMode.CEILING);
			//writer.write("*Média Harmônica(Precision e Recall): " + harmonicMean + "\n");
			writer.write("**************************************************************************************************************\n");
			
			
			printTotalsPercRecommendationPeriodicReport(manualCodedMethods, automaticCodedMethods);
			printChartInput(correctness, stdDevCorrectness, automatizationPerc, stdDevAutomatizationPerc, fMeasure, stdDevFMeasure, 
					manualCodedMethods, automaticCodedMethods, usefulSuggestionCount, uselessSuggestionCount, windowBegin, windowEnd, realCommitIndex,
					validMethodsCount, commit);
			
			
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
			//writer.write("Sugestão Aprovada: " + (addedMethod.getAcceptedSugestionPosition() != -1 ? "Sim" : "Não") + "\n");
			writer.write("** Sugestões fornecidas **\n");
			for (int i = 0; i < addedMethod.getSuggestionsProvided().size(); i++) {
				/*if(addedMethod.getAcceptedSugestionPosition() == i){
					writer.write("SUGESTÃO APROVADA!\n");
				}*/
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

	private void printAddedMethodCallsAutomatizationPercAndCorrectness(EvaluatedMethod evaluatedMethod, FileWriter writer, CommitsEvaluationWindow commitsEvaluationWindow) throws IOException {
		/**
		 * This array stores each method call situation from evaluated Method
		 * 0 : methodCallName
		 * 1 : method situation [Manual(we couldn't foreseen it), Automático(we could foreseen it)]
		 * 2 : methodCall index from where the suggestion that foreseen this method came. ....
		 * 3 : suggestion index
		 */
		
		HashMap<String, Boolean> methodsUsefulness = new HashMap<String, Boolean>();
		
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
					
					boolean usefulMethod = false;
					for (int j = i+1; j < methodReport.length; j++){
						if(suggestedMethod.equals(methodReport[j][0])){
							usefulMethod = true;
							if(!METHOD_CALL_STATUS_AUTOMATIC.equals(methodReport[j][1])){
								methodReport[j][1] = METHOD_CALL_STATUS_AUTOMATIC;
								methodReport[j][2] = String.valueOf(i);
								methodReport[j][3] = String.valueOf(s);
							}
							break;
						}
					}
					
					//Analisa corretude
					Boolean usefulness = methodsUsefulness.get(suggestedMethod);
					if(usefulness == null){
						methodsUsefulness.put(suggestedMethod, usefulMethod);
					}
				}
			}
		}
		
		
		//Conta os métodos automatizados e exibe o suporte, a confiança, e a f-measure de cada sugestão aceita
		int automaticCodedMethods = 0;
		int manualCodedMethods = 0;
		for (int j = 0; j < methodReport.length; j++) {
			String[] methodSituation = methodReport[j];
			writer.write("Chamada: " + methodSituation[0] + "\n");
			writer.write("Situação: " + methodSituation[1] + "\n");
			
			if(j > 0){
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
		
		writer.write("Corretude/Utilidade dos Métodos\n");
		
		
		int usefulSuggestionCount = 0;
		int uselessSuggestionCount = 0;
		//Conta a corretude das sugestões, de acordo com cada diferente método sugerido
		for (String method : methodsUsefulness.keySet()) {
			Boolean useful = methodsUsefulness.get(method);
			writer.write("Método: " + method + "\n");
			if(useful){
				writer.write("Útil\n");
				usefulSuggestionCount++;
			}else{
				writer.write("Inútil\n");
				uselessSuggestionCount++;
			}
			
			
		}
		
		commitsEvaluationWindow.setAutomaticCodedMethods(commitsEvaluationWindow.getAutomaticCodedMethods() + automaticCodedMethods);
		commitsEvaluationWindow.setManualCodedMethods(commitsEvaluationWindow.getManualCodedMethods() + manualCodedMethods);
		
		Double automatizationPerc = new Double(automaticCodedMethods) / new Double(methodReport.length-1);
		commitsEvaluationWindow.getAutomatizationPercValues().add(automatizationPerc);
		writer.write("Percentual de Automatização: " + automatizationPerc + "\n");
		
		commitsEvaluationWindow.setUsefulSuggestionCount(commitsEvaluationWindow.getUsefulSuggestionCount() + usefulSuggestionCount);
		commitsEvaluationWindow.setUselessSuggestionCount(commitsEvaluationWindow.getUselessSuggestionCount() + uselessSuggestionCount);
		
		Double correctness;
		if(usefulSuggestionCount > 0 || uselessSuggestionCount > 0){
			correctness = new Double(usefulSuggestionCount) / new Double(uselessSuggestionCount + usefulSuggestionCount); 
		}else{
			correctness = 0D;
		}
		commitsEvaluationWindow.getCorrectnessValues().add(correctness);
		writer.write("Corretude: " + correctness + "\n");
		
		Double fMeasure;
		if(correctness.doubleValue() + automatizationPerc.doubleValue() != 0d){
			fMeasure = (2D*correctness.doubleValue()*automatizationPerc.doubleValue())/ (correctness.doubleValue() + automatizationPerc.doubleValue());
		}else{
			fMeasure = 0D;
		}
		commitsEvaluationWindow.getfMeasureValues().add(fMeasure);
		writer.write("F-Measure: " + fMeasure + "\n");
	}
	
	private void printTotalsPercRecommendationPeriodicReport(Integer manualCodedMethods, Integer automaticCodedMethods) {
		Integer totalCodedMethods = automaticCodedMethods + manualCodedMethods;
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Periodico.txt"), Boolean.TRUE);
			writer.write("**************************************************************************************************************\n");
			writer.write("*Métodos codificados: " + totalCodedMethods + "\n");
			writer.write("*Métodos codificados automaticamente: " + automaticCodedMethods + "\n");
			writer.write("*Métodos codificados manualmente: " + manualCodedMethods + "\n");
			BigDecimal automatizationRate = new BigDecimal(automaticCodedMethods.doubleValue()/totalCodedMethods.doubleValue());
			writer.write("*Percentual de Automatização: " + automatizationRate.setScale(3, RoundingMode.CEILING)+ "\n");
			
			BigDecimal averageSugIndex = new BigDecimal(calculateMean(acceptedSuggestionIndexes));
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
	
	private void printChartInput(BigDecimal correctness, BigDecimal stdDevCorrectness, BigDecimal automatizationPerc, BigDecimal stdDevAutomatizationPerc, 
			BigDecimal fMeasure, BigDecimal stdDevFMeasure, Integer manualCodedMethods, Integer automaticCodedMethods,
			Integer usefulSuggestionCount, Integer uselessSuggestionCount, int windowBegin, int windowEnd, Integer realCommitIndex, Integer validMethodsCount, CommitNode commit) {
		
		FileWriter writer = null;
		try {
			printChartInputHeader();
			writer = new FileWriter(new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Grafico.csv"), Boolean.TRUE);
			
			writer.write(windowBegin + " a " + windowEnd + ";");
			writer.write(validMethodsCount + ";");
			writer.write(totalEvaluatedMethods + ";");
			writer.write(realCommitIndex + " ("+ (commit != null ? commit.getDate() + "[" + commit.getId()  + "]": "ULTIMO") + ");");
			writer.write(usefulSuggestionCount + ";");
			writer.write(uselessSuggestionCount + ";");
			
			BigDecimal globalCorrectness;
			if(uselessSuggestionCount+usefulSuggestionCount == 0){
				globalCorrectness = BigDecimal.ZERO;
			}else{
				globalCorrectness = new BigDecimal(usefulSuggestionCount.doubleValue()/(uselessSuggestionCount+usefulSuggestionCount)).setScale(3, RoundingMode.CEILING);
			}
			writer.write(globalCorrectness + ";");
			
			printSingleDataChartInputCSV("globalCorrectness", globalCorrectness.toString());
			
			writer.write(automaticCodedMethods + ";");
			writer.write(manualCodedMethods + ";");
			
			BigDecimal globalAutomatizationPerc =  new BigDecimal(automaticCodedMethods.doubleValue()/(automaticCodedMethods+manualCodedMethods)).setScale(3, RoundingMode.CEILING);
			writer.write(globalAutomatizationPerc + ";");
			
			printSingleDataChartInputCSV("globalAutomatizationPerc", globalAutomatizationPerc.toString());
			
			BigDecimal globalFfMeasure;
			if(globalCorrectness.doubleValue() + globalAutomatizationPerc.doubleValue() != 0d){
				globalFfMeasure = new BigDecimal((2D*globalCorrectness.doubleValue()*globalAutomatizationPerc.doubleValue())/ (globalCorrectness.doubleValue() + globalAutomatizationPerc.doubleValue())).setScale(3, RoundingMode.CEILING);;
			}else{
				globalFfMeasure = BigDecimal.ZERO;
			}
			writer.write(globalFfMeasure + ";");
			
			printSingleDataChartInputCSV("globalFfMeasure", globalFfMeasure.toString());
			
			writer.write(correctness + ";");
			
			printSingleDataChartInputCSV("correctness", correctness.toString());
			
			writer.write(stdDevCorrectness + ";");
			writer.write(automatizationPerc + ";");
			
			printSingleDataChartInputCSV("automatizationPerc", automatizationPerc.toString());
			
			writer.write(stdDevAutomatizationPerc + ";");
			writer.write(fMeasure + ";");
			
			printSingleDataChartInputCSV("fMeasure", fMeasure.toString());
			
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
	
	private void printSingleDataChartInputCSV(String fileName, String data){
		File singleDataChartInputFile = new File(reportPath + projectEvaluationFolder + "\\" +  fileName + ".csv");
		String lastLine = tail(singleDataChartInputFile);
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(singleDataChartInputFile, Boolean.TRUE);
			if(lastLine == null || !lastLine.startsWith(reportNamePrefix)){
				writer.write("\n");
				writer.write(reportNamePrefix + ";");
			}
			writer.write(data + ";");
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

	private void printChartInputHeader() {
		File chartInputFile = new File(reportPath + projectEvaluationFolder + "\\" + reportNamePrefix + "Grafico.csv");
		if(!chartInputFile.exists()){
			FileWriter writer = null;
			try {
				writer = new FileWriter(chartInputFile);
				writer.write("Janela de Commits;");
				writer.write("Métodos Válidos;");
				writer.write("Métodos Avaliados(Alguma Sugestão);");
				writer.write("Índice real Commits;");
				writer.write("Sugestões Úteis;");
				writer.write("Sugestões Inúteis;");
				writer.write("Corretude Geral;");
				writer.write("Métodos Codif. Automaticamente;");
				writer.write("Métodos Codif. Manualmente;");
				writer.write("Perc. de Automatização Geral;");
				writer.write("F-Measure Geral;");
				writer.write("Corretude;");
				writer.write("StdDev Corretude;");
				writer.write("Perc. de Automatização;");
				writer.write("StdDev Perc. de Automatização;");
				writer.write("F-Measure;");
				writer.write("StdDev F-Measure");
				
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

	private double calculateMean(List<? extends Number> numbersList) {
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

    double calculateStdDev(List<? extends Number> numbersList, double mean)
    {
        return Math.sqrt(calculateVariance(numbersList, mean));
    }
    
    public String tail( File file ) {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile( file, "r" );
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();

            for(long filePointer = fileLength; filePointer != -1; filePointer--){
                fileHandler.seek( filePointer );
                int readByte = fileHandler.readByte();

                if( readByte == 0xA ) {
                    if( filePointer == fileLength ) {
                        continue;
                    }
                    break;

                } else if( readByte == 0xD ) {
                    if( filePointer == fileLength - 1 ) {
                        continue;
                    }
                    break;
                }

                sb.append( ( char ) readByte );
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch( java.io.FileNotFoundException e ) {
            e.printStackTrace();
            return null;
        } catch( java.io.IOException e ) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileHandler != null )
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    /* ignore */
                }
        }
    }
}
