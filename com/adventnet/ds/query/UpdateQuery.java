package com.adventnet.ds.query;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

public interface UpdateQuery extends Serializable, Cloneable
{
    String getTableName();
    
    Criteria getCriteria();
    
    void setCriteria(final Criteria p0);
    
    Map getUpdateColumns();
    
    void setUpdateColumn(final String p0, final Object p1);
    
    void addJoin(final Join p0);
    
    List<Join> getJoins();
    
    List<Table> getTableList();
    
    Object clone();
}
