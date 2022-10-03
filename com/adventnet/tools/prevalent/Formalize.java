package com.adventnet.tools.prevalent;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

public final class Formalize
{
    private static Formalize formalize;
    private String invalidKeyMesg;
    private String contactMesg;
    private int lType;
    private int type;
    private String productName;
    private String productVersion;
    
    private Formalize() {
        this.invalidKeyMesg = ToolsUtils.getString("Invalid") + " " + ToolsUtils.getString("License key");
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.lType = 0;
        this.type = 0;
        this.productName = null;
        this.productVersion = null;
    }
    
    public static Formalize getInstance() {
        if (Formalize.formalize == null) {
            Formalize.formalize = new Formalize();
        }
        return Formalize.formalize;
    }
    
    public boolean doFormalize(final String user, final String company, final String email, final String userType, final String key, final boolean display) {
        final NVariegation validation = NVariegation.getInstance();
        ArrayList s = null;
        String product = null;
        String productVersion = null;
        final StringBuffer buffer = new StringBuffer();
        final StringTokenizer st = new StringTokenizer(key, "-");
        while (st.hasMoreTokens()) {
            buffer.append(st.nextToken());
        }
        final String trunKey = buffer.toString();
        try {
            final Indication indicate = Indication.getInstance();
            indicate.deSerialize();
            final int map = indicate.getProductName();
            indicate.productNameDeSerialize();
            final int prod = indicate.getProductNameInt();
            if (map != prod) {
                LUtil.showError("ERROR CODE : 212", this.invalidKeyMesg, this.contactMesg, "Error", 212);
                return false;
            }
            this.setProductName(Laterality.productName[map]);
            product = String.valueOf(map);
            productVersion = indicate.getProductVersion();
            this.setProductVersion(productVersion);
            s = validation.getTrial(product, productVersion, user, company, trunKey, userType);
        }
        catch (final Exception e) {
            if (display) {
                LUtil.showError("ERROR CODE : 213", this.invalidKeyMesg, this.contactMesg, "Error", 213);
            }
            else {
                System.out.println("ERROR CODE : 213\n" + this.invalidKeyMesg + this.contactMesg);
            }
            return false;
        }
        if (s == null || !s.contains(trunKey)) {
            if (trunKey.length() == 20 && userType.equals("R")) {
                try {
                    final NRearward nRear = new NRearward(user, trunKey, Integer.parseInt(product));
                    nRear.getRegistered();
                    final String[] props = nRear.getPropValues();
                    this.setOldType(this.lType = Integer.parseInt(props[3]));
                    if (nRear.isValidationOk()) {
                        return true;
                    }
                    if (display) {
                        LUtil.showError("ERROR CODE : 211", this.invalidKeyMesg, this.contactMesg, "Error", 211);
                    }
                    else {
                        System.out.println("ERROR CODE : 211\n" + this.invalidKeyMesg + this.contactMesg);
                    }
                    return false;
                }
                catch (final Exception ex) {
                    if (display) {
                        LUtil.showError("ERROR CODE : 210", this.invalidKeyMesg, this.contactMesg, "Error", 210);
                    }
                    else {
                        System.out.println("ERROR CODE : 210\n" + this.invalidKeyMesg + this.contactMesg);
                    }
                    return false;
                }
            }
            if (display) {
                LUtil.showError("ERROR CODE : 209", this.invalidKeyMesg, this.contactMesg, "Error", 209);
            }
            else {
                System.out.println("ERROR CODE : 209\n" + this.invalidKeyMesg + this.contactMesg);
            }
            return false;
        }
        this.lType = s.indexOf(trunKey) / 3;
        final ROperation operation = ROperation.getInstance();
        final String regisKey = operation.getRegValues(product, productVersion);
        final int index = regisKey.lastIndexOf("-");
        String registryKey;
        if (index != -1) {
            registryKey = regisKey.substring(0, index);
        }
        else {
            registryKey = regisKey;
        }
        final Clientele client = Clientele.getInstance();
        boolean registryboolean = false;
        if (!userType.equals("T")) {
            return client.getRegisterState(user, company, trunKey);
        }
        final Calendar cal = Calendar.getInstance();
        final Date currentDate = Clientele.getCurrentDate(cal);
        String registryDate = "";
        if (index != -1) {
            registryDate = regisKey.substring(index + 1);
        }
        Date regTmpDate = null;
        if (!registryDate.equals("")) {
            regTmpDate = client.getTheDate(registryDate, true);
        }
        int check = -1;
        if (regTmpDate != null) {
            check = client.compareTo(regTmpDate, currentDate);
        }
        if (registryKey.equals(trunKey) && check > 0) {
            if (display) {
                LUtil.showError("ERROR CODE : 207", this.invalidKeyMesg, this.contactMesg, "Error", 207);
            }
            else {
                System.out.println("ERROR CODE : 207\n" + this.invalidKeyMesg + this.contactMesg);
            }
            return false;
        }
        final String dd = validation.getTheValidKey();
        final boolean cust = client.first(dd, user, company, trunKey);
        if (cust) {
            final String lastAccessDate = client.getTheLastAccessedDate();
            final String lastDate = "-" + lastAccessDate.replace(' ', ':');
            registryboolean = operation.writeRegValue(product, productVersion, trunKey + lastDate);
            if (display) {
                final String ls = "The evaluation period of this product ends on " + client.getEvaluationExpiryDate().toString();
                LUtil.showError("", ls, this.contactMesg, "Information", -3);
            }
            else {
                System.out.println("\nThe evaluation period of this product ends on " + client.getEvaluationExpiryDate().toString() + "\n");
                System.out.println("Press Enter to continue...");
                try {
                    System.in.read();
                }
                catch (final Exception ex2) {}
            }
            return true;
        }
        System.exit(0);
        return false;
    }
    
    private void setProductName(final String name) {
        this.productName = name;
    }
    
    private void setProductVersion(final String version) {
        this.productVersion = version;
    }
    
    public String getProductName() {
        return this.productName;
    }
    
    public String getProductVersion() {
        return this.productVersion;
    }
    
    public int getOldLType() {
        return this.lType;
    }
    
    public void setOldType(final int ltype) {
        this.lType = ltype;
    }
    
    public int getType(final String key) {
        this.setType(key);
        return this.type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final String key) {
        try {
            final Modulation k = Modulation.getInstance();
            final WebGet wget = WebGet.getInstance();
            final String str = wget.getValues(key, "WEB");
            final StringTokenizer st = new StringTokenizer(str, ",");
            while (st.hasMoreTokens()) {
                final String d = st.nextToken();
                final String a = st.nextToken();
                if (a.length() == 3) {
                    this.type = Integer.parseInt(a.substring(1, 2));
                }
            }
        }
        catch (final Exception ex) {}
    }
    
    static {
        Formalize.formalize = null;
    }
}
