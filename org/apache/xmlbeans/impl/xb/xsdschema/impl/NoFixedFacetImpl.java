package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;

public class NoFixedFacetImpl extends FacetImpl implements NoFixedFacet
{
    private static final long serialVersionUID = 1L;
    
    public NoFixedFacetImpl(final SchemaType sType) {
        super(sType);
    }
}
