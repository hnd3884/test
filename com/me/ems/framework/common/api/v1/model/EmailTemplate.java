package com.me.ems.framework.common.api.v1.model;

import java.util.List;

public class EmailTemplate
{
    Long alertID;
    String title;
    String subject;
    String description;
    boolean isAlertReconfigured;
    List<EmailTemplateKeys> templateKeys;
    
    public Long getAlertID() {
        return this.alertID;
    }
    
    public void setAlertID(final Long alertID) {
        this.alertID = alertID;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getSubject() {
        return this.subject;
    }
    
    public void setSubject(final String subject) {
        this.subject = subject;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public boolean isAlertReconfigured() {
        return this.isAlertReconfigured;
    }
    
    public void setAlertReconfigured(final boolean alertReconfigured) {
        this.isAlertReconfigured = alertReconfigured;
    }
    
    public List<EmailTemplateKeys> getTemplateKeys() {
        return this.templateKeys;
    }
    
    public void setTemplateKeys(final List<EmailTemplateKeys> templateKeys) {
        this.templateKeys = templateKeys;
    }
}
