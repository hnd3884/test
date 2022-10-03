package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class OptionalHintClause
{
    private String option;
    private ArrayList queryHintList;
    
    public OptionalHintClause() {
        this.queryHintList = new ArrayList();
    }
    
    public void setOption(final String s) {
        this.option = s;
    }
    
    public void setQueryHintList(final ArrayList list) {
        this.queryHintList = list;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(this.option);
        for (int i = 0; i < this.queryHintList.size(); ++i) {
            stringbuffer.append(" " + this.queryHintList.get(i));
        }
        return stringbuffer.toString();
    }
}
