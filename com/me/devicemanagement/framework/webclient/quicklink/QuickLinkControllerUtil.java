package com.me.devicemanagement.framework.webclient.quicklink;

import org.json.JSONException;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.function.Consumer;
import com.adventnet.ds.query.UnionQuery;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import java.sql.SQLException;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.adventnet.i18n.I18N;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.UnionQueryImpl;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import com.adventnet.ds.query.Join;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.Set;
import org.json.JSONArray;
import java.util.HashSet;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.io.File;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.Collection;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import java.util.logging.Logger;

public class QuickLinkControllerUtil
{
    private static Logger out;
    private static JSONObject ondemandQuickLinkonfigurations;
    private static JSONObject frameworkConfigurations;
    private static JSONObject quickLinkConfigurations;
    private static boolean isDynamicQuickLinkEnabled;
    private static boolean isCompletedEventsQuickLinkMoveToLast;
    private static boolean isRoleHandlingEnabled;
    public static QuickLinkControllerUtil quickLinkControlUtil;
    
    public static synchronized QuickLinkControllerUtil getInstance() {
        if (QuickLinkControllerUtil.quickLinkControlUtil == null) {
            QuickLinkControllerUtil.quickLinkControlUtil = new QuickLinkControllerUtil();
        }
        return QuickLinkControllerUtil.quickLinkControlUtil;
    }
    
    public List<HashMap<String, Object>> getQuickLinkList(final Long pageNumber, final String type) throws QuickLinkException {
        List<HashMap<String, Object>> overAllQuickLinkList = new ArrayList<HashMap<String, Object>>();
        try {
            QuickLinkControllerUtil.out.log(Level.FINE, "  quickLink Configurations => " + QuickLinkControllerUtil.quickLinkConfigurations);
            final List<HashMap<String, Object>> ondemandQuickLinkList = this.getOnDemandQuickLinkList(pageNumber, type);
            if (ondemandQuickLinkList != null && !ondemandQuickLinkList.isEmpty()) {
                overAllQuickLinkList = ondemandQuickLinkList;
            }
            overAllQuickLinkList.addAll(this.getPreDefinedQuickLinkList(pageNumber, type));
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6000);
        }
        return this.getCountSpecifcQuicklink(overAllQuickLinkList, type);
    }
    
    private List<HashMap<String, Object>> getPreDefinedQuickLinkList(final Long pageNumber, final String type) throws QuickLinkException {
        List<HashMap<String, Object>> quickLinks = new ArrayList<HashMap<String, Object>>();
        try {
            final List roleIdList = ApiFactoryProvider.getAuthUtilAccessAPI().getRoles();
            final List applicableRoleIDList = this.getApplicableRoleIDDO(roleIdList);
            QuickLinkControllerUtil.out.log(Level.FINE, " getPreDefinedQuickLinkList  " + applicableRoleIDList);
            final Long loginID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
            final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
            final List closedQuickLinkList = this.getClosedQuickLinkList(pageNumber, customerID, loginID);
            QuickLinkControllerUtil.out.log(Level.FINE, " Closed Quick Link list for the corresponding user. List " + closedQuickLinkList);
            if (QuickLinkControllerUtil.isDynamicQuickLinkEnabled && QuickLinkControllerUtil.isCompletedEventsQuickLinkMoveToLast) {
                quickLinks = this.getQuickLinkListWithDynamicChange(pageNumber, type, closedQuickLinkList, applicableRoleIDList);
            }
            else {
                quickLinks = this.getApplicablePreDefinedQuickLinkList(pageNumber, type, closedQuickLinkList, applicableRoleIDList);
            }
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6005);
        }
        QuickLinkControllerUtil.out.log(Level.FINE, " Predefined quick link list. " + quickLinks);
        return quickLinks;
    }
    
    public List<HashMap<String, Object>> getOnDemandQuickLinkList(final Long pageNumber, final String type) throws QuickLinkException {
        final List<HashMap<String, Object>> instantQuickLinks = new ArrayList<HashMap<String, Object>>();
        try {
            if (QuickLinkControllerUtil.frameworkConfigurations != null && QuickLinkControllerUtil.frameworkConfigurations.has("quicklink_configurations")) {
                final JSONObject jsonObject = (JSONObject)QuickLinkControllerUtil.frameworkConfigurations.get("quicklink_configurations");
                if (jsonObject.has("ondemand_quicklink_configuration_filepath")) {
                    final String filepath = System.getProperty("server.home") + File.separator + String.valueOf(jsonObject.get("ondemand_quicklink_configuration_filepath")).trim();
                    QuickLinkControllerUtil.ondemandQuickLinkonfigurations = JsonUtils.loadJsonFile(new File(filepath));
                }
            }
            if (QuickLinkControllerUtil.ondemandQuickLinkonfigurations != null && QuickLinkControllerUtil.ondemandQuickLinkonfigurations.has("ondemand_quicklinks")) {
                final JSONArray jsonArray = QuickLinkControllerUtil.ondemandQuickLinkonfigurations.getJSONArray("ondemand_quicklinks");
                final Set<String> uniqueKeys = new HashSet<String>(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); ++i) {
                    final JSONObject jsonObj = jsonArray.getJSONObject(i);
                    final HashMap<String, Object> individualMessage = new HashMap<String, Object>();
                    final String pageNum = String.valueOf(jsonObj.get("pageid")).trim();
                    if (pageNumber != null && pageNum.equalsIgnoreCase(pageNumber.toString())) {
                        final String typeName = String.valueOf(jsonObj.get("type")).trim();
                        if (type != null && typeName.trim().length() > 0 && type.trim().length() > 0 && typeName.equalsIgnoreCase(type)) {
                            final long curTime = System.currentTimeMillis();
                            long expiryTime = -1L;
                            if (jsonObj.has("expiry_time")) {
                                expiryTime = Long.parseLong(String.valueOf(jsonObj.get("expiry_time")).trim());
                            }
                            final String displayName = (String)jsonObj.get("display_name");
                            if (expiryTime > curTime && !uniqueKeys.contains(displayName)) {
                                individualMessage.put("text", displayName);
                                individualMessage.put("url", jsonObj.get("url"));
                                instantQuickLinks.add(individualMessage);
                                uniqueKeys.add(displayName);
                            }
                        }
                    }
                }
            }
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6006);
        }
        return instantQuickLinks;
    }
    
    private List getApplicableRoleIDDO(final List roleIdList) throws DataAccessException {
        final List returnList = new ArrayList();
        final Object[] roles = roleIdList.toArray();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("AaaRole"));
        query.addSelectColumn(Column.getColumn("AaaRole", "NAME"));
        query.addSelectColumn(Column.getColumn("AaaRole", "ROLE_ID"));
        final Criteria roleCriteria = new Criteria(Column.getColumn("AaaRole", "NAME"), (Object)roles, 8);
        query.setCriteria(roleCriteria);
        final DataObject roleDO = SyMUtil.getPersistence().get(query);
        if (!roleDO.isEmpty()) {
            final Iterator iter = roleDO.getRows("AaaRole");
            while (iter.hasNext()) {
                final Row row = iter.next();
                final Long roleId = (Long)row.get("ROLE_ID");
                returnList.add(roleId);
            }
        }
        QuickLinkControllerUtil.out.log(Level.FINE, "Applicable roles : " + returnList);
        return returnList;
    }
    
    private List getClosedQuickLinkList(final Long pageNumber, final Long customerID, final Long userID) throws QuickLinkException {
        final List closedQuickLinkList = new ArrayList();
        try {
            final List userCloseList = this.getUserCloseList(pageNumber, userID);
            final List customerCloseList = this.getCustomerCloseList(pageNumber, customerID);
            final List globalCloseList = this.getGlobalCloseList(pageNumber);
            if (userCloseList != null && userCloseList.size() > 0) {
                closedQuickLinkList.addAll(userCloseList);
            }
            if (customerCloseList != null && customerCloseList.size() > 0) {
                closedQuickLinkList.addAll(customerCloseList);
            }
            if (globalCloseList != null && globalCloseList.size() > 0) {
                closedQuickLinkList.addAll(globalCloseList);
            }
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6004);
        }
        return closedQuickLinkList;
    }
    
    private List getUserCloseList(final Long pageNumber, final Long userId) throws DataAccessException {
        DataObject data = null;
        final List userCloseList = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("QuickLinkUserStatus"));
        query.addSelectColumn(Column.getColumn("QuickLinkUserStatus", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("QuickLinkUserStatus", "USER_ID"));
        query.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("DCQuickLink", "PAGE_ID"));
        query.addJoin(new Join("QuickLinkUserStatus", "DCQuickLink", new String[] { "ARTICLE_ID" }, new String[] { "ARTICLE_ID" }, 2));
        final Criteria userCriteria = new Criteria(Column.getColumn("QuickLinkUserStatus", "USER_ID"), (Object)userId, 0, false);
        final Criteria viewCriteria = new Criteria(Column.getColumn("DCQuickLink", "PAGE_ID"), (Object)pageNumber, 0, false);
        final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
        query.setCriteria(userCriteria.and(viewCriteria).and(productCodeCriteria));
        data = SyMUtil.getPersistence().get(query);
        final Iterator userClosedIter = data.getRows("QuickLinkUserStatus");
        while (userClosedIter.hasNext()) {
            final Row summaryROW = userClosedIter.next();
            final Long articleID = (Long)summaryROW.get("ARTICLE_ID");
            if (articleID != null) {
                userCloseList.add(articleID);
            }
        }
        return userCloseList;
    }
    
    private List getCustomerCloseList(final Long pageNumber, final Long customerID) throws DataAccessException {
        final List customerCloseList = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("QuickLinkCustomerStatus"));
        query.addSelectColumn(Column.getColumn("QuickLinkCustomerStatus", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("QuickLinkCustomerStatus", "CUSTOMER_ID"));
        query.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("DCQuickLink", "PAGE_ID"));
        query.addJoin(new Join("QuickLinkCustomerStatus", "DCQuickLink", new String[] { "ARTICLE_ID" }, new String[] { "ARTICLE_ID" }, 2));
        final Criteria userCriteria = new Criteria(Column.getColumn("QuickLinkCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0, false);
        final Criteria viewCriteria = new Criteria(Column.getColumn("DCQuickLink", "PAGE_ID"), (Object)pageNumber, 0, false);
        final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
        query.setCriteria(userCriteria.and(viewCriteria).and(productCodeCriteria));
        final DataObject data = SyMUtil.getPersistence().get(query);
        final Iterator userClosedIter = data.getRows("QuickLinkCustomerStatus");
        while (userClosedIter.hasNext()) {
            final Row summaryROW = userClosedIter.next();
            final Long articleID = (Long)summaryROW.get("ARTICLE_ID");
            if (articleID != null) {
                customerCloseList.add(articleID);
            }
        }
        QuickLinkControllerUtil.out.log(Level.FINE, " Customer specific closed quick link list :  " + customerCloseList);
        return customerCloseList;
    }
    
    private List getGlobalCloseList(final Long pageNumber) throws DataAccessException {
        DataObject data = null;
        final List globalCloseList = new ArrayList();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("QuickLinkGlobalStatus"));
        query.addSelectColumn(Column.getColumn("QuickLinkGlobalStatus", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("DCQuickLink", "PAGE_ID"));
        query.addJoin(new Join("QuickLinkGlobalStatus", "DCQuickLink", new String[] { "ARTICLE_ID" }, new String[] { "ARTICLE_ID" }, 2));
        final Criteria viewCriteria = new Criteria(Column.getColumn("DCQuickLink", "PAGE_ID"), (Object)pageNumber, 0, false);
        final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
        query.setCriteria(viewCriteria.and(productCodeCriteria));
        data = SyMUtil.getPersistence().get(query);
        final Iterator userClosedIter = data.getRows("QuickLinkGlobalStatus");
        while (userClosedIter.hasNext()) {
            final Row summaryROW = userClosedIter.next();
            final Long articleID = (Long)summaryROW.get("ARTICLE_ID");
            if (articleID != null) {
                globalCloseList.add(articleID);
            }
        }
        QuickLinkControllerUtil.out.log(Level.FINE, " Globally closed quick link list :   " + globalCloseList);
        return globalCloseList;
    }
    
    private List<HashMap<String, Object>> getQuickLinkListWithDynamicChange(final Long pageNumber, final String type, final List hideList, final List articleRoleList) throws QuickLinkException {
        final List<HashMap<String, Object>> quickLinks = new ArrayList<HashMap<String, Object>>();
        Connection con = null;
        DataSet ds = null;
        try {
            if (hideList != null) {
                SelectQuery leftQuery = this.getCommonSelectQueryForDynamicQuickLink();
                SelectQuery rightQuery = this.getCommonSelectQueryForDynamicQuickLink();
                final Column c = (Column)Column.createFunction("DUMMY_COL", new Object[] { 1 });
                c.setDataType("INTEGER");
                c.setColumnAlias("displayorder");
                leftQuery.addSelectColumn(c);
                final Column c2 = (Column)Column.createFunction("DUMMY_COL", new Object[] { 2 });
                c2.setDataType("INTEGER");
                c2.setColumnAlias("displayorder");
                rightQuery.addSelectColumn(c2);
                final Object hideArray = hideList.toArray();
                Criteria leftQueryCriteria = new Criteria(Column.getColumn("DCUIPage", "PAGE_ID"), (Object)pageNumber, 0);
                leftQueryCriteria = leftQueryCriteria.and(new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_ID"), hideArray, 9));
                leftQueryCriteria = leftQueryCriteria.and(new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)type, 0));
                Criteria rightQueryCriteria = new Criteria(Column.getColumn("DCUIPage", "PAGE_ID"), (Object)pageNumber, 0);
                rightQueryCriteria = rightQueryCriteria.and(new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_ID"), hideArray, 8));
                rightQueryCriteria = rightQueryCriteria.and(new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)type, 0));
                if (articleRoleList != null && QuickLinkControllerUtil.isRoleHandlingEnabled) {
                    final Object[] roleArray = articleRoleList.toArray();
                    leftQuery = this.getRoleHandlingQuery(leftQuery);
                    rightQuery = this.getRoleHandlingQuery(rightQuery);
                    leftQueryCriteria = leftQueryCriteria.and(new Criteria(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"), (Object)roleArray, 8));
                    rightQueryCriteria = rightQueryCriteria.and(new Criteria(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"), (Object)roleArray, 8));
                }
                final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
                leftQueryCriteria = leftQueryCriteria.and(productCodeCriteria);
                rightQueryCriteria = rightQueryCriteria.and(productCodeCriteria);
                leftQuery.setCriteria(leftQueryCriteria);
                rightQuery.setCriteria(rightQueryCriteria);
                final UnionQuery query = (UnionQuery)new UnionQueryImpl((Query)leftQuery, (Query)rightQuery, false);
                query.addSortColumn(new SortColumn(c, true));
                final SortColumn sortColumn = new SortColumn(Column.getColumn("DCQuickLink", "DISPLAY_ORDER"), true);
                query.addSortColumn(sortColumn);
                con = RelationalAPI.getInstance().getConnection();
                ds = RelationalAPI.getInstance().executeQuery((Query)query, con);
                final Set<String> uniqueKeys = new HashSet<String>();
                while (ds.next()) {
                    String dispName = (String)ds.getValue("DISPLAY_NAME");
                    if (!uniqueKeys.contains(dispName)) {
                        final HashMap<String, Object> individualMessage = new HashMap<String, Object>();
                        uniqueKeys.add(dispName);
                        dispName = I18N.getMsg(dispName, new Object[0]);
                        final String url = (String)ds.getValue("ARTICLE_URL");
                        final String resultUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(url, ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
                        individualMessage.put("text", dispName);
                        individualMessage.put("url", resultUrl);
                        quickLinks.add(individualMessage);
                    }
                }
            }
            return quickLinks;
        }
        catch (final Exception e) {
            throw new QuickLinkException(e, 6003);
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    private SelectQuery getRoleHandlingQuery(final SelectQuery query) {
        query.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ARTICLE_ID"));
        query.addSelectColumn(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"));
        query.addJoin(new Join("DCQuickLink", "ArticleToRoleMapping", new String[] { "ARTICLE_ID" }, new String[] { "ARTICLE_ID" }, 2));
        final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
        query.setCriteria(productCodeCriteria);
        return query;
    }
    
    private SelectQuery getCommonSelectQueryForDynamicQuickLink() {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DCUIPage"));
        sq.addSelectColumn(Column.getColumn("DCUIPage", "PAGE_ID"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_ID"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "DISPLAY_ORDER"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "DISPLAY_NAME"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "PAGE_ID"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_NAME"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_URL"));
        sq.addJoin(new Join("DCUIPage", "DCQuickLink", new String[] { "PAGE_ID" }, new String[] { "PAGE_ID" }, 1));
        return sq;
    }
    
    private List<HashMap<String, Object>> getApplicablePreDefinedQuickLinkList(final Long pageNumber, final String type, final List hideList, final List articleRoleList) throws QuickLinkException {
        final List<HashMap<String, Object>> quickLinks = new ArrayList<HashMap<String, Object>>();
        Connection con = null;
        DataSet ds = null;
        try {
            SelectQuery query = this.getCommonSelectQueryForDynamicQuickLink();
            Criteria crit = new Criteria(Column.getColumn("DCUIPage", "PAGE_ID"), (Object)pageNumber, 0);
            crit = crit.and(new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_TYPE"), (Object)type, 0));
            if (hideList != null) {
                final Object hideArray = hideList.toArray();
                crit = crit.and(new Criteria(Column.getColumn("DCQuickLink", "ARTICLE_ID"), hideArray, 9));
            }
            if (articleRoleList != null && QuickLinkControllerUtil.isRoleHandlingEnabled) {
                final Object[] roleArray = articleRoleList.toArray();
                query = this.getRoleHandlingQuery(query);
                crit = crit.and(new Criteria(Column.getColumn("ArticleToRoleMapping", "ROLE_ID"), (Object)roleArray, 8));
            }
            final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
            query.setCriteria(crit.and(productCodeCriteria));
            final SortColumn sortColumn = new SortColumn(Column.getColumn("DCQuickLink", "DISPLAY_ORDER"), true);
            query.addSortColumn(sortColumn);
            con = RelationalAPI.getInstance().getConnection();
            ds = RelationalAPI.getInstance().executeQuery((Query)query, con);
            final Set<String> uniqueKeys = new HashSet<String>();
            while (ds.next()) {
                String dispName = (String)ds.getValue("DISPLAY_NAME");
                if (!uniqueKeys.contains(dispName)) {
                    final HashMap<String, Object> individualMessage = new HashMap<String, Object>();
                    uniqueKeys.add(dispName);
                    dispName = I18N.getMsg(dispName, new Object[0]);
                    final String url = (String)ds.getValue("ARTICLE_URL");
                    final String resultUrl = UrlReplacementUtil.replaceUrlAndAppendTrackCode(url, ProductUrlLoader.getInstance().getValue("tracking-quicklinks"));
                    individualMessage.put("text", dispName);
                    individualMessage.put("url", resultUrl);
                    quickLinks.add(individualMessage);
                }
            }
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6002);
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ds != null) {
                try {
                    ds.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return quickLinks;
    }
    
    public void setQuickLinkstatusForAction(final String actionName) throws QuickLinkException {
        try {
            this.updateQuickLinkstatus(actionName, false);
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6001);
        }
    }
    
    public void resetQuickLinkstatusForAction(final String actionName) throws QuickLinkException {
        try {
            this.updateQuickLinkstatus(actionName, true);
        }
        catch (final Exception ex) {
            throw new QuickLinkException(ex, 6001);
        }
    }
    
    private void updateQuickLinkstatus(final String actionName, final boolean isDelete) throws Exception {
        QuickLinkControllerUtil.out.log(Level.FINE, " setQuickLinkstatus actionName " + actionName);
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("DCQuickLink"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "ARTICLE_ID"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "ACTION_NAME"));
        sq.addSelectColumn(Column.getColumn("DCQuickLink", "DISPLAY_SCOPE"));
        final Criteria productCodeCriteria = EMSProductUtil.constructProductCodeCriteria("DCQuickLink", "PRODUCT_CODE");
        sq.setCriteria(new Criteria(Column.getColumn("DCQuickLink", "ACTION_NAME"), (Object)actionName, 0, false).and(productCodeCriteria));
        final DataObject data = SyMUtil.getPersistence().get(sq);
        final Iterator quickLinkIter = data.getRows("DCQuickLink");
        while (quickLinkIter.hasNext()) {
            final Row row = quickLinkIter.next();
            final int displayScope = (int)row.get("DISPLAY_SCOPE");
            final Long articleID = (Long)row.get("ARTICLE_ID");
            if (articleID != null) {
                if (displayScope == 0) {
                    this.updateGlobalScopeQuickLinks(articleID, isDelete);
                }
                else if (displayScope == 1) {
                    final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                    this.updateUserScopeQuickLinks(articleID, userID, isDelete);
                }
                else {
                    if (displayScope != 2) {
                        continue;
                    }
                    final Long customerID = CustomerInfoUtil.getInstance().getCustomerId();
                    this.updateCustomerScopeQuickLinks(articleID, customerID, isDelete);
                }
            }
        }
    }
    
    private void updateGlobalScopeQuickLinks(final Long articleID, final boolean isDelete) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("QuickLinkGlobalStatus"));
        sq.addSelectColumn(Column.getColumn("QuickLinkGlobalStatus", "ARTICLE_ID"));
        sq.setCriteria(new Criteria(Column.getColumn("QuickLinkGlobalStatus", "ARTICLE_ID"), (Object)articleID, 0));
        final DataObject doObj = SyMUtil.getPersistence().get(sq);
        if (doObj.isEmpty() && !isDelete) {
            final Row row = new Row("QuickLinkGlobalStatus");
            row.set("ARTICLE_ID", (Object)articleID);
            doObj.addRow(row);
            SyMUtil.getPersistence().add(doObj);
        }
        else if (!doObj.isEmpty() && isDelete) {
            final Row row = doObj.getFirstRow("QuickLinkGlobalStatus");
            SyMUtil.getPersistence().delete(row);
        }
    }
    
    private void updateUserScopeQuickLinks(final Long articleID, final Long userID, final boolean isDelete) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("QuickLinkUserStatus"));
        sq.addSelectColumn(Column.getColumn("QuickLinkUserStatus", "ARTICLE_ID"));
        sq.addSelectColumn(Column.getColumn("QuickLinkUserStatus", "USER_ID"));
        Criteria criteria = new Criteria(Column.getColumn("QuickLinkUserStatus", "ARTICLE_ID"), (Object)articleID, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("QuickLinkUserStatus", "USER_ID"), (Object)userID, 0));
        sq.setCriteria(criteria);
        final DataObject doObj = SyMUtil.getPersistence().get(sq);
        if (doObj.isEmpty() && !isDelete) {
            final Row row = new Row("QuickLinkUserStatus");
            row.set("ARTICLE_ID", (Object)articleID);
            row.set("USER_ID", (Object)userID);
            doObj.addRow(row);
            SyMUtil.getPersistence().add(doObj);
        }
        else if (!doObj.isEmpty() && isDelete) {
            final Row row = doObj.getFirstRow("QuickLinkUserStatus");
            SyMUtil.getPersistence().delete(row);
        }
    }
    
    private void updateCustomerScopeQuickLinks(final Long articleID, final Long customerID, final boolean isDelete) throws DataAccessException {
        final SelectQuery sq = (SelectQuery)new SelectQueryImpl(Table.getTable("QuickLinkCustomerStatus"));
        sq.addSelectColumn(Column.getColumn("QuickLinkCustomerStatus", "ARTICLE_ID"));
        sq.addSelectColumn(Column.getColumn("QuickLinkCustomerStatus", "CUSTOMER_ID"));
        Criteria criteria = new Criteria(Column.getColumn("QuickLinkCustomerStatus", "ARTICLE_ID"), (Object)articleID, 0);
        criteria = criteria.and(new Criteria(Column.getColumn("QuickLinkCustomerStatus", "CUSTOMER_ID"), (Object)customerID, 0));
        sq.setCriteria(criteria);
        final DataObject doObj = SyMUtil.getPersistence().get(sq);
        if (doObj.isEmpty() && !isDelete) {
            final Row row = new Row("QuickLinkCustomerStatus");
            row.set("ARTICLE_ID", (Object)articleID);
            row.set("CUSTOMER_ID", (Object)customerID);
            doObj.addRow(row);
            SyMUtil.getPersistence().add(doObj);
        }
        else if (!doObj.isEmpty() && isDelete) {
            final Row row = doObj.getFirstRow("QuickLinkCustomerStatus");
            SyMUtil.getPersistence().delete(row);
        }
    }
    
    private List<HashMap<String, Object>> getCountSpecifcQuicklink(final List<HashMap<String, Object>> overAllQuickLinks, final String type) {
        try {
            String typeCount = type + "_count";
            typeCount = typeCount.toLowerCase().trim();
            int count = 10000;
            if (QuickLinkControllerUtil.quickLinkConfigurations != null && QuickLinkControllerUtil.quickLinkConfigurations.has(typeCount)) {
                count = Integer.parseInt(String.valueOf(QuickLinkControllerUtil.quickLinkConfigurations.get(typeCount)).trim());
            }
            if (overAllQuickLinks.size() > count) {
                final List<HashMap<String, Object>> reducedQuickLinks = new ArrayList<HashMap<String, Object>>();
                overAllQuickLinks.stream().limit(count).forEach(reducedQuickLinks::add);
                return reducedQuickLinks;
            }
            return overAllQuickLinks;
        }
        catch (final Exception ex) {
            QuickLinkControllerUtil.out.log(Level.WARNING, " Unbale to display cout specific records, so display all the records. Exception : ", ex);
            return overAllQuickLinks;
        }
    }
    
    public void setShowHideStatus(final Integer showHideStatus, final Long pageNumber, final Long userID) {
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DCQuickLinkDisplayStatus"));
            Criteria cri = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "PAGE_ID"), (Object)pageNumber, 0);
            final Criteria cri2 = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "USER_ID"), (Object)userID, 0);
            sq.addSelectColumn(new Column((String)null, "*"));
            cri = cri.and(cri2);
            sq.setCriteria(cri);
            DataObject dataObj = SyMUtil.getPersistence().get(sq);
            if (dataObj != null && !dataObj.isEmpty()) {
                final Row statusRow = dataObj.getRow("DCQuickLinkDisplayStatus");
                statusRow.set("VISUAL_STATE", (Object)showHideStatus);
                statusRow.set("USER_ID", (Object)userID);
                dataObj.updateRow(statusRow);
                SyMUtil.getPersistence().update(dataObj);
            }
            else {
                dataObj = SyMUtil.getPersistence().constructDataObject();
                final Row statusRow = new Row("DCQuickLinkDisplayStatus");
                statusRow.set("PAGE_ID", (Object)pageNumber);
                statusRow.set("VISUAL_STATE", (Object)showHideStatus);
                statusRow.set("USER_ID", (Object)userID);
                dataObj.addRow(statusRow);
                SyMUtil.getPersistence().add(dataObj);
            }
        }
        catch (final Exception e) {
            QuickLinkControllerUtil.out.log(Level.WARNING, "Exception in the method setShowHideStatus() method", e);
        }
    }
    
    public String getModuleNamefromPage(final Long pageNumber) {
        String moduleName = "";
        try {
            moduleName = (String)DBUtil.getValueFromDB("DCUIPage", "PAGE_ID", pageNumber, "MODULE_NAME");
        }
        catch (final Exception e) {
            QuickLinkControllerUtil.out.log(Level.WARNING, "Exception in the method  getShowHideStatus()", e);
        }
        return moduleName;
    }
    
    public Integer getShowHideStatus(final Long pageNumber, final Long userID) {
        Integer visualState = null;
        try {
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("DCQuickLinkDisplayStatus"));
            Criteria cri = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "PAGE_ID"), (Object)pageNumber, 0);
            final Criteria cri2 = new Criteria(Column.getColumn("DCQuickLinkDisplayStatus", "USER_ID"), (Object)userID, 0);
            sq.addSelectColumn(new Column((String)null, "*"));
            cri = cri.and(cri2);
            sq.setCriteria(cri);
            final DataObject dataObj = SyMUtil.getPersistence().get(sq);
            if (dataObj != null && !dataObj.isEmpty()) {
                final Row statusRow = dataObj.getRow("DCQuickLinkDisplayStatus");
                visualState = (Integer)statusRow.get("VISUAL_STATE");
            }
        }
        catch (final Exception e) {
            QuickLinkControllerUtil.out.log(Level.WARNING, "Exception in the method  getShowHideStatus()", e);
        }
        return visualState;
    }
    
    static {
        QuickLinkControllerUtil.out = Logger.getLogger(QuickLinkControllerUtil.class.getName());
        QuickLinkControllerUtil.ondemandQuickLinkonfigurations = null;
        QuickLinkControllerUtil.frameworkConfigurations = null;
        QuickLinkControllerUtil.quickLinkConfigurations = null;
        QuickLinkControllerUtil.isDynamicQuickLinkEnabled = false;
        QuickLinkControllerUtil.isCompletedEventsQuickLinkMoveToLast = true;
        QuickLinkControllerUtil.isRoleHandlingEnabled = false;
        QuickLinkControllerUtil.quickLinkControlUtil = null;
        try {
            QuickLinkControllerUtil.frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
            if (QuickLinkControllerUtil.frameworkConfigurations != null && QuickLinkControllerUtil.frameworkConfigurations.has("quicklink_configurations")) {
                QuickLinkControllerUtil.quickLinkConfigurations = (JSONObject)QuickLinkControllerUtil.frameworkConfigurations.get("quicklink_configurations");
                if (QuickLinkControllerUtil.quickLinkConfigurations != null) {
                    if (QuickLinkControllerUtil.quickLinkConfigurations.has("isdynamic_quickLink_enabled")) {
                        QuickLinkControllerUtil.isDynamicQuickLinkEnabled = Boolean.valueOf(String.valueOf(QuickLinkControllerUtil.quickLinkConfigurations.get("isdynamic_quickLink_enabled")).trim());
                    }
                    if (QuickLinkControllerUtil.quickLinkConfigurations.has("iscompleted_events_quicklink_moved_to_last")) {
                        QuickLinkControllerUtil.isCompletedEventsQuickLinkMoveToLast = Boolean.valueOf(String.valueOf(QuickLinkControllerUtil.quickLinkConfigurations.get("iscompleted_events_quicklink_moved_to_last")).trim());
                    }
                    if (QuickLinkControllerUtil.quickLinkConfigurations.has("isrole_handling_needed")) {
                        QuickLinkControllerUtil.isRoleHandlingEnabled = Boolean.valueOf(String.valueOf(QuickLinkControllerUtil.quickLinkConfigurations.get("isrole_handling_needed")).trim());
                    }
                    else {
                        QuickLinkControllerUtil.isRoleHandlingEnabled = QuickLinkControllerUtil.isDynamicQuickLinkEnabled;
                    }
                }
            }
        }
        catch (final JSONException e) {
            QuickLinkControllerUtil.out.log(Level.WARNING, "  Unable to load the quicklink configurations from framework settings file. Exception : ", (Throwable)e);
        }
    }
}
