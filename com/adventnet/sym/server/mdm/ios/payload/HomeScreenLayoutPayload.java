package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSArray;

public class HomeScreenLayoutPayload extends IOSPayload
{
    private NSArray pageArray;
    private NSArray pageLayoutArray;
    private NSArray folderLayoutPages;
    private NSDictionary folderDictionary;
    private NSArray folderLayoutArray;
    
    public HomeScreenLayoutPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.homescreenlayout", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    private void setPageArray(final String key, final int pageSize) {
        final NSArray nsArray = new NSArray(pageSize);
        this.getPayloadDict().put(key, (NSObject)nsArray);
        this.pageArray = nsArray;
    }
    
    public void setScreenPageArray(final int pageSize) {
        this.setPageArray("Pages", pageSize);
    }
    
    public void setDockPageArray(final int size) {
        this.setPageArray("Dock", size);
        this.pageLayoutArray = this.pageArray;
    }
    
    public void setPageLayoutArray(final int pageNo, final int pageLayoutSize, final boolean isFolder) {
        final NSArray pageLayoutArray = new NSArray(pageLayoutSize);
        if (isFolder) {
            this.folderLayoutPages.setValue(pageNo - 1, (Object)pageLayoutArray);
            this.folderLayoutArray = pageLayoutArray;
        }
        else {
            this.pageArray.setValue(pageNo - 1, (Object)pageLayoutArray);
            this.pageLayoutArray = pageLayoutArray;
        }
    }
    
    public void setFolderLayoutPages(final int pageSize) {
        final NSArray folderPages = new NSArray(pageSize);
        this.folderDictionary.put("Pages", (NSObject)folderPages);
        this.folderLayoutPages = folderPages;
    }
    
    public void setAppDetails(final int position, final String bundleIdentifier, final boolean isFolder) {
        final NSArray layoutArray = this.getPageLayoutArray(isFolder);
        final NSDictionary dictionary = new NSDictionary();
        dictionary.put("BundleID", (Object)bundleIdentifier);
        dictionary.put("Type", (Object)"Application");
        layoutArray.setValue(position, (Object)dictionary);
    }
    
    public void setWebclipDetails(final int position, final String url, final boolean isFolder) {
        final NSArray layoutArray = this.getPageLayoutArray(isFolder);
        final NSDictionary dictionary = new NSDictionary();
        dictionary.put("URL", (Object)url);
        dictionary.put("Type", (Object)"WebClip");
        layoutArray.setValue(position, (Object)dictionary);
    }
    
    public void setFolderDetails(final int position, final String folderName) {
        final NSDictionary dictionary = new NSDictionary();
        dictionary.put("Type", (Object)"Folder");
        dictionary.put("DisplayName", (Object)folderName);
        this.folderDictionary = dictionary;
        this.pageLayoutArray.setValue(position, (Object)dictionary);
    }
    
    private NSArray getPageLayoutArray(final boolean isFolder) {
        if (isFolder) {
            return this.folderLayoutArray;
        }
        return this.pageLayoutArray;
    }
}
