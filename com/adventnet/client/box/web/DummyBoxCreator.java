package com.adventnet.client.box.web;

import com.adventnet.client.view.web.ViewContext;

public class DummyBoxCreator implements BoxCreator
{
    @Override
    public void initBox(final String boxConfig, final String boxId, final boolean isOpen) {
    }
    
    @Override
    public String getHtmlForBoxPrefix() {
        return "";
    }
    
    @Override
    public String getHtmlForBoxSuffix() {
        return "";
    }
    
    @Override
    public void setToolBarSnippet(final String toolBarSnippet) {
    }
    
    @Override
    public void setTitle(final String title) {
    }
    
    @Override
    public void setViewContext(final ViewContext vc) {
    }
    
    @Override
    public String getBoxConfigName() {
        return null;
    }
}
