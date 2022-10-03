package com.me.devicemanagement.framework.server.customreport;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class CustomReportDetails implements Serializable
{
    public Long moduleID;
    public Long subModuleID;
    public Long userID;
    public String sessionID;
    public String reportName;
    public String reportDisplayName;
    public String reportDesc;
    public List selectColumnList;
    public List criteriaList;
    public String sortColumn;
    
    public CustomReportDetails() {
        this.selectColumnList = new ArrayList();
        this.criteriaList = new ArrayList();
    }
}
