package com.me.devicemanagement.onpremise.server.sdp;

import java.util.Properties;

public class Ticket
{
    public String principal;
    public String domainName;
    public String roles;
    public String ticket;
    public String ipaddress;
    public Properties properties;
    
    public Ticket() {
        this.principal = null;
        this.domainName = null;
        this.roles = "";
    }
    
    @Override
    public String toString() {
        return "Principal = " + this.principal + "\n\tDomain Name = " + this.domainName + "\n\tRoles = " + this.roles + "\n\tticket = " + this.ticket + "IPaddress:" + this.ipaddress + " Properties : " + this.properties;
    }
}
