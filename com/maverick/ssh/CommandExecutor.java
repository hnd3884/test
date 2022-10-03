package com.maverick.ssh;

import java.io.IOException;

public class CommandExecutor
{
    SshSession e;
    String d;
    String b;
    String c;
    
    public CommandExecutor(final SshSession e, final String d, final String s, final String b, final String c) throws SshException, IOException {
        this.e = e;
        this.d = d;
        this.b = b;
        this.c = c;
        this.executeCommand(s);
    }
    
    public String executeCommand(final String s) throws SshException, IOException {
        try {
            this.e.getOutputStream().write(s.getBytes());
            this.e.getOutputStream().write(this.d.getBytes());
            final StringBuffer sb = new StringBuffer();
            do {
                final int read = this.e.getInputStream().read();
                if (read == -1) {
                    break;
                }
                sb.append((char)read);
            } while (!sb.toString().endsWith(this.b));
            return sb.toString().substring(0, sb.length() - this.b.length()).trim();
        }
        catch (final SshIOException ex) {
            throw ex.getRealException();
        }
    }
}
