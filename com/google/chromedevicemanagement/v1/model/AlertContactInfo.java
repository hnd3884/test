package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class AlertContactInfo extends GenericJson
{
    @Key
    private List<String> alertingEmails;
    @Key
    private List<String> alertingMobilePhones;
    
    public List<String> getAlertingEmails() {
        return this.alertingEmails;
    }
    
    public AlertContactInfo setAlertingEmails(final List<String> alertingEmails) {
        this.alertingEmails = alertingEmails;
        return this;
    }
    
    public List<String> getAlertingMobilePhones() {
        return this.alertingMobilePhones;
    }
    
    public AlertContactInfo setAlertingMobilePhones(final List<String> alertingMobilePhones) {
        this.alertingMobilePhones = alertingMobilePhones;
        return this;
    }
    
    public AlertContactInfo set(final String s, final Object o) {
        return (AlertContactInfo)super.set(s, o);
    }
    
    public AlertContactInfo clone() {
        return (AlertContactInfo)super.clone();
    }
}
