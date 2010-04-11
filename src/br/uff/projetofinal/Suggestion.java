package br.uff.projetofinal;

import java.util.ArrayList;
import java.util.Collection;

public class Suggestion implements Comparable<Suggestion>
{
    private double support;
    private double confidence;
    
    private Collection<String> methods = new ArrayList<String>();

    public Suggestion(Collection<String> methods, double support, double confidence)
    {
        this.methods = methods;
        this.support = support;
        this.confidence = confidence;
    }
    public double getSupport()
    {
        return support;
    }

    public void setSupport(double support)
    {
        this.support = support;
    }

    public double getConfidence()
    {
        return confidence;
    }

    public void setConfidence(double confidence)
    {
        this.confidence = confidence;
    }

    public Collection<String> getMethods()
    {
        return methods;
    }

    public void setMethods(Collection<String> methods)
    {
        this.methods = methods;
    }

    @Override
    public int compareTo(Suggestion o)
    {
        if(support > o.getSupport())
            return 1;
        else
            return -1;
    }
    
}