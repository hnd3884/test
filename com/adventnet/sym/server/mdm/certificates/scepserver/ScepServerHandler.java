package com.adventnet.sym.server.mdm.certificates.scepserver;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONObject;

public interface ScepServerHandler
{
    void validateRelatedServerDetail(final long p0, final JSONObject p1);
    
    void addRelatedServerDetail(final ScepServer p0) throws Exception;
    
    boolean modifyRelatedServerDetail(final long p0, final long p1, final ScepServer p2) throws Exception;
    
    void deleteRelatedServerDetail(final long p0) throws DataAccessException;
    
    ScepServer getRelatedServerDetail(final long p0) throws DataAccessException;
}
