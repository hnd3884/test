package com.adventnet.tools.prevalent;

import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;
import java.util.Calendar;
import java.util.Date;

public final class Clientele
{
    String fileName;
    private String dir;
    private String contactMesg;
    private String dateChangeMesg;
    private String invalidLicenseMesg;
    private String trialExpiryMesg;
    private String productExpiryMesg;
    private String invalidKey;
    private Date expiryDate;
    private boolean register;
    private static String userNameForLauncher;
    private static Clientele client;
    private String lastAccessedString;
    
    private Clientele() {
        this.fileName = null;
        this.dir = "";
        this.contactMesg = "\n\n" + ToolsUtils.getString("Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com") + "\n\n";
        this.dateChangeMesg = ToolsUtils.getString("Inconsistent product configuration.\n Please restore original settings.");
        this.invalidLicenseMesg = ToolsUtils.getString("Invalid License");
        this.trialExpiryMesg = ToolsUtils.getString("Trial Period has expired");
        this.productExpiryMesg = ToolsUtils.getString("Product trial version expired");
        this.invalidKey = ToolsUtils.getString("Invalid key");
        this.register = false;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("p");
        strBuff.append("e");
        strBuff.append("ti");
        strBuff.append("n");
        strBuff.append("f");
        strBuff.append("o.d");
        strBuff.append("a");
        strBuff.append("t");
        this.fileName = strBuff.toString();
    }
    
    public static Clientele getInstance() {
        if (Clientele.client == null) {
            Clientele.client = new Clientele();
        }
        return Clientele.client;
    }
    
    public static Date getCurrentDate(final Calendar cal) {
        final long timeInMillis = cal.getTime().getTime();
        final long hours = cal.get(11);
        final long minutes = cal.get(12);
        final long seconds = cal.get(13);
        final long milliseconds = cal.get(14);
        final Calendar cal2 = Calendar.getInstance();
        final int year = cal.get(1);
        final int date = cal.get(5);
        final int month = cal.get(2);
        cal2.set(year, month, date);
        final long todayTimeInMillis = hours * 60L * 60L * 1000L + minutes * 60L * 1000L + seconds * 1000L + milliseconds;
        final long todayStartTimeInMillis = timeInMillis - todayTimeInMillis;
        final Date newDate = new Date(todayStartTimeInMillis);
        return newDate;
    }
    
    public int compareTo(final Date date1, final Date date2) {
        final long thisTime = date1.getTime();
        final long anotherTime = date2.getTime();
        return (thisTime < anotherTime) ? -1 : ((thisTime == anotherTime) ? 0 : 1);
    }
    
    public boolean getClienteleState() {
        try {
            final File serFile = this.getTheFile(this.fileName);
            if (serFile.exists()) {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final int map = util.getProductName();
                final String minorVersion = util.getProductVersion();
                final float licenseVersion = util.getLicenseVersion();
                final int noOfEvalDays = util.getNoOfEvalDays();
                final String installationExpiryString = util.getInstallationExpiryDate();
                final Date installationExpiryDate = this.getTheDate(installationExpiryString, false);
                final boolean configTrialUser = util.getConfigTrialUser();
                boolean isFirstTimeUser = util.getFirstTimeUser();
                final String userName = util.getTheUserName();
                final String companyName = util.getTheCompanyName();
                final String key = util.getTheKey();
                final String hostName = util.getTheHostName();
                final String regCheck = util.getTheRegCheck();
                if (!util.getLastAccessedDate().toString().equals(" ")) {
                    final Calendar cal = Calendar.getInstance();
                    final Date currentDate = getCurrentDate(cal);
                    final String currentString = this.getTheString(currentDate);
                    final String evalExpiryString = util.getEvalExpiryDate();
                    final Date evalExpiryDate = this.getTheDate(evalExpiryString, false);
                    this.lastAccessedString = util.getLastAccessedDate();
                    final Date lastAccessedDate = this.getTheDate(this.lastAccessedString, false);
                    isFirstTimeUser = true;
                    if (this.compareTo(lastAccessedDate, currentDate) <= 0 && this.compareTo(evalExpiryDate, currentDate) > 0) {
                        util.addEntry(map, minorVersion, currentString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck);
                        util.serialize();
                        this.expiryDate = evalExpiryDate;
                        return true;
                    }
                    if (this.compareTo(lastAccessedDate, currentDate) > 0) {
                        LUtil.showError("ERROR CODE : 102", this.dateChangeMesg, this.contactMesg, "Error", 102);
                        this.expiryDate = evalExpiryDate;
                        return false;
                    }
                    LUtil.showError("", this.trialExpiryMesg, this.contactMesg, "Error", -1);
                    util.addEntry(map, minorVersion, currentString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck);
                    util.serialize();
                    final String lastDate = "-" + currentString.replace(' ', ':');
                    final String product = new Integer(map).toString();
                    final String productVersion = minorVersion;
                    final ROperation operation = ROperation.getInstance();
                    operation.writeRegValue(product, productVersion, key + lastDate);
                    this.register = true;
                    return false;
                }
            }
        }
        catch (final Exception en) {
            LUtil.showError("ERROR CODE : 202", this.invalidLicenseMesg, this.contactMesg, "Error", 202);
            return false;
        }
        return true;
    }
    
    public static boolean getIsFirstTimeUser() {
        try {
            final Indication util = Indication.getInstance();
            util.deSerialize();
            return util.getFirstTimeUser();
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    public String getTheUserNameForLauncher() {
        return Clientele.userNameForLauncher;
    }
    
    public static String getTheRegisterCheck() {
        try {
            final Indication util = Indication.getInstance();
            util.deSerialize();
            Clientele.userNameForLauncher = util.getTheUserName();
            return util.getTheRegCheck();
        }
        catch (final Exception e) {
            return "exception";
        }
    }
    
    public Date getTheDate(final String date, final boolean boo) {
        final Calendar cal = Calendar.getInstance();
        if (date.equals(" ")) {
            return cal.getTime();
        }
        StringTokenizer st = null;
        if (boo) {
            st = new StringTokenizer(date, ":");
        }
        else {
            st = new StringTokenizer(date, " ");
        }
        if (st.countTokens() == 3) {
            final ArrayList array = new ArrayList();
            while (st.hasMoreTokens()) {
                array.add(st.nextToken());
            }
            cal.set(new Integer(array.get(0)), new Integer(array.get(1)), new Integer(array.get(2)));
            return getCurrentDate(cal);
        }
        return cal.getTime();
    }
    
    public String getTheString(final Date dd) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        final String year = new Integer(cal.get(1)).toString();
        final String month = new Integer(cal.get(2)).toString();
        final String day = new Integer(cal.get(5)).toString();
        return year + " " + month + " " + day;
    }
    
    public boolean first(final String keyDate, final String user, final String company, final String trialKey) {
        try {
            final File serFile = this.getTheFile(this.fileName);
            if (serFile.exists()) {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final int map = util.getProductName();
                final String minorVersion = util.getProductVersion();
                final float licenseVersion = util.getLicenseVersion();
                int noOfEvalDays = util.getNoOfEvalDays();
                final String installationExpiryString = util.getInstallationExpiryDate();
                final String uploadString = util.getLastAccessedDate();
                final Date uploadDate = this.getTheDate(uploadString, false);
                final Date installationExpiryDate = this.getTheDate(installationExpiryString, false);
                final boolean configTrialUser = util.getConfigTrialUser();
                boolean isFirstTimeUser = util.getFirstTimeUser();
                String userName = util.getTheUserName();
                String companyName = util.getTheCompanyName();
                String key = util.getTheKey();
                String hostName = util.getTheHostName();
                final String regCheck = util.getTheRegCheck();
                userName = user;
                companyName = company;
                key = trialKey;
                try {
                    Date evalExpiryDate = new Date(0L);
                    hostName = util.getLocalHostName();
                    final Calendar cal = Calendar.getInstance();
                    final Date lastAccessedDate = getCurrentDate(cal);
                    if (this.compareTo(lastAccessedDate, uploadDate) < 0) {
                        LUtil.showError("ERROR CODE : 101", this.dateChangeMesg, this.contactMesg, "Error", 101);
                        return false;
                    }
                    if (this.compareTo(installationExpiryDate, lastAccessedDate) <= 0) {
                        LUtil.showError("", this.productExpiryMesg, this.contactMesg, "Information", -2);
                        return false;
                    }
                    final Calendar expCal = Calendar.getInstance();
                    final ArrayList kDate = new ArrayList();
                    final String dd = keyDate;
                    final StringTokenizer stoken = new StringTokenizer(dd, " ");
                    while (stoken.hasMoreTokens()) {
                        kDate.add(stoken.nextElement());
                    }
                    final int d = new Integer(kDate.get(0));
                    final int m = new Integer(kDate.get(1)) - 1;
                    final int y = new Integer(kDate.get(2)) + 2000;
                    expCal.set(y, m, d);
                    if (configTrialUser) {
                        evalExpiryDate = getCurrentDate(expCal);
                    }
                    else {
                        final Date test = getCurrentDate(expCal);
                        if (this.compareTo(test, installationExpiryDate) < 0) {
                            evalExpiryDate = test;
                        }
                        else {
                            evalExpiryDate = installationExpiryDate;
                        }
                    }
                    if (this.compareTo(evalExpiryDate, lastAccessedDate) <= 0) {
                        LUtil.showError("ERROR CODE : 507", this.invalidKey, this.contactMesg, "Error", 507);
                        return false;
                    }
                    final long second = evalExpiryDate.getTime();
                    final long first = lastAccessedDate.getTime();
                    final long diff = (second - first) / 86400000L;
                    noOfEvalDays = (int)diff;
                    final String evalExpiryString = this.getTheString(evalExpiryDate);
                    this.lastAccessedString = this.getTheString(lastAccessedDate);
                    isFirstTimeUser = true;
                    util.addEntry(map, minorVersion, this.lastAccessedString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck);
                    util.serialize();
                    this.expiryDate = evalExpiryDate;
                    return true;
                }
                catch (final Exception e) {
                    return false;
                }
            }
        }
        catch (final Exception en) {
            LUtil.showError("ERROR CODE : 203", this.invalidLicenseMesg, this.contactMesg, "Error", 203);
            return false;
        }
        return false;
    }
    
    public String getTheLastAccessedDate() {
        return this.lastAccessedString;
    }
    
    public Date getEvaluationExpiryDate() {
        return this.expiryDate;
    }
    
    public boolean getRegister() {
        return this.register;
    }
    
    public boolean getRegisterState(final String user, final String company, final String regKey) {
        try {
            final File serFile = this.getTheFile(this.fileName);
            if (serFile.exists()) {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final int map = util.getProductName();
                final String minorVersion = util.getProductVersion();
                final float licenseVersion = util.getLicenseVersion();
                final int noOfEvalDays = util.getNoOfEvalDays();
                final String installationExpiryString = util.getInstallationExpiryDate();
                final boolean configTrialUser = util.getConfigTrialUser();
                final boolean isFirstTimeUser = true;
                String userName = util.getTheUserName();
                String companyName = util.getTheCompanyName();
                String key = util.getTheKey();
                String hostName = util.getTheHostName();
                String evalExpiryString = util.getEvalExpiryDate();
                final String lastAccessedString = util.getLastAccessedDate();
                String regCheck = util.getTheRegCheck();
                evalExpiryString = "never";
                if (hostName.equals(" ")) {
                    hostName = util.getLocalHostName();
                }
                userName = user;
                companyName = company;
                key = regKey;
                regCheck = "R";
                util.addEntry(map, minorVersion, lastAccessedString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck);
                util.serialize();
            }
        }
        catch (final Exception en) {
            LUtil.showError("ERROR CODE : 205", this.invalidLicenseMesg, this.contactMesg, "Error", 205);
            return false;
        }
        return true;
    }
    
    private File getTheFile(final String fileNameArg) {
        File lFile = new File(fileNameArg);
        if (!lFile.exists()) {
            lFile = new File(this.dir + File.separatorChar + fileNameArg);
            if (!lFile.exists()) {
                lFile = new File(LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + fileNameArg);
                if (lFile.exists()) {
                    return lFile;
                }
                final URL fileUrl = this.getClass().getResource("/" + fileNameArg);
                try {
                    final String fName = LUtil.getFileName(fileUrl);
                    lFile = new File(fName);
                }
                catch (final Exception ex) {}
            }
        }
        return lFile;
    }
    
    void setDir(final String dirArg) {
        if (dirArg == null) {
            return;
        }
        this.dir = dirArg;
    }
    
    String getDir() {
        return this.dir;
    }
    
    boolean checkExpiry() {
        try {
            final Indication util = Indication.getInstance();
            util.deSerialize();
            final String expDate = util.getRegExpiryDate();
            if (expDate == null || expDate.equals("") || expDate.equals("NULL")) {
                return true;
            }
            final Date expiryDate = this.getTheDate(expDate, false);
            final Calendar cal = Calendar.getInstance();
            final Date currentDate = getCurrentDate(cal);
            final int comp = this.compareTo(expiryDate, currentDate);
            return comp >= 0;
        }
        catch (final Exception e) {
            return false;
        }
    }
    
    static {
        Clientele.client = null;
    }
}
