package com.adventnet.client.box.web;

import com.adventnet.client.view.web.ViewContext;

public interface BoxCreator
{
    void initBox(final String p0, final String p1, final boolean p2) throws Exception;
    
    String getHtmlForBoxPrefix();
    
    String getHtmlForBoxSuffix();
    
    void setToolBarSnippet(final String p0);
    
    void setTitle(final String p0);
    
    void setViewContext(final ViewContext p0);
    
    String getBoxConfigName();
}
