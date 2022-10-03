package com.adventnet.beans.smartsearchcomponent.events;

import com.adventnet.beans.criteriatable.Criteria;
import java.util.EventObject;

public class SearchEvent extends EventObject
{
    Criteria criteria;
    Object searchTarget;
    
    public SearchEvent(final Object o, final Criteria criteria, final Object searchTarget) {
        super(o);
        this.criteria = criteria;
        this.searchTarget = searchTarget;
    }
    
    public Criteria getCriteria() {
        return this.criteria;
    }
    
    public Object getSearchTarget() {
        return this.searchTarget;
    }
}
