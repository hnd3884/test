package com.adventnet.client.view.web;

import com.adventnet.client.util.web.WebConstants;

public class ShowTileTag extends ShowViewTag implements WebConstants
{
    protected String tileName;
    
    public String getTileName() {
        return this.tileName;
    }
    
    public void setTileName(final String newTileName) {
        this.tileName = newTileName;
    }
    
    @Override
    public String getViewName() {
        return (String)this.pageContext.getRequest().getAttribute("TILE:" + this.tileName);
    }
    
    @Override
    public String getViewUniqueId() {
        return this.getViewName();
    }
}
