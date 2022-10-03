package com.adventnet.client.components.tpl;

import com.adventnet.persistence.DataObject;
import com.adventnet.client.components.tpl.service.TplTablePopulator;
import org.apache.struts.action.ActionForward;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.Action;

public class RePopulateTemplateHashAction extends Action
{
    public ActionForward execute(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        final TplTablePopulator pop = new TplTablePopulator();
        pop.create(null);
        response.getOutputStream().write("Done".getBytes());
        return null;
    }
}
