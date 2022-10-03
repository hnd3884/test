package com.me.devicemanagement.framework.webclient.navigation;

import com.adventnet.client.view.web.ViewController;
import java.util.logging.Level;
import com.adventnet.client.ClientException;
import com.adventnet.client.ClientErrorCodes;
import com.adventnet.client.components.table.web.TableViewController;
import com.adventnet.client.view.web.ViewContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class DMGetCount extends HttpServlet
{
    private static final Logger LOGGER;
    
    public void doPost(final HttpServletRequest servReq, final HttpServletResponse servResp) {
        this.doGet(servReq, servResp);
    }
    
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final String viewName = request.getParameter("viewName");
        final ViewContext vc = ViewContext.getViewContext((Object)viewName, request);
        final ViewController viewCont = vc.getModel().getController();
        try {
            if (!(viewCont instanceof TableViewController)) {
                throw new ClientException(ClientErrorCodes.COUNT_NOT_SUPPORTED);
            }
            final long count = ((TableViewController)viewCont).getCount(vc);
            response.getWriter().println(count);
        }
        catch (final Exception ex) {
            DMGetCount.LOGGER.log(Level.SEVERE, "Exception occurred while fetching count for the view");
        }
    }
    
    static {
        LOGGER = Logger.getLogger(DMGetCount.class.getName());
    }
}
