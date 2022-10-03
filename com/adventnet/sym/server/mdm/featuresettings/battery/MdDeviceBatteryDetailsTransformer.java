package com.adventnet.sym.server.mdm.featuresettings.battery;

import java.util.Locale;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.util.Utils;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.mdm.server.settings.battery.MdDeviceBatteryDetailsDBHandler;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.components.table.web.DefaultTransformer;

public class MdDeviceBatteryDetailsTransformer extends DefaultTransformer
{
    protected boolean checkIfColumnRendererable(final TransformerContext tableContext) throws Exception {
        try {
            final ViewContext viewCtx = tableContext.getViewContext();
            final HttpServletRequest request = viewCtx.getRequest();
            final String columnalias = tableContext.getPropertyName();
            final String deviceLocalTime = request.getParameter("MdDeviceBatteryDetails.DEVICE_LOCAL_TIME");
            if (columnalias.equals("MdDeviceBatteryDetails.DEVICE_LOCAL_TIME")) {
                return deviceLocalTime == null || deviceLocalTime.isEmpty();
            }
            return super.checkIfColumnRendererable(tableContext);
        }
        catch (final Exception ex) {
            throw ex;
        }
    }
    
    public void renderCell(final TransformerContext tableContext) throws Exception {
        final String columnalais = tableContext.getPropertyName();
        final HashMap columnProperties = tableContext.getRenderedAttributes();
        if (columnalais.equals("MdDeviceBatteryDetails.DEVICE_LOCAL_TIME")) {
            final String dateString = (String)tableContext.getAssociatedPropertyValue("MdDeviceBatteryDetails.DEVICE_LOCAL_TIME");
            if (!MDMStringUtils.isEmpty(dateString)) {
                final Long deviceLocalTime = MdDeviceBatteryDetailsDBHandler.convertDateToMilliseconds(dateString);
                if (deviceLocalTime != null) {
                    final String dateFormat = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeFormat();
                    final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
                    final Date date = new Date(deviceLocalTime);
                    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
                    final String formattedDate = simpleDateFormat.format(date);
                    columnProperties.put("VALUE", formattedDate);
                }
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
        if (columnalais.equals("MdDeviceBatteryDetails.DEVICE_UTC_TIME")) {
            final Long dateLong = (Long)tableContext.getAssociatedPropertyValue("MdDeviceBatteryDetails.DEVICE_UTC_TIME");
            if (dateLong != null && dateLong != -1L) {
                final String dateFormat2 = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeFormat();
                final Locale locale2 = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
                final TimeZone timeZone = TimeZone.getTimeZone(ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZoneID());
                final String columnValue = Utils.getTime(dateLong, dateFormat2, locale2, timeZone);
                columnProperties.put("VALUE", columnValue);
            }
            else {
                columnProperties.put("VALUE", "--");
            }
        }
    }
}
