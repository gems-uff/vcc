package br.uff.vcc.exp.entity;

import java.util.List;

public class MethodCallsDiff {

	private String methodName;
	
	private List<String> oldMethodCalls;
	private List<String> newMethodCalls;
	
	
	public List<String> getOldMethodCalls() {
		return oldMethodCalls;
	}
	public void setOldMethodCalls(List<String> oldMethodCalls) {
		this.oldMethodCalls = oldMethodCalls;
	}
	public List<String> getNewMethodCalls() {
		return newMethodCalls;
	}
	public void setNewMethodCalls(List<String> newMethodCalls) {
		this.newMethodCalls = newMethodCalls;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	
	
}
