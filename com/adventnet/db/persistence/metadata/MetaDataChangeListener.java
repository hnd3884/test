package com.adventnet.db.persistence.metadata;

public interface MetaDataChangeListener
{
    void metaDataChanged(final MetaDataChangeEvent p0);
    
    void preMetaDataChange(final MetaDataPreChangeEvent p0);
}
