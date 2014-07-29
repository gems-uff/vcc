package br.uff.vcc.exp.entity;

import java.util.List;

public class EvaluatedMethod {

	private String methodName;
	private List<AddedMethod> addedMethods;
	private Boolean suggestionsProvided;
	private Boolean suggestionAccepted;
	
	private String commitId;
	
	private MethodCallsDiff methodCallsDiff;
	
	public EvaluatedMethod(String methodName, List<AddedMethod> addedMethods, String commitId) {
		super();
		this.methodName = methodName;
		this.addedMethods = addedMethods;
		this.commitId = commitId;
		suggestionAccepted = Boolean.FALSE;
		suggestionsProvided = Boolean.FALSE;
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

	public Boolean isSuggestionsProvided() {
		return suggestionsProvided;
	}

	public void setSuggestionsProvided(Boolean suggestionsProvided) {
		this.suggestionsProvided = suggestionsProvided;
	}

	public Boolean isSuggestionAccepted() {
		return suggestionAccepted;
	}

	public void setSuggestionAccepted(Boolean suggestionAccepted) {
		this.suggestionAccepted = suggestionAccepted;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

	public MethodCallsDiff getMethodCallsDiff() {
		return methodCallsDiff;
	}

	public void setMethodCallsDiff(MethodCallsDiff methodCallsDiff) {
		this.methodCallsDiff = methodCallsDiff;
	}
}
