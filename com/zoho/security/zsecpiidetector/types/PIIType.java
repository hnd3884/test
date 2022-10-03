package com.zoho.security.zsecpiidetector.types;

public class PIIType
{
    private String piiType;
    private PIIEnum.Category category;
    private PIIEnum.Sensitivity sensitivity;
    private PIIEnum.DetectionType detectionType;
    
    public PIIType(final String piiType, final PIIEnum.Category category, final PIIEnum.Sensitivity sensitivity, final PIIEnum.DetectionType detectionType) {
        this.piiType = piiType;
        this.category = category;
        this.sensitivity = sensitivity;
        this.detectionType = detectionType;
    }
    
    public String getPiiType() {
        return this.piiType;
    }
    
    public PIIEnum.Category getCategory() {
        return this.category;
    }
    
    public PIIEnum.Sensitivity getSensitivity() {
        return this.sensitivity;
    }
    
    public PIIEnum.DetectionType getDetectionType() {
        return this.detectionType;
    }
    
    @Override
    public String toString() {
        return "piiType=" + this.getPiiType() + " category=" + this.getCategory() + " sensitivity=" + this.getSensitivity() + " detectionType=" + this.getDetectionType();
    }
}
