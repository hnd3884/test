package com.adventnet.sym.server.mdm.ios.payload.transform;

import org.json.JSONArray;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.google.api.client.util.ArrayMap;
import com.me.mdm.server.security.profile.PayloadSecretFieldsHandler;
import java.util.Iterator;
import java.util.logging.Level;
import com.me.mdm.server.common.customdata.CustomDataHandler;
import java.util.List;
import com.dd.plist.NSDictionary;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.mdm.server.profiles.config.WebContentConfigHandler;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.WebConentFilterPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.logging.Logger;

public class Do2WebContentPolicyPayload implements DO2Payload
{
    private Logger logger;
    
    public Do2WebContentPolicyPayload() {
        this.logger = Logger.getLogger("MDMConfigLogger");
    }
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        WebConentFilterPayload payload = null;
        final WebConentFilterPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("IOSWebContentPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new WebConentFilterPayload(1, "MDM", "com.mdm.mobiledevice.webcontent.filter", "Web Content Filter Policy");
                final Integer filterType = (Integer)row.get("FILTER_TYPE");
                final Boolean isBuiltIn = WebContentConfigHandler.BUILTIN_WCF.equals(filterType);
                final long policyId = (long)row.get("CONFIG_DATA_ITEM_ID");
                if (isBuiltIn) {
                    final boolean urlFilterType = (boolean)row.get("URL_FILTER_TYPE");
                    final Criteria cPolicy = new Criteria(new Column("URLRestrictionDetails", "CONFIG_DATA_ITEM_ID"), (Object)policyId, 0);
                    final Join connAttemptjoin = new Join("URLRestrictionDetails", "URLDetails", new String[] { "URL_DETAILS_ID" }, new String[] { "URL_DETAILS_ID" }, 2);
                    final boolean autoFilter = (boolean)row.get("ENABLE_AUTO_FILTER");
                    final Iterator urlIterator = dataObject.getRows("URLDetails", cPolicy, connAttemptjoin);
                    if (urlFilterType) {
                        final List<NSDictionary> urlList = new ArrayList<NSDictionary>();
                        while (urlIterator.hasNext()) {
                            final Row urlDetails = urlIterator.next();
                            final String name = (String)urlDetails.get("URL");
                            final String title = (String)urlDetails.get("BOOKMARK_TITILE");
                            final String path = (String)urlDetails.get("BOOKMARK_PATH");
                            final NSDictionary urlDict = payload.getUrl(name, title, path);
                            urlList.add(urlDict);
                        }
                        payload.setWhitelistedBookmarks(urlList);
                    }
                    else {
                        final List<String> urlList2 = new ArrayList<String>();
                        while (urlIterator.hasNext()) {
                            final Row urlDetails = urlIterator.next();
                            final String name = (String)urlDetails.get("URL");
                            urlList2.add(name);
                        }
                        payload.setBlacklistedURLs(urlList2);
                        payload.setAutoFilterEnabled(autoFilter);
                    }
                    if (!autoFilter) {
                        continue;
                    }
                    this.setPermittedURLS(dataObject, payload);
                }
                else {
                    this.setPluginPayladFields(dataObject, payload);
                    if (!CustomDataHandler.hasCustomConfigData(dataObject)) {
                        continue;
                    }
                    final NSDictionary customDict = new CustomDataHandler().getCustomDataAsNSDict(policyId, dataObject);
                    payload.setVendorConfig(customDict);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in webcontent fileter", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    private void setPluginPayladFields(final DataObject dataObject, final WebConentFilterPayload payload) throws Exception {
        final Row appleWCFRow = dataObject.containsTable("AppleWCFConfig") ? dataObject.getFirstRow("AppleWCFConfig") : null;
        final Row macWCFRow = dataObject.containsTable("MacWCFKext") ? dataObject.getFirstRow("MacWCFKext") : null;
        final String userName = (appleWCFRow != null) ? ((String)appleWCFRow.get("USER_NAME")) : null;
        final String userDefName = (appleWCFRow != null) ? ((String)appleWCFRow.get("USER_DEF_NAME")) : null;
        final String serverAddress = (appleWCFRow != null) ? ((String)appleWCFRow.get("SERVER_ADDRESS")) : null;
        final String pluginBundleID = (appleWCFRow != null) ? ((String)appleWCFRow.get("PLUGIN_BUNDLE_ID")) : null;
        final String organization = (appleWCFRow != null) ? ((String)appleWCFRow.get("ORGANIZATION")) : null;
        final Boolean filterSocket = (appleWCFRow != null) ? ((Boolean)appleWCFRow.get("FILTER_SOCKET")) : null;
        final Boolean filterBrowser = (appleWCFRow != null) ? ((Boolean)appleWCFRow.get("FILTER_BROWSER")) : null;
        String password = "";
        if (appleWCFRow.get("PASSWORD_ID") != null) {
            final Long password_Id = (Long)appleWCFRow.get("PASSWORD_ID");
            password = PayloadSecretFieldsHandler.getInstance().constructPayloadSecretField(password_Id.toString());
        }
        final String filterDataBundleID = (macWCFRow != null) ? ((String)macWCFRow.get("FILTER_DATA_BUNDLE_ID")) : null;
        final String filterDataCodeReq = (macWCFRow != null) ? ((String)macWCFRow.get("FILTER_DATA_CODE_REQ")) : null;
        final Integer filterGrade = (macWCFRow != null) ? ((Integer)macWCFRow.get("FILTER_GRADE")) : null;
        final Boolean filterPacket = (macWCFRow != null) ? ((Boolean)macWCFRow.get("FILTER_PACKET")) : null;
        final String filterPacketBundleID = (macWCFRow != null) ? ((String)macWCFRow.get("FILTER_PACKET_BUNDLE_ID")) : null;
        final String filterPacketCodeReq = (macWCFRow != null) ? ((String)macWCFRow.get("FILTER_PACKET_CODE_REQ")) : null;
        final Map<Integer, String> filterGradeMap = (Map<Integer, String>)new ArrayMap<Integer, String>() {
            {
                this.put((Object)1, (Object)"firewall");
                this.put((Object)2, (Object)"inspector");
            }
        };
        payload.setFilterType("Plugin");
        if (!MDMStringUtils.isEmpty(userName)) {
            payload.setUserName(userName);
        }
        if (!MDMStringUtils.isEmpty(password)) {
            payload.setPassword(password);
        }
        if (!MDMStringUtils.isEmpty(userDefName)) {
            payload.setUserDefinedName(userDefName);
        }
        if (serverAddress != null) {
            payload.setServerAddress(serverAddress);
        }
        if (!MDMStringUtils.isEmpty(pluginBundleID)) {
            payload.setPluginBundleID(pluginBundleID);
        }
        if (!MDMStringUtils.isEmpty(organization)) {
            payload.setOrganization(organization);
        }
        if (filterSocket != null) {
            payload.setFilterSockets(filterSocket);
            if (filterSocket && !MDMStringUtils.isEmpty(filterDataBundleID)) {
                payload.setFilterDataProviderBundleIdentifier(filterDataBundleID);
            }
            if (filterSocket && !MDMStringUtils.isEmpty(filterDataCodeReq)) {
                payload.setFilterDataProviderDesignatedRequirement(filterDataCodeReq);
            }
        }
        if (filterBrowser != null) {
            payload.setFilterBrowser(filterBrowser);
        }
        if (filterPacket != null) {
            payload.setFilterPackets(filterPacket);
            if (filterPacket && !MDMStringUtils.isEmpty(filterPacketBundleID)) {
                payload.setFilterPacketProviderBundleIdentifier(filterPacketBundleID);
            }
            if (filterPacket && !MDMStringUtils.isEmpty(filterPacketCodeReq)) {
                payload.setFilterPacketProviderDesignatedRequirement(filterPacketCodeReq);
            }
        }
        if (filterGrade != null) {
            payload.setFilterGrade(filterGradeMap.get(filterGrade));
        }
    }
    
    private void setPermittedURLS(final DataObject dataObject, final WebConentFilterPayload payload) throws Exception {
        final JSONArray permitedURLSArray = new WebContentConfigHandler().getUrlLinksForTable(dataObject, "AppleWCFPermittedURL", null);
        if (permitedURLSArray.length() == 0) {
            return;
        }
        final List<String> permittedURLs = new ArrayList<String>();
        for (int i = 0; i < permitedURLSArray.length(); ++i) {
            final String url = permitedURLSArray.getJSONObject(i).getString("URL".toLowerCase());
            permittedURLs.add(url);
        }
        payload.setPermittedURLs(permittedURLs);
    }
}
