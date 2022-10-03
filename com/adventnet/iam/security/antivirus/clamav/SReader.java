package com.adventnet.iam.security.antivirus.clamav;

import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import java.nio.channels.SocketChannel;

class SReader extends Thread
{
    SocketChannel sc;
    String result;
    boolean var;
    private static final Logger LOGGER;
    
    public SReader(final SocketChannel sc) {
        this.result = "";
        this.var = true;
        this.sc = sc;
    }
    
    @Override
    public void run() {
        try {
            final ByteBuffer readbuf = ByteBuffer.allocate(4096);
            while (this.var) {
                while (this.sc.read(readbuf) > 0) {
                    readbuf.flip();
                    final Charset charset = Charset.forName("us-ascii");
                    final CharsetDecoder decoder = charset.newDecoder();
                    final CharBuffer charBuffer = decoder.decode(readbuf);
                    this.result += charBuffer.toString();
                    readbuf.flip();
                    if (this.result.indexOf("stream: ") >= 0 || this.result.indexOf("INSTREAM size limit exceeded") >= 0 || !this.result.isEmpty()) {
                        this.var = false;
                        break;
                    }
                }
            }
        }
        catch (final ClosedByInterruptException e) {
            SReader.LOGGER.log(Level.WARNING, "Clam AV Response thread interrupted", e);
        }
        catch (final IOException ioex) {
            SReader.LOGGER.log(Level.WARNING, "IOException while reading stuff from clam AV", ioex);
        }
    }
    
    public boolean isRunning() {
        return this.var;
    }
    
    public void shutdown() {
        this.var = false;
        this.result += ". Thread forcibly shutdown";
    }
    
    public String getMessage() {
        return this.result;
    }
    
    public boolean getResult() {
        return this.result.indexOf("stream: OK") < 0;
    }
    
    public SocketChannel getChannel() {
        return this.sc;
    }
    
    static {
        LOGGER = Logger.getLogger(SReader.class.getName());
    }
}
