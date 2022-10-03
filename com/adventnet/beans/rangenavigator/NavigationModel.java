package com.adventnet.beans.rangenavigator;

import com.adventnet.beans.rangenavigator.events.NavigationListener;

public interface NavigationModel
{
    long getStartIndex();
    
    long getEndIndex();
    
    long getPageLength();
    
    long getTotalRecordsCount();
    
    void showRange(final long p0, final long p1);
    
    void setPageLength(final long p0);
    
    void addNavigationListener(final NavigationListener p0);
    
    void removeNavigationListener(final NavigationListener p0);
}
