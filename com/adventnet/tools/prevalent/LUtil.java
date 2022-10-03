package com.adventnet.tools.prevalent;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.Calendar;

public class LUtil
{
    private static String dir;
    private static String subDir;
    private static boolean showBackward;
    private static boolean isTrialMacBased;
    private static boolean isISMP;
    private static String errorCode;
    private static String errorMsg;
    private static String detErrorMsg;
    private static ErrorDetails errorDet;
    public static final String licenseBackUpDir = "keybkp";
    public static final String MEAIM = "meiam";
    public static final String MEIAMLOCKFILE = "IT360Configured.lock";
    public static final String TRIALEDITIONDIR = "editionchange";
    public static final String MPCHECK = "isMultiPdtSupported";
    public static final String MPVL = "true";
    
    public static void showError(final String code, final String message, final String detailMessage, final String title, final int codeInt) {
        try {
            LUtil.errorDet = null;
            LUtil.errorDet = new ErrorDetails(codeInt, code, message, detailMessage);
            final OptionDialogInformer error = new OptionDialogInformer();
            if (title.equalsIgnoreCase("ERROR")) {
                error.setDialogType(1);
            }
            else if (title.equalsIgnoreCase("WARNING")) {
                error.setDialogType(0);
            }
            else {
                error.setDialogType(2);
            }
            error.setDialogTitle(ToolsUtils.getString(title));
            error.setMessage(ToolsUtils.getString(message));
            error.setDetailedMessage(ToolsUtils.getString(code) + "\n" + ToolsUtils.getString(detailMessage));
            error.showOptionDialog();
        }
        catch (final Error e) {
            showCMDError(code, message, detailMessage, codeInt);
        }
    }
    
    public static void showCMDError(final String code, final String message, final String detailMessage, final int codeInt) {
        LUtil.errorDet = null;
        LUtil.errorDet = new ErrorDetails(codeInt, code, message, detailMessage);
        if (LUtil.isISMP) {
            setErrorCode(code);
            setErrorMessage(message);
            setDetailedErrorMessage(detailMessage);
        }
        else {
            System.out.println(ToolsUtils.getString(code) + "\n" + ToolsUtils.getString(message) + "\n" + ToolsUtils.getString(detailMessage));
        }
    }
    
    public static void setDir(final String dirArg) {
        if (dirArg == null) {
            return;
        }
        LUtil.dir = dirArg;
    }
    
    public static String getDir() {
        return LUtil.dir;
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
    
    public static Date getTheDate(final String date, final boolean boo) {
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
    
    public static void setLicenseDir(final String sDir) {
        LUtil.subDir = sDir;
    }
    
    public static String getLicenseDir() {
        return LUtil.subDir;
    }
    
    public static int getNewType(final String type) {
        if (type.equalsIgnoreCase("Standard")) {
            return 0;
        }
        if (type.equalsIgnoreCase("Professional")) {
            return 1;
        }
        return -1;
    }
    
    public static int getNewCategory(final String category) {
        if (category.equalsIgnoreCase("OEM")) {
            return 0;
        }
        if (category.equalsIgnoreCase("Enterprise")) {
            return 1;
        }
        if (category.equalsIgnoreCase("Service Provider")) {
            return 2;
        }
        return -1;
    }
    
    public static String getMapping(final int type) {
        if (type == 2) {
            return "Carrier";
        }
        if (type == 1) {
            return "Enterprise";
        }
        return "WorkGroup";
    }
    
    public static void setBackwardSupport(final boolean backward) {
        LUtil.showBackward = backward;
    }
    
    public static boolean getBackwardSupport() {
        return LUtil.showBackward;
    }
    
    public static void setTrialMacSupport(final boolean macTrial) {
        LUtil.isTrialMacBased = macTrial;
    }
    
    public static boolean isTrialMacBased() {
        return LUtil.isTrialMacBased;
    }
    
    public static String getFileName(final URL fileUrl) throws Exception {
        final String javaVersion = System.getProperty("java.version");
        String fileName = null;
        if (!javaVersion.startsWith("1.4") && !javaVersion.startsWith("1.5")) {
            if (!javaVersion.startsWith("1.6")) {
                return fileUrl.getFile();
            }
        }
        try {
            final Class a = Class.forName("java.net.URI");
            final Class[] classArray = { Class.forName("java.lang.String") };
            final Constructor cons = a.getConstructor((Class[])classArray);
            final Object[] args = { fileUrl.toString() };
            final Object uri = cons.newInstance(args);
            final Method method = a.getMethod("getPath", (Class[])null);
            fileName = (String)method.invoke(uri, (Object[])null);
            return fileName;
        }
        catch (final Exception ex) {
            throw ex;
        }
        fileName = fileUrl.getFile();
        return fileName;
    }
    
    public static String getUniqueID() {
        final String os = System.getProperty("os.name");
        final String dir = getDir();
        String ID = null;
        if (os.startsWith("Windows")) {
            final String winPath = dir + File.separator + "bin" + File.separator + "UniqueID.exe";
            if (new File(winPath).exists()) {
                ID = execute(winPath + " java");
            }
        }
        else if (os.startsWith("Sun")) {
            final String solPath = dir + File.separator + "bin" + File.separator + "UniqueIDSolaris.sh";
            if (new File(solPath).exists()) {
                ID = execute("sh " + solPath + " java");
            }
        }
        else if (os.startsWith("HP")) {
            final String hpPath = dir + File.separator + "bin" + File.separator + "UniqueIDHP-UX.sh";
            if (new File(hpPath).exists()) {
                ID = execute("sh " + hpPath + " java");
            }
        }
        else {
            final String unixPath = dir + File.separator + "bin" + File.separator + "UniqueIDLinux.sh";
            if (new File(unixPath).exists()) {
                ID = execute("sh " + unixPath + " java");
            }
        }
        final int index = ID.indexOf("is");
        if (ID == null || index == -1) {
            System.out.println("The native file is not present");
            return null;
        }
        final String machineID = ID.substring(index + 2);
        return machineID.trim();
    }
    
    private static String execute(final String s) {
        try {
            final Process p = Runtime.getRuntime().exec(s);
            final InputStream input = p.getInputStream();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String buf = "";
            while (true) {
                final String temp = reader.readLine();
                if (temp == null) {
                    break;
                }
                if (temp.startsWith("Your")) {
                    buf = temp;
                    break;
                }
            }
            return buf;
        }
        catch (final Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    public static File getTheDetailsFile(final Class obj) {
        final String fileNameArg = "LICENSE_DETAILS";
        File lFile = new File(fileNameArg);
        final String dir = getDir();
        if (!lFile.exists()) {
            lFile = new File(dir + File.separatorChar + fileNameArg);
            if (lFile.exists()) {
                return lFile;
            }
            final URL fileUrl = obj.getResource("/" + fileNameArg);
            try {
                final String fName = getFileName(fileUrl);
                lFile = new File(fName);
            }
            catch (final Exception ex) {}
        }
        return lFile;
    }
    
    public static String[] getUserArray(final String licenseFile) {
        String[] userArray = null;
        try {
            final InputFileParser parser = new InputFileParser(licenseFile);
            final DataClass data = parser.getDataClass();
            final ArrayList userList = data.getUserList();
            final int size = userList.size();
            userArray = new String[size];
            for (int i = 0; i < size; ++i) {
                userArray[i] = userList.get(i);
            }
        }
        catch (final Exception exp) {
            return null;
        }
        return userArray;
    }
    
    public static String getCompanyName(final String licenseFile) {
        String companyName = null;
        try {
            final InputFileParser parser = new InputFileParser(licenseFile);
            final DataClass data = parser.getDataClass();
            final ArrayList userList = data.getUserList();
            final User user = data.getUserObject(userList.get(0));
            companyName = user.getCompanyName();
        }
        catch (final Exception exp) {
            return null;
        }
        return companyName;
    }
    
    public static void setISMP(final boolean b) {
        LUtil.isISMP = b;
    }
    
    public static boolean isISMP() {
        return LUtil.isISMP;
    }
    
    public static void setErrorCode(final String code) {
        LUtil.errorCode = code;
    }
    
    public static String getErrorCode() {
        return ToolsUtils.getString(LUtil.errorCode);
    }
    
    public static void setErrorMessage(final String msg) {
        LUtil.errorMsg = msg;
    }
    
    public static String getErrorMessage() {
        return ToolsUtils.getString(LUtil.errorMsg);
    }
    
    public static void setDetailedErrorMessage(final String msg) {
        LUtil.detErrorMsg = msg;
    }
    
    public static String getDetailedErrorMessage() {
        return ToolsUtils.getString(LUtil.detErrorMsg);
    }
    
    public static void copyFile(final File from, final File to) {
        if (from.equals(to)) {}
        final byte[] buf = new byte[4096];
        try {
            final FileInputStream fis = new FileInputStream(from);
            final FileOutputStream fos = new FileOutputStream(to);
            int i = 0;
            do {
                i = fis.read(buf);
                if (i != -1) {
                    fos.write(buf, 0, i);
                }
            } while (i != -1);
            fis.close();
            fos.close();
        }
        catch (final Exception ex) {}
    }
    
    public static void setErrorDetails(final ErrorDetails errorDetail) {
        LUtil.errorDet = errorDetail;
    }
    
    public static ErrorDetails getErrorDetails() {
        return LUtil.errorDet;
    }
    
    public static void deleteFilesInDir(final File dir) {
        final String[] arr$;
        final String[] files = arr$ = dir.list();
        for (final String file : arr$) {
            new File(dir.getAbsoluteFile() + File.separator + file).delete();
        }
    }
    
    static {
        LUtil.dir = System.getProperty("user.dir");
        LUtil.subDir = "classes";
        LUtil.showBackward = false;
        LUtil.isTrialMacBased = false;
        LUtil.isISMP = false;
        LUtil.errorCode = null;
        LUtil.errorMsg = null;
        LUtil.detErrorMsg = null;
        LUtil.errorDet = null;
    }
}
