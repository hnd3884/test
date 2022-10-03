package com.adventnet.client.components.personalize.web;

import com.adventnet.client.view.web.ViewController;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.view.web.DefaultViewController;

public class AddNewTabController extends DefaultViewController
{
    public ActionForward processEvent(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String eventType) throws Exception {
        final String targetTabName = WebClientUtil.getRequiredParameter("VIEWNAME", request);
        final String newViewTitle = WebClientUtil.getRequiredParameter("NEWVIEWTITLE", request);
        final String dummyViewName = WebClientUtil.getRequiredParameter("VIEWTYPE", request);
        if (dummyViewName == null) {
            throw new RuntimeException("Unknown View Type " + dummyViewName + " selected ");
        }
        final WritableDataObject viewConfig = PersonalizationUtil.getNewViewFromExisting(newViewTitle, dummyViewName, true, WebClientUtil.getAccountId());
        final ViewController vc = WebViewAPI.getViewController((DataObject)viewConfig);
        final String childViewName = (String)viewConfig.getFirstValue("ViewConfiguration", 2);
        if (vc instanceof PersonalizableView) {
            ((PersonalizableView)vc).createViewFromTemplate((DataObject)viewConfig, WebClientUtil.getAccountId());
        }
        final PersonalizableView pvc = (PersonalizableView)WebViewAPI.getViewController(WebViewAPI.getViewConfiguration((Object)targetTabName));
        LookUpUtil.getPersistence().add((DataObject)viewConfig);
        pvc.addView(targetTabName, childViewName, WebClientUtil.getAccountId(), request);
        return DynamicContentAreaAPI.closeView(viewCtx, request);
    }
}
