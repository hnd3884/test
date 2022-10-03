package com.adventnet.sym.server.mdm.config;

import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import java.util.List;

public class MDMConfigQuery
{
    private List<Integer> configIds;
    private List<Join> configJoins;
    private Criteria configCriteria;
    private List<Column> configColumns;
    private SortColumn configSortColumn;
    
    public SortColumn getConfigSortColumn() {
        return this.configSortColumn;
    }
    
    public void setConfigSortColumn(final SortColumn configSortColumn) {
        this.configSortColumn = configSortColumn;
    }
    
    public MDMConfigQuery(final List<Integer> configIds, final Criteria criteria) {
        this.configJoins = null;
        this.configCriteria = null;
        this.configColumns = null;
        this.configSortColumn = null;
        this.configIds = configIds;
        this.configCriteria = criteria;
    }
    
    public List<Integer> getConfigIds() {
        return this.configIds;
    }
    
    public void setConfigIds(final List<Integer> configIds) {
        this.configIds = configIds;
    }
    
    public List<Join> getConfigJoins() {
        return this.configJoins;
    }
    
    public void setConfigJoins(final List<Join> configJoins) {
        this.configJoins = configJoins;
    }
    
    public Criteria getConfigCriteria() {
        return this.configCriteria;
    }
    
    public void setConfigCriteria(final Criteria configCriteria) {
        this.configCriteria = configCriteria;
    }
    
    public List<Column> getConfigColumns() {
        return this.configColumns;
    }
    
    public void setConfigColumns(final List<Column> configColumns) {
        this.configColumns = configColumns;
    }
}
