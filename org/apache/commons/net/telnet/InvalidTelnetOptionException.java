package org.apache.commons.net.telnet;

public class InvalidTelnetOptionException extends Exception
{
    private static final long serialVersionUID = -2516777155928793597L;
    private final int optionCode;
    private final String msg;
    
    public InvalidTelnetOptionException(final String message, final int optcode) {
        this.optionCode = optcode;
        this.msg = message;
    }
    
    @Override
    public String getMessage() {
        return this.msg + ": " + this.optionCode;
    }
}
