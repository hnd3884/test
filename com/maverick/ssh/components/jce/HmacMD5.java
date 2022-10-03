package com.maverick.ssh.components.jce;

public class HmacMD5 extends AbstractHmac
{
    public HmacMD5() {
        super("HmacMD5", 16);
    }
    
    public String getAlgorithm() {
        return "hmac-md5";
    }
}
