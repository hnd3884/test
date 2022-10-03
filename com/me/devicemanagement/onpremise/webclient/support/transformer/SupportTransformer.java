package com.me.devicemanagement.onpremise.webclient.support.transformer;

import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Utils;
import com.me.devicemanagement.onpremise.server.authentication.DMOnPremiseUserHandler;
import com.adventnet.i18n.I18N;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class SupportTransformer extends DefaultTransformer
{
    public void renderCell(final TransformerContext tableContext) throws Exception {
        super.renderCell(tableContext);
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        final String columnalais = tableContext.getPropertyName();
        if (columnalais.equals("BUILD_TYPE")) {
            String columnValue = tableContext.getAssociatedPropertyValue("BUILD_TYPE").toString();
            String description = tableContext.getAssociatedPropertyValue("REMARKS").toString();
            final String buildno = tableContext.getAssociatedPropertyValue("BUILD_NUMBER").toString();
            switch (Integer.parseInt(columnValue)) {
                case 1: {
                    columnValue = I18N.getMsg("dm.support.transformer.freshinstallation", new Object[0]);
                    break;
                }
                case 2: {
                    final int buildNumber = Integer.valueOf(buildno);
                    final int version = buildNumber % 1000;
                    if (version <= 20) {
                        columnValue = I18N.getMsg("dm.support.transformer.servicepack", new Object[0]);
                        break;
                    }
                    columnValue = I18N.getMsg("dm.support.transformer.hotfix", new Object[0]);
                    break;
                }
                case 3: {
                    description = description.substring(description.indexOf("PatchDescription") + 17, description.indexOf("type=JarPPM") - 2);
                    columnValue = I18N.getMsg("dm.support.transformer.quickfix", new Object[0]) + "<img src='/images/help_small.gif' style=\"position:relative;top:5px;\" height=\"17px\" onmouseover=\"return overlib('" + description + "', WIDTH , ' " + description.length() + "',FGCOLOR, '#ffffff' , BGCOLOR, '#b8b7b3');\" onmouseout=\"return nd();\" />";
                    break;
                }
            }
            columnProperties.put("VALUE", columnValue);
        }
        else if (columnalais.equals("BUILD_DETECTED_AT")) {
            final String columnValue = tableContext.getAssociatedPropertyValue("BUILD_DETECTED_AT").toString();
            columnProperties.put("VALUE", Utils.longdateToString(Long.parseLong(columnValue), DMOnPremiseUserHandler.getUserTimeFormat()));
        }
    }
}
