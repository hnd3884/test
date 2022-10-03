package com.adventnet.tools.update.util;

import java.util.Properties;
import java.util.ArrayList;

public class Criteria
{
    private ArrayList critList;
    private ArrayList actionList;
    private Properties props;
    
    public Criteria() {
        this.critList = new ArrayList();
        this.actionList = new ArrayList();
        this.props = new Properties();
    }
    
    public void setProperties(final Properties props) {
        this.props = props;
    }
    
    public Properties getProperties() {
        return this.props;
    }
    
    public boolean addCriterion(final String critString, final String action) {
        if (this.isValidCriteria(critString, action)) {
            if (!this.critList.contains(critString)) {
                this.critList.add(critString);
                this.actionList.add(action);
            }
            return true;
        }
        return false;
    }
    
    public boolean isValidCriteria(final String critString, final String action) {
        return critString != null && action != null && critString.indexOf("*") == critString.lastIndexOf("*");
    }
    
    public void removeCriterion(final String critString) {
        final int index = this.critList.indexOf(critString);
        if (index != -1) {
            this.critList.remove(index);
            this.actionList.remove(index);
        }
    }
    
    public String getAction(final String critString) {
        final int index = this.critList.indexOf(critString);
        if (index != -1) {
            return this.actionList.get(index);
        }
        return null;
    }
    
    public String[] getCriteriaList() {
        return this.critList.toArray(new String[0]);
    }
    
    public String[] getActionList() {
        return this.actionList.toArray(new String[0]);
    }
    
    @Override
    public String toString() {
        String str = "Criteria - ";
        for (int i = 0; i < this.critList.size(); ++i) {
            str = str + "\n\t" + this.critList.get(i) + " : " + this.actionList.get(i);
        }
        return str;
    }
}
