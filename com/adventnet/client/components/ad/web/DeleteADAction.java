package com.adventnet.client.components.ad.web;

import java.util.Map;
import com.adventnet.client.view.web.WebViewAPI;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.Row;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import java.util.logging.Logger;
import com.adventnet.client.util.web.WebConstants;
import org.apache.struts.action.Action;

public class DeleteADAction extends Action implements WebConstants
{
    private static Logger logger;
    
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final String ad_id = request.getParameter("AD_ID");
        final Row adRow = new Row("ActiveDirectoryInfo");
        adRow.set("AD_ID", (Object)new Long(ad_id));
        DataAccess.delete(adRow);
        return WebViewAPI.sendResponse(request, response, true, "successfully deleted", (Map)null);
    }
    
    static {
        DeleteADAction.logger = Logger.getLogger(DeleteADAction.class.getName());
    }
}
