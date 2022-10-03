package com.adventnet.client.components.form.web;

import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.internal.UniqueValueHolder;
import com.adventnet.persistence.DataObject;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import com.adventnet.client.util.web.JSUtil;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.JavaScriptConstants;
import com.adventnet.client.util.web.WebConstants;

public class FormAPI implements WebConstants, JavaScriptConstants
{
    private static final String FILLFORMPREFIX = "<script>updateFormWithData(";
    private static final String FILLFORMSUFFIX = "});</script>";
    
    public static String getAjaxFormScript(final ViewContext viewCtx) throws Exception {
        final Iterator ite = viewCtx.getModel().getViewConfiguration().getRows("ACAjaxForm");
        final StringBuffer strBuilder = new StringBuffer("<script>");
        while (ite.hasNext()) {
            final Row r = ite.next();
            final List colNames = r.getColumns();
            strBuilder.append("AjaxAPI.setAjaxAttributes({");
            for (int i = 0; i < colNames.size(); ++i) {
                if (r.get(i + 1) != null) {
                    strBuilder.append(colNames.get(i)).append(":\"").append(IAMEncoder.encodeJavaScript(String.valueOf(r.get(i + 1)))).append("\",");
                }
            }
            if (r.get("ACTION") == null) {
                strBuilder.append("ACTION:\"" + viewCtx.getUniqueId() + ".ve\",");
            }
            strBuilder.append("SRCVIEW:\"").append(viewCtx.getUniqueId()).append('\"');
            if (viewCtx.getModel().getViewConfiguration().containsTable("ACAjaxFormOption")) {
                strBuilder.append(',');
                final Iterator optionsIte = viewCtx.getModel().getViewConfiguration().getRows("ACAjaxFormOption", r);
                JSUtil.appendProperties(strBuilder, optionsIte, 3, 4);
            }
            strBuilder.append("});");
        }
        strBuilder.append("</script>");
        return strBuilder.toString();
    }
    
    public static String getFillFormWithDataScript(final String frmName, final Map data) {
        final StringBuffer strBuilder = new StringBuffer("<script>updateFormWithData(");
        strBuilder.append('\"').append(frmName).append("\",{");
        for (final Map.Entry prop : data.entrySet()) {
            appendFormProp(strBuilder, prop.getKey(), prop.getValue());
        }
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append("});</script>");
        return strBuilder.toString();
    }
    
    public static String getFillFormWithDataScript(final String frmName, final DataObject data, final boolean prefixTableName) throws Exception {
        final StringBuffer strBuilder = new StringBuffer("<script>updateFormWithData(");
        strBuilder.append('\"').append(frmName).append("\",{");
        final List tables = data.getTableNames();
        for (int i = 0; i < tables.size(); ++i) {
            final Row r = data.getFirstRow((String)tables.get(i));
            final List colNames = r.getColumns();
            for (int j = 0; j < colNames.size(); ++j) {
                final String key = prefixTableName ? (r.getTableName() + "." + colNames.get(j)) : colNames.get(j);
                appendFormProp(strBuilder, key, r.get(j + 1));
            }
        }
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        strBuilder.append("});</script>");
        return strBuilder.toString();
    }
    
    private static void appendFormProp(final StringBuffer strBuilder, final String key, final Object value) {
        if (value == null || value instanceof UniqueValueHolder) {
            return;
        }
        strBuilder.append('\"').append(key).append("\":\"").append(JSUtil.getEscapedString(value)).append("\",");
    }
    
    public static void fillRow(final Row rowToFill, final HttpServletRequest request, final boolean tableNamePrefixed) throws Exception {
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(rowToFill.getOriginalTableName());
        final List colList = td.getColumnList();
        final List colNames = rowToFill.getColumns();
        for (int j = 0; j < colNames.size(); ++j) {
            final String paramName = tableNamePrefixed ? (rowToFill.getTableName() + "." + colNames.get(j)) : colNames.get(j);
            final String columnName = colNames.get(j);
            final String value = request.getParameter(paramName);
            if (value != null && !(rowToFill.get(columnName) instanceof UniqueValueHolder)) {
                final Object transData = (value.length() == 0) ? null : convertDataToType(value, colList.get(j).getSQLType());
                rowToFill.set(j + 1, transData);
            }
        }
    }
    
    public static Object convertDataToType(final String value, final int type) {
        if (type == 12) {
            return value;
        }
        if (type == -5) {
            return new Long(value);
        }
        if (type == 16) {
            return "TRUE".equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (type == 4) {
            return new Integer(value);
        }
        if (type == 6) {
            return new Float(value);
        }
        if (type == 8) {
            return new Double(value);
        }
        return value;
    }
}
