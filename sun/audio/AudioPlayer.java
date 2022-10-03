package sun.audio;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class AudioPlayer extends Thread
{
    private final AudioDevice devAudio;
    private static final boolean DEBUG = false;
    public static final AudioPlayer player;
    
    private static ThreadGroup getAudioThreadGroup() {
        ThreadGroup threadGroup;
        for (threadGroup = Thread.currentThread().getThreadGroup(); threadGroup.getParent() != null && threadGroup.getParent().getParent() != null; threadGroup = threadGroup.getParent()) {}
        return threadGroup;
    }
    
    private static AudioPlayer getAudioPlayer() {
        return AccessController.doPrivileged((PrivilegedAction<AudioPlayer>)new PrivilegedAction() {
            @Override
            public Object run() {
                final AudioPlayer audioPlayer = new AudioPlayer((AudioPlayer$1)null);
                audioPlayer.setPriority(10);
                audioPlayer.setDaemon(true);
                audioPlayer.start();
                return audioPlayer;
            }
        });
    }
    
    private AudioPlayer() {
        super(getAudioThreadGroup(), "Audio Player");
        (this.devAudio = AudioDevice.device).open();
    }
    
    public synchronized void start(final InputStream inputStream) {
        this.devAudio.openChannel(inputStream);
        this.notify();
    }
    
    public synchronized void stop(final InputStream inputStream) {
        this.devAudio.closeChannel(inputStream);
    }
    
    @Override
    public void run() {
        this.devAudio.play();
        try {
            while (true) {
                Thread.sleep(5000L);
            }
        }
        catch (final Exception ex) {}
    }
    
    static {
        player = getAudioPlayer();
    }
}
