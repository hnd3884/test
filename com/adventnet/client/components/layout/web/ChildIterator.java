package com.adventnet.client.components.layout.web;

import com.adventnet.client.view.web.ViewContext;

public abstract class ChildIterator
{
    public abstract boolean next() throws Exception;
    
    public abstract ViewContext getChildCtx();
}
