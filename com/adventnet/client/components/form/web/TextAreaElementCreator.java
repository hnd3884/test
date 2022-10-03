package com.adventnet.client.components.form.web;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.client.components.web.TransformerContext;

public class TextAreaElementCreator extends InputElementCreator
{
    @Override
    public String getHtmlForElement(final TransformerContext context, final Properties elementProps) {
        final StringBuffer htmlCode = new StringBuffer("<TEXTAREA ");
        htmlCode.append(this.getAttributesAsString(elementProps));
        htmlCode.append(">");
        htmlCode.append(((Hashtable<K, Object>)elementProps).get("value"));
        htmlCode.append("</TEXTAREA>");
        return htmlCode.toString();
    }
}
