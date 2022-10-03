package com.me.mdm.webclient.search;

import com.me.devicemanagement.framework.server.util.DMIAMEncoder;
import com.adventnet.sym.webclient.mdm.reports.AppByDeviceViewController;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Level;
import java.net.URLDecoder;
import com.me.devicemanagement.framework.server.search.AdvSearchUtil;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DMSqlViewRetrieverAction;

public class AdvSearchSqlController extends DMSqlViewRetrieverAction
{
    public static String className;
    public static Logger logger;
    private static final String APP_SEARCH_VIEW = "ApplicationByDeviceSearch";
    
    public String getSQLString(final ViewContext viewCtx) throws Exception {
        final HttpServletRequest request = viewCtx.getRequest();
        String searchText = request.getParameter("searchText");
        final String criteriaId = request.getParameter("criteriaId");
        String view_name = "";
        String table_name = "";
        String column_name = "";
        final HashMap viewDetailsMap = AdvSearchUtil.getInstance().getSearchViewDetails(criteriaId);
        String sql = super.getSQLString(viewCtx);
        view_name = viewCtx.getUniqueId();
        table_name = viewDetailsMap.get("table_name");
        column_name = viewDetailsMap.get("column_name");
        searchText = URLDecoder.decode(URLDecoder.decode(searchText, "UTF-8"), "UTF-8");
        sql = this.applySearchCriteria(sql, view_name, table_name, column_name, searchText);
        AdvSearchSqlController.logger.log(Level.INFO, "Query after applying filter criteria  : {0}", sql);
        return sql;
    }
    
    public String getVariableValue(final ViewContext viewCtx, final String variableName) {
        String variableValue = "";
        final String viewName = viewCtx.getUniqueId();
        if (viewName.equalsIgnoreCase("ApplicationByDeviceSearch")) {
            final AppByDeviceViewController app = new AppByDeviceViewController();
            variableValue = app.getVariableValue(viewCtx, variableName);
            if (variableValue == null || variableValue.equals("")) {
                variableValue = "AND (1 = 1)";
            }
        }
        return variableValue;
    }
    
    private String applySearchCriteria(String sql, final String view_name, final String table_name, final String column_name, final String searchText) {
        try {
            final StringBuilder searchQueryStrBuff = new StringBuilder(sql);
            String value = "";
            if ("ApplicationByDeviceSearch".equals(view_name) && searchText != null && !searchText.trim().equals("")) {
                value = "  " + table_name + "." + column_name + " LIKE '" + "%" + DMIAMEncoder.encodeSQLForNonPatternContext(searchText) + "%" + "'";
                sql = searchQueryStrBuff.insert(sql.length(), " AND " + value).toString();
            }
        }
        catch (final Exception ex) {
            AdvSearchSqlController.logger.log(Level.WARNING, "Exception in applySearchCriteria", ex);
        }
        return sql;
    }
    
    static {
        AdvSearchSqlController.className = AdvSearchSqlController.class.getName();
        AdvSearchSqlController.logger = Logger.getLogger(AdvSearchSqlController.className);
    }
}
