package com.adventnet.client.components.property.web;

import com.adventnet.client.util.DataUtils;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.table.web.TableViewModel;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public abstract class PropertyController extends DefaultViewController implements WebConstants
{
    public abstract Object getPropertyValue(final ViewContext p0, final String p1) throws Exception;
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        final Long columnConfigName = (Long)viewCtx.getModel().getViewConfiguration().getFirstValue("ACTableViewConfig", 4);
        final DataObject columnConfig = this.getColumnConfigDO(columnConfigName);
        final ArrayList propertyNames = new ArrayList();
        final ArrayList values = new ArrayList();
        final Iterator ite = columnConfig.getRows("ACColumnConfiguration");
        while (ite.hasNext()) {
            final Row r = ite.next();
            propertyNames.add(r.get(3));
            values.add(this.getPropertyValue(viewCtx, (String)r.get(3)));
        }
        final TableViewModel viewModel = new TableViewModel(new PropertyTableModel(propertyNames, values), viewCtx);
        viewModel.init();
        viewModel.getTableTransformerContext().setRequest(viewCtx.getRequest());
        viewCtx.setViewModel((Object)viewModel);
    }
    
    public DataObject getColumnConfigDO(final Object columnConfigName) throws Exception {
        if (columnConfigName instanceof String) {
            return DataUtils.getFromCache("ColumnConfiguration", "ACColumnConfigurationList", "NAME", columnConfigName);
        }
        return DataUtils.getFromCache("ColumnConfiguration", "ACColumnConfigurationList", "NAME_NO", columnConfigName);
    }
}
