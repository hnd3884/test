package com.adventnet.client.components.util.web;

import com.adventnet.client.view.web.ViewController;
import java.util.Iterator;
import com.adventnet.client.components.personalize.web.PersonalizableView;
import java.util.List;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.client.util.StaticLists;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.util.web.WebClientUtil;
import java.util.logging.Level;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import org.apache.struts.action.ActionForward;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class PersonalizationUtil
{
    private static Logger logger;
    
    public static boolean isCustomizable(final HttpServletRequest request, final ViewContext context) throws DataAccessException {
        if (UserPersonalizationAPI.isPersonalizeEnabled(request)) {
            final DataObject dataObject = context.getModel().getViewConfiguration();
            final String custType = (String)dataObject.getFirstValue("ViewConfiguration", "CUSTOMIZETYPE");
            return !custType.equals("NO");
        }
        return false;
    }
    
    public static void isViewPresent(final String viewName) throws Exception {
        final Row r = new Row("ViewConfiguration");
        r.set("VIEWNAME", (Object)viewName);
        if (LookUpUtil.getPersistence().get("ViewConfiguration", r).containsTable("ViewConfiguration")) {
            throw new RuntimeException("Already a view exists with name " + viewName);
        }
    }
    
    public static String getFromReqOrFeatureParams(final ViewContext viewCtx, final String paramName) {
        String value = viewCtx.getRequest().getParameter(paramName);
        if (value == null) {
            value = viewCtx.getModel().getFeatureValue(paramName);
        }
        return value;
    }
    
    public static String genNameFromTitle(final String title) {
        final char[] arr = title.toCharArray();
        for (int i = 0; i < arr.length; ++i) {
            if ((arr[i] < 'A' || arr[i] > 'Z') && (arr[i] < 'a' || arr[i] > 'z') && (arr[i] < '0' || arr[i] > '9') && arr[i] < '_') {
                arr[i] = '_';
            }
        }
        return new String(arr);
    }
    
    @Deprecated
    public static ActionForward getForward(final ViewContext viewCtx, final String customizeViewName, final HttpServletRequest request) throws Exception {
        final String forwardView = viewCtx.getModel().getFeatureValue("FORWARDVIEW");
        if (forwardView != null) {
            return DynamicContentAreaAPI.replaceView(viewCtx, forwardView, "VIEWNAME=" + customizeViewName, false);
        }
        return DynamicContentAreaAPI.closeView(viewCtx, request);
    }
    
    public static boolean isPlaceHolderView(final DataObject dao) throws DataAccessException {
        final Long compName = (Long)dao.getFirstValue("ViewConfiguration", 3);
        Long tmpCompName = null;
        try {
            tmpCompName = WebViewAPI.getUIComponentNameNo("ACPLACEHOLDER");
        }
        catch (final Exception exp) {
            PersonalizationUtil.logger.log(Level.WARNING, exp.getMessage());
        }
        return compName.equals(tmpCompName);
    }
    
    public static DataObject getDataObjectIfPlaceHolderView(final DataObject dao, final String dummyViewName) throws Exception {
        if (isPlaceHolderView(dao)) {
            final long accountId = WebClientUtil.getAccountId();
            return createUpdateViewFromDummy(dummyViewName, (String)dao.getFirstValue("ViewConfiguration", 2), null, accountId);
        }
        return dao;
    }
    
    public static WritableDataObject getNewViewFromExisting(final String newViewTitle, final String origViewName, final boolean checkIfAlreadyExists, final long accountId) throws Exception {
        final String newViewName = genNameFromTitle(newViewTitle);
        if (checkIfAlreadyExists) {
            isViewPresent(newViewName);
        }
        final Row viewConfigRow = new Row("ViewConfiguration");
        viewConfigRow.set(2, (Object)origViewName);
        WritableDataObject viewConfig = (WritableDataObject)LookUpUtil.getPersistence().getForPersonalities(StaticLists.VIEWCONFIGURATIONPERS, StaticLists.VIEWCONFIGURATIONPERS, viewConfigRow);
        viewConfig = (WritableDataObject)PersistenceUtil.constructDO((DataObject)viewConfig);
        final Row viewRow = viewConfig.getRow("ViewConfiguration");
        viewRow.set(6, (Object)newViewTitle);
        viewRow.set("VIEWNAME", (Object)newViewName);
        if (accountId != -1L) {
            viewRow.set(13, (Object)new Long(accountId));
        }
        return viewConfig;
    }
    
    public static DataObject createUpdateViewFromDummy(final String dummyViewName, final String viewName, final String title, final long accountId) throws Exception {
        DataObject dao = WebViewAPI.getViewConfiguration((Object)dummyViewName);
        dao = PersistenceUtil.constructDO(dao);
        final DataObject existDO = WebViewAPI.getViewConfiguration((Object)viewName);
        if (existDO.containsTable("ViewConfiguration")) {
            dao = updatePlaceHolderView(existDO, dao);
        }
        else {
            final Row viewRow = dao.getFirstRow("ViewConfiguration");
            viewRow.set("VIEWNAME", (Object)viewName);
            viewRow.set(6, (Object)title);
            viewRow.set(13, (Object)new Long(accountId));
            dao.updateRow(viewRow);
            ((WritableDataObject)dao).clearOperations();
            dao = LookUpUtil.getPersistence().add(dao);
        }
        return dao;
    }
    
    public static DataObject updatePlaceHolderView(final DataObject existDO, DataObject newDO) throws Exception {
        final Row viewRow = newDO.getFirstRow("ViewConfiguration");
        final Row existRow = existDO.getFirstRow("ViewConfiguration");
        viewRow.set(2, existRow.get(2));
        viewRow.set(6, existRow.get(6));
        final List cols = viewRow.getColumns();
        for (int i = 0; i < cols.size(); ++i) {
            if (!cols.get(i).equals("VIEWNAME_NO") && !cols.get(i).equals("VIEWNAME")) {
                if (!cols.get(i).equals("TITLE")) {
                    final Object origvalue = viewRow.getOriginalValue((String)cols.get(i));
                    existRow.set((String)cols.get(i), origvalue);
                }
            }
        }
        existDO.updateRow(existRow);
        LookUpUtil.getPersistence().update(existDO);
        viewRow.set(2, (Object)"000---00");
        newDO.deleteRows("ViewConfiguration", viewRow);
        ((WritableDataObject)newDO).clearOperations();
        LookUpUtil.getPersistence().add(newDO);
        newDO = WebViewAPI.getViewConfiguration((Object)existRow.get(2));
        return newDO;
    }
    
    private static String getRepacedViewNameFromTemplate(final String parentViewName, final String templateViewName) {
        return parentViewName + "_" + templateViewName.substring("_DUMMY_".length());
    }
    
    public static void createChildViewsFromTemplate(final DataObject viewDataObj, final long accountId, final String childTableName, final int childColumnIndex) throws Exception {
        final String parentView = (String)viewDataObj.getFirstValue("ViewConfiguration", 2);
        final Iterator ite = viewDataObj.getRows(childTableName);
        while (ite.hasNext()) {
            final Row r = ite.next();
            String childViewName = (String)r.get(childColumnIndex);
            if (!childViewName.startsWith("_DUMMY_")) {
                continue;
            }
            final String dummyChildViewName = childViewName;
            childViewName = getRepacedViewNameFromTemplate(parentView, dummyChildViewName);
            final String title = childViewName.replace('_', ' ');
            final DataObject childConfig = (DataObject)getNewViewFromExisting(title, dummyChildViewName, true, accountId);
            final ViewController childVC = WebViewAPI.getViewController(childConfig);
            if (childVC instanceof PersonalizableView) {
                ((PersonalizableView)childVC).createViewFromTemplate(childConfig, accountId);
            }
            r.set(childColumnIndex, (Object)childViewName);
            viewDataObj.merge(childConfig);
        }
    }
    
    public static void deleteView(final String parentViewName, final String childToBeDeleted, final long accountId, final String tableName, final int colIndex) throws Exception {
        final WritableDataObject viewConfig = (WritableDataObject)UserPersonalizationAPI.getPersonalizedView((Object)parentViewName, accountId);
        final Iterator<Row> ite = viewConfig.getRows(tableName);
        while (ite.hasNext()) {
            final Row curRow = ite.next();
            if (childToBeDeleted.equals(curRow.get(colIndex))) {
                viewConfig.deleteRow(curRow);
            }
        }
        UserPersonalizationAPI.updatePersonalizedView(viewConfig, accountId);
    }
    
    static {
        PersonalizationUtil.logger = Logger.getLogger("com.adventnet.client.components.util.web.PersonalizationUtil");
    }
}
