package org.apache.poi.sl.usermodel;

import java.util.List;

public interface Slide<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Sheet<S, P>
{
    Notes<S, P> getNotes();
    
    void setNotes(final Notes<S, P> p0);
    
    boolean getFollowMasterBackground();
    
    void setFollowMasterBackground(final boolean p0);
    
    boolean getFollowMasterColourScheme();
    
    void setFollowMasterColourScheme(final boolean p0);
    
    boolean getFollowMasterObjects();
    
    void setFollowMasterObjects(final boolean p0);
    
    int getSlideNumber();
    
    String getTitle();
    
    boolean getDisplayPlaceholder(final Placeholder p0);
    
    void setHidden(final boolean p0);
    
    boolean isHidden();
    
    List<? extends Comment> getComments();
    
    MasterSheet<S, P> getSlideLayout();
    
    String getSlideName();
}
