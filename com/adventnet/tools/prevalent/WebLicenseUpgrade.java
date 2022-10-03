package com.adventnet.tools.prevalent;

import java.util.Hashtable;
import java.util.Properties;
import java.util.ArrayList;
import java.io.File;

public class WebLicenseUpgrade
{
    private String licensePath;
    private String home;
    private String license_dir;
    boolean status;
    private User user;
    private DataClass data;
    private String reason;
    private String userName;
    
    public WebLicenseUpgrade(final String home, final String license_dir, final String licensePath) {
        this.licensePath = null;
        this.home = ".";
        this.license_dir = "classes";
        this.status = false;
        this.user = null;
        this.data = null;
        this.reason = null;
        this.userName = null;
        if (home != null) {
            this.home = home;
        }
        if (license_dir != null) {
            this.license_dir = license_dir;
        }
        if (licensePath != null) {
            this.licensePath = licensePath;
        }
    }
    
    public boolean isValidLicense() {
        final File f = new File(this.licensePath);
        if (f.exists()) {
            InputFileParser parse = null;
            try {
                parse = new InputFileParser(this.licensePath);
                this.data = parse.getDataClass();
                final ArrayList userList = this.data.getUserList();
                if (userList == null || userList.isEmpty()) {
                    this.reason = "PARSE ERROR";
                    return false;
                }
            }
            catch (final Exception e) {
                this.reason = "PARSE ERROR";
                return false;
            }
            final String[] users = LUtil.getUserArray(this.licensePath);
            if (users != null) {
                for (int i = 0; i < users.length; ++i) {
                    System.out.println("User Name : " + users[i]);
                }
            }
            final Validation valid = Validation.getInstance();
            try {
                final String user = users[0];
                LUtil.setISMP(true);
                LUtil.setLicenseDir(this.license_dir);
                this.status = valid.doValidation(this.home, user, this.licensePath, false, false);
            }
            catch (final Exception ex) {
                System.out.println("Message : Invalid License File ");
            }
            if (this.status) {
                valid.copyLicenseFile(this.home, this.licensePath);
            }
        }
        return this.status;
    }
    
    private String getReason() {
        return this.reason;
    }
    
    public Properties getErrorMessage() {
        String reason = "";
        if (this.getReason() != null) {
            reason = this.getReason();
        }
        String error_code = "";
        if (LUtil.getErrorCode() != null) {
            error_code = LUtil.getErrorCode();
        }
        String error_message = "";
        if (LUtil.getErrorMessage() != null) {
            error_message = LUtil.getErrorMessage();
        }
        String detailed_error_message = "";
        if (LUtil.getDetailedErrorMessage() != null) {
            detailed_error_message = LUtil.getDetailedErrorMessage();
        }
        final Properties p = new Properties();
        ((Hashtable<String, String>)p).put("REASON", reason);
        ((Hashtable<String, String>)p).put("ERROR_CODE", error_code);
        ((Hashtable<String, String>)p).put("ERROR_MESSAGE", error_message);
        ((Hashtable<String, String>)p).put("DETAILED_ERROR_MESSAGE", detailed_error_message);
        return p;
    }
    
    public Properties getOutputMessage() {
        final Wield wield = Wield.getInstance();
        wield.validateInvoke("License Manager", this.license_dir, false);
        final Properties p = new Properties();
        String user_type = "";
        String license_type = "";
        String license_category = "";
        String upgrade_message = "";
        if (wield.getUserType() != null) {
            user_type = wield.getUserType();
        }
        if (wield.getLicenseTypeString() != null) {
            license_type = wield.getLicenseTypeString();
        }
        if (wield.getProductCategoryString() != null) {
            license_category = wield.getProductCategoryString();
        }
        if (wield.getUserType().equals("R")) {
            upgrade_message = "Product Updated with Registered license.";
        }
        if (wield.getUserType().equals("F")) {
            upgrade_message = "Product Updated with Free license.";
        }
        ((Hashtable<String, String>)p).put("USER_TYPE", user_type);
        ((Hashtable<String, String>)p).put("LICENSE_TYPE", license_type);
        ((Hashtable<String, String>)p).put("LICENSE_CATEGORY", license_category);
        ((Hashtable<String, String>)p).put("MESSAGE", upgrade_message);
        return p;
    }
    
    public static void main(final String[] args) {
        if (args.length != 3) {
            System.out.println("USAGE:::java WebLicenseUpgrade <home_dir> <license_dir> <license_file_path>");
            System.exit(1);
        }
        final WebLicenseUpgrade lt = new WebLicenseUpgrade(args[0], args[1], args[2]);
        final boolean status = lt.isValidLicense();
        if (!status) {
            System.out.println(lt.getErrorMessage());
            System.exit(1);
        }
        System.out.println(lt.getOutputMessage());
        System.exit(0);
    }
}
