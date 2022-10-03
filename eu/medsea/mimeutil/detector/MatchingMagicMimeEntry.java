package eu.medsea.mimeutil.detector;

import eu.medsea.mimeutil.MimeType;
import java.util.Iterator;

class MatchingMagicMimeEntry
{
    private MagicMimeEntry magicMimeEntry;
    private double specificity;
    
    public MatchingMagicMimeEntry(final MagicMimeEntry magicMimeEntry) {
        this.specificity = -1.0;
        this.magicMimeEntry = magicMimeEntry;
    }
    
    public MagicMimeEntry getMagicMimeEntry() {
        return this.magicMimeEntry;
    }
    
    private int getLevel() {
        int l = 0;
        for (MagicMimeEntry parent = this.magicMimeEntry.getParent(); parent != null; parent = parent.getParent()) {
            ++l;
        }
        return l;
    }
    
    private int getRecursiveSubEntryCount() {
        return this.getRecursiveSubEntryCount(this.magicMimeEntry, 0);
    }
    
    public int getRecursiveSubEntryCount(final MagicMimeEntry entry, int subLevel) {
        ++subLevel;
        int result = 0;
        final Iterator it = entry.getSubEntries().iterator();
        while (it.hasNext()) {
            final MagicMimeEntry subEntry = it.next();
            result += subLevel * (1 + this.getRecursiveSubEntryCount(subEntry, subLevel));
        }
        return result;
    }
    
    public double getSpecificity() {
        if (this.specificity < 0.0) {
            this.specificity = (this.getLevel() + 1) / (double)(this.getRecursiveSubEntryCount() + 1);
        }
        return this.specificity;
    }
    
    public MimeType getMimeType() {
        return new MimeType(this.magicMimeEntry.getMimeType());
    }
    
    public String toString() {
        return String.valueOf(this.getClass().getName()) + '[' + this.getMimeType() + ',' + this.getSpecificity() + ']';
    }
}
