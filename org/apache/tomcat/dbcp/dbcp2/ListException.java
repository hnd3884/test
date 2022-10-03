package org.apache.tomcat.dbcp.dbcp2;

import java.util.List;

public class ListException extends Exception
{
    private static final long serialVersionUID = 1L;
    private final List<Throwable> exceptionList;
    
    public ListException(final String message, final List<Throwable> exceptionList) {
        super(message);
        this.exceptionList = exceptionList;
    }
    
    public List<Throwable> getExceptionList() {
        return this.exceptionList;
    }
}
