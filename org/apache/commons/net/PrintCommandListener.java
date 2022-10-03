package org.apache.commons.net;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class PrintCommandListener implements ProtocolCommandListener
{
    private final PrintWriter __writer;
    private final boolean __nologin;
    private final char __eolMarker;
    private final boolean __directionMarker;
    
    public PrintCommandListener(final PrintStream stream) {
        this(new PrintWriter(stream));
    }
    
    public PrintCommandListener(final PrintStream stream, final boolean suppressLogin) {
        this(new PrintWriter(stream), suppressLogin);
    }
    
    public PrintCommandListener(final PrintStream stream, final boolean suppressLogin, final char eolMarker) {
        this(new PrintWriter(stream), suppressLogin, eolMarker);
    }
    
    public PrintCommandListener(final PrintStream stream, final boolean suppressLogin, final char eolMarker, final boolean showDirection) {
        this(new PrintWriter(stream), suppressLogin, eolMarker, showDirection);
    }
    
    public PrintCommandListener(final PrintWriter writer) {
        this(writer, false);
    }
    
    public PrintCommandListener(final PrintWriter writer, final boolean suppressLogin) {
        this(writer, suppressLogin, '\0');
    }
    
    public PrintCommandListener(final PrintWriter writer, final boolean suppressLogin, final char eolMarker) {
        this(writer, suppressLogin, eolMarker, false);
    }
    
    public PrintCommandListener(final PrintWriter writer, final boolean suppressLogin, final char eolMarker, final boolean showDirection) {
        this.__writer = writer;
        this.__nologin = suppressLogin;
        this.__eolMarker = eolMarker;
        this.__directionMarker = showDirection;
    }
    
    @Override
    public void protocolCommandSent(final ProtocolCommandEvent event) {
        if (this.__directionMarker) {
            this.__writer.print("> ");
        }
        if (this.__nologin) {
            final String cmd = event.getCommand();
            if ("PASS".equalsIgnoreCase(cmd) || "USER".equalsIgnoreCase(cmd)) {
                this.__writer.print(cmd);
                this.__writer.println(" *******");
            }
            else {
                final String IMAP_LOGIN = "LOGIN";
                if ("LOGIN".equalsIgnoreCase(cmd)) {
                    String msg = event.getMessage();
                    msg = msg.substring(0, msg.indexOf("LOGIN") + "LOGIN".length());
                    this.__writer.print(msg);
                    this.__writer.println(" *******");
                }
                else {
                    this.__writer.print(this.getPrintableString(event.getMessage()));
                }
            }
        }
        else {
            this.__writer.print(this.getPrintableString(event.getMessage()));
        }
        this.__writer.flush();
    }
    
    private String getPrintableString(final String msg) {
        if (this.__eolMarker == '\0') {
            return msg;
        }
        final int pos = msg.indexOf("\r\n");
        if (pos > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(msg.substring(0, pos));
            sb.append(this.__eolMarker);
            sb.append(msg.substring(pos));
            return sb.toString();
        }
        return msg;
    }
    
    @Override
    public void protocolReplyReceived(final ProtocolCommandEvent event) {
        if (this.__directionMarker) {
            this.__writer.print("< ");
        }
        this.__writer.print(event.getMessage());
        this.__writer.flush();
    }
}
