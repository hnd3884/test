package com.adventnet.client.components.table.web;

import com.adventnet.ds.query.Table;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.ArrayList;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;

public class DCRestrictTaskRetriverAction extends DCTableRetrieverAction
{
    @Override
    public void setCriteria(final SelectQuery selectQuery, final ViewContext viewCtx) {
        final HttpServletRequest request = viewCtx.getRequest();
        final ArrayList tableList = (ArrayList)selectQuery.getTableList();
        final ArrayList tableNameList = this.fetchNameFromList(tableList, "Table");
        final ArrayList columnList = (ArrayList)selectQuery.getSelectColumns();
        final ArrayList columnNameList = this.fetchNameFromList(columnList, "Column");
        Long loginId = null;
        Long userId = null;
        try {
            loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            userId = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
        }
        catch (final Exception e) {
            Logger.getLogger(DCRestrictTaskRetriverAction.class.getName()).log(Level.SEVERE, "Exception occured in getting AuthUtilAPI", e);
        }
        Criteria techIdCriteria = null;
        if (!request.isUserInRole("Common_Write") && request.isUserInRole("RESTRICT_USER_TASKS")) {
            if (userId != null && tableNameList.contains("TaskToUserRel")) {
                techIdCriteria = new Criteria(Column.getColumn("TaskToUserRel", "USER_ID"), (Object)userId, 0);
            }
            else if (loginId != null && tableNameList.contains("AaaLogin")) {
                techIdCriteria = new Criteria(new Column("AaaLogin", "LOGIN_ID"), (Object)loginId, 0);
            }
            else if (userId != null && tableNameList.contains("AaaUser")) {
                techIdCriteria = new Criteria(new Column("AaaUser", "USER_ID"), (Object)userId, 0);
            }
        }
        if (techIdCriteria != null) {
            Criteria criteria = selectQuery.getCriteria();
            if (criteria != null) {
                criteria = criteria.and(techIdCriteria);
            }
            else {
                criteria = techIdCriteria;
            }
            selectQuery.setCriteria(criteria);
        }
        super.setCriteria(selectQuery, viewCtx);
    }
    
    public ArrayList fetchNameFromList(final ArrayList list, final String nameOf) {
        final ArrayList name = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            if (nameOf.equalsIgnoreCase("Table")) {
                final Table table = list.get(i);
                name.add(table.getTableName());
            }
            else if (nameOf.equals("Column")) {
                final Column column = list.get(i);
                name.add(column.getColumnName());
            }
        }
        return name;
    }
}
