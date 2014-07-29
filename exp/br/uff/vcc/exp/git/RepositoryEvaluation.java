package br.uff.vcc.exp.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import br.uff.vcc.exp.entity.AddedMethod;
import br.uff.vcc.exp.entity.EvaluatedMethod;
import br.uff.vcc.exp.entity.MethodCallsDiff;
import br.uff.vcc.exp.report.ReportWriter;
import br.uff.vcc.plugin.handlers.SearchPatternsHandler;
import br.uff.vcc.util.ComparableList;
import br.uff.vcc.util.Suggestion;

public class RepositoryEvaluation {
	
	private Repository gitRepository;
	private String gitRepositoryPath;
	private String headCommit;
	private String innerProjectName;
	private String unitName;
	private String eclipseProjectName;
	private ReportWriter reportWriter;
	private Boolean evaluateOnlyNewMethods;
	private Integer periodicReportInterval;
	private Integer amountSuggestionsProvidedPerQuery;
	
	public RepositoryEvaluation(String location, String headCommit, String innerProjectName, String unitName, String eclipseProjectName, ReportWriter reportWriter, Boolean evaluateOnlyNewMethods, Integer periodicReportInterval, Integer amountSuggestionsProvidedPerQuery) {
		this.gitRepositoryPath = location;
		this.headCommit = headCommit;
		this.innerProjectName = innerProjectName;
		this.unitName = unitName;
		this.eclipseProjectName = eclipseProjectName;
		this.reportWriter = reportWriter;
		this.evaluateOnlyNewMethods = evaluateOnlyNewMethods;
		this.periodicReportInterval = periodicReportInterval;
		this.amountSuggestionsProvidedPerQuery = amountSuggestionsProvidedPerQuery;
		
		try {
			gitRepository = new FileRepository(new File(gitRepositoryPath + ".git"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void evaluateRepository() throws Exception{
		evaluateCommits();
		reportWriter.printTotalsPeriodicReport(null, null);
	}
	
	private void evaluateCommits() throws Exception{
		RevWalk rw = new RevWalk(gitRepository);
		if(innerProjectName != null && !"".equals(innerProjectName)){
			rw.setTreeFilter(AndTreeFilter.create(PathFilter.create(innerProjectName), TreeFilter.ANY_DIFF));
		}
		AnyObjectId headId, targetCommitId;
		
		try {
			headId = gitRepository.resolve(Constants.MASTER);
			targetCommitId = gitRepository.resolve(headCommit);
			RevCommit targetCommit = rw.parseCommit(targetCommitId);
			RevCommit headCommit = rw.parseCommit(headId);
			List<RevCommit> revCommits = RevWalkUtils.find(rw, headCommit, targetCommit);
			Collections.reverse(revCommits);
			for (int i = 0; i < revCommits.size(); i++) {
				RevCommit c = revCommits.get(i);
				CommitNode commit = CommitNode.Parse(c, gitRepository, null, innerProjectName);
				List<MethodCallsDiff> methodsDiff = new ArrayList<MethodCallsDiff>();
				for (FileNode f : commit.getFiles()){
					methodsDiff.addAll(f.extractAllMethodsDiff(gitRepository, eclipseProjectName, unitName));
				}
				
				List<EvaluatedMethod> evaluatedMethods = evaluateMethods(methodsDiff, c.getId().toString());
				reportWriter.printFullReport(evaluatedMethods);
				reportWriter.printMediumReport(evaluatedMethods);
				reportWriter.printShortReport(evaluatedMethods);
				reportWriter.printSuperShortReport(evaluatedMethods);
				if(i % periodicReportInterval == 0){
					reportWriter.printTotalsPeriodicReport(i, commit.getId());
				}
				evaluatedMethods = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Create EvaluatedMethod objects. Only the methods calls that weren't invoked before the commit, or that were invoked less times before the commit, are evaluated.
	 * @param methodsDiff
	 * @param commitId
	 * @return
	 */
	private List<EvaluatedMethod> evaluateMethods(List<MethodCallsDiff> methodsDiff, String commitId) {
		List<EvaluatedMethod> evaluatedMethods = new ArrayList<EvaluatedMethod>();
		for (MethodCallsDiff methodCallsDiff : methodsDiff) {
			if(evaluateOnlyNewMethods && !(methodCallsDiff.getOldMethodCalls() == null || methodCallsDiff.getOldMethodCalls().isEmpty()) ){
				continue;
			}
			EvaluatedMethod e = new EvaluatedMethod(methodCallsDiff.getMethodName(), new ArrayList<AddedMethod>(), commitId);
			//The first method is not evaluated, provided we don't have any previous method call to query the tree
			for (int i = 1; i < methodCallsDiff.getNewMethodCalls().size(); i++) {
				String newMethodCall = methodCallsDiff.getNewMethodCalls().get(i);
				if(!evaluateOnlyNewMethods){
					if(methodWillBeInvokedLater(newMethodCall, methodCallsDiff.getNewMethodCalls(), i)){
						continue;
					}
					if(methodCallsDiff.getOldMethodCalls() != null && methodCallsDiff.getOldMethodCalls().contains(newMethodCall)){
						int newMethodsCount = countListOcurrences(methodCallsDiff.getNewMethodCalls(), newMethodCall);
						int oldMethodsCount = countListOcurrences(methodCallsDiff.getOldMethodCalls(), newMethodCall);;
						if(oldMethodsCount >= newMethodsCount){
							continue;
						}
					}
				}
				ComparableList<String> queryInput = new ComparableList<String>();
				for (int j = 0; j < i; j++) {
					queryInput.add(methodCallsDiff.getNewMethodCalls().get(j));
				}
				ArrayList<Suggestion> suggestions = SearchPatternsHandler.searchInTree(queryInput);
				suggestions = filterTopSuggestions(suggestions);
				e.getAddedMethods().add(createAddedMethod(newMethodCall, suggestions));
			}
			if(e.getAddedMethods().size() > 0){
				for (AddedMethod addedMethod : e.getAddedMethods()) {
					if (addedMethod.getSuggestionsProvided().size() > 0) {
						e.setSuggestionsProvided(Boolean.TRUE);
					}
					if(addedMethod.getAcceptedSugestionPosition() != -1){
						e.setSuggestionAccepted(Boolean.TRUE);
						break;
					}
				}
				e.setMethodCallsDiff(methodCallsDiff);
				evaluatedMethods.add(e);
			}
		}
		return evaluatedMethods;
	}

	
	/**
	 * Do not evaluate all suggestions, since user won't scroll them forever
	 * @param suggestions
	 * @return
	 */
	private ArrayList<Suggestion> filterTopSuggestions(ArrayList<Suggestion> suggestions) {
		ArrayList<Suggestion> filteredSuggestions = new ArrayList<Suggestion>();
		for (int i = 0; i <Math.min(suggestions.size(), amountSuggestionsProvidedPerQuery); i++) {
			filteredSuggestions.add(suggestions.get(i));
		}
		
		return filteredSuggestions;
	}

	private static boolean methodWillBeInvokedLater(String newMethodCall, List<String> newMethodCalls, int i) {
		for (int j = i + 1; j < newMethodCalls.size(); j++) {
			if(newMethodCall.equals(newMethodCalls.get(j))){
				return true;
			}
		}
		return false;
	}

	private static int countListOcurrences(List list, Object o){
		int count = 0;
		for (Object listObject : list) {
			if(o.equals(listObject)){
				count++;
			}
		}
		return count;
		
	}
	private static AddedMethod createAddedMethod(String newMethodCall, ArrayList<Suggestion> suggestions) {
		if(suggestions.size() == 0){
			return new AddedMethod(newMethodCall, new ArrayList<Suggestion>(), -1, 0D);
		}else{
			for (int i = 0; i < suggestions.size(); i++) {
				Suggestion suggestion = suggestions.get(i);
				for (String suggestedMethod : suggestion.getSuggestedMethods()) {
					if(suggestedMethod.equals(newMethodCall)){
						return new AddedMethod(newMethodCall, suggestions, i, suggestion.getConfidence());
					}
				}
			}
			return new AddedMethod(newMethodCall, suggestions, -1, 0D);
		}
	}
}
