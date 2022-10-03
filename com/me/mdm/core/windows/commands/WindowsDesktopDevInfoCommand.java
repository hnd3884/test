package com.me.mdm.core.windows.commands;

import java.util.ArrayList;
import org.json.JSONObject;

public class WindowsDesktopDevInfoCommand extends WpDeviceInformationCommand
{
    @Override
    protected ArrayList getWpInventoryQuery(final JSONObject jsonObject) throws Exception {
        final ArrayList list = super.getWpInventoryQuery(jsonObject);
        final Boolean fetchPhonenum = jsonObject.optBoolean("fetchPhonenum", true);
        if (fetchPhonenum) {
            list.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/CellularIdentities?list=StructData"));
        }
        else {
            final String imei = jsonObject.optString("IMEI", (String)null);
            if (imei != null) {
                list.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/CellularIdentities/" + imei + "/RoamingStatus"));
                list.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/CellularIdentities/" + imei + "/IMSI"));
                list.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/CellularIdentities/" + imei + "/CommercializationOperator"));
            }
            else {
                list.add(this.createTargetItemTagElement("./Vendor/MSFT/DeviceStatus/CellularIdentities?list=Struct"));
            }
        }
        return list;
    }
}
