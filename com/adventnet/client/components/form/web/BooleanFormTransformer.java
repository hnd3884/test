package com.adventnet.client.components.form.web;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class BooleanFormTransformer extends DefaultTransformer
{
    @Override
    public void renderCell(final TransformerContext tableContext) throws Exception {
        final HashMap<String, Object> columnProperties = tableContext.getRenderedAttributes();
        final Object data = tableContext.getPropertyValue();
        columnProperties.put("VALUE", data);
        final HashMap<String, String> propHash = (HashMap<String, String>)tableContext.getRendererConfigProps().clone();
        if (propHash != null) {
            final String booleanTrue = propHash.remove("TRUE");
            final String booleanFalse = propHash.remove("FALSE");
            final List<String> serverList = new ArrayList<String>();
            serverList.add("true");
            serverList.add("false");
            final List<String> clientList = new ArrayList<String>();
            clientList.add(booleanTrue);
            clientList.add(booleanFalse);
            columnProperties.put("SERVER_VALUE", serverList);
            columnProperties.put("CLIENT_VALUE", clientList);
        }
    }
}
