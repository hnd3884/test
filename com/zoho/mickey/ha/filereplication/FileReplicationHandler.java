package com.zoho.mickey.ha.filereplication;

import java.util.List;
import com.zoho.mickey.ha.HAException;
import com.zoho.mickey.ha.HAConfig;

public interface FileReplicationHandler
{
    public static final String COPY = "COPY";
    public static final String MIRROR = "MIRROR";
    public static final String REPLICATE = "REPLICATE";
    
    void initialize(final HAConfig p0, final String p1) throws HAException;
    
    void startReplication() throws HAException;
    
    void stopReplication() throws HAException;
    
    boolean completePendingReplication() throws HAException;
    
    default boolean replicateDirs(final HAConfig config, final List<String> dirs, final String mode) throws HAException {
        return false;
    }
    
    boolean replicateFiles(final HAConfig p0, final List<String> p1) throws HAException;
    
    default boolean replicateDirs(final HAConfig config, final List<String> dirs, final List<String> excludeDirs, final List<String> excludeFiles, final String mode) throws HAException {
        return false;
    }
}
