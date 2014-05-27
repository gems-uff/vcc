package br.uff.vcc.plugin.visitors.entity;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.SwitchStatement;

public class CustomSwitchStatement {
	
	private SwitchStatement switchStatement;

	private List<SplittedCaseStatement> caseStatements;

	public CustomSwitchStatement(){
		caseStatements = new ArrayList<SplittedCaseStatement>();
	}
	
	public List<SplittedCaseStatement> getCaseStatements() {
		return caseStatements;
	}

	public void setCaseStatements(List<SplittedCaseStatement> caseStatements) {
		this.caseStatements = caseStatements;
	}

	public SwitchStatement getSwitchStatement() {
		return switchStatement;
	}

	public void setSwitchStatement(SwitchStatement switchStatement) {
		this.switchStatement = switchStatement;
	}
}
