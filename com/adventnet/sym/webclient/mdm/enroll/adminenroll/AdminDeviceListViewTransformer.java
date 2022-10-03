package com.adventnet.sym.webclient.mdm.enroll.adminenroll;

import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.util.DateTimeUtil;
import java.util.Hashtable;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.adventnet.client.components.web.TransformerContext;
import com.me.devicemanagement.framework.webclient.authorization.RolecheckerTransformer;

public class AdminDeviceListViewTransformer extends RolecheckerTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        final String columnalias = tableContext.getPropertyName();
        final String isExport = MDMApiFactoryProvider.getMDMTableViewAPI().getIsExport(tableContext);
        if (columnalias.equals("AaaLogin.DOMAINNAME") && CustomerInfoUtil.getInstance().isMSP()) {
            return false;
        }
        if (columnalias.equals("Action")) {
            return isExport == null || !isExport.equalsIgnoreCase("true");
        }
        return super.checkIfColumnRendererable(tableContext);
    }
    
    public void renderHeader(final TransformerContext tableContext) {
        super.renderHeader(tableContext);
        final HashMap headerProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("AndroidAdminDeviceDetails.ADMIN_DEVICE_ID")) {
            final String checkAll = "<table><tr><td nowrap><input type=\"checkbox\" id=\"selectAll\" value=\"SelectAll\" name=\"selectcheckbox\" onclick=\"javascript:selectAllObjects(this.checked)\"></td></tr></table>";
            headerProperties.put("VALUE", checkAll);
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        final Object data = tableContext.getPropertyValue();
        if (columnalais.equals("Action")) {
            final String udid = (String)tableContext.getAssociatedPropertyValue("AndroidAdminDeviceDetails.UDID");
            final String actionStr = "<a href=\"javascript:removeAdminDevice('" + udid + "')\"><img src=\"/images/delete_1.png\" title=\"Delete\" class=\"menuItemImage\" style=\"width:10px;height:10px\" border=\"0\" align=\"top\"/></a>";
            columnProperties.put("VALUE", actionStr);
        }
        if (columnalais.equals("AndroidAdminDeviceDetails.LAST_CONTACTED_TIME")) {
            final Long lastContactTime = (Long)data;
            Hashtable ht = new Hashtable();
            ht = DateTimeUtil.determine_From_To_Times("today");
            final Long today = ht.get("date1");
            String isactive = null;
            if (lastContactTime >= today) {
                isactive = I18N.getMsg("dc.wc.inv.common.Active", new Object[0]);
            }
            else {
                isactive = I18N.getMsg("dc.mdm.knox.container.status.inactive", new Object[0]);
            }
            columnProperties.put("VALUE", isactive);
        }
        if (columnalais.equals("AaaLogin.DOMAINNAME")) {
            final String value = columnProperties.get("VALUE").toString();
            columnProperties.put("VALUE", value.equalsIgnoreCase("-") ? "Local" : value);
        }
        if (columnalais.equals("AaaUserContactInfo.EMAILID")) {
            if (data == "") {
                columnProperties.put("VALUE", "--");
            }
            else {
                columnProperties.put("VALUE", data);
            }
        }
    }
}
