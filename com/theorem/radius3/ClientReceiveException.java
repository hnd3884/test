package com.theorem.radius3;

import java.io.Serializable;
import java.io.IOException;

public final class ClientReceiveException extends IOException implements Serializable
{
    public ClientReceiveException(final String s) {
        super(s);
    }
    
    public ClientReceiveException() {
    }
}
