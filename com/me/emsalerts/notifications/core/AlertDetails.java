package com.me.emsalerts.notifications.core;

import java.util.Properties;

public class AlertDetails
{
    public Long eventCode;
    public Long customerID;
    public Long technicianID;
    public Long mediumID;
    public boolean isHelpDeskMode;
    public Properties alertProps;
    
    public AlertDetails(final Long inputEventCode, final Long inputCustomerID, final Long inputTechnicianID, final boolean inputIsHelpDeskMode) {
        this.eventCode = -1L;
        this.customerID = -1L;
        this.technicianID = -1L;
        this.mediumID = -1L;
        this.isHelpDeskMode = false;
        this.alertProps = null;
        this.eventCode = inputEventCode;
        this.customerID = inputCustomerID;
        this.technicianID = inputTechnicianID;
        this.isHelpDeskMode = inputIsHelpDeskMode;
    }
}
