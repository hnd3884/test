package org.apache.poi.sl.usermodel;

import java.awt.geom.Point2D;
import java.util.Date;

public interface Comment
{
    String getAuthor();
    
    void setAuthor(final String p0);
    
    String getAuthorInitials();
    
    void setAuthorInitials(final String p0);
    
    String getText();
    
    void setText(final String p0);
    
    Date getDate();
    
    void setDate(final Date p0);
    
    Point2D getOffset();
    
    void setOffset(final Point2D p0);
}
