package br.uff.vcc.exp.entity;

import java.util.ArrayList;
import java.util.List;

public class CommitsEvaluationWindow {

	private List<Double> automatizationPercValues;
	private List<Double> correctnessValues;
	private List<Double> fMeasureValues;
	
	private Integer manualCodedMethods;
	private Integer automaticCodedMethods;
	
	private Integer usefulSuggestionCount;
	private Integer uselessSuggestionCount;
	
	private String commitId;
	
	public CommitsEvaluationWindow() {
		automatizationPercValues = new ArrayList<Double>();
		correctnessValues = new ArrayList<Double>();
		fMeasureValues = new ArrayList<Double>();
		
		manualCodedMethods = 0;
		automaticCodedMethods = 0;
		
		usefulSuggestionCount = 0;
		uselessSuggestionCount = 0;
	}
	
	public List<Double> getAutomatizationPercValues() {
		return automatizationPercValues;
	}
	public void setAutomatizationPercValues(List<Double> automatizationPercValues) {
		this.automatizationPercValues = automatizationPercValues;
	}
	public List<Double> getCorrectnessValues() {
		return correctnessValues;
	}
	public void setCorrectnessValues(List<Double> correctnessValues) {
		this.correctnessValues = correctnessValues;
	}
	public List<Double> getfMeasureValues() {
		return fMeasureValues;
	}
	public void setfMeasureValues(List<Double> fMeasureValues) {
		this.fMeasureValues = fMeasureValues;
	}
	public Integer getManualCodedMethods() {
		return manualCodedMethods;
	}
	public void setManualCodedMethods(Integer manualCodedMethods) {
		this.manualCodedMethods = manualCodedMethods;
	}
	public Integer getAutomaticCodedMethods() {
		return automaticCodedMethods;
	}
	public void setAutomaticCodedMethods(Integer automaticCodedMethods) {
		this.automaticCodedMethods = automaticCodedMethods;
	}
	public Integer getUsefulSuggestionCount() {
		return usefulSuggestionCount;
	}
	public void setUsefulSuggestionCount(Integer usefulSuggestionCount) {
		this.usefulSuggestionCount = usefulSuggestionCount;
	}
	public Integer getUselessSuggestionCount() {
		return uselessSuggestionCount;
	}
	public void setUselessSuggestionCount(Integer uselessSuggestionCount) {
		this.uselessSuggestionCount = uselessSuggestionCount;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}
}