package com.adventnet.tools.prevalent;

import java.util.StringTokenizer;
import java.util.Locale;
import javax.swing.LookAndFeel;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.File;
import java.util.logging.Logger;

public final class Wield
{
    private static final Logger LOGGER;
    private static Wield bare;
    private String title;
    private boolean displayFlag;
    private String dir;
    private final int NEWLICENSE = 0;
    private final int SP7LICENSE = 1;
    private final int OLDLICENSE = 2;
    private final int RUNTIME_LICENSE = 3;
    private int LICENSETYPE;
    private String licenseFileDir;
    public final int STANDARD = 0;
    public final int PROFESSIONAL = 1;
    public final int OEM = 0;
    public final int ENTERPRISE = 1;
    public final int SERVICE_PROVIDER = 2;
    private Object lkey;
    private NThreadBare ntb;
    private ThreadWorn ntw;
    private RTObject rtObject;
    private static final String licenseBackUpDir = "keybkp";
    private static int restorationCount;
    
    private Wield() {
        this.title = null;
        this.displayFlag = false;
        this.dir = System.getProperty("user.dir");
        this.LICENSETYPE = -1;
        this.licenseFileDir = "classes";
        this.lkey = null;
        this.ntb = null;
        this.ntw = null;
        this.rtObject = null;
    }
    
    public static Wield getInstance() {
        if (Wield.bare == null) {
            Wield.bare = new Wield();
        }
        return Wield.bare;
    }
    
    public void validateInvoke(final String title) {
        this.validateInvoke(title, this.dir, true, this.licenseFileDir, false, null, null);
    }
    
    public void validateInvoke(final String title, final String dir) {
        this.validateInvoke(title, dir, true, this.licenseFileDir, false, null, null);
    }
    
    public void validateInvoke(final String title, final boolean displayFlag) {
        this.validateInvoke(title, null, displayFlag, this.licenseFileDir, false, null, null);
    }
    
    public void validateInvoke(final String title, final String dirArg, final boolean displayFlag) {
        this.validateInvoke(title, dirArg, displayFlag, this.licenseFileDir, false, null, null);
    }
    
    public void validateInvoke(final String title, final String dirArg, final boolean displayFlag, final String licenseFilePath) {
        this.validateInvoke(title, dirArg, displayFlag, licenseFilePath, false, null, null);
    }
    
    public void validateInvoke(final String title, final String dirArg, final boolean displayFlag, final String licenseFilePath, final boolean hideAgreement) {
        this.validateInvoke(title, dirArg, displayFlag, licenseFilePath, hideAgreement, null, null);
    }
    
    public void validateInvoke(final String title, final String dirArg, final boolean displayFlag, final String licenseFilePath, final boolean hideAgreement, final String licFile, final String userName) {
        try {
            this.title = title;
            LUtil.setLicenseDir(this.licenseFileDir = licenseFilePath);
            if (dirArg != null && !dirArg.equals(".")) {
                this.dir = dirArg;
            }
            if (!this.dir.endsWith(File.separator)) {
                this.dir += File.separator;
            }
            LUtil.setDir(this.dir);
            Indication.setDir(this.dir);
            this.displayFlag = displayFlag;
            if (this.checkForRuntime(displayFlag)) {
                return;
            }
            final boolean reg = Boolean.valueOf(System.getProperty("Upgrade"));
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("RE");
            strBuff.append("GK");
            strBuff.append("EY");
            strBuff.append("_DA");
            strBuff.append("TA");
            final File regFile = this.getTheFile(strBuff.toString());
            if (regFile != null && regFile.exists() && !reg) {
                this.LICENSETYPE = 2;
                this.invokeOldLicense(title, this.dir, displayFlag);
                return;
            }
            final StringBuffer strBuff2 = new StringBuffer();
            strBuff2.append("pet");
            strBuff2.append("info");
            strBuff2.append(".dat");
            final StringBuffer strBuff3 = new StringBuffer();
            strBuff3.append("Adv");
            strBuff3.append("ent");
            strBuff3.append("Net");
            strBuff3.append("Li");
            strBuff3.append("cens");
            strBuff3.append("e.");
            strBuff3.append("a");
            strBuff3.append("li");
            final File petFile = this.getTheFile(strBuff2.toString());
            final File aliFile = this.getTheFile(strBuff3.toString());
            if (petFile != null && petFile.exists() && !reg) {
                final File xFile = this.getXFile(this.dir, "AdventNetLicense.xml");
                if (!this.convertOldFile(displayFlag)) {
                    return;
                }
                if (xFile.exists()) {
                    this.LICENSETYPE = 0;
                    this.invokeNewLicense(title, this.dir, displayFlag, hideAgreement, licFile, userName);
                }
                else {
                    Indication indicate = null;
                    boolean isPetinfoUsed = false;
                    String regCheck = null;
                    try {
                        indicate = Indication.getInstance();
                        indicate.deSerialize();
                        isPetinfoUsed = indicate.getFirstTimeUser();
                        regCheck = indicate.getTheRegCheck();
                    }
                    catch (final Exception e) {
                        this.showError("ERROR CODE : 536", "Invalid License File", displayFlag, 536);
                        return;
                    }
                    if (isPetinfoUsed || regCheck.equals("R")) {
                        if (regCheck.equals("T")) {
                            final int noOfEvalDays = indicate.getNoOfEvalDays();
                            final Calendar cal = new GregorianCalendar();
                            final Date currDate = cal.getTime();
                            final String evalExpiryString = indicate.getEvalExpiryDate();
                            final Date evalExpiryDate = LUtil.getTheDate(evalExpiryString, false);
                            final int i = currDate.compareTo(evalExpiryDate);
                            if (i <= 0 || noOfEvalDays == 0) {
                                this.LICENSETYPE = 1;
                                this.invokeSP7License(title, this.dir, displayFlag);
                            }
                            else {
                                this.LICENSETYPE = 0;
                                this.invokeNewFreshLicense(title, this.dir, displayFlag);
                            }
                        }
                        else if (regCheck.equals("R")) {
                            this.LICENSETYPE = 1;
                            this.invokeSP7License(title, this.dir, displayFlag);
                        }
                    }
                    else if (aliFile != null && aliFile.exists() && !reg) {
                        this.aliFileCheck(aliFile, title, this.dir, displayFlag);
                    }
                    else {
                        this.LICENSETYPE = 0;
                        this.invokeNewLicense(title, this.dir, displayFlag, hideAgreement, licFile, userName);
                    }
                }
            }
            else if (aliFile != null && aliFile.exists() && !reg) {
                this.aliFileCheck(aliFile, title, this.dir, displayFlag);
            }
            else {
                if (!this.convertOldFile(displayFlag)) {
                    return;
                }
                this.LICENSETYPE = 0;
                this.invokeNewLicense(title, this.dir, displayFlag, hideAgreement, licFile, userName);
            }
        }
        catch (final UnsupportedOperationException e2) {
            this.validateInvoke(title, dirArg, false, licenseFilePath, hideAgreement, licFile, userName);
        }
    }
    
    private Object methodInvoke(final Method meth, final Object obj, final Object[] array) {
        Object uri = null;
        try {
            uri = meth.invoke(obj, array);
        }
        catch (final InvocationTargetException in) {}
        catch (final IllegalAccessException ex) {}
        return uri;
    }
    
    private void aliFileCheck(final File aliFile, final String title, final String dir, final boolean displayFlag) {
        Object info = null;
        try {
            info = this.getLicenseInfo();
            final Method method = this.getMethod(info.getClass(), "deSerialize", null);
            this.methodInvoke(method, info, null);
        }
        catch (final Exception ex) {
            this.showError("ERROR CODE : 528", "Invalid License File", displayFlag, 528);
            return;
        }
        final Method method = this.getMethod(info.getClass(), "getFirstTimeUser", null);
        final Boolean bool = (Boolean)this.methodInvoke(method, info, null);
        final boolean alreadyTrialUser = bool;
        final Method method2 = this.getMethod(info.getClass(), "getNoOfEvalDays", null);
        final Integer daysLeft = (Integer)this.methodInvoke(method2, info, null);
        final int nodaysLeft = daysLeft;
        final Calendar cal = new GregorianCalendar();
        final Date currDate = cal.getTime();
        final Method method3 = this.getMethod(info.getClass(), "getEvalExpiryDate", null);
        final Date evalExpiryDate = (Date)this.methodInvoke(method3, info, null);
        final int i = currDate.compareTo(evalExpiryDate);
        if (alreadyTrialUser && (i <= 0 || nodaysLeft == 0)) {
            this.LICENSETYPE = 2;
            this.invokeOldLicense(title, dir, displayFlag);
            return;
        }
        Indication indicate = null;
        try {
            indicate = Indication.getInstance();
            indicate.deSerialize();
        }
        catch (final Exception e) {
            this.showError("ERROR CODE : 529", "Invalid License File", displayFlag, 529);
            return;
        }
        if (alreadyTrialUser && indicate.getTheRegCheck().equals("T") && !indicate.getFirstTimeUser()) {
            final Class a = this.getForNamedClass("com.adventnet.tools.license.CustomerLicense");
            Constructor cons = null;
            Object uri = null;
            try {
                cons = a.getConstructor((Class[])null);
            }
            catch (final NoSuchMethodException ee) {
                ee.printStackTrace();
            }
            uri = this.getObject(cons, null);
            final Class[] array = { String.class };
            final Method lSMethod = this.getMethod(uri.getClass(), "getCustomerLicenseState", array);
            final Object[] args = { dir };
            this.methodInvoke(lSMethod, uri, args);
        }
        this.LICENSETYPE = 0;
        this.invokeNewLicense(title, dir, displayFlag, false, null, null);
    }
    
    private void invokeOldLicense(final String title, final String dir, final boolean displayFlag) {
        try {
            Object info = null;
            try {
                info = this.getLicenseInfo();
                final Method method = this.getMethod(info.getClass(), "deSerialize", null);
                this.methodInvoke(method, info, null);
            }
            catch (final Exception ex) {
                this.showError("ERROR CODE : 475", "Invalid License File", displayFlag, 475);
                return;
            }
            final Method method = this.getMethod(info.getClass(), "getConfigRegUser", null);
            final Boolean bool = (Boolean)this.methodInvoke(method, info, null);
            final boolean configRegUser = bool;
            final Method method2 = this.getMethod(info.getClass(), "getNoOfEvalDays", null);
            final Integer daysLeft = (Integer)this.methodInvoke(method2, info, null);
            final int nodaysLeft = daysLeft;
            final Calendar cal = new GregorianCalendar();
            final Date currDate = cal.getTime();
            final Method method3 = this.getMethod(info.getClass(), "getRegistrationExpiryDate", null);
            final Date evalExpiryDate = (Date)this.methodInvoke(method3, info, null);
            final int i = currDate.compareTo(evalExpiryDate);
            if (!configRegUser && (i > 0 || nodaysLeft == 0)) {
                if (!this.convertOldFile(displayFlag)) {
                    return;
                }
                this.LICENSETYPE = 0;
                this.invokeNewLicense(title, dir, displayFlag, false, null, null);
            }
            else {
                this.lkey = this.getLicensingKey();
            }
        }
        catch (final Exception ex2) {}
    }
    
    private Method getMethod(final Class obj, final String name, final Class[] arg) {
        Method method = null;
        try {
            method = obj.getMethod(name, (Class[])arg);
            return method;
        }
        catch (final NoSuchMethodException ne) {
            ne.printStackTrace();
            return method;
        }
    }
    
    private Object getLicensingKey() {
        try {
            final Class a = this.getForNamedClass("com.adventnet.tools.license.LicensingKey");
            final Class[] classArray = { Class.forName("java.lang.String"), Class.forName("java.lang.String"), Boolean.TYPE };
            Constructor cons = null;
            Object uri = null;
            try {
                cons = a.getConstructor((Class[])classArray);
            }
            catch (final NoSuchMethodException ee) {
                ee.printStackTrace();
            }
            final Boolean boolValue = new Boolean(String.valueOf(this.displayFlag));
            final Object[] args = { this.title, this.dir, boolValue };
            return uri = this.getObject(cons, args);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private Object getObject(final Constructor cons, final Object[] args) {
        Object uri = null;
        try {
            uri = cons.newInstance(args);
        }
        catch (final InvocationTargetException in) {
            in.printStackTrace();
        }
        catch (final IllegalAccessException ill) {
            ill.printStackTrace();
        }
        catch (final InstantiationException inst) {
            inst.printStackTrace();
        }
        return uri;
    }
    
    private Class getForNamedClass(final String className) {
        Class forNamedClass = null;
        try {
            forNamedClass = Class.forName(className);
        }
        catch (final ClassNotFoundException exp) {
            exp.printStackTrace();
        }
        return forNamedClass;
    }
    
    private Object getLicenseInfo() {
        try {
            final StringBuffer strBuff2 = new StringBuffer();
            strBuff2.append("Adv");
            strBuff2.append("ent");
            strBuff2.append("Net");
            strBuff2.append("Li");
            strBuff2.append("cens");
            strBuff2.append("e.");
            strBuff2.append("a");
            strBuff2.append("li");
            final File aliFile = this.getTheFile(strBuff2.toString());
            final Class a = this.getForNamedClass("com.adventnet.tools.license.LicenseInfo");
            final Class[] classArray = { File.class };
            Constructor cons = null;
            Object uri = null;
            try {
                cons = a.getConstructor((Class[])classArray);
            }
            catch (final NoSuchMethodException ee) {
                ee.printStackTrace();
            }
            final Object[] args = { aliFile };
            return uri = this.getObject(cons, args);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private void invokeSP7License(final String title, final String dir, final boolean displayFlag) {
        this.ntb = new NThreadBare();
        if (dir != null) {
            try {
                Indication.setDir(dir);
            }
            catch (final Exception ex) {
                this.showError("ERROR CODE : 530", "Invalid License File", displayFlag, 530);
                return;
            }
            Clientele.getInstance().setDir(dir);
        }
        this.ntb.initalize(title, dir, displayFlag);
    }
    
    private void invokeNewFreshLicense(final String title, final String dir, final boolean displayFlag) {
        this.ntw = new ThreadWorn();
        if (dir != null) {
            try {
                LUtil.setDir(dir);
                Indication.setDir(dir);
            }
            catch (final Exception ex) {
                this.showError("ERROR CODE : 470", "Invalid License File", displayFlag, 470);
                return;
            }
        }
        this.ntw.initalizeForLauncher(title, dir, displayFlag);
        this.ntw.showLicenseScreen();
    }
    
    private void invokeNewLicense(final String title, final String dir, final boolean displayFlag, final boolean hideAgreement, final String licFile, final String userName) {
        this.ntw = new ThreadWorn();
        if (dir != null) {
            try {
                LUtil.setDir(dir);
                Indication.setDir(dir);
                this.ntw.initalize(title, dir, displayFlag, hideAgreement, licFile, userName);
            }
            catch (final Exception ex) {
                this.showError("ERROR CODE : 531", "Invalid License File", displayFlag, 531);
            }
        }
    }
    
    public long getEvaluationDays() {
        if (this.LICENSETYPE == 2) {
            try {
                final StringBuffer strBuff = new StringBuffer();
                strBuff.append("RE");
                strBuff.append("GK");
                strBuff.append("EY");
                strBuff.append("_DA");
                strBuff.append("TA");
                final File regFile = this.getTheFile(strBuff.toString());
                if (regFile != null && regFile.exists()) {
                    return 0L;
                }
                final Method method = this.getMethod(this.lkey.getClass(), "getDays", null);
                final Long longValue = (Long)this.methodInvoke(method, this.lkey, null);
                return longValue;
            }
            catch (final Exception ex) {
                return 0L;
            }
        }
        if (this.LICENSETYPE == 1) {
            String check = null;
            try {
                final Indication indication = Indication.getInstance();
                indication.deSerialize();
                check = indication.getTheRegCheck();
            }
            catch (final Exception exp) {
                return 0L;
            }
            if (check.equals("T")) {
                return this.ntb.getEvaluationDays();
            }
            return 0L;
        }
        else {
            if (this.LICENSETYPE == 0) {
                return this.ntw.getEvaluationDays();
            }
            if (this.LICENSETYPE == 3) {
                return 0L;
            }
            return 0L;
        }
    }
    
    public String getUserName() {
        if (this.LICENSETYPE == 2) {
            final Method method = this.getMethod(this.lkey.getClass(), "getLicenseDetails", null);
            Method keyDetailsMethod = null;
            Object uri = null;
            uri = this.methodInvoke(method, this.lkey, null);
            keyDetailsMethod = this.getMethod(uri.getClass(), "getUserName", null);
            return (String)this.methodInvoke(keyDetailsMethod, uri, null);
        }
        if (this.LICENSETYPE == 1) {
            try {
                final Indication indication = Indication.getInstance();
                indication.deSerialize();
                return indication.getTheUserName();
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 0) {
            return this.ntw.getUserName();
        }
        if (this.LICENSETYPE == 3) {
            return null;
        }
        return null;
    }
    
    public String getCompanyName() {
        if (this.LICENSETYPE == 2) {
            final Method method = this.getMethod(this.lkey.getClass(), "getLicenseDetails", null);
            Object uri = null;
            final Constructor cons = null;
            Method keyDetailsMethod = null;
            uri = this.methodInvoke(method, this.lkey, null);
            keyDetailsMethod = this.getMethod(uri.getClass(), "getCompanyName", null);
            return (String)this.methodInvoke(keyDetailsMethod, uri, null);
        }
        if (this.LICENSETYPE == 1) {
            try {
                final Indication indication = Indication.getInstance();
                indication.deSerialize();
                return indication.getTheCompanyName();
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 0) {
            return this.ntw.getCompanyName();
        }
        if (this.LICENSETYPE == 3) {
            return null;
        }
        return null;
    }
    
    public String getProductName() {
        if (this.LICENSETYPE == 2) {
            try {
                final Object linfo = this.getLicenseInfo();
                final Method method = this.getMethod(linfo.getClass(), "deSerialize", null);
                this.methodInvoke(method, linfo, null);
                final Method method2 = this.getMethod(linfo.getClass(), "getProductName", null);
                final Integer prod = (Integer)method2.invoke(linfo, (Object[])null);
                final int prd = prod;
                final Class a = this.getForNamedClass("com.adventnet.tools.license.LicenseMap");
                final Field field = a.getField("productName");
                final String[] lSMethod = (String[])field.get(a);
                return lSMethod[prd];
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 1) {
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                return Laterality.productName[util.getProductName()];
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 0) {
            return this.ntw.getProductName();
        }
        if (this.LICENSETYPE != 3) {
            return null;
        }
        if (this.rtObject != null) {
            return this.rtObject.getProductName();
        }
        return null;
    }
    
    public String getProductVersion() {
        if (this.LICENSETYPE == 2) {
            try {
                final Object linfo = this.getLicenseInfo();
                final Method method = this.getMethod(linfo.getClass(), "deSerialize", null);
                this.methodInvoke(method, linfo, null);
                final Method method2 = this.getMethod(linfo.getClass(), "getProductVersion", null);
                final Float bool = (Float)this.methodInvoke(method2, linfo, null);
                return String.valueOf((float)bool);
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 1) {
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                return util.getProductVersion();
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 0) {
            return this.ntw.getProductVersion();
        }
        if (this.LICENSETYPE != 3) {
            return null;
        }
        if (this.rtObject != null) {
            return this.rtObject.getProductVersion();
        }
        return null;
    }
    
    public int getLicenseType() {
        if (this.LICENSETYPE == 2) {
            final String strType = this.getOldType();
            if (strType == null) {
                return -1;
            }
            if (strType.equals("Trial User")) {
                final String type = LUtil.getMapping(2);
                final String version = this.getProductVersion();
                return this.getOldTypeInt(version, type);
            }
            final int oldType = Integer.parseInt(strType);
            final String type2 = LUtil.getMapping(oldType);
            final String version2 = this.getProductVersion();
            return this.getOldTypeInt(version2, type2);
        }
        else if (this.LICENSETYPE == 1) {
            if (this.ntb == null) {
                return -1;
            }
            final int spType = this.ntb.getType();
            final String type = LUtil.getMapping(spType);
            final String version = this.getProductVersion();
            final Product suppProd = this.getSupportedProduct(version, type);
            if (suppProd == null) {
                return -1;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductLicenseType()));
            return LUtil.getNewType(stype);
        }
        else if (this.LICENSETYPE == 0) {
            if (this.ntw != null) {
                return this.ntw.getType();
            }
            return -1;
        }
        else {
            if (this.LICENSETYPE != 3) {
                return -1;
            }
            if (this.rtObject != null) {
                return this.rtObject.getType();
            }
            return -1;
        }
    }
    
    private int getOldTypeInt(final String version, final String type) {
        final Product suppProd = this.getSupportedProduct(version, type);
        if (suppProd == null) {
            return -1;
        }
        final String stype = new String(Encode.revShiftBytes(suppProd.getProductLicenseType()));
        return LUtil.getNewType(stype);
    }
    
    public String getLicenseTypeString() {
        if (this.LICENSETYPE == 2) {
            final String strType = this.getOldType();
            if (strType == null) {
                return null;
            }
            if (strType.equals("Trial User")) {
                final String type = LUtil.getMapping(2);
                final String version = this.getProductVersion();
                return this.getOldTypeString(version, type);
            }
            final int oldType = Integer.parseInt(strType);
            final String type2 = LUtil.getMapping(oldType);
            final String version2 = this.getProductVersion();
            return this.getOldTypeString(version2, type2);
        }
        else if (this.LICENSETYPE == 1) {
            if (this.ntb == null) {
                return null;
            }
            final int spType = this.ntb.getType();
            final String type = LUtil.getMapping(spType);
            final String version = this.getProductVersion();
            final Product suppProd = this.getSupportedProduct(version, type);
            if (suppProd == null) {
                return null;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductLicenseType()));
            return stype;
        }
        else if (this.LICENSETYPE == 0) {
            if (this.ntw != null) {
                return this.ntw.getTypeString();
            }
            return null;
        }
        else {
            if (this.LICENSETYPE != 3) {
                return null;
            }
            if (this.rtObject != null) {
                return this.rtObject.getTypeString();
            }
            return null;
        }
    }
    
    private String getOldTypeString(final String version, final String type) {
        final Product suppProd = this.getSupportedProduct(version, type);
        if (suppProd == null) {
            return null;
        }
        final String stype = new String(Encode.revShiftBytes(suppProd.getProductLicenseType()));
        return stype;
    }
    
    public int getProductCategory() {
        if (this.LICENSETYPE == 2) {
            final String strType = this.getOldType();
            if (strType == null) {
                return -1;
            }
            if (strType.equals("Trial User")) {
                final String type = LUtil.getMapping(2);
                final String version = this.getProductVersion();
                return this.getOldProductCategoryInt(version, type);
            }
            final int oldType = Integer.parseInt(strType);
            final String type2 = LUtil.getMapping(oldType);
            final String version2 = this.getProductVersion();
            return this.getOldProductCategoryInt(version2, type2);
        }
        else if (this.LICENSETYPE == 1) {
            if (this.ntb == null) {
                return -1;
            }
            final int spType = this.ntb.getType();
            final String type = LUtil.getMapping(spType);
            final String version = this.getProductVersion();
            final Product suppProd = this.getSupportedProduct(version, type);
            if (suppProd == null) {
                return -1;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductCategory()));
            return LUtil.getNewCategory(stype);
        }
        else if (this.LICENSETYPE == 0) {
            if (this.ntw != null) {
                return this.ntw.getProductCategory();
            }
            return -1;
        }
        else {
            if (this.LICENSETYPE != 3) {
                return -1;
            }
            if (this.rtObject != null) {
                return this.rtObject.getProductCategory();
            }
            return -1;
        }
    }
    
    private int getOldProductCategoryInt(final String version, final String type) {
        final Product suppProd = this.getSupportedProduct(version, type);
        if (suppProd == null) {
            return -1;
        }
        final String stype = new String(Encode.revShiftBytes(suppProd.getProductCategory()));
        return LUtil.getNewCategory(stype);
    }
    
    public String getProductCategoryString() {
        if (this.LICENSETYPE == 2) {
            final String strType = this.getOldType();
            if (strType == null) {
                return null;
            }
            if (strType.equals("Trial User")) {
                final String type = LUtil.getMapping(2);
                final String version = this.getProductVersion();
                return this.getOldProductCategoryString(version, type);
            }
            final int oldType = Integer.parseInt(strType);
            final String type2 = LUtil.getMapping(oldType);
            final String version2 = this.getProductVersion();
            return this.getOldProductCategoryString(version2, type2);
        }
        else if (this.LICENSETYPE == 1) {
            if (this.ntb == null) {
                return null;
            }
            final int spType = this.ntb.getType();
            final String type = LUtil.getMapping(spType);
            final String version = this.getProductVersion();
            final Product suppProd = this.getSupportedProduct(version, type);
            if (suppProd == null) {
                return null;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductCategory()));
            return stype;
        }
        else if (this.LICENSETYPE == 0) {
            if (this.ntw != null) {
                return this.ntw.getProductCategoryString();
            }
            return null;
        }
        else {
            if (this.LICENSETYPE != 3) {
                return null;
            }
            if (this.rtObject != null) {
                return this.rtObject.getProductCategoryString();
            }
            return null;
        }
    }
    
    private String getOldProductCategoryString(final String version, final String type) {
        final Product suppProd = this.getSupportedProduct(version, type);
        if (suppProd == null) {
            return null;
        }
        final String stype = new String(Encode.revShiftBytes(suppProd.getProductCategory()));
        return stype;
    }
    
    private Product getSupportedProduct(final String version, final String type) {
        BObject bobject = null;
        try {
            final Indication indication = Indication.getInstance();
            indication.productNameDeSerialize();
            bobject = indication.getBCObject();
            return bobject.getSupportedProduct(version, type);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    
    public boolean isModulePresent(final String moduleName) {
        if (this.LICENSETYPE == 2) {
            final String strType = this.getOldType();
            if (strType == null) {
                return false;
            }
            if (strType.equals("Trial User")) {
                final String version = this.getProductVersion();
                final String type = LUtil.getMapping(2);
                return this.isOldModulePresent(version, type, moduleName);
            }
            final int oldType = Integer.parseInt(strType);
            final String type = LUtil.getMapping(oldType);
            final String version2 = this.getProductVersion();
            return this.isOldModulePresent(version2, type, moduleName);
        }
        else if (this.LICENSETYPE == 1) {
            if (this.ntb == null) {
                return false;
            }
            final int spType = this.ntb.getType();
            final String type2 = LUtil.getMapping(spType);
            final String version3 = this.getProductVersion();
            final Product suppProd = this.getSupportedProduct(version3, type2);
            if (suppProd == null) {
                return false;
            }
            final ArrayList components = suppProd.getComponents();
            if (components != null) {
                for (int size = components.size(), i = 0; i < size; ++i) {
                    final Component comp = components.get(i);
                    final String name = comp.getName();
                    if (name.equals(Encode.swap(moduleName))) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        else {
            if (this.LICENSETYPE == 0) {
                return this.ntw != null && this.ntw.isModulePresent(moduleName);
            }
            return this.LICENSETYPE == 3 && this.rtObject != null && this.rtObject.isModulePresent(moduleName);
        }
    }
    
    private boolean isOldModulePresent(final String version, final String type, final String moduleName) {
        final Product suppProd = this.getSupportedProduct(version, type);
        if (suppProd == null) {
            return false;
        }
        final ArrayList components = suppProd.getComponents();
        if (components != null) {
            for (int size = components.size(), i = 0; i < size; ++i) {
                final Component comp = components.get(i);
                final String name = comp.getName();
                if (name.equals(Encode.swap(moduleName))) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    public ArrayList getAllModules() {
        if (this.LICENSETYPE != 0) {
            return null;
        }
        if (this.ntw != null) {
            return this.ntw.getAllModules();
        }
        return null;
    }
    
    public String getAllModulesAsJson(final boolean includeOnlyLicenseModules) {
        return this.ntw.getAllModulesAsJson(includeOnlyLicenseModules);
    }
    
    public String getUserInfoJson() {
        return this.ntw.getUserInfoJson();
    }
    
    public ArrayList<HashMap<String, Properties>> getAllModuleProperties(final boolean includeOnlyLicenseModules) {
        return this.ntw.getAllModuleProperties(includeOnlyLicenseModules);
    }
    
    public Properties getModuleProperties(final String moduleName) {
        if (this.LICENSETYPE == 2) {
            final String strType = this.getOldType();
            if (strType == null) {
                return null;
            }
            if (strType.equals("Trial User")) {
                final String version = this.getProductVersion();
                final String type = LUtil.getMapping(2);
                return this.getOldProperties(version, type, moduleName);
            }
            final int oldType = Integer.parseInt(strType);
            final String type = LUtil.getMapping(oldType);
            final String version2 = this.getProductVersion();
            return this.getOldProperties(version2, type, moduleName);
        }
        else if (this.LICENSETYPE == 1) {
            if (this.ntb == null) {
                return null;
            }
            final int spType = this.ntb.getType();
            final String type2 = LUtil.getMapping(spType);
            final String version3 = this.getProductVersion();
            final Product suppProd = this.getSupportedProduct(version3, type2);
            if (suppProd == null) {
                return null;
            }
            final ArrayList components = suppProd.getComponents();
            if (components != null) {
                for (int size = components.size(), i = 0; i < size; ++i) {
                    final Component comp = components.get(i);
                    final String name = comp.getName();
                    if (name.equals(Encode.swap(moduleName))) {
                        final Properties prop = new Properties();
                        final ArrayList list = comp.getProperties();
                        for (int count = list.size(), j = 0; j < count; j += 2) {
                            final Object key = list.get(j);
                            final Object value = list.get(j + 1);
                            prop.put(key, value);
                        }
                        return prop;
                    }
                }
                return null;
            }
            return null;
        }
        else if (this.LICENSETYPE == 0) {
            if (this.ntw != null) {
                return this.ntw.getModuleProperties(moduleName);
            }
            return null;
        }
        else {
            if (this.LICENSETYPE != 3) {
                return null;
            }
            if (this.rtObject != null) {
                return this.rtObject.getModuleProperties(moduleName);
            }
            return null;
        }
    }
    
    private Properties getOldProperties(final String version, final String type, final String moduleName) {
        final Product suppProd = this.getSupportedProduct(version, type);
        if (suppProd == null) {
            return null;
        }
        final ArrayList components = suppProd.getComponents();
        if (components != null) {
            for (int size = components.size(), i = 0; i < size; ++i) {
                final Component comp = components.get(i);
                final String name = comp.getName();
                if (name.equals(Encode.swap(moduleName))) {
                    final Properties prop = new Properties();
                    final ArrayList list = comp.getProperties();
                    for (int count = list.size(), j = 0; j < count; j += 2) {
                        final Object key = list.get(j);
                        final Object value = list.get(j + 1);
                        prop.put(key, value);
                    }
                    return prop;
                }
            }
            return null;
        }
        return null;
    }
    
    public boolean isBare() {
        if (this.LICENSETYPE == 2) {
            final Method method = this.getMethod(this.lkey.getClass(), "isValidationOk", null);
            final Boolean bool = (Boolean)this.methodInvoke(method, this.lkey, null);
            return bool;
        }
        if (this.LICENSETYPE == 1) {
            return this.ntb != null && this.ntb.isBare();
        }
        if (this.LICENSETYPE == 0) {
            if (Wield.restorationCount == 0) {
                ++Wield.restorationCount;
                final RestoreBackUp rbackUp = new RestoreBackUp();
                rbackUp.licenseRestoreValidation(this.ntw, "keybkp", this.licenseFileDir, this.dir);
            }
            return this.ntw != null && this.ntw.isBare();
        }
        return this.LICENSETYPE == 3 && this.rtObject != null && this.rtObject.isBare();
    }
    
    private File getTheFile(final String fileNameArg) {
        File lFile = new File(fileNameArg);
        if (!lFile.exists()) {
            lFile = new File(this.dir + File.separatorChar + fileNameArg);
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
    
    public String getUserType() {
        if (this.LICENSETYPE == 2) {
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("RE");
            strBuff.append("GK");
            strBuff.append("EY");
            strBuff.append("_DA");
            strBuff.append("TA");
            final File regFile = this.getTheFile(strBuff.toString());
            if (regFile != null && regFile.exists()) {
                return "R";
            }
            return "T";
        }
        else {
            if (this.LICENSETYPE == 1) {
                String check = null;
                try {
                    final Indication indication = Indication.getInstance();
                    indication.deSerialize();
                    check = indication.getTheRegCheck();
                }
                catch (final Exception exp) {
                    return null;
                }
                return check;
            }
            if (this.LICENSETYPE == 0) {
                return this.ntw.getUserType();
            }
            if (this.LICENSETYPE != 3) {
                return null;
            }
            if (this.rtObject != null) {
                return this.rtObject.getUserType();
            }
            return null;
        }
    }
    
    private void showError(final String code, final String invalidFile, final boolean mode, final int codeInt) {
        final String contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        if (mode) {
            LUtil.showError(code, invalidFile, contactMesg, "Error", codeInt);
        }
        else {
            LUtil.showCMDError(code, invalidFile, contactMesg, codeInt);
        }
    }
    
    private boolean convertOldFile(final boolean displayFlag) {
        try {
            final Indication indication = Indication.getInstance();
            indication.deSerialize();
            return true;
        }
        catch (final FileNotFoundException fnfe) {
            this.showError("ERROR CODE : 1003", "File access denied, Check the user previllages , For more details", displayFlag, 1003);
            return false;
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
            this.showError("ERROR CODE : 1010", "Invalid License File", displayFlag, 1010);
            return false;
        }
        catch (final Exception exp_) {
            exp_.printStackTrace();
            this.showError("ERROR CODE : 535", "Invalid License File", displayFlag, 535);
            return false;
        }
    }
    
    private String getOldType() {
        Method keyDetailsMethod = null;
        Object uri = null;
        final Method method = this.getMethod(this.lkey.getClass(), "getLicenseDetails", null);
        final Class a = method.getReturnType();
        uri = this.methodInvoke(method, this.lkey, null);
        keyDetailsMethod = this.getMethod(uri.getClass(), "getTypeOfLicense", null);
        return (String)this.methodInvoke(keyDetailsMethod, uri, null);
    }
    
    private boolean checkForRuntime(final boolean displayFlag) {
        String lFile = null;
        final StringBuffer runFile = new StringBuffer();
        runFile.append("com/adv");
        runFile.append("entnet/tools");
        runFile.append("/prevalent/");
        runFile.append("Run");
        runFile.append("time");
        runFile.append(".xml");
        final URL fileUrl = this.getClass().getResource("/" + runFile.toString());
        try {
            lFile = LUtil.getFileName(fileUrl);
        }
        catch (final Exception ex2) {}
        if (lFile == null) {
            return false;
        }
        try {
            this.LICENSETYPE = 3;
            this.rtObject = new RTObject(lFile, displayFlag);
        }
        catch (final Exception ex) {
            this.showError("ERROR CODE : 481", "Invalid License File", displayFlag, 481);
            return false;
        }
        return true;
    }
    
    public void showRegistrationScreen(final LookAndFeel laf) {
        if (this.ntw == null) {
            (this.ntw = new ThreadWorn()).initalizeForLauncher(this.title, this.dir, this.displayFlag);
        }
        this.ntw.showRegistrationScreen(laf);
    }
    
    public boolean invokeRegistrationScreen(final LookAndFeel laf) {
        if (this.ntw == null) {
            (this.ntw = new ThreadWorn()).initalizeForLauncher(this.title, this.dir, this.displayFlag);
        }
        return this.ntw.invokeRegistrationScreen(laf);
    }
    
    public String getEvaluationExpiryDate() {
        if (this.LICENSETYPE == 2) {
            return null;
        }
        if (this.LICENSETYPE == 1) {
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                return util.getEvalExpiryDate();
            }
            catch (final Exception exp) {
                return null;
            }
        }
        if (this.LICENSETYPE == 0) {
            if (this.ntw.getEvaluationExpiryDate().equals("never") || this.ntw.getEvaluationExpiryDate() == null || this.ntw.getEvaluationExpiryDate().equals("null") || this.ntw.getEvaluationExpiryDate().equals("")) {
                return this.ntw.getEvaluationExpiryDate();
            }
            return this.addOneMonth(this.ntw.getEvaluationExpiryDate());
        }
        else {
            if (this.LICENSETYPE == 3) {
                return null;
            }
            return null;
        }
    }
    
    public void setLocale(final String language, final String country) {
        if (language != null && country != null) {
            ToolsUtils.setLocale(new Locale(language, country));
        }
    }
    
    public void setBundleName(final String fileName) {
        if (fileName != null) {
            ToolsUtils.setBundleName(fileName);
        }
    }
    
    public void setSearchPath(final String path) {
        if (path != null) {
            ToolsUtils.setSearchPath(path);
        }
    }
    
    private File getXFile(final String homeDir, final String fileName) {
        File lFile = new File(homeDir + File.separator + LUtil.getLicenseDir() + File.separator + fileName);
        if (!lFile.exists()) {
            final URL fileUrl = this.getClass().getResource("/" + fileName);
            try {
                final String fName = LUtil.getFileName(fileUrl);
                lFile = new File(fName);
            }
            catch (final Exception ex) {}
        }
        return lFile;
    }
    
    public void reloadXmlFile() {
        this.ntw.reInitialize();
    }
    
    public int getRemainingDaysForModule(final String addonmodule) {
        int remainingDays = -1;
        final Wield licenseObj = getInstance();
        final Properties addonLicenseProps = licenseObj.getModuleProperties(addonmodule);
        if (addonLicenseProps != null) {
            final String expiryDateStr = addonLicenseProps.getProperty("Expiry");
            if (expiryDateStr == null) {
                return -2;
            }
            final StringTokenizer dateToks = new StringTokenizer(expiryDateStr, "-");
            final int year = Integer.parseInt(dateToks.nextToken());
            final int month = Integer.parseInt(dateToks.nextToken()) - 1;
            final int date = Integer.parseInt(dateToks.nextToken());
            final Calendar cal = Calendar.getInstance();
            cal.set(year, month, date);
            final Date expiryDate = cal.getTime();
            final long expiryInMillis = expiryDate.getTime();
            final long remainingDaysInMillis = expiryInMillis - System.currentTimeMillis() + 1L;
            remainingDays = (int)(remainingDaysInMillis / 86400000L);
            if (--remainingDays < 0) {
                remainingDays = -1;
            }
        }
        return remainingDays;
    }
    
    public String addOneMonth(final String actDate) {
        final ArrayList dateList = new ArrayList();
        final StringTokenizer st = new StringTokenizer(actDate, " ");
        while (st.hasMoreTokens()) {
            dateList.add(st.nextElement());
        }
        final int y = new Integer(dateList.get(0));
        final int m = new Integer(dateList.get(1));
        final int d = new Integer(dateList.get(2));
        return Integer.toString(y) + " " + Integer.toString(m + 1) + " " + Integer.toString(d);
    }
    
    public String getUserTypeString() {
        return this.ntw.getUserTypeString();
    }
    
    public String getAMSExpiry() {
        return this.ntw.getAMSExpiry();
    }
    
    public boolean doValidation(final String existingLicenseDir, final String userName, final String newLicenseDir, final boolean mode, final boolean licenseBack) {
        final Validation valid = Validation.getInstance();
        final boolean validationResult = valid.doValidation(existingLicenseDir, userName, newLicenseDir, mode, true);
        if (validationResult && licenseBack) {
            this.copyLicenseFiles(existingLicenseDir, newLicenseDir);
        }
        return validationResult;
    }
    
    private void copyLicenseFiles(final String homeDir, final String newLicenseFile) {
        Wield.LOGGER.info("license back up started ");
        final File backUpDir = new File(homeDir + File.separator + "keybkp");
        if (!backUpDir.exists()) {
            backUpDir.mkdirs();
        }
        final RestoreBackUp rbackUp = new RestoreBackUp();
        try {
            rbackUp.copyFile(homeDir + File.separator + this.licenseFileDir + File.separator + "product.dat", homeDir + File.separator + "keybkp" + File.separator + "product.dat");
            rbackUp.copyFile(homeDir + File.separator + this.licenseFileDir + File.separator + "petinfo.dat", homeDir + File.separator + "keybkp" + File.separator + "petinfo.dat");
            rbackUp.copyFile(newLicenseFile, homeDir + File.separator + "keybkp" + File.separator + "AdventNetLicense.xml");
            System.out.println("license back up completed ..");
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }
    
    public void restoreLicenseBackUp() {
        Wield.LOGGER.info("license restoration  " + Wield.restorationCount);
        if (Wield.restorationCount == 0) {
            ++Wield.restorationCount;
            final RestoreBackUp rbackUp = new RestoreBackUp();
            rbackUp.licenseRestoreValidation(this.ntw, "keybkp", this.licenseFileDir, this.dir);
        }
    }
    
    public boolean changeTrialEdition(final String trialFileName) {
        boolean validationResult = false;
        if (getInstance().getUserType().equals("T") && getInstance().getEvaluationDays() > 0L) {
            final String upgradeTrialLicense = LUtil.getDir() + File.separator + "editionchange" + File.separator + trialFileName;
            validationResult = this.doValidation(LUtil.getDir(), "Evaluation User", upgradeTrialLicense, false, false);
            if (validationResult) {
                Validation.getInstance().copyLicenseFile(LUtil.getDir(), upgradeTrialLicense);
            }
        }
        return validationResult;
    }
    
    public int getRestoreRetryCount() {
        return Wield.restorationCount;
    }
    
    public void applyLicense(final String checkLicenseInClient, final String productName, final String licenseFile) throws Exception {
        final String homeDir = System.getProperty("server.dir");
        final Wield wield = getInstance();
        final String freeXmlPath = homeDir + File.separatorChar + "lib" + File.separatorChar + "Free.xml";
        System.setProperty("IsWeb", "true");
        wield.setLocale("en", "US");
        wield.setBundleName("LicenseManager.properties");
        wield.setSearchPath(System.getProperty("server.home") + "/conf");
        if (checkLicenseInClient != null && checkLicenseInClient.equals("true")) {
            if (licenseFile == null || productName == null) {
                throw new IllegalArgumentException("Either the licence file or the ProuctName is InValid.");
            }
            System.setProperty("CheckLicenseInClient", checkLicenseInClient);
            System.setProperty("ProductName", productName);
            System.setProperty("LicenseValidator", licenseFile);
            final Indication i = Indication.getInstance();
            i.deSerialize();
            if (!i.getFirstTimeUser()) {
                System.setProperty("WEB_LICENSE_CHECK", "true");
                wield.validateInvoke("License Agreement", homeDir, false, "lib", true);
            }
        }
        else {
            System.setProperty("CheckLicenseInClient", "false");
            wield.validateInvoke("License Agreement", homeDir, false, "lib", true);
        }
        if (!wield.isBare()) {
            final File freeXmlFile = new File(freeXmlPath);
            if (freeXmlFile.exists()) {
                final Validation valid = Validation.getInstance();
                valid.doValidation(homeDir, "Evaluation User", freeXmlPath, false);
                ConsoleOut.println("Your license has expired. Moving to free edition");
                valid.copyLicenseFile(homeDir, freeXmlPath);
            }
            else if (System.getProperty("CheckLicenseInClient").equals("false")) {
                System.setProperty("IsWeb", "false");
                wield.validateInvoke("License Agreement", homeDir, false, "lib", false);
                if (!wield.isBare()) {
                    System.exit(0);
                }
            }
        }
        else {
            String userType = wield.getUserType();
            userType = ((userType == null) ? "" : userType);
            if (userType.equals("R")) {
                ConsoleOut.println("This copy is licensed to " + wield.getCompanyName() + "\n");
            }
            else if (userType.equals("F")) {
                ConsoleOut.println("Free edition...\n");
            }
        }
    }
    
    public void applyLicense(final String checkLicenseInClient, final String productName, final String licenseFile, final String customValidator) throws Exception {
        if (customValidator != null) {
            final ApplyLicense applyLicense = (ApplyLicense)Class.forName(customValidator).newInstance();
            applyLicense.applyLicense();
        }
        else {
            this.applyLicense(checkLicenseInClient, productName, licenseFile);
        }
    }
    
    public String getRenewalLink() {
        final GetSalesLink gsl = new GetSalesLink();
        return gsl.getSalesLink();
    }
    
    public String getCustomUserType() {
        return this.ntw.getCustomUserType();
    }
    
    public boolean licenseMig(final String licDir) throws Exception {
        LUtil.setLicenseDir(licDir);
        final Indication ind = Indication.getInstance();
        ind.deSerialize();
        if (new ReadLicense().isAllowedProduct(ind.getProductName())) {
            final int baseId = new ReadBaseLicense().readLicense().getBaseObject()[0];
            ind.addEntry(baseId);
            ind.serialize();
            ind.deSerialize();
            if (ind.getProductName() == baseId) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(Wield.class.getName());
        Wield.bare = null;
        Wield.restorationCount = 0;
    }
}
