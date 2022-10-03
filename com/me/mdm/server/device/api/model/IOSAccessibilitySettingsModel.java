package com.me.mdm.server.device.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IOSAccessibilitySettingsModel
{
    @JsonProperty("bold_text_enabled")
    private boolean boldTextEnabled;
    @JsonProperty("increase_contrast_enabled")
    private boolean increaseContrastEnabled;
    @JsonProperty("reduce_motion_enabled")
    private boolean reduceMotionEnabled;
    @JsonProperty("reduce_transparency_enabled")
    private boolean reduceTransparencyEnabled;
    @JsonProperty("text_size")
    private Integer textSize;
    @JsonProperty("touch_accommodations_enabled")
    private boolean touchAccommodationsEnabled;
    @JsonProperty("voice_over_enabled")
    private boolean voiceOverEnabled;
    @JsonProperty("zoom_enabled")
    private boolean zoomEnabled;
    
    public boolean isBoldTextEnabled() {
        return this.boldTextEnabled;
    }
    
    public void setBoldTextEnabled(final boolean boldTextEnabled) {
        this.boldTextEnabled = boldTextEnabled;
    }
    
    public boolean isIncreaseContrastEnabled() {
        return this.increaseContrastEnabled;
    }
    
    public void setIncreaseContrastEnabled(final boolean increaseContrastEnabled) {
        this.increaseContrastEnabled = increaseContrastEnabled;
    }
    
    public boolean isReduceMotionEnabled() {
        return this.reduceMotionEnabled;
    }
    
    public void setReduceMotionEnabled(final boolean reduceMotionEnabled) {
        this.reduceMotionEnabled = reduceMotionEnabled;
    }
    
    public boolean isReduceTransparencyEnabled() {
        return this.reduceTransparencyEnabled;
    }
    
    public void setReduceTransparencyEnabled(final boolean reduceTransparencyEnabled) {
        this.reduceTransparencyEnabled = reduceTransparencyEnabled;
    }
    
    public Integer getTextSize() {
        return this.textSize;
    }
    
    public void setTextSize(final Integer textSize) {
        this.textSize = textSize;
    }
    
    public boolean isTouchAccommodationsEnabled() {
        return this.touchAccommodationsEnabled;
    }
    
    public void setTouchAccommodationsEnabled(final boolean touchAccommodationsEnabled) {
        this.touchAccommodationsEnabled = touchAccommodationsEnabled;
    }
    
    public boolean isVoiceOverEnabled() {
        return this.voiceOverEnabled;
    }
    
    public void setVoiceOverEnabled(final boolean voiceOverEnabled) {
        this.voiceOverEnabled = voiceOverEnabled;
    }
    
    public boolean isZoomEnabled() {
        return this.zoomEnabled;
    }
    
    public void setZoomEnabled(final boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }
}
