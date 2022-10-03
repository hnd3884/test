package com.adventnet.client.components.table.web;

import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;

public class MessageTrimmer extends DefaultTransformer
{
    int textLength;
    boolean constructLink;
    
    public MessageTrimmer() {
        this.textLength = 0;
        this.constructLink = false;
    }
    
    @Override
    public void initCellRendering(final TransformerContext context) {
        final HashMap<String, String> propHash = context.getRendererConfigProps();
        this.textLength = new Integer(propHash.get("LENGTH"));
        if (propHash.get("CONSTRUCT_LINK") != null && propHash.get("CONSTRUCT_LINK").equalsIgnoreCase("TRUE")) {
            this.constructLink = true;
        }
    }
    
    @Override
    public void renderCell(final TransformerContext context) throws Exception {
        final TableTransformerContext tableContext = (TableTransformerContext)context;
        final HashMap<String, Object> columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        if (data == null) {
            columnProperties.put("VALUE", "");
        }
        else {
            String trimmedContent = String.valueOf(data);
            if (trimmedContent.length() > this.textLength) {
                trimmedContent = trimmedContent.substring(0, this.textLength) + " ...";
                if (this.constructLink) {
                    columnProperties.put("ACTUAL_VALUE", String.valueOf(data));
                }
            }
            columnProperties.put("VALUE", trimmedContent);
        }
    }
}
