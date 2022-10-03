package java.beans;

import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.image.ImageProducer;
import java.awt.Image;
import java.applet.AudioClip;
import java.net.URL;
import java.util.Hashtable;
import java.applet.Applet;
import java.applet.AppletContext;

class BeansAppletContext implements AppletContext
{
    Applet target;
    Hashtable<URL, Object> imageCache;
    
    BeansAppletContext(final Applet target) {
        this.imageCache = new Hashtable<URL, Object>();
        this.target = target;
    }
    
    @Override
    public AudioClip getAudioClip(final URL url) {
        try {
            return (AudioClip)url.getContent();
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public synchronized Image getImage(final URL url) {
        final Image value = this.imageCache.get(url);
        if (value != null) {
            return value;
        }
        try {
            final Object content = url.getContent();
            if (content == null) {
                return null;
            }
            if (content instanceof Image) {
                this.imageCache.put(url, content);
                return (Image)content;
            }
            final Image image = this.target.createImage((ImageProducer)content);
            this.imageCache.put(url, image);
            return image;
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    @Override
    public Applet getApplet(final String s) {
        return null;
    }
    
    @Override
    public Enumeration<Applet> getApplets() {
        final Vector vector = new Vector();
        vector.addElement(this.target);
        return vector.elements();
    }
    
    @Override
    public void showDocument(final URL url) {
    }
    
    @Override
    public void showDocument(final URL url, final String s) {
    }
    
    @Override
    public void showStatus(final String s) {
    }
    
    @Override
    public void setStream(final String s, final InputStream inputStream) throws IOException {
    }
    
    @Override
    public InputStream getStream(final String s) {
        return null;
    }
    
    @Override
    public Iterator<String> getStreamKeys() {
        return null;
    }
}
