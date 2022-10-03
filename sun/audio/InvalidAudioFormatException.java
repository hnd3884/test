package sun.audio;

import java.io.IOException;

final class InvalidAudioFormatException extends IOException
{
    InvalidAudioFormatException() {
    }
    
    InvalidAudioFormatException(final String s) {
        super(s);
    }
}
