package br.uff.vcc.exp.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;

import br.uff.vcc.exp.entity.MethodCallsDiff;

public class FileNode { //extends PatchInfo {

	List<ClassNode> classes = new ArrayList<ClassNode>();
	String newName;
	String oldName;
	ObjectId newObjId;
	ObjectId oldObjId;
	ChangeType changeType;
	
	public List<ClassNode> getClasses(){
		return classes;
	}
	
	public FileNode(String _newName, String _oldName, ObjectId _newObjId, ObjectId _oldObjId,
			ChangeType _changeType) {
		this.newName = _newName;
		this.oldName = _oldName;
		this.newObjId = _newObjId;
		this.oldObjId = _oldObjId;
		this.changeType = _changeType;
		//super(_newName, _oldName, _newObjId, _oldObjId, _changeType);
	}
	
	public List<MethodCallsDiff> Parse(Repository _repo) throws Exception{
		
		try {
			List<MethodCallsDiff> methodsDiff = new ArrayList<MethodCallsDiff>();
			methodsDiff.addAll(ClassNode.parse(this, _repo));
			return methodsDiff;
		} catch (Exception e) {
			throw e;
		} 
	}
	
	public void Debug(){
		System.out.println("Name: " + newName);
		System.out.println("--Modification: " + changeType.toString());
		
		for (ClassNode cl : classes){
			cl.Debug();
		}
	}
}
