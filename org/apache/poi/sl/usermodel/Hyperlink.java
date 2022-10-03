package org.apache.poi.sl.usermodel;

public interface Hyperlink<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends org.apache.poi.common.usermodel.Hyperlink
{
    void linkToEmail(final String p0);
    
    void linkToUrl(final String p0);
    
    void linkToSlide(final Slide<S, P> p0);
    
    void linkToNextSlide();
    
    void linkToPreviousSlide();
    
    void linkToFirstSlide();
    
    void linkToLastSlide();
}
