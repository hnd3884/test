package com.me.devicemanagement.framework.webclient.schedulereport;

import java.util.LinkedHashMap;
import com.me.devicemanagement.framework.server.util.ResponseStatusBean;
import javax.xml.bind.annotation.XmlRootElement;
import com.me.devicemanagement.framework.server.scheduler.SchedulerBean;

@XmlRootElement
public class ScheduleReportBean extends SchedulerBean
{
    Integer scheduleBackupHistoryPeriod;
    Boolean scheduleBackupStatus;
    ResponseStatusBean status;
    String onceTime;
    Boolean isTimeExpired;
    Boolean isReportNameAlreadyExist;
    Integer reportCategoryId;
    String reportTypeName;
    String reportName;
    String jsonArrayOfReportsList;
    String description;
    Integer reportFormat;
    Integer deliveryFormat;
    String reportList;
    String sender;
    String subject;
    String content;
    String cutomerId;
    Integer attachLimit;
    Boolean attachLimitFlag;
    String criteriaColsList;
    Long taskId;
    String subCategoryId;
    Integer categoryId;
    Long[] tasksList;
    LinkedHashMap reportMap;
    
    public LinkedHashMap getReportMap() {
        return this.reportMap;
    }
    
    public void setReportMap(final LinkedHashMap reportMap) {
        this.reportMap = reportMap;
    }
    
    public Long[] getTasksList() {
        return this.tasksList;
    }
    
    public void setTasksList(final Long[] tasksList) {
        this.tasksList = tasksList;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public Integer getReportFormat() {
        return this.reportFormat;
    }
    
    public void setReportFormat(final Integer reportFormat) {
        this.reportFormat = reportFormat;
    }
    
    public Integer getDeliveryFormat() {
        return this.deliveryFormat;
    }
    
    public void setDeliveryFormat(final Integer deliveryFormat) {
        this.deliveryFormat = deliveryFormat;
    }
    
    public String getReportList() {
        return this.reportList;
    }
    
    public void setReportList(final String reportList) {
        this.reportList = reportList;
    }
    
    public String getSender() {
        return this.sender;
    }
    
    public void setSender(final String sender) {
        this.sender = sender;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    public Integer getAttachLimit() {
        return this.attachLimit;
    }
    
    public void setAttachLimit(final Integer attachLimit) {
        this.attachLimit = attachLimit;
    }
    
    public Boolean getAttachLimitFlag() {
        return this.attachLimitFlag;
    }
    
    public void setAttachLimitFlag(final Boolean attachLimitFlag) {
        this.attachLimitFlag = attachLimitFlag;
    }
    
    public String getCriteriaColsList() {
        return this.criteriaColsList;
    }
    
    public void setCriteriaColsList(final String criteriaColsList) {
        this.criteriaColsList = criteriaColsList;
    }
    
    public Long getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Long taskId) {
        this.taskId = taskId;
    }
    
    public String getJsonArrayOfReportsList() {
        return this.jsonArrayOfReportsList;
    }
    
    public void setJsonArrayOfReportsList(final String jsonArrayOfReportsList) {
        this.jsonArrayOfReportsList = jsonArrayOfReportsList;
    }
    
    public Boolean getIsReportNameAlreadyExist() {
        return this.isReportNameAlreadyExist;
    }
    
    public void setIsReportNameAlreadyExist(final Boolean reportNameAlreadyExist) {
        this.isReportNameAlreadyExist = reportNameAlreadyExist;
    }
    
    public String getReportTypeName() {
        return this.reportTypeName;
    }
    
    public void setReportTypeName(final String reportTypeName) {
        this.reportTypeName = reportTypeName;
    }
    
    public Integer getReportCategoryId() {
        return this.reportCategoryId;
    }
    
    public String getReportName() {
        return this.reportName;
    }
    
    public void setReportName(final String reportName) {
        this.reportName = reportName;
    }
    
    public void setReportCategoryId(final Integer reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }
    
    public Integer getScheduleBackupHistoryPeriod() {
        return this.scheduleBackupHistoryPeriod;
    }
    
    public Boolean getScheduleBackupStatus() {
        return this.scheduleBackupStatus;
    }
    
    public ResponseStatusBean getResponseStatus() {
        return this.status;
    }
    
    public String getOnceTime() {
        return this.onceTime;
    }
    
    public Boolean getIsTimeExpired() {
        return this.isTimeExpired;
    }
    
    public void setScheduleBackupHistoryPeriod(final Integer historyPeriod) {
        this.scheduleBackupHistoryPeriod = historyPeriod;
    }
    
    public void setScheduleBackupStatus(final Boolean enableStatus) {
        this.scheduleBackupStatus = enableStatus;
    }
    
    public void setResponseStatus(final ResponseStatusBean status) {
        this.status = status;
    }
    
    public void setOnceTime(final String onceTime) {
        this.onceTime = onceTime;
    }
    
    public void setIsTimeExpired(final Boolean isTimeExpired) {
        this.isTimeExpired = isTimeExpired;
    }
    
    public String getSubCategoryId() {
        return this.subCategoryId;
    }
    
    public void setSubCategoryId(final String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }
    
    public Integer getCategoryId() {
        return this.categoryId;
    }
    
    public void setCategoryId(final Integer categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCutomerId() {
        return this.cutomerId;
    }
    
    public void setCutomerId(final String cutomerId) {
        this.cutomerId = cutomerId;
    }
}
