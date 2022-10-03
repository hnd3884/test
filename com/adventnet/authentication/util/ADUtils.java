package com.adventnet.authentication.util;

import java.util.logging.Level;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

public class ADUtils
{
    private Logger logger;
    
    public ADUtils() {
        this.logger = Logger.getLogger(ADUtils.class.getName());
    }
    
    public native String[] getDomains();
    
    public native String[] getDomainUsers(final String p0, final String p1, final String p2);
    
    public native String[] getComputersInDomain(final String p0, final String p1, final String p2, final String p3);
    
    public native Hashtable discoverWorkStation(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native Hashtable getProgramsInformation(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native String[] getADUsers(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5) throws Exception;
    
    public native String[] getComputersInOU(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5) throws Exception;
    
    public native String[] getADComputers(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native String getRootDNC(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native boolean authenticateUser(final String p0, final String p1, final String p2, final String p3, final boolean p4) throws Exception;
    
    public native ArrayList getOUsInDomain(final String p0, final String p1, final String p2, final String p3, final boolean p4);
    
    public native Properties getSysInfoFromADServer(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native int installStartDiscoveryAgent(final String p0, final String p1, final String p2, final String p3) throws Exception;
    
    public native Properties getADUserInfo(final String p0, final String p1, final String p2, final String p3, final String p4, final ArrayList p5, final boolean p6) throws Exception;
    
    public native ArrayList getUserGroupsInDomain(final String p0, final String p1, final String p2, final String p3, final boolean p4);
    
    public native ArrayList getUsersFromGroup(final String p0, final String p1, final String p2, final String p3, final String p4, final boolean p5, final boolean p6);
    
    public void agentLog(final String str) {
        this.logger.log(Level.INFO, "Log message from Agent : {0}", str);
    }
    
    public static Properties getADUserProps(final String domain, final String dc, final String userName, final String pword, final boolean ssl) {
        try {
            final ArrayList list = new ArrayList();
            list.add("displayName");
            list.add("mail");
            list.add("telephoneNumber");
            list.add("mobile");
            list.add("department");
            list.add("title");
            list.add("sAMAccountName");
            list.add("manager");
            list.add("accountExpires");
            list.add("streetAddress");
            list.add("badPasswordTime");
            list.add("badPwdCount");
            list.add("codePage");
            list.add("info");
            list.add("cn");
            list.add("company");
            list.add("countryCode");
            list.add("c");
            list.add("description");
            list.add("facsimileTelephoneNumber");
            list.add("givenName");
            list.add("initials");
            list.add("instanceType");
            list.add("lastLogoff");
            list.add("lastLogon");
            list.add("l");
            list.add("logonCount");
            list.add("distinguishedName");
            list.add("objectCategory");
            list.add("objectClass");
            list.add("objectGUID");
            list.add("objectSid");
            list.add("homePhone");
            list.add("ipPhone");
            list.add("pager");
            list.add("physicalDeliveryOfficeName");
            list.add("postOfficeBox");
            list.add("postalCode");
            list.add("primaryGroupID");
            list.add("pwdLastSet");
            list.add("name");
            list.add("sAMAccountType");
            list.add("st");
            list.add("sn");
            list.add("co");
            list.add("userAccountControl");
            list.add("userPrincipalName");
            list.add("uSNChanged");
            list.add("uSNCreated");
            list.add("whenChanged");
            list.add("whenCreated");
            list.add("wWWHomePage");
            list.add("ADsPath");
            final ADUtils wdUtil = new ADUtils();
            final Properties userProp = wdUtil.getADUserInfo(domain, dc, userName, userName, pword, list, ssl);
            System.out.println("userProp " + userProp);
            return userProp;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    static {
        if (System.getProperty("os.name").indexOf("Windows") != -1) {
            System.loadLibrary("ADAuth");
            System.loadLibrary("ADAuthWmi");
        }
    }
}
