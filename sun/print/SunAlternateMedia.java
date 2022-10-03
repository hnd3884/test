package sun.print;

import javax.print.attribute.standard.Media;
import javax.print.attribute.PrintRequestAttribute;

public class SunAlternateMedia implements PrintRequestAttribute
{
    private static final long serialVersionUID = -8878868345472850201L;
    private Media media;
    
    public SunAlternateMedia(final Media media) {
        this.media = media;
    }
    
    public Media getMedia() {
        return this.media;
    }
    
    @Override
    public final Class getCategory() {
        return SunAlternateMedia.class;
    }
    
    @Override
    public final String getName() {
        return "sun-alternate-media";
    }
    
    @Override
    public String toString() {
        return "alternate-media: " + this.media.toString();
    }
    
    @Override
    public int hashCode() {
        return this.media.hashCode();
    }
}
