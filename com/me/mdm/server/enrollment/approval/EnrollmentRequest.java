package com.me.mdm.server.enrollment.approval;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class EnrollmentRequest
{
    Long customerID;
    public User user;
    public Device device;
    
    public EnrollmentRequest(final JSONObject json) {
        try {
            this.user = new User(String.valueOf(json.get("DomainName")), String.valueOf(json.get("UserName")), json.optString("EmailAddress"), json.optString("ADPassword"));
            this.customerID = json.getLong("CustomerID");
        }
        catch (final Exception ex) {
            Logger.getLogger(EnrollmentRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    class User
    {
        String domainName;
        String userName;
        String emailAddress;
        String password;
        
        public User(final String domainName, final String userName, final String emailAddress, final String password) {
            this.domainName = domainName;
            this.userName = userName;
            this.emailAddress = emailAddress;
            this.password = password;
        }
    }
    
    class Device
    {
        String imei;
        String serialNumber;
    }
}
