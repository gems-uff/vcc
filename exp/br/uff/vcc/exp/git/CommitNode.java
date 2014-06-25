package br.uff.vcc.exp.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import br.uff.vcc.exp.entity.AddedMethod;
import br.uff.vcc.exp.entity.EvaluatedMethod;
import br.uff.vcc.exp.entity.MethodCallsDiff;
import br.uff.vcc.plugin.handlers.SearchPatternsHandler;
import br.uff.vcc.util.ComparableList;
import br.uff.vcc.util.Suggestion;

/* Teste
 * 
 */
 public class CommitNode {

	private CommitNode parent = null;
	//private UserNode user;
	private String logMessage;
	
	public java.util.Date getDate() {
		return date;
	}

	private String id;
	private java.util.Date date;
	private List<FileNode> files = new ArrayList<FileNode>();
	
	public CommitNode(CommitNode _parent){
		parent = _parent;
	}
	
	public CommitNode(){
		
	}
	
	public void Debug(){
		//System.out.println("Commit Message: " + logMessage + " User: " + user.getName());
		
		for (FileNode f : files){
			f.Debug();
		}
	}
	
	final public static List<EvaluatedMethod> evaluateRepository(Repository _repo, String initialCommitId) throws Exception{
		List<CommitNode> commits = new ArrayList<CommitNode>();
		List<EvaluatedMethod> evaluatedMethods = new ArrayList<EvaluatedMethod>();
		
		RevWalk rw = new RevWalk(_repo);
		AnyObjectId headId, targetCommitId;
		
		try {
			
			headId = _repo.resolve(Constants.HEAD);
			targetCommitId = _repo.resolve(initialCommitId);
			RevCommit targetCommit = rw.parseCommit(targetCommitId);
			RevCommit headCommit = rw.parseCommit(headId);
			List<RevCommit> revCommits = RevWalkUtils.find(rw, headCommit, targetCommit);
			Collections.reverse(revCommits);
			for (RevCommit c : revCommits) {
				CommitNode commit = Parse(c, _repo, null);
				List<MethodCallsDiff> methodsDiff = new ArrayList<MethodCallsDiff>();
				for (FileNode f : commit.files){
					methodsDiff.addAll(f.Parse(_repo));
				}
				
				evaluatedMethods.addAll(evaluateMethods(methodsDiff));
				
				
				//commit.user = UserNode.AddOrRetrieveUser(c.getAuthorIdent().getName());
				commit.logMessage = c.getFullMessage();
				commit.id = c.getId().toString();
				commits.add(commit);
			}
			
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	
	
	private static List<EvaluatedMethod> evaluateMethods(List<MethodCallsDiff> methodsDiff) {
		List<EvaluatedMethod> evaluatedMethods = new ArrayList<EvaluatedMethod>();
		for (MethodCallsDiff methodCallsDiff : methodsDiff) {
			EvaluatedMethod e = new EvaluatedMethod(methodCallsDiff.getMethodName(), new ArrayList<AddedMethod>());
			for (int i = 1; i < methodCallsDiff.getNewMethodCalls().size(); i++) {
				String newMethodCall = methodCallsDiff.getNewMethodCalls().get(i);
				if(!methodCallsDiff.getOldMethodCalls().contains(newMethodCall)){
					ComparableList<String> queryInput = new ComparableList<String>();
					for (int j = 0; j < i; j++) {
						queryInput.add(methodCallsDiff.getNewMethodCalls().get(j));
					}
					ArrayList<Suggestion> suggestions = SearchPatternsHandler.searchInTree(queryInput);
					e.getAddedMethods().add(createAddedMethod(newMethodCall, suggestions));
				}
			}
			evaluatedMethods.add(e);
		}
		return evaluatedMethods;
	}

	private static AddedMethod createAddedMethod(String newMethodCall, ArrayList<Suggestion> suggestions) {
		if(suggestions.size() == 0){
			return new AddedMethod(newMethodCall, null, -1, 0D);
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

	private static CommitNode Parse(RevCommit _revCommit, Repository _repo, Date later){
		
		return ExtractCommitInfo(_revCommit, _repo, later);
	}
	
	private static CommitNode ExtractCommitInfo(RevCommit _revCommit, Repository _repo, Date later) {
		
		CommitNode c = new CommitNode(null);

		
		RevWalk rw = new RevWalk(_repo);
		try {
			/*if (commit == null) {
				ObjectId object = getDefaultBranch(repository);
				commit = rw.parseCommit(object);
			}*/

			if (_revCommit.getParentCount() == 0) {
				TreeWalk tw = new TreeWalk(_repo);
				tw.reset();
				tw.setRecursive(true);
				tw.addTree(_revCommit.getTree());
				c.date = _revCommit.getAuthorIdent().getWhen();
				
				if ( (later != null) && c.date.before(later)){
					return null;
				}
				
				while (tw.next()) {
					
					if (isJavaFile(tw.getPathString())){
						c.files.add(new FileNode(tw.getPathString(), tw.getPathString(),
								tw.getObjectId(0), null, ChangeType.ADD));
					}
				}
				tw.release();
			} else {
				RevCommit parent = rw.parseCommit(_revCommit.getParent(0).getId());
				DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
				df.setRepository(_repo);	
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setDetectRenames(true);
				List<DiffEntry> diffs = df.scan(parent.getTree(), _revCommit.getTree());
				c.date = _revCommit.getAuthorIdent().getWhen();
				
				if ((later != null) && c.date.before(later)){
					return null;
				}
				
				for (DiffEntry diff : diffs) {
					
					if (diff.getChangeType().equals(ChangeType.ADD)) {
						if (isJavaFile(diff.getNewPath())){
							c.files.add(new FileNode(diff.getNewPath(), null,
								diff.getNewId().toObjectId(), null, diff.getChangeType()));
						}
					} else if (diff.getChangeType().equals(ChangeType.MODIFY)){
						if (isJavaFile(diff.getNewPath())){
							c.files.add(new FileNode(diff.getNewPath(), null,
									diff.getNewId().toObjectId(), diff.getOldId().toObjectId(), diff.getChangeType()));
						}
					} else if (diff.getChangeType().equals(ChangeType.RENAME)) {
						if (isJavaFile(diff.getNewPath())){
							c.files.add(new FileNode(diff.getNewPath(), diff.getOldPath(),
								diff.getNewId().toObjectId(), diff.getOldId().toObjectId(), diff.getChangeType()));
						}
					} /*else if (diff.getChangeType().equals(ChangeType.DELETE)) {
						if (isJavaFile(diff.getOldPath())){
							c.files.add(new FileNode(null, diff.getOldPath(),
									null, diff.getOldId().toObjectId(), diff.getChangeType()));
						}
					}*/
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rw.dispose();
		}
		
		return c;
	}
	
	private static boolean isJavaFile(String file){

        int lastSlash = file.lastIndexOf(File.separatorChar);
        String filename = file.substring(lastSlash + 1);
        int last_dot = filename.lastIndexOf('.');
        String extension = filename.substring(last_dot + 1);

        if (extension.compareTo("java") == 0) {
            return true;
        }
        
        return false;
	}

	/*public UserNode getUser() {
		return user;
	}*/

	public String getLogMessage() {
		return logMessage;
	}

	public String getId() {
		return id;
	}

	public List<FileNode> getFiles() {
		return files;
	}

	/*public static void SaveToDatabase(RepositoryNode repoNode,
			Date lastCommitDate) {
		
		RevWalk rw = new RevWalk(repoNode.getRepository());
		AnyObjectId id;
		int counter = 0;
		
		try {
			id = repoNode.getRepository().resolve(Constants.HEAD);
			RevCommit root = rw.parseCommit(id);
			rw.sort(RevSort.REVERSE);
			rw.markStart(root);
			
			RevCommit c = null;
			while ((c = rw.next()) != null){
				CommitNode commit = Parse(c, 
						repoNode.getRepository(), lastCommitDate);
				
				if (commit != null){
					for (FileNode f : commit.files){
						f.Parse(repoNode.getRepository());
					}
				
				
					//commit.user = UserNode.AddOrRetrieveUser(c.getAuthorIdent().getName());
					commit.logMessage = c.getFullMessage();
					commit.id = c.getId().toString();
					System.out.println("Processados: " + counter);
					//Database.AddCommit(commit, repoNode);
					counter++;
				}
			}
			
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
}

 

