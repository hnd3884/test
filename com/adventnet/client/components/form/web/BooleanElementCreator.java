package com.adventnet.client.components.form.web;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Properties;
import com.adventnet.client.components.web.TransformerContext;

public class BooleanElementCreator extends InputElementCreator
{
    @Override
    public String getHtmlForElement(final TransformerContext context, final Properties elementProps) {
        ArrayList clientValues = context.getRenderedAttributes().get("CLIENT_VALUE");
        final ArrayList serverValues = context.getRenderedAttributes().get("SERVER_VALUE");
        if (clientValues == null) {
            clientValues = serverValues;
        }
        final StringBuffer htmlCode = new StringBuffer();
        final String value = ((Hashtable<K, String>)elementProps).remove("value");
        for (int count = 0; count < serverValues.size(); ++count) {
            htmlCode.append("<INPUT ");
            htmlCode.append(this.getAttributesAsString(elementProps));
            htmlCode.append(" value = '" + serverValues.get(count) + "' ");
            if (serverValues.get(count).equals(value)) {
                htmlCode.append("checked ");
            }
            htmlCode.append(">&nbsp;");
            htmlCode.append(clientValues.get(count));
            htmlCode.append("&nbsp;&nbsp;&nbsp;");
        }
        return htmlCode.toString();
    }
}
