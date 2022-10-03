package com.me.devicemanagement.framework.webclient.search;

import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.net.URLDecoder;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.me.devicemanagement.framework.server.search.AdvSearchUtil;
import java.net.URLEncoder;
import java.util.logging.Level;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DCTableRetrieverAction;

public class AdvSearchController extends DCTableRetrieverAction
{
    public static String className;
    public static Logger logger;
    private static Logger advSearchErrorLogger;
    
    @Override
    public void setCriteria(SelectQuery selectQuery, final ViewContext viewCtx) {
        AdvSearchController.logger.log(Level.FINE, "Advanced Search : setCriteria in AdvSearchController");
        final HttpServletRequest request = viewCtx.getRequest();
        String searchText = request.getParameter("searchText");
        final String criteriaId = request.getParameter("criteriaId");
        String view_name = "";
        String table_name = "";
        String column_name = "";
        try {
            searchText = URLEncoder.encode(searchText, "UTF-8");
            final HashMap viewDetailsMap = AdvSearchUtil.getInstance().getSearchViewDetails(criteriaId);
            view_name = viewCtx.getUniqueId();
            table_name = viewDetailsMap.get("table_name");
            column_name = viewDetailsMap.get("column_name");
            this.setCorrespondingCriteria(selectQuery, viewCtx, view_name);
            selectQuery = this.applySearchCriteria(selectQuery, table_name, column_name, searchText);
            AdvSearchController.logger.log(Level.FINE, "Advanced Search Query   :".concat(RelationalAPI.getInstance().getSelectSQL((Query)selectQuery)));
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchController : Exception occurred - setCriteria() :  ", ex);
        }
        selectQuery.setDistinct(true);
        super.setCriteria(selectQuery, viewCtx);
    }
    
    private SelectQuery applySearchCriteria(final SelectQuery selectQuery, final String table_name, final String column_name, String searchText) {
        try {
            searchText = URLDecoder.decode(searchText, "UTF-8");
            Criteria searchCri = new Criteria(Column.getColumn(table_name, column_name), (Object)("*" + searchText.trim() + "*"), 2, false);
            final Criteria cri = selectQuery.getCriteria();
            if (cri != null) {
                searchCri = searchCri.and(cri);
            }
            selectQuery.setDistinct(true);
            selectQuery.setCriteria(searchCri);
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchController : Exception occurred - applySearchCriteria() :  ", ex);
        }
        return selectQuery;
    }
    
    private SelectQuery setCorrespondingCriteria(final SelectQuery selectQuery, final ViewContext viewCtx, final String view_name) {
        try {
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isDC() || CustomerInfoUtil.isPMP() || CustomerInfoUtil.getInstance().isRAP()) {
                WebclientAPIFactoryProvider.getSearchCriteria("com.me.dc.webclient.search.DCSearchCriteriaImpl").setCorrespondingCriteria(viewCtx, selectQuery, view_name);
            }
            CustomerInfoUtil.getInstance();
            if (CustomerInfoUtil.isMDM()) {
                WebclientAPIFactoryProvider.getSearchCriteria("com.me.mdm.webclient.search.MDMSearchCriteriaImpl").setCorrespondingCriteria(viewCtx, selectQuery, view_name);
            }
        }
        catch (final Exception ex) {
            AdvSearchController.advSearchErrorLogger.log(Level.SEVERE, "AdvSearchController : Exception occurred - setCorrespondingCriteria() :  ", ex);
        }
        return selectQuery;
    }
    
    static {
        AdvSearchController.className = AdvSearchController.class.getName();
        AdvSearchController.logger = Logger.getLogger(AdvSearchController.className);
        AdvSearchController.advSearchErrorLogger = Logger.getLogger("AdvSearchError");
    }
}
