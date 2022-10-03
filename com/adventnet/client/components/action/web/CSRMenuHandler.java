package com.adventnet.client.components.action.web;

import org.json.JSONObject;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.view.web.ViewContext;

public interface CSRMenuHandler
{
    void initMenuHandler(final ViewContext p0);
    
    JSONObject getRenderingAttrForMenu(final ViewContext p0, final TransformerContext p1) throws Exception;
    
    int handleMenuItem(final String p0, final ViewContext p1, final TransformerContext p2);
    
    JSONObject getExtraOptions(final String p0, final ViewContext p1, final TransformerContext p2) throws Exception;
}
