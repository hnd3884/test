package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSObject;
import com.dd.plist.NSDictionary;

public class AppLockPayload extends IOSPayload
{
    NSDictionary appDictionary;
    NSDictionary optionsDictionary;
    NSDictionary userEnabledOptionsDictionary;
    
    public AppLockPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.app.lock", payloadOrganization, payloadIdentifier, payloadDisplayName);
        this.appDictionary = new NSDictionary();
        this.optionsDictionary = new NSDictionary();
        this.userEnabledOptionsDictionary = new NSDictionary();
        this.appDictionary.put("Options", (NSObject)this.optionsDictionary);
        this.appDictionary.put("UserEnabledOptions", (NSObject)this.userEnabledOptionsDictionary);
        this.getPayloadDict().put("App", (NSObject)this.appDictionary);
    }
    
    public void setAppIdentifier(final String identifier) {
        this.getPayloadDict().put("Identifier", (Object)identifier);
        this.appDictionary.put("Identifier", (Object)identifier);
    }
    
    public void setDisableTouchOption(final boolean isSet) {
        this.optionsDictionary.put("DisableTouch", (Object)isSet);
    }
    
    public void setDisableDeviceRotationOption(final boolean isSet) {
        this.optionsDictionary.put("DisableDeviceRotation", (Object)isSet);
    }
    
    public void setDisableVolumeButtonsOption(final boolean isSet) {
        this.optionsDictionary.put("DisableVolumeButtons", (Object)isSet);
    }
    
    public void setDisableRingerSwitchOption(final boolean isSet) {
        this.optionsDictionary.put("DisableRingerSwitch", (Object)isSet);
    }
    
    public void setDisableSleepWakeButtonOption(final boolean isSet) {
        this.optionsDictionary.put("DisableSleepWakeButton", (Object)isSet);
    }
    
    public void setDisableAutoLockOption(final boolean isSet) {
        this.optionsDictionary.put("DisableAutoLock", (Object)isSet);
    }
    
    public void setVoiceOverConfiguration(final boolean isSet) {
        this.userEnabledOptionsDictionary.put("VoiceOver", (Object)isSet);
        this.optionsDictionary.put("EnableVoiceOver", (Object)isSet);
    }
    
    public void setZoomConfiguration(final boolean isSet) {
        this.userEnabledOptionsDictionary.put("Zoom", (Object)isSet);
        this.optionsDictionary.put("EnableZoom", (Object)isSet);
    }
    
    public void setInvertColorsConfiguration(final boolean isSet) {
        this.optionsDictionary.put("EnableInvertColors", (Object)isSet);
        this.userEnabledOptionsDictionary.put("InvertColors", (Object)isSet);
    }
    
    public void setAssistiveTouchConfiguration(final boolean isSet) {
        this.optionsDictionary.put("EnableAssistiveTouch", (Object)isSet);
        this.userEnabledOptionsDictionary.put("AssistiveTouch", (Object)isSet);
    }
    
    public void setEnableSpeakSelectionOption(final boolean isSet) {
        this.optionsDictionary.put("EnableSpeakSelection", (Object)isSet);
    }
    
    public void setEnableMonoAudioOption(final boolean isSet) {
        this.optionsDictionary.put("EnableMonoAudio", (Object)isSet);
    }
    
    public void setEnableVoiceControl(final boolean isSet) {
        this.optionsDictionary.put("EnableVoiceControl", (Object)isSet);
    }
}
