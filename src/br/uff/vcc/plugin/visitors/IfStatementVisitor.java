package br.uff.vcc.plugin.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IfStatement;

public class IfStatementVisitor extends ASTVisitor {
	List<IfStatement> statements = new ArrayList<IfStatement>();

	@Override
	public boolean visit(IfStatement node) {
		statements.add(node);
		return super.visit(node);
	}

	public List<IfStatement> getStatements() {
		return statements;
	}
}

