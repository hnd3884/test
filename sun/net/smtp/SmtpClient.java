package sun.net.smtp;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.io.IOException;
import sun.net.TransferProtocolClient;

public class SmtpClient extends TransferProtocolClient
{
    private static int DEFAULT_SMTP_PORT;
    String mailhost;
    SmtpPrintStream message;
    
    @Override
    public void closeServer() throws IOException {
        if (this.serverIsOpen()) {
            this.closeMessage();
            this.issueCommand("QUIT\r\n", 221);
            super.closeServer();
        }
    }
    
    void issueCommand(final String s, final int n) throws IOException {
        this.sendServer(s);
        int serverResponse;
        while ((serverResponse = this.readServerResponse()) != n) {
            if (serverResponse != 220) {
                throw new SmtpProtocolException(this.getResponseString());
            }
        }
    }
    
    private void toCanonical(final String s) throws IOException {
        if (s.startsWith("<")) {
            this.issueCommand("rcpt to: " + s + "\r\n", 250);
        }
        else {
            this.issueCommand("rcpt to: <" + s + ">\r\n", 250);
        }
    }
    
    public void to(final String s) throws IOException {
        if (s.indexOf(10) != -1) {
            throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
        }
        int n = 0;
        final int length = s.length();
        int i = 0;
        int n2 = 0;
        int n3 = 0;
        boolean b = false;
        while (i < length) {
            final char char1 = s.charAt(i);
            if (n3 > 0) {
                if (char1 == '(') {
                    ++n3;
                }
                else if (char1 == ')') {
                    --n3;
                }
                if (n3 == 0) {
                    if (n2 > n) {
                        b = true;
                    }
                    else {
                        n = i + 1;
                    }
                }
            }
            else if (char1 == '(') {
                ++n3;
            }
            else if (char1 == '<') {
                n2 = (n = i + 1);
            }
            else if (char1 == '>') {
                b = true;
            }
            else if (char1 == ',') {
                if (n2 > n) {
                    this.toCanonical(s.substring(n, n2));
                }
                n = i + 1;
                b = false;
            }
            else if (char1 > ' ' && !b) {
                n2 = i + 1;
            }
            else if (n == i) {
                ++n;
            }
            ++i;
        }
        if (n2 > n) {
            this.toCanonical(s.substring(n, n2));
        }
    }
    
    public void from(final String s) throws IOException {
        if (s.indexOf(10) != -1) {
            throw new IOException("Illegal SMTP command", new IllegalArgumentException("Illegal carriage return"));
        }
        if (s.startsWith("<")) {
            this.issueCommand("mail from: " + s + "\r\n", 250);
        }
        else {
            this.issueCommand("mail from: <" + s + ">\r\n", 250);
        }
    }
    
    private void openServer(final String mailhost) throws IOException {
        this.openServer(this.mailhost = mailhost, SmtpClient.DEFAULT_SMTP_PORT);
        this.issueCommand("helo " + InetAddress.getLocalHost().getHostName() + "\r\n", 250);
    }
    
    public PrintStream startMessage() throws IOException {
        this.issueCommand("data\r\n", 354);
        try {
            this.message = new SmtpPrintStream(this.serverOutput, this);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new InternalError(SmtpClient.encoding + " encoding not found", ex);
        }
        return this.message;
    }
    
    void closeMessage() throws IOException {
        if (this.message != null) {
            this.message.close();
        }
    }
    
    public SmtpClient(final String mailhost) throws IOException {
        if (mailhost != null) {
            try {
                this.openServer(mailhost);
                this.mailhost = mailhost;
                return;
            }
            catch (final Exception ex) {}
        }
        try {
            this.mailhost = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("mail.host"));
            if (this.mailhost != null) {
                this.openServer(this.mailhost);
                return;
            }
        }
        catch (final Exception ex2) {}
        try {
            this.openServer(this.mailhost = "localhost");
        }
        catch (final Exception ex3) {
            this.openServer(this.mailhost = "mailhost");
        }
    }
    
    public SmtpClient() throws IOException {
        this(null);
    }
    
    public SmtpClient(final int connectTimeout) throws IOException {
        this.setConnectTimeout(connectTimeout);
        try {
            this.mailhost = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("mail.host"));
            if (this.mailhost != null) {
                this.openServer(this.mailhost);
                return;
            }
        }
        catch (final Exception ex) {}
        try {
            this.openServer(this.mailhost = "localhost");
        }
        catch (final Exception ex2) {
            this.openServer(this.mailhost = "mailhost");
        }
    }
    
    public String getMailHost() {
        return this.mailhost;
    }
    
    String getEncoding() {
        return SmtpClient.encoding;
    }
    
    static {
        SmtpClient.DEFAULT_SMTP_PORT = 25;
    }
}
