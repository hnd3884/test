package org.apache.poi.sl.usermodel;

public interface PlaceholderDetails
{
    Placeholder getPlaceholder();
    
    void setPlaceholder(final Placeholder p0);
    
    boolean isVisible();
    
    void setVisible(final boolean p0);
    
    PlaceholderSize getSize();
    
    void setSize(final PlaceholderSize p0);
    
    String getText();
    
    void setText(final String p0);
    
    public enum PlaceholderSize
    {
        quarter, 
        half, 
        full;
    }
}
