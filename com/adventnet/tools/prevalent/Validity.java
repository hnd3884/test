package com.adventnet.tools.prevalent;

import java.util.ArrayList;

public class Validity
{
    private User user;
    private String fileUrl;
    private DataClass data;
    private String reason;
    private String userName;
    
    Validity(final String fileName) {
        this.user = null;
        this.fileUrl = null;
        this.data = null;
        this.reason = null;
        this.userName = null;
        this.fileUrl = fileName;
    }
    
    protected boolean process() {
        if (this.fileUrl == null) {
            this.reason = "Input License file not Found.";
            return false;
        }
        final boolean isOk = false;
        InputFileParser parse = null;
        try {
            parse = new InputFileParser(this.fileUrl);
            this.data = parse.getDataClass();
            final ArrayList userList = this.data.getUserList();
            if (userList == null || userList.isEmpty()) {
                this.reason = "The Trial Input file does not contain user";
                return false;
            }
            this.userName = userList.get(0);
            this.user = this.data.getUserObject(this.userName);
            if (this.user == null) {
                this.reason = "The Trial Input file does not contain user";
                return false;
            }
            final ArrayList idList = this.user.getIDs();
            if (idList.isEmpty()) {
                this.reason = "The Trial Input file does not contains the map ID";
                return false;
            }
            final Details details = this.data.getDetails(idList.get(0));
            if (details == null) {
                this.reason = "The Trial Input file does not contains the details object";
                return false;
            }
        }
        catch (final Exception e) {
            this.reason = "PARSE_ERROR";
            return false;
        }
        return this.isValidFile();
    }
    
    protected String getReason() {
        return this.reason;
    }
    
    private boolean isValidFile() {
        boolean ok = false;
        final String company = this.user.getCompanyName();
        final String mailID = this.user.getMailId();
        final String macID = this.user.getMacId();
        final String expiryDate = this.user.getExpiryDate();
        final String numDays = this.user.getNumberOfDays();
        final String evalDays = this.user.getNumberOfDays();
        final String licenseType = this.user.getLicenseType();
        final String noOfRTLicense = this.user.getNoOfRTLicense();
        final String emailRestrict = this.user.getEmailRestrict();
        final String genDate = this.user.getGeneratedDate();
        final String maxEvalPeriod = this.user.getMaxTrialPeriod();
        final String macPolicy = this.user.getTrialMACPolicy();
        final String key = this.user.getKey();
        final String returnKey = Encode.getKey(this.userName, company, mailID, macID, expiryDate, numDays, licenseType, null, null, null, null, noOfRTLicense, emailRestrict, genDate, maxEvalPeriod, macPolicy);
        if (!key.equals(returnKey)) {
            this.reason = "Invalid License File. File entries might have been changed.";
            return false;
        }
        ok = true;
        final String wholeKey = this.data.getLicenseFileKey();
        final String encodedKey = Encode.getFinalKey(this.data.getWholeKeyBuffer());
        if (encodedKey.equals(wholeKey)) {
            ok = true;
            return ok;
        }
        this.reason = "Invalid License File. File entries might have been changed.";
        return false;
    }
    
    public static void main(final String[] args) {
        if (args.length != 1) {
            System.out.println("The Usage: Validity <licensefilename>");
            System.exit(0);
        }
        final Validity val = new Validity(args[0]);
        final boolean bool = val.process();
        System.out.println("Is the file valid:" + bool);
        System.out.println("Is Error:" + val.getReason());
    }
}
