package com.theorem.radius3;

import java.io.Serializable;
import java.io.IOException;

public final class ClientSendException extends IOException implements Serializable
{
    public ClientSendException(final String s) {
        super(s);
    }
    
    public ClientSendException() {
    }
}
