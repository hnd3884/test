package com.adventnet.client.components.form.web;

import java.util.Iterator;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.components.web.UICreator;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import java.util.TreeMap;
import com.adventnet.persistence.DataObject;
import java.util.Properties;
import com.adventnet.client.components.web.TransformerContext;

public class ButtonPanelCreator extends ButtonElementCreator
{
    @Override
    public String getHtmlForElement(final TransformerContext context, final Properties elementProps) {
        final DataObject creatorDO = (DataObject)context.getCreatorConfiguration();
        final StringBuffer buffer = new StringBuffer();
        try {
            final Iterator propIterator = creatorDO.getRows("ACPanelElement");
            final TreeMap map = new TreeMap();
            while (propIterator.hasNext()) {
                final Row attrRow = propIterator.next();
                map.put(attrRow.get("BUTTONINDEX"), attrRow);
            }
            for (final Integer intr : map.keySet()) {
                final Row attrRow2 = map.get(intr);
                final Long elementName = (Long)attrRow2.get("CHILDELEMENT");
                final Row formRow = new Row("ACElement");
                formRow.set("NAME_NO", (Object)elementName);
                final DataObject configDO = LookUpUtil.getPersistence().getForPersonality("ElementConfig", formRow);
                context.setCreatorConfiguration(configDO);
                final String uiCreator = (String)configDO.getFirstValue("ACElement", "UICREATOR");
                final UICreator elementCreator = (UICreator)WebClientUtil.createInstance(uiCreator);
                buffer.append("&nbsp;&nbsp;&nbsp;" + elementCreator.constructCell(context, true));
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        return buffer.toString();
    }
    
    @Override
    public String getHtmlWithErrorDiv(final TransformerContext context, final Properties props, final String type) {
        return this.getHtmlForElement(context, props);
    }
}
