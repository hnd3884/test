package com.me.devicemanagement.framework.common.api.v1.model;

import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import org.json.JSONArray;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DCViewFilter
{
    @JsonProperty("criteria")
    private List<DCViewFilterCriteria> dcViewFilterCriteriaList;
    
    @JsonGetter("criteria")
    public List<DCViewFilterCriteria> getDcViewFilterCriteriaList() {
        return this.dcViewFilterCriteriaList;
    }
    
    @JsonSetter("criteria")
    public void setDcViewFilterCriteriaList(final List<DCViewFilterCriteria> dcViewFilterCriteriaList) {
        this.dcViewFilterCriteriaList = dcViewFilterCriteriaList;
    }
    
    public void setDcViewFilterCriteriaList(final JSONArray jsonArray) throws Exception {
        for (int i = 0; i < jsonArray.length(); ++i) {
            this.addDCViewFilterCriteria(DCViewFilterCriteria.dcViewFilterCriteriaMapper(jsonArray.getJSONObject(i)));
        }
    }
    
    public void addDCViewFilterCriteria(final DCViewFilterCriteria dcViewFilterCriteria) {
        (this.dcViewFilterCriteriaList = ((this.dcViewFilterCriteriaList != null) ? this.dcViewFilterCriteriaList : new ArrayList<DCViewFilterCriteria>())).add(dcViewFilterCriteria);
    }
    
    public static DCViewFilter dcViewFilterMapper(final String dcViewFilterString) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final DCViewFilter dcViewFilter = (DCViewFilter)mapper.readValue(dcViewFilterString, (Class)DCViewFilter.class);
        return dcViewFilter;
    }
    
    public JSONObject dcViewFilterMapper() {
        JSONObject jsonObject = null;
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonString = mapper.writeValueAsString((Object)this);
            jsonObject = new JSONObject(jsonString);
        }
        catch (final Exception ex) {}
        return jsonObject;
    }
}
