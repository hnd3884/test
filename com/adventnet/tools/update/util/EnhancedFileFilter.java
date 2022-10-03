package com.adventnet.tools.update.util;

import java.util.Properties;

public class EnhancedFileFilter
{
    private Criteria crit;
    private String[] filterCrit;
    private String[] action;
    private Properties critProps;
    
    public EnhancedFileFilter() {
        this.crit = null;
        this.filterCrit = null;
        this.action = null;
        this.critProps = null;
    }
    
    public void setCriteria(final Criteria crit) {
        if (crit != null) {
            this.crit = crit;
            this.filterCrit = crit.getCriteriaList();
            this.action = crit.getActionList();
            this.critProps = crit.getProperties();
        }
    }
    
    public Criteria getCriteria() {
        return this.crit;
    }
    
    public boolean accept(final String fileToFilter) {
        boolean retValue = true;
        if (this.crit == null) {
            return true;
        }
        final int noOfCrit = this.filterCrit.length;
        boolean matched = false;
        for (int i = 0; i < noOfCrit; ++i) {
            final String crit = this.filterCrit[i];
            final int index = crit.indexOf("*");
            if (index == -1) {
                matched = fileToFilter.equals(crit);
            }
            else {
                matched = (fileToFilter.startsWith(crit.substring(0, index)) && fileToFilter.endsWith(crit.substring(index + 1, crit.length())));
            }
            if (matched) {
                retValue = this.getReturnValue(this.action[i]);
                break;
            }
        }
        this.log("Filtering : [ " + fileToFilter + " ] - " + retValue);
        return retValue;
    }
    
    private boolean getReturnValue(final String action) {
        if (action.equals("ACCEPT")) {
            return true;
        }
        if (action.equals("REJECT")) {
            return false;
        }
        System.err.println("UNKNOWN ACTION [ " + action + " ] SPECIFIED");
        return false;
    }
    
    private void log(final String message) {
    }
}
