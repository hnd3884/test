package com.me.mdm.server.windows.profile.payload.content.security;

import java.util.Iterator;
import java.util.UUID;
import org.json.JSONObject;
import java.util.List;

public class AppLock
{
    String baseXML;
    String innerXML;
    String legacyInnerXML;
    public static final int MODERN_APP_TYPE = 1;
    public static final int LEGACY_APP_TYPE = 2;
    
    public AppLock() {
        this.baseXML = "<RuleCollection Type=\"%type%\" EnforcementMode=\"%mode%\">%blacklistrule%</RuleCollection>";
        this.innerXML = "<FilePublisherRule Id=\"%uuid%\" Name=\"Blacklist App{0}\" Description=\"Allow Admins\"  UserOrGroupSid=\"S-1-1-0\" Action=\"%allow%\"><Conditions><FilePublisherCondition PublisherName=\"%publishername%\" ProductName=\"%productname%\" BinaryName=\"%binaryname%\" /></Conditions></FilePublisherRule>";
        this.legacyInnerXML = "<FilePathRule Id=\"%uuid%\" Name=\"Allow %productname%\" Description=\"Allows members of the Everyone group to run applications that are located in the Windows folder.\" UserOrGroupSid=\"S-1-1-0\" Action=\"%allow%\"><Conditions><FilePathCondition Path=\"%productname%\" /></Conditions></FilePathRule>";
    }
    
    public String createAppLockXML(final List appList, final int type, final Boolean enabled) throws Exception {
        String finalXML = "";
        finalXML = finalXML.replaceAll("%mode%", enabled ? "enabled" : "disabled");
        if (type == 1) {
            finalXML = this.baseXML.replaceAll("%type%", "appx");
            final Iterator iterator = appList.iterator();
            final StringBuilder replacementXML = new StringBuilder();
            int i = 1;
            while (iterator.hasNext()) {
                final JSONObject jsonObject = iterator.next();
                replacementXML.append(this.innerXML.replaceAll("%publishername%", jsonObject.optString("publisherName", "*")).replaceAll("%binaryname%", jsonObject.optString("binaryName", "*")).replaceAll("%productname%", String.valueOf(jsonObject.get("productName"))).replaceAll("%app%", "" + i).replaceAll("%uuid%", UUID.randomUUID().toString()).replaceAll("%allow%", "Allow"));
                ++i;
            }
            finalXML = finalXML.replaceAll("%blacklistrule%", replacementXML.toString()).replaceAll("%uuid%", UUID.randomUUID().toString());
        }
        if (type == 2) {
            finalXML = this.baseXML.replaceAll("%type%", "exe");
            final Iterator iterator = appList.iterator();
            final StringBuilder replacementXML = new StringBuilder();
            int i = 1;
            while (iterator.hasNext()) {
                final JSONObject jsonObject = iterator.next();
                replacementXML.append(this.legacyInnerXML.replaceAll("%productname%", String.valueOf(jsonObject.get("productName"))).replaceAll("%uuid%", UUID.randomUUID().toString()).replaceAll("%allow%", "Allow"));
                ++i;
            }
            finalXML = finalXML.replaceAll("%blacklistrule%", replacementXML.toString()).replaceAll("%uuid%", UUID.randomUUID().toString());
        }
        return finalXML;
    }
}
