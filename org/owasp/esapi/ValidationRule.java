package org.owasp.esapi;

import java.util.Set;
import org.owasp.esapi.errors.ValidationException;

public interface ValidationRule
{
    Object getValid(final String p0, final String p1) throws ValidationException;
    
    void setAllowNull(final boolean p0);
    
    String getTypeName();
    
    void setTypeName(final String p0);
    
    void setEncoder(final Encoder p0);
    
    void assertValid(final String p0, final String p1) throws ValidationException;
    
    Object getValid(final String p0, final String p1, final ValidationErrorList p2) throws ValidationException;
    
    Object getSafe(final String p0, final String p1);
    
    boolean isValid(final String p0, final String p1);
    
    String whitelist(final String p0, final char[] p1);
    
    String whitelist(final String p0, final Set<Character> p1);
}
