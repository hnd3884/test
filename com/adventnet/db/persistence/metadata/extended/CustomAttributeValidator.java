package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.db.persistence.metadata.MetaDataException;

public interface CustomAttributeValidator
{
    boolean validateStaticAttribute(final String p0, final String p1, final String p2) throws MetaDataException;
    
    boolean validateDynamicAttribute(final String p0, final String p1, final String p2) throws MetaDataException;
}
