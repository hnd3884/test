package com.adventnet.client.components.quicklinks.web;

import java.util.List;
import com.adventnet.client.view.dynamiccontentarea.web.DynamicContentAreaAPI;
import com.adventnet.persistence.DataObject;
import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import com.adventnet.client.components.util.web.PersonalizationUtil;
import com.adventnet.client.view.web.StateAPI;
import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class QuickLinksCreatorAction extends Action implements WebConstants
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        try {
            final String viewName = WebClientUtil.getRequiredParameter("VIEWNAME", request);
            final String rootViewId = (String)StateAPI.getRequestState("_RVID");
            final String title = WebClientUtil.getRequiredParameter("LINKTITLE", request);
            final long accountId = WebClientUtil.getAccountId();
            final String[] dacs = request.getParameterValues("DACNAME");
            final String linkId = PersonalizationUtil.genNameFromTitle(title) + "_A_" + accountId;
            final Row menuItemRow = new Row("MenuItem");
            menuItemRow.set(2, (Object)linkId);
            if (LookUpUtil.getPersistence().get("MenuItem", menuItemRow).containsTable("MenuItem")) {
                throw new IllegalArgumentException("Already a link with the name " + title + " exists");
            }
            final DataObject linkDO = (DataObject)new WritableDataObject();
            menuItemRow.set(4, (Object)title);
            linkDO.addRow(menuItemRow);
            for (int i = 0; i < dacs.length; ++i) {
                this.addOPVRows(linkId, rootViewId, dacs[i], linkDO, request);
            }
            final ViewContext vc = ViewContext.getViewContext((Object)viewName, (Object)viewName, request);
            ((LinkViewController)vc.getModel().getController()).addAction(vc, linkDO, WebClientUtil.getAccountId(), request);
            return WebViewAPI.sendResponse(request, response, true, "Link (" + title + ") successfully added.", (Map)null);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            return WebViewAPI.sendResponse(request, response, false, "Failed to add Link : " + ex.getMessage(), (Map)null);
        }
    }
    
    public void addOPVRows(final String linkId, final String rootViewId, final String dacName, final DataObject linkDO, final HttpServletRequest request) throws Exception {
        final List dacList = DynamicContentAreaAPI.getDynamicContentAreaModel(request, dacName).getContentList();
        if (dacList.size() == 0) {
            return;
        }
        for (int i = 0; i < dacList.size(); ++i) {
            final Row opvRow = new Row("OpenViewInContentArea");
            final ViewContext vc = dacList.get(i);
            opvRow.set(1, linkDO.getFirstValue("MenuItem", 1));
            opvRow.set(2, (Object)vc.getModel().getViewNameNo());
            opvRow.set(3, (Object)rootViewId);
            opvRow.set(4, (Object)dacName);
            opvRow.set(5, (Object)(i > 0));
            opvRow.set(6, vc.getStateOrURLStateParameter("_D_RP"));
            opvRow.set(8, (Object)new Integer(i));
            opvRow.set(7, (Object)Boolean.FALSE);
            linkDO.addRow(opvRow);
        }
    }
}
