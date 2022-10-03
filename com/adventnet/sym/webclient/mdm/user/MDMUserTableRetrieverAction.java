package com.adventnet.sym.webclient.mdm.user;

import java.util.Collection;
import java.util.Arrays;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.me.devicemanagement.framework.webclient.common.DMWebClientCommonUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.idps.core.util.DirectoryUtil;
import java.util.Iterator;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.sym.server.mdm.config.ProfileAssociateHandler;
import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.sym.server.mdm.config.ResourceSummaryHandler;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Table;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.sym.webclient.mdm.MDMEmberTableRetrieverAction;

public class MDMUserTableRetrieverAction extends MDMEmberTableRetrieverAction
{
    private static final ArrayList<Integer> SUPPORTED_DOMAINS;
    
    private boolean isTablePresent(final List<Table> tables, final String tableName) {
        for (int i = 0; i < tables.size(); ++i) {
            final Table curTable = tables.get(i);
            if (curTable.getTableAlias().equalsIgnoreCase(tableName) || curTable.getTableName().equalsIgnoreCase(tableName)) {
                return true;
            }
        }
        return false;
    }
    
    private String checkAndAddTable(final SelectQuery selectQuery, final Long attrID) {
        String tableAlias = null;
        if (attrID != null) {
            String attrName = null;
            tableAlias = "DirObjRegStrVal".toUpperCase() + "_";
            if (attrID == 106L) {
                attrName = "EMAIL";
            }
            else if (attrID == 114L) {
                attrName = "PHONE";
            }
            else if (attrID == 108L) {
                attrName = "LAST_NAME";
            }
            else if (attrID == 109L) {
                attrName = "FIRST_NAME";
            }
            else if (attrID == 110L) {
                attrName = "MIDDLE_NAME";
            }
            else if (attrID == 111L) {
                attrName = "DISPLAY_NAME";
            }
            else if (attrID == 116L) {
                attrName = "DOMAIN_NETBIOS_NAME";
            }
            else if (attrID == 128L) {
                attrName = "DEPARTMENT";
            }
            tableAlias += attrName;
        }
        final List<Table> tables = selectQuery.getTableList();
        if (!this.isTablePresent(tables, tableAlias)) {
            final Criteria joinCri = new Criteria(Column.getColumn(tableAlias, "ATTR_ID"), (Object)attrID, 0).and(new Criteria(Column.getColumn("DirResRel", "OBJ_ID"), (Object)Column.getColumn(tableAlias, "OBJ_ID"), 0));
            selectQuery.addJoin(new Join(new Table("DirResRel"), new Table("DirObjRegStrVal", tableAlias), joinCri, 1));
        }
        return tableAlias;
    }
    
    @Override
    protected SelectQuery fetchAndCacheSelectQuery(final ViewContext viewCtx) throws Exception {
        final SelectQuery selectQuery = super.fetchAndCacheSelectQuery(viewCtx);
        final List<Table> tables = selectQuery.getTableList();
        if (!this.isTablePresent(tables, "MDMResource")) {
            ResourceSummaryHandler.getInstance().updateResSummary(2);
            final Criteria statusJoinCri = new Criteria(Column.getColumn("DirObjRegIntVal", "ATTR_ID"), (Object)118L, 0).and(new Criteria(Column.getColumn("DirResRel", "OBJ_ID"), (Object)Column.getColumn("DirObjRegIntVal", "OBJ_ID"), 0));
            selectQuery.addJoin(new Join("Resource", "MDMResource", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 2));
            selectQuery.addJoin(new Join("DirResRel", "DirObjRegIntVal", statusJoinCri, 1));
            selectQuery.addJoin(new Join("MDMResource", "ResourceToProfileSummary", new String[] { "RESOURCE_ID" }, new String[] { "RESOURCE_ID" }, 1));
            selectQuery.addSelectColumn(Column.getColumn("DirResRel", "OBJ_ID"));
            selectQuery.addSelectColumn(Column.getColumn("DirObjRegIntVal", "VALUE", "DIROBJREGINTVAL_STATUS_VALUE"));
            selectQuery.addSelectColumn(Column.getColumn("ResourceToProfileSummary", "MEMBER_COUNT", "MANAGED_COUNT"));
        }
        return selectQuery;
    }
    
    private int getRequestParamInInt(final HttpServletRequest request, final String param) {
        int res = -1;
        final String str = request.getParameter(param);
        try {
            res = Integer.valueOf(str);
        }
        catch (final Exception ex) {
            res = -1;
        }
        return res;
    }
    
    private void setSearchCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final String[] searchcol = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_COLUMN");
        if (searchcol != null && searchcol.length != 0) {
            final String[] searchval = MDMApiFactoryProvider.getMDMTableViewAPI().getViewContextParameterValues(viewCtx, "SEARCH_VALUE");
            for (int i = 0; i < searchcol.length; ++i) {
                String colVal = searchval[i];
                colVal = DMIAMEncoder.encodeSQLForNonPatternContext(colVal);
                Criteria criteria = null;
                final Criteria baseCriteria = selectQuery.getCriteria();
                final String s = searchcol[i];
                switch (s) {
                    case "Resource.NAME": {
                        criteria = new Criteria(Column.getColumn("Resource", "NAME"), (Object)colVal, 12, false);
                        break;
                    }
                    case "Resource.DOMAIN_NETBIOS_NAME": {
                        criteria = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)colVal, 12, false);
                        break;
                    }
                    case "ManagedUser.DISPLAY_NAME": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 111L);
                        criteria = new Criteria(Column.getColumn("ManagedUser", "DISPLAY_NAME"), (Object)colVal, 12, false).or(new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false));
                        break;
                    }
                    case "ManagedUser.EMAIL_ADDRESS": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 106L);
                        criteria = new Criteria(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), (Object)colVal, 12, false).or(new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false));
                        break;
                    }
                    case "ManagedUser.FIRST_NAME": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 109L);
                        criteria = new Criteria(Column.getColumn("ManagedUser", "FIRST_NAME"), (Object)colVal, 12, false).or(new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false));
                        break;
                    }
                    case "ManagedUser.LAST_NAME": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 108L);
                        criteria = new Criteria(Column.getColumn("ManagedUser", "LAST_NAME"), (Object)colVal, 12, false).or(new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false));
                        break;
                    }
                    case "ManagedUser.MIDDLE_NAME": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 110L);
                        criteria = new Criteria(Column.getColumn("ManagedUser", "MIDDLE_NAME"), (Object)colVal, 12, false).or(new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false));
                        break;
                    }
                    case "ManagedUser.PHONE_NUMBER": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 114L);
                        criteria = new Criteria(Column.getColumn("ManagedUser", "PHONE_NUMBER"), (Object)colVal, 12, false).or(new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false));
                        break;
                    }
                    case "DEPARTMENT": {
                        final String tableAlias = this.checkAndAddTable(selectQuery, 128L);
                        criteria = new Criteria(Column.getColumn(tableAlias, "VALUE"), (Object)colVal, 12, false);
                        break;
                    }
                }
                if (criteria != null) {
                    selectQuery.setCriteria(baseCriteria.and(criteria));
                }
            }
        }
        ProfileAssociateHandler.getInstance().updateuserProfileSummary();
    }
    
    private void setSortCol(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final String sortColumn = (String)viewCtx.getStateOrURLStateParameter("_SB");
        final String sortOrderStr = (String)viewCtx.getStateOrURLStateParameter("_SO");
        final boolean sortOrder = sortOrderStr != null && sortOrderStr.equalsIgnoreCase("A");
        if (sortColumn != null) {
            final List<SortColumn> sortColumns = new ArrayList<SortColumn>();
            for (final SortColumn sortCol : sortColumns) {
                selectQuery.removeSortColumn(sortCol);
            }
            final String s = sortColumn;
            switch (s) {
                case "Resource.NAME": {
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("Resource", "NAME"), sortOrder));
                    break;
                }
                case "Resource.DOMAIN_NETBIOS_NAME": {
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), sortOrder));
                    break;
                }
                case "ManagedUser.DISPLAY_NAME": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 111L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "DISPLAY_NAME"), sortOrder));
                    break;
                }
                case "ManagedUser.EMAIL_ADDRESS": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 106L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "EMAIL_ADDRESS"), sortOrder));
                    break;
                }
                case "ManagedUser.FIRST_NAME": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 109L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "FIRST_NAME"), sortOrder));
                    break;
                }
                case "ManagedUser.LAST_NAME": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 108L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "LAST_NAME"), sortOrder));
                    break;
                }
                case "ManagedUser.MIDDLE_NAME": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 110L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "MIDDLE_NAME"), sortOrder));
                    break;
                }
                case "ManagedUser.PHONE_NUMBER": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 114L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn("ManagedUser", "PHONE_NUMBER"), sortOrder));
                    break;
                }
                case "DEPARTMENT": {
                    final String tableAlias = this.checkAndAddTable(selectQuery, 128L);
                    selectQuery.addSortColumn(new SortColumn(Column.getColumn(tableAlias, "VALUE"), sortOrder));
                    break;
                }
            }
        }
    }
    
    private Criteria getFilterCri(final int userfilter, final int userstatus, int domainTypeFilter, Criteria baseCri, final Long customerID) {
        final Criteria dirUserCri = new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 1);
        if (userfilter == 1) {
            baseCri = baseCri.and(new Criteria(Column.getColumn("Resource", "DOMAIN_NETBIOS_NAME"), (Object)"MDM", 0));
        }
        else if (userfilter == 2) {
            baseCri = baseCri.and(dirUserCri);
        }
        else if (userfilter == 3) {
            baseCri = baseCri.and(new Criteria(Column.getColumn("ResourceToProfileSummary", "MEMBER_COUNT"), (Object)0, 5));
        }
        if (userstatus >= 4 && userstatus <= 6) {
            int statusCriVal = 4;
            switch (userstatus) {
                case 4: {
                    statusCriVal = 4;
                    break;
                }
                case 5: {
                    statusCriVal = 3;
                    break;
                }
                case 6: {
                    statusCriVal = 5;
                    break;
                }
            }
            Criteria userFilterCri = new Criteria(Column.getColumn("DirObjRegIntVal", "VALUE"), (Object)statusCriVal, 0);
            if (userstatus == 6) {
                userFilterCri = userFilterCri.or(new Criteria(Column.getColumn("DirResRel", "OBJ_ID"), (Object)null, 0));
            }
            final Criteria crietria2 = userFilterCri.and(dirUserCri);
            baseCri = baseCri.and(crietria2);
        }
        final Column clientIDcol = Column.getColumn("DMDomain", "CLIENT_ID");
        final boolean isZDexplicit = DirectoryUtil.getInstance().isZDexplicit((long)customerID);
        if (MDMUserTableRetrieverAction.SUPPORTED_DOMAINS.contains(domainTypeFilter)) {
            if (domainTypeFilter == 201 && !isZDexplicit) {
                domainTypeFilter = -1;
            }
            baseCri = baseCri.and(new Criteria(clientIDcol, (Object)domainTypeFilter, 0));
        }
        else if (!isZDexplicit) {
            baseCri = baseCri.and(new Criteria(clientIDcol, (Object)null, 0).or(new Criteria(clientIDcol, (Object)201, 1)));
        }
        return baseCri;
    }
    
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        try {
            final Long customerID = MSPWebClientUtil.getCustomerID(request);
            final int userstatus = this.getRequestParamInInt(request, "status");
            final int userfilter = this.getRequestParamInInt(request, "userfilter");
            final int domainTypeFilter = this.getRequestParamInInt(request, "domainTypeFilter");
            final Criteria selectCri = selectQuery.getCriteria();
            Criteria baseCri = new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0)).and(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)null, 0).or(new Criteria(Column.getColumn("ManagedUser", "STATUS"), (Object)11, 1)));
            baseCri = ((selectCri != null) ? baseCri.and(selectCri) : baseCri);
            baseCri = this.getFilterCri(userfilter, userstatus, domainTypeFilter, baseCri, customerID);
            selectQuery.setCriteria(baseCri);
            this.setSearchCriteria(selectQuery, viewCtx);
            this.setSortCol(selectQuery, viewCtx);
            request.setAttribute("status", (Object)userstatus);
            request.setAttribute("userfilter", (Object)userfilter);
        }
        catch (final Exception ex) {
            SyMLogger.log("MDMLogger", Level.SEVERE, (String)null, (Throwable)ex);
        }
    }
    
    public void postModelFetch(final ViewContext viewCtx) {
        final HashMap transformData = new HashMap();
        final ArrayList<Long> dirObjIDs = new ArrayList<Long>();
        final DMWebClientCommonUtil dmWebClientCommonUtil = new DMWebClientCommonUtil();
        final ArrayList<Long> objIDs = (ArrayList<Long>)dmWebClientCommonUtil.getColumnValues(viewCtx, "OBJ_ID");
        for (final Long objID : objIDs) {
            if (objID != null) {
                dirObjIDs.add(objID);
            }
        }
        if (!dirObjIDs.isEmpty()) {
            final Long[] attrIds = { 106L, 114L, 108L, 109L, 110L, 111L, 116L };
            final SelectQuery attrQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("DirObjRegStrVal"));
            attrQuery.setCriteria(new Criteria(Column.getColumn("DirObjRegStrVal", "ATTR_ID"), (Object)attrIds, 8).and(new Criteria(Column.getColumn("DirObjRegStrVal", "OBJ_ID"), (Object)dirObjIDs.toArray(new Long[dirObjIDs.size()]), 8)));
            attrQuery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "VALUE"));
            attrQuery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "OBJ_ID"));
            attrQuery.addSelectColumn(Column.getColumn("DirObjRegStrVal", "ATTR_ID"));
            final JSONArray attrResult = MDMUtil.executeSelectQuery(attrQuery);
            final HashMap<Long, HashMap<Long, String>> hashMap = new HashMap<Long, HashMap<Long, String>>();
            for (int i = 0; i < attrResult.size(); ++i) {
                final JSONObject jsObj = (JSONObject)attrResult.get(i);
                final Long objID2 = (Long)jsObj.get((Object)"OBJ_ID");
                final String val = (String)jsObj.get((Object)"VALUE");
                final Long attrID = (Long)jsObj.get((Object)"ATTR_ID");
                HashMap<Long, String> objAttr = null;
                if (hashMap.containsKey(objID2)) {
                    objAttr = hashMap.get(objID2);
                }
                if (objAttr == null) {
                    objAttr = new HashMap<Long, String>();
                    hashMap.put(objID2, objAttr);
                }
                objAttr.put(attrID, val);
            }
            transformData.put("DIR_ATTR_VAL", hashMap);
            viewCtx.getRequest().setAttribute("TRANSFORMER_PRE_DATA", (Object)transformData);
        }
    }
    
    static {
        SUPPORTED_DOMAINS = new ArrayList<Integer>(Arrays.asList(301, 201, 101, 3, 2));
    }
}
