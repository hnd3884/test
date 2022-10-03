package sun.rmi.transport.proxy;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.Socket;
import java.net.InetAddress;
import java.io.IOException;
import java.io.DataInputStream;

final class CGIForwardCommand implements CGICommandHandler
{
    @Override
    public String getName() {
        return "forward";
    }
    
    private String getLine(final DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readLine();
    }
    
    @Override
    public void execute(final String s) throws CGIClientException, CGIServerException {
        if (!CGIHandler.RequestMethod.equals("POST")) {
            throw new CGIClientException("can only forward POST requests");
        }
        int int1;
        try {
            int1 = Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            throw new CGIClientException("invalid port number.", ex);
        }
        if (int1 <= 0 || int1 > 65535) {
            throw new CGIClientException("invalid port: " + int1);
        }
        if (int1 < 1024) {
            throw new CGIClientException("permission denied for port: " + int1);
        }
        Socket socket;
        try {
            socket = new Socket(InetAddress.getLocalHost(), int1);
        }
        catch (final IOException ex2) {
            throw new CGIServerException("could not connect to local port", ex2);
        }
        final DataInputStream dataInputStream = new DataInputStream(System.in);
        final byte[] array = new byte[CGIHandler.ContentLength];
        try {
            dataInputStream.readFully(array);
        }
        catch (final EOFException ex3) {
            throw new CGIClientException("unexpected EOF reading request body", ex3);
        }
        catch (final IOException ex4) {
            throw new CGIClientException("error reading request body", ex4);
        }
        try {
            final DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeBytes("POST / HTTP/1.0\r\n");
            dataOutputStream.writeBytes("Content-length: " + CGIHandler.ContentLength + "\r\n\r\n");
            dataOutputStream.write(array);
            dataOutputStream.flush();
        }
        catch (final IOException ex5) {
            throw new CGIServerException("error writing to server", ex5);
        }
        DataInputStream dataInputStream2;
        try {
            dataInputStream2 = new DataInputStream(socket.getInputStream());
        }
        catch (final IOException ex6) {
            throw new CGIServerException("error reading from server", ex6);
        }
        final String lowerCase = "Content-length:".toLowerCase();
        int n = 0;
        int int2 = -1;
        while (true) {
            String line;
            try {
                line = this.getLine(dataInputStream2);
            }
            catch (final IOException ex7) {
                throw new CGIServerException("error reading from server", ex7);
            }
            if (line == null) {
                throw new CGIServerException("unexpected EOF reading server response");
            }
            if (line.toLowerCase().startsWith(lowerCase)) {
                if (n != 0) {
                    throw new CGIServerException("Multiple Content-length entries found.");
                }
                int2 = Integer.parseInt(line.substring(lowerCase.length()).trim());
                n = 1;
            }
            if (line.length() != 0 && line.charAt(0) != '\r' && line.charAt(0) != '\n') {
                continue;
            }
            if (n == 0 || int2 < 0) {
                throw new CGIServerException("missing or invalid content length in server response");
            }
            final byte[] array2 = new byte[int2];
            try {
                dataInputStream2.readFully(array2);
            }
            catch (final EOFException ex8) {
                throw new CGIServerException("unexpected EOF reading server response", ex8);
            }
            catch (final IOException ex9) {
                throw new CGIServerException("error reading from server", ex9);
            }
            System.out.println("Status: 200 OK");
            System.out.println("Content-type: application/octet-stream");
            System.out.println("");
            try {
                System.out.write(array2);
            }
            catch (final IOException ex10) {
                throw new CGIServerException("error writing response", ex10);
            }
            System.out.flush();
        }
    }
}
