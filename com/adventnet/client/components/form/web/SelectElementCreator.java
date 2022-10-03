package com.adventnet.client.components.form.web;

import java.util.ArrayList;
import java.util.Properties;
import com.adventnet.client.components.web.TransformerContext;

public class SelectElementCreator extends InputElementCreator
{
    @Override
    public String getHtmlForElement(final TransformerContext context, final Properties elementProps) {
        final Object data = context.getPropertyValue();
        ArrayList clientValues = context.getRenderedAttributes().get("CLIENT_VALUE");
        final ArrayList serverValues = context.getRenderedAttributes().get("SERVER_VALUE");
        if (clientValues == null) {
            clientValues = serverValues;
        }
        elementProps.remove("type");
        final StringBuffer htmlCode = new StringBuffer();
        htmlCode.append("<SELECT ");
        htmlCode.append(this.getAttributesAsString(elementProps));
        htmlCode.append(" >");
        for (int count = 0; count < serverValues.size(); ++count) {
            htmlCode.append("<OPTION value = '" + serverValues.get(count) + "' ");
            if (data != null && serverValues.get(count) != null && serverValues.get(count).equals(data.toString())) {
                htmlCode.append("selected ");
            }
            htmlCode.append(">");
            htmlCode.append(clientValues.get(count));
            htmlCode.append("</OPTION>");
        }
        htmlCode.append("</SELECT>");
        return htmlCode.toString();
    }
}
