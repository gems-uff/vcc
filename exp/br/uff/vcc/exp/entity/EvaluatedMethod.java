package br.uff.vcc.exp.entity;

import java.util.List;

public class EvaluatedMethod {

	private String methodName;
	private List<AddedMethod> addedMethods;
	private Boolean suggestionsProvided;
	private Boolean suggestionAccepted;
	
	public EvaluatedMethod(String methodName, List<AddedMethod> addedMethods) {
		super();
		this.methodName = methodName;
		this.addedMethods = addedMethods;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<AddedMethod> getAddedMethods() {
		return addedMethods;
	}

	public void setAddedMethods(List<AddedMethod> addedMethods) {
		this.addedMethods = addedMethods;
	}

	public Boolean getSuggestionsProvided() {
		return suggestionsProvided;
	}

	public void setSuggestionsProvided(Boolean suggestionsProvided) {
		this.suggestionsProvided = suggestionsProvided;
	}

	public Boolean getSuggestionAccepted() {
		return suggestionAccepted;
	}

	public void setSuggestionAccepted(Boolean suggestionAccepted) {
		this.suggestionAccepted = suggestionAccepted;
	}
}
