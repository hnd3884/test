package com.me.mdm.webclient.transformer;

import javax.servlet.http.HttpServletRequest;
import com.me.mdm.server.role.RBDAUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.util.UrlReplacementUtil;
import com.adventnet.sym.webclient.mdm.device.MDMDevicetoProfileTransformer;
import org.json.JSONObject;
import com.adventnet.client.view.web.ViewContext;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.mdm.webclient.i18n.MDMI18N;
import java.util.HashMap;
import com.adventnet.client.components.web.TransformerContext;
import java.util.logging.Logger;

public class TransformerUtil
{
    public static Logger logger;
    
    public static void renderRemarks(final TransformerContext tableContext, final HashMap columnProperties, final String keyWithargs, final boolean isError, final boolean isPlainText) throws Exception {
        String value = MDMI18N.getMsg(keyWithargs, isPlainText);
        try {
            final ViewContext vc = tableContext.getViewContext();
            final String viewname = vc.getUniqueId();
            if (!isPlainText) {
                SYMClientUtil.renderRemarksWithKB(tableContext, columnProperties, (Object)value, (String)null, !isPlainText && isError);
                value = columnProperties.get("VALUE");
                value = MDMUtil.replaceProductUrlLoaderValuesinText(value, viewname);
            }
        }
        catch (final Exception ex) {
            TransformerUtil.logger.log(Level.SEVERE, "Exception occured in renderRemarks", ex);
        }
        columnProperties.put("VALUE", value);
    }
    
    public static void renderRemarksAsText(final TransformerContext tableContext, final HashMap columnProperties, String keyWithargs, final boolean isError, final boolean isPlainText) throws Exception {
        String value = "--";
        if (keyWithargs != null) {
            if (keyWithargs.contains("LongTime:")) {
                final String[] split = keyWithargs.split("LongTime:");
                final Long expDate = Long.parseLong(split[split.length - 1]);
                keyWithargs = split[0] + MDMUtil.getDate((long)expDate);
            }
            value = MDMI18N.getMsg(keyWithargs, isPlainText, false);
        }
        if (!isPlainText) {
            JSONObject payloadData = new JSONObject();
            final ViewContext vc = tableContext.getViewContext();
            final String viewname = vc.getUniqueId();
            payloadData.put("VALUE", (Object)value);
            if (viewname.equals("mdmDeviceApps")) {
                payloadData = MDMDevicetoProfileTransformer.addAccountTroubleshootDataForApp(tableContext, payloadData, keyWithargs);
            }
            payloadData.put("ISERROR", isError);
            String kbURL = (String)tableContext.getAssociatedPropertyValue("ErrorCodeToKBUrl.KB_URL");
            if (kbURL != null) {
                kbURL = UrlReplacementUtil.replaceUrlAndAppendTrackCode(kbURL);
                payloadData.put("READKB", (Object)MDMUtil.replaceProductUrlLoaderValuesinText(kbURL, null));
            }
            columnProperties.put("PAYLOAD", payloadData);
        }
        else {
            renderRemarks(tableContext, columnProperties, keyWithargs, isError, isPlainText);
        }
        columnProperties.put("VALUE", value);
    }
    
    public static Object getPreValuesForTransformer(final ViewContext viewContext, final String value) {
        final HashMap hashMap = (HashMap)viewContext.getRequest().getAttribute("TRANSFORMER_PRE_DATA");
        if (hashMap != null) {
            return hashMap.containsKey(value) ? hashMap.get(value) : null;
        }
        return null;
    }
    
    public static boolean hasUserAllDeviceScopeGroup(final ViewContext viewContext, final Boolean isGroup) {
        try {
            final HttpServletRequest request = viewContext.getRequest();
            final Boolean isMDMAdmin = request.isUserInRole("All_Managed_Mobile_Devices");
            final Long loginId = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            final Boolean isRBDAGroupCheck = RBDAUtil.getInstance().hasRBDAGroupCheck(loginId, isGroup);
            return isMDMAdmin || isRBDAGroupCheck;
        }
        catch (final Exception ex) {
            TransformerUtil.logger.log(Level.SEVERE, "Exception while checking logged in user has all managed device scope or All managed device group", ex);
            return false;
        }
    }
    
    static {
        TransformerUtil.logger = Logger.getLogger(TransformerUtil.class.getName());
    }
}
