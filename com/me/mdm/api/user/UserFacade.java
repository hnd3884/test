package com.me.mdm.api.user;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.DataAccessException;
import java.util.logging.Level;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.APIUtil;
import java.util.HashSet;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.SelectQuery;
import java.util.Collection;
import java.util.logging.Logger;

public class UserFacade
{
    protected static Logger logger;
    
    public SelectQuery getUserValidationQuery(final Collection<Long> userIDs, final Long customerID) {
        final SelectQuery selectQuery = (SelectQuery)new SelectQueryImpl(Table.getTable("Resource"));
        selectQuery.setCriteria(new Criteria(Column.getColumn("Resource", "CUSTOMER_ID"), (Object)customerID, 0).and(new Criteria(Column.getColumn("Resource", "RESOURCE_TYPE"), (Object)2, 0).and(new Criteria(Column.getColumn("Resource", "RESOURCE_ID"), (Object)userIDs.toArray(new Long[userIDs.size()]), 8))));
        selectQuery.addSelectColumn(Column.getColumn("Resource", "RESOURCE_ID"));
        return selectQuery;
    }
    
    public void validateIfUsersExists(final Collection<Long> userIDs, final Long customerID) throws APIHTTPException {
        try {
            final DataObject dataObject = DataAccess.get(this.getUserValidationQuery(userIDs, customerID));
            final Iterator<Row> rows = dataObject.getRows("Resource");
            final ArrayList<Long> correctUsers = new ArrayList<Long>();
            while (rows != null && rows.hasNext()) {
                correctUsers.add(Long.valueOf(String.valueOf(rows.next().get("RESOURCE_ID"))));
            }
            final HashSet<Long> users = new HashSet<Long>(userIDs);
            users.removeAll(correctUsers);
            if (users.size() != 0) {
                throw new APIHTTPException("COM0008", new Object[] { APIUtil.getCommaSeperatedString(users) });
            }
        }
        catch (final DataAccessException ex) {
            UserFacade.logger.log(Level.SEVERE, "exception in validateIfUsersExists", (Throwable)ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        UserFacade.logger = Logger.getLogger("MDMApiLogger");
    }
}
