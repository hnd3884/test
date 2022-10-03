package org.cyberneko.html.filters;

import org.cyberneko.html.HTMLEventInfo;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.QName;

public class Identity extends DefaultFilter
{
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    protected static final String FILTERS = "http://cyberneko.org/html/properties/filters";
    
    public void startElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (augs == null || !synthesized(augs)) {
            super.startElement(element, attributes, augs);
        }
    }
    
    public void emptyElement(final QName element, final XMLAttributes attributes, final Augmentations augs) throws XNIException {
        if (augs == null || !synthesized(augs)) {
            super.emptyElement(element, attributes, augs);
        }
    }
    
    public void endElement(final QName element, final Augmentations augs) throws XNIException {
        if (augs == null || !synthesized(augs)) {
            super.endElement(element, augs);
        }
    }
    
    protected static boolean synthesized(final Augmentations augs) {
        final HTMLEventInfo info = (HTMLEventInfo)augs.getItem("http://cyberneko.org/html/features/augmentations");
        return info != null && info.isSynthesized();
    }
}
