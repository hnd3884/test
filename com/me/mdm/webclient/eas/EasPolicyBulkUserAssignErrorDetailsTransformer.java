package com.me.mdm.webclient.eas;

import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class EasPolicyBulkUserAssignErrorDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final ViewContext vc = tableContext.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final Boolean isEmailAddress = (Boolean)request.getAttribute("IsEmailInCSV");
            if (tableContext.getPropertyName().equals("EASCsvTableAlias.EMAIL_ADDRESS")) {
                return isEmailAddress;
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
}
