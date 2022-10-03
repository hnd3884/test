package com.adventnet.sym.webclient.mdm.inv;

import java.util.HashMap;
import com.me.mdm.server.customgroup.GroupFacade;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import org.json.simple.JSONArray;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
import java.util.ArrayList;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.mdm.server.util.CalendarUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.i18n.I18N;
import com.me.mdm.server.settings.location.GeoLocationFacade;
import com.adventnet.sym.server.mdm.group.MDMGroupFilterHandler;
import org.json.simple.JSONObject;
import java.util.LinkedHashMap;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class DeviceListTRAction extends MDMEmberTableRetrieverAction
{
    public Logger logger;
    
    public DeviceListTRAction() {
        this.logger = Logger.getLogger(DeviceListTRAction.class.getName());
    }
    
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            Criteria criteria = new Criteria(new Column("ManagedDevice", "MANAGED_STATUS"), (Object)2, 0, false);
            final Criteria simSlotCriteria = new Criteria(new Column("MdSIM2Info", "SLOT"), (Object)2, 0, false);
            final Criteria mdSIM2InfoJoinCri = new Criteria(new Column("MdSIM2Info", "RESOURCE_ID"), (Object)new Column("ManagedDevice", "RESOURCE_ID"), 0);
            query.addJoin(new Join("ManagedDevice", "ManagedDevice", "MdSIMInfo", "MdSIM2Info", simSlotCriteria.and(mdSIM2InfoJoinCri), 1));
            final Column sim2Number = new Column("MdSIM2Info", "PHONE_NUMBER");
            sim2Number.setColumnAlias("MdSIM2Info.PHONE_NUMBER");
            final Column sim2Imei = new Column("MdSIM2Info", "IMEI");
            sim2Imei.setColumnAlias("MdSIM2Info.IMEI");
            query.addSelectColumn(sim2Imei);
            query.addSelectColumn(sim2Number);
            final HttpServletRequest request = viewCtx.getRequest();
            final String viewName = viewCtx.getUniqueId();
            final List mdmGpList = MDMGroupHandler.getCustomGroups();
            if (mdmGpList != null) {
                request.setAttribute("mdmGroupList", (Object)mdmGpList);
            }
            LinkedHashMap OSVersionMap = new LinkedHashMap();
            String platform = request.getParameter("platformType");
            if (platform == null) {
                platform = "";
            }
            JSONObject OSVersionMap2 = new JSONObject();
            if (platform.equalsIgnoreCase("all") || platform.isEmpty()) {
                OSVersionMap2 = MDMGroupFilterHandler.getInstance().getAllOSVersion();
            }
            else {
                OSVersionMap2 = MDMGroupFilterHandler.getInstance().getOSVersionByPlatform(Integer.parseInt(platform));
            }
            final Object members = OSVersionMap2.get((Object)"FILTER_MEMBERS");
            OSVersionMap = this.extractOSVersion(members, "", OSVersionMap);
            request.setAttribute("OSVersionMap", (Object)OSVersionMap);
            request.setAttribute("toolID", (Object)"40033");
            final String lostModeStatus = request.getParameter("lostModeStatus");
            final String geoStatus = request.getParameter("geoStatus");
            final String mdmGroupIdStr = request.getParameter("mdmGroupId");
            final String scanStatus = request.getParameter("scanStatus");
            final String modelType = request.getParameter("modelType");
            final String osCategory = request.getParameter("osCategory");
            final String inactiveStartDate = request.getParameter("inactiveStartDate");
            final String inactiveEndDate = request.getParameter("inactiveEndDate");
            final String userId = request.getParameter("userId");
            final String kioskStatus = request.getParameter("kioskStatus");
            if (userId != null && !userId.equalsIgnoreCase("")) {
                final Long userIdLong = Long.parseLong(userId);
                final Criteria userIdCri = new Criteria(new Column("ManagedUser", "MANAGED_USER_ID"), (Object)userIdLong, 0);
                criteria = criteria.and(userIdCri);
                request.setAttribute("showBulkEdit", (Object)false);
                request.setAttribute("userId", (Object)userId);
            }
            else {
                request.setAttribute("userId", (Object)"");
            }
            if (scanStatus != null && !"all".equals(scanStatus)) {
                request.setAttribute("scanStatus", (Object)scanStatus);
                final int status = new Integer(scanStatus);
                if (status != -1) {
                    final Criteria scanStatusCriteria = new Criteria(new Column("MdDeviceScanStatus", "SCAN_STATUS"), (Object)status, 0);
                    criteria = criteria.and(scanStatusCriteria);
                }
                else {
                    final Criteria scanStatusCriteria = new Criteria(new Column("MdDeviceScanStatus", "SCAN_STATUS"), (Object)new Integer[] { 2, 0 }, 9);
                    criteria = criteria.and(scanStatusCriteria);
                }
            }
            if (geoStatus != null && !"all".equals(geoStatus)) {
                query.addJoin(new Join("ManagedDevice", "MdAppCatalogToResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                query.addJoin(new Join("MdAppCatalogToResource", "MdAppDetails", new String[] { "INSTALLED_APP_ID" }, new String[] { "APP_ID" }, 1));
                query.addJoin(new Join("ManagedDevice", "IOSNativeAppStatus", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
                final int geoStatusInt = Integer.valueOf(geoStatus);
                final Criteria geoStatusCri = new GeoLocationFacade().getGeoStatusCrit(geoStatusInt, false);
                criteria = ((criteria == null) ? geoStatusCri : criteria.and(geoStatusCri));
            }
            if (lostModeStatus != null && !"all".equals(lostModeStatus)) {
                Criteria lmStatusCriteria = null;
                if (viewName.equals("DeviceList")) {
                    final Boolean status2 = Boolean.parseBoolean(lostModeStatus);
                    if (status2) {
                        lmStatusCriteria = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 1, 4, 6, 2 }, 8);
                        criteria = ((criteria == null) ? lmStatusCriteria : criteria.and(lmStatusCriteria));
                    }
                }
            }
            if (modelType != null && modelType != "" && !"all".equals(modelType)) {
                request.setAttribute("modelType", (Object)modelType);
                final int modeltype = Integer.parseInt(modelType);
                if (modeltype == 12) {
                    final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new int[] { 1, 2 }, 8, false);
                    criteria = criteria.and(modelCriteria);
                }
                else {
                    final Criteria modelCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)modeltype, 0, false);
                    criteria = criteria.and(modelCriteria);
                }
                final String multiUser = request.getParameter("isMultiUser");
                if (Integer.parseInt(modelType) == 2 && multiUser != null && !multiUser.equalsIgnoreCase("all")) {
                    criteria = criteria.and(new Criteria(new Column("MdDeviceInfo", "IS_MULTIUSER"), (Object)multiUser, 0));
                }
            }
            if (osCategory != null && !"all".equals(osCategory)) {
                String platformType = null;
                if (platform.compareTo("1") == 0) {
                    platformType = I18N.getMsg("dc.mdm.ios", new Object[0]);
                }
                else if (platform.compareTo("2") == 0) {
                    platformType = I18N.getMsg("dc.mdm.android", new Object[0]);
                }
                else if (platform.compareTo("3") == 0) {
                    platformType = I18N.getMsg("dc.common.WINDOWS", new Object[0]);
                }
                else if (platform.compareTo(Integer.valueOf(4).toString()) == 0) {
                    platformType = I18N.getMsg("mdm.common.chrome", new Object[0]);
                }
                final String osVer = osCategory.replace("*", "x");
                request.setAttribute("osCategory", (Object)platformType);
                request.setAttribute("osVer", (Object)osVer);
                if (osCategory.equalsIgnoreCase("others")) {
                    final Criteria os4CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"6.*", 3);
                    final Criteria os5CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"7.*", 3);
                    final Criteria os6CategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)"8.*", 3);
                    criteria = criteria.and(os4CategoryCriteria).and(os5CategoryCriteria.and(os6CategoryCriteria));
                }
                else {
                    final String[] osCat = osCategory.split(";");
                    Criteria osCategoryCriteria = null;
                    Criteria osCategoryCriteriaCombined = null;
                    for (int i = 0; i < osCat.length; ++i) {
                        osCategoryCriteria = new Criteria(new Column("MdDeviceInfo", "OS_VERSION"), (Object)osCat[i], 2);
                        if (osCategoryCriteriaCombined == null) {
                            osCategoryCriteriaCombined = osCategoryCriteria;
                        }
                        else {
                            osCategoryCriteriaCombined = osCategoryCriteriaCombined.or(osCategoryCriteria);
                        }
                    }
                    criteria = criteria.and(osCategoryCriteriaCombined);
                }
            }
            if (!MDMStringUtils.isEmpty(platform) && !"all".equals(platform)) {
                final Criteria iosDeviceTypeCrit = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 1, 2, 0 }, 8);
                final Criteria macDeviceTypeCrit = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)new Integer[] { 4, 3 }, 8);
                final Criteria tvCriteria = new Criteria(new Column("MdModelInfo", "MODEL_TYPE"), (Object)5, 0);
                final Criteria iosPlatformCri = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)1, 0);
                request.setAttribute("platform", (Object)platform);
                final Integer platformInt = new Integer(platform);
                Criteria cPlatform = new Criteria(new Column("ManagedDevice", "PLATFORM_TYPE"), (Object)new Integer(platform), 0);
                if (platformInt == 1) {
                    cPlatform = iosPlatformCri.and(iosDeviceTypeCrit);
                }
                else if (platformInt == 6) {
                    cPlatform = iosPlatformCri.and(macDeviceTypeCrit);
                }
                else if (platformInt == 7) {
                    cPlatform = iosPlatformCri.and(tvCriteria);
                }
                criteria = criteria.and(cPlatform);
            }
            if (mdmGroupIdStr != null && !"all".equals(mdmGroupIdStr)) {
                request.setAttribute("mdmGroupId", (Object)mdmGroupIdStr);
                final Long mdmGroupId = new Long(mdmGroupIdStr);
                query.addJoin(new Join("Resource", "CustomGroupMemberRel", new String[] { "RESOURCE_ID" }, new String[] { "MEMBER_RESOURCE_ID" }, 2));
                final Criteria cgCriteria = new Criteria(Column.getColumn("CustomGroupMemberRel", "GROUP_RESOURCE_ID"), (Object)mdmGroupId, 0);
                criteria = criteria.and(cgCriteria);
            }
            if (!MDMStringUtils.isEmpty(inactiveStartDate) && !MDMStringUtils.isEmpty(inactiveEndDate)) {
                long start = Long.parseLong(inactiveStartDate);
                long end = Long.parseLong(inactiveEndDate);
                if (start == -1L && end != -1L) {
                    end = CalendarUtil.getInstance().getStartTimeOfTheDay(end).getTime();
                    final Criteria criteria2 = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)end, 7);
                    criteria = criteria.and(criteria2);
                }
                else if (start != -1L && end != -1L) {
                    start = CalendarUtil.getInstance().getStartTimeOfTheDay(start).getTime();
                    end = CalendarUtil.getInstance().getEndTimeOfTheDay(end).getTime() - 1L;
                    final Criteria criteria3 = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)start, 4);
                    final Criteria criteria4 = new Criteria(Column.getColumn("AgentContact", "LAST_CONTACT_TIME"), (Object)end, 6);
                    criteria = criteria.and(criteria3).and(criteria4);
                }
            }
            if (viewName.equals("LostModeDeviceList")) {
                final SelectQuery subQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("CommandHistory"));
                final Column recent_added_time = new Column("CommandHistory", "ADDED_TIME").maximum();
                final Column comm = Column.getColumn("CommandHistory", "COMMAND_ID");
                final Column res = Column.getColumn("CommandHistory", "RESOURCE_ID");
                recent_added_time.setColumnAlias("subQuery.ADDED_TIME");
                subQuery.addSelectColumn(comm);
                subQuery.addSelectColumn(res);
                subQuery.addSelectColumn(recent_added_time);
                subQuery.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
                final Object cmdStatus = { 1, 2, 0, -1, 4 };
                Criteria criteria5 = new Criteria(new Column("CommandHistory", "COMMAND_STATUS"), cmdStatus, 8);
                criteria5 = criteria5.and(new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)"EnableLostMode", 0));
                subQuery.setCriteria(criteria5);
                final List groupByList = new ArrayList();
                groupByList.add(comm);
                groupByList.add(res);
                final GroupByClause groupByClause = new GroupByClause(groupByList);
                subQuery.setGroupByClause(groupByClause);
                final DerivedTable commandDerievedTab = new DerivedTable("subQuery", (Query)subQuery);
                query.addJoin(new Join(Table.getTable("LostModeTrackInfo"), (Table)commandDerievedTab, new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
                query.addJoin(new Join((Table)commandDerievedTab, Table.getTable("CommandHistory"), new String[] { "RESOURCE_ID", "COMMAND_ID", "subQuery.ADDED_TIME" }, new String[] { "RESOURCE_ID", "COMMAND_ID", "ADDED_TIME" }, 2));
                query.addJoin(new Join("CommandHistory", "MdCommands", new String[] { "COMMAND_ID" }, new String[] { "COMMAND_ID" }, 2));
                query.addJoin(new Join("CommandHistory", "CommandError", new String[] { "COMMAND_HISTORY_ID" }, new String[] { "COMMAND_HISTORY_ID" }, 1));
                final Object lmCmdStatus = { 2, 1, 3, 4, 6 };
                query.setCriteria(new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), lmCmdStatus, 8));
                final Column added_time = new Column("CommandHistory", "ADDED_TIME");
                added_time.setColumnAlias("CommandHistory.ADDED_TIME");
                query.addSelectColumn(added_time);
                final Column updated_time = new Column("CommandHistory", "UPDATED_TIME");
                updated_time.setColumnAlias("CommandHistory.UPDATED_TIME");
                query.addSelectColumn(updated_time);
                final Column remarks = new Column("CommandHistory", "REMARKS");
                remarks.setColumnAlias("CommandHistory.REMARKS");
                query.addSelectColumn(remarks);
                final Column error_code = new Column("CommandError", "ERROR_CODE");
                error_code.setColumnAlias("CommandError.ERROR_CODE");
                query.addSelectColumn(error_code);
                query.addSelectColumn(new Column("CommandHistory", "ADDED_BY"));
                query.addSelectColumn(new Column("Resource", "RESOURCE_ID"));
                query.addSelectColumn(new Column("LostModeTrackInfo", "LOST_MODE_TRACK_ID"));
                query.addSelectColumn(new Column("CommandHistory", "COMMAND_HISTORY_ID"));
                query.addSelectColumn(new Column("MdCommands", "COMMAND_ID"));
                criteria = criteria.and(new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), lmCmdStatus, 8));
                criteria = criteria.and(new Criteria(new Column("MdCommands", "COMMAND_UUID"), (Object)"EnableLostMode", 0));
                if (lostModeStatus != null && !"all".equals(lostModeStatus)) {
                    final int status3 = new Integer(lostModeStatus);
                    if (status3 != -1) {
                        Criteria lmStatusCriteria2 = null;
                        if (status3 == 1) {
                            lmStatusCriteria2 = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 1, 4 }, 8);
                        }
                        else if (status3 == 0) {
                            lmStatusCriteria2 = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)new Object[] { 3, 6 }, 8);
                        }
                        else {
                            lmStatusCriteria2 = new Criteria(new Column("LostModeTrackInfo", "TRACKING_STATUS"), (Object)2, 0);
                        }
                        criteria = criteria.and(lmStatusCriteria2);
                    }
                }
            }
            if (kioskStatus != null && !kioskStatus.equals("all")) {
                if (Integer.parseInt(kioskStatus) != 3) {
                    final Criteria kioskStatusCriteria = new Criteria(new Column("DeviceKioskStateInfo", "CURRENT_KIOSK_STATE"), (Object)kioskStatus, 0);
                    criteria = ((criteria == null) ? kioskStatusCriteria : criteria.and(kioskStatusCriteria));
                }
                else {
                    final Criteria kioskStatusCriteria = new Criteria(new Column("DeviceKioskStateInfo", "CURRENT_KIOSK_STATE"), (Object)kioskStatus, 0);
                    final Criteria nokioskStatusCriteria = new Criteria(new Column("DeviceKioskStateInfo", "CURRENT_KIOSK_STATE"), (Object)null, 0);
                    criteria = ((criteria == null) ? kioskStatusCriteria.or(nokioskStatusCriteria) : criteria.and(kioskStatusCriteria.or(nokioskStatusCriteria)));
                }
            }
            final int managedDeviceCount = ManagedDeviceHandler.getInstance().getManagedDeviceCount(null, false);
            request.setAttribute("managedDeviceCount", (Object)managedDeviceCount);
            query.setCriteria(criteria);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in DeviceListTRAction ", ex);
        }
        super.setCriteria(query, viewCtx);
    }
    
    public LinkedHashMap extractOSVersion(final Object members, String platform, final LinkedHashMap osVersionMap) {
        final JSONArray membersArray = (JSONArray)members;
        if (membersArray.size() > 0) {
            for (int i = 0; i < membersArray.size(); ++i) {
                final JSONObject obj = (JSONObject)membersArray.get(i);
                String os = (String)obj.get((Object)"FILTER_MEMBER_NAME");
                final int platformName = (int)obj.get((Object)"PLATFORM_NAME");
                if (platformName == 1) {
                    platform = "iOS";
                }
                else if (platformName == 2) {
                    platform = "Android";
                }
                else if (platformName == 3) {
                    platform = "Windows";
                }
                else if (platformName == 4) {
                    platform = "Chrome";
                }
                final String[] osver = os.split("\\.");
                os = osver[0] + ".x";
                osVersionMap.put(os, platform);
            }
        }
        return osVersionMap;
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        try {
            final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
            final ArrayList<Long> list = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "Resource.RESOURCE_ID");
            final HashMap hashMap = new GroupFacade().getAssociatedGroupsForResList(list);
            viewCtx.getRequest().setAttribute("ASSOCIATED_GROUP_NAMES", (Object)hashMap);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while Add Group Names..", e);
        }
    }
}
