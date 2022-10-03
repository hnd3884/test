package com.adventnet.client.components.form.web;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.util.web.WebConstants;
import com.adventnet.client.view.web.DefaultViewController;

public class AjaxFormController extends DefaultViewController implements WebConstants
{
    public void processPostRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        response.getWriter().println(FormAPI.getAjaxFormScript(viewCtx));
    }
}
