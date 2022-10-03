package com.me.mdm.server.easmanagement.pss;

public class PSSException extends Exception
{
    public PSSException() {
        super("Failed to start a Powershell persistent Session with Host");
    }
}
