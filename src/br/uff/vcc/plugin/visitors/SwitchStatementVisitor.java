package br.uff.vcc.plugin.visitors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.SwitchStatement;

public class SwitchStatementVisitor extends ASTVisitor {
	List<SwitchStatement> statements = new ArrayList<SwitchStatement>();

	@Override
	public boolean visit(SwitchStatement node) {
		statements.add(node);
		return super.visit(node);
	}

	public List<SwitchStatement> getStatements() {
		return statements;
	}
}

