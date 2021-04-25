package br.uff.vcc.exp.git;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.AndTreeFilter;
import org.eclipse.jgit.treewalk.filter.OrTreeFilter;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

 public class CommitNode {

	private String logMessage;
	
	public java.util.Date getDate() {
		return date;
	}

	private String id;
	private java.util.Date date;
	private List<FileNode> files = new ArrayList<FileNode>();
	
	public CommitNode(){
		
	}
	
	public static CommitNode Parse(RevCommit _revCommit, Repository _repo, Date later, String innerProjectName){
		
		return ExtractCommitInfo(_revCommit, _repo, later, innerProjectName);
	}
	
	private static CommitNode ExtractCommitInfo(RevCommit _revCommit, Repository _repo, Date later, String innerProjectName) {
		
		CommitNode c = new CommitNode();

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
					
					Boolean pathFromSelectedProjects = Boolean.FALSE; 
					String[] projectNames = innerProjectName.split(";");
					for (int i = 0; i < projectNames.length; i++) {
						pathFromSelectedProjects = diff.getNewPath().startsWith(projectNames[i]);
						if(pathFromSelectedProjects){
							break;
						}
					}
					if(!pathFromSelectedProjects){
						continue;
					}
					
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
}
