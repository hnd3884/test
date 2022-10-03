package sun.net;

import java.io.IOException;
import java.util.Vector;

public class TransferProtocolClient extends NetworkClient
{
    static final boolean debug = false;
    protected Vector<String> serverResponse;
    protected int lastReplyCode;
    
    public int readServerResponse() throws IOException {
        final StringBuffer sb = new StringBuffer(32);
        int n = -1;
        this.serverResponse.setSize(0);
        int int1;
        while (true) {
            int n2;
            if ((n2 = this.serverInput.read()) != -1) {
                if (n2 == 13 && (n2 = this.serverInput.read()) != 10) {
                    sb.append('\r');
                }
                sb.append((char)n2);
                if (n2 != 10) {
                    continue;
                }
            }
            final String string = sb.toString();
            sb.setLength(0);
            if (string.length() == 0) {
                int1 = -1;
            }
            else {
                try {
                    int1 = Integer.parseInt(string.substring(0, 3));
                }
                catch (final NumberFormatException ex) {
                    int1 = -1;
                }
                catch (final StringIndexOutOfBoundsException ex2) {
                    continue;
                }
            }
            this.serverResponse.addElement(string);
            if (n != -1) {
                if (int1 != n) {
                    continue;
                }
                if (string.length() >= 4 && string.charAt(3) == '-') {
                    continue;
                }
                break;
            }
            else {
                if (string.length() < 4 || string.charAt(3) != '-') {
                    break;
                }
                n = int1;
            }
        }
        return this.lastReplyCode = int1;
    }
    
    public void sendServer(final String s) {
        this.serverOutput.print(s);
    }
    
    public String getResponseString() {
        return this.serverResponse.elementAt(0);
    }
    
    public Vector<String> getResponseStrings() {
        return this.serverResponse;
    }
    
    public TransferProtocolClient(final String s, final int n) throws IOException {
        super(s, n);
        this.serverResponse = new Vector<String>(1);
    }
    
    public TransferProtocolClient() {
        this.serverResponse = new Vector<String>(1);
    }
}
