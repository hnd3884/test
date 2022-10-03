package com.me.devicemanagement.onpremise.start;

import java.util.logging.Level;
import java.util.Enumeration;
import java.io.IOException;
import java.util.Vector;
import java.io.BufferedInputStream;
import java.util.logging.Logger;

public class StreamReader implements Runnable
{
    private static final Logger LOGGER;
    private Process proc;
    private BufferedInputStream inBufRead;
    Vector outputProcessers;
    private boolean isInputStream;
    
    public StreamReader(final Process proc, final boolean isInputStream) {
        this.outputProcessers = new Vector();
        this.isInputStream = isInputStream;
        if (isInputStream) {
            this.inBufRead = new BufferedInputStream(proc.getInputStream());
        }
        else {
            this.inBufRead = new BufferedInputStream(proc.getErrorStream());
        }
        this.proc = proc;
    }
    
    public void closeReader() {
        if (this.inBufRead != null) {
            try {
                this.inBufRead.close();
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    public void startReading() {
        final Thread readThread = new Thread(this);
        readThread.start();
    }
    
    public void addOutputProcesser(final OutputProcesser opp) {
        this.outputProcessers.add(opp);
    }
    
    public void removeOutputProcesser(final OutputProcesser opp) {
        this.outputProcessers.remove(opp);
    }
    
    private boolean notifyAndCheckFinish(final String s) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != '\r') {
                buf.append(s.charAt(i));
                if (i + 1 < s.length()) {
                    continue;
                }
            }
            final String op = buf.toString();
            buf = new StringBuffer();
            if (!op.equals("")) {
                final Enumeration enu = this.outputProcessers.elements();
                while (enu.hasMoreElements()) {
                    final OutputProcesser opp = enu.nextElement();
                    if (this.isInputStream) {
                        if (opp.processOutput(op)) {
                            return true;
                        }
                        continue;
                    }
                    else {
                        if (opp.processError(op)) {
                            return true;
                        }
                        continue;
                    }
                }
            }
        }
        return false;
    }
    
    private void notifyEndString() {
        StreamReader.LOGGER.log(Level.INFO, "Notifying End of process to " + this.outputProcessers);
        final Enumeration enu = this.outputProcessers.elements();
        while (enu.hasMoreElements()) {
            final OutputProcesser opp = enu.nextElement();
            opp.endStringReached();
        }
    }
    
    private void notifyTerminate() {
        StreamReader.LOGGER.log(Level.INFO, "Notifying Termination of process to " + this.outputProcessers);
        final Enumeration enu = this.outputProcessers.elements();
        while (enu.hasMoreElements()) {
            final OutputProcesser opp = enu.nextElement();
            opp.terminated();
        }
        this.closeReader();
    }
    
    @Override
    public void run() {
        StreamReader.LOGGER.log(Level.INFO, "Output processers are " + this.outputProcessers);
        this.readAndNotify(this.proc, this.inBufRead);
    }
    
    private String readAndNotify(final Process proc, final BufferedInputStream inBufRead) {
        final StringBuffer strBuf = new StringBuffer();
        final StringBuffer Buf = new StringBuffer();
        StreamReader.LOGGER.log(Level.INFO, "readAndNotify");
        try {
            byte readChar = (byte)inBufRead.read();
            StreamReader.LOGGER.log(Level.INFO, "readChar " + readChar);
            while (readChar != -1) {
                StreamReader.LOGGER.log(Level.INFO, "readChar in " + readChar);
                final int size = inBufRead.available();
                if (size <= 0) {
                    try {
                        proc.exitValue();
                        break;
                    }
                    catch (final IllegalThreadStateException ex) {}
                }
                final byte[] b = new byte[size + 1];
                inBufRead.read(b, 1, size);
                b[0] = readChar;
                final String s = new String(b);
                if (!s.trim().equals("")) {
                    strBuf.append(s);
                    if (this.isInputStream) {
                        StartupUtil.getInstance().printStr(s);
                    }
                    else {
                        StartupUtil.getInstance().printErrStr(s);
                    }
                    if (this.notifyAndCheckFinish(s)) {
                        this.notifyEndString();
                        this.waitForProcessStop();
                    }
                }
                readChar = (byte)inBufRead.read();
            }
            int exitStatus = 0;
            StreamReader.LOGGER.log(Level.INFO, "exitStatus " + exitStatus);
            while (true) {
                try {
                    proc.waitFor();
                    exitStatus = proc.exitValue();
                }
                catch (final InterruptedException iex) {
                    iex.printStackTrace();
                    continue;
                }
                catch (final IllegalThreadStateException ex2) {
                    continue;
                }
                break;
            }
            StreamReader.LOGGER.log(Level.INFO, "exitStatus 1 " + exitStatus);
            if (this.isInputStream) {
                this.notifyTerminate();
            }
        }
        catch (final IOException e) {
            this.closeReader();
            e.printStackTrace();
        }
        return strBuf.toString();
    }
    
    private void waitForProcessStop() {
        final Thread waitForTh = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        StreamReader.this.proc.waitFor();
                        StreamReader.this.proc.exitValue();
                        if (StreamReader.this.isInputStream) {
                            StreamReader.this.notifyTerminate();
                        }
                    }
                    catch (final InterruptedException iex) {
                        iex.printStackTrace();
                        continue;
                    }
                    catch (final IllegalThreadStateException ex) {
                        continue;
                    }
                    catch (final Exception e) {}
                    break;
                }
            }
        };
        waitForTh.start();
    }
    
    static {
        LOGGER = Logger.getLogger(StreamReader.class.getName());
    }
}
