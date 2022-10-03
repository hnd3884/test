package com.me.mdm.server.windows.profile.payload;

public class WinMobilePasscodePayload extends WindowsPasscodePayload
{
    public WinMobilePasscodePayload() {
        this.keyPrefix = "./Vendor/MSFT/Policy/Config/DeviceLock";
    }
}
