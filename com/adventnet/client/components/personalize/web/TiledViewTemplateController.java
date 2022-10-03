package com.adventnet.client.components.personalize.web;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class TiledViewTemplateController extends DefaultViewController implements WebConstants, PersonalizableView
{
    public void createViewFromTemplate(final DataObject viewDataObj, final long accountId) throws Exception {
        PersonalizationUtil.createChildViewsFromTemplate(viewDataObj, accountId, "TiledView", 3);
    }
    
    public void addView(final String viewName, final String newChildVewName, final long accountId, final HttpServletRequest request) {
        throw new RuntimeException("Not yet supported");
    }
}
