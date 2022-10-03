package com.me.mdm.server.enrollment.admin.android.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.me.mdm.api.model.BaseAPIModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoogleWebtokenEnrollmentModel extends BaseAPIModel
{
    @JsonProperty("parent")
    private String parent;
    @JsonProperty("template_type")
    private int template_type;
    
    public String getParent() {
        return this.parent;
    }
    
    public void setParent(final String parent) {
        this.parent = parent;
    }
    
    public int getTemplateType() {
        return this.template_type;
    }
    
    public void setTemplateType(final int template_type) {
        this.template_type = template_type;
    }
}
