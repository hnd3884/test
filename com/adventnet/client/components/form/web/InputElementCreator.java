package com.adventnet.client.components.form.web;

import java.util.Hashtable;
import com.adventnet.client.view.web.ViewContext;
import java.util.Iterator;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.Row;
import com.adventnet.iam.xss.IAMEncoder;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.web.DefaultUICreator;

public class InputElementCreator extends DefaultUICreator
{
    @Override
    public String constructHeader(final TransformerContext context, final boolean isEditMode) {
        if (!"FieldName".equals(((FormTransformerContext)context).getDataType())) {
            return super.constructHeader(context, isEditMode);
        }
        return super.constructHeader(context, isEditMode);
    }
    
    @Override
    public String constructCell(final TransformerContext context, final boolean isEditMode) {
        if (!isEditMode) {
            return super.constructCell(context, true);
        }
        return this.createElement(context, (DataObject)context.getCreatorConfiguration());
    }
    
    public String createElement(final TransformerContext context, final DataObject configuration) {
        final Properties elementProps = this.getElementProps(context, configuration);
        this.updateType(context, elementProps);
        this.updateValue(context, elementProps);
        this.updateName(context, elementProps);
        this.updateId(context, elementProps);
        String type = "";
        try {
            type = (String)context.getViewContext().getModel().getViewConfiguration().getFirstValue("ACFormConfig", "ALERTTYPE");
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        final StringBuffer buffer = new StringBuffer();
        if ("Default".equals(type) || "Custom".equals(type)) {
            buffer.append(this.getHtmlForElement(context, elementProps));
        }
        else {
            buffer.append(this.getHtmlWithErrorDiv(context, elementProps, type));
        }
        return buffer.toString();
    }
    
    public String getHtmlWithErrorDiv(final TransformerContext context, final Properties props, final String type) {
        final String className = ((FormTransformerContext)context).getDataType() + "Class";
        final StringBuffer buffer = new StringBuffer();
        buffer.append("<table cellspacing='0' cellpadding='0' border='0' width='100%'><tr><td class='" + IAMEncoder.encodeHTMLAttribute(className) + "'>");
        buffer.append(this.getHtmlForElement(context, props));
        buffer.append("</td>");
        if ("CompleteBottom".equals(type)) {
            buffer.append("</tr><tr>");
        }
        buffer.append("<td class='errorMsg'><div id='" + IAMEncoder.encodeHTMLAttribute((String)((Hashtable<K, String>)props).get("id")) + "_DIV'></div></td></tr></table>");
        return buffer.toString();
    }
    
    public Properties getElementProps(final TransformerContext context, final DataObject elementConfiguration) {
        final Properties elementProps = new Properties();
        try {
            if (elementConfiguration != null) {
                final Iterator propIterator = elementConfiguration.getRows("ACElementAttr");
                while (propIterator.hasNext()) {
                    final Row attrRow = propIterator.next();
                    elementProps.put(attrRow.get(2), attrRow.get(3));
                }
                if (elementConfiguration.getFirstValue("ACElement", 4) != null) {
                    final String method = (String)elementConfiguration.getFirstValue("ACElement", 4);
                    if (method != null) {
                        ((Hashtable<String, String>)elementProps).put("validatemethod", method);
                    }
                }
                if (elementConfiguration.getFirstValue("ACElement", 5) != null) {
                    final String method = (String)elementConfiguration.getFirstValue("ACElement", 5);
                    if (method != null) {
                        ((Hashtable<String, String>)elementProps).put("errormsg", I18N.getMsg(method, new Object[0]));
                    }
                }
            }
            ((Hashtable<String, String>)elementProps).put("displayname", context.getDisplayName());
            final DataObject columnDO = context.getColumnConfiguration();
            if (columnDO.getFirstValue("ACColumnConfiguration", "ISNULLABLE") != null) {
                final Boolean isNullable = (Boolean)columnDO.getFirstValue("ACColumnConfiguration", "ISNULLABLE");
                ((Hashtable<String, String>)elementProps).put("isnullable", isNullable.toString());
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        return elementProps;
    }
    
    public String getAttributesAsString(final Properties elementProps) {
        final StringBuffer attrList = new StringBuffer(" ");
        for (final String key : ((Hashtable<Object, V>)elementProps).keySet()) {
            final String value = ((Hashtable<K, String>)elementProps).get(key);
            attrList.append(key);
            attrList.append("='");
            final String type = ((Hashtable<K, String>)elementProps).get("type");
            String tmpstr = getEscapedString(value);
            Label_0139: {
                if (key.equalsIgnoreCase("value") && type != null) {
                    if (type.equals("text")) {
                        if (type.equals("hidden")) {
                            break Label_0139;
                        }
                    }
                    try {
                        tmpstr = I18N.getMsg(tmpstr, new Object[0]);
                    }
                    catch (final Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }
            attrList.append(tmpstr);
            attrList.append("'  ");
        }
        return attrList.toString();
    }
    
    public static String getEscapedString(final String text) {
        if (text == null) {
            return null;
        }
        final StringBuffer charBuffer = new StringBuffer();
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
    
    public String getHtmlForElement(final TransformerContext context, final Properties elementProps) {
        final StringBuffer htmlCode = new StringBuffer("<INPUT ");
        htmlCode.append(this.getAttributesAsString(elementProps));
        final String type = ((Hashtable<K, String>)elementProps).get("type");
        htmlCode.append(">");
        if ("radio".equals(type) || "checkbox".equals(type)) {
            htmlCode.append(IAMEncoder.encodeHTML((String)((Hashtable<K, String>)elementProps).get("value")));
        }
        return htmlCode.toString();
    }
    
    public void updateType(final TransformerContext context, final Properties elementProps) {
        final String type = ((Hashtable<K, String>)elementProps).get("type");
        if (type == null) {
            ((Hashtable<String, String>)elementProps).put("type", "text");
        }
    }
    
    public void updateValue(final TransformerContext context, final Properties elementProps) {
        Object value = ((Hashtable<K, Object>)elementProps).get("value");
        if (value == null) {
            value = context.getRenderedAttributes().get("VALUE");
            final String type = ((Hashtable<K, String>)elementProps).get("type");
            if (value != null) {
                if ("image".equalsIgnoreCase(type)) {
                    ((Hashtable<String, Object>)elementProps).put("src", value);
                }
                else {
                    ((Hashtable<String, Object>)elementProps).put("value", value);
                }
            }
            else if (context.getPropertyValue() != null) {
                ((Hashtable<String, Object>)elementProps).put("value", context.getPropertyValue());
            }
            else {
                ((Hashtable<String, String>)elementProps).put("value", "");
            }
        }
    }
    
    public void updateName(final TransformerContext context, final Properties elementProps) {
        if (!elementProps.containsKey("name")) {
            ((Hashtable<String, String>)elementProps).put("name", context.getPropertyName());
        }
    }
    
    public void updateId(final TransformerContext context, final Properties elementProps) {
        if (!elementProps.containsKey("id")) {
            final StringBuffer id = new StringBuffer("F");
            final ViewContext viewContext = context.getViewContext();
            id.append("R");
            id.append(viewContext.getReferenceId());
            id.append("R");
            id.append(context.getRowIndex());
            id.append("C");
            id.append(context.getColumnIndex());
            ((Hashtable<String, String>)elementProps).put("id", id.toString());
        }
    }
}
