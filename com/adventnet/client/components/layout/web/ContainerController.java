package com.adventnet.client.components.layout.web;

import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.UserPersonalizationAPI;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public abstract class ContainerController extends DefaultViewController implements WebConstants
{
    public abstract ChildIterator getChildIterator(final ViewContext p0) throws Exception;
    
    public static void savePreferences(final ViewContext viewCtx, final String tableName) throws Exception {
        final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewCtx.getModel().getViewName(), WebClientUtil.getAccountId());
        final Iterator ite = dataObject.getRows(tableName);
        while (ite.hasNext()) {
            final Row row = ite.next();
            final Long child = (Long)row.get("CHILDVIEWNAME");
            final String state = (String)viewCtx.getStateParameter("S_" + WebViewAPI.getViewName((Object)child));
            if (state != null) {
                row.set("ISOPEN", (Object)new Boolean(state));
                dataObject.updateRow(row);
            }
        }
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, WebClientUtil.getAccountId());
    }
}
