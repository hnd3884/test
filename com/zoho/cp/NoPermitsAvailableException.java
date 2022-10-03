package com.zoho.cp;

class NoPermitsAvailableException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public NoPermitsAvailableException(final String msg) {
        super(msg);
    }
}
