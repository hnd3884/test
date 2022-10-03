package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;

public interface ConfigurationPopulationHandler
{
    void populate(final ConfUrlInfo p0, final DataObject p1) throws DataAccessException, ConfigurationPopulationException;
}
