package javax.sound.sampled;

import java.security.BasicPermission;

public class AudioPermission extends BasicPermission
{
    public AudioPermission(final String s) {
        super(s);
    }
    
    public AudioPermission(final String s, final String s2) {
        super(s, s2);
    }
}
