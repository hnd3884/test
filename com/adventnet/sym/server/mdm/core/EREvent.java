package com.adventnet.sym.server.mdm.core;

import java.util.Properties;

public class EREvent
{
    public String otp;
    public Long customerId;
    public String userName;
    public String email;
    public String enrollmentRequestId;
    public Properties enrollmentRequestProperties;
    public String resourceJSONString;
    
    public EREvent(final String enrollmentRequestId) {
        this.otp = null;
        this.customerId = null;
        this.userName = null;
        this.email = null;
        this.enrollmentRequestId = null;
        this.enrollmentRequestProperties = null;
        this.resourceJSONString = null;
        this.enrollmentRequestId = enrollmentRequestId;
    }
    
    public EREvent(final String enrollmentRequestId, final String otp) {
        this.otp = null;
        this.customerId = null;
        this.userName = null;
        this.email = null;
        this.enrollmentRequestId = null;
        this.enrollmentRequestProperties = null;
        this.resourceJSONString = null;
        this.enrollmentRequestId = enrollmentRequestId;
        this.otp = otp;
    }
    
    public EREvent(final Long customerId, final String userName, final String email, final String enrollmentRequestId) {
        this.otp = null;
        this.customerId = null;
        this.userName = null;
        this.email = null;
        this.enrollmentRequestId = null;
        this.enrollmentRequestProperties = null;
        this.resourceJSONString = null;
        this.customerId = customerId;
        this.userName = userName;
        this.email = email;
        this.enrollmentRequestId = enrollmentRequestId;
    }
    
    public EREvent(final Long customerId, final String userName, final String email, final Properties properties) {
        this.otp = null;
        this.customerId = null;
        this.userName = null;
        this.email = null;
        this.enrollmentRequestId = null;
        this.enrollmentRequestProperties = null;
        this.resourceJSONString = null;
        this.customerId = customerId;
        this.userName = userName;
        this.email = email;
        this.enrollmentRequestId = properties.getProperty("ENROLLMENT_REQUEST_ID", null);
        this.enrollmentRequestProperties = properties;
    }
}
