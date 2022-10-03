package com.me.mdm.webclient.osupdate;

import java.util.HashMap;
import com.adventnet.sym.server.mdm.group.MDMGroupHandler;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MDMOSUpdateGroupTransformer extends DefaultTransformer
{
    private Logger logger;
    
    public MDMOSUpdateGroupTransformer() {
        this.logger = Logger.getLogger("");
    }
    
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final ViewContext vc = tableContext.getViewContext();
        final HttpServletRequest request = vc.getRequest();
        final int reportType = tableContext.getViewContext().getRenderType();
        if (columnalias.equalsIgnoreCase("CustomGroup.RESOURCE_ID") || columnalias.equalsIgnoreCase("checkbox")) {
            final boolean hasWritePrivillage = request.isUserInRole("MDM_OSUpdateMgmt_Write");
            return reportType == 4 && hasWritePrivillage;
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderCell(final TransformerContext tableContext) {
        this.logger.log(Level.FINE, "Entering CustomeGroupingTransformer...");
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            this.logger.log(Level.FINE, "Columnalais : ", columnalais);
            final long resourceIdforCheck = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
            final String groupNameforCheck = (String)tableContext.getAssociatedPropertyValue("Resource.NAME");
            final int groupTypeforCheck = (int)tableContext.getAssociatedPropertyValue("CustomGroup.GROUP_TYPE");
            final boolean isEditableforCheck = (boolean)tableContext.getAssociatedPropertyValue("CustomGroup.IS_EDITABLE");
            final Long createdByUserId = (Long)tableContext.getAssociatedPropertyValue("CreatedUser.USER_ID");
            final boolean isDefaultGroup = MDMGroupHandler.getInstance().isDefaultGroup(groupNameforCheck);
            if (columnalais.equals("MEMBER_COUNT") && data == null) {
                columnProperties.put("VALUE", "0");
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception occoured in renderCell", e);
        }
    }
}
