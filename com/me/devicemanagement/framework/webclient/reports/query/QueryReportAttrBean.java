package com.me.devicemanagement.framework.webclient.reports.query;

import java.util.ArrayList;
import java.io.Serializable;

public class QueryReportAttrBean implements Serializable
{
    public ArrayList<ArrayList> dataList;
    public String queryVal;
    public String queryNameVal;
    public String runTypeVal;
    public String[] navigationList;
    public String sqlError;
    public String dataPerPage;
    public boolean flag;
    public int range;
    public int totalRecord;
    
    public QueryReportAttrBean() {
        this.dataList = new ArrayList<ArrayList>();
        this.queryVal = null;
        this.queryNameVal = null;
        this.runTypeVal = "runandsave";
        this.navigationList = new String[3];
        this.sqlError = "";
        this.dataPerPage = "100";
        this.flag = true;
        this.range = 0;
        this.totalRecord = 0;
    }
    
    public String getQueryVal() {
        return this.queryVal;
    }
    
    public String getQueryNameVal() {
        return this.queryNameVal;
    }
    
    public String getDataPerPageVal() {
        return this.dataPerPage;
    }
    
    public String getSqlError() {
        return this.sqlError;
    }
    
    public String[] getNavigateString() {
        return this.navigationList;
    }
    
    public ArrayList getDataList() {
        return this.dataList;
    }
    
    public String getRunType() {
        return this.runTypeVal;
    }
    
    public int getRangeVal() {
        return this.range;
    }
    
    public int getTotalRecord() {
        return this.totalRecord;
    }
    
    public void reSetValues() {
        this.dataList.clear();
        this.sqlError = "";
        this.queryVal = "";
        this.queryNameVal = "";
        this.dataPerPage = "100";
        this.navigationList[0] = "false";
        this.navigationList[1] = "1-" + this.dataPerPage;
        this.navigationList[2] = "true";
        this.range = 0;
        this.totalRecord = 0;
    }
    
    public void setQueryVal(final String query) {
        this.queryVal = query;
    }
    
    public void setDataPerPageVal(final String datePerPageStr) {
        this.dataPerPage = datePerPageStr;
    }
    
    public void setQueryNameVal(final String queryName) {
        this.queryNameVal = queryName;
    }
    
    public void setRunTypeVal(final String runType) {
        this.runTypeVal = runType;
    }
    
    public void setNavigationList(final String[] navList) {
        this.navigationList = navList;
    }
    
    public void setRangeVal(final int rang) {
        this.range = rang;
    }
    
    public void setTotalRecord(final int totalRec) {
        this.totalRecord = totalRec;
    }
    
    public void setSqlError(final String sqlEr) {
        this.sqlError = sqlEr;
    }
}
