package com.sshtools.publickey;

public class InvalidPassphraseException extends Exception
{
    public InvalidPassphraseException() {
        super("The passphrase supplied was invalid!");
    }
    
    public InvalidPassphraseException(final Exception ex) {
        super(ex.getMessage());
    }
}
