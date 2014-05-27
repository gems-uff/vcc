package br.uff.vcc.entity;

import java.io.Serializable;
import java.util.HashMap;

public class MethodCallNode implements Serializable
{
    private static final long               serialVersionUID = -3961267160887082047L;

    private String                          methodSignature;

    private double[]                        confidences;

    private int                             maxTreeDepth     = 0;

    private HashMap<String, MethodCallNode> methodChildren;

    private MethodCallNode                  parentNode;

    public MethodCallNode(String methodSignature, double[] confidences, MethodCallNode parentNode)
    {
        this.methodSignature = methodSignature;
        if (confidences != null)
            this.confidences = confidences;
        else
            this.confidences = new double[0];
        this.methodChildren = new HashMap<String, MethodCallNode>();
        this.parentNode = parentNode;
    }

    public String getMethodSignature()
    {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature)
    {
        this.methodSignature = methodSignature;
    }

    public double[] getConfidences()
    {
        return confidences;
    }

    public void setConfidences(double confidences[])
    {
        this.confidences = confidences;
    }

    public HashMap<String, MethodCallNode> getMethodChildren()
    {
        return methodChildren;
    }

    public void addChild(MethodCallNode methodChild)
    {
        this.methodChildren.put(methodChild.getMethodSignature(), methodChild);
    }

    public int getDepth()
    {
        return confidences.length;
    }

    @Override
    public boolean equals(Object arg0)
    {
        if (methodSignature.equals(((MethodCallNode) arg0).getMethodSignature()))
            return true;
        else
            return false;
    }
    
    @Override
    public int hashCode() {
    	return methodSignature.hashCode();
    }

    public MethodCallNode getParentNode()
    {
        return parentNode;
    }

    public void setParentNode(MethodCallNode parentNode)
    {
        this.parentNode = parentNode;
    }

    public double getSupport()
    {
        if (confidences.length == 0)
            return 1;
        return confidences[0];
    }

    public Integer getMaxTreeDepth()
    {
        return maxTreeDepth;
    }

    public void setMaxTreeDepth(Integer maxTreeDepth)
    {
        this.maxTreeDepth = maxTreeDepth;
    }
}