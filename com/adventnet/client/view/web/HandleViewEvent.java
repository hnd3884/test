package com.adventnet.client.view.web;

import com.adventnet.client.util.web.WebClientUtil;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class HandleViewEvent extends Action implements WebConstants
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String uniqueId = WebClientUtil.getRequiredParameter("ACTION_SOURCE", request);
        final String eventType = request.getParameter("EVENT_TYPE");
        final ViewContext viewCtx = ViewContext.getViewContext(uniqueId, request);
        return viewCtx.getModel().getController().processEvent(viewCtx, request, response, eventType);
    }
}
