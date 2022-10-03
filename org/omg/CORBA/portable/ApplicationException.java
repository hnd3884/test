package org.omg.CORBA.portable;

public class ApplicationException extends Exception
{
    private String id;
    private InputStream ins;
    
    public ApplicationException(final String id, final InputStream ins) {
        this.id = id;
        this.ins = ins;
    }
    
    public String getId() {
        return this.id;
    }
    
    public InputStream getInputStream() {
        return this.ins;
    }
}
