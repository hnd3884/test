package com.adventnet.customview;

public interface CustomViewManager
{
    ViewData getData(final CustomViewRequest p0) throws CustomViewException;
    
    CustomViewManagerContext getCustomViewManagerContext();
}
