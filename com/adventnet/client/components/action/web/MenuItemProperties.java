package com.adventnet.client.components.action.web;

import com.adventnet.i18n.I18N;

public class MenuItemProperties
{
    private String menuItemId;
    private String displayName;
    private String imageSrc;
    private String imageCSSClass;
    private int viewType;
    
    public MenuItemProperties(final String menuItemId, final String displayName, final String imageSrc) {
        this(menuItemId, displayName, imageSrc, null);
    }
    
    MenuItemProperties(final String menuItemId, final String displayName, final String imageSrc, final String imageCSSClass) {
        this.viewType = 1;
        this.menuItemId = menuItemId;
        this.displayName = displayName;
        this.imageSrc = imageSrc;
        this.imageCSSClass = imageCSSClass;
    }
    
    public String getImageCSSClass() {
        return this.imageCSSClass;
    }
    
    public String getMenuItemId() {
        return this.menuItemId;
    }
    
    public String getDisplayName() throws Exception {
        if (this.displayName != null) {
            return I18N.getMsg(this.displayName, new Object[0]);
        }
        return this.displayName;
    }
    
    public String getImageSrc() {
        return this.imageSrc;
    }
    
    public void setViewType(final int viewType) {
        this.viewType = viewType;
    }
    
    public int getViewType() {
        return this.viewType;
    }
}
