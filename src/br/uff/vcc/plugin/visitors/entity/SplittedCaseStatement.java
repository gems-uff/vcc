package br.uff.vcc.plugin.visitors.entity;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Statement;

public class SplittedCaseStatement {

	private List<Statement> statements;
	
	public SplittedCaseStatement(){
		statements = new ArrayList<Statement>();
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}
	
}
