package com.adventnet.client.components.table.web;

import com.adventnet.client.util.web.JSUtil;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.themes.web.ThemesAPI;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.client.cache.StaticCache;
import com.adventnet.client.components.tpl.service.TemplateTablePopulator;
import com.adventnet.client.tpl.TemplateAPI;
import java.io.File;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public class TableTemplateHandler implements TableConstants
{
    public static Logger out;
    private static Pattern varPat;
    private Object[] compiledInfo;
    private boolean useCCache;
    
    public TableTemplateHandler() {
        this.useCCache = true;
    }
    
    public void initialize(final String filename, final TableTransformerContext transformerContext) throws Exception {
        String templateFile = transformerContext.getViewContext().getRequest().getSession().getServletContext().getRealPath(filename);
        String fileData = null;
        if (templateFile == null || !new File(templateFile).exists()) {
            templateFile = System.getProperty("server.dir") + "/" + filename;
        }
        fileData = TemplateAPI.getFileAsString(templateFile);
        fileData = TemplateTablePopulator.checkDollarProperties(fileData);
        this.compiledInfo = this.getCompiledInfo(filename, fileData, transformerContext);
    }
    
    public void initialize(final String fileName, final String fileData, final TableTransformerContext transformerContext) throws Exception {
        this.compiledInfo = this.getCompiledInfo(fileName, fileData, transformerContext);
    }
    
    public void useClientCache(final boolean arg) {
        this.useCCache = arg;
    }
    
    private Object[] getCompiledInfo(final String filename, final String fileData, final TableTransformerContext transformerContext) throws Exception {
        final String key = "TTHLR:" + filename;
        Object[] compiledInfo = null;
        final String developmentMode = System.getProperty("development.mode");
        if (this.useCCache && (developmentMode == null || !developmentMode.equals("true"))) {
            compiledInfo = (Object[])StaticCache.getFromCache((Object)key);
        }
        if (compiledInfo != null) {
            return compiledInfo;
        }
        final String[] staticList = TableTemplateHandler.varPat.split(fileData);
        final List variables = new ArrayList(staticList.length);
        final Matcher mat = TableTemplateHandler.varPat.matcher(fileData);
        while (mat.find()) {
            final String variable = mat.group(1).intern();
            final Integer varindex = (Integer)transformerContext.getPropertyIndex(variable);
            variables.add(varindex);
        }
        compiledInfo = new Object[] { staticList, variables.toArray(new Integer[variables.size()]) };
        if (this.useCCache) {
            StaticCache.addToCache((Object)key, (Object)compiledInfo);
        }
        return compiledInfo;
    }
    
    public String renderRow(final TableTransformerContext transformerContext) throws Exception {
        final ViewContext viewCtx = transformerContext.getViewContext();
        final HttpServletRequest request = viewCtx.getRequest();
        final TableViewModel viewModel = (TableViewModel)viewCtx.getViewModel();
        final Object[] viewColumns = viewModel.getViewColumns();
        final HashMap map = new HashMap();
        for (int i = 0; i < viewColumns.length; ++i) {
            final Integer varindex = (Integer)transformerContext.getPropertyIndex((String)((Object[])viewColumns[i])[0]);
            map.put(varindex, viewColumns[i]);
        }
        final StringBuffer strBuf = new StringBuffer();
        final String[] staticList = (String[])this.compiledInfo[0];
        final Integer[] variablesList = (Integer[])this.compiledInfo[1];
        for (int j = 0; j < staticList.length; ++j) {
            strBuf.append(staticList[j]);
            if (j < variablesList.length) {
                try {
                    final Object[] viewColumn = map.get(variablesList[j]);
                    transformerContext.setRendererConfigProps((HashMap<String, String>)viewColumn[3]);
                    transformerContext.setViewIndexForCol(j);
                    transformerContext.setColumnIndex((int)viewColumn[1]);
                    transformerContext.setColumnConfiguration((DataObject)viewColumn[4]);
                    transformerContext.setDisplayName((String)viewColumn[5]);
                    final ColumnTransformer transformer = (ColumnTransformer)viewColumn[2];
                    if (transformerContext.getRowIndex() == 0) {
                        transformer.initCellRendering(transformerContext);
                    }
                    transformer.renderCell(transformerContext);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
                final HashMap columnProperties = transformerContext.getRenderedAttributes();
                this.renderCell(strBuf, columnProperties, transformerContext, request);
            }
        }
        return strBuf.toString();
    }
    
    private void renderCell(final StringBuffer buff, final HashMap props, final TableTransformerContext transformerContext, final HttpServletRequest request) throws Exception {
        if (request == null) {
            final Object value = props.get("VALUE");
            if (value != null) {
                buff.append(value);
            }
            return;
        }
        final ViewContext viewContext = transformerContext.getViewContext();
        final String referenceId = viewContext.getReferenceId();
        final String themeDir = ThemesAPI.getThemeDirForRequest(request);
        if (props.size() == 0) {
            buff.append("&nbsp");
        }
        else {
            final String icon = ThemesAPI.handlePath((String)props.get("ICON"), request, themeDir);
            final String suffixIcon = ThemesAPI.handlePath((String)props.get("SUFFIX_ICON"), request, themeDir);
            if (props.get("LINK") != null) {
                buff.append("<a href=\"" + IAMEncoder.encodeHTMLAttribute((String)props.get("LINK")) + "\">");
            }
            if (icon != null) {
                buff.append("<img src=\"" + IAMEncoder.encodeHTMLAttribute(icon) + "\">");
            }
            if (props.get("PREFIX_TEXT") != null) {
                buff.append(IAMEncoder.encodeHTML((String)props.get("PREFIX_TEXT")));
            }
            if (props.get("ACTUAL_VALUE") != null) {
                final String key = "r" + referenceId + "r" + transformerContext.getRowIndex() + "c" + transformerContext.getColumnIndex();
                buff.append("<div id='" + IAMEncoder.encodeHTMLAttribute(key) + "'></div>");
                buff.append("<Script>");
                buff.append(props.get("MESSAGE_DISPLAYER") + "('" + JSUtil.getEscapedString(props.get("TRIMMED_VALUE")) + "','" + JSUtil.getEscapedString(props.get("ACTUAL_VALUE")) + "','" + key + "','" + props.get("ACTION_LINK") + "',document);");
                buff.append("</Script>");
            }
            Object value2 = props.get("VALUE");
            if (value2 == null || value2.equals("")) {
                value2 = "";
            }
            buff.append(value2);
            if (props.get("SUFFIX_TEXT") != null) {
                buff.append(IAMEncoder.encodeHTML((String)props.get("SUFFIX_TEXT")));
            }
            if (suffixIcon != null) {
                buff.append("<img src=\"" + IAMEncoder.encodeHTMLAttribute(suffixIcon) + "\">");
            }
            if (props.get("LINK") != null) {
                buff.append("</a>");
            }
        }
    }
    
    static {
        TableTemplateHandler.out = Logger.getLogger(TableTemplateHandler.class.getName());
        TableTemplateHandler.varPat = Pattern.compile("\\$\\{\\{([^\\}]*)\\}\\}");
    }
}
