package com.adventnet.persistence.migration;

import java.util.List;

public interface ConfigurationUpdater
{
    void applyDBParamsChange(final String p0, final String p1, final String p2, final boolean p3, final List<String> p4) throws Exception;
    
    void applyPersistenceConfChanges(final String p0, final String p1, final String p2) throws Exception;
    
    void revertDBParamsChanges(final String p0) throws Exception;
    
    void revertPersistenceConfChanges(final String p0);
}
