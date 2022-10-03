package com.maverick.ssh.components.jce;

public class HmacMD596 extends AbstractHmac
{
    public HmacMD596() {
        super("HmacMD5", 16, 12);
    }
    
    public String getAlgorithm() {
        return "hmac-md5";
    }
}
