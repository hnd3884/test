package com.adventnet.client.components.form.web;

import java.util.Hashtable;
import java.util.Properties;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.web.TransformerContext;

public class ButtonElementCreator extends InputElementCreator
{
    @Override
    public String constructCell(final TransformerContext context, final boolean isEditMode) {
        if (!isEditMode) {
            return "";
        }
        return this.createElement(context, (DataObject)context.getCreatorConfiguration());
    }
    
    @Override
    public String getHtmlWithErrorDiv(final TransformerContext context, final Properties props, final String type) {
        return this.getHtmlForElement(context, props);
    }
    
    @Override
    public void updateType(final TransformerContext context, final Properties elementProps) {
        final String type = ((Hashtable<K, String>)elementProps).get("type");
        if (type == null) {
            ((Hashtable<String, String>)elementProps).put("type", "button");
        }
    }
}
