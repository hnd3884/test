package com.zoho.security.zsecpiidetector;

import org.json.JSONArray;
import com.zoho.security.zsecpiidetector.types.PIIEnum;
import org.json.JSONObject;

public class PIIResult
{
    private JSONObject resultJson;
    
    public PIIResult(final JSONObject result) {
        this.resultJson = result;
    }
    
    public JSONObject get() {
        return this.resultJson;
    }
    
    @Override
    public String toString() {
        if (this.resultJson == null) {
            return null;
        }
        if (this.resultJson.has(PIIEnum.JsonKeys.REGEX_BASED_MASKED_DATA.value())) {
            return this.resultJson.getString(PIIEnum.JsonKeys.REGEX_BASED_MASKED_DATA.value());
        }
        if (this.resultJson.has(PIIEnum.JsonKeys.ML_BASED_MASKED_DATA.value())) {
            return this.resultJson.getString(PIIEnum.JsonKeys.ML_BASED_MASKED_DATA.value());
        }
        return "";
    }
    
    public String getResult() {
        return this.toString();
    }
    
    public JSONObject getResultAsJson() {
        return this.resultJson;
    }
    
    public void bindRegexResults(final PIIResult result) {
        this.bindResults(result, PIIEnum.JsonKeys.REGEX_BASED_MASKED_DATA, PIIEnum.JsonKeys.REGEX_KEY);
    }
    
    public void bindMLResults(final PIIResult result) {
        this.bindResults(result, PIIEnum.JsonKeys.ML_BASED_MASKED_DATA, PIIEnum.JsonKeys.ML_KEY);
    }
    
    private void bindResults(final PIIResult resultJson, final PIIEnum.JsonKeys maskDataKey, final PIIEnum.JsonKeys detailKey) {
        if (resultJson == null) {
            return;
        }
        final JSONObject resultJsonObject = resultJson.getResultAsJson();
        if (resultJsonObject == null) {
            return;
        }
        if (this.resultJson == null) {
            this.resultJson = new JSONObject();
        }
        if (resultJsonObject.has(maskDataKey.value())) {
            this.resultJson.put(maskDataKey.value(), (Object)resultJsonObject.getString(maskDataKey.value()));
        }
        if (resultJsonObject.has(detailKey.value())) {
            this.joinJSONArrays(resultJsonObject.getJSONArray(detailKey.value()), detailKey);
        }
    }
    
    private void joinJSONArrays(final JSONArray array, final PIIEnum.JsonKeys jsonKey) {
        if (array != null && this.resultJson != null) {
            for (int i = 0; i < array.length(); ++i) {
                if (this.resultJson.has(jsonKey.value())) {
                    this.resultJson.accumulate(PIIEnum.JsonKeys.REGEX_KEY.value(), (Object)array.getJSONObject(i));
                }
                else {
                    final JSONArray tempArray = new JSONArray();
                    tempArray.put((Object)array.getJSONObject(i));
                    this.resultJson.put(PIIEnum.JsonKeys.REGEX_KEY.value(), (Object)tempArray);
                }
            }
        }
    }
    
    public enum InfoFormat
    {
        DISABLE, 
        NORMAL, 
        INDEX;
    }
}
