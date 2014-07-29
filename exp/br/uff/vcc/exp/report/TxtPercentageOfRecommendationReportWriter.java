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

public class TxtPercentageOfRecommendationReportWriter extends TxtReportWriter {

	private Integer manualCodedMethods = 0;
	private Integer automaticCodedMethods = 0;
	private Integer totalCodedMethods = 0;
	
	private List<Integer> acceptedSuggestionIndexes;
	
	
	public TxtPercentageOfRecommendationReportWriter(String eclipseProjectName, String reportNamePrefix) {
		super(eclipseProjectName, reportNamePrefix);
		acceptedSuggestionIndexes = new ArrayList<Integer>();
	}

	@Override
	protected void printAddedMethodCalls(EvaluatedMethod evaluatedMethod, FileWriter writer) throws IOException {
		/**
		 * This array stores each method call situation from evaluated Method
		 * 0 : methodCallName
		 * 1 : method situation [Manual(we couldn't foreseen it), Automático(we could foreseen it)]
		 * 2 : methodCall index from where the suggestion that foreseen this method came. ....
		 * 3 : suggestion index
		 */
		String[][] methodReport = new String[evaluatedMethod.getAddedMethods().size() + 1][4];
		methodReport[0][0] = evaluatedMethod.getMethodCallsDiff().getNewMethodCalls().get(0);
		methodReport[0][1] = "Primeira Chamada";
		//writer.write("---------------- Chamadas de método adicionadas ---------------\n");
		for (int i = 0; i < evaluatedMethod.getAddedMethods().size(); i++){
			AddedMethod addedMethod = evaluatedMethod.getAddedMethods().get(i);
			methodReport[i+1][0] = addedMethod.getAddedMethodName();
			methodReport[i+1][1] = "Manual";
		}
		for (int i = 0; i < evaluatedMethod.getAddedMethods().size(); i++) {
			AddedMethod addedMethod = evaluatedMethod.getAddedMethods().get(i);
			if(addedMethod.getSuggestionsProvided().size() == 0){
				continue;
			}
			
			int bestSuggestionIndex = -1;
			int bestSuggestionCount = 0;
			boolean bestSuggestionIncludesNextMethodCall = false;
			for (int s = 0; s < addedMethod.getSuggestionsProvided().size(); s++) {
				Suggestion suggestion = addedMethod.getSuggestionsProvided().get(s);
				int currentSuggestedMethodIndex = 0;
				int currentSuggestionCount = 0;
				boolean currentSuggestionIncludesNextMethodCall = false;
				for (int j = i+1; j < methodReport.length; j++) {
					if("Automático".equals(methodReport[j][1])){
						continue;
					}
					for(int k = currentSuggestedMethodIndex; k < suggestion.getSuggestedMethods().size(); k++){
						String suggestedMethod = suggestion.getSuggestedMethods().get(k);
						if(suggestedMethod.equals(methodReport[j][0])){
							currentSuggestedMethodIndex = k + 1;
							currentSuggestionCount++;
						}
					}
					if(j == i && currentSuggestionCount > 0){
						currentSuggestionIncludesNextMethodCall = true;
					}
				}
				if(bestSuggestionIncludesNextMethodCall && !currentSuggestionIncludesNextMethodCall){
					continue;
				}
				if((!bestSuggestionIncludesNextMethodCall && currentSuggestionIncludesNextMethodCall) || 
						currentSuggestionCount > bestSuggestionCount){
					bestSuggestionCount = currentSuggestionCount;
					bestSuggestionIndex = s;
					bestSuggestionIncludesNextMethodCall = currentSuggestionIncludesNextMethodCall;
				}
			}
			
			if(bestSuggestionCount > 0){
				Suggestion suggestion = addedMethod.getSuggestionsProvided().get(bestSuggestionIndex);
				for (int j = 1; j < methodReport.length; j++) {
					String[] methodSituation = methodReport[j];
					if(!"Automático".equals(methodSituation[1]) && suggestion.getSuggestedMethods().contains(methodSituation[0])){
						methodSituation[1] = "Automático";
						methodSituation[2] = String.valueOf(i);
						methodSituation[3] = String.valueOf(bestSuggestionIndex);
					}
				}
			}
		}
		
		for (int j = 0; j < methodReport.length; j++) {
			String[] methodSituation = methodReport[j];
			writer.write("Chamada: " + methodSituation[0] + "\n");
			writer.write("Situação: " + methodSituation[1] + "\n");
			
			if(j > 0){
				totalCodedMethods++;
				if("Automático".equals(methodSituation[1])){
					Integer suggestionIndex = Integer.valueOf(methodSituation[3]);
					Integer methodCallPositionSuggestionCameFromIndex = Integer.valueOf(methodSituation[2]);
					//Suggestion index starts in 0. Show it starting in 1
					writer.write("Índice da Sugestão: " + (suggestionIndex+1) + "\n");
					acceptedSuggestionIndexes.add(suggestionIndex+1);
					Suggestion suggestion = evaluatedMethod.getAddedMethods().get(methodCallPositionSuggestionCameFromIndex).getSuggestionsProvided().get(suggestionIndex);
					writer.write("Confiança: " + suggestion.getConfidence() + "\n");
					writer.write("Suporte: " + suggestion.getSupport() + "\n");
					
					BigDecimal harmonicMean = new BigDecimal(2D/ (1D/suggestion.getConfidence() + 1D/suggestion.getSupport()));
					writer.write("Média Harmônica: " + harmonicMean.setScale(3, RoundingMode.CEILING) + "\n");
					
					automaticCodedMethods++;
				}else{
					manualCodedMethods++;
				}
			}
		}
	}
	
	@Override
	public void printTotalsPeriodicReport(Integer commitPosition, String commitId) {
		if(totalCodedMethods == 0){
			return;
		}
		
		super.printTotalsPeriodicReport(commitPosition, commitId);
		FileWriter writer = null;
		try {
			writer = new FileWriter(new File(reportPath + eclipseProjectName + "\\" + reportNamePrefix + "Periodico.txt"), Boolean.TRUE);
			writer.write("**************************************************************************************************************\n");
			writer.write("*Métodos codificados: " + totalCodedMethods + "\n");
			writer.write("*Métodos codificados automaticamente: " + automaticCodedMethods + "\n");
			writer.write("*Métodos codificados manualmente: " + manualCodedMethods + "\n");
			BigDecimal automatizationRate = new BigDecimal(automaticCodedMethods.doubleValue()/totalCodedMethods.doubleValue());
			writer.write("*Percentual de Automatização: " + automatizationRate.setScale(3, RoundingMode.CEILING)+ "\n");
			BigDecimal averageSugIndex = new BigDecimal(averageSuggestionIndexes());
			writer.write("*Posição média da sugestão: " + averageSugIndex.setScale(3, RoundingMode.CEILING) + "\n");
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

	private double averageSuggestionIndexes() {
		Integer sum = 0;
		if (!acceptedSuggestionIndexes.isEmpty()) {
			for (Integer value : acceptedSuggestionIndexes) {
				sum += value;
			}
			return sum.doubleValue() / acceptedSuggestionIndexes.size();
		}
		return sum;
	}
	
}
