package sun.awt.windows;

import java.net.URI;
import java.io.IOException;
import java.io.File;
import java.awt.Desktop;
import java.awt.peer.DesktopPeer;

final class WDesktopPeer implements DesktopPeer
{
    private static String ACTION_OPEN_VERB;
    private static String ACTION_EDIT_VERB;
    private static String ACTION_PRINT_VERB;
    
    @Override
    public boolean isSupported(final Desktop.Action action) {
        return true;
    }
    
    @Override
    public void open(final File file) throws IOException {
        this.ShellExecute(file, WDesktopPeer.ACTION_OPEN_VERB);
    }
    
    @Override
    public void edit(final File file) throws IOException {
        this.ShellExecute(file, WDesktopPeer.ACTION_EDIT_VERB);
    }
    
    @Override
    public void print(final File file) throws IOException {
        this.ShellExecute(file, WDesktopPeer.ACTION_PRINT_VERB);
    }
    
    @Override
    public void mail(final URI uri) throws IOException {
        this.ShellExecute(uri, WDesktopPeer.ACTION_OPEN_VERB);
    }
    
    @Override
    public void browse(final URI uri) throws IOException {
        this.ShellExecute(uri, WDesktopPeer.ACTION_OPEN_VERB);
    }
    
    private void ShellExecute(final File file, final String s) throws IOException {
        final String shellExecute = ShellExecute(file.getAbsolutePath(), s);
        if (shellExecute != null) {
            throw new IOException("Failed to " + s + " " + file + ". Error message: " + shellExecute);
        }
    }
    
    private void ShellExecute(final URI uri, final String s) throws IOException {
        final String shellExecute = ShellExecute(uri.toString(), s);
        if (shellExecute != null) {
            throw new IOException("Failed to " + s + " " + uri + ". Error message: " + shellExecute);
        }
    }
    
    private static native String ShellExecute(final String p0, final String p1);
    
    static {
        WDesktopPeer.ACTION_OPEN_VERB = "open";
        WDesktopPeer.ACTION_EDIT_VERB = "edit";
        WDesktopPeer.ACTION_PRINT_VERB = "print";
    }
}
