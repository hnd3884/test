package com.adventnet.sym.webclient.admin.som;

import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import java.util.HashMap;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class SoMDefnColumnTransformer extends RolecheckerTransformer
{
    private Logger logger;
    
    public SoMDefnColumnTransformer() {
        this.logger = Logger.getLogger(SoMDefnColumnTransformer.class.getName());
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final String checkbox = tableContext.getPropertyName();
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (checkbox.equals("SomAdCompAdded.SOM_AD_COMP_ID")) {
            String checkAll = "";
            if (isExport == null) {
                checkAll = "<input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectSomAdObjects(this.form,this.checked,'added_list')\">";
            }
            headerProperties.put("VALUE", checkAll);
        }
        if (checkbox.equals("SomAdCompDeleted.RESOURCE_ID")) {
            String checkAll = "";
            if (isExport == null) {
                checkAll = "<input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectSomAdObjects(this.form,this.checked,'deleted_list')\">";
            }
            headerProperties.put("VALUE", checkAll);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final Object data = tableContext.getPropertyValue();
            final String columnalais = tableContext.getPropertyName();
            final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
            if (columnalais.equals("Resource.IS_INACTIVE")) {
                final long resource_id = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                final String domainName = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
                String resid = "";
                if (isExport == null && data != null) {
                    resid = "<a href=\"javascript:deleteDomainDetails('" + resource_id + "')\" ><img src=\"/images/delete.png\" alt=\"Delete " + domainName + " domain details\" title=\"" + I18N.getMsg("dc.som.addcomputer.deleteDomaindetails", new Object[] { domainName }) + "\"></a>";
                }
                columnProperties.put("VALUE", resid);
            }
            else if (columnalais.equals("Resource.RESOURCE_ID")) {
                final long resource_id = (long)tableContext.getAssociatedPropertyValue("Resource.RESOURCE_ID");
                String resid2 = "";
                if (isExport == null) {
                    String dcName = (String)tableContext.getAssociatedPropertyValue("ManagedDomain.DC_NAME");
                    final String domainName2 = (String)tableContext.getAssociatedPropertyValue("Resource.DOMAIN_NETBIOS_NAME");
                    if (dcName == null) {
                        dcName = "";
                    }
                    if (data != null) {
                        resid2 = "&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"javascript:editDomainDetails('" + resource_id + "')\" >" + I18N.getMsg("dc.common.EDIT", new Object[0]) + "</a>";
                    }
                }
                columnProperties.put("VALUE", resid2);
            }
            else if (columnalais.equals("SelectedComputer.SELECTED_COMPUTER_ID")) {
                String resid3 = "";
                if (isExport == null) {
                    final long sel_resource_id = (long)tableContext.getAssociatedPropertyValue("SelectedComputer.SELECTED_COMPUTER_ID");
                    final String computerName = (String)tableContext.getAssociatedPropertyValue("SelectedComputer.COMPUTER_NAME");
                    if (data != null) {
                        resid3 = "<a href=\"javascript:deleteSelectedComputer('" + sel_resource_id + "')\" alt=\"Delete " + computerName + "\"><img src=\"/images/delete.png\" ></a>&nbsp;";
                    }
                }
                columnProperties.put("VALUE", resid3);
            }
            else if (columnalais.equals("SomAdCompAdded.SOM_AD_COMP_ID")) {
                String checkbox = "";
                if (isExport == null) {
                    final Long com_id = (Long)tableContext.getAssociatedPropertyValue("SomAdCompAdded.SOM_AD_COMP_ID");
                    checkbox = "<input type=\"checkbox\" value = \"" + com_id + "\" name = \"added_list\">";
                }
                columnProperties.put("VALUE", checkbox);
            }
            else if (columnalais.equals("SomAdCompDeleted.RESOURCE_ID")) {
                String checkbox = "";
                if (isExport == null) {
                    final Long com_id = (Long)tableContext.getAssociatedPropertyValue("SomAdCompDeleted.RESOURCE_ID");
                    checkbox = "<input type=\"checkbox\" value = \"" + com_id + "\" name = \"deleted_list\">";
                }
                columnProperties.put("VALUE", checkbox);
            }
            else if (columnalais.equals("SoMADSelectedOu.SOM_AD_OU_ID")) {
                String msg = "";
                final Long ou_id = (Long)tableContext.getAssociatedPropertyValue("SoMADSelectedOu.SOM_AD_OU_ID");
                msg = "<a href=\"javascript:deleteOu('" + ou_id + "')\"><img src=\"/images/delete.png\" ></a>&nbsp;";
                columnProperties.put("VALUE", msg);
            }
            else if (columnalais.equals("SoMADSelectedOu.OU_NAME")) {
                String msg = "";
                final int type = (int)tableContext.getAssociatedPropertyValue("SoMADSelectedOu.TARGET_TYPE");
                if (type == 6) {
                    final String domainName = (String)tableContext.getAssociatedPropertyValue("SoMADSelectedOu.DOMAIN_NAME");
                    final String ouName = (String)tableContext.getAssociatedPropertyValue("SoMADSelectedOu.OU_NAME");
                    msg = ouName + " [" + domainName + "]";
                    columnProperties.put("VALUE", msg);
                }
            }
            else if (columnalais.equals("SoMADSelectedOu.TARGET_TYPE")) {
                String msg = I18N.getMsg("dc.common.DOMAIN", new Object[0]);
                final int type = (int)tableContext.getAssociatedPropertyValue("SoMADSelectedOu.TARGET_TYPE");
                if (type == 6) {
                    msg = I18N.getMsg("dc.common.ORGANIZATIONAL_UNIT", new Object[0]);
                }
                columnProperties.put("VALUE", msg);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occured while rendering cell value in SoMDefnColumnTransformer ", ex);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "--");
        }
    }
}
