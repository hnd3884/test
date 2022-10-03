package org.apache.xmlbeans.impl.common;

import javax.xml.stream.Location;

public class InvalidLexicalValueException extends RuntimeException
{
    private Location _location;
    
    public InvalidLexicalValueException() {
    }
    
    public InvalidLexicalValueException(final String msg) {
        super(msg);
    }
    
    public InvalidLexicalValueException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
    
    public InvalidLexicalValueException(final Throwable cause) {
        super(cause);
    }
    
    public InvalidLexicalValueException(final String msg, final Location location) {
        super(msg);
        this.setLocation(location);
    }
    
    public InvalidLexicalValueException(final String msg, final Throwable cause, final Location location) {
        super(msg, cause);
        this.setLocation(location);
    }
    
    public InvalidLexicalValueException(final Throwable cause, final Location location) {
        super(cause);
        this.setLocation(location);
    }
    
    public Location getLocation() {
        return this._location;
    }
    
    public void setLocation(final Location location) {
        this._location = location;
    }
}
