package com.adventnet.client.components.form.web;

import com.adventnet.persistence.personality.PersonalityConfigurationUtil;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.client.components.table.web.TableViewModel;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.web.UICreator;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.ColumnTransformer;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.web.WebViewAPI;
import javax.servlet.jsp.PageContext;
import java.util.HashMap;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.table.web.TableConstants;

public class FormCreator implements TableConstants
{
    ViewContext viewContext;
    Properties props;
    DataObject viewConfiguration;
    HashMap columnConfigMap;
    DataObject layoutConfigDO;
    PageContext pageContext;
    boolean isReadMode;
    String formName;
    String actionLink;
    String target;
    String jsFileName;
    String formType;
    String alertType;
    
    public FormCreator(final ViewContext context, final PageContext pageContext) {
        this.viewContext = null;
        this.props = null;
        this.viewConfiguration = null;
        this.columnConfigMap = null;
        this.layoutConfigDO = null;
        this.pageContext = null;
        this.isReadMode = false;
        this.formName = null;
        this.actionLink = null;
        this.target = null;
        this.jsFileName = null;
        this.formType = "UPDATE";
        this.alertType = "Default";
        this.viewContext = context;
        this.pageContext = pageContext;
        this.props = (Properties)this.viewContext.getViewModel();
        this.viewConfiguration = this.viewContext.getModel().getViewConfiguration();
        try {
            final Row formConfigRow = this.viewConfiguration.getFirstRow("ACFormConfig");
            final Long columnConfig = (Long)formConfigRow.get("COLUMNCONFIGLIST");
            final Long layoutConfig = (Long)formConfigRow.get("LAYOUTCONFIGLIST");
            this.isReadMode = (boolean)formConfigRow.get("ISREADMODE");
            this.formName = WebViewAPI.getViewName((Object)formConfigRow.get("NAME"));
            this.actionLink = (String)formConfigRow.get("ACTIONLINK");
            this.target = (String)formConfigRow.get("TARGET");
            this.alertType = (String)formConfigRow.get("ALERTTYPE");
            this.jsFileName = (String)formConfigRow.get("JSFILENAME");
            this.formType = (String)formConfigRow.get("FORMTYPE");
            if (this.formType != null && this.formType.equalsIgnoreCase("CREATE")) {
                this.props.clear();
            }
            this.columnConfigMap = this.getColumnConfigMap(columnConfig);
            this.layoutConfigDO = this.getLayoutDataObject(layoutConfig);
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public String getHtmlForModeSwitcher(final String formName) {
        return "<DIV align='center' class='ModeSwitcher'><A HREF='javascript:enableForm(\"" + IAMEncoder.encodeJavaScript(formName) + "\")'>Edit</A></DIV><br>";
    }
    
    public void instantiateFormElement(final StringBuffer buffer) throws Exception {
        buffer.append("<FORM NAME='" + IAMEncoder.encodeHTMLAttribute(this.formName) + "_FORM' METHOD='post' onSubmit=\"return executeFunction('ValidateForm',this)\" viewName='" + IAMEncoder.encodeHTMLAttribute(this.formName) + "' alerttype=" + IAMEncoder.encodeHTMLAttribute(this.alertType));
        if (this.actionLink == null) {
            this.actionLink = this.formName + ".ve";
        }
        buffer.append(" ACTION='" + IAMEncoder.encodeHTMLAttribute(this.actionLink) + "' ");
        if (this.target != null) {
            buffer.append(" TARGET='" + IAMEncoder.encodeHTMLAttribute(this.target) + "'");
        }
        buffer.append(" >");
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        if (this.viewConfiguration.containsTable("TemplateViewParams")) {
            final Iterator iterator = this.viewConfiguration.getRows("TemplateViewParams");
            while (iterator.hasNext()) {
                final Row currentRow = iterator.next();
                final String paramName = (String)currentRow.get("PARAMNAME");
                final String paramValue = request.getParameter(paramName);
                if (paramValue != null) {
                    buffer.append("<Input type='hidden' name='" + IAMEncoder.encodeHTMLAttribute(paramName) + "' value='" + IAMEncoder.encodeHTMLAttribute(paramValue) + "'>");
                }
            }
        }
    }
    
    public String constructTD(final Row currentElement) {
        final String dataType = (String)currentElement.get("DATATYPE");
        final String dataValue = (String)currentElement.get("DATAVALUE");
        String width = "";
        String height = "";
        String colspan = "";
        String rowspan = "";
        if ((int)currentElement.get("COLSPAN") > 1) {
            colspan = "COLSPAN='" + currentElement.get("COLSPAN") + "' ";
        }
        if ((int)currentElement.get("ROWSPAN") > 1) {
            rowspan = "ROWSPAN='" + currentElement.get("ROWSPAN") + "' ";
        }
        if (currentElement.get("WIDTH") != null) {
            width = "WIDTH='" + currentElement.get("WIDTH") + "%'";
        }
        if (currentElement.get("HEIGHT") != null) {
            height = "HEIGHT='" + currentElement.get("HEIGHT") + "%'";
        }
        return "<TD " + colspan + rowspan + " CLASS='" + IAMEncoder.encodeHTMLAttribute(dataType) + "Class'" + width + height + " >";
    }
    
    public String getFormUI() throws Exception {
        final FormTransformerContext transformerContext = new FormTransformerContext(this.props, this.viewContext);
        transformerContext.setPageContext(this.pageContext);
        final HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
        transformerContext.setRequest(request);
        int previousRowIndex = -1;
        int previousColIndex = 0;
        int count = -1;
        final StringBuffer finalCode = new StringBuffer();
        if (this.jsFileName != null) {
            finalCode.append("<script src='" + request.getContextPath() + this.jsFileName + "'></script>");
        }
        if (this.isReadMode) {
            finalCode.append(this.getHtmlForModeSwitcher(this.formName));
        }
        this.instantiateFormElement(finalCode);
        final StringBuffer formEditCode = new StringBuffer("<TABLE CLASS='formTable' CELLSPACING='1'>");
        final StringBuffer formReadCode = new StringBuffer("<TABLE CLASS='formTable' CELLSPACING='1'>");
        try {
            final Iterator rowIterator = this.layoutConfigDO.getRows("ACPSConfiguration");
            while (rowIterator.hasNext()) {
                ++count;
                final Row currentElement = rowIterator.next();
                final String columnName = (String)currentElement.get("DATAVALUE");
                final String columnType = (String)currentElement.get("DATATYPE");
                Object formElement = null;
                final Object[] viewColumn = this.columnConfigMap.get(columnName);
                ColumnTransformer transformer = null;
                transformerContext.reset();
                transformerContext.setPropertyName(columnName);
                transformerContext.setColumnIndex(count);
                if (viewColumn != null && !"Label".equals(columnType) && !"Text".equals(columnType)) {
                    transformerContext.setRendererConfigProps((HashMap<String, String>)viewColumn[3]);
                    transformerContext.setColumnConfiguration((DataObject)viewColumn[4]);
                    transformerContext.setDisplayName((String)viewColumn[5]);
                    transformer = (ColumnTransformer)viewColumn[2];
                    transformer.initCellRendering(transformerContext);
                    transformer.renderCell(transformerContext);
                    formElement = viewColumn[6];
                }
                else {
                    transformerContext.setDisplayName(columnName);
                }
                if (currentElement.get("CREATORCONFIG") != null) {
                    formElement = currentElement.get("CREATORCONFIG");
                }
                if (formElement == null) {
                    formElement = "DefaultInputElement";
                }
                final Row formRow = new Row("ACElement");
                if (formElement instanceof String) {
                    formRow.set("NAME", formElement);
                }
                else {
                    formRow.set("NAME_NO", formElement);
                }
                final DataObject configDO = LookUpUtil.getPersistence().getForPersonality("ElementConfig", formRow);
                transformerContext.setCreatorConfiguration(configDO);
                final String uiCreator = (String)configDO.getFirstValue("ACElement", "UICREATOR");
                final UICreator elementCreator = (UICreator)WebClientUtil.createInstance(uiCreator);
                final int currentRowIndex = (int)currentElement.get("ROWINDEX");
                final int currentColIndex = (int)currentElement.get("COLUMNINDEX");
                if (previousRowIndex != currentRowIndex) {
                    if (currentRowIndex > 0) {
                        formEditCode.append("</TR>");
                        formReadCode.append("</TR>");
                    }
                    previousRowIndex = currentRowIndex;
                    previousColIndex = 0;
                    formEditCode.append("<TR>");
                    formReadCode.append("<TR>");
                }
                if (previousColIndex < currentColIndex) {
                    for (int i = previousColIndex; i < currentColIndex; ++i) {
                        formEditCode.append("<TD CLASS='EmptyFormCell'>&nbsp;</TD>");
                        formReadCode.append("<TD CLASS='EmptyFormCell'>&nbsp;</TD>");
                    }
                }
                previousColIndex = currentColIndex + (int)currentElement.get("COLSPAN");
                final String dataType = (String)currentElement.get("DATATYPE");
                final String dataValue = (String)currentElement.get("DATAVALUE");
                final String tdValue = this.constructTD(currentElement);
                formEditCode.append(tdValue);
                formReadCode.append(tdValue);
                transformerContext.setDataType(dataType);
                if ("FieldName".equals(dataType) || "Label".equals(dataType) || "Text".equals(dataType)) {
                    if (transformer != null) {
                        transformer.renderHeader(transformerContext);
                    }
                    formEditCode.append(elementCreator.constructHeader(transformerContext, true));
                    formReadCode.append(elementCreator.constructHeader(transformerContext, false));
                }
                if ("FieldValue".equals(dataType) || "FormElement".equals(dataType) || "ButtonPanel".equals(dataType)) {
                    if (transformer != null) {
                        transformer.renderCell(transformerContext);
                    }
                    formEditCode.append(elementCreator.constructCell(transformerContext, true));
                    formReadCode.append(elementCreator.constructCell(transformerContext, false));
                }
                formEditCode.append("</TD>");
                formReadCode.append("</TD>");
            }
            formEditCode.append("</TR></TABLE>");
            formReadCode.append("</TR></TABLE>");
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        if (this.isReadMode) {
            finalCode.append("<DIV id='" + IAMEncoder.encodeHTMLAttribute(this.formName) + "ReadMode'>");
            finalCode.append(formReadCode);
            finalCode.append("</DIV>");
        }
        finalCode.append("<DIV id='" + IAMEncoder.encodeHTMLAttribute(this.formName) + "EditMode'");
        if (this.isReadMode) {
            finalCode.append(" class='hide'");
        }
        finalCode.append(" >");
        finalCode.append(formEditCode);
        finalCode.append("</DIV>");
        finalCode.append("</FORM><SCRIPT>createFormMethodContainer('" + IAMEncoder.encodeJavaScript(this.formName) + "_FORM');</SCRIPT>");
        return finalCode.toString();
    }
    
    public HashMap getColumnConfigMap(final Object configurationList) {
        final DataObject columnConfigDO = this.getColumnConfigDO(configurationList);
        final HashMap columnMap = new HashMap();
        final ArrayList list = new ArrayList();
        list.add("ACColumnConfiguration");
        list.add("ACRendererConfiguration");
        try {
            final Iterator colConfig = columnConfigDO.getRows("ACColumnConfiguration");
            while (colConfig.hasNext()) {
                final Row columnRow = colConfig.next();
                final DataObject newDO = columnConfigDO.getDataObject((List)list, columnRow);
                final Object[] columnObject = new Object[7];
                final String columnName = (String)columnRow.get(3);
                final String displayName = (String)columnRow.get(4);
                final String transformer = (String)columnRow.get(7);
                final Long attrList = (Long)columnRow.get("CREATORCONFIG");
                columnObject[0] = columnName;
                columnObject[1] = new Integer(-1);
                columnObject[2] = WebClientUtil.createInstance(transformer);
                columnObject[3] = TableViewModel.getRendererConfigProps(newDO);
                columnObject[4] = newDO;
                columnObject[5] = displayName;
                columnObject[6] = attrList;
                columnMap.put(columnName, columnObject);
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
        return columnMap;
    }
    
    public DataObject getLayoutDataObject(final Object layoutName) {
        try {
            final Row customViewRow = new Row("CustomViewConfiguration");
            customViewRow.set("CVNAME", (Object)"PSCV");
            final DataObject customViewDO = LookUpUtil.getPersistence().get("CustomViewConfiguration", customViewRow);
            final long queryID = (long)customViewDO.getFirstValue("CustomViewConfiguration", "QUERYID");
            final SelectQuery query = QueryUtil.getSelectQuery(queryID);
            final Criteria crit = new Criteria(new Column("ACPSConfiguration", "CONFIGNAME"), layoutName, 0);
            query.setCriteria(crit);
            return LookUpUtil.getPersistence().get(query);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public DataObject getColumnConfigDO(final Object columnConfigName) {
        DataObject configDO = null;
        try {
            configDO = (DataObject)StaticCache.getFromCache((Object)("COLUMN_CONFIG:" + columnConfigName));
            if (configDO == null) {
                final Row navigRow = new Row("ACColumnConfigurationList");
                if (columnConfigName instanceof String) {
                    navigRow.set("NAME", columnConfigName);
                }
                else {
                    navigRow.set("NAME_NO", columnConfigName);
                }
                configDO = LookUpUtil.getPersistence().getForPersonality("ColumnConfiguration", navigRow);
                StaticCache.addToCache((Object)("COLUMN_CONFIG:" + columnConfigName), (Object)configDO, PersonalityConfigurationUtil.getConstituentTables("ColumnConfiguration"));
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return configDO;
    }
}
