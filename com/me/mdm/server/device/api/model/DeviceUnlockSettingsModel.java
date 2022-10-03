package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceUnlockSettingsModel extends BaseAPIModel
{
    @JsonProperty("device_wipe_pin")
    private String deviceWipePIN;
    @JsonProperty("device_lock_pin")
    private String deviceLockPIN;
    @JsonProperty("device_wipe_pin_state")
    private boolean deviceWipePINState;
    @JsonProperty("device_lock_pin_state")
    private boolean deviceLockPINState;
    private Long resourceID;
    
    public DeviceUnlockSettingsModel() {
        this.deviceWipePIN = null;
        this.deviceLockPIN = null;
        this.deviceWipePINState = Boolean.FALSE;
        this.deviceLockPINState = Boolean.FALSE;
        this.resourceID = null;
    }
    
    public Boolean getDeviceWipePinState() {
        return this.deviceWipePINState;
    }
    
    public Boolean getDeviceUnlockPinState() {
        return this.deviceLockPINState;
    }
    
    public void setDeviceWipePinState(final Boolean deviceWipePINState) {
        this.deviceWipePINState = deviceWipePINState;
    }
    
    public void setDeviceLockPinState(final Boolean deviceLockPINState) {
        this.deviceLockPINState = deviceLockPINState;
    }
    
    public Boolean getDeviceWipePin() {
        return this.deviceWipePINState;
    }
    
    public Boolean getDeviceUnlockPin() {
        return this.deviceLockPINState;
    }
    
    public void setDeviceWipePin(final String deviceWipePIN) {
        this.deviceWipePIN = deviceWipePIN;
    }
    
    public void setDeviceLockPin(final String deviceLockPIN) {
        this.deviceLockPIN = deviceLockPIN;
    }
    
    public Long getResourceID() {
        return this.resourceID;
    }
    
    public void setResourceID(final Long resourceID) {
        this.resourceID = resourceID;
    }
}
