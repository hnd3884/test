package com.adventnet.tools.prevalent;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URL;

public final class OIndication
{
    URL url;
    private static String dir;
    URL prdurl;
    private File fileName;
    private File prdFileName;
    private int productName;
    private int productNameInt;
    private String productVersion;
    private String lastAccessedDate;
    private String evalExpiryDate;
    private String regExpiryDate;
    private int noOfEvalDays;
    private String installationExpiryDate;
    private boolean configTrialUser;
    private float licenseVersion;
    private boolean isFirstTimeUser;
    private String key;
    private String companyName;
    private String userName;
    private String hostName;
    private String regCheck;
    private static OIndication indication;
    
    private OIndication() throws Exception {
        this.url = null;
        this.prdurl = null;
        this.fileName = null;
        this.prdFileName = null;
        this.productName = -1;
        this.productNameInt = -1;
        this.productVersion = null;
        this.lastAccessedDate = "";
        this.evalExpiryDate = "";
        this.regExpiryDate = "NULL";
        this.noOfEvalDays = 0;
        this.installationExpiryDate = "";
        this.licenseVersion = 0.0f;
        this.key = null;
        this.companyName = null;
        this.userName = null;
        this.hostName = null;
        this.regCheck = "";
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("p");
        strBuff.append("e");
        strBuff.append("ti");
        strBuff.append("n");
        strBuff.append("f");
        strBuff.append("o.d");
        strBuff.append("a");
        strBuff.append("t");
        final StringBuffer prdBuff = new StringBuffer();
        prdBuff.append("p");
        prdBuff.append("r");
        prdBuff.append("o");
        prdBuff.append("d");
        prdBuff.append("uc");
        prdBuff.append("t.d");
        prdBuff.append("a");
        prdBuff.append("t");
        this.fileName = this.getTheFile(strBuff.toString());
        this.prdFileName = this.getTheFile(prdBuff.toString());
    }
    
    private OIndication(final String s, final String prdName) {
        this.url = null;
        this.prdurl = null;
        this.fileName = null;
        this.prdFileName = null;
        this.productName = -1;
        this.productNameInt = -1;
        this.productVersion = null;
        this.lastAccessedDate = "";
        this.evalExpiryDate = "";
        this.regExpiryDate = "NULL";
        this.noOfEvalDays = 0;
        this.installationExpiryDate = "";
        this.licenseVersion = 0.0f;
        this.key = null;
        this.companyName = null;
        this.userName = null;
        this.hostName = null;
        this.regCheck = "";
        if (s != null) {
            this.fileName = new File(s);
        }
        if (prdName != null) {
            this.prdFileName = new File(prdName);
        }
    }
    
    public static OIndication getInstance() throws Exception {
        if (OIndication.indication == null) {
            OIndication.indication = new OIndication();
        }
        return OIndication.indication;
    }
    
    public static OIndication getInstanceToWrite(final String petName, final String prdName) {
        final OIndication indicationToWrite = new OIndication(petName, prdName);
        return indicationToWrite;
    }
    
    public static void setDir(final String dirArg) {
        if (dirArg == null) {
            return;
        }
        OIndication.dir = dirArg;
    }
    
    public String getDir() {
        return OIndication.dir;
    }
    
    public void serialize() throws FileNotFoundException, IOException {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(this.productName - 1);
        buffer.append("\n");
        buffer.append(this.productName);
        buffer.append("\nThis file is restricted");
        buffer.append(this.productVersion + "2.0");
        buffer.append("\n");
        buffer.append(this.productVersion);
        buffer.append("\n2002 2001 10 11 90 01 2003 90 01\n");
        buffer.append(this.lastAccessedDate);
        buffer.append("\n2003 Nov 30 9 23 2002 Dec 25 2003 08 25" + this.evalExpiryDate + "\n");
        buffer.append(this.evalExpiryDate);
        buffer.append("\n10 23 days 45 90 not validated\n");
        buffer.append(this.noOfEvalDays);
        buffer.append("\nthe date of expiry is 2002 Dec 11 03 AdventNet\n");
        buffer.append(this.installationExpiryDate);
        buffer.append("\ntrue false product\n");
        buffer.append(this.configTrialUser);
        buffer.append("\n2.33333theVeIsBSSPARARLIRAAA2.04.5-\n");
        buffer.append(this.licenseVersion);
        buffer.append("\nfalse\n");
        buffer.append(this.isFirstTimeUser);
        buffer.append("\n/*///Delete dont touch \\this file\\\n");
        buffer.append(this.companyName);
        buffer.append("\ncopyrightthisfileshouldbotnr568099009709\n");
        buffer.append(this.key);
        buffer.append("\n2165gu89uh%^*&*fdfd(2002 12 06 2003 22 04\n");
        buffer.append(this.userName);
        buffer.append("\nthnameofthecuthemanse4334r34*(\n");
        buffer.append(this.hostName);
        buffer.append("\nthnameofthecuthemanse4334r34*(\n");
        buffer.append(this.regCheck);
        buffer.append("\n2003 Aug 31 5 26 2002 Aug 21 2004 04 22" + this.regExpiryDate + "\n");
        buffer.append(this.regExpiryDate);
        final String string = buffer.toString();
        final char[] b = string.toCharArray();
        final char[] swapby = this.swapBytes(b);
        final char[] shift = this.shiftBytes(swapby);
        final FileOutputStream fout = new FileOutputStream(this.fileName);
        final Writer out = new OutputStreamWriter(fout, "UTF8");
        out.write(shift);
        out.flush();
        out.close();
    }
    
    public void deSerialize() throws FileNotFoundException, IOException, ClassNotFoundException {
        if (this.fileName != null) {
            final FileInputStream fin = new FileInputStream(this.fileName);
            final StringBuffer sbuf = new StringBuffer();
            final InputStreamReader isr = new InputStreamReader(fin, "UTF8");
            final BufferedReader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                sbuf.append((char)ch);
            }
            in.close();
            final char[] by = sbuf.toString().toCharArray();
            final char[] revbye = this.revShiftBytes(by);
            final char[] bye = this.swapBytes(revbye);
            final String string = new String(bye);
            final StringTokenizer stoken = new StringTokenizer(string, "\n");
            final ArrayList theList = new ArrayList();
            while (stoken.hasMoreTokens()) {
                stoken.nextToken();
                theList.add(stoken.nextToken());
            }
            this.productName = new Integer(theList.get(0));
            this.productVersion = theList.get(1);
            this.lastAccessedDate = theList.get(2);
            this.evalExpiryDate = theList.get(3);
            this.noOfEvalDays = new Integer(theList.get(4));
            this.installationExpiryDate = theList.get(5);
            this.configTrialUser = new Boolean(theList.get(6));
            this.licenseVersion = new Float(theList.get(7));
            this.isFirstTimeUser = new Boolean(theList.get(8));
            this.companyName = theList.get(9);
            this.key = theList.get(10);
            this.userName = theList.get(11);
            this.hostName = theList.get(12);
            this.regCheck = theList.get(13);
            this.regExpiryDate = theList.get(14);
        }
    }
    
    public void addEntry(final int productName, final String productVersion, final String lastAccessedDate, final String evalExpiryDate, final String installationExpiryDate, final int noOfEvalDays, final boolean configTrialUser, final float licenseVersion, final boolean isFirstTimeUser, final String key, final String userName, final String companyName, final String hostName, final String regCheck) {
        this.addEntry(productName, productVersion, lastAccessedDate, evalExpiryDate, installationExpiryDate, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, null);
    }
    
    public void addEntry(final int productName, final String productVersion, final String lastAccessedDate, final String evalExpiryDate, final String installationExpiryDate, final int noOfEvalDays, final boolean configTrialUser, final float licenseVersion, final boolean isFirstTimeUser, final String key, final String userName, final String companyName, final String hostName, final String regCheck, final String regExpiryDate) {
        this.productName = productName;
        this.productVersion = productVersion;
        this.lastAccessedDate = lastAccessedDate;
        this.evalExpiryDate = evalExpiryDate;
        this.installationExpiryDate = installationExpiryDate;
        if (noOfEvalDays != 0) {
            this.noOfEvalDays = noOfEvalDays;
        }
        this.configTrialUser = configTrialUser;
        this.licenseVersion = licenseVersion;
        this.isFirstTimeUser = isFirstTimeUser;
        this.companyName = companyName;
        this.key = key;
        this.userName = userName;
        this.hostName = hostName;
        this.regCheck = regCheck;
        if (regExpiryDate != null) {
            this.regExpiryDate = regExpiryDate;
        }
    }
    
    public int getProductName() {
        return this.productName;
    }
    
    public String getProductVersion() {
        return this.productVersion;
    }
    
    public String getLastAccessedDate() {
        return this.lastAccessedDate;
    }
    
    public String getEvalExpiryDate() {
        return this.evalExpiryDate;
    }
    
    public int getNoOfEvalDays() {
        return this.noOfEvalDays;
    }
    
    public String getInstallationExpiryDate() {
        return this.installationExpiryDate;
    }
    
    public boolean getConfigTrialUser() {
        return this.configTrialUser;
    }
    
    public float getLicenseVersion() {
        return this.licenseVersion;
    }
    
    public boolean getFirstTimeUser() {
        return this.isFirstTimeUser;
    }
    
    public String getTheKey() {
        return this.key;
    }
    
    public String getTheUserName() {
        return this.userName;
    }
    
    public String getTheCompanyName() {
        return this.companyName;
    }
    
    public String getTheHostName() {
        return this.hostName;
    }
    
    public String getTheRegCheck() {
        return this.regCheck;
    }
    
    public String getRegExpiryDate() {
        return this.regExpiryDate;
    }
    
    private char[] shiftBytes(final char[] by) {
        final int len = by.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; i += 2) {
            if (i + 1 < len) {
                ret[i] = (char)(by[i + 1] << 1);
                ret[i + 1] = (char)(by[i] << 1);
            }
            else {
                ret[i] = (char)(by[i] << 1);
            }
        }
        return ret;
    }
    
    private char[] revShiftBytes(final char[] by) {
        final int len = by.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; i += 2) {
            if (i + 1 < len) {
                ret[i] = (char)(by[i + 1] >> 1);
                ret[i + 1] = (char)(by[i] >> 1);
            }
            else {
                ret[i] = (char)(by[i] >> 1);
            }
        }
        return ret;
    }
    
    private char[] swapBytes(final char[] b) {
        final int len = b.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; i += 2) {
            if (i + 1 < len) {
                ret[i] = b[i + 1];
                ret[i + 1] = b[i];
            }
            else {
                ret[i] = b[i];
            }
        }
        return ret;
    }
    
    public String getLocalHostName() {
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (final Exception ex) {}
        if (hostName == null) {
            try {
                hostName = InetAddress.getByName("127.0.0.1").getHostName();
            }
            catch (final Exception ex2) {}
        }
        if (hostName == null) {
            hostName = "127.0.0.1";
        }
        return hostName;
    }
    
    public void addProductEntry(final int productNameInt) {
        this.productNameInt = productNameInt;
    }
    
    public void productNameSerialize() throws FileNotFoundException, IOException {
        try {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(this.productNameInt);
            final String string = buffer.toString();
            final char[] b = string.toCharArray();
            final char[] swapby = this.swapBytes(b);
            final char[] shift = this.shiftBytes(swapby);
            final FileOutputStream fout = new FileOutputStream(this.prdFileName);
            final Writer out = new OutputStreamWriter(fout, "UTF8");
            out.write(shift);
            out.flush();
            out.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void productNameDeSerialize() throws FileNotFoundException, IOException, ClassNotFoundException {
        if (this.prdFileName != null) {
            final FileInputStream fin = new FileInputStream(this.prdFileName);
            final StringBuffer sbuf = new StringBuffer();
            final InputStreamReader isr = new InputStreamReader(fin, "UTF8");
            final Reader in = new BufferedReader(isr);
            int ch;
            while ((ch = in.read()) > -1) {
                sbuf.append((char)ch);
            }
            in.close();
            final char[] by = sbuf.toString().toCharArray();
            final char[] revbye = this.revShiftBytes(by);
            final char[] bye = this.swapBytes(revbye);
            final String str = new String(bye);
            this.productNameInt = Integer.parseInt(str);
        }
    }
    
    public int getProductNameInt() {
        return this.productNameInt;
    }
    
    private File getTheFile(final String fileNameArg) {
        File lFile = new File(fileNameArg);
        if (!lFile.exists()) {
            lFile = new File(OIndication.dir + File.separatorChar + fileNameArg);
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
    
    public static void main(final String[] args) {
        try {
            final OIndication in = getInstance();
            in.deSerialize();
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
    }
    
    static {
        OIndication.dir = "";
        OIndication.indication = null;
    }
}
