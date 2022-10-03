package com.adventnet.sym.webclient.mdm.enroll.adep;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import com.adventnet.i18n.I18N;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class AppleDEPTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final ViewContext viewCtx = tableContext.getViewContext();
            final HttpServletRequest request = viewCtx.getRequest();
            final String columnalias = tableContext.getPropertyName();
            final int reportType = tableContext.getViewContext().getRenderType();
            if (columnalias.equals("Action") && reportType != 4) {
                return false;
            }
            if (columnalias.equals("DOMAIN_NAME") && CustomerInfoUtil.getInstance().isMSP()) {
                return Boolean.FALSE;
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        try {
            super.renderCell(tableContext);
            final HashMap columnProperties = tableContext.getRenderedAttributes();
            final String columnalais = tableContext.getPropertyName();
            final Long userId = (Long)tableContext.getAssociatedPropertyValue("ManagedUser.MANAGED_USER_ID");
            if (columnalais.equals("Action")) {
                String assignUserTxt = null;
                if (userId != null) {
                    assignUserTxt = I18N.getMsg("dc.mdm.enroll.action.reassign_user", new Object[0]);
                }
                else {
                    assignUserTxt = I18N.getMsg("dc.mdm.dep.assign_device", new Object[0]);
                }
                final JSONObject actionDetails = new JSONObject();
                actionDetails.put((Object)"assignUserTxt", (Object)assignUserTxt);
                columnProperties.put("PAYLOAD", actionDetails);
            }
            if (columnalais.equals("AppleDEPDeviceForEnrollment.DEVICE_MODEL")) {
                Integer model = (Integer)tableContext.getAssociatedPropertyValue("MdModelInfo.MODEL_TYPE");
                String val = "--";
                if (model != null) {
                    switch (model) {
                        case 2: {
                            val = I18N.getMsg("dc.mdm.actionlog.appmgmt.ipad", new Object[0]);
                            break;
                        }
                        case 1: {
                            val = I18N.getMsg("dc.mdm.actionlog.appmgmt.iphone", new Object[0]);
                            break;
                        }
                        case 0: {
                            val = I18N.getMsg("mdm.os.ipod", new Object[0]);
                            break;
                        }
                        case 3:
                        case 4: {
                            val = I18N.getMsg("mdm.os.mac", new Object[0]);
                            break;
                        }
                        case 5: {
                            val = I18N.getMsg("mdm.os.tvos", new Object[0]);
                            break;
                        }
                    }
                }
                else {
                    model = (Integer)tableContext.getAssociatedPropertyValue("AppleDEPDeviceForEnrollment.DEVICE_MODEL");
                    if (model != null) {
                        switch (model) {
                            case 1: {
                                val = I18N.getMsg("dc.mdm.actionlog.appmgmt.ipad", new Object[0]);
                                break;
                            }
                            case 2: {
                                val = I18N.getMsg("dc.mdm.actionlog.appmgmt.iphone", new Object[0]);
                                break;
                            }
                            case 3: {
                                val = I18N.getMsg("mdm.os.ipod", new Object[0]);
                                break;
                            }
                            case 4: {
                                val = I18N.getMsg("mdm.os.mac", new Object[0]);
                                break;
                            }
                            case 5: {
                                val = I18N.getMsg("mdm.os.tvos", new Object[0]);
                                break;
                            }
                        }
                    }
                }
                columnProperties.put("VALUE", val);
            }
        }
        catch (final Exception e) {
            final Logger logger = Logger.getLogger("MDMEnrollment");
            logger.log(Level.WARNING, "Exception in AppleDEPTransformer.. {0}", this.columnAlias);
            logger.log(Level.WARNING, "Exception in AppleDEPTransformer.. ", e);
            final HashMap columnProperties2 = tableContext.getRenderedAttributes();
            columnProperties2.put("VALUE", "---");
        }
    }
}
