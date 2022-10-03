package com.zoho.mickeyclient.action;

import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import javax.servlet.ServletException;
import java.io.IOException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public abstract class Action
{
    private ActionContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;
    
    public abstract void execute(final ActionContext p0, final HttpServletRequest p1, final HttpServletResponse p2) throws Exception;
    
    protected final void set(final ActionContext context, final HttpServletRequest request, final HttpServletResponse response) {
        this.context = context;
        this.request = request;
        this.response = response;
    }
    
    protected final void transitTo(final String name, final String queryString) throws DataAccessException, IOException, ServletException {
        if (name == null) {
            throw new MenuActionException(ActionErrors.NULL_FORWARDNAME, new Object[0]);
        }
        final DataObject menuDO = MenuVariablesGenerator.getCompleteMenuItemData(this.context.getMenuItemName());
        this.checkIfForwardExists(menuDO, name);
        final Row row = this.getForwardRow(menuDO, name);
        final String path = this.getCompletePath(row, queryString);
        if (row.get("REDIRECT") != null && (boolean)row.get("REDIRECT")) {
            HttpUtil.redirect(path, this.response);
        }
        else {
            HttpUtil.forward(path, this.request, this.response);
        }
    }
    
    private Row getForwardRow(final DataObject menuDO, final String name) throws DataAccessException {
        return menuDO.getRow("Forward", new Criteria(Column.getColumn("Forward", "NAME"), (Object)name, 0));
    }
    
    private String getConfiguredPath(final Row row) throws DataAccessException {
        if (row.get("PATH") == null) {
            throw new MenuActionException(ActionErrors.FORWARD_PATH_NULL, new Object[0]);
        }
        return (String)row.get("PATH");
    }
    
    private String getCompletePath(final Row row, final String queryString) throws DataAccessException {
        if (queryString == null) {
            return this.getConfiguredPath(row);
        }
        return this.getConfiguredPath(row) + queryString;
    }
    
    private void checkIfForwardExists(final DataObject menuDO, final String name) throws DataAccessException {
        if (!menuDO.containsTable("Forward")) {
            throw new MenuActionException(ActionErrors.FORWARD_MISSING, new Object[] { name });
        }
    }
}
