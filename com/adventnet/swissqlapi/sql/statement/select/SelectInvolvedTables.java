package com.adventnet.swissqlapi.sql.statement.select;

import java.util.HashSet;
import java.util.Set;

public class SelectInvolvedTables
{
    public Set<String> involvedTables;
    public boolean isNeeded;
    
    public SelectInvolvedTables() {
        this.involvedTables = new HashSet<String>();
        this.isNeeded = false;
    }
}
