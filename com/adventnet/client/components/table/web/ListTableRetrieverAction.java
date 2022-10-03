package com.adventnet.client.components.table.web;

import java.util.Iterator;
import com.adventnet.client.view.ViewModel;
import com.adventnet.persistence.Row;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.client.util.web.WebConstants;

public class ListTableRetrieverAction extends TableRetrieverAction implements WebConstants
{
    @Override
    public void setCriteria(final SelectQuery query, final ViewContext viewCtx) {
        try {
            super.setCriteria(query, viewCtx);
            Criteria criteria = query.getCriteria();
            Object value = viewCtx.getRequest().getParameter("VIEWNAME");
            if (value == null) {
                value = viewCtx.getStateParameter("VIEWNAME");
            }
            viewCtx.setStateParameter("VIEWNAME", value);
            if (value != null) {
                final Column col = new Column("ViewConfiguration", "VIEWNAME");
                final Criteria crit = new Criteria(col, value, 1);
                if (criteria != null) {
                    criteria = criteria.and(crit);
                }
                else {
                    criteria = crit;
                }
                query.setCriteria(criteria);
                this.addTemplateCriteria((String)value, query);
            }
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void addTemplateCriteria(final String viewName, final SelectQuery query) throws Exception {
        final ViewModel vm = (ViewModel)WebViewAPI.getConfigModel((Object)viewName, true);
        Join j = null;
        if (!vm.getViewConfiguration().containsTable("TemplateViewParams")) {
            j = new Join("ViewConfiguration", "TemplateViewParams", new String[] { "VIEWNAME_NO" }, new String[] { "VIEWNAME" }, "ViewConfiguration", "CountTVP", 1);
            query.addJoin(j);
            Criteria cr = query.getCriteria();
            cr = cr.and(new Criteria(new Column("CountTVP", "VIEWNAME"), (Object)null, 0));
            query.setCriteria(cr);
            return;
        }
        final ArrayList notInString = new ArrayList();
        final Iterator ite = vm.getViewConfiguration().getRows("TemplateViewParams");
        while (ite.hasNext()) {
            final Row r = ite.next();
            final String paramName = (String)r.get(2);
            notInString.add(paramName);
            Criteria cr2 = new Criteria(new Column("ViewConfiguration", "VIEWNAME"), (Object)new Column("TVP_" + paramName, "VIEWNAME"), 0);
            cr2 = cr2.and(new Criteria(new Column("TVP_" + paramName, "PARAMNAME"), (Object)paramName, 0));
            j = new Join("ViewConfiguration", "ViewConfiguration", "TemplateViewParams", "TVP_" + paramName, cr2, 2);
            query.addJoin(j);
        }
        Criteria joinCr = new Criteria(new Column("CountTVP", "PARAMNAME"), (Object)notInString.toArray(), 9);
        joinCr = joinCr.and(new Criteria(new Column("ViewConfiguration", "VIEWNAME"), (Object)new Column("CountTVP", "VIEWNAME"), 0));
        j = new Join("ViewConfiguration", "ViewConfiguration", "TemplateViewParams", "CountTVP", joinCr, 1);
        query.addJoin(j);
        Criteria cr3 = query.getCriteria();
        cr3 = cr3.and(new Criteria(new Column("CountTVP", "VIEWNAME"), (Object)null, 0));
        query.setCriteria(cr3);
    }
}
