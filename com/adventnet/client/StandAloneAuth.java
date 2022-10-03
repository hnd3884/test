package com.adventnet.client;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.GroupByClause;
import java.util.Collection;
import java.util.Arrays;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.ArrayList;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.authentication.Credential;
import com.adventnet.authentication.util.AuthUtil;

public class StandAloneAuth implements AuthInterface
{
    @Override
    public Long getAccountID() {
        final Credential cred = AuthUtil.getUserCredential();
        if (cred == null) {
            return -1L;
        }
        return cred.getAccountId();
    }
    
    @Override
    public String getLoginName() {
        final Credential cred = AuthUtil.getUserCredential();
        return cred.getLoginName();
    }
    
    @Override
    public Long getUserID() {
        final Credential cred = AuthUtil.getUserCredential();
        return cred.getUserId();
    }
    
    @Override
    public boolean userExists(final String roleName) {
        final Credential cred = AuthUtil.getUserCredential();
        return cred.getRoles().contains(roleName);
    }
    
    @Override
    public Object encrypt(final Object paramName, final Object paramValue, final HttpServletRequest request) {
        return paramValue;
    }
    
    @Override
    public Object encrypt(final Object value) {
        return null;
    }
    
    @Override
    public String getListViewTotalHtmlString(final ViewContext vc) {
        return "";
    }
    
    @Override
    public List<Long> getAccountIDs(final List<String> roles) throws Exception {
        final List<Long> accountIds = new ArrayList<Long>();
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table("AaaAccount"));
        final Column column = new Column("AaaAccount", "ACCOUNT_ID");
        query.addSelectColumn(column);
        query.addJoin(new Join("AaaAccount", "AaaAuthorizedRole", new String[] { "ACCOUNT_ID" }, new String[] { "ACCOUNT_ID" }, 2));
        query.addJoin(new Join("AaaAuthorizedRole", "AaaRole", new String[] { "ROLE_ID" }, new String[] { "ROLE_ID" }, 2));
        query.setCriteria(new Criteria(new Column("AaaRole", "NAME"), (Object)roles.toArray(new String[roles.size()]), 8));
        final List<Column> groupBy = new ArrayList<Column>(Arrays.asList(column));
        query.setGroupByClause(new GroupByClause((List)groupBy, new Criteria(column.count(), (Object)roles.size(), 0)));
        final DataObject dobj = LookUpUtil.getPersistence().get(query);
        final Iterator<Long> itr = dobj.get("AaaAccount", "ACCOUNT_ID");
        while (itr.hasNext()) {
            accountIds.add(itr.next());
        }
        return accountIds;
    }
}
