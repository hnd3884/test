package com.adventnet.sym.webclient.mdm.android.debug;

import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class APKMigrationStatusTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (columnalias.equals("AgentMigration.RESOURCE_ID") || columnalias.equals("Action")) {
            return isExport == null || !isExport.equalsIgnoreCase("true");
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("AgentMigration.RESOURCE_ID")) {
            final String checkAll = "<table><tr><td nowrap><input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\"></td></tr></table>";
            headerProperties.put("VALUE", checkAll);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        final String columnalais = tableContext.getPropertyName();
        final Object data = tableContext.getPropertyValue();
        if (columnalais.equals("AgentMigration.RESOURCE_ID")) {
            final Long resourceID = (Long)data;
            final String actionStr = "<table><tr><td nowrap><input type=\"checkbox\" value=\"" + resourceID + "\" name=\"object_list\" onclick=\"\"></td></tr></table>";
            columnProperties.put("VALUE", actionStr);
        }
        else if (columnalais.equals("Action")) {
            final Long resourceId = (Long)tableContext.getAssociatedPropertyValue("AgentMigration.RESOURCE_ID");
            final String actionStr = "<a href=\"javascript:initiateAgentMigration('" + resourceId + "')\"><img src=\"/images/reset.png\" title=\"Delete\" class=\"menuItemImage\" width=\"10px\" height=\"10px\" border=\"0\" align=\"top\"/></a>";
            columnProperties.put("VALUE", actionStr);
        }
        else if (columnalais.equals("AgentMigration.MIGRATION_STATUS")) {
            final Integer status = (Integer)tableContext.getAssociatedPropertyValue("AgentMigration.MIGRATION_STATUS");
            String migrationStatus = "--";
            switch (status) {
                case -1: {
                    if (isExport == null) {
                        migrationStatus = "<img src=\"/images/not-yet-deployed.png\" title=" + I18N.getMsg("dc.common.YET_TO_UPDATE", new Object[0]) + " width=\"13\" height=\"13\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\">" + I18N.getMsg("dc.common.YET_TO_UPDATE", new Object[0]) + "</span>";
                        break;
                    }
                    migrationStatus = I18N.getMsg("dc.common.YET_TO_UPDATE", new Object[0]);
                    break;
                }
                case 0: {
                    if (isExport == null) {
                        migrationStatus = "<img src=\"/images/success.png\" title=" + I18N.getMsg("dc.common.SUCCESS", new Object[0]) + " width=\"13\" height=\"13\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\">" + I18N.getMsg("dc.common.SUCCESS", new Object[0]) + "</span>";
                        break;
                    }
                    migrationStatus = I18N.getMsg("dc.common.SUCCESS", new Object[0]);
                    break;
                }
                case 1: {
                    if (isExport == null) {
                        migrationStatus = "<img src=\"/images/status_inprog.gif\" title=" + I18N.getMsg("dc.common.status.in_progress", new Object[0]) + " width=\"13\" height=\"13\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\">" + I18N.getMsg("dc.common.status.in_progress", new Object[0]) + "</span>";
                        break;
                    }
                    migrationStatus = I18N.getMsg("dc.common.status.in_progress", new Object[0]);
                    break;
                }
                case 2: {
                    if (isExport == null) {
                        migrationStatus = "<img src=\"/images/error_icon.gif\" title=" + I18N.getMsg("dc.db.config.status.failed", new Object[0]) + " width=\"16\" height=\"16\" hspace=\"3\" vspace=\"0\" align=\"absmiddle\" ><span class=\"bodytextbig\">" + I18N.getMsg("dc.db.config.status.failed", new Object[0]) + "</span>";
                        break;
                    }
                    migrationStatus = I18N.getMsg("dc.db.config.status.failed", new Object[0]);
                    break;
                }
            }
            columnProperties.put("VALUE", migrationStatus);
        }
        else if (columnalais.equalsIgnoreCase("AgentMigration.REMARKS")) {
            final Integer status = (Integer)tableContext.getAssociatedPropertyValue("AgentMigration.MIGRATION_STATUS");
            if (status == 2) {
                SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, data, (String)null, true);
            }
            else {
                columnProperties.put("VALUE", I18N.getMsg((String)data, new Object[0]));
            }
        }
    }
}
