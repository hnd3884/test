package com.adventnet.collaboration;

import com.adventnet.persistence.DataObject;

public interface Collaboration
{
    void reserveDataSpace(final String p0, final String p1) throws Exception;
    
    void reserveDataSpace(final String p0, final String p1, final String... p2) throws Exception;
    
    void reserveDataSpace(final String p0, final DataObject p1, final String p2, final String... p3) throws Exception;
    
    void reserveDataSpace(final String p0) throws Exception;
    
    void reserveDataSpace(final String p0, final String... p1) throws Exception;
    
    void reserveDataSpace(final String p0, final DataObject p1, final String... p2) throws Exception;
    
    default void reserveDataSpaceInCluster(final String name, final Long clusterId, final String clusterTag) throws Exception {
    }
    
    void unreserveDataSpace(final String p0) throws Exception;
    
    boolean dataSpaceExists(final String p0) throws Exception;
    
    void associateDataSpaceName(final String p0, final String p1, final DataObject p2, final String... p3) throws Exception;
    
    void associateDataSpaceName(final String p0, final String p1, final String... p2) throws Exception;
    
    void updateDataSpaceName(final String p0, final String p1) throws Exception;
}
