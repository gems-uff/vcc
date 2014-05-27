package br.uff.vcc.plugin.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TryStatement;

public class TryCatchStatementVisitor extends ASTVisitor {
	List<TryStatement> statements = new ArrayList<TryStatement>();

	@Override
	public boolean visit(TryStatement node) {
		statements.add(node);
		return super.visit(node);
	}

	public List<TryStatement> getStatements() {
		return statements;
	}
}

