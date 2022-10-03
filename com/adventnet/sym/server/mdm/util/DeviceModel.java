package com.adventnet.sym.server.mdm.util;

import java.util.HashMap;
import com.adventnet.sym.server.mdm.enroll.MDMModelNameMappingHandler;

public class DeviceModel
{
    private Long supportedModelId;
    private String manufacturer;
    private String modelName;
    private String modelCode;
    private String formFactor;
    private String screenSize;
    private String platformType;
    
    public DeviceModel(final String[] device) throws Exception {
        final MDMUtil util = MDMUtil.getInstance();
        this.validateTokens(device);
        this.supportedModelId = Long.parseLong(device[MDMModelNameMappingHandler.SUPPORTED_MODEL_IDX]);
        this.manufacturer = device[MDMModelNameMappingHandler.MANUFACTURER_IDX];
        this.modelName = device[MDMModelNameMappingHandler.MODEL_NAME_IDX];
        this.modelCode = device[MDMModelNameMappingHandler.MODEL_CODE_IDX];
        this.formFactor = device[MDMModelNameMappingHandler.FORM_FACTOR_IDX];
        this.screenSize = device[MDMModelNameMappingHandler.SCREEN_SIZE_IDX];
        this.platformType = device[MDMModelNameMappingHandler.PLATFORM_TYPE_IDX];
    }
    
    public Long getSupportedModelId() {
        return this.supportedModelId;
    }
    
    public String getManufacturer() {
        return this.manufacturer;
    }
    
    public String getModelName() {
        return this.modelName;
    }
    
    public String getModelCode() {
        return this.modelCode;
    }
    
    public String getFormFactor() {
        return this.formFactor;
    }
    
    public String getScreenSize() {
        return this.screenSize;
    }
    
    public String getPlatformType() {
        return this.platformType;
    }
    
    private void validateTokens(final String[] entries) {
        for (int i = 0; i < entries.length; ++i) {
            entries[i] = entries[i].replace("\"", "");
        }
    }
    
    private DeviceModel(final Long supportedModelId, final String manufacturer, final String modelName, final String modelCode, final String formFactor, final String screenSize, final String platformType) {
        this.supportedModelId = supportedModelId;
        this.manufacturer = manufacturer;
        this.modelName = modelName;
        this.modelCode = modelCode;
        this.formFactor = formFactor;
        this.screenSize = screenSize;
        this.platformType = platformType;
    }
    
    public static DeviceModel getDeviceModel(final HashMap<Integer, String> modelCodeValues) {
        if (modelCodeValues.get(MDMModelNameMappingHandler.SUPPORTED_MODEL_IDX) != null) {
            final Long supportedModelId = Long.parseLong(modelCodeValues.get(MDMModelNameMappingHandler.SUPPORTED_MODEL_IDX));
            final String manufacturer = modelCodeValues.get(MDMModelNameMappingHandler.MANUFACTURER_IDX);
            final String modelName = modelCodeValues.get(MDMModelNameMappingHandler.MODEL_NAME_IDX);
            final String modelCode = modelCodeValues.get(MDMModelNameMappingHandler.MODEL_CODE_IDX);
            final String formFactor = modelCodeValues.get(MDMModelNameMappingHandler.FORM_FACTOR_IDX);
            final String screenSize = modelCodeValues.get(MDMModelNameMappingHandler.SCREEN_SIZE_IDX);
            final String platformType = modelCodeValues.get(MDMModelNameMappingHandler.PLATFORM_TYPE_IDX);
            return new DeviceModel(supportedModelId, manufacturer, modelName, modelCode, formFactor, screenSize, platformType);
        }
        return null;
    }
    
    @Override
    public boolean equals(final Object value) {
        return value instanceof DeviceModel && (this.supportedModelId == null || this.supportedModelId.equals(((DeviceModel)value).getSupportedModelId())) && (this.manufacturer == null || this.manufacturer.equals(((DeviceModel)value).getManufacturer())) && (this.modelName == null || this.modelName.equals(((DeviceModel)value).getModelName())) && (this.modelCode == null || this.modelCode.equals(((DeviceModel)value).getModelCode())) && (this.formFactor == null || this.formFactor.equals(((DeviceModel)value).getFormFactor())) && (this.screenSize == null || this.screenSize.equals(((DeviceModel)value).getScreenSize())) && (this.platformType == null || this.platformType.equals(((DeviceModel)value).getPlatformType()));
    }
}
