package com.adventnet.tools.prevalent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.net.URL;

public final class Indication
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
    private String fileKey;
    private static Indication indication;
    private BObject bCompatibilityObject;
    
    private Indication() throws Exception {
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
        this.fileKey = null;
        this.bCompatibilityObject = null;
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
    
    private Indication(final String s, final String prdName) {
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
        this.fileKey = null;
        this.bCompatibilityObject = null;
        if (s != null) {
            this.fileName = new File(s);
        }
        if (prdName != null) {
            this.prdFileName = new File(prdName);
        }
    }
    
    public static Indication getInstance() throws Exception {
        if (Indication.indication == null) {
            Indication.indication = new Indication();
        }
        return Indication.indication;
    }
    
    public static Indication getInstanceToWrite(final String petName, final String prdName) {
        final Indication indicationToWrite = new Indication(petName, prdName);
        return indicationToWrite;
    }
    
    public static void setDir(final String dirArg) {
        if (dirArg == null) {
            return;
        }
        Indication.dir = dirArg;
    }
    
    public String getDir() {
        return Indication.dir;
    }
    
    public synchronized void serialize() throws FileNotFoundException, IOException {
        try {
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
            buffer.append("\nthnameofthecuthemanse4334r34*(\n");
            buffer.append(this.fileKey);
            final String string = buffer.toString();
            final char[] b = string.toCharArray();
            final char[] swapby = this.swapBytes(b);
            final int[] ch1 = this.shiftBytes(swapby);
            final FileOutputStream fout = new FileOutputStream(this.fileName);
            final DataOutputStream out = new DataOutputStream(fout);
            final int len = ch1.length;
            out.writeInt(len);
            for (int i = 0; i < len; ++i) {
                out.writeInt(ch1[i]);
            }
            out.flush();
            out.close();
        }
        catch (final FileNotFoundException fnfe) {
            throw fnfe;
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            LUtil.setErrorDetails(new ErrorDetails(501, "501", "Exception 4", "Exception 3"));
            Wield.getInstance().restoreLicenseBackUp();
        }
        catch (final Exception e) {
            e.printStackTrace();
            LUtil.setErrorDetails(new ErrorDetails(501, "501", "Exception 3", "Exception 3"));
            Wield.getInstance().restoreLicenseBackUp();
        }
        catch (final Error err) {
            err.printStackTrace();
            LUtil.setErrorDetails(new ErrorDetails(501, "501", "Exception", "Exception 3"));
            Wield.getInstance().restoreLicenseBackUp();
        }
    }
    
    public synchronized void deSerialize() throws Exception {
        try {
            if (this.fileName != null) {
                final FileInputStream fin = new FileInputStream(this.fileName);
                final DataInputStream in = new DataInputStream(fin);
                final int length = in.readInt();
                final int[] data = new int[length];
                for (int i = 0; i < length; ++i) {
                    data[i] = in.readInt();
                }
                in.close();
                final char[] revbye = this.revShiftBytes(data);
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
                this.fileKey = theList.get(15);
            }
        }
        catch (final FileNotFoundException fnfe) {
            throw fnfe;
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            this.callLicenseRestore(501, ioe, null, "Exception 2");
        }
        catch (final Exception e) {
            e.printStackTrace();
            this.callLicenseRestore(501, e, null, "Exception 2");
        }
        catch (final Error err) {
            err.printStackTrace();
            this.callLicenseRestore(501, null, err, "Exception 4");
        }
    }
    
    private void callLicenseRestore(final int rErrorCode, final Exception e, final Error err, final String rMessage) throws Exception {
        if (Wield.getInstance().getRestoreRetryCount() == 0) {
            LUtil.setErrorDetails(new ErrorDetails(rErrorCode, Integer.toString(rErrorCode), "Exception", rMessage));
            Wield.getInstance().restoreLicenseBackUp();
            return;
        }
        if (e != null) {
            throw e;
        }
        throw err;
    }
    
    public void addEntry(final int productName, final String productVersion, final String lastAccessedDate, final String evalExpiryDate, final String installationExpiryDate, final int noOfEvalDays, final boolean configTrialUser, final float licenseVersion, final boolean isFirstTimeUser, final String key, final String userName, final String companyName, final String hostName, final String regCheck) {
        this.addEntry(productName, productVersion, lastAccessedDate, evalExpiryDate, installationExpiryDate, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, null, null);
    }
    
    public void addEntry(final int productName, final String productVersion, final String lastAccessedDate, final String evalExpiryDate, final String installationExpiryDate, final int noOfEvalDays, final boolean configTrialUser, final float licenseVersion, final boolean isFirstTimeUser, final String key, final String userName, final String companyName, final String hostName, final String regCheck, final String regExpiryDate) {
        this.addEntry(productName, productVersion, lastAccessedDate, evalExpiryDate, installationExpiryDate, noOfEvalDays, configTrialUser, licenseVersion, isFirstTimeUser, key, userName, companyName, hostName, regCheck, regExpiryDate, null);
    }
    
    public void addEntry(final int productName, final String productVersion, final String lastAccessedDate, final String evalExpiryDate, final String installationExpiryDate, final int noOfEvalDays, final boolean configTrialUser, final float licenseVersion, final boolean isFirstTimeUser, final String key, final String userName, final String companyName, final String hostName, final String regCheck, final String regExpiryDate, final String fileKey) {
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
        this.fileKey = fileKey;
    }
    
    public int getProductName() {
        return this.productName;
    }
    
    public String getFileKey() {
        return this.fileKey;
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
    
    private int[] shiftBytes(final char[] by) {
        final int len = by.length;
        final int[] ret = new int[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = by[i] << 1;
        }
        return ret;
    }
    
    private char[] revShiftBytes(final int[] by) {
        final int len = by.length;
        final char[] ret = new char[len];
        for (int i = 0; i < len; ++i) {
            ret[i] = (char)(by[i] >> 1);
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
    
    public void defaultBackFileSerialize(final String compatibilityFilePath, final String pName, final String pVersion) throws FileNotFoundException, IOException, Exception {
        final CompatibilityParser parser = new CompatibilityParser(compatibilityFilePath);
        final BObject obj = parser.getObject();
        obj.setProductName(pName);
        obj.setProductVersion(pVersion);
        try {
            final FileOutputStream out = new FileOutputStream(this.prdFileName);
            final ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(obj);
            s.flush();
            s.close();
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void productNameSerialize(final String compatibilityFilePath) throws FileNotFoundException, IOException, Exception {
        final CompatibilityParser parser = new CompatibilityParser(compatibilityFilePath);
        final BObject obj = parser.getObject();
        try {
            final FileOutputStream out = new FileOutputStream(this.prdFileName);
            final ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(obj);
            s.flush();
            s.close();
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void productNameDeSerialize() throws FileNotFoundException, IOException, ClassNotFoundException {
        if (this.prdFileName != null) {
            final FileInputStream in = new FileInputStream(this.prdFileName);
            final ObjectInputStream s = new ObjectInputStream(in);
            this.bCompatibilityObject = (BObject)s.readObject();
            s.close();
        }
    }
    
    public BObject getBCObject() {
        return this.bCompatibilityObject;
    }
    
    public int getProductNameInt() {
        final String str = new String(Encode.revShiftBytes(this.bCompatibilityObject.getProductName()));
        int value = Laterality.getMapValue(str);
        if (value == -1) {
            value = Laterality.getHashCode(str);
        }
        return value;
    }
    
    public boolean getBackwardSupport() {
        final int[] state = this.bCompatibilityObject.getBackwardState();
        String str = null;
        if (state != null) {
            str = new String(Encode.revShiftBytes(state));
        }
        return str == null || Boolean.valueOf(str);
    }
    
    public boolean isNativeFilesToBeBundled() {
        final int[] state = this.bCompatibilityObject.getNativeFilesState();
        String str = null;
        if (state != null) {
            str = new String(Encode.revShiftBytes(state));
        }
        return str == null || Boolean.valueOf(str);
    }
    
    public boolean isTrialMacBased() {
        final int[] state = this.bCompatibilityObject.isTrialMacBased();
        String str = null;
        if (state != null) {
            str = new String(Encode.revShiftBytes(state));
        }
        return str != null && Boolean.valueOf(str);
    }
    
    public boolean allowFreeAfterExpiry() {
        final int[] state = this.bCompatibilityObject.allowFreeAfterExpiry();
        String str = null;
        if (state != null) {
            str = new String(Encode.revShiftBytes(state));
        }
        return str == null || Boolean.valueOf(str);
    }
    
    public boolean oneTimeStandardEval() {
        final int[] state = this.bCompatibilityObject.oneTimeStandardEval();
        String str = null;
        if (state != null) {
            str = new String(Encode.revShiftBytes(state));
        }
        return str != null && Boolean.valueOf(str);
    }
    
    public boolean isAgreementHide() {
        try {
            if (this.bCompatibilityObject == null) {
                this.productNameDeSerialize();
            }
            final int[] state = this.bCompatibilityObject.isAgreementHide();
            String str = null;
            if (state != null) {
                str = new String(Encode.revShiftBytes(state));
            }
            return str != null && Boolean.valueOf(str);
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean mandatoryMacLicense() {
        final int[] state = this.bCompatibilityObject.mandatoryMacLicense();
        String str = null;
        if (state != null) {
            str = new String(Encode.revShiftBytes(state));
        }
        return str != null && Boolean.valueOf(str);
    }
    
    public File getTheFile(final String fileNameArg) {
        File lFile = new File(fileNameArg);
        if (!lFile.exists()) {
            lFile = new File(Indication.dir + File.separatorChar + fileNameArg);
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
    
    public void addEntry(final int id) {
        this.productName = id;
        this.isFirstTimeUser = true;
    }
    
    static {
        Indication.dir = System.getProperty("user.dir");
        Indication.indication = null;
    }
}
