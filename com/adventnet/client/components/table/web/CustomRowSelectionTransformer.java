package com.adventnet.client.components.table.web;

import java.util.HashMap;
import org.json.JSONObject;
import com.adventnet.client.components.web.TransformerContext;

public class CustomRowSelectionTransformer extends DefaultTransformer
{
    @Override
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap<String, Object> columnProperties = tableContext.getRenderedAttributes();
        final JSONObject val = new JSONObject();
        val.put("isDisabled", tableContext.getRowIndex() % 2 == 0);
        columnProperties.put("PAYLOAD", val);
    }
}
