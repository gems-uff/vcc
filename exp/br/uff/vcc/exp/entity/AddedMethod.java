package br.uff.vcc.exp.entity;

import java.util.List;

import br.uff.vcc.util.Suggestion;

/**
 * This Class stores information about the methods added in a parsed commit and also the information about the suggestions provided to 
 * this method.
 * @author Luiz
 *
 */
public class AddedMethod {

	private String addedMethodName;
	private List<Suggestion> suggestionsProvided;
	private Integer acceptedSugestionPosition;
	private Double acceptedSuggestionConfidence;
	
	public AddedMethod(String addedMethodName,
			List<Suggestion> suggestionsProvided,
			Integer acceptedSugestionPosition,
			Double acceptedSuggestionConfidence) {
		super();
		this.addedMethodName = addedMethodName;
		this.suggestionsProvided = suggestionsProvided;
		this.acceptedSugestionPosition = acceptedSugestionPosition;
		this.acceptedSuggestionConfidence = acceptedSuggestionConfidence;
	}
	
	public String getAddedMethodName() {
		return addedMethodName;
	}
	
	public void setAddedMethodName(String addedMethodName) {
		this.addedMethodName = addedMethodName;
	}
	
	public List<Suggestion> getSuggestionsProvided() {
		return suggestionsProvided;
	}
	
	public void setSuggestionsProvided(List<Suggestion> suggestionsProvided) {
		this.suggestionsProvided = suggestionsProvided;
	}
	
	public Integer getAcceptedSugestionPosition() {
		return acceptedSugestionPosition;
	}
	
	public void setAcceptedSugestionPosition(Integer acceptedSugestionPosition) {
		this.acceptedSugestionPosition = acceptedSugestionPosition;
	}
	
	public Double getAcceptedSuggestionConfidence() {
		return acceptedSuggestionConfidence;
	}
	
	public void setAcceptedSuggestionConfidence(Double acceptedSuggestionConfidence) {
		this.acceptedSuggestionConfidence = acceptedSuggestionConfidence;
	}
}