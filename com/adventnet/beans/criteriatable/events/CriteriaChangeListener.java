package com.adventnet.beans.criteriatable.events;

import java.util.EventListener;

public interface CriteriaChangeListener extends EventListener
{
    void criteriaChanged(final CriteriaChangeEvent p0);
}
