package com.me.ems.framework.home.core;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CardPositionBean
{
    @NotNull
    @NotEmpty
    String viewID;
    @NotNull
    @NotEmpty
    Integer position;
    
    public String getViewID() {
        return this.viewID;
    }
    
    public void setViewID(final String viewID) {
        this.viewID = viewID;
    }
    
    public Integer getPosition() {
        return this.position;
    }
    
    public void setPosition(final Integer position) {
        this.position = position;
    }
}
