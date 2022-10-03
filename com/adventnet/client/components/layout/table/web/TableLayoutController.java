package com.adventnet.client.components.layout.table.web;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.client.components.layout.web.ChildIterator;
import java.util.Iterator;
import java.util.Collections;
import com.adventnet.persistence.Row;
import java.util.Comparator;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import java.util.List;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.personalize.web.PersonalizableView;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.components.layout.web.ContainerController;

public class TableLayoutController extends ContainerController implements WebConstants, PersonalizableView
{
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        List childConfigList = (List)viewCtx.getModel().getCompiledData((Object)"SORTEDTABLELIST");
        if (childConfigList == null) {
            childConfigList = getSortedList(viewCtx.getModel().getViewConfiguration());
            viewCtx.getModel().addCompiledData((Object)"SORTEDTABLELIST", (Object)childConfigList);
        }
        viewCtx.setViewModel((Object)new TableLayoutModel(viewCtx, childConfigList));
    }
    
    public static List getSortedList(final DataObject configDO) {
        try {
            final ArrayList sortedList = new ArrayList();
            if (configDO.containsTable("ACTableLayoutChildConfig")) {
                final Iterator ite = configDO.getRows("ACTableLayoutChildConfig");
                while (ite.hasNext()) {
                    sortedList.add(ite.next());
                }
                Collections.sort((List<Object>)sortedList, new Comparator() {
                    @Override
                    public int compare(final Object row1, final Object row2) {
                        return this.compare((Row)row1, (Row)row2);
                    }
                    
                    public int compare(final Row row1, final Row row2) {
                        return ((int)row1.get(3) - (int)row2.get(3)) * 1000 + ((int)row1.get(4) - (int)row2.get(4));
                    }
                });
            }
            return sortedList;
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public ChildIterator getChildIterator(final ViewContext vc) throws Exception {
        vc.getViewModel(false);
        return ((TableLayoutModel)vc.getViewModel()).getIterator();
    }
    
    public void createViewFromTemplate(final DataObject viewDataObj, final long accountId) throws Exception {
        PersonalizationUtil.createChildViewsFromTemplate(viewDataObj, accountId, "ACTableLayoutChildConfig", 2);
    }
    
    public void addView(final String viewName, final String newChildVewName, final long accountId, final HttpServletRequest request) {
        throw new RuntimeException("Not yet supported");
    }
    
    public void savePreferences(final ViewContext viewCtx) throws Exception {
        ContainerController.savePreferences(viewCtx, "ACTableLayoutChildConfig");
    }
}
