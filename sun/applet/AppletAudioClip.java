package sun.applet;

import com.sun.media.sound.JavaSoundAudioClip;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.io.IOException;
import java.net.URL;
import java.applet.AudioClip;

public class AppletAudioClip implements AudioClip
{
    private URL url;
    private AudioClip audioClip;
    boolean DEBUG;
    
    public AppletAudioClip(final URL url) {
        this.url = null;
        this.audioClip = null;
        this.DEBUG = false;
        this.url = url;
        try {
            this.createAppletAudioClip(url.openStream());
        }
        catch (final IOException ex) {
            if (this.DEBUG) {
                System.err.println("IOException creating AppletAudioClip" + ex);
            }
        }
    }
    
    public AppletAudioClip(final URLConnection urlConnection) {
        this.url = null;
        this.audioClip = null;
        this.DEBUG = false;
        try {
            this.createAppletAudioClip(urlConnection.getInputStream());
        }
        catch (final IOException ex) {
            if (this.DEBUG) {
                System.err.println("IOException creating AppletAudioClip" + ex);
            }
        }
    }
    
    public AppletAudioClip(final byte[] array) {
        this.url = null;
        this.audioClip = null;
        this.DEBUG = false;
        try {
            this.createAppletAudioClip(new ByteArrayInputStream(array));
        }
        catch (final IOException ex) {
            if (this.DEBUG) {
                System.err.println("IOException creating AppletAudioClip " + ex);
            }
        }
    }
    
    void createAppletAudioClip(final InputStream inputStream) throws IOException {
        try {
            this.audioClip = new JavaSoundAudioClip(inputStream);
        }
        catch (final Exception ex) {
            throw new IOException("Failed to construct the AudioClip: " + ex);
        }
    }
    
    @Override
    public synchronized void play() {
        if (this.audioClip != null) {
            this.audioClip.play();
        }
    }
    
    @Override
    public synchronized void loop() {
        if (this.audioClip != null) {
            this.audioClip.loop();
        }
    }
    
    @Override
    public synchronized void stop() {
        if (this.audioClip != null) {
            this.audioClip.stop();
        }
    }
}
