package com.adventnet.sym.webclient.mdm.user;

import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Logger;
import com.adventnet.client.components.form.web.AjaxFormController;

public class MDMDevicesListForUserController extends AjaxFormController
{
    public static Logger logger;
    
    public String processPreRendering(final ViewContext viewCtx, final HttpServletRequest request, final HttpServletResponse response, final String viewUrl) throws Exception {
        try {
            super.processPreRendering(viewCtx, request, response, viewUrl);
            request.setAttribute("userName", (Object)request.getParameter("userName"));
            request.setAttribute("domainName", (Object)request.getParameter("domainName"));
            request.setAttribute("deviceCount", (Object)request.getParameter("deviceCount"));
            request.setAttribute("userId", (Object)request.getParameter("userId"));
            if (Boolean.parseBoolean(request.getParameter("groupsOrDevices"))) {
                request.setAttribute("showRemoveUser", (Object)false);
            }
            else {
                request.setAttribute("showRemoveUser", (Object)true);
            }
        }
        catch (final Exception ex) {
            MDMDevicesListForUserController.logger.log(Level.SEVERE, "Exception occured in processPreRendering -  MDMDevicesListForUserController {0}", ex);
        }
        return viewUrl;
    }
    
    static {
        MDMDevicesListForUserController.logger = Logger.getLogger("MDMConfigLogger");
    }
}
