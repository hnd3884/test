package com.adventnet.client.action.web;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;

public interface MenuHandler
{
    void initMenuHandler(final ViewContext p0, final HttpServletRequest p1, final HttpServletResponse p2);
    
    int handleMenuItem(final String p0, final ViewContext p1, final HttpServletRequest p2, final HttpServletResponse p3);
}
