package com.adventnet.tools.prevalent;

import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.io.File;

public final class CMDClass
{
    private boolean accepted;
    private boolean free;
    private String userName;
    private String licenseFile;
    private String home;
    private String licFile;
    private String licFilePath;
    private String lFilePath;
    private File freeFile;
    private String userType;
    private String companyName;
    private String key;
    private Validation valid;
    private String invalidFile;
    private String contactMesg;
    private boolean evalStandard;
    private String defaultEvalFilePath;
    String[] users;
    Vector userVec;
    
    public CMDClass() {
        this.accepted = false;
        this.free = false;
        this.lFilePath = "";
        this.freeFile = new File(LUtil.getDir() + LUtil.getLicenseDir() + File.separator + "Free.xml");
        this.userType = null;
        this.valid = null;
        this.invalidFile = "Invalid License File";
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.users = null;
        this.userVec = new Vector();
    }
    
    public Vector getUserList(final String fileUrl) {
        this.users = LUtil.getUserArray(fileUrl);
        if (this.users != null) {
            for (int i = 0; i < this.users.length; ++i) {
                this.userVec.add(this.users[i]);
            }
        }
        return this.userVec;
    }
    
    public CMDClass(final String dir, final Validation val) {
        this.accepted = false;
        this.free = false;
        this.lFilePath = "";
        this.freeFile = new File(LUtil.getDir() + LUtil.getLicenseDir() + File.separator + "Free.xml");
        this.userType = null;
        this.valid = null;
        this.invalidFile = "Invalid License File";
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.users = null;
        this.userVec = new Vector();
        this.valid = val;
        this.lFilePath = dir;
    }
    
    public CMDClass(final String dir, final Validation val, final String filePath, final boolean policy) {
        this.accepted = false;
        this.free = false;
        this.lFilePath = "";
        this.freeFile = new File(LUtil.getDir() + LUtil.getLicenseDir() + File.separator + "Free.xml");
        this.userType = null;
        this.valid = null;
        this.invalidFile = "Invalid License File";
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.evalStandard = false;
        this.defaultEvalFilePath = null;
        this.users = null;
        this.userVec = new Vector();
        this.valid = val;
        this.lFilePath = dir;
        this.evalStandard = policy;
        this.defaultEvalFilePath = filePath;
    }
    
    public void invokeInCMD() {
        try {
            if (System.getProperty("WEB_LICENSE_CHECK") == null) {
                final Indication indicate = Indication.getInstance();
                if (!indicate.isAgreementHide()) {
                    this.aggrement();
                }
                this.home = LUtil.getDir();
                this.licFile = LUtil.getLicenseDir();
                this.licFilePath = this.licFile + File.separator + "StandardEvaluation.xml";
                final File f = new File(this.licFilePath);
                this.valid = Validation.getInstance();
                final String uType = this.valid.getUserType();
                if (!indicate.isAgreementHide()) {
                    if (f.exists() && uType != null && uType.equals("T")) {
                        ConsoleOut.println("Do you accept the LICENSE AGREEMENT   (y/n)");
                        ConsoleOut.println("(OR) Want to continue in FREE mode   (free)");
                    }
                    else {
                        ConsoleOut.println("Do you accept the LICENSE AGREEMENT   (y/n)");
                    }
                }
                this.userChoice();
                if (this.hasAccepted()) {
                    this.registration();
                    this.getRegistered(this.userName, this.licenseFile);
                }
                if (this.isFree()) {
                    final boolean success = this.valid.doValidation(this.home, "Evaluation User", this.licFilePath, false, false);
                    if (success) {
                        this.valid.copyLicenseFile(this.home, this.licFilePath);
                    }
                }
            }
        }
        catch (final Exception exp) {
            LUtil.showCMDError("ERROR CODE : 485", this.invalidFile, this.contactMesg, 485);
        }
    }
    
    public void nonXWindowRegister() {
        try {
            this.registration();
            if (this.userType == null) {
                if (this.valid == null) {
                    this.valid = Validation.getInstance();
                }
                final boolean bool = this.valid.isRegisteredFile(this.licenseFile, this.userName, false);
                if (!bool) {
                    return;
                }
            }
            this.getRegistered(this.userName, this.licenseFile);
        }
        catch (final Exception exp) {
            LUtil.showCMDError("ERROR CODE : 486", this.invalidFile, this.contactMesg, 486);
        }
    }
    
    private void aggrement() {
        final String filePath = this.lFilePath + File.separator + "LICENSE_AGREEMENT";
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                final Indication util = Indication.getInstance();
                f = util.getTheFile("LICENSE_AGREEMENT");
            }
            catch (final Exception e) {
                ConsoleOut.println("Error while reading license agreement");
            }
        }
        try {
            final RandomAccessFile license = new RandomAccessFile(f, "r");
            String s = "";
            int i = 0;
            while (s != null) {
                ConsoleOut.println(s);
                if (++i > 21) {
                    i = 0;
                    ConsoleOut.println("\t\t\t\t\tPress Enter to continue...");
                    System.in.read();
                    System.in.skip(System.in.available());
                }
                s = license.readLine();
            }
            license.close();
        }
        catch (final Exception e) {
            System.err.println(e.toString());
        }
    }
    
    String getTheString(final Date dd) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        final String year = new Integer(cal.get(1)).toString();
        final String month = new Integer(cal.get(2)).toString();
        final String day = new Integer(cal.get(5)).toString();
        return year + " " + month + " " + day;
    }
    
    private void userChoice() {
        try {
            final Indication indicate = Indication.getInstance();
            String input = null;
            if (indicate.isAgreementHide()) {
                input = "y";
            }
            else {
                input = this.getInput();
            }
            if (input.equalsIgnoreCase("y")) {
                this.accepted = true;
            }
            else if (input.equalsIgnoreCase("n")) {
                System.exit(0);
            }
            else if (input.equalsIgnoreCase("free")) {
                this.free = true;
            }
            else {
                ConsoleOut.print("\nWrong input. Please re-enter");
                ConsoleOut.println("\t Press (y) to accept or (n) to cancel");
                this.userChoice();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getInput() {
        String inpStr = null;
        try {
            final BufferedReader bufred = new BufferedReader(new InputStreamReader(System.in));
            inpStr = bufred.readLine().trim();
        }
        catch (final Exception e) {
            System.err.println(e.toString());
        }
        return inpStr;
    }
    
    private boolean hasAccepted() {
        return this.accepted;
    }
    
    private boolean isFree() {
        return this.free;
    }
    
    private void registration() {
        final String standardBackward = "\n\nPress t to start the product in Evaluation mode\n      l to provide the User Name and License File path\n      r if you are a registered user of the product with a license key,select this option and proceed for entering old license key \n      e to  Exit\nChoose an Option :: ";
        final String standard = "\n\nPress t to start the product in Evaluation mode\n      l to provide the User Name and License File path\n      e to  Exit\nChoose an Option :: ";
        final String generalBackward = "\n\nPress l to provide the User Name and License File path\n      r if you are a registered user of the product with a license key,select this option and proceed for entering old license key \n      e to  Exit\nChoose an Option :: ";
        final String general = "\n\nPress 1 to provide the User Name and License File path\n      2 to  Exit\nChoose an Option :: ";
        final String free = "\n\nPress f to start the product in Free mode\n      l to provide the User Name and License File path\n      e to  Exit\nChoose an Option :: ";
        final String freeAndStandard = "\n\nPress f to start the product in Free mode\n      t to start the product in Evaluation mode\n      l to provide the User Name and License File path\n      e to  Exit\nChoose an Option :: ";
        final boolean backward = LUtil.getBackwardSupport();
        if (this.evalStandard) {
            if (backward) {
                this.display(standardBackward);
            }
            else if (this.freeFile.exists()) {
                this.display(freeAndStandard);
            }
            else {
                this.display(standard);
            }
        }
        else if (backward) {
            this.display(generalBackward);
        }
        else if (this.freeFile.exists()) {
            this.display(free);
        }
        else {
            this.display(general);
        }
        final String input = this.getInput();
        if (input.equalsIgnoreCase("t") && this.evalStandard) {
            this.userName = "Evaluation User";
            this.licenseFile = this.defaultEvalFilePath;
        }
        else if (input.equalsIgnoreCase("f") && this.freeFile.exists()) {
            this.userName = "Evaluation User";
            this.licenseFile = this.freeFile.toString();
        }
        else if (input.equalsIgnoreCase("l") || input.equals("1")) {
            if (LUtil.isTrialMacBased()) {
                this.displayIDDetails();
                ConsoleOut.println("\n\nDo you want to continue (y/n)");
                this.userChoice();
                if (!this.hasAccepted()) {
                    return;
                }
            }
            ConsoleOut.print("\nEnter User Name\t : ");
            this.userName = this.getInput();
            ConsoleOut.print("\nEnter The License File path : ");
            this.licenseFile = this.getInput();
        }
        else if (input.equalsIgnoreCase("r") && backward) {
            ConsoleOut.print("\nEnter User Name\t : ");
            this.userName = this.getInput();
            ConsoleOut.print("\nEnter Company Name\t : ");
            this.companyName = this.getInput();
            ConsoleOut.print("\nEnter the Registered user Key : ");
            this.key = this.getInput();
            final StringBuffer buffer = new StringBuffer();
            final StringTokenizer st = new StringTokenizer(this.key, "-");
            while (st.hasMoreTokens()) {
                buffer.append(st.nextToken());
            }
            this.key = buffer.toString();
            this.userType = "R";
        }
        else if (input.equalsIgnoreCase("e") || input.equals("2")) {
            System.exit(0);
        }
        else {
            ConsoleOut.println("\nWrong input. Please re-try ...");
            this.registration();
        }
    }
    
    private void display(final String str) {
        try {
            ConsoleOut.print("\n\t******* REGISTRATION ******* \nHOST NAME IS " + Indication.getInstance().getLocalHostName() + str);
        }
        catch (final Exception ex) {}
    }
    
    private void getRegistered(final String userName, final String licenseFile) {
        if (this.valid == null) {
            this.valid = Validation.getInstance();
        }
        if (this.userType == null) {
            final boolean success = this.valid.doValidation(this.lFilePath, userName, licenseFile, false);
            if (success) {
                this.valid.copyLicenseFile(this.lFilePath, licenseFile);
                if (this.evalStandard) {
                    new File(licenseFile).delete();
                }
            }
        }
        else {
            this.valid.doOldValidation(userName, this.companyName, null, this.userType, this.key, false);
        }
    }
    
    public Validation getValidation() {
        return this.valid;
    }
    
    private void displayIDDetails() {
        final File filePath = LUtil.getTheDetailsFile(this.getClass());
        final String ID = LUtil.getUniqueID();
        ConsoleOut.println("\n\n" + ID + "\n\n");
        try {
            final RandomAccessFile license = new RandomAccessFile(filePath.toString(), "r");
            String s = "";
            int i = 0;
            while (s != null) {
                ConsoleOut.println(s);
                if (++i > 21) {
                    i = 0;
                    ConsoleOut.println("\t\t\t\t\tPress Enter to continue...");
                    System.in.read();
                    System.in.skip(System.in.available());
                }
                s = license.readLine();
            }
            license.close();
        }
        catch (final Exception e) {
            ConsoleOut.println("Unable to read Unique ID details in command line");
        }
    }
}
