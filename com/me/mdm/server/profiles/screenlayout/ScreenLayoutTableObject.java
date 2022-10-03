package com.me.mdm.server.profiles.screenlayout;

public class ScreenLayoutTableObject
{
    private String pageTableName;
    private String screenPageToPageLayoutTableName;
    private String screenPageLayoutTableName;
    private String screenPageLayoutToAppTableName;
    private String screenPageLayoutToFolderTableName;
    private String screenPageLayoutToWebClipTableName;
    private String appGroupTableName;
    private String webClipPolicyTableName;
    private String packageToAppData;
    
    public ScreenLayoutTableObject(final String pageTableName, final String screenPageToPageLayoutTableName, final String screenPageLayoutTableName, final String screenPageLayoutToAppTableName, final String screenPageLayoutToFolderTableName, final String screenPageLayoutToWebClipTableName, final String appGroupTableName, final String webClipPolicyTableName, final String packageToAppData) {
        this.pageTableName = pageTableName;
        this.screenPageToPageLayoutTableName = screenPageToPageLayoutTableName;
        this.screenPageLayoutTableName = screenPageLayoutTableName;
        this.screenPageLayoutToAppTableName = screenPageLayoutToAppTableName;
        this.screenPageLayoutToFolderTableName = screenPageLayoutToFolderTableName;
        this.screenPageLayoutToWebClipTableName = screenPageLayoutToWebClipTableName;
        this.appGroupTableName = appGroupTableName;
        this.webClipPolicyTableName = webClipPolicyTableName;
        this.packageToAppData = packageToAppData;
    }
    
    public String getPageTableName() {
        return this.pageTableName;
    }
    
    public String getScreenPageToPageLayoutTableName() {
        return this.screenPageToPageLayoutTableName;
    }
    
    public String getScreenPageLayoutTableName() {
        return this.screenPageLayoutTableName;
    }
    
    public String getScreenPageLayoutToAppTableName() {
        return this.screenPageLayoutToAppTableName;
    }
    
    public String getScreenPageLayoutToFolderTableName() {
        return this.screenPageLayoutToFolderTableName;
    }
    
    public String getScreenPageLayoutToWebClipTableName() {
        return this.screenPageLayoutToWebClipTableName;
    }
    
    public String getAppGroupTableName() {
        return this.appGroupTableName;
    }
    
    public String getWebClipPolicyTableName() {
        return this.webClipPolicyTableName;
    }
    
    public String getPackageToAppData() {
        return this.packageToAppData;
    }
}
