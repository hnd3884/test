package com.adventnet.db.persistence.metadata.util;

import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Collection;

public interface TableListProvider
{
    Collection<Long> getTablesToBeLoaded(final String p0) throws MetaDataException;
    
    default Collection<Long> getTablesToBeLoaded(final Collection<Long> tableIDs) throws MetaDataException {
        return tableIDs;
    }
}
