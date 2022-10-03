package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.db.persistence.metadata.MetaDataException;

public class DefaultValueValidator implements CustomAttributeValidator
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
        throw new MetaDataException("defaultValue of column should not be changed during run time");
    }
}
