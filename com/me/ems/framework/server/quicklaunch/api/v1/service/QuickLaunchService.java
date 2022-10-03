package com.me.ems.framework.server.quicklaunch.api.v1.service;

import java.util.Iterator;
import org.json.simple.JSONArray;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.EMSServerUtil;
import com.me.ems.framework.server.quicklaunch.core.QuickLaunchUtil;
import com.me.devicemanagement.framework.server.util.EMSProductUtil;
import java.util.Map;
import java.util.List;
import com.me.ems.framework.uac.api.v1.model.User;
import java.util.logging.Logger;

public class QuickLaunchService
{
    private static Logger logger;
    
    public List<Map<String, Object>> getQuickLaunchLinks(final User user) throws APIException {
        try {
            final String productCode = EMSProductUtil.getEMSProductCode().get(0).toString();
            String basePath = "";
            final JSONArray configArray = QuickLaunchUtil.getConfigArray();
            for (final Object object : configArray) {
                final Map productMap = (Map)object;
                final String allowedProductCodes = productMap.getOrDefault("productCodes", "");
                if (allowedProductCodes.contains(productCode)) {
                    final String emsServerType = productMap.getOrDefault("emsServerType", "");
                    if (emsServerType == null || emsServerType.equals("") || EMSServerUtil.isMatchingServerType(emsServerType)) {
                        basePath = productMap.get("filePath");
                        break;
                    }
                    continue;
                }
            }
            if (basePath == null || basePath.equals("")) {
                QuickLaunchService.logger.log(Level.SEVERE, "No file available for this product code in quicklaunch-configurations.json");
                throw new APIException("GENERIC0002", "No file available for this product code in quicklaunch-configurations", new String[0]);
            }
            return QuickLaunchUtil.getQuickLaunchLinks(basePath, user);
        }
        catch (final Exception ex) {
            QuickLaunchService.logger.log(Level.SEVERE, "Error in QuickLaunchService :: getQuickLaunchLinks()", ex);
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
    }
    
    static {
        QuickLaunchService.logger = Logger.getLogger(QuickLaunchService.class.getName());
    }
}
