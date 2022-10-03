package com.me.devicemanagement.framework.server.api;

import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import com.adventnet.persistence.DataAccessException;

public interface IdPsAPI
{
    void handleDomainDeletionInDM(final String p0, final Long p1) throws DataAccessException;
    
    String addOrUpdateDMMDrel(final Long p0, final HashMap p1);
    
    int deleteDomainCredentials(final List p0);
    
    Properties checkForSameDomainExisting(final Long p0, final String p1, final String p2, final int p3, final String p4) throws Exception;
}
