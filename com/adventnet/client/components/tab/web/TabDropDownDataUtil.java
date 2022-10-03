package com.adventnet.client.components.tab.web;

import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;

public class TabDropDownDataUtil
{
    public static JSONObject getDropDownAsJSON(final TabModel.TabIterator itr, final ViewContext parentViewContext) throws Exception {
        try {
            final String dropDownTabViewName = itr.getDropDownTab();
            if (dropDownTabViewName != null) {
                final ViewContext viewContext = ViewContext.getViewContext((Object)dropDownTabViewName, parentViewContext.getRequest());
                viewContext.getViewModel(true);
                return TabDataUtil.getAsJSON(viewContext);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }
}
