package com.adventnet.tools.prevalent;

import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;

public final class Vendee
{
    private static Vendee client;
    private String fileName;
    private String lastAccessedString;
    private Date expiryDate;
    private String dir;
    private String date;
    private boolean register;
    private String productName;
    private String productVersion;
    private String userName;
    private String userType;
    private String companyName;
    private String invalidFile;
    private boolean mode;
    
    public void setMode(final boolean mode) {
        this.mode = mode;
    }
    
    private Vendee() {
        this.fileName = null;
        this.lastAccessedString = null;
        this.expiryDate = null;
        this.dir = "";
        this.date = null;
        this.register = false;
        this.productName = null;
        this.productVersion = null;
        this.userName = null;
        this.userType = null;
        this.companyName = null;
        this.invalidFile = "Invalid License File";
        this.mode = true;
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
    
    public static Vendee getInstance() {
        if (Vendee.client == null) {
            Vendee.client = new Vendee();
        }
        return Vendee.client;
    }
    
    boolean first(final String user, final String company, final String trialKey, final String evalDays, final String prdExpiryDate, final String check, final String fKey) {
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
                final Date uploadDate = LUtil.getTheDate(uploadString, false);
                final Date installationExpiryDate = LUtil.getTheDate(installationExpiryString, false);
                final boolean configTrialUser = util.getConfigTrialUser();
                boolean isFirstTimeUser = util.getFirstTimeUser();
                String userName = util.getTheUserName();
                String companyName = util.getTheCompanyName();
                String key = util.getTheKey();
                String hostName = util.getTheHostName();
                String regCheck = util.getTheRegCheck();
                String fileKey = util.getFileKey();
                userName = user;
                companyName = company;
                key = trialKey;
                regCheck = check;
                fileKey = fKey;
                try {
                    Date evalExpiryDate = new Date(0L);
                    hostName = util.getLocalHostName();
                    final Calendar cal = Calendar.getInstance();
                    final Date lastAccessedDate = LUtil.getCurrentDate(cal);
                    if (this.compareTo(lastAccessedDate, uploadDate) < 0) {
                        this.showError("ERROR CODE : 516", this.invalidFile, 516);
                        return false;
                    }
                    if (regCheck.equals("T") && this.compareTo(installationExpiryDate, lastAccessedDate) <= 0) {
                        this.showError("ERROR CODE : 517", this.invalidFile, 517);
                        return false;
                    }
                    if (!regCheck.equals("F")) {
                        final Calendar expCal = new GregorianCalendar();
                        if (evalDays != null) {
                            noOfEvalDays = Integer.parseInt(evalDays);
                            expCal.add(5, noOfEvalDays);
                        }
                        else {
                            final String dd = prdExpiryDate;
                            final ArrayList kDate = new ArrayList();
                            final StringTokenizer stoken = new StringTokenizer(dd, "-");
                            while (stoken.hasMoreTokens()) {
                                kDate.add(stoken.nextElement());
                            }
                            final int y = new Integer(kDate.get(0));
                            final int m = new Integer(kDate.get(1));
                            final int d = new Integer(kDate.get(2));
                            expCal.set(y, m - 1, d);
                        }
                        if (configTrialUser) {
                            evalExpiryDate = LUtil.getCurrentDate(expCal);
                        }
                        else {
                            final Date test = LUtil.getCurrentDate(expCal);
                            if (this.compareTo(test, installationExpiryDate) < 0) {
                                evalExpiryDate = test;
                            }
                            else {
                                evalExpiryDate = installationExpiryDate;
                            }
                        }
                        if (this.compareTo(evalExpiryDate, lastAccessedDate) <= 0) {
                            this.showError("ERROR CODE : 512", "Invalid License File", 512);
                            return false;
                        }
                        if (evalDays == null) {
                            final long second = evalExpiryDate.getTime();
                            final long first = lastAccessedDate.getTime();
                            final long diff = (second - first) / 86400000L;
                            noOfEvalDays = (int)diff;
                        }
                    }
                    final String evalExpiryString = this.getTheString(evalExpiryDate);
                    this.lastAccessedString = this.getTheString(lastAccessedDate);
                    isFirstTimeUser = true;
                    util.addEntry(map, minorVersion, this.lastAccessedString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, null, fileKey);
                    util.serialize();
                    this.expiryDate = evalExpiryDate;
                    return true;
                }
                catch (final FileNotFoundException fnfe) {
                    this.showError("ERROR CODE : 1001", "File access denied, Check the user previllages , For more details", 1001);
                    return false;
                }
                catch (final Exception e) {
                    this.showError("ERROR CODE : 469", this.invalidFile, 469);
                    return false;
                }
            }
        }
        catch (final Exception en) {
            this.showError("ERROR CODE : 513", this.invalidFile, 513);
            return false;
        }
        return false;
    }
    
    private File getTheFile(final String fileNameArg) {
        File lFile = new File(fileNameArg);
        if (!lFile.exists()) {
            lFile = new File(LUtil.getDir() + File.separatorChar + fileNameArg);
            if (lFile.exists()) {
                return lFile;
            }
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
        return lFile;
    }
    
    boolean getRegisterState(final String user, final String company, final String regKey, final String fileKey) {
        try {
            final File serFile = this.getTheFile(this.fileName);
            if (!serFile.exists()) {
                this.showError("ERROR CODE : 518", this.invalidFile, 518);
                return false;
            }
            final Indication util = Indication.getInstance();
            util.deSerialize();
            final int map = util.getProductName();
            final String minorVersion = util.getProductVersion();
            final float licenseVersion = util.getLicenseVersion();
            final int noOfEvalDays = util.getNoOfEvalDays();
            final String installationExpiryString = util.getInstallationExpiryDate();
            final boolean configTrialUser = util.getConfigTrialUser();
            boolean isFirstTimeUser = util.getFirstTimeUser();
            String userName = util.getTheUserName();
            String companyName = util.getTheCompanyName();
            String key = util.getTheKey();
            String hostName = util.getTheHostName();
            String evalExpiryString = util.getEvalExpiryDate();
            final String lastAccessedString = util.getLastAccessedDate();
            String regCheck = util.getTheRegCheck();
            evalExpiryString = "never";
            isFirstTimeUser = true;
            if (hostName.equals(" ")) {
                hostName = util.getLocalHostName();
            }
            userName = user;
            companyName = company;
            key = regKey;
            regCheck = "R";
            util.addEntry(map, minorVersion, lastAccessedString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, null, fileKey);
            util.serialize();
            return true;
        }
        catch (final Exception en) {
            this.showError("ERROR CODE : 515", this.invalidFile, 515);
            return false;
        }
    }
    
    public String getTheLastAccessedDate() {
        return this.lastAccessedString;
    }
    
    public Date getEvaluationExpiryDate() {
        return this.expiryDate;
    }
    
    int compareTo(final Date date1, final Date date2) {
        final long thisTime = date1.getTime();
        final long anotherTime = date2.getTime();
        return (thisTime < anotherTime) ? -1 : ((thisTime == anotherTime) ? 0 : 1);
    }
    
    String getTheString(final Date dd) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        final String year = new Integer(cal.get(1)).toString();
        final String month = new Integer(cal.get(2)).toString();
        final String day = new Integer(cal.get(5)).toString();
        return year + " " + month + " " + day;
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
    
    private void setProductName(final String name) {
        this.productName = name;
    }
    
    private void setProductVersion(final String name) {
        this.productVersion = name;
    }
    
    private void setUserName(final String name) {
        this.userName = name;
    }
    
    private void setUserType(final String name) {
        this.userType = name;
    }
    
    private void setCompanyName(final String name) {
        this.companyName = name;
    }
    
    String getProductName() {
        return this.productName;
    }
    
    String getProductVersion() {
        return this.productVersion;
    }
    
    String getUserName() {
        return this.userName;
    }
    
    String getUserType() {
        return this.userType;
    }
    
    String getCompanyName() {
        return this.companyName;
    }
    
    public boolean readFile() {
        Indication util = null;
        try {
            util = Indication.getInstance();
            util.deSerialize();
            util.productNameDeSerialize();
        }
        catch (final Exception e) {
            this.showError("ERROR CODE : 520", this.invalidFile, 520);
            return false;
        }
        final int map = util.getProductName();
        final String product = new Integer(map).toString();
        final int prod = util.getProductNameInt();
        if (map != prod) {
            this.showError("ERROR CODE : 521", this.invalidFile, 521);
            return false;
        }
        this.setProductName(Laterality.productName[map]);
        final String version = util.getProductVersion();
        this.setProductVersion(version);
        final String userName = util.getTheUserName();
        this.setUserName(userName);
        final String companyName = util.getTheCompanyName();
        this.setCompanyName(companyName);
        final String key = util.getTheKey();
        final String hostName = util.getTheHostName();
        final String lastAccessedString = util.getLastAccessedDate();
        final String regCheck = util.getTheRegCheck();
        this.setUserType(regCheck);
        try {
            try {
                final ArrayList s = this.getTrial(product, version, userName, companyName, key, regCheck);
                if (s.contains(key)) {
                    final String regProduct = new Integer(map).toString();
                    final ROperation operation = ROperation.getInstance();
                    final String regisKey = operation.getRegValues(regProduct, version);
                    final int index = regisKey.lastIndexOf("-");
                    if (index != -1) {
                        final String registryKey = regisKey.substring(0, index);
                    }
                    else {
                        final String registryKey = regisKey;
                    }
                    String dateKey = "";
                    if (regCheck.equals("T")) {
                        dateKey = "-" + lastAccessedString.replace(' ', ':');
                        boolean registryboolean = false;
                        registryboolean = operation.writeRegValue(regProduct, version, key + dateKey);
                    }
                    if (key.length() == 24) {
                        Formalize.getInstance().setType(key);
                    }
                    return true;
                }
                if (key.length() == 20) {
                    try {
                        final NRearward nRear = new NRearward(userName, key, map);
                        nRear.getRegistered();
                        final String[] props = nRear.getPropValues();
                        final int lType = Integer.parseInt(props[3]);
                        Formalize.getInstance().setOldType(lType);
                        return nRear.isValidationOk();
                    }
                    catch (final Exception ex) {
                        this.showError("ERROR CODE : 522", this.invalidFile, 522);
                        return false;
                    }
                }
                return false;
            }
            catch (final Exception e2) {
                e2.printStackTrace();
            }
        }
        catch (final Exception ex2) {}
        return false;
    }
    
    public ArrayList getTrial(final String product, final String version, final String user, final String company, final String key, final String userType) throws Exception {
        final Modulation k = Modulation.getInstance();
        final ArrayList list = new ArrayList();
        final WebGet wget = WebGet.getInstance();
        final String str = wget.getValues(key, "WEB");
        final StringTokenizer st = new StringTokenizer(str, ",");
        String type = null;
        String d = null;
        String a = null;
        while (st.hasMoreTokens()) {
            d = st.nextToken();
            a = st.nextToken();
        }
        if (a != null && a.length() == 3) {
            type = a.substring(1, 2);
        }
        if (userType.equals("T")) {
            if (d.length() == 3) {
                final StringBuffer strBuff = new StringBuffer();
                strBuff.append(new Integer(k.getInt(d.charAt(0))).toString());
                strBuff.append(" ");
                strBuff.append(new Integer(k.getInt(d.charAt(1))).toString());
                strBuff.append(" ");
                strBuff.append(new Integer(k.getInt(d.charAt(2))).toString());
                this.date = strBuff.toString();
            }
            list.add(k.getKey(user, company, product, type, version, this.date, userType, "@@"));
        }
        else if (userType.equals("R") && key.length() == 24) {
            final String macAdd = this.getTheMacID();
            if (!macAdd.equals("NULL") || !macAdd.equals("null")) {
                final MacComp comp = MacComp.getInstance();
                final String mac = MacComp.processString(comp.getTheStringForProcess(comp.getTheFinalValue(macAdd)));
                list.add(k.getKey(user, company, product, type, version, mac));
            }
            final String[] macArray = { "db", "14", "e9", "41", "aa", "6b", "6c", "3c", "cd", "e3" };
            for (int i = 0; i < macArray.length; ++i) {
                list.add(k.getKey(user, company, product, type, version, macArray[i]));
            }
        }
        return list;
    }
    
    public String getTheMacID() {
        final Intonation intonate = Intonation.getInstance();
        final String macAdd = intonate.getTheMAC("NO");
        return macAdd;
    }
    
    public String getTheMacID(final String MacId) {
        final Intonation intonate = Intonation.getInstance();
        final String macAdd = intonate.getTheMAC(MacId);
        return macAdd;
    }
    
    public boolean getClienteleState() {
        try {
            final File serFile = this.getTheFile(this.fileName);
            if (!serFile.exists()) {
                this.showError("ERROR CODE : 525", this.invalidFile, 525);
                return false;
            }
            final Indication util = Indication.getInstance();
            util.deSerialize();
            final int map = util.getProductName();
            final String minorVersion = util.getProductVersion();
            final float licenseVersion = util.getLicenseVersion();
            final int noOfEvalDays = util.getNoOfEvalDays();
            final String installationExpiryString = util.getInstallationExpiryDate();
            final Date installationExpiryDate = LUtil.getTheDate(installationExpiryString, false);
            final boolean configTrialUser = util.getConfigTrialUser();
            boolean isFirstTimeUser = util.getFirstTimeUser();
            final String userName = util.getTheUserName();
            final String companyName = util.getTheCompanyName();
            final String key = util.getTheKey();
            final String hostName = util.getTheHostName();
            final String regCheck = util.getTheRegCheck();
            final String fileKey = util.getFileKey();
            if (util.getLastAccessedDate().toString().equals(" ")) {
                return false;
            }
            final Calendar cal = Calendar.getInstance();
            final Date currentDate = LUtil.getCurrentDate(cal);
            final String currentString = this.getTheString(currentDate);
            final String evalExpiryString = util.getEvalExpiryDate();
            final Date evalExpiryDate = LUtil.getTheDate(evalExpiryString, false);
            this.lastAccessedString = util.getLastAccessedDate();
            final Date lastAccessedDate = LUtil.getTheDate(this.lastAccessedString, false);
            isFirstTimeUser = true;
            if (this.compareTo(lastAccessedDate, currentDate) <= 0 && this.compareTo(evalExpiryDate, currentDate) > 0) {
                util.addEntry(map, minorVersion, currentString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, null, fileKey);
                util.serialize();
                this.expiryDate = evalExpiryDate;
                return true;
            }
            if (this.compareTo(lastAccessedDate, currentDate) > 0) {
                this.showError("ERROR CODE : 527", "Inconsistent change detected in date settings.\n Please restore original date settings.", 527);
                this.expiryDate = evalExpiryDate;
                return false;
            }
            final String regProduct = new Integer(map).toString();
            String mess = null;
            if (regCheck.equals("T")) {
                mess = "Trial period has expired";
            }
            else {
                mess = "Registered period has expired";
            }
            this.showError("ERROR CODE : 519", mess, 519);
            util.addEntry(map, minorVersion, currentString, evalExpiryString, installationExpiryString, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, null, fileKey);
            util.serialize();
            if (userName.equals("Evaluation User")) {
                final String evalFile = LUtil.getDir() + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml";
                final File f = new File(evalFile);
                f.delete();
                final File tEditionDir = new File(LUtil.getDir() + File.separator + "editionchange");
                if (tEditionDir.exists() && tEditionDir.isDirectory()) {
                    LUtil.deleteFilesInDir(tEditionDir);
                }
            }
            final String lastDate = "-" + currentString.replace(' ', ':');
            final String product = new Integer(map).toString();
            final String productVersion = minorVersion;
            final ROperation operation = ROperation.getInstance();
            operation.writeRegValue(regProduct, productVersion, key + lastDate);
            this.register = true;
            return false;
        }
        catch (final FileNotFoundException fnf) {
            this.showError("ERROR CODE : 1002", "File access denied, Check the user previllages , For more details", 1002);
            return false;
        }
        catch (final Exception en) {
            this.showError("ERROR CODE : 523", this.invalidFile, 523);
            return false;
        }
    }
    
    public boolean getRegister() {
        return this.register;
    }
    
    private void showError(final String code, final String invalidFile, final int codeInt) {
        final String contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        if (this.mode) {
            try {
                LUtil.showError(code, invalidFile, contactMesg, "Error", codeInt);
            }
            catch (final Error e) {
                LUtil.showCMDError(code, invalidFile, contactMesg, codeInt);
            }
        }
        else {
            LUtil.showCMDError(code, invalidFile, contactMesg, codeInt);
        }
    }
    
    static {
        Vendee.client = null;
    }
}
