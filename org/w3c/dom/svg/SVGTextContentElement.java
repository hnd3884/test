package org.w3c.dom.svg;

import org.w3c.dom.DOMException;
import org.w3c.dom.events.EventTarget;

public interface SVGTextContentElement extends SVGElement, SVGTests, SVGLangSpace, SVGExternalResourcesRequired, SVGStylable, EventTarget
{
    public static final short LENGTHADJUST_UNKNOWN = 0;
    public static final short LENGTHADJUST_SPACING = 1;
    public static final short LENGTHADJUST_SPACINGANDGLYPHS = 2;
    
    SVGAnimatedLength getTextLength();
    
    SVGAnimatedEnumeration getLengthAdjust();
    
    int getNumberOfChars();
    
    float getComputedTextLength();
    
    float getSubStringLength(final int p0, final int p1) throws DOMException;
    
    SVGPoint getStartPositionOfChar(final int p0) throws DOMException;
    
    SVGPoint getEndPositionOfChar(final int p0) throws DOMException;
    
    SVGRect getExtentOfChar(final int p0) throws DOMException;
    
    float getRotationOfChar(final int p0) throws DOMException;
    
    int getCharNumAtPosition(final SVGPoint p0);
    
    void selectSubString(final int p0, final int p1) throws DOMException;
}
