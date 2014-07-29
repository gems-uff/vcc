package br.uff.vcc.exp.git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import br.uff.vcc.exp.entity.MethodCallsDiff;
import br.uff.vcc.plugin.handlers.GenerateTreeHandler;

public class FunctionNode {

	private String eclipseProjectName;
	private String unitName;
	
	public FunctionNode(String eclipseProjectName, String unitName) {
		super();
		this.eclipseProjectName = eclipseProjectName;
		this.unitName = unitName;
	}

	public List<MethodCallsDiff> extractMethodCallsDiff(String newData, String oldData) {
		
		List<MethodCallsDiff> methodCallsDiffs = new ArrayList<MethodCallsDiff>();

		if(oldData == null){//ADD
			Map<String,MethodDeclaration> methodDeclarations = extractMethodDeclarations(newData);
			for (String methodName: methodDeclarations.keySet()) {
				MethodCallsDiff mDiff = new MethodCallsDiff();
				mDiff.setMethodName(methodName);
				mDiff.setNewMethodCalls(extractInvokedMethods(methodDeclarations.get(methodName)));
				methodCallsDiffs.add(mDiff);
			}
		}else{//MODIFY
			// Extract functions from class in order to see if there is any
			// modification on it
			Map<String,MethodDeclaration> methodDeclarations = extractMethodDeclarations(newData);
			Map<String,MethodDeclaration> oldMethodDeclarations = extractMethodDeclarations(oldData);
			for (String methodName: methodDeclarations.keySet()) {
				MethodCallsDiff mDiff = new MethodCallsDiff();
				mDiff.setMethodName(methodName);
				MethodDeclaration oldMethodDeclaration = oldMethodDeclarations.get(methodName);
				MethodDeclaration newMethodDeclaration = methodDeclarations.get(methodName);
				mDiff.setNewMethodCalls(extractInvokedMethods(newMethodDeclaration));
				//Don't analyze empty methods
				if(mDiff.getNewMethodCalls().size() == 0){
					continue;
				}
				if(oldMethodDeclaration != null && oldMethodDeclaration.getBody() != null){
					//Don't analyze unchanged methods
					if(newMethodDeclaration.getBody().toString().equals(oldMethodDeclaration.getBody().toString())){
						continue;
					}
					mDiff.setOldMethodCalls(extractInvokedMethods(oldMethodDeclaration));
					if(mDiff.getNewMethodCalls().equals(mDiff.getOldMethodCalls())){
						continue;
					}
				}
				methodCallsDiffs.add(mDiff);
			}
		}

		return methodCallsDiffs;
	}

	private Map<String,MethodDeclaration> extractMethodDeclarations(String newData){
		final Map<String,MethodDeclaration> methods = new HashMap<String, MethodDeclaration>();
		
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(newData.toCharArray());
		parser.setResolveBindings(true);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		Hashtable<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.DISABLED);
		parser.setCompilerOptions(options);
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		IProject parsedProject = null;
		for (IProject project : projects) {
			if (project.getName().equals(eclipseProjectName)){
				parsedProject = project;
				break;
			}
		}
		parser.setProject(JavaCore.create(parsedProject));
		parser.setUnitName(unitName);
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

		cu.accept(new ASTVisitor() {
			
			public boolean visit(MethodDeclaration node){
				String methodName = "";
				try {
					IMethodBinding imb = node.resolveBinding();
					if(imb == null){
						methodName = node.getName().toString();
					}else{
						methodName = GenerateTreeHandler.getCompleteMethodName(imb);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				methods.put(methodName, node);
				return false;
			}
		});

		
		return methods;
		
	}
	
	private List<String> extractInvokedMethods(MethodDeclaration methodDeclaration){
		final List<String> methodInvocatons = new ArrayList<String >();
		
		if(methodDeclaration.getBody() == null){
			return methodInvocatons;
		}
		
		methodDeclaration.getBody().accept(new ASTVisitor() {
			
			public boolean visit(MethodInvocation node){
				String methodName = "";
				try {
					IMethodBinding imb = node.resolveMethodBinding();
					if(imb == null){
						methodName = node.getName().toString();
					}else{
						methodName = GenerateTreeHandler.getCompleteMethodName(imb);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				methodInvocatons.add(methodName);
				return false;
			}
		});

		
		return methodInvocatons;
		
	}
}
