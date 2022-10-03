package com.adventnet.client.components.cthelp.web;

import com.adventnet.persistence.DataObject;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.client.util.LookUpUtil;
import com.adventnet.persistence.Row;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class CTHelpActionHandler extends Action
{
    String noHelpPath;
    
    public CTHelpActionHandler() {
        this.noHelpPath = "/help/NoHelp.html";
    }
    
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final Row r = new Row("ACContextHelp");
        r.set("TARGET", (Object)request.getParameter("TARGET"));
        final DataObject ctDO = LookUpUtil.getPersistence().get("ACContextHelp", r);
        String path = this.noHelpPath;
        if (ctDO.containsTable("ACContextHelp")) {
            path = (String)ctDO.getFirstValue("ACContextHelp", "URL");
        }
        response.setContentType("text/html");
        response.getWriter().print("<meta http-equiv=\"refresh\" content=\"0;url=" + IAMEncoder.encodeURL(path) + "\">");
        response.getWriter().flush();
        return null;
    }
}
