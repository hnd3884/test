package com.adventnet.persistence.interceptor;

import com.adventnet.persistence.DataAccessException;
import java.util.Set;

public class TerminalPersistenceRequest extends PersistenceRequest
{
    public static final int START = 901;
    public static final int END = 902;
    private int terminalType;
    private long requestId;
    private static long nextRequestId;
    
    public TerminalPersistenceRequest(final int terminalType, final long requestId) {
        if (terminalType != 901 && terminalType != 902) {
            throw new RuntimeException("TerminalType value should be either 901 or 902.");
        }
        this.terminalType = terminalType;
        this.requestId = requestId;
    }
    
    @Override
    public int getOperationType() {
        return 604;
    }
    
    public int getTerminalType() {
        return this.terminalType;
    }
    
    public long getRequestId() {
        return this.requestId;
    }
    
    public static long nextRequestId() {
        return ++TerminalPersistenceRequest.nextRequestId;
    }
    
    @Override
    public Set<String> getTableList() throws DataAccessException {
        return null;
    }
}
