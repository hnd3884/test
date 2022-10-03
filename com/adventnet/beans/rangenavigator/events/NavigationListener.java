package com.adventnet.beans.rangenavigator.events;

import java.util.EventListener;

public interface NavigationListener extends EventListener
{
    void navigationChanged(final NavigationEvent p0);
}
