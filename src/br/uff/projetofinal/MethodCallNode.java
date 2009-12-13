package br.uff.projetofinal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MethodCallNode
{
    private String              methodSignature;

    private double[]            confidences;

    private HashMap<String, MethodCallNode> methodChildren;

    private MethodCallNode      parentNode;
    
    //private double              support;

    public MethodCallNode(String methodSignature, double[] confidences, MethodCallNode parentNode)
    {
        this.methodSignature = methodSignature;
        if(confidences != null)
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
        if(confidences.length == 0)
            return 1;
        return confidences[0];
    }
}