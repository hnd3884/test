package sun.awt.image;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class FileImageSource extends InputStreamImageSource
{
    String imagefile;
    
    public FileImageSource(final String imagefile) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkRead(imagefile);
        }
        this.imagefile = imagefile;
    }
    
    @Override
    final boolean checkSecurity(final Object o, final boolean b) {
        return true;
    }
    
    @Override
    protected ImageDecoder getDecoder() {
        if (this.imagefile == null) {
            return null;
        }
        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(this.imagefile));
        }
        catch (final FileNotFoundException ex) {
            return null;
        }
        return this.getDecoder(bufferedInputStream);
    }
}
