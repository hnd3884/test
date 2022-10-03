package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSString;
import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;
import java.util.List;

public class WebConentFilterPayload extends IOSPayload
{
    public WebConentFilterPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.webcontent-filter", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAutoFilterEnabled(final boolean value) {
        this.getPayloadDict().put("AutoFilterEnabled", (Object)value);
    }
    
    public void setWhitelistedBookmarks(final List urls) {
        final NSArray whitelistArray = this.getURLs(urls);
        this.getPayloadDict().put("WhitelistedBookmarks", (NSObject)whitelistArray);
        this.getPayloadDict().put("AllowListBookmarks", (NSObject)whitelistArray);
    }
    
    public void setBlacklistedURLs(final List urls) {
        final NSArray blacklistArray = this.getURLString(urls);
        this.getPayloadDict().put("BlacklistedURLs", (NSObject)blacklistArray);
        this.getPayloadDict().put("DenyListURLs", (NSObject)blacklistArray);
    }
    
    public void setFilterDataProviderBundleIdentifier(final String value) {
        this.getPayloadDict().put("FilterDataProviderBundleIdentifier", (Object)value);
    }
    
    public void setFilterBrowser(final Boolean value) {
        this.getPayloadDict().put("FilterBrowsers", (Object)value);
    }
    
    public void setFilterSockets(final Boolean value) {
        this.getPayloadDict().put("FilterSockets", (Object)value);
    }
    
    public void setFilterType(final String value) {
        this.getPayloadDict().put("FilterType", (Object)value);
    }
    
    public void setOrganization(final String value) {
        this.getPayloadDict().put("Organization", (Object)value);
    }
    
    public void setPayloadCertificateUUID(final String value) {
        this.getPayloadDict().put("PayloadCertificateUUID", (Object)value);
    }
    
    public void setPermittedURLs(final List<String> value) {
        final NSArray permittedURLs = this.getURLString(value);
        this.getPayloadDict().put("PermittedURLs", (NSObject)permittedURLs);
    }
    
    public void setPluginBundleID(final String value) {
        this.getPayloadDict().put("PluginBundleID", (Object)value);
    }
    
    public void setServerAddress(final String value) {
        this.getPayloadDict().put("ServerAddress", (Object)value);
    }
    
    public void setUserDefinedName(final String value) {
        this.getPayloadDict().put("UserDefinedName", (Object)value);
    }
    
    public void setUserName(final String value) {
        this.getPayloadDict().put("UserName", (Object)value);
    }
    
    public void setPassword(final String value) {
        this.getPayloadDict().put("Password", (Object)value);
    }
    
    public void setFilterDataProviderDesignatedRequirement(final String value) {
        this.getPayloadDict().put("FilterDataProviderDesignatedRequirement", (Object)value);
    }
    
    public void setFilterGrade(final String value) {
        this.getPayloadDict().put("FilterGrade", (Object)value);
    }
    
    public void setFilterPackets(final Boolean value) {
        this.getPayloadDict().put("FilterPackets", (Object)value);
    }
    
    public void setFilterPacketProviderBundleIdentifier(final String value) {
        this.getPayloadDict().put("FilterPacketProviderBundleIdentifier", (Object)value);
    }
    
    public void setFilterPacketProviderDesignatedRequirement(final String value) {
        this.getPayloadDict().put("FilterPacketProviderDesignatedRequirement", (Object)value);
    }
    
    public void setVendorConfig(final NSDictionary dictionary) {
        this.getPayloadDict().put("VendorConfig", (NSObject)dictionary);
    }
    
    private NSArray getURLs(final List<NSDictionary> urls) {
        final NSArray urlNSArray = new NSArray(urls.size() + 1);
        for (int i = 0; i < urls.size(); ++i) {
            final NSDictionary dic = urls.get(i);
            urlNSArray.setValue(i, (Object)dic);
        }
        final NSDictionary dic2 = this.getUrl("https://%ServerName%:%ServerPort%", "MDM", "");
        urlNSArray.setValue(urls.size(), (Object)dic2);
        return urlNSArray;
    }
    
    private NSArray getURLString(final List<String> urls) {
        final NSArray urlNSArray = new NSArray(urls.size());
        for (int i = 0; i < urls.size(); ++i) {
            urlNSArray.setValue(i, (Object)new NSString((String)urls.get(i)));
        }
        return urlNSArray;
    }
    
    public NSDictionary getUrl(final String url, final String name, final String path) {
        final NSDictionary urlDic = new NSDictionary();
        urlDic.put("URL", (Object)url);
        urlDic.put("Title", (Object)name);
        urlDic.put("BookmarkPath", (Object)path);
        return urlDic;
    }
}
