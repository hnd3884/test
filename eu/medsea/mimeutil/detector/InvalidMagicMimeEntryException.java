package eu.medsea.mimeutil.detector;

import java.util.List;

class InvalidMagicMimeEntryException extends RuntimeException
{
    private static final long serialVersionUID = -6705937358834408523L;
    
    public InvalidMagicMimeEntryException() {
        super("Invalid Magic Mime Entry: Unknown entry");
    }
    
    public InvalidMagicMimeEntryException(final List mimeMagicEntry) {
        super("Invalid Magic Mime Entry: " + mimeMagicEntry.toString());
    }
    
    public InvalidMagicMimeEntryException(final List mimeMagicEntry, final Throwable t) {
        super("Invalid Magic Mime Entry: " + mimeMagicEntry.toString(), t);
    }
    
    public InvalidMagicMimeEntryException(final Throwable t) {
        super(t);
    }
    
    public InvalidMagicMimeEntryException(final String message, final Throwable t) {
        super(message, t);
    }
}
