package com.me.ems.framework.server.tabcomponents.api.v1.service;

import java.util.Arrays;
import java.util.EnumMap;
import javax.ws.rs.core.Response;
import com.me.ems.framework.server.tabcomponents.core.TabBean;
import java.util.Set;
import com.me.ems.framework.server.tabcomponents.core.TabComponentCacheUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import java.util.Map;
import java.util.List;
import java.util.logging.Level;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.server.tabcomponents.core.TabComponentUtil;
import com.me.ems.framework.server.tabcomponents.api.v1.model.TabComponent;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class TabComponentService
{
    private static final Logger LOGGER;
    
    public TabComponent getApplicableTabs(final User user) throws APIException {
        final TabComponent tabComponentResponseObject = new TabComponent();
        try {
            final Long userID = user.getUserID();
            final List<Map<ServerAPIConstants.TabAttribute, Object>> applicableTabList = TabComponentUtil.getApplicableTabs(user);
            TabComponentUtil.setIsNewTabKeyForTabs(applicableTabList, userID);
            TabComponentUtil.sortTabsBasedOnUserPreference(applicableTabList, userID);
            if (applicableTabList.isEmpty()) {
                throw new APIException("GENERIC0005");
            }
            tabComponentResponseObject.setTabs(applicableTabList);
            return tabComponentResponseObject;
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            TabComponentService.LOGGER.log(Level.SEVERE, "Exception occurred while fetching tabs ", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    public Map<String, Boolean> updateTabPosition(final List<String> updatedTabOrder, final User user) throws APIException {
        final Map<String, Boolean> responseMap = new HashMap<String, Boolean>(2);
        try {
            final Long userID = user.getUserID();
            final Set<String> tabIDsFromClient = new HashSet<String>(updatedTabOrder);
            final Set<String> tabIDsFromServer = TabComponentUtil.getTabIdsFromServer(user);
            if (!tabIDsFromClient.equals(tabIDsFromServer)) {
                throw new APIException("TAB001");
            }
            final String existingOrder = TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER);
            final String updatedOrder = String.join(",", updatedTabOrder);
            final boolean isPositionUpdated = !updatedOrder.equals(existingOrder);
            if (isPositionUpdated) {
                TabComponentCacheUtil.addOrUpdateTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.USER_TO_TAB_ORDER, updatedOrder);
                TabComponentCacheUtil.addOrUpdateTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.HAS_USER_CUSTOMIZED, String.valueOf(isPositionUpdated));
            }
            responseMap.put("isPositionUpdated", isPositionUpdated);
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            TabComponentService.LOGGER.log(Level.SEVERE, "Exception occurred", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
        return responseMap;
    }
    
    public Map<String, String> addNewCustomTab(final TabBean tabBean, final Long userID) throws APIException {
        try {
            final String displayName = tabBean.getDisplayName();
            final String url = tabBean.getUrl();
            final String toolTip = tabBean.getToolTip();
            final Integer position = tabBean.getPosition();
            final String customTabID = TabComponentUtil.addCustomTabToDB(displayName, url, toolTip, userID);
            TabComponentUtil.addTabAtAParticularPosition(position, customTabID, userID);
            final Map<String, String> customTab = new HashMap<String, String>(2);
            customTab.put(ServerAPIConstants.TabAttribute.tabID.toString(), customTabID);
            return customTab;
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            TabComponentService.LOGGER.log(Level.SEVERE, "Exception occurred", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
    }
    
    public Response updateCustomTab(final TabBean tabBean, final String customTabID, final Long userID) throws APIException {
        try {
            final String displayName = tabBean.getDisplayName();
            final String url = tabBean.getUrl();
            final String toolTip = tabBean.getToolTip();
            TabComponentUtil.updateCustomTabToDB(customTabID, displayName, url, toolTip, userID);
            return Response.ok().build();
        }
        catch (final APIException apiEx) {
            throw apiEx;
        }
        catch (final Exception ex) {
            TabComponentService.LOGGER.log(Level.SEVERE, "Exception occurred", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
    }
    
    public Map<ServerAPIConstants.TabAttribute, Boolean> updateNewTabCounter(final String tabID, final Long userID) throws APIException {
        final Map<ServerAPIConstants.TabAttribute, Boolean> responseMap = new EnumMap<ServerAPIConstants.TabAttribute, Boolean>(ServerAPIConstants.TabAttribute.class);
        try {
            String newTabIDString = TabComponentCacheUtil.getTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.NEW_TABS);
            if (newTabIDString != null) {
                final Set<String> newTabIDs = new HashSet<String>(Arrays.asList(newTabIDString.split(",")));
                boolean newTabBadgeRequired = Boolean.FALSE;
                if (newTabIDs.contains(tabID)) {
                    newTabBadgeRequired = TabComponentCacheUtil.incrementCounterForNewTab(tabID, userID);
                }
                if (!newTabBadgeRequired) {
                    final boolean isRemoved = newTabIDs.remove(tabID);
                    if (isRemoved && newTabIDs.isEmpty()) {
                        TabComponentCacheUtil.deleteTabComponentUserParameters(userID, ServerAPIConstants.TabComponentCacheParam.NEW_TABS);
                    }
                    else if (isRemoved) {
                        newTabIDString = String.join(",", newTabIDs);
                        TabComponentCacheUtil.addOrUpdateTabComponentUserParameter(userID, ServerAPIConstants.TabComponentCacheParam.NEW_TABS, newTabIDString);
                    }
                }
                responseMap.put(ServerAPIConstants.TabAttribute.isNewTab, newTabBadgeRequired);
            }
            else {
                responseMap.put(ServerAPIConstants.TabAttribute.isNewTab, Boolean.FALSE);
            }
            return responseMap;
        }
        catch (final Exception ex) {
            TabComponentService.LOGGER.log(Level.SEVERE, "Exception occurred", ex);
            throw new APIException("GENERIC0002", ex.getLocalizedMessage(), new String[0]);
        }
    }
    
    public Response deleteCustomTab(final String tabID, final Long userID) throws APIException {
        TabComponentUtil.deleteCustomTabFromDB(tabID, userID);
        return Response.ok().build();
    }
    
    static {
        LOGGER = Logger.getLogger(TabComponentService.class.getName());
    }
}
