package com.adventnet.client.view;

import com.adventnet.client.view.web.ViewContext;

public interface ViewHandler
{
    boolean canRender(final ViewContext p0) throws Exception;
}
