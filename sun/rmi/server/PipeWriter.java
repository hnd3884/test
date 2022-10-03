package sun.rmi.server;

import sun.security.action.GetPropertyAction;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.rmi.runtime.NewThreadAction;
import java.util.Date;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

class PipeWriter implements Runnable
{
    private ByteArrayOutputStream bufOut;
    private int cLast;
    private byte[] currSep;
    private PrintWriter out;
    private InputStream in;
    private String pipeString;
    private String execString;
    private static String lineSeparator;
    private static int lineSeparatorLength;
    private static int numExecs;
    
    private PipeWriter(final InputStream in, final OutputStream outputStream, final String s, final int n) {
        this.in = in;
        this.out = new PrintWriter(outputStream);
        this.bufOut = new ByteArrayOutputStream();
        this.currSep = new byte[PipeWriter.lineSeparatorLength];
        this.execString = ":ExecGroup-" + Integer.toString(n) + ':' + s + ':';
    }
    
    @Override
    public void run() {
        final byte[] array = new byte[256];
        try {
            int read;
            while ((read = this.in.read(array)) != -1) {
                this.write(array, 0, read);
            }
            final String string = this.bufOut.toString();
            this.bufOut.reset();
            if (string.length() > 0) {
                this.out.println(this.createAnnotation() + string);
                this.out.flush();
            }
        }
        catch (final IOException ex) {}
    }
    
    private void write(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 < 0) {
            throw new ArrayIndexOutOfBoundsException(n2);
        }
        for (int i = 0; i < n2; ++i) {
            this.write(array[n + i]);
        }
    }
    
    private void write(final byte b) throws IOException {
        int i;
        for (i = 1; i < this.currSep.length; ++i) {
            this.currSep[i - 1] = this.currSep[i];
        }
        this.currSep[i - 1] = b;
        this.bufOut.write(b);
        if (this.cLast >= PipeWriter.lineSeparatorLength - 1 && PipeWriter.lineSeparator.equals(new String(this.currSep))) {
            this.cLast = 0;
            this.out.print(this.createAnnotation() + this.bufOut.toString());
            this.out.flush();
            this.bufOut.reset();
            if (this.out.checkError()) {
                throw new IOException("PipeWriter: IO Exception when writing to output stream.");
            }
        }
        else {
            ++this.cLast;
        }
    }
    
    private String createAnnotation() {
        return new Date().toString() + this.execString;
    }
    
    static void plugTogetherPair(final InputStream inputStream, final OutputStream outputStream, final InputStream inputStream2, final OutputStream outputStream2) {
        final int numExec = getNumExec();
        final Thread thread = AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(new PipeWriter(inputStream, outputStream, "out", numExec), "out", true));
        final Thread thread2 = AccessController.doPrivileged((PrivilegedAction<Thread>)new NewThreadAction(new PipeWriter(inputStream2, outputStream2, "err", numExec), "err", true));
        thread.start();
        thread2.start();
    }
    
    private static synchronized int getNumExec() {
        return PipeWriter.numExecs++;
    }
    
    static {
        PipeWriter.numExecs = 0;
        PipeWriter.lineSeparator = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("line.separator"));
        PipeWriter.lineSeparatorLength = PipeWriter.lineSeparator.length();
    }
}
