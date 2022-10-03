package com.adventnet.persistence.xml;

import com.adventnet.persistence.DataObject;

public interface ConfigurationPopulator
{
    void populate(final DataObject p0) throws ConfigurationPopulationException;
    
    void update(final DataObject p0) throws ConfigurationPopulationException;
}
