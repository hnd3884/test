package com.me.mdm.server.factory;

import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;

public interface MDMTableViewAPI
{
    String getIsExport(final TransformerContext p0);
    
    String[] getViewContextParameterValues(final ViewContext p0, final String p1);
}
