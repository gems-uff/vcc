package br.uff.vcc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Suggestion implements Comparable<Suggestion>
{
    private double             support;

    private double             confidence;

    private Collection<String> invocatedMethods = new ArrayList<String>();

    private List<String> suggestedMethods = new ArrayList<String>();

    public Suggestion(Collection<String> invocatedMethods, List<String> suggestedMethods, double support, double confidence)
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

    public List<String> getSuggestedMethods()
    {
        return suggestedMethods;
    }

    public void setSuggestedMethods(List<String> suggestedMethods)
    {
        this.suggestedMethods = suggestedMethods;
    }

    @Override
    public int compareTo(Suggestion o)
    {
        if (confidence > o.getConfidence()){
            return -1;
        } else if(confidence == o.getConfidence()){
        	return (suggestedMethods.size() < o.getSuggestedMethods().size() ? -1 : 1);
        } else{
            return 1;
        }
    }

    public Collection<String> getInvocatedMethods()
    {
        return invocatedMethods;
    }

    public void setInvocatedMethods(Collection<String> invocatedMethods)
    {
        this.invocatedMethods = invocatedMethods;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((invocatedMethods == null) ? 0 : invocatedMethods.hashCode());
		result = prime
				* result
				+ ((suggestedMethods == null) ? 0 : suggestedMethods.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Suggestion other = (Suggestion) obj;
		if (invocatedMethods == null) {
			if (other.invocatedMethods != null)
				return false;
		} else if (!invocatedMethods.equals(other.invocatedMethods))
			return false;
		if (suggestedMethods == null) {
			if (other.suggestedMethods != null)
				return false;
		} else if (!suggestedMethods.equals(other.suggestedMethods))
			return false;
		return true;
	}
}