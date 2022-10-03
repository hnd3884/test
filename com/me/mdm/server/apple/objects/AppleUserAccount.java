package com.me.mdm.server.apple.objects;

import org.json.JSONObject;
import com.dd.plist.NSDictionary;

public class AppleUserAccount implements Comparable<AppleUserAccount>
{
    private String dataQuota;
    private String dataUsed;
    private String fullName;
    private boolean dataSynced;
    private boolean isLoggedIn;
    private boolean mobileAccount;
    private String uID;
    private String userGUID;
    private String userName;
    private boolean hasSecureToken;
    
    private AppleUserAccount() {
    }
    
    public AppleUserAccount(final NSDictionary userAccountDict) {
        this.dataQuota = String.valueOf(userAccountDict.get((Object)"DataQuota"));
        this.dataUsed = String.valueOf(userAccountDict.get((Object)"DataUsed"));
        this.fullName = String.valueOf(userAccountDict.get((Object)"FullName"));
        this.dataSynced = !Boolean.parseBoolean(String.valueOf(userAccountDict.get((Object)"HasDataToSync")));
        this.isLoggedIn = Boolean.parseBoolean(String.valueOf(userAccountDict.get((Object)"IsLoggedIn")));
        this.mobileAccount = Boolean.parseBoolean(String.valueOf(userAccountDict.get((Object)"MobileAccount")));
        this.uID = String.valueOf(userAccountDict.get((Object)"UID"));
        this.userGUID = String.valueOf(userAccountDict.get((Object)"UserGUID"));
        this.userName = String.valueOf(userAccountDict.get((Object)"UserName"));
        this.hasSecureToken = Boolean.parseBoolean(String.valueOf(userAccountDict.get((Object)"HasSecureToken")));
    }
    
    @Override
    public String toString() {
        final JSONObject object = new JSONObject();
        object.put("DataQuota", (Object)this.dataQuota);
        object.put("DataUsed", (Object)this.dataUsed);
        object.put("FullName", (Object)this.fullName);
        object.put("HasDataToSync", this.dataSynced);
        object.put("IsLoggedIn", this.isLoggedIn);
        object.put("MobileAccount", this.mobileAccount);
        object.put("UID", (Object)this.uID);
        object.put("UserGUID", (Object)this.userGUID);
        object.put("UserName", (Object)this.userName);
        return object.toString();
    }
    
    public void setDataQuota(final String dataQuota) {
        this.dataQuota = dataQuota;
    }
    
    public void setDataUsed(final String dataUsed) {
        this.dataUsed = dataUsed;
    }
    
    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }
    
    public void setHasDataToSync(final boolean dataSynced) {
        this.dataSynced = dataSynced;
    }
    
    public void setIsLoggedIn(final boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }
    
    public void setMobileAccount(final boolean mobileAccount) {
        this.mobileAccount = mobileAccount;
    }
    
    public void setuID(final String uID) {
        this.uID = uID;
    }
    
    public void setUserGUID(final String userGUID) {
        this.userGUID = userGUID;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getDataQuota() {
        return this.dataQuota;
    }
    
    public String getDataUsed() {
        return this.dataUsed;
    }
    
    public String getFullName() {
        return this.fullName;
    }
    
    public boolean isHasSecureToken() {
        return this.hasSecureToken;
    }
    
    public void setHasSecureToken(final boolean hasSecureToken) {
        this.hasSecureToken = hasSecureToken;
    }
    
    public boolean getHasDataToSync() {
        return this.dataSynced;
    }
    
    public boolean getIsLoggedIn() {
        return this.isLoggedIn;
    }
    
    public boolean getMobileAccount() {
        return this.mobileAccount;
    }
    
    public String getuID() {
        return this.uID;
    }
    
    public String getUserGUID() {
        return this.userGUID;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    @Override
    public int compareTo(final AppleUserAccount otherObject) {
        int compareResult = 0;
        if (compareResult == 0) {
            compareResult = this.uID.compareTo(otherObject.uID);
        }
        return compareResult;
    }
}
