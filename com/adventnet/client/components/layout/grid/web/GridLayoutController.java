package com.adventnet.client.components.layout.grid.web;

import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.persistence.Row;
import com.adventnet.client.view.UserPersonalizationAPI;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.layout.web.ChildIterator;
import com.adventnet.client.util.DataUtils;
import java.util.List;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.personalize.web.PersonalizableView;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.components.layout.web.ContainerController;

public class GridLayoutController extends ContainerController implements WebConstants, PersonalizableView
{
    private static final String SORTEDGRIDLIST = "SORTEDGRIDLIST";
    
    public void updateViewModel(final ViewContext viewCtx) throws Exception {
        viewCtx.setViewModel((Object)new GridModel(viewCtx, this.getChildList(viewCtx)));
    }
    
    public JSONObject getModelAsJSON(final ViewContext vc) throws Exception {
        return GridLayoutUtil.getAsJSON(vc);
    }
    
    protected List getChildList(final ViewContext viewCtx) throws Exception {
        List childConfigList = (List)viewCtx.getModel().getCompiledData((Object)"SORTEDGRIDLIST");
        if (childConfigList == null) {
            childConfigList = DataUtils.getSortedList(viewCtx.getModel().getViewConfiguration(), "ACGridLayoutChildConfig", "CHILDINDEX");
            viewCtx.getModel().addCompiledData((Object)"SORTEDGRIDLIST", (Object)childConfigList);
        }
        return childConfigList;
    }
    
    @Override
    public ChildIterator getChildIterator(final ViewContext vc) throws Exception {
        vc.getViewModel();
        return ((GridModel)vc.getViewModel()).getIterator();
    }
    
    public void createViewFromTemplate(final DataObject viewDataObj, final long accountId) throws Exception {
        PersonalizationUtil.createChildViewsFromTemplate(viewDataObj, accountId, "ACGridLayoutChildConfig", 2);
    }
    
    public void addView(final String viewName, final String newChildVewName, final long accountId, final HttpServletRequest request) throws Exception {
        final DataObject dataObject = UserPersonalizationAPI.getPersonalizedView((Object)viewName, accountId);
        final int index = DataUtils.getMaxIndex(dataObject, "ACTabChildConfig", 3);
        final Row newGrid = new Row("ACTabChildConfig");
        newGrid.set(1, dataObject.getFirstValue("ViewConfiguration", 1));
        newGrid.set(2, (Object)WebViewAPI.getViewNameNo((Object)newChildVewName));
        newGrid.set(3, (Object)new Integer(index + 1));
        dataObject.addRow(newGrid);
        UserPersonalizationAPI.updatePersonalizedView((WritableDataObject)dataObject, accountId);
    }
    
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        if (eventType.equals("CLOSECHILDVIEW")) {
            this.deleteView(viewCtx, request, response);
        }
        return super.processEvent(viewCtx, request, response, eventType);
    }
    
    public void deleteView(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        PersonalizationUtil.deleteView(viewCtx.getModel().getViewName(), WebClientUtil.getRequiredParameter("CLOSEDVIEW", request), WebClientUtil.getAccountId(), "ACGridLayoutChildConfig", 2);
    }
    
    public void savePreferences(final ViewContext viewCtx) throws Exception {
        final List newList = (List)viewCtx.getStateParameter("GDLIST");
        if (newList != null) {
            final String[] newListArr = newList.toArray(new String[newList.size()]);
            GridLayoutPersController.updateGridForAccount(viewCtx.getModel().getViewName(), WebClientUtil.getAccountId(), viewCtx.getRequest(), newListArr, null, viewCtx.getPreviousState());
        }
        else {
            ContainerController.savePreferences(viewCtx, "ACGridLayoutChildConfig");
        }
    }
}
