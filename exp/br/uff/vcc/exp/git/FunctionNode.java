package br.uff.vcc.exp.git;

import java.io.IOException;
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
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

import br.uff.vcc.exp.entity.MethodCallsDiff;
import br.uff.vcc.plugin.handlers.GenerateTreeHandler;
import br.uff.vcc.plugin.visitors.MethodInvocationVisitor;

public class FunctionNode {
	int lineStart;
	public int getLineStart() {
		return lineStart;
	}

	public int getLineEnd() {
		return lineEnd;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public String getName() {
		return name;
	}

	int lineEnd;
	ChangeType changeType;
	String name;
	/*
	private static Map<String,FunctionNode> extractFunctions(ClassNode _classNode){
		final Map<String,FunctionNode> _functions = new HashMap<String, FunctionNode>();
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(_classNode.getData().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
		parser.setCompilerOptions(options);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
			public boolean visit(MethodDeclaration node){
				FunctionNode _functionNode = new FunctionNode();
				_functionNode.name = node.getName().toString();
				_functionNode.lineStart = cu.getLineNumber(node.getName().getStartPosition());
				_functionNode.lineEnd = cu.getLineNumber(node.getStartPosition() + node.getLength());
				
				_functions.put(_functionNode.name, _functionNode);
				return false;
			}
		});

		
		return _functions;
		
	}
	*/
	private static Map<String,MethodDeclaration> extractMethodDeclarations(ClassNode _classNode){
		final Map<String,MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(_classNode.getData().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
		parser.setCompilerOptions(options);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
			public boolean visit(MethodDeclaration node){
				String methodName = "";
				try {
					methodName = GenerateTreeHandler.getCompleteMethodName(node.resolveBinding());
				} catch (IOException e) {
					e.printStackTrace();
				}
				methods.put(methodName, node);
				return false;
			}
		});

		
		return methods;
		
	}
	
	private static List<String> extractInvokedMethods(MethodDeclaration methodDeclaration){
		final List<String> methodInvocatons = new ArrayList<String >();
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(methodDeclaration.toString().toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
		parser.setCompilerOptions(options);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
			public boolean visit(MethodInvocation node){
				String methodName = "";
				try {
					methodName = GenerateTreeHandler.getCompleteMethodName(node.resolveMethodBinding());
				} catch (IOException e) {
					e.printStackTrace();
				}
				methodInvocatons.add(methodName);
				return false;
			}
		});

		
		return methodInvocatons;
		
	}
	
	/*
	public static List<FunctionNode> Parse(ClassNode _newClass, ClassNode _oldClass) {
		
		List<FunctionNode> resultFunctions = new ArrayList<FunctionNode>();

		switch (_newClass.getChangeType()) {
		case ADD: {
			resultFunctions.addAll(ExtractFunctions(_newClass).values());

			for (FunctionNode _function : resultFunctions) {
				_function.changeType = ChangeType.ADD;
			}

		}
			break;

		case MODIFY: {
			// Extract functions from class in order to see if there is any
			// modification on it
			Map<String, FunctionNode> oldFunctions = ExtractFunctions(_oldClass);
			Map<String, FunctionNode> newFunctions = ExtractFunctions(_newClass);

			resultFunctions = FindFunctionsDiff(oldFunctions, newFunctions, _oldClass, _newClass);
		}
			break;

		default:
			break;

		}
		return resultFunctions;
	}*/
	
	public static List<MethodCallsDiff> extractMethodCallsDiff(ClassNode _newClass, ClassNode _oldClass) {
		
		List<MethodCallsDiff> methodCallsDiffs = new ArrayList<MethodCallsDiff>();

		switch (_newClass.getChangeType()) {
		case ADD: {
			Map<String,MethodDeclaration> methodDeclarations = extractMethodDeclarations(_newClass);
			for (String methodName: methodDeclarations.keySet()) {
				MethodCallsDiff mDiff = new MethodCallsDiff();
				mDiff.setMethodName(methodName);
				mDiff.setNewMethodCalls(extractInvokedMethods(methodDeclarations.get(methodName)));
				methodCallsDiffs.add(mDiff);
			}
		}
			break;

		case MODIFY: {
			// Extract functions from class in order to see if there is any
			// modification on it
			Map<String,MethodDeclaration> methodDeclarations = extractMethodDeclarations(_newClass);
			Map<String,MethodDeclaration> oldMethodDeclarations = extractMethodDeclarations(_oldClass);
			for (String methodName: methodDeclarations.keySet()) {
				MethodCallsDiff mDiff = new MethodCallsDiff();
				mDiff.setMethodName(methodName);
				mDiff.setNewMethodCalls(extractInvokedMethods(methodDeclarations.get(methodName)));
				MethodDeclaration oldMethodDeclaration = oldMethodDeclarations.get(methodName);
				if(oldMethodDeclaration != null){
					mDiff.setOldMethodCalls(extractInvokedMethods(oldMethodDeclaration));
				}
				methodCallsDiffs.add(mDiff);
			}
		}
			break;

		default:

		}
		return methodCallsDiffs;
	}
	
	/*
	private static List<FunctionNode> FindFunctionsDiff(Map<String,FunctionNode> oldFunctions,
			Map<String,FunctionNode> newFunctions, ClassNode _oldClass, ClassNode _newClass) {
		
		List<FunctionNode> _res = new ArrayList<FunctionNode>();
		
		// Compare each Function
		for (FunctionNode _newFunction : newFunctions.values()){
			
			// Try to find if the function exists previously
			FunctionNode _oldFunction = oldFunctions.get(_newFunction.name);
			
			if (_oldFunction != null){
				
				// Get the function's body to see if they are different
				String _newFunctionBody = _newClass.getData().substring(
						_newFunction.lineStart-1, _newFunction.lineEnd-1);
				String _oldFunctionBody = _oldClass.getData().substring(
						_oldFunction.lineStart-1, _oldFunction.lineEnd-1);
				
				if (_newFunctionBody.compareTo(_oldFunctionBody) != 0){
					_newFunction.changeType = ChangeType.MODIFY;
					
					_res.add(_newFunction);
				}
			} else { // The old file does not have this class (NEW)
				_newFunction.changeType = ChangeType.ADD;
				_res.add(_newFunction);
			}
			
		}
		
		return _res;
	}
	*/
	public void Debug(){
		System.out.println("------Name: " + name);
		System.out.println("--------LineStart: " + lineStart);
		System.out.println("--------LineEnd: " + lineEnd);
		System.out.println("--------ChangeType: " + changeType.toString());
	}
	/*
	private List<MethodInvocation> parseMethodInvocations(String methodBody){
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(methodBody.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
		parser.setCompilerOptions(options);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		
		MethodInvocationVisitor methodInvocationVisitor = new MethodInvocationVisitor();
		cu.accept(methodInvocationVisitor);
		return methodInvocationVisitor.getMethods();
	}*/
}
