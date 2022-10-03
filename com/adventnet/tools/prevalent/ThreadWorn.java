package com.adventnet.tools.prevalent;

import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.util.Calendar;

public final class ThreadWorn
{
    private static ThreadWorn threadBare;
    private String title;
    private String lFilePath;
    private boolean GUI;
    private CMDClass cmdClass;
    private LUIGeneral frame;
    private Validation valid;
    
    public ThreadWorn() {
        this.title = null;
        this.lFilePath = "";
        this.GUI = true;
        this.cmdClass = null;
        this.frame = null;
        this.valid = null;
    }
    
    public static ThreadWorn getInstance() {
        if (ThreadWorn.threadBare == null) {
            ThreadWorn.threadBare = new ThreadWorn();
        }
        return ThreadWorn.threadBare;
    }
    
    public void reInitialize() {
        if (ThreadWorn.threadBare != null) {
            ThreadWorn.threadBare = null;
        }
        this.valid.reInitialize();
    }
    
    public void initalize(final String title, final String dir, final boolean displayFlag) {
        this.initalize(title, dir, displayFlag, false, null, null);
    }
    
    public void initalize(final String title, final String dir, final boolean displayFlag, final boolean hideAgreement) {
        this.initalize(title, dir, displayFlag, hideAgreement, null, null);
    }
    
    public void initalize(final String title, final String dir, final boolean displayFlag, boolean hideAgreement, final String licFile, final String userName) {
        final String invalidFile = "Invalid License File";
        final String contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.GUI = displayFlag;
        if (dir != null) {
            this.lFilePath = dir;
        }
        if (licFile != null && userName != null) {
            LUtil.setISMP(true);
        }
        this.title = title;
        boolean alreadyStarted = false;
        String regCheck = null;
        String evalExpiry = null;
        boolean bSupport = false;
        boolean trialMac = false;
        Date installationExpiryDate = null;
        Date lastAccessedDate = null;
        try {
            final Indication util = Indication.getInstance();
            util.deSerialize();
            util.productNameDeSerialize();
            alreadyStarted = util.getFirstTimeUser();
            regCheck = util.getTheRegCheck();
            evalExpiry = util.getEvalExpiryDate();
            bSupport = util.getBackwardSupport();
            trialMac = util.isTrialMacBased();
            LUtil.setBackwardSupport(bSupport);
            LUtil.setTrialMacSupport(trialMac);
            final String installationExpiryString = util.getInstallationExpiryDate();
            final Calendar cal = Calendar.getInstance();
            lastAccessedDate = LUtil.getCurrentDate(cal);
            installationExpiryDate = LUtil.getTheDate(installationExpiryString, false);
        }
        catch (final Exception e) {
            if (this.GUI) {
                LUtil.showError("ERROR CODE : 532\n", invalidFile, contactMesg, "Error", 532);
            }
            else {
                LUtil.showCMDError("ERROR CODE : 532\n", invalidFile, contactMesg, 532);
            }
            return;
        }
        String evalFile = this.lFilePath + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
        final File standardEvalFile = new File(evalFile);
        if (alreadyStarted) {
            (this.valid = Validation.getInstance()).validate(this.lFilePath, this.GUI);
            if (this.valid.isVRTUser()) {
                final boolean reg = Boolean.valueOf(System.getProperty("Upgrade"));
                final boolean licenseUpgrade = Boolean.valueOf(System.getProperty("licenseUpgrade"));
                if (licenseUpgrade) {
                    this.licenseUpgrade();
                    return;
                }
                if (reg) {
                    this.nonXWindowRegister();
                }
                else if (regCheck.equals("T")) {
                    final long days = this.valid.getEvaluationDays();
                    if (days != 0L) {
                        System.out.println(ToolsUtils.getString("This evaluation copy is valid for") + " " + days + " " + ToolsUtils.getString("days"));
                    }
                    else {
                        String sr = null;
                        sr = this.valid.getUserType();
                        if (!sr.equals("F")) {
                            System.out.println(ToolsUtils.getString("Today is the last day for evaluation"));
                        }
                        else {
                            System.out.println(ToolsUtils.getString("This product is running in Free license mode"));
                        }
                    }
                }
            }
            boolean b = false;
            if (System.getProperty("IsWeb") != null) {
                b = Boolean.valueOf(System.getProperty("IsWeb"));
            }
            if (!this.valid.getRegister() || b) {
                return;
            }
            this.showLicenseScreen();
        }
        else {
            final String autoUserName = System.getProperty("tools.licenseUserName");
            final String autoLicenseFile = System.getProperty("tools.licenseFileName");
            if (autoUserName != null && autoLicenseFile != null) {
                this.valid = Validation.getInstance();
                final boolean success = this.valid.doValidation(this.lFilePath, autoUserName, autoLicenseFile, this.GUI);
                if (success) {
                    this.valid.copyLicenseFile(this.lFilePath, autoLicenseFile);
                }
                return;
            }
            if (licFile != null && userName != null) {
                evalFile = licFile;
            }
            if (this.compareTo(installationExpiryDate, lastAccessedDate) <= 0) {
                hideAgreement = false;
            }
            if (this.GUI) {
                try {
                    if (standardEvalFile.exists() && !hideAgreement) {
                        this.frame = new LUIGeneral(title, this.lFilePath, this.valid, evalFile, true);
                    }
                    else if (!hideAgreement) {
                        this.frame = new LUIGeneral(title, this.lFilePath, this.valid);
                    }
                    if (!hideAgreement) {
                        this.frame.setVisible(true);
                        this.frame.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(final WindowEvent evt) {
                                System.exit(0);
                            }
                        });
                        while (!this.frame.isValidationOk()) {
                            try {
                                Thread.sleep(1000L);
                            }
                            catch (final Exception ex) {}
                        }
                        this.valid = this.frame.getValidation();
                    }
                    else {
                        this.validateWithoutAgreement(this.lFilePath, evalFile, userName);
                    }
                }
                catch (final UnsupportedOperationException e2) {
                    System.out.println(ToolsUtils.getString("UnsupportedOperationException detected. Continuing in the command line mode..."));
                    this.validateInCommandLine(standardEvalFile, hideAgreement, this.lFilePath, evalFile, userName);
                }
                catch (final Throwable err) {
                    err.printStackTrace();
                    this.validateInCommandLine(standardEvalFile, hideAgreement, this.lFilePath, evalFile, userName);
                }
            }
            else {
                this.validateInCommandLine(standardEvalFile, hideAgreement, this.lFilePath, evalFile, userName);
            }
        }
    }
    
    private void validateInCommandLine(final File standardEvalFile, final boolean hideAgreement, final String lFilePath, final String evalFile, final String userName) {
        if (standardEvalFile.exists() && !hideAgreement) {
            this.cmdClass = new CMDClass(lFilePath, this.valid, evalFile, true);
        }
        else if (!hideAgreement) {
            this.cmdClass = new CMDClass(lFilePath, this.valid);
        }
        if (!hideAgreement) {
            this.cmdClass.invokeInCMD();
            this.valid = this.cmdClass.getValidation();
        }
        else {
            this.validateWithoutAgreement(lFilePath, evalFile, userName);
        }
    }
    
    private void validateWithoutAgreement(final String lFilePath, final String licenseFile, String userName) {
        boolean del = false;
        if (this.valid == null) {
            this.valid = Validation.getInstance();
        }
        if (userName == null) {
            del = true;
            this.cmdClass = new CMDClass();
            userName = this.cmdClass.getUserList(licenseFile).elementAt(0);
        }
        final boolean success = this.valid.doValidation(lFilePath, userName, licenseFile, false, false);
        if (success) {
            this.valid.copyLicenseFile(lFilePath, licenseFile);
            boolean allow = true;
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                allow = util.allowFreeAfterExpiry();
            }
            catch (final Exception ex) {}
            if (del && !this.valid.getUserType().equals("F")) {
                new File(licenseFile).delete();
            }
            else if (this.valid.getUserType().equals("F") && !allow) {
                new File(licenseFile).delete();
            }
        }
    }
    
    private void nonXWindowRegister() {
        (this.cmdClass = new CMDClass(this.lFilePath, this.valid)).nonXWindowRegister();
        this.valid = this.cmdClass.getValidation();
    }
    
    private void licenseUpgrade() {
        (this.cmdClass = new CMDClass(this.lFilePath, this.valid)).invokeInCMD();
        this.valid = this.cmdClass.getValidation();
    }
    
    public boolean isBare() {
        return this.valid != null && this.valid.isVRTUser();
    }
    
    public String getUserType() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getUserType();
    }
    
    public int getType() {
        if (this.valid == null) {
            return -1;
        }
        return this.valid.getType();
    }
    
    public String getTypeString() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getTypeString();
    }
    
    public int getProductCategory() {
        if (this.valid == null) {
            return -1;
        }
        return this.valid.getProductCategory();
    }
    
    public String getProductCategoryString() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getProductCategoryString();
    }
    
    public String getUserName() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getUserName();
    }
    
    public String getCompanyName() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getCompanyName();
    }
    
    public String getProductName() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getProductName();
    }
    
    public String getProductVersion() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getProductVersion();
    }
    
    public String getEvaluationExpiryDate() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getEvaluationExpiryDate();
    }
    
    public long getEvaluationDays() {
        if (this.valid == null) {
            return 0L;
        }
        return this.valid.getEvaluationDays();
    }
    
    public String getUserInfoJson() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getUserInfoJson();
    }
    
    public String getAllModulesAsJson(final boolean includeOnlyLicenseModules) {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getAllModulesAsJson(includeOnlyLicenseModules);
    }
    
    public ArrayList<HashMap<String, Properties>> getAllModuleProperties(final boolean includeOnlyLicenseModules) {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getAllModuleProperties(includeOnlyLicenseModules);
    }
    
    public Properties getModuleProperties(final String moduleName) {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getModuleProperties(moduleName);
    }
    
    public ArrayList getAllModules() {
        if (this.valid == null) {
            return null;
        }
        return this.valid.getAllModules();
    }
    
    public boolean isModulePresent(final String moduleName) {
        return this.valid != null && this.valid.isModulePresent(moduleName);
    }
    
    public void showRegistrationScreen(final LookAndFeel laf) {
        try {
            (this.frame = new LUIGeneral(this.title, this.lFilePath, this.valid)).setValidation(true);
            this.frame.showSecond(true);
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(this.frame);
            while (!this.frame.isValidationOk()) {
                try {
                    Thread.sleep(1000L);
                }
                catch (final Exception ex) {}
            }
            this.valid = this.frame.getValidation();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean invokeRegistrationScreen(final LookAndFeel laf) {
        try {
            (this.frame = new LUIGeneral(this.title, this.lFilePath, this.valid)).setValidation(false);
            this.frame.showSecond(true);
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(this.frame);
            while (!this.frame.isValidationOk()) {
                try {
                    Thread.sleep(1000L);
                }
                catch (final Exception ex) {}
            }
            this.valid = this.frame.getValidation();
        }
        catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    void showLicenseScreen() {
        if (this.GUI) {
            try {
                (this.frame = new LUIGeneral(this.title, this.lFilePath, this.valid)).showSecond(false);
                while (!this.frame.isValidationOk()) {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (final Exception ex) {}
                }
                this.valid = this.frame.getValidation();
            }
            catch (final Error e) {
                (this.cmdClass = new CMDClass(this.lFilePath, this.valid)).invokeInCMD();
                this.valid = this.cmdClass.getValidation();
            }
        }
        else {
            (this.cmdClass = new CMDClass(this.lFilePath, this.valid)).invokeInCMD();
            this.valid = this.cmdClass.getValidation();
        }
    }
    
    public void initalizeForLauncher(final String title, final String dir, final boolean displayFlag) {
        this.GUI = displayFlag;
        if (dir != null) {
            this.lFilePath = dir;
        }
        this.title = title;
    }
    
    int compareTo(final Date date1, final Date date2) {
        final long thisTime = date1.getTime();
        final long anotherTime = date2.getTime();
        return (thisTime < anotherTime) ? -1 : ((thisTime == anotherTime) ? 0 : 1);
    }
    
    public String getUserTypeString() {
        if (this.valid == null) {
            return null;
        }
        if (this.valid.getUserType().equals("T")) {
            return "Trial";
        }
        if (this.valid.getUserType().equals("R")) {
            final Properties userTypeProp = this.valid.getModuleProperties("LicenseDetails");
            if (userTypeProp != null && userTypeProp.getProperty("licenseusertype") != null) {
                return userTypeProp.getProperty("licenseusertype");
            }
            return "Registered";
        }
        else {
            if (this.valid.getUserType().equals("F")) {
                return "Free";
            }
            return this.valid.getUserType();
        }
    }
    
    public String getAMSExpiry() {
        if (this.valid != null && this.valid.getModuleProperties("AMS") != null) {
            return this.valid.getModuleProperties("AMS").getProperty("Expiry");
        }
        return null;
    }
    
    public String getCustomUserType() {
        if (this.valid != null && this.valid.getModuleProperties("LicenseDetails") != null && this.valid.getModuleProperties("LicenseDetails").getProperty("licenseusertype") != null) {
            return this.valid.getModuleProperties("LicenseDetails").getProperty("licenseusertype");
        }
        return null;
    }
    
    static {
        ThreadWorn.threadBare = null;
    }
}
