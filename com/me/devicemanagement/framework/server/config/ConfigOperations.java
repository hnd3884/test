package com.me.devicemanagement.framework.server.config;

import java.util.List;
import com.me.devicemanagement.framework.server.exception.SyMException;
import java.util.Properties;

public interface ConfigOperations
{
    public static final int PERSIST_COLLECTION = 1;
    public static final int PERSIST_AND_DEPLOY_COLLECTION = 2;
    public static final int PERSIST_MODIFIED_COLLECTION = 3;
    public static final int PERSIST_AND_DEPLOY_MODIFIED_COLLECTION = 4;
    public static final int DEPLOY_COLLECTION = 5;
    public static final int DELETE_COLLECTION = 6;
    public static final int SUSPEND_COLLECTION = 7;
    public static final int RESUME_COLLECTION = 8;
    public static final int MARK_AS_DELETE = 9;
    public static final int RESTORE_COLLECTION = 10;
    
    Properties persistCollection(final Properties p0) throws SyMException;
    
    Properties persistAndDeployCollection(final Properties p0) throws SyMException;
    
    Properties persistModifiedCollection(final Properties p0) throws SyMException;
    
    Properties persistAndDeployModifiedCollection(final Properties p0) throws SyMException;
    
    Properties deployCollection(final Long p0, final String p1) throws SyMException;
    
    Properties deleteCollection(final Long p0) throws SyMException;
    
    Properties suspendCollection(final Long p0) throws SyMException;
    
    Properties suspendCollection(final Long p0, final Long p1) throws SyMException;
    
    Properties resumeCollection(final Long p0) throws SyMException;
    
    Properties resumeCollection(final Long p0, final Long p1) throws SyMException;
    
    void moveCollectionsToTrash(final List<Long> p0, final Long p1) throws SyMException;
}
