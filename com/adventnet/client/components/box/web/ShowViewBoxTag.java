package com.adventnet.client.components.box.web;

import com.adventnet.client.view.web.ShowTileTag;

public class ShowViewBoxTag extends ShowTileTag
{
    boolean showBox;
    private String boxType;
    
    public ShowViewBoxTag() {
        this.showBox = true;
        this.boxType = null;
    }
    
    public void setInitialState(final String initialState) {
    }
    
    public String getInitialState() {
        return "1";
    }
    
    public String getViewName() {
        if (this.tileName == null) {
            return this.viewName;
        }
        return super.getViewName();
    }
    
    public String getViewUniqueId() {
        if (this.tileName == null) {
            return (this.viewUniqueId != null) ? this.viewUniqueId : this.viewName;
        }
        return super.getViewUniqueId();
    }
    
    public boolean isShowBox() {
        return this.showBox;
    }
    
    public void setShowBox(final boolean newShowBox) {
        this.showBox = newShowBox;
    }
    
    public void setShowInBox(final String showInBox) {
        this.boxType = showInBox;
    }
    
    public String getShowInBox() {
        return this.boxType;
    }
}
