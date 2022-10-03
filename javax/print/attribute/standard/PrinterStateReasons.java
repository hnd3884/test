package javax.print.attribute.standard;

import java.util.NoSuchElementException;
import java.util.AbstractSet;
import java.util.Set;
import javax.print.attribute.Attribute;
import java.util.Iterator;
import java.util.Map;
import javax.print.attribute.PrintServiceAttribute;
import java.util.HashMap;

public final class PrinterStateReasons extends HashMap<PrinterStateReason, Severity> implements PrintServiceAttribute
{
    private static final long serialVersionUID = -3731791085163619457L;
    
    public PrinterStateReasons() {
    }
    
    public PrinterStateReasons(final int n) {
        super(n);
    }
    
    public PrinterStateReasons(final int n, final float n2) {
        super(n, n2);
    }
    
    public PrinterStateReasons(final Map<PrinterStateReason, Severity> map) {
        this();
        for (final Map.Entry entry : map.entrySet()) {
            this.put((PrinterStateReason)entry.getKey(), (Severity)entry.getValue());
        }
    }
    
    @Override
    public Severity put(final PrinterStateReason printerStateReason, final Severity severity) {
        if (printerStateReason == null) {
            throw new NullPointerException("reason is null");
        }
        if (severity == null) {
            throw new NullPointerException("severity is null");
        }
        return super.put(printerStateReason, severity);
    }
    
    @Override
    public final Class<? extends Attribute> getCategory() {
        return PrinterStateReasons.class;
    }
    
    @Override
    public final String getName() {
        return "printer-state-reasons";
    }
    
    public Set<PrinterStateReason> printerStateReasonSet(final Severity severity) {
        if (severity == null) {
            throw new NullPointerException("severity is null");
        }
        return new PrinterStateReasonSet(severity, this.entrySet());
    }
    
    private class PrinterStateReasonSet extends AbstractSet<PrinterStateReason>
    {
        private Severity mySeverity;
        private Set myEntrySet;
        
        public PrinterStateReasonSet(final Severity mySeverity, final Set myEntrySet) {
            this.mySeverity = mySeverity;
            this.myEntrySet = myEntrySet;
        }
        
        @Override
        public int size() {
            int n = 0;
            final Iterator iterator = this.iterator();
            while (iterator.hasNext()) {
                iterator.next();
                ++n;
            }
            return n;
        }
        
        @Override
        public Iterator iterator() {
            return new PrinterStateReasonSetIterator(this.mySeverity, this.myEntrySet.iterator());
        }
    }
    
    private class PrinterStateReasonSetIterator implements Iterator
    {
        private Severity mySeverity;
        private Iterator myIterator;
        private Map.Entry myEntry;
        
        public PrinterStateReasonSetIterator(final Severity mySeverity, final Iterator myIterator) {
            this.mySeverity = mySeverity;
            this.myIterator = myIterator;
            this.goToNext();
        }
        
        private void goToNext() {
            this.myEntry = null;
            while (this.myEntry == null && this.myIterator.hasNext()) {
                this.myEntry = this.myIterator.next();
                if (this.myEntry.getValue() != this.mySeverity) {
                    this.myEntry = null;
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.myEntry != null;
        }
        
        @Override
        public Object next() {
            if (this.myEntry == null) {
                throw new NoSuchElementException();
            }
            final Object key = this.myEntry.getKey();
            this.goToNext();
            return key;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
