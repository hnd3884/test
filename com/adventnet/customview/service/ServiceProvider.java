package com.adventnet.customview.service;

import com.adventnet.customview.CustomViewException;
import com.adventnet.customview.ViewData;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.CustomViewManagerContext;

public interface ServiceProvider
{
    String getServiceName();
    
    void setCustomViewManagerContext(final CustomViewManagerContext p0);
    
    void setNextServiceProvider(final ServiceProvider p0);
    
    ViewData process(final CustomViewRequest p0) throws CustomViewException;
    
    void cleanup();
}
