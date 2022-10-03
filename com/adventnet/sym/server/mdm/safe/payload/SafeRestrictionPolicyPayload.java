package com.adventnet.sym.server.mdm.safe.payload;

import org.json.JSONException;
import com.adventnet.sym.server.mdm.android.payload.AndroidPayload;

public class SafeRestrictionPolicyPayload extends AndroidPayload
{
    public SafeRestrictionPolicyPayload(final String payloadVersion, final String payloadIdentifier, final String payloadDisplayName) throws JSONException {
        super(payloadVersion, "Restrictions", payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowBluetooth(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBluetooth", value);
    }
    
    public void setAllowBluetoothTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBluetoothTethering", value);
    }
    
    public void setAllowTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowTethering", value);
    }
    
    public void setAllowFactoryReset(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowFactoryReset", value);
    }
    
    public void setAllowSettings(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSettings", value);
    }
    
    public void setAllowWiFi(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowWiFi", value);
    }
    
    public void setAllowWiFiTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowWiFiTethering", value);
    }
    
    public void setAllowBackgroundData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowBackgroundData", value);
    }
    
    public void setAllowGoogleBackup(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowGoogleBackup", value);
    }
    
    public void setAllowCamera(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowCamera", value);
    }
    
    public void setAllowCellularData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowCellularData", value);
    }
    
    public void setAllowClipboard(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowClipboard", value);
    }
    
    public void setAllowNFC(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowNFC", value);
    }
    
    public void setAllowMicrophone(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowMicroPhone", value);
    }
    
    public void setAllowMockLocation(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowMockLocation", value);
    }
    
    public void setAllowScreenCapture(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowScreenCapture", value);
    }
    
    public void setAllowSDCard(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSDCard", value);
    }
    
    public void setAllowUSBDebug(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSBDebug", value);
    }
    
    public void setAllowUSB(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSB", value);
    }
    
    public void setAllowUSBTethering(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSBTethering", value);
    }
    
    public void setAllowUSBMediaPlayer(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUSBMediaPlayer", value);
    }
    
    public void setAllowVPN(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowVPN", value);
    }
    
    public void setAllowGoogleCrashReport(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowGoogleCrashReport", value);
    }
    
    public void setAllowOTAUpgrade(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowOTAUpgrade", value);
    }
    
    public void setAllowPowerOff(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowPowerOff", value);
    }
    
    public void setAllowSDCardWrite(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowSDCardWrite", value);
    }
    
    public void setAllowStatusBarExpansion(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowStatusBarExpansion", value);
    }
    
    public void setAllowWallpaperChange(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowWallpaperChange", value);
    }
    
    public void setAllowAndroidMarket(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAndroidMarket", value);
    }
    
    public void setAllowVoiceDialer(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowVoiceDialer", value);
    }
    
    public void setAllowYouTube(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowYouTube", value);
    }
    
    public void setAllowInstallApp(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowInstallApp", value);
    }
    
    public void setAllowUnInstallApp(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowUnInstallApp", value);
    }
    
    public void setAllowAndroidBrowser(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowAndroidBrowser", value);
    }
    
    public void setAllowBrowserAutoFill(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowAutoFill", value);
    }
    
    public void setAllowBrowserCookies(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowCookies", value);
    }
    
    public void setAllowBrowserJavaScript(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowJavaScript", value);
    }
    
    public void setAllowBrowserPopUps(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowPopups", value);
    }
    
    public void setAllowBrowserAllowFraudWarning(final boolean value) throws JSONException {
        this.getPayloadJSON().put("browserAllowFraudWarning", value);
    }
    
    public void setAllowRoamingData(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingData", value);
    }
    
    public void setAllowRoamingPush(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingPush", value);
    }
    
    public void setAllowRoamingSync(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingSync", value);
    }
    
    public void setAllowRoamingVoiceCall(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowRoamingVoiceCall", value);
    }
    
    public void setAllowInternalStorageEncryption(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowInternalStorageEncryption", value);
    }
    
    public void setAllowExternalStorageEncryption(final boolean value) throws JSONException {
        this.getPayloadJSON().put("allowExternalStorageEncryption", value);
    }
}
