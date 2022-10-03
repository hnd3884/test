package com.adventnet.tree.query;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;

public interface TreeQuery extends SelectQuery, Cloneable
{
    Row getStartingParentKey();
    
    void compile(final DataObject p0) throws QueryConstructionException, MetaDataException, DataAccessException;
    
    boolean isCompiled();
    
    DataObject getTreeDefinition();
    
    void setTreeDefinition(final DataObject p0);
    
    Row getTreeIdentifier();
    
    String getTreeType();
    
    int getDepth();
    
    SelectQuery getSelectQuery(final String p0);
    
    void setSelectQuery(final SelectQuery p0);
    
    TreeQuery getTreeQuery(final Row p0);
}
