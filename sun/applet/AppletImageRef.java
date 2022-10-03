package sun.applet;

import java.awt.image.ImageProducer;
import sun.awt.image.URLImageSource;
import java.awt.Toolkit;
import java.net.URL;
import sun.misc.Ref;

class AppletImageRef extends Ref
{
    URL url;
    
    AppletImageRef(final URL url) {
        this.url = url;
    }
    
    @Override
    public void flush() {
        super.flush();
    }
    
    @Override
    public Object reconstitute() {
        return Toolkit.getDefaultToolkit().createImage(new URLImageSource(this.url));
    }
}
