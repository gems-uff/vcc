package br.uff.vcc.util;

import java.util.ArrayList;
import java.util.Collection;

public class Suggestion implements Comparable<Suggestion>
{
    private double             support;

    private double             confidence;

    private Collection<String> invocatedMethods = new ArrayList<String>();

    private Collection<String> suggestedMethods = new ArrayList<String>();

    public Suggestion(Collection<String> invocatedMethods, Collection<String> suggestedMethods, double support, double confidence)
    {
        this.invocatedMethods = invocatedMethods;
        this.suggestedMethods = suggestedMethods;
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

    public Collection<String> getSuggestedMethods()
    {
        return suggestedMethods;
    }

    public void setSuggestedMethods(Collection<String> suggestedMethods)
    {
        this.suggestedMethods = suggestedMethods;
    }

    @Override
    public int compareTo(Suggestion o)
    {
        if (confidence > o.getConfidence())
            return -1;
        else
            return 1;
    }

    public Collection<String> getInvocatedMethods()
    {
        return invocatedMethods;
    }

    public void setInvocatedMethods(Collection<String> invocatedMethods)
    {
        this.invocatedMethods = invocatedMethods;
    }

}