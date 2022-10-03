package javax.resource.cci;

import java.io.Serializable;

public interface Record extends Cloneable, Serializable
{
    Object clone() throws CloneNotSupportedException;
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String getRecordName();
    
    void setRecordName(final String p0);
    
    String getRecordShortDescription();
    
    void setRecordShortDescription(final String p0);
}
