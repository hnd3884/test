package org.apache.commons.net.telnet;

public class SuppressGAOptionHandler extends TelnetOptionHandler
{
    public SuppressGAOptionHandler(final boolean initlocal, final boolean initremote, final boolean acceptlocal, final boolean acceptremote) {
        super(3, initlocal, initremote, acceptlocal, acceptremote);
    }
    
    public SuppressGAOptionHandler() {
        super(3, false, false, false, false);
    }
}
