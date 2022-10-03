package com.adventnet.client.usermgmt.web;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AddUserEdit extends DefaultTransformer
{
    private Logger logger;
    
    public AddUserEdit() {
        this.logger = Logger.getLogger(AddUserEdit.class.getName());
    }
    
    @Override
    public void renderCell(final TransformerContext context) {
        try {
            final ViewContext vc = context.getViewContext();
            final HttpServletRequest request = vc.getRequest();
            final String userName = request.getUserPrincipal().getName();
            super.renderCell(context);
            final TableTransformerContext tableContext = (TableTransformerContext)context;
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String loginName = (String)context.getAssociatedPropertyValue("NAME1");
            if (loginName != null && loginName.equals("admin") && !loginName.equals(userName)) {
                columnProperties.put("VALUE", "");
                columnProperties.put("ICON", null);
                columnProperties.put("LINK", null);
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in AddUserEdit while rendering cell", e);
        }
    }
}
