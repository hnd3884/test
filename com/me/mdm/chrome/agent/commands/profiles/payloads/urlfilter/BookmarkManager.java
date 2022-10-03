package com.me.mdm.chrome.agent.commands.profiles.payloads.urlfilter;

import java.util.Iterator;
import com.google.chromedevicemanagement.v1.model.EditBookmarksDisabled;
import com.google.chromedevicemanagement.v1.model.BookmarksBarEnabled;
import org.json.JSONObject;
import com.me.mdm.chrome.agent.GoogleChromeApiErrorHandler;
import java.util.Set;
import com.google.chromedevicemanagement.v1.model.ManagedBookmarks;
import com.google.chromedevicemanagement.v1.model.UserPolicy;
import com.me.mdm.chrome.agent.commands.profiles.PayloadResponse;
import java.util.List;
import com.me.mdm.chrome.agent.utils.ChromeAgentJSONUtil;
import org.json.JSONException;
import java.util.logging.Level;
import com.me.mdm.chrome.agent.db.MDMAgentParamsTableHandler;
import org.json.JSONArray;
import com.me.mdm.chrome.agent.Context;
import java.util.logging.Logger;

public class BookmarkManager
{
    public Logger logger;
    public static final String APPLIED_BOOKMARK_DETAIL = "AppliedBookmarkDetail";
    public static final String BOOKMARK_DETAILS = "BookmarkDetails";
    public static final String IS_EDITTING_ALLOWED = "IsEditBookmarksAllowed";
    public static final String TOPLEVEL_NAME = "toplevel_name";
    public static final String BOOKMARK_NAME = "name";
    public static final String BOOKMARK_URL = "url";
    public static final String BOOKMARKS = "Bookmarks";
    
    public BookmarkManager() {
        this.logger = Logger.getLogger("MDMChromeAgentLogger");
    }
    
    public void addPayloadIdentifierToDB(final Context context, final String payloadIdentifierName) {
        try {
            int i;
            JSONArray appliedPayloadIdentifiers;
            for (i = 0, appliedPayloadIdentifiers = new JSONArray(), appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedBookmarkDetail"), i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                if (String.valueOf(appliedPayloadIdentifiers.get(i)).equals(payloadIdentifierName)) {
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
            if (i == appliedPayloadIdentifiers.length()) {
                appliedPayloadIdentifiers.put((Object)payloadIdentifierName);
            }
            new MDMAgentParamsTableHandler(context).addJSONArray("AppliedBookmarkDetail", appliedPayloadIdentifiers);
        }
        catch (final JSONException e) {
            this.logger.log(Level.INFO, "\" Exception in addPayloadIdentifierToDB :\",", (Throwable)e);
        }
    }
    
    public void removePayloadData(final Context context, final String payloadIdentifierName) {
        try {
            JSONArray appliedPayloadIdentifiers = new JSONArray();
            final ChromeAgentJSONUtil jSONUtil = new ChromeAgentJSONUtil();
            appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedBookmarkDetail");
            final List<String> appliedPayloadList = jSONUtil.convertJSONArrayTOList(appliedPayloadIdentifiers);
            for (int i = 0; i < appliedPayloadList.size(); ++i) {
                if (appliedPayloadList.get(i).equals(payloadIdentifierName)) {
                    appliedPayloadList.remove(i);
                    new MDMAgentParamsTableHandler(context).addJSONArray("AppliedBookmarkDetail", jSONUtil.convertListToJSONArray(appliedPayloadList));
                    new MDMAgentParamsTableHandler(context).removeValue(payloadIdentifierName);
                    break;
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, " Exception in removePayload data :", e);
        }
    }
    
    public void setBookmarkData(final Context context, final PayloadResponse payloadResp, final Boolean isInstallProfile) {
        this.logger.log(Level.INFO, "Going to set the Bookmarks Folder");
        try {
            final JSONArray managedbookmark = this.createBookMarkData(context);
            this.logger.log(Level.INFO, "BookMark Data : {0}", managedbookmark);
            final UserPolicy userPolicy = new UserPolicy();
            if (managedbookmark.length() > 0) {
                final ManagedBookmarks managedBookmarks = new ManagedBookmarks();
                managedBookmarks.setManagedBookmarks(managedbookmark.toString());
                userPolicy.setManagedBookmarks(managedBookmarks);
            }
            else {
                userPolicy.setManagedBookmarks(new ManagedBookmarks());
            }
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, isInstallProfile);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    public JSONArray createBookMarkData(final Context context) {
        final JSONArray bookmarksJSON = new JSONArray();
        try {
            int bookmarks = 0;
            final JSONArray appliedPayloadIdentifiers = new MDMAgentParamsTableHandler(context).optJSONArray("AppliedBookmarkDetail");
            for (int i = 0; i < appliedPayloadIdentifiers.length(); ++i) {
                final JSONArray payloadData = new MDMAgentParamsTableHandler(context).optJSONArray(String.valueOf(appliedPayloadIdentifiers.get(i)));
                for (int j = 0; j < payloadData.length(); ++j) {
                    final JSONObject BookmarkObject = payloadData.optJSONObject(j);
                    final JSONObject bookmarkData = new JSONObject();
                    if (BookmarkObject.opt("url") != null) {
                        bookmarkData.put("name", (Object)BookmarkObject.optString("name"));
                        bookmarkData.put("url", (Object)BookmarkObject.optString("url"));
                        bookmarksJSON.put(bookmarks++, (Object)bookmarkData);
                    }
                    else if (BookmarkObject.opt("toplevel_name") != null) {
                        bookmarkData.put("toplevel_name", (Object)BookmarkObject.optString("toplevel_name"));
                        bookmarksJSON.put(bookmarks++, (Object)bookmarkData);
                    }
                }
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception in creating Bookmark data :", e);
        }
        return bookmarksJSON;
    }
    
    public void enableBookmarksBar(final Context context, final boolean isEditingAllowed, final PayloadResponse payloadResp) {
        try {
            final UserPolicy userPolicy = new UserPolicy();
            final BookmarksBarEnabled bookmarksBarEnabled = new BookmarksBarEnabled();
            bookmarksBarEnabled.setBookmarksBarEnabledMode("BOOKMARKS_BAR_ENABLED_ALWAYS");
            final EditBookmarksDisabled editBookmarksDisabled = new EditBookmarksDisabled();
            editBookmarksDisabled.setEditBookmarksDisabled(Boolean.valueOf(!isEditingAllowed));
            userPolicy.setEditBookmarksDisabled(editBookmarksDisabled);
            userPolicy.setBookmarksBarEnabled(bookmarksBarEnabled);
            final String updateMask = this.getUpdateMask(userPolicy.keySet());
            context.getCMPAService().enterprises().users().updateUserPolicy(context.getCMPAEnterpriseAndUDID(), userPolicy).setUpdateMask(updateMask).execute();
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception : ", ex);
            final JSONObject errorJSON = GoogleChromeApiErrorHandler.getErrorResponseJSON(ex, true);
            payloadResp.setErrorCode(errorJSON.optInt("errorCode", 70010));
            payloadResp.setErrorMsg(errorJSON.optString("errorMsg", ex.getMessage()));
        }
    }
    
    private String getUpdateMask(final Set<String> keySet) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : keySet) {
            builder.append(s + ",");
        }
        return builder.toString();
    }
}
