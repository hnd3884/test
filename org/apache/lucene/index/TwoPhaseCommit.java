package org.apache.lucene.index;

import java.io.IOException;

public interface TwoPhaseCommit
{
    void prepareCommit() throws IOException;
    
    void commit() throws IOException;
    
    void rollback() throws IOException;
}
