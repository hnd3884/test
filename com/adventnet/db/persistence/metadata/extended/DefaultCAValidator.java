package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.db.persistence.metadata.MetaDataException;

public class DefaultCAValidator implements CustomAttributeValidator
{
    @Override
    public boolean validateStaticAttribute(final String key, final String existingAttrValue, final String newAttrValue) throws MetaDataException {
        if (newAttrValue == null || newAttrValue.isEmpty()) {
            throw new MetaDataException("value provided for key [" + key + "]  cannot have empty/null value");
        }
        return true;
    }
    
    @Override
    public boolean validateDynamicAttribute(final String key, final String existingAttrValue, final String newAttrValue) throws MetaDataException {
        if (newAttrValue == null || newAttrValue.isEmpty()) {
            throw new MetaDataException("value provided for key [" + key + "]  cannot have empty/null value");
        }
        return true;
    }
}
