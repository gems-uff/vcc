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
		List<EvaluatedMethod> evaluatedMethods = CommitNode.evaluateRepository(gitRepository, "a64161a3f0845562b26995aa62b46068af1c821b");
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
		RepositoryNode r= new RepositoryNode("sl4j", "C:\\Desenvolvimento\\repositorios\\slf4j\\");
		r.Parse();
	}
}
