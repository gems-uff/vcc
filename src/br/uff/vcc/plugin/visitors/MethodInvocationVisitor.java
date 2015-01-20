package br.uff.vcc.plugin.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.MethodInvocation;

public class MethodInvocationVisitor extends ASTVisitor {
	List<MethodInvocation> methods = new ArrayList<MethodInvocation>();

	@Override
	public boolean visit(MethodInvocation node) {
		boolean isCascadeCall = false;
		if(node!=null && node.getParent() != null){
			ASTNode parent = node.getParent();
			while(!(parent instanceof MethodInvocation) && !(parent instanceof Block) && parent != null){
				parent = parent.getParent();
			}
			if(parent != null){
				if(parent instanceof MethodInvocation){
					methods.add(methods.indexOf(parent), node);
					isCascadeCall = true;
				}
			}
		}
		
		if(!isCascadeCall){
			methods.add(node);
		}
		return super.visit(node);
	}

	public List<MethodInvocation> getMethods() {
		return methods;
	}
}

