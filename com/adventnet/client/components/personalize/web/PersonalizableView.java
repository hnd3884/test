package com.adventnet.client.components.personalize.web;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.DataObject;

public interface PersonalizableView
{
    void createViewFromTemplate(final DataObject p0, final long p1) throws Exception;
    
    void addView(final String p0, final String p1, final long p2, final HttpServletRequest p3) throws Exception;
}
