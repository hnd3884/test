package com.adventnet.tools.prevalent;

import java.util.StringTokenizer;
import java.util.ArrayList;

public final class NVariegation
{
    private static NVariegation varie;
    private String date;
    private String[] trialArray;
    private String invalidKeyMesg;
    private String contactMesg;
    
    private NVariegation() throws Exception {
        this.date = null;
        this.trialArray = new String[] { "a[dqQRCb", "afc@[rR[", "a[dqQRGq", "caa6VZN4", "a[dqQQNd", "a[dqQJSw", "aacQJRDc", "afc@[q]r", "aaa7@AQZ", "aadEXLmk" };
        this.invalidKeyMesg = ToolsUtils.getString("Invalid") + " " + ToolsUtils.getString("License key");
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
    }
    
    public static NVariegation getInstance() {
        if (NVariegation.varie == null) {
            try {
                NVariegation.varie = new NVariegation();
            }
            catch (final Exception ex) {
                ex.getMessage();
            }
        }
        return NVariegation.varie;
    }
    
    public String getTheMacID() throws Exception {
        final Indication indi = Indication.getInstance();
        final Intonation intonate = Intonation.getInstance();
        final String macAdd = intonate.getTheMAC(indi.getTheKey());
        return macAdd;
    }
    
    public ArrayList getTrial(final String product, final String version, final String user, final String company, final String key, final String userType) throws Exception {
        final Modulation k = Modulation.getInstance();
        final ArrayList list = new ArrayList();
        final WebGet wget = WebGet.getInstance();
        final String str = wget.getValues(key, "WEB");
        final StringTokenizer st = new StringTokenizer(str, ",");
        String type = null;
        String d = null;
        String a = null;
        while (st.hasMoreTokens()) {
            d = st.nextToken();
            a = st.nextToken();
        }
        if (a != null && a.length() == 3) {
            type = a.substring(1, 2);
        }
        if (userType.equals("T")) {
            if (d.length() == 3) {
                final StringBuffer strBuff = new StringBuffer();
                strBuff.append(new Integer(k.getInt(d.charAt(0))).toString());
                strBuff.append(" ");
                strBuff.append(new Integer(k.getInt(d.charAt(1))).toString());
                strBuff.append(" ");
                strBuff.append(new Integer(k.getInt(d.charAt(2))).toString());
                this.date = strBuff.toString();
            }
            list.add(k.getKey(user, company, product, type, version, this.date, userType, "@@"));
        }
        else if (userType.equals("R") && key.length() == 24) {
            final String macAdd = this.getTheMacID();
            if (!macAdd.equals("NULL") || !macAdd.equals("null")) {
                final MacComp comp = MacComp.getInstance();
                final String mac = MacComp.processString(comp.getTheStringForProcess(comp.getTheFinalValue(macAdd)));
                list.add(k.getKey(user, company, product, type, version, mac));
            }
            final String[] macArray = { "db", "14", "e9", "41", "aa", "6b", "6c", "3c", "cd", "e3" };
            for (int i = 0; i < macArray.length; ++i) {
                list.add(k.getKey(user, company, product, type, version, macArray[i]));
            }
        }
        return list;
    }
    
    public String getTheValidKey() {
        return this.date;
    }
    
    public boolean readFile() {
        Indication util = null;
        try {
            util = Indication.getInstance();
            util.deSerialize();
            util.productNameDeSerialize();
        }
        catch (final Exception e) {
            LUtil.showError("ERROR CODE : 210", this.invalidKeyMesg, this.contactMesg, "Error", 210);
            return false;
        }
        final int map = util.getProductName();
        final String product = new Integer(map).toString();
        final int prod = util.getProductNameInt();
        if (map != prod) {
            LUtil.showError("ERROR CODE : 210", this.invalidKeyMesg, this.contactMesg, "Error", 210);
            return false;
        }
        final String version = util.getProductVersion();
        final String userName = util.getTheUserName();
        final String companyName = util.getTheCompanyName();
        final String key = util.getTheKey();
        final String hostName = util.getTheHostName();
        final String lastAccessedString = util.getLastAccessedDate();
        final String regCheck = util.getTheRegCheck();
        try {
            try {
                final ArrayList s = this.getTrial(product, version, userName, companyName, key, regCheck);
                if (s.contains(key)) {
                    final ROperation operation = ROperation.getInstance();
                    final String regisKey = operation.getRegValues(product, version);
                    final int index = regisKey.lastIndexOf("-");
                    if (index != -1) {
                        final String registryKey = regisKey.substring(0, index);
                    }
                    else {
                        final String registryKey = regisKey;
                    }
                    String dateKey = "";
                    if (regCheck.equals("T")) {
                        dateKey = "-" + lastAccessedString.replace(' ', ':');
                        boolean registryboolean = false;
                        registryboolean = operation.writeRegValue(product, version, key + dateKey);
                    }
                    if (key.length() == 24) {
                        Formalize.getInstance().setType(key);
                    }
                    return true;
                }
                if (key.length() == 20) {
                    try {
                        final NRearward nRear = new NRearward(userName, key, map);
                        nRear.getRegistered();
                        final String[] props = nRear.getPropValues();
                        final int lType = Integer.parseInt(props[3]);
                        Formalize.getInstance().setOldType(lType);
                        return nRear.isValidationOk();
                    }
                    catch (final Exception ex) {
                        LUtil.showError("ERROR CODE : 210", this.invalidKeyMesg, this.contactMesg, "Error", 210);
                        return false;
                    }
                }
                return false;
            }
            catch (final Exception e2) {}
        }
        catch (final Exception ex2) {}
        return false;
    }
    
    public String getGeneratedValue(final int index) {
        return this.trialArray[index];
    }
    
    static {
        NVariegation.varie = null;
    }
}
