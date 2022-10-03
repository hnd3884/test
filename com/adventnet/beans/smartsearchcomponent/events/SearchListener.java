package com.adventnet.beans.smartsearchcomponent.events;

import java.util.EventListener;

public interface SearchListener extends EventListener
{
    void startSearch(final SearchEvent p0);
    
    void stopSearch(final SearchEvent p0);
}
