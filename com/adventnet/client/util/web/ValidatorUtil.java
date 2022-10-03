package com.adventnet.client.util.web;

import org.apache.commons.validator.ValidatorAction;
import java.util.Map;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import java.text.MessageFormat;
import java.util.Locale;
import com.adventnet.client.view.web.ViewContext;
import org.apache.commons.validator.ValidatorResults;
import java.util.HashMap;
import org.apache.commons.validator.Validator;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.Row;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.ValidatorResources;

public class ValidatorUtil
{
    private static ValidatorResources resources;
    
    public static ValidatorResources getValidatorResources(final HttpServletRequest request) throws Exception {
        if (ValidatorUtil.resources == null) {
            final Table table1 = new Table("ValidationFiles");
            final SelectQuery sq = (SelectQuery)new SelectQueryImpl(table1);
            final Column c1 = new Column("ValidationFiles", "FILE_NO");
            final Column c2 = new Column("ValidationFiles", "FILE_NAME");
            final ArrayList colList = new ArrayList();
            colList.add(c1);
            colList.add(c2);
            sq.addSelectColumns((List)colList);
            final Persistence per = (Persistence)BeanUtil.lookup("Persistence");
            final DataObject d = per.get(sq);
            final Iterator it = d.getRows("ValidationFiles");
            String data = null;
            final ArrayList vfiles = new ArrayList();
            while (it.hasNext()) {
                final Row row = it.next();
                data = (String)row.get("FILE_NAME");
                final String validationpath = request.getSession().getServletContext().getRealPath(data);
                vfiles.add(validationpath);
            }
            ValidatorUtil.resources = new ValidatorResources((String[])vfiles.toArray(new String[0]));
        }
        return ValidatorUtil.resources;
    }
    
    public static boolean validate(final String formname, final HttpServletRequest request) throws Exception {
        final ValidatorResources resources = getValidatorResources(request);
        final Validator vdator = new Validator(resources, formname);
        final HashMap form = new HashMap();
        for (final String name : request.getParameterMap().keySet()) {
            final String value = request.getParameter(name);
            form.put(name, value);
        }
        vdator.setParameter("java.lang.Object", (Object)form);
        final ValidatorResults results = vdator.validate();
        return parseResults(formname, form, results, resources);
    }
    
    public static boolean validate(final ViewContext viewctx) throws Exception {
        final HttpServletRequest request = viewctx.getRequest();
        final ValidatorResources resources = getValidatorResources(request);
        final Validator vdator = new Validator(resources, viewctx.getUniqueId());
        final HashMap form = new HashMap();
        for (final String name : request.getParameterMap().keySet()) {
            final String value = request.getParameter(name);
            form.put(name, value);
        }
        vdator.setParameter("java.lang.Object", (Object)form);
        final ValidatorResults results = vdator.validate();
        return parseResults(viewctx.getUniqueId(), form, results, resources);
    }
    
    public static boolean parseResults(final String formName, final Object bean, final ValidatorResults results, final ValidatorResources resources) throws Exception {
        final StringBuilder errorMsg = new StringBuilder();
        boolean success = true;
        final Form form = resources.getForm(Locale.getDefault(), formName);
        errorMsg.append("\n\nValidating:");
        errorMsg.append(bean);
        for (final String propertyName : results.getPropertyNames()) {
            final Field field = form.getField(propertyName);
            final String prettyFieldName = field.getArg(0).getKey();
            final ValidatorResult result = results.getValidatorResult(propertyName);
            final Map actionMap = result.getActionMap();
            for (final String actName : actionMap.keySet()) {
                final ValidatorAction action = resources.getValidatorAction(actName);
                errorMsg.append("\n" + propertyName + "[" + actName + "] (" + (result.isValid(actName) ? "PASSED" : "FAILED") + ")");
                if (!result.isValid(actName)) {
                    success = false;
                    final String message = action.getMsg();
                    final Object[] args = { prettyFieldName };
                    errorMsg.append("\n     Error message will be: " + MessageFormat.format(message, args));
                }
            }
        }
        if (success) {
            errorMsg.append("\nFORM VALIDATION PASSED");
            return success;
        }
        errorMsg.append("\nFORM VALIDATION FAILED");
        throw new Exception(errorMsg.toString());
    }
    
    static {
        ValidatorUtil.resources = null;
    }
}
