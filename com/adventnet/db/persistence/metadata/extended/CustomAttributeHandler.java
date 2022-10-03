package com.adventnet.db.persistence.metadata.extended;

import java.util.Map;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.DataAccessException;
import java.io.IOException;

public interface CustomAttributeHandler
{
    boolean setAttribute(final String p0, final String p1, final String p2, final String p3) throws IOException, DataAccessException, MetaDataException;
    
    boolean setAttribute(final String p0, final String p1, final String p2) throws IOException, DataAccessException, MetaDataException;
    
    Map<String, String> loadDynamicCustomDDAttributes() throws IOException, DataAccessException, MetaDataException;
    
    boolean removeAttribute(final String p0, final String p1, final String p2) throws IOException, DataAccessException, MetaDataException;
    
    boolean removeAttribute(final String p0, final String p1) throws IOException, DataAccessException, MetaDataException;
}
