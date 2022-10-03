package com.adventnet.tools.prevalent;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Date;

public final class NThreadBare
{
    private boolean accepted;
    private String title;
    private boolean GUI;
    private String lFilePath;
    private boolean rtuser;
    private String userName;
    private String companyName;
    private String key;
    private String trialName;
    private String email;
    private int type;
    private String contactMesg;
    String invalidKeyMesg;
    
    public NThreadBare() {
        this.accepted = false;
        this.title = null;
        this.GUI = true;
        this.lFilePath = "";
        this.rtuser = false;
        this.contactMesg = "\n\n" + ToolsUtils.getString("Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com") + "\n\n";
        this.invalidKeyMesg = ToolsUtils.getString("Invalid License");
    }
    
    public NThreadBare(final String title) {
        this(title, null, true);
    }
    
    public NThreadBare(final String title, final boolean displayFlag) {
        this(title, null, displayFlag);
    }
    
    public NThreadBare(final String title, final String dir) {
        this(title, dir, true);
    }
    
    public NThreadBare(final String title, final String dir, final boolean displayFlag) {
        this.accepted = false;
        this.title = null;
        this.GUI = true;
        this.lFilePath = "";
        this.rtuser = false;
        this.contactMesg = "\n\n" + ToolsUtils.getString("Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com") + "\n\n";
        this.invalidKeyMesg = ToolsUtils.getString("Invalid License");
        this.GUI = displayFlag;
        if (dir != null) {
            this.lFilePath = dir;
        }
        this.title = title;
    }
    
    public void initalize(final String title, final String dir, final boolean displayFlag) {
        this.GUI = displayFlag;
        if (dir != null) {
            this.lFilePath = dir;
        }
        this.title = title;
        Clientele.getInstance();
        final String sr = Clientele.getTheRegisterCheck();
        if (sr.equals("exception")) {
            if (this.GUI) {
                LUtil.showError("ERROR CODE : 206", this.invalidKeyMesg, this.contactMesg, "Error", 206);
            }
            else {
                System.out.println("ERROR CODE : 206\n" + this.invalidKeyMesg + this.contactMesg);
            }
        }
        else {
            final NVariegation v = NVariegation.getInstance();
            if (sr.equals("R")) {
                if (v.readFile()) {
                    if (Clientele.getInstance().checkExpiry()) {
                        this.rtuser = true;
                    }
                    else {
                        LUtil.showError("", "License period has Expired", this.contactMesg, "Information", -4);
                        this.rtuser = false;
                    }
                }
            }
            else {
                Clientele.getInstance();
                if (Clientele.getIsFirstTimeUser() && v.readFile()) {
                    final Clientele client = Clientele.getInstance();
                    final boolean cust = client.getClienteleState();
                    if (cust) {
                        final boolean reg = Boolean.valueOf(System.getProperty("Register"));
                        if (reg) {
                            this.nonXWindowRegister();
                        }
                        else {
                            final long l = this.getDays(client.getEvaluationExpiryDate());
                            if (l != 0L) {
                                System.out.println(ToolsUtils.getString("This evaluation copy will be valid for") + " " + l + " " + ToolsUtils.getString("day(s)"));
                            }
                            else {
                                System.out.println(ToolsUtils.getString("Today is the last day for evaluation"));
                            }
                            this.rtuser = true;
                        }
                    }
                    if (client.getRegister()) {}
                }
            }
        }
    }
    
    public boolean isBare() {
        return this.rtuser;
    }
    
    private void nonXWindowRegister() {
        Clientele.getInstance();
        if (!Clientele.getTheRegisterCheck().equals("R")) {
            this.GUI = false;
            this.commandLineRegistration();
            if (this.getUserType().equals("T")) {
                return;
            }
            this.getRegistered(this.getUserType());
        }
    }
    
    public void getRegistered(final String userType) {
        final Formalize form = Formalize.getInstance();
        if (userType.equals("T")) {
            this.rtuser = form.doFormalize(this.email.substring(0, this.email.indexOf("@")), this.email.substring(this.email.indexOf("@") + 1), this.email, userType, this.key, this.GUI);
        }
        else if (userType.equals("R")) {
            this.rtuser = form.doFormalize(this.userName, this.companyName, this.email, userType, this.key, this.GUI);
        }
        if (this.key.length() == 24) {
            this.setType(form.getType(this.key));
        }
        else if (this.key.length() == 20) {
            this.setType(form.getOldLType());
        }
    }
    
    public long getDays(final Date d) {
        try {
            final long expirytimeinmillis = d.getTime();
            final Date currentdate = new Date();
            final long currenttimeinmillis = currentdate.getTime();
            final long difference = expirytimeinmillis - currenttimeinmillis;
            final long daysLeft = difference / 86400000L;
            return daysLeft;
        }
        catch (final Exception exp) {
            return -1L;
        }
    }
    
    public long getEvaluationDays() {
        final Clientele client = Clientele.getInstance();
        return this.getDays(client.getEvaluationExpiryDate());
    }
    
    public void registration() {
        try {
            System.out.print("\n\t******* REGISTRATION ******* \nHOST NAME IS " + Indication.getInstance().getLocalHostName() + "\n\nPress r to  Register\n      t for Trial Copy\n      e to  Exit\nChoose an Option :: ");
            final String input = this.getInput();
            if (input.equalsIgnoreCase("r")) {
                System.out.print("\nPlease Enter User Name\t : ");
                this.userName = this.getInput();
                System.out.print("\nPlease Enter Company Name\t : ");
                this.companyName = this.getInput();
                System.out.print("\nPlease Enter the Registered user Key : ");
                this.key = this.getInput();
                this.trialName = "R";
            }
            else if (input.equalsIgnoreCase("t")) {
                System.out.print("\nPlease Enter the Email ID\t : ");
                this.email = this.getInput();
                System.out.print("\nPlease Enter the Trial user Key : ");
                this.key = this.getInput();
                this.trialName = "T";
            }
            else if (input.equalsIgnoreCase("e")) {
                System.exit(0);
            }
            else {
                System.out.println("\nWrong input. Please re-try ...");
                this.registration();
            }
            if (this.trialName.equals("R") && (this.userName == null || this.userName.trim().equals(""))) {
                System.out.println(ToolsUtils.getString("Invalid user name"));
                this.registration();
            }
            else if (this.trialName.equals("R") && (this.companyName == null || this.companyName.trim().equals(""))) {
                System.out.println(ToolsUtils.getString("Invalid company name"));
                this.registration();
            }
            else if (this.key == null || this.key.trim().equals("") || this.key.trim().length() < 20 || this.key.trim().length() > 24) {
                System.out.println(ToolsUtils.getString("Invalid License key"));
                this.registration();
            }
        }
        catch (final Exception ex) {}
    }
    
    public void commandLineRegistration() {
        try {
            System.out.print("\n\t******* REGISTRATION ******* \nHOST NAME IS " + Indication.getInstance().getLocalHostName() + "\n\nPress r to  Register\n      e to  Exit\nChoose an Option :: ");
            final String input = this.getInput();
            if (input.equalsIgnoreCase("r")) {
                System.out.print("\nPlease Enter User Name\t : ");
                this.userName = this.getInput();
                System.out.print("\nPlease Enter Company Name\t : ");
                this.companyName = this.getInput();
                System.out.print("\nPlease Enter the Registered user Key : ");
                this.key = this.getInput();
                this.trialName = "R";
            }
            else if (input.equalsIgnoreCase("e")) {
                System.exit(0);
            }
            else {
                System.out.println("\nWrong input. Please re-try ...");
                this.commandLineRegistration();
            }
        }
        catch (final Exception ex) {}
    }
    
    private String getInput() {
        final byte[] inpByte = new byte[100];
        String inpStr = "";
        try {
            System.in.read(inpByte);
            inpStr = new String(inpByte).trim();
        }
        catch (final Exception ex) {}
        return inpStr;
    }
    
    private String getUserType() {
        return this.trialName;
    }
    
    public void aggrement() {
        final String filePath = this.lFilePath + "LICENSE_AGREEMENT";
        try {
            final RandomAccessFile license = new RandomAccessFile(filePath, "r");
            String s = "";
            int i = 0;
            while (s != null) {
                System.out.println(s);
                if (++i > 21) {
                    i = 0;
                    System.out.println("\t\t\t\t\tPress Enter to continue...");
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
    
    private void getUniqueID() {
    }
    
    private void idDetails(final String path, final String ID) {
        final String filePath = path + "LICENSE_DETAILS";
        try {
            final RandomAccessFile license = new RandomAccessFile(filePath, "r");
            String s = "";
            int i = 0;
            System.out.println("\n\n" + ID + "\n\n");
            while (s != null) {
                System.out.println(s);
                if (++i > 21) {
                    i = 0;
                    System.out.println("\t\t\t\t\tPress Enter to continue...");
                    System.in.read();
                    System.in.skip(System.in.available());
                }
                s = license.readLine();
            }
            license.close();
        }
        catch (final Exception e) {
            System.out.println("Unable to read Unique ID details in command line");
            e.printStackTrace();
        }
    }
    
    private String execute(final String s) {
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
    
    public void userChoice() {
        final String input = this.getInput();
        if (input.equalsIgnoreCase("y")) {
            this.accepted = true;
        }
        else if (input.equalsIgnoreCase("n")) {
            System.exit(0);
        }
        else {
            System.out.print("\nWrong input. Please re-enter");
            System.out.println("\t Press (y) to accept or (n) to cancel");
            this.userChoice();
        }
    }
    
    public boolean hasAccepted() {
        return this.accepted;
    }
    
    public void setUserName(final String uname) {
        this.userName = uname;
    }
    
    public void setCompanyName(final String company) {
        this.companyName = company;
    }
    
    public void setEmailID(final String emailID) {
        this.email = emailID;
    }
    
    public void setKey(final String lkey) {
        this.key = lkey;
    }
    
    private void setType(final int i) {
        this.type = i;
    }
    
    public int getType() {
        if (this.type == 0) {
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key = util.getTheKey();
                if (key != null && key.length() == 24) {
                    return this.type = form.getType(key);
                }
                if (key != null && key.length() == 20) {
                    return this.type = form.getOldLType();
                }
            }
            catch (final Exception ex) {}
        }
        return this.type;
    }
}
