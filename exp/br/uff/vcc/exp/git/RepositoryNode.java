package br.uff.vcc.exp.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import br.uff.vcc.exp.entity.EvaluatedMethod;

public class RepositoryNode {
	
	public enum Detail {
		File,
		Class,
		Method
	}
	
	public String getName() {
		return name;
	}

	public List<CommitNode> getCommits() {
		return commits;
	}

	Git git;
	Repository gitRepository;
	String gitRepositoryPath;
	String name;
	List<CommitNode> commits = new ArrayList<CommitNode>();
	
	Repository getRepository(){ 
		return gitRepository;
	}
	
	
	public RepositoryNode(String _name, String _location) {
		name = _name;
		gitRepositoryPath = _location;
		
		try {
			gitRepository = new FileRepository(new File(gitRepositoryPath + ".git"));
			git = new Git(gitRepository);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Parse() throws Exception{
		List<EvaluatedMethod> evaluatedMethods = CommitNode.evaluateRepository(gitRepository, "0e70e23f8301223b677dbf3f4720c0f22969ac4a");
		int suggestionsProvided = 0;
		int suggestionsAccepted = 0;
		for (EvaluatedMethod evaluatedMethod : evaluatedMethods) {
			printMethodReport();
			printOldMethodCalls();
			printNewMethodCalls();
			printAddedMethodCalls();
			if(evaluatedMethod.getSuggestionAccepted()){
				suggestionsAccepted++;
				suggestionsProvided++;
			}
			else if(evaluatedMethod.getSuggestionsProvided()){
				suggestionsProvided++;
			}
		}
		printFinalReport(evaluatedMethods, suggestionsProvided, suggestionsAccepted);
	}

	public void printFinalReport(List<EvaluatedMethod> evaluatedMethods,
			int suggestionsProvided, int suggestionsAccepted) {
		System.out.println("Métodos avaliados: " + evaluatedMethods.size());
		System.out.println("Métodos onde alguma sugestão foi fornecida: " + suggestionsProvided);
		System.out.println("Métodos onde uma sugestão foi aceita: " + suggestionsAccepted);
	}
	
	public void Debug(){
		System.out.println("Repository: " + name + " Path: " + gitRepositoryPath);
		
		for (CommitNode c : commits)
			c.Debug();
	}
	
	public static void SaveToDatabase(String repoPath, String repoName){
		RepositoryNode repoNode = new RepositoryNode(repoName, repoPath);
		
		
		//Database.Open();
		Date lastCommitDate = new Date();//Database.AddRepository(repoNode);
		//CommitNode.SaveToDatabase(repoNode, lastCommitDate);	
		//Database.UpdateRepoToLastCommit(repoNode);
		//Database.Close();
		
	}
	
	public static void main(String[] args) throws Exception {
		RepositoryNode r= new RepositoryNode("sl4j-api", "C:\\Desenvolvimento\\repositorios\\slf4j\\slf4j-api\\");
		r.Parse();
	}
}
