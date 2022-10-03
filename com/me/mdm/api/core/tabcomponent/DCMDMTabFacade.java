package com.me.mdm.api.core.tabcomponent;

import java.util.Arrays;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import java.util.Iterator;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.i18n.I18N;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.json.JSONArray;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.me.ems.framework.server.tabcomponents.core.ServerAPIConstants;
import java.util.Map;
import com.me.mdm.api.core.tabcomponent.quicklaunch.DCMDMTabProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;

public class DCMDMTabFacade
{
    public JSONObject getApplicableTabs() throws Exception {
        final List<String> userRoleList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
        final List<Map<ServerAPIConstants.TabAttribute, Object>> tempList = new DCMDMTabProvider().getProductSpecificTabComponents();
        final Map<String, Map<ServerAPIConstants.TabAttribute, Object>> tabComponents = tempList.stream().collect((Collector<? super Object, ?, Map<String, Map<ServerAPIConstants.TabAttribute, Object>>>)Collectors.toConcurrentMap(map -> map.get(ServerAPIConstants.TabAttribute.tabID), map -> map, (firstKey, secondKey) -> firstKey));
        final Iterator iterator = tabComponents.entrySet().iterator();
        final JSONObject responseObject = new JSONObject();
        final JSONArray jsonArray = new JSONArray();
        while (iterator.hasNext()) {
            final Map.Entry<String, Map<ServerAPIConstants.TabAttribute, Object>> tab = iterator.next();
            final Map tabMap = tab.getValue();
            final String roles = tabMap.get(ServerAPIConstants.TabAttribute.roles);
            if (this.checkIfApplicableForUser(new ArrayList(userRoleList), roles)) {
                final JSONObject compObject = new JSONObject();
                compObject.put("tabid", (Object)tabMap.get(ServerAPIConstants.TabAttribute.tabID).toString()).put("canbereordered", (Object)tabMap.get(ServerAPIConstants.TabAttribute.canBeReordered).toString()).put("displayname", (Object)I18N.getMsg(tabMap.get(ServerAPIConstants.TabAttribute.displayName).toString(), new Object[0])).put("roles", (Object)tabMap.get(ServerAPIConstants.TabAttribute.roles).toString()).put("taborder", (Object)tabMap.get(ServerAPIConstants.TabAttribute.tabOrder).toString()).put("tooltip", (Object)tabMap.get(ServerAPIConstants.TabAttribute.toolTip).toString()).put("iconurl", (Object)tabMap.get(ServerAPIConstants.TabAttribute.iconURL).toString()).put("url", (Object)tabMap.get(ServerAPIConstants.TabAttribute.url).toString());
                jsonArray.put((Object)compObject);
            }
        }
        if (jsonArray.length() < 1) {
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        responseObject.put("tabs", (Object)jsonArray);
        return responseObject;
    }
    
    private boolean checkIfApplicableForUser(final List userRoles, final String roles) throws Exception {
        if (!MDMStringUtils.isEmpty(roles)) {
            final List<String> tabRoles = new ArrayList<String>(Arrays.asList(roles.split(",")));
            tabRoles.retainAll(userRoles);
            if (tabRoles.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
