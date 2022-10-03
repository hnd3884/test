package com.me.devicemanagement.framework.winaccess;

import java.util.Enumeration;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.logging.Logger;

public class ADObjectsConvertor
{
    private static Logger logger;
    private static String sourceClass;
    public static final int INTEGER = 1;
    public static final int LONG = 2;
    public static final int BOOLEAN = 3;
    public static final int STRING = 4;
    private Hashtable adUser;
    private Hashtable adUserPWDInfo;
    private Hashtable generalComputer;
    private Hashtable adComputer;
    private Hashtable adResource;
    private Hashtable adGroup;
    private Hashtable adContainer;
    private Hashtable adDomainPWDInfo;
    private Hashtable adGpo;
    private Hashtable adPrinter;
    
    public ADObjectsConvertor() {
        this.adUser = new Hashtable();
        this.adUserPWDInfo = new Hashtable();
        this.generalComputer = new Hashtable();
        this.adComputer = new Hashtable();
        this.adResource = new Hashtable();
        this.adGroup = new Hashtable();
        this.adContainer = new Hashtable();
        this.adDomainPWDInfo = new Hashtable();
        this.adGpo = new Hashtable();
        this.adPrinter = new Hashtable();
    }
    
    public List getUserAttrList() throws Exception {
        final String sourceMethod = "getUserAttrList";
        final List userAttr = new ArrayList();
        try {
            final Hashtable user = this.getADUserAttr();
            final Enumeration enume = user.keys();
            while (enume.hasMoreElements()) {
                userAttr.add(enume.nextElement());
            }
            userAttr.add("lastLogonTimestamp");
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADUserAttributes : " + userAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getUserAttrList() : " + ex, ex);
            throw ex;
        }
        return userAttr;
    }
    
    protected Hashtable getADUserAttr() throws Exception {
        final String sourceMethod = "getADUserAttr";
        try {
            final Object[] fullName = { null };
            final Object[] fullNameCol = { "FULL_NAME", new Integer(4) };
            fullName[0] = fullNameCol;
            this.adUser.put("sAMAccountName", fullName);
            final Object[] lastName = { null };
            final Object[] lastNameCol = { "LAST_NAME", new Integer(4) };
            lastName[0] = lastNameCol;
            this.adUser.put("sn", lastName);
            final Object[] dispName = { null };
            final Object[] dispNameCol = { "DISPLAY_NAME", new Integer(4) };
            dispName[0] = dispNameCol;
            this.adUser.put("displayName", dispName);
            final Object[] mail = { null };
            final Object[] mailCol = { "EMAIL_ADDR", new Integer(4) };
            mail[0] = mailCol;
            this.adUser.put("mail", mail);
            final Object[] st = { null };
            final Object[] stCol = { "STREET_ADDR", new Integer(4) };
            st[0] = stCol;
            this.adUser.put("streetAddress", st);
            final Object[] country = { null };
            final Object[] countryCol = { "COUNTRY", new Integer(4) };
            country[0] = countryCol;
            this.adUser.put("co", country);
            final Object[] poCode = { null };
            final Object[] poCodeCol = { "POSTAL_CODE", new Integer(4) };
            poCode[0] = poCodeCol;
            this.adUser.put("postalCode", poCode);
            final Object[] poBox = { null };
            final Object[] poBoxCol = { "PO_BOX", new Integer(4) };
            poBox[0] = poBoxCol;
            this.adUser.put("postOfficeBox", poBox);
            final Object[] state = { null };
            final Object[] stateCol = { "STATE_OR_PROVINCE", new Integer(4) };
            state[0] = stateCol;
            this.adUser.put("st", state);
            final Object[] homePhone = { null };
            final Object[] homePhoneCol = { "HOME_PHONE", new Integer(4) };
            homePhone[0] = homePhoneCol;
            this.adUser.put("homePhone", homePhone);
            final Object[] mobile = { null };
            final Object[] mobileCol = { "MOBILE", new Integer(4) };
            mobile[0] = mobileCol;
            this.adUser.put("mobile", mobile);
            final Object[] fax = { null };
            final Object[] faxCol = { "FAX", new Integer(4) };
            fax[0] = faxCol;
            this.adUser.put("facsimileTelephoneNumber", fax);
            final Object[] pager = { null };
            final Object[] pagerCol = { "PAGER", new Integer(4) };
            pager[0] = pagerCol;
            this.adUser.put("pager", pager);
            final Object[] ipPhone = { null };
            final Object[] ipPhoneCol = { "IP_PHONE", new Integer(4) };
            ipPhone[0] = ipPhoneCol;
            this.adUser.put("ipPhone", ipPhone);
            final Object[] notes = { null };
            final Object[] notesCol = { "NOTES", new Integer(4) };
            notes[0] = notesCol;
            this.adUser.put("info", notes);
            final Object[] company = { null };
            final Object[] companyCol = { "COMPANY_NAME", new Integer(4) };
            company[0] = companyCol;
            this.adUser.put("company", company);
            final Object[] title = { null };
            final Object[] titleCol = { "TITLE", new Integer(4) };
            title[0] = titleCol;
            this.adUser.put("title", title);
            final Object[] dept = { null };
            final Object[] deptCol = { "DEPT", new Integer(4) };
            dept[0] = deptCol;
            this.adUser.put("department", dept);
            final Object[] lastLogon = { null };
            final Object[] lastLogonCol = { "LAST_LOGON", new Integer(2) };
            lastLogon[0] = lastLogonCol;
            this.adUser.put("lastLogon", lastLogon);
            final Object[] script = { null };
            final Object[] scriptCol = { "LOGON_SCRIPT_PATH", new Integer(4) };
            script[0] = scriptCol;
            this.adUser.put("scriptPath", script);
            final Object[] homeDir = { null };
            final Object[] homeDirCol = { "HOME_DIRECTORY", new Integer(4) };
            homeDir[0] = homeDirCol;
            this.adUser.put("homeDirectory", homeDir);
            final Object[] sharedFolder = { null };
            final Object[] sharedFolderCol = { "SHARED_FOLDER", new Integer(4) };
            sharedFolder[0] = sharedFolderCol;
            this.adUser.put("userSharedFolder", sharedFolder);
            final Object[] profilePath = { null };
            final Object[] profilePathCol = { "PROFILE_PATH", new Integer(4) };
            profilePath[0] = profilePathCol;
            this.adUser.put("profilePath", profilePath);
            final Object[] isDialin = { null };
            final Object[] isDialinCol = { "IS_DIALIN", new Integer(3) };
            isDialin[0] = isDialinCol;
            this.adUser.put("msNPAllowDialin", isDialin);
            final Object[] accExpiryDate = { null };
            final Object[] accExpiryDateCol = { "ACCOUNT_EXPIRY_DATE", new Integer(2) };
            accExpiryDate[0] = accExpiryDateCol;
            this.adUser.put("accountExpires", accExpiryDate);
            final Object[] isDisabled = { null };
            final Object[] isDisabledCol = { "IS_DISABLED", new Integer(3) };
            isDisabled[0] = isDisabledCol;
            this.adUser.put("userAccountControl", isDisabled);
            final Object[] manager = { null };
            final Object[] managerCol = { "MANAGER", new Integer(4) };
            manager[0] = managerCol;
            this.adUser.put("manager", manager);
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Added Managed attr!!");
            final Object[] officeLoc = { null };
            final Object[] officeLocCol = { "OFFICE_LOCATION", new Integer(4) };
            officeLoc[0] = officeLocCol;
            this.adUser.put("physicalDeliveryOfficeName", officeLoc);
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Added Office Location attr!!");
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADUserAttr() : " + ex, ex);
            throw ex;
        }
        return this.adUser;
    }
    
    public List getADResourceAttrList() throws Exception {
        final String sourceMethod = "getADResourceAttrList";
        final List resourceAttr = new ArrayList();
        try {
            final Hashtable resource = this.getADResourceAttr();
            final Enumeration enume = resource.keys();
            while (enume.hasMoreElements()) {
                final String attr = enume.nextElement();
                if (attr != "type") {
                    resourceAttr.add(attr);
                }
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADResourceAttributes : " + resourceAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADResourceAttrList() : " + ex, ex);
            throw ex;
        }
        return resourceAttr;
    }
    
    protected Hashtable getADResourceAttr() throws Exception {
        final String sourceMethod = "getADResourceAttr";
        try {
            final Object[] guid = { null };
            final Object[] guidCol = { "GUID", new Integer(4) };
            guid[0] = guidCol;
            this.adResource.put("objectGUID", guid);
            final Object[] domainName = { null };
            final Object[] domainNameCol = { "DOMAIN_NAME", new Integer(4) };
            domainName[0] = domainNameCol;
            this.adResource.put("domainName", domainName);
            final Object[] createTime = { null };
            final Object[] createTimeCol = { "CREATION_TIME", new Integer(2) };
            createTime[0] = createTimeCol;
            this.adResource.put("createTimeStamp", createTime);
            final Object[] modifyTime = { null };
            final Object[] modifyTimeCol = { "MODIFIED_TIME", new Integer(2) };
            modifyTime[0] = modifyTimeCol;
            this.adResource.put("modifyTimeStamp", modifyTime);
            final Object[] dn = { null };
            final Object[] dnCol = { "DN", new Integer(4) };
            dn[0] = dnCol;
            this.adResource.put("distinguishedName", dn);
            final Object[] desc = { null };
            final Object[] descCol = { "DESCRIPTION", new Integer(4) };
            desc[0] = descCol;
            this.adResource.put("description", desc);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADResourceAttr() : " + ex, ex);
            throw ex;
        }
        return this.adResource;
    }
    
    public List getUserPWDInfoAttrList() throws Exception {
        final String sourceMethod = "getUserPWDInfoAttrList";
        final List userPWDAttr = new ArrayList();
        try {
            final Hashtable pwdHash = this.getADUserPWDInfoAttr();
            final Enumeration enume = pwdHash.keys();
            while (enume.hasMoreElements()) {
                userPWDAttr.add(enume.nextElement());
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADUserPWDAttributes : " + userPWDAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getUserPWDInfoAttrList() : " + ex, ex);
            throw ex;
        }
        return userPWDAttr;
    }
    
    protected Hashtable getADUserPWDInfoAttr() throws Exception {
        final String sourceMethod = "getADUserPWDInfoAttr";
        try {
            final Object[] pwdLastSet = { null };
            final Object[] pwdLastSetCol = { "PWD_LAST_SET_DATE", new Integer(2) };
            pwdLastSet[0] = pwdLastSetCol;
            this.adUserPWDInfo.put("pwdLastSet", pwdLastSet);
            final Object[] badPwdTime = { null };
            final Object[] badPwdTimeCol = { "BAD_PWD_TIME", new Integer(2) };
            badPwdTime[0] = badPwdTimeCol;
            this.adUserPWDInfo.put("badPasswordTime", badPwdTime);
            final Object[] badPwdCount = { null };
            final Object[] badPwdCountCol = { "BAD_PWD_COUNT", new Integer(2) };
            badPwdCount[0] = badPwdCountCol;
            this.adUserPWDInfo.put("badPwdCount", badPwdCountCol);
            final Object[] lockoutTime = { null };
            final Object[] lockoutTimeCol = { "LOCKOUT_TIME", new Integer(2) };
            lockoutTime[0] = lockoutTimeCol;
            this.adUserPWDInfo.put("lockoutTime", lockoutTime);
            final Object[] userAccControl = new Object[5];
            final Object[] isPwdExpiredCol = { "IS_PWD_EXPIRED", new Integer(3) };
            final Object[] isPwdRequiredCol = { "IS_PWD_REQUIRED", new Integer(3) };
            final Object[] isPwdNeverExpiresCol = { "IS_PWD_NEVER_EXPIRES", new Integer(3) };
            final Object[] isPwdCantChangeCol = { "IS_PWD_CANT_CHANGE", new Integer(3) };
            final Object[] isLockedCol = { "IS_LOCKED", new Integer(3) };
            userAccControl[0] = isPwdExpiredCol;
            userAccControl[1] = isPwdRequiredCol;
            userAccControl[2] = isPwdNeverExpiresCol;
            userAccControl[3] = isPwdCantChangeCol;
            userAccControl[4] = isLockedCol;
            this.adUserPWDInfo.put("userAccountControl", userAccControl);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADUserPWDInfoAttr() : " + ex, ex);
            throw ex;
        }
        return this.adUserPWDInfo;
    }
    
    public List getADPrinterAttrList() throws Exception {
        final String sourceMethod = "getADPrinterAttrList";
        final List printerAttr = new ArrayList();
        try {
            final Hashtable printer = this.getADPrinterAttr();
            final Enumeration enume = printer.keys();
            while (enume.hasMoreElements()) {
                printerAttr.add(enume.nextElement());
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "AD Printer Attributes : " + printerAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADPrinterAttrList() : " + ex, ex);
            throw ex;
        }
        return printerAttr;
    }
    
    protected Hashtable getADPrinterAttr() throws Exception {
        final String sourceMethod = "getADPrinterAttr";
        try {
            final Object[] printShareName = { null };
            final Object[] printShareNameCol = { "SHARE_NAME", new Integer(4) };
            printShareName[0] = printShareNameCol;
            this.adPrinter.put("printShareName", printShareName);
            final Object[] location = { null };
            final Object[] locationCol = { "PRINTER_LOCATION", new Integer(4) };
            location[0] = locationCol;
            this.adPrinter.put("location", location);
            final Object[] printerName = { null };
            final Object[] printerNameCol = { "MODEL", new Integer(4) };
            printerName[0] = printerNameCol;
            this.adPrinter.put("printerName", printerName);
            final Object[] driverName = { null };
            final Object[] driverNameCol = { "DRIVER_NAME", new Integer(4) };
            driverName[0] = driverNameCol;
            this.adPrinter.put("driverName", driverName);
            final Object[] portName = { null };
            final Object[] portNameCol = { "PORT_NAME", new Integer(4) };
            portName[0] = portNameCol;
            this.adPrinter.put("portName", portName);
            final Object[] serverName = { null };
            final Object[] serverNameCol = { "SERVER_NAME", new Integer(4) };
            serverName[0] = serverNameCol;
            this.adPrinter.put("serverName", serverName);
            final Object[] location2 = { null };
            final Object[] location1Col = { "LOCATION", new Integer(4) };
            location2[0] = location1Col;
            this.adPrinter.put("location1", location2);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADPrinterAttr() : " + ex, ex);
            throw ex;
        }
        return this.adPrinter;
    }
    
    public List getComputerAttrList() throws Exception {
        final String sourceMethod = "getComputerAttrList";
        final List computerAttr = new ArrayList();
        try {
            final Hashtable computer = this.getADComputerAttr();
            final Enumeration enume = computer.keys();
            while (enume.hasMoreElements()) {
                final String attr = enume.nextElement();
                if (attr != "role") {
                    computerAttr.add(attr);
                }
            }
            computerAttr.add("lastLogonTimestamp");
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADComputerAttributes : " + computerAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getComputerAttrList() : " + ex, ex);
            throw ex;
        }
        return computerAttr;
    }
    
    protected Hashtable getADComputerAttr() throws Exception {
        final String sourceMethod = "getADComputerAttr";
        try {
            final Object[] dnsName = { null };
            final Object[] dnsNameCol = { "DNS_NAME", new Integer(4) };
            dnsName[0] = dnsNameCol;
            this.adComputer.put("dNSHostName", dnsName);
            final Object[] lastLogon = { null };
            final Object[] lastLogonCol = { "LAST_LOGON", new Integer(2) };
            lastLogon[0] = lastLogonCol;
            this.adComputer.put("lastLogon", lastLogon);
            final Object[] accControl = new Object[2];
            final Object[] isDisabledCol = { "IS_DISABLED", new Integer(3) };
            final Object[] isDelegationTrustedCol = { "TRUSTED_FOR_DELEGATION", new Integer(3) };
            accControl[0] = isDisabledCol;
            accControl[1] = isDelegationTrustedCol;
            this.adComputer.put("userAccountControl", accControl);
            final Object[] osName = { null };
            final Object[] osNameCol = { "OS_NAME", new Integer(4) };
            osName[0] = osNameCol;
            this.adComputer.put("operatingSystem", osName);
            final Object[] osVersion = { null };
            final Object[] osVersionCol = { "OS_VERSION", new Integer(4) };
            osVersion[0] = osVersionCol;
            this.adComputer.put("operatingSystemVersion", osVersion);
            final Object[] servicePack = { null };
            final Object[] servicePackCol = { "SERVICE_PACK", new Integer(4) };
            servicePack[0] = servicePackCol;
            this.adComputer.put("operatingSystemServicePack", servicePack);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADComputerAttr() : " + ex, ex);
            throw ex;
        }
        return this.adComputer;
    }
    
    public List getGeneralComputerAttrList() throws Exception {
        final String sourceMethod = "getGeneralComputerAttrList";
        final List computerAttr = new ArrayList();
        try {
            final Hashtable computer = this.getGeneralComputerAttr();
            final Enumeration enume = computer.keys();
            while (enume.hasMoreElements()) {
                final String attr = enume.nextElement();
                computerAttr.add(attr);
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADComputerAttributes : " + computerAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getComputerAttrList() : " + ex, ex);
            throw ex;
        }
        return computerAttr;
    }
    
    protected Hashtable getGeneralComputerAttr() throws Exception {
        final String sourceMethod = "getGeneralComputerAttr";
        try {
            final Object[] osName = { null };
            final Object[] osNameCol = { "OS_NAME", new Integer(4) };
            osName[0] = osNameCol;
            this.generalComputer.put("operatingSystem", osName);
            final Object[] osVersion = { null };
            final Object[] osVersionCol = { "OS_VERSION", new Integer(4) };
            osVersion[0] = osVersionCol;
            this.generalComputer.put("operatingSystemVersion", osVersion);
            final Object[] servicePack = { null };
            final Object[] servicePackCol = { "SERVICE_PACK", new Integer(4) };
            servicePack[0] = servicePackCol;
            this.generalComputer.put("operatingSystemServicePack", servicePack);
            final Object[] osCategory = { null };
            final Object[] osCategoryCol = { "OS_CATEGORY", new Integer(1) };
            osCategory[0] = osCategoryCol;
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getGeneralComputerAttr() : " + ex, ex);
            throw ex;
        }
        return this.generalComputer;
    }
    
    public List getGroupAttrList() throws Exception {
        final String sourceMethod = "getGroupAttrList";
        final List groupAttr = new ArrayList();
        try {
            final Hashtable group = this.getADGroupAttr();
            final Enumeration enume = group.keys();
            while (enume.hasMoreElements()) {
                groupAttr.add(enume.nextElement());
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADGroupAttributes : " + groupAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getGroupAttrList() : " + ex, ex);
            throw ex;
        }
        return groupAttr;
    }
    
    public List getContainerAttrList() throws Exception {
        final String sourceMethod = "getContainerAttrList";
        final List containerAttr = new ArrayList();
        try {
            final Hashtable container = this.getADContainerAttr();
            final Enumeration enume = container.keys();
            while (enume.hasMoreElements()) {
                containerAttr.add(enume.nextElement());
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADContainerAttributes : " + containerAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getContainerAttrList() : " + ex, ex);
            throw ex;
        }
        return containerAttr;
    }
    
    public List getDomainPWDInfoAttrList() throws Exception {
        final String sourceMethod = "getDomainPWDInfoAttrList";
        final List domainPWDAttr = new ArrayList();
        try {
            final Hashtable pwdHash = this.getADDomainPWDInfoAttr();
            final Enumeration enume = pwdHash.keys();
            while (enume.hasMoreElements()) {
                domainPWDAttr.add(enume.nextElement());
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADDomainPWDAttributes : " + domainPWDAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getDomainrPWDInfoAttrList() : " + ex, ex);
            throw ex;
        }
        return domainPWDAttr;
    }
    
    protected Hashtable getADDomainPWDInfoAttr() throws Exception {
        final String sourceMethod = "getADDomainPWDInfoAttr";
        try {
            final Object[] maxPwdAge = { null };
            final Object[] maxPwdAgeCol = { "MAX_PWD_AGE", new Integer(2) };
            maxPwdAge[0] = maxPwdAgeCol;
            this.adDomainPWDInfo.put("maxPwdAge", maxPwdAge);
            final Object[] minPwdAge = { null };
            final Object[] minPwdAgeCol = { "MIN_PWD_AGE", new Integer(1) };
            minPwdAge[0] = minPwdAgeCol;
            this.adDomainPWDInfo.put("minPwdAge", minPwdAge);
            final Object[] minPwdLength = { null };
            final Object[] minPwdLengthCol = { "MIN_PWD_LENGTH", new Integer(1) };
            minPwdLength[0] = minPwdLengthCol;
            this.adDomainPWDInfo.put("minPwdLength", minPwdLengthCol);
            final Object[] lockoutDuration = { null };
            final Object[] lockoutDurationCol = { "LOCKOUT_DURATION", new Integer(2) };
            lockoutDuration[0] = lockoutDurationCol;
            this.adDomainPWDInfo.put("lockoutDuration", lockoutDuration);
            final Object[] lockOutObservationWindow = { null };
            final Object[] lockOutObservationWindowCol = { "LOCKOUT_OBSERVATION_WINDOW", new Integer(1) };
            lockOutObservationWindow[0] = lockOutObservationWindowCol;
            this.adDomainPWDInfo.put("lockOutObservationWindow", lockOutObservationWindow);
            final Object[] lockoutThreshold = { null };
            final Object[] lockoutThresholdCol = { "LOCKOUT_THRESHOLD", new Integer(1) };
            lockoutThreshold[0] = lockoutThresholdCol;
            this.adDomainPWDInfo.put("lockoutThreshold", lockoutThreshold);
            final Object[] pwdHistoryLength = { null };
            final Object[] pwdHistoryLengthCol = { "PWD_HISTORY_LENGTH", new Integer(1) };
            pwdHistoryLength[0] = pwdHistoryLengthCol;
            this.adDomainPWDInfo.put("pwdHistoryLength", pwdHistoryLength);
            final Object[] pwdProperties = new Object[6];
            final Object[] isPwdComplexCol = { "IS_PWD_COMPLEX", new Integer(3) };
            final Object[] isPwdLockoutAdminsCol = { "IS_PWD_LOCKOUT_ADMINS", new Integer(3) };
            final Object[] isPwdStoreCol = { "IS_PWD_STORE_CLEARTEXT", new Integer(3) };
            final Object[] isPwdNoAnonCol = { "IS_PWD_NO_ANON_CHANGE", new Integer(3) };
            final Object[] isPwdNoClearChangeCol = { "IS_PWD_NO_CLEAR_CHANGE", new Integer(3) };
            final Object[] isPwdRefuseChangeCol = { "IS_PWD_REFUSE_CHANGE", new Integer(3) };
            pwdProperties[0] = isPwdComplexCol;
            pwdProperties[1] = isPwdLockoutAdminsCol;
            pwdProperties[2] = isPwdStoreCol;
            pwdProperties[3] = isPwdNoAnonCol;
            pwdProperties[4] = isPwdNoClearChangeCol;
            pwdProperties[5] = isPwdRefuseChangeCol;
            this.adDomainPWDInfo.put("pwdProperties", pwdProperties);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADDomainPWDInfoAttr() : " + ex, ex);
            throw ex;
        }
        return this.adDomainPWDInfo;
    }
    
    public List getGpoAttrList() throws Exception {
        final String sourceMethod = "getGpoAttrList";
        final List gpoAttr = new ArrayList();
        try {
            final Hashtable gpo = this.getADGpoAttr();
            final Enumeration enume = gpo.keys();
            while (enume.hasMoreElements()) {
                gpoAttr.add(enume.nextElement());
            }
            SyMLogger.debug(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "ADGpoAttributes : " + gpoAttr);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getGpoAttrList() : " + ex, ex);
            throw ex;
        }
        return gpoAttr;
    }
    
    protected Hashtable getADGpoAttr() throws Exception {
        final String sourceMethod = "getADGpoAttr";
        try {
            final Object[] fileSysPath = { null };
            final Object[] fileSysPathCol = { "FILE_SYS_PATH", new Integer(4) };
            fileSysPath[0] = fileSysPathCol;
            this.adGpo.put("gPCFileSysPath", fileSysPath);
            final Object[] gpoStatus = { null };
            final Object[] gpoStatusCol = { "GPO_STATUS", new Integer(1) };
            gpoStatus[0] = gpoStatusCol;
            this.adGpo.put("flags", gpoStatus);
            final Object[] gpoDispName = { null };
            final Object[] gpoDispNameCol = { "DISPLAY_NAME", new Integer(4) };
            gpoDispName[0] = gpoDispNameCol;
            this.adGpo.put("displayName", gpoDispName);
            final Object[] version = new Object[2];
            final Object[] userVersionCol = { "USER_VERSION", new Integer(1) };
            final Object[] compVersionCol = { "COMPUTER_VERSION", new Integer(1) };
            version[0] = userVersionCol;
            version[1] = compVersionCol;
            this.adGpo.put("versionNumber", version);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADGpoAttr() : " + ex, ex);
            throw ex;
        }
        return this.adGpo;
    }
    
    protected Hashtable getADGroupAttr() throws Exception {
        final String sourceMethod = "getADGroupAttr";
        try {
            final Object[] groupInfo = new Object[2];
            final Object[] groupTypeCol = { "GROUP_TYPE", new Integer(4) };
            final Object[] groupScopeCol = { "GROUP_SCOPE", new Integer(4) };
            groupInfo[0] = groupTypeCol;
            groupInfo[1] = groupScopeCol;
            this.adGroup.put("groupType", groupInfo);
            final Object[] groupLocation = { null };
            final Object[] groupLocationCol = { "LOCATION", new Integer(4) };
            groupLocation[0] = groupLocationCol;
            this.adGroup.put("location", groupLocation);
            final Object[] groupManagedBy = { null };
            final Object[] groupManagedByCol = { "MANAGED_BY", new Integer(4) };
            groupManagedBy[0] = groupManagedByCol;
            this.adGroup.put("managedBy", groupManagedBy);
            final Object[] primaryGroupToken = { null };
            final Object[] primaryGroupTokenCol = { "GROUP_TOKEN_ID", new Integer(1) };
            primaryGroupToken[0] = primaryGroupTokenCol;
            this.adGroup.put("primaryGroupToken", primaryGroupToken);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADGroupAttr() : " + ex, ex);
            throw ex;
        }
        return this.adGroup;
    }
    
    protected Hashtable getADContainerAttr() throws Exception {
        final String sourceMethod = "getADContainerAttr";
        try {
            final Object[] containerLocation = { null };
            final Object[] containerLocationCol = { "LOCATION", new Integer(4) };
            containerLocation[0] = containerLocationCol;
            this.adContainer.put("location", containerLocation);
        }
        catch (final Exception ex) {
            SyMLogger.error(ADObjectsConvertor.logger, ADObjectsConvertor.sourceClass, sourceMethod, "Exception occured in getADContainerAttr() : " + ex, ex);
            throw ex;
        }
        return this.adContainer;
    }
    
    static {
        ADObjectsConvertor.logger = Logger.getLogger("SoMLogger");
        ADObjectsConvertor.sourceClass = "ADObjectsConvertor";
    }
}
