package com.me.devicemanagement.framework.webclient.alert;

public class EmailTemplateChangeEvent
{
    public Long customerId;
    public Long technicianId;
    public Long alertConstant;
    
    private EmailTemplateChangeEvent() {
    }
    
    public EmailTemplateChangeEvent(final Long customerId, final Long technicianId, final Long alertConstant) {
        this.customerId = customerId;
        this.technicianId = technicianId;
        this.alertConstant = alertConstant;
    }
}
