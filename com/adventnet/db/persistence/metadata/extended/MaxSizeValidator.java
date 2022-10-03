package com.adventnet.db.persistence.metadata.extended;

import com.adventnet.db.persistence.metadata.MetaDataException;

public class MaxSizeValidator implements CustomAttributeValidator
{
    @Override
    public boolean validateStaticAttribute(final String key, final String existingAttrValue, final String newAttrValue) throws MetaDataException {
        if (newAttrValue == null) {
            throw new MetaDataException("Max Size of column [" + key.substring(0, key.lastIndexOf(".")) + "] cannot be null");
        }
        if (existingAttrValue == null) {
            return true;
        }
        final int maxLength = Integer.parseInt(existingAttrValue);
        final int extMaxLength = Integer.parseInt(newAttrValue);
        if ((extMaxLength != -1 && maxLength != -1 && extMaxLength < maxLength) || (maxLength == -1 && extMaxLength > 0)) {
            throw new MetaDataException("max-size for column [" + key.substring(0, key.lastIndexOf(".")) + "]  is already defined as [" + maxLength + "] and cannot be decreased to [" + extMaxLength + "]");
        }
        return true;
    }
    
    @Override
    public boolean validateDynamicAttribute(final String key, final String existingAttrValue, final String newAttrValue) throws MetaDataException {
        throw new MetaDataException("maxSize of column should not be changed during run time");
    }
}
