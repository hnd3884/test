package com.sun.mail.pop3;

import java.io.InputStream;

class Response
{
    boolean ok;
    String data;
    InputStream bytes;
    
    Response() {
        this.ok = false;
        this.data = null;
        this.bytes = null;
    }
}
