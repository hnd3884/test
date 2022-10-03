package com.me.devicemanagement.framework.webclient.authentication;

import java.util.HashMap;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class UserAdministrationRoleTransformer extends DefaultTransformer
{
    private static final Logger LOGGER;
    
    public void renderCell(final TransformerContext tableContext) {
        UserAdministrationRoleTransformer.LOGGER.log(Level.FINE, "Entering RoleManagement Transformer ...");
        try {
            super.renderCell(tableContext);
            final String columnalais = tableContext.getPropertyName();
            UserAdministrationRoleTransformer.LOGGER.log(Level.FINE, "Columnalais : " + columnalais);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            if (columnalais.equals("Action")) {
                String link = "<div class='actionDropdown'><ul><div class=\"dropdown_pointer\"><img src=\"/images/dropdown_point.png\" width=\"11\" height=\"19\"></div>";
                final String id = tableContext.getAssociatedPropertyValue("UMRole.UM_ROLE_ID").toString();
                final int index = tableContext.getRowIndex();
                final String refID = tableContext.getViewContext().getReferenceId();
                link = link + "<li style=\"cursor: pointer;\" onclick=\"javascript:modifyRole( 'modifyRole','" + refID + "',''," + index + ",this);closeDialog(null,this)\" class=\"bodytext\"><img src=\"/images/modify.png\" border=\"0\" align=\"absmiddle\" alt=\"" + I18N.getMsg("dm.common.modify_role", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.common.EDIT", new Object[0]) + "\">" + I18N.getMsg("dm.common.modify_role", new Object[0]) + "</li>";
                link = link + "<li style=\"cursor: pointer;\" onclick=\"javascript:deleteRole( 'deleteRole','" + refID + "','role_id=" + id + "'," + index + ",this);closeDialog(null,this)\" class=\"bodytext\"><img src=\"/images/delete.png\" border=\"0\" align=\"absmiddle\" alt=\"" + I18N.getMsg("dm.common.delete_role", new Object[0]) + "\" title=\"" + I18N.getMsg("dc.common.DELETE", new Object[0]) + "\">" + I18N.getMsg("dm.common.delete_role", new Object[0]) + "</li>";
                link = "<a href='javascript:popup(\"" + id + "\")' ><img src=\"/images/action_dropdown.png\" width=\"20\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\"></a><div style='display:none' id='" + id + "'>" + link + "</div>";
                columnProperties.put("VALUE", link);
            }
        }
        catch (final Exception e) {
            UserAdministrationRoleTransformer.LOGGER.severe("Exception occured while rendering cell value" + e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(UserAdministrationRoleTransformer.class.getName());
    }
}
