package br.uff.vcc.exp.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;

import br.uff.vcc.exp.entity.MethodCallsDiff;

public class ClassNode  {

	public int getLineEnd() {
		return lineEnd;
	}

	int lineStart;
	int lineEnd;
	String data;
	ChangeType changeType;
	String name;
	List<FunctionNode> functions = new ArrayList<FunctionNode>();
	
	public List<FunctionNode> getFunctions() {
		return functions;
	}

	public int getLineStart() {
		return lineStart;
	}

	public String getData() {
		return data;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public String getName() {
		return name;
	}
	
	public void Debug(){
		System.out.println("----Name: " + name);
		System.out.println("------LineStart: " + lineStart);
		System.out.println("------LineEnd: " + lineEnd);
		System.out.println("------ChangeType: " + changeType.toString());
		
		for (FunctionNode func : functions)
			func.Debug();
	}

	
	private static Map<String,ClassNode> extractClasses(String source){
		final Map<String,ClassNode> _classes = new HashMap<String, ClassNode>();
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
		parser.setCompilerOptions(options);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
			public boolean visit(TypeDeclaration node){
				ClassNode _c = new ClassNode();
				_c.data = node.toString();
				_c.lineStart = cu.getLineNumber(node.getName().getStartPosition());
				_c.lineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
				_c.name = node.getName().toString();
				
				_classes.put(_c.name, _c);
				return false;
			}
		});

		
		return _classes;
		
	}
	
	public static List<MethodCallsDiff> parse(FileNode _file, Repository _repo) throws MissingObjectException, IOException{
		
		List<MethodCallsDiff> methodsDiff = new ArrayList<MethodCallsDiff>();
		List<ClassNode> resultClass = new ArrayList<ClassNode>();

		switch (_file.changeType) {
		case ADD: {
			ObjectLoader newFile = _repo.open(_file.newObjId);
			String newData = readStream(newFile.openStream());

			resultClass.addAll(extractClasses(newData).values());

			for (ClassNode _class : resultClass) {
				_class.changeType = ChangeType.ADD;
				methodsDiff.addAll(FunctionNode.extractMethodCallsDiff(_class, null));
			}

		}
			break;

		case MODIFY: {
			// Extract classes from all files in order to see if there is any
			// modification on it
			ObjectLoader oldFile = _repo.open(_file.oldObjId);
			String oldData = readStream(oldFile.openStream());

			ObjectLoader newFile = _repo.open(_file.newObjId);
			String newData = readStream(newFile.openStream());

			Map<String, ClassNode> oldClasses = extractClasses(oldData);
			Map<String, ClassNode> newClasses = extractClasses(newData);

			methodsDiff = findClassDiff(oldClasses, newClasses);
		}
			break;

		default:
			break;

		}
		return methodsDiff;
	}
	
	private static List<MethodCallsDiff> findClassDiff(Map<String,ClassNode> oldClasses,
			Map<String,ClassNode> newClasses) {
		
		List<MethodCallsDiff> methodCallsDiff = new ArrayList<MethodCallsDiff>();
		
		// Compare each class
		for (ClassNode _classNew : newClasses.values()){
			
			// Try to find the class in previous version
			ClassNode _oldClass = oldClasses.get(_classNew.name);
			
			if (_oldClass != null){
				
				// See if they are different
				if (_classNew.data.compareTo(_oldClass.data) != 0){
					_classNew.changeType = ChangeType.MODIFY;
					
					// Check for functions modified
					methodCallsDiff.addAll(FunctionNode.extractMethodCallsDiff(_classNew, _oldClass));
				}
			} else { // The old file does not have this class (NEW)
				_classNew.changeType = ChangeType.ADD;
				methodCallsDiff.addAll(FunctionNode.extractMethodCallsDiff(_classNew, null));
			}
			
		}
		
		return methodCallsDiff;
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
