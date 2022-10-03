package com.adventnet.client.util.web;

import java.util.concurrent.ConcurrentHashMap;
import com.zoho.conf.Configuration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.i18n.I18N;
import java.util.Iterator;
import com.adventnet.persistence.Row;
import java.util.List;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.ArrayList;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.io.File;
import com.adventnet.authorization.AuthorizationException;
import com.adventnet.client.action.web.MenuVariablesGenerator;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.zoho.authentication.AuthenticationUtil;
import javax.servlet.jsp.JspContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.Map;
import com.adventnet.client.AuthInterface;

public class WebClientUtil implements WebConstants
{
    private static boolean isNewAuthPropertySet;
    @Deprecated
    private static AuthInterface authImpl;
    private static Map<String, ServletContext> contextMap;
    
    public static String getRequiredParameter(final String parameterName, final HttpServletRequest request) {
        final String parameter = request.getParameter(parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException(parameterName + " is not present in the request parameter list");
        }
        return parameter;
    }
    
    public static String getXSSSafeParameter(final String parameterName, final HttpServletRequest request) {
        final String parameter = request.getParameter(parameterName);
        if (parameter != null) {
            return HTMLUtil.encode(parameter);
        }
        return parameter;
    }
    
    public static Object getRequiredAttribute(final String attributeName, final HttpServletRequest request) {
        final Object attribute = request.getAttribute(attributeName);
        if (attribute == null) {
            throw new IllegalArgumentException(attributeName + " is not present in the request attribute list");
        }
        return attribute;
    }
    
    public static Object getRequiredAttribute(final String attributeName, final JspContext ctx, final int scope) {
        final Object attribute = ctx.getAttribute(attributeName, scope);
        if (attribute == null) {
            throw new IllegalArgumentException(attributeName + " is not present in the request attribute list");
        }
        return attribute;
    }
    
    public static ServletContext getServletContext(final String contextName) {
        return WebClientUtil.contextMap.get(contextName);
    }
    
    public static void setServletContext(final String contextName, final ServletContext newServletContext) {
        WebClientUtil.contextMap.put(contextName, newServletContext);
    }
    
    public static boolean isNewAuthPropertySet() {
        return WebClientUtil.isNewAuthPropertySet;
    }
    
    @Deprecated
    public static boolean isUserCredentialSet() {
        if (WebClientUtil.isNewAuthPropertySet) {
            return AuthenticationUtil.isUserAuthenticated();
        }
        return WebClientUtil.authImpl.getAccountID() != null;
    }
    
    @Deprecated
    public static long getAccountId() {
        if (WebClientUtil.isNewAuthPropertySet) {
            return AuthenticationUtil.getAccountID();
        }
        return WebClientUtil.authImpl.getAccountID();
    }
    
    @Deprecated
    private static boolean checkForRole(final String roleName) {
        if (WebClientUtil.isNewAuthPropertySet) {
            return AuthenticationUtil.isUserExists(roleName);
        }
        return WebClientUtil.authImpl.userExists(roleName);
    }
    
    public static boolean isMenuAuthorized(final DataObject menuObj) {
        try {
            final String roleName = (String)menuObj.getFirstValue("Menu", "ROLENAME");
            return roleName == null || checkForRole(roleName);
        }
        catch (final DataAccessException ex) {
            throw new RuntimeException((Throwable)ex);
        }
    }
    
    public static boolean isMenuItemAuthorized(final String menuItemId) {
        try {
            return MenuVariablesGenerator.getCompleteMenuItemData(menuItemId) != null;
        }
        catch (final DataAccessException ex) {
            throw new RuntimeException((Throwable)ex);
        }
        catch (final AuthorizationException ae) {
            ae.printStackTrace();
            return false;
        }
    }
    
    public static boolean isMenuItemAuthorized(final DataObject menuItemObj) {
        try {
            final String roleName = (String)menuItemObj.getFirstValue("MenuItem", "ROLENAME");
            return roleName == null || checkForRole(roleName);
        }
        catch (final DataAccessException ex) {
            throw new RuntimeException((Throwable)ex);
        }
    }
    
    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }
    
    public static Object createInstance(final String className) {
        if (className == null) {
            return null;
        }
        try {
            return loadClass(className).newInstance();
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in creating an instance of the class: " + className, e);
        }
    }
    
    public static String getRequestedPathWithExtension(final HttpServletRequest request) {
        String pathName = null;
        pathName = (String)request.getAttribute("javax.servlet.include.path_info");
        if (pathName == null) {
            pathName = request.getPathInfo();
        }
        if (pathName != null && pathName.length() > 0) {
            return pathName;
        }
        pathName = (String)request.getAttribute("javax.servlet.include.servlet_path");
        if (pathName == null) {
            pathName = request.getServletPath();
        }
        return pathName;
    }
    
    public static String getRequestedPathName(final HttpServletRequest request) {
        String pathName = getRequestedPathWithExtension(request);
        final int slash = pathName.lastIndexOf(47);
        final int period = pathName.lastIndexOf(46);
        pathName = pathName.substring(slash + 1, period);
        return pathName;
    }
    
    public static void validatePath(final String path) {
        if (path != null && path.contains(".." + File.separator)) {
            throw new RuntimeException("The path [" + path + "] may cause Path Traversal or Backtracking Attack.");
        }
    }
    
    public static String getEscapedStringForForm(final String text) {
        if (text == null) {
            return null;
        }
        final StringBuilder charBuffer = new StringBuilder();
        for (int length = 0; length < text.length(); ++length) {
            final char ch = text.charAt(length);
            if (ch == '\r' || ch == '\n' || ch == '\"' || ch == '\'' || ch == '/') {
                charBuffer.append("&#");
                charBuffer.append((int)ch);
            }
            else {
                charBuffer.append(ch);
            }
        }
        return charBuffer.toString();
    }
    
    @Deprecated
    public static String getDescriptionForColumn(final String tableName, final String columnName) {
        TableDefinition tableDef = null;
        ColumnDefinition columnDef = null;
        String description = null;
        try {
            tableDef = MetaDataUtil.getTableDefinitionByName(tableName);
            columnDef = tableDef.getColumnDefinitionByName(columnName);
            description = columnDef.getDescription();
        }
        catch (final MetaDataException mde) {
            throw new RuntimeException((Throwable)mde);
        }
        if (description == null) {
            return "";
        }
        return description;
    }
    
    @Deprecated
    public static boolean isADEnabled() throws Exception {
        String eblStr = (String)StaticCache.getFromCache("AD_ENABLED");
        if (eblStr == null) {
            final ArrayList<String> list = new ArrayList<String>();
            list.add("AaaPamModule");
            list.add("AaaPamConf");
            list.add("AaaService");
            final Criteria criteria = new Criteria(new Column("AaaService", "NAME"), (Object)"System", 0);
            final DataObject daob = LookUpUtil.getPersistence().get((List)list, criteria);
            final Iterator iter = daob.getRows("AaaPamModule");
            while (iter.hasNext()) {
                final Row reqRow = iter.next();
                if (reqRow.get("NAME").equals("Authenticator")) {
                    eblStr = "AAA";
                    break;
                }
                if (reqRow.get("NAME").equals("ADAuthenticator")) {
                    eblStr = "AD";
                    break;
                }
            }
            StaticCache.addToCache("AD_ENABLED", eblStr, list);
        }
        return "AD".equals(eblStr);
    }
    
    @Deprecated
    public static void setAuthType(final String authType) throws DataAccessException {
        final ArrayList<String> list = new ArrayList<String>();
        list.add("AaaPamModule");
        list.add("AaaPamConf");
        list.add("AaaService");
        final Criteria criteria = new Criteria(new Column("AaaService", "NAME"), (Object)"System", 0);
        final DataObject daob = LookUpUtil.getPersistence().get((List)list, criteria);
        final ArrayList<String> list2 = new ArrayList<String>();
        list2.add("AaaPamModule");
        final Criteria criteria2 = new Criteria(new Column("AaaPamModule", "NAME"), (Object)"ADAuthenticator", 0);
        final DataObject daob2 = LookUpUtil.getPersistence().get((List)list2, criteria2);
        final ArrayList<String> list3 = new ArrayList<String>();
        list3.add("AaaPamModule");
        final Criteria criteria3 = new Criteria(new Column("AaaPamModule", "NAME"), (Object)"Authenticator", 0);
        final DataObject daob3 = LookUpUtil.getPersistence().get((List)list3, criteria3);
        Long id = new Long(0L);
        Row reqRow = null;
        String aaSelected = "";
        String adSelected = "";
        Iterator iter = daob.getRows("AaaPamModule");
        while (iter.hasNext()) {
            reqRow = iter.next();
            if (reqRow.get("NAME").equals("Authenticator")) {
                id = (Long)reqRow.get("PAMMODULE_ID");
                aaSelected = "checked";
                break;
            }
            if (reqRow.get("NAME").equals("ADAuthenticator")) {
                id = (Long)reqRow.get("PAMMODULE_ID");
                adSelected = "checked";
                break;
            }
        }
        iter = daob.getRows("AaaPamConf");
        while (iter.hasNext()) {
            reqRow = iter.next();
            if (((Long)reqRow.get("PAMMODULE_ID")).intValue() == id.intValue()) {
                break;
            }
        }
        if (authType.equals("AD") && !adSelected.equals("checked")) {
            daob.deleteRow(reqRow);
            final Row newRow = new Row("AaaPamConf");
            newRow.set("SERVICE_ID", (Object)new Long(1L));
            newRow.set("PAMMODULE_ID", daob2.getFirstValue("AaaPamModule", "PAMMODULE_ID"));
            newRow.set("EXECORDER", (Object)new Integer(1));
            newRow.set("CONTROL_FLAG", (Object)"REQUIRED");
            daob.addRow(newRow);
            LookUpUtil.getPersistence().update(daob);
        }
        if (authType.equals("AAA") && !aaSelected.equals("checked")) {
            daob.deleteRow(reqRow);
            final Row newRow = new Row("AaaPamConf");
            newRow.set("SERVICE_ID", (Object)new Long(1L));
            newRow.set("PAMMODULE_ID", daob3.getFirstValue("AaaPamModule", "PAMMODULE_ID"));
            newRow.set("EXECORDER", (Object)new Integer(1));
            newRow.set("CONTROL_FLAG", (Object)"REQUIRED");
            daob.addRow(newRow);
            LookUpUtil.getPersistence().update(daob);
        }
    }
    
    public static void setStatusMessage(final HttpServletRequest request, final String message, final boolean isSuccess) {
        setStatusMessage(request, message, isSuccess, true);
    }
    
    public static void setStatusMessage(final HttpServletRequest request, String message, final boolean isSuccess, final boolean escapeStr) {
        if (message != null) {
            try {
                message = I18N.getMsg(message, new Object[0]);
            }
            catch (final Exception exp) {
                exp.printStackTrace();
            }
            request.setAttribute("STATUS_MESSAGE", (Object)(escapeStr ? JSUtil.getEscapedString(message) : message));
        }
        request.setAttribute("RESPONSE_STATUS", (Object)(isSuccess ? Boolean.TRUE : Boolean.FALSE));
    }
    
    public static boolean isRestful(final HttpServletRequest request) {
        if (request == null || request.getSession() == null) {
            return false;
        }
        final String rest = request.getSession().getServletContext().getInitParameter("rest");
        return rest != null && rest.equals("true");
    }
    
    public static boolean isInSasMode(final HttpServletRequest request) {
        if (request == null || request.getSession() == null) {
            return false;
        }
        final String sasmode = request.getSession().getServletContext().getInitParameter("sasmode");
        return sasmode != null && sasmode.equals("true");
    }
    
    public static boolean isRequiredUrl(final ViewContext viewCtx) {
        final String viewNameWithUrlPattern = getRequestedPathWithExtension(viewCtx.getRequest());
        final Pattern pattern = Pattern.compile("\\.cc|\\.ve|\\.ma|\\.jsp|\\.do");
        final Matcher matcher = pattern.matcher(viewNameWithUrlPattern);
        return matcher.find();
    }
    
    @Deprecated
    public static void setAuthImpl(final AuthInterface obj) {
        WebClientUtil.authImpl = obj;
    }
    
    @Deprecated
    public static AuthInterface getAuthImpl() {
        return WebClientUtil.authImpl;
    }
    
    static {
        WebClientUtil.isNewAuthPropertySet = false;
        final String propValue = Configuration.getString("auth.interface");
        WebClientUtil.isNewAuthPropertySet = (propValue != null && !propValue.isEmpty());
        WebClientUtil.authImpl = null;
        WebClientUtil.contextMap = new ConcurrentHashMap<String, ServletContext>();
    }
}
