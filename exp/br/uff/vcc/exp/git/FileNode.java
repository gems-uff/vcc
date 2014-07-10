package br.uff.vcc.exp.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;

import br.uff.vcc.exp.entity.MethodCallsDiff;

public class FileNode { //extends PatchInfo {

	String newName;
	String oldName;
	ObjectId newObjId;
	ObjectId oldObjId;
	ChangeType changeType;
	
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
		List<MethodCallsDiff> methodsDiff = new ArrayList<MethodCallsDiff>();
		switch (changeType) {
		case ADD: {
			ObjectLoader newFile = _repo.open(newObjId);
			String newData = readStream(newFile.openStream());

			//resultClass.addAll(extractClasses(newData).values());

			methodsDiff.addAll(FunctionNode.extractMethodCallsDiff(newData, null));
			//for (ClassNode _class : resultClass) {
				//_class.changeType = ChangeType.ADD;
			//}

		}
		break;

		case MODIFY: {
			// Extract classes from all files in order to see if there is any
			// modification on it
			ObjectLoader oldFile = _repo.open(oldObjId);
			String oldData = readStream(oldFile.openStream());

			ObjectLoader newFile = _repo.open(newObjId);
			String newData = readStream(newFile.openStream());

			//Map<String, ClassNode> oldClasses = extractClasses(oldData);
			//Map<String, ClassNode> newClasses = extractClasses(newData);

			methodsDiff.addAll(FunctionNode.extractMethodCallsDiff(newData, oldData));
		}
		break;

		default:
			break;

		}
		return methodsDiff;
	}
	
	public void Debug(){
		System.out.println("Name: " + newName);
		System.out.println("--Modification: " + changeType.toString());
	}
	
	private static String readStream(InputStream iStream) throws IOException {
		// build a Stream Reader, it can read char by char
		InputStreamReader iStreamReader = new InputStreamReader(iStream);
		// build a buffered Reader, so that i can read whole line at once
		BufferedReader bReader = new BufferedReader(iStreamReader);
		String line = null;
		StringBuilder builder = new StringBuilder();
		while ((line = bReader.readLine()) != null) { // Read till end
			builder.append(line + '\n');
		}
		bReader.close(); // close all opened stuff
		iStreamReader.close();
		iStream.close();
		return builder.toString();
	}
}
