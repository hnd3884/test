package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.StringComparable;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;
import java.util.Locale;
import java.text.Collator;

public abstract class NodeSortRecord
{
    public static final int COMPARE_STRING = 0;
    public static final int COMPARE_NUMERIC = 1;
    public static final int COMPARE_ASCENDING = 0;
    public static final int COMPARE_DESCENDING = 1;
    @Deprecated
    private static final Collator DEFAULT_COLLATOR;
    @Deprecated
    protected Collator _collator;
    protected Collator[] _collators;
    @Deprecated
    protected Locale _locale;
    protected CollatorFactory _collatorFactory;
    protected SortSettings _settings;
    private DOM _dom;
    private int _node;
    private int _last;
    private int _scanned;
    private Object[] _values;
    
    public NodeSortRecord(final int node) {
        this._collator = NodeSortRecord.DEFAULT_COLLATOR;
        this._dom = null;
        this._last = 0;
        this._scanned = 0;
        this._node = node;
    }
    
    public NodeSortRecord() {
        this(0);
    }
    
    public final void initialize(final int node, final int last, final DOM dom, final SortSettings settings) throws TransletException {
        this._dom = dom;
        this._node = node;
        this._last = last;
        this._settings = settings;
        final int levels = settings.getSortOrders().length;
        this._values = new Object[levels];
        String colFactClassname = null;
        try {
            colFactClassname = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.xsltc.COLLATOR_FACTORY");
        }
        catch (final SecurityException ex) {}
        if (colFactClassname != null) {
            try {
                final Object candObj = ObjectFactory.findProviderClass(colFactClassname, true);
                this._collatorFactory = (CollatorFactory)candObj;
            }
            catch (final ClassNotFoundException e) {
                throw new TransletException(e);
            }
            final Locale[] locales = settings.getLocales();
            this._collators = new Collator[levels];
            for (int i = 0; i < levels; ++i) {
                this._collators[i] = this._collatorFactory.getCollator(locales[i]);
            }
            this._collator = this._collators[0];
        }
        else {
            this._collators = settings.getCollators();
            this._collator = this._collators[0];
        }
    }
    
    public final int getNode() {
        return this._node;
    }
    
    public final int compareDocOrder(final NodeSortRecord other) {
        return this._node - other._node;
    }
    
    private final Comparable stringValue(final int level) {
        if (this._scanned <= level) {
            final AbstractTranslet translet = this._settings.getTranslet();
            final Locale[] locales = this._settings.getLocales();
            final String[] caseOrder = this._settings.getCaseOrders();
            final String str = this.extractValueFromDOM(this._dom, this._node, level, translet, this._last);
            final Comparable key = StringComparable.getComparator(str, locales[level], this._collators[level], caseOrder[level]);
            return (Comparable)(this._values[this._scanned++] = key);
        }
        return (Comparable)this._values[level];
    }
    
    private final Double numericValue(final int level) {
        if (this._scanned <= level) {
            final AbstractTranslet translet = this._settings.getTranslet();
            final String str = this.extractValueFromDOM(this._dom, this._node, level, translet, this._last);
            Double num;
            try {
                num = new Double(str);
            }
            catch (final NumberFormatException e) {
                num = new Double(Double.NEGATIVE_INFINITY);
            }
            return (Double)(this._values[this._scanned++] = num);
        }
        return (Double)this._values[level];
    }
    
    public int compareTo(final NodeSortRecord other) {
        final int[] sortOrder = this._settings.getSortOrders();
        final int levels = this._settings.getSortOrders().length;
        final int[] compareTypes = this._settings.getTypes();
        for (int level = 0; level < levels; ++level) {
            int cmp;
            if (compareTypes[level] == 1) {
                final Double our = this.numericValue(level);
                final Double their = other.numericValue(level);
                cmp = our.compareTo(their);
            }
            else {
                final Comparable our2 = this.stringValue(level);
                final Comparable their2 = other.stringValue(level);
                cmp = our2.compareTo(their2);
            }
            if (cmp != 0) {
                return (sortOrder[level] == 1) ? (0 - cmp) : cmp;
            }
        }
        return this._node - other._node;
    }
    
    public Collator[] getCollator() {
        return this._collators;
    }
    
    public abstract String extractValueFromDOM(final DOM p0, final int p1, final int p2, final AbstractTranslet p3, final int p4);
    
    static {
        DEFAULT_COLLATOR = Collator.getInstance();
    }
}
