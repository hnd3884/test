package org.cyberneko.html;

import org.apache.xerces.xni.parser.XMLComponent;

public interface HTMLComponent extends XMLComponent
{
    Boolean getFeatureDefault(final String p0);
    
    Object getPropertyDefault(final String p0);
}
