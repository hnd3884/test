package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.LocaleUtility;
import java.util.Locale;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import java.text.Collator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;

public class NodeSortRecordFactory
{
    private static int DESCENDING;
    private static int NUMBER;
    private final DOM _dom;
    private final String _className;
    private Class _class;
    private SortSettings _sortSettings;
    protected Collator _collator;
    
    @Deprecated
    public NodeSortRecordFactory(final DOM dom, final String className, final Translet translet, final String[] order, final String[] type) throws TransletException {
        this(dom, className, translet, order, type, null, null);
    }
    
    public NodeSortRecordFactory(final DOM dom, final String className, final Translet translet, final String[] order, final String[] type, String[] lang, String[] caseOrder) throws TransletException {
        try {
            this._dom = dom;
            this._className = className;
            this._class = translet.getAuxiliaryClass(className);
            if (this._class == null) {
                this._class = ObjectFactory.findProviderClass(className, true);
            }
            final int levels = order.length;
            final int[] iOrder = new int[levels];
            final int[] iType = new int[levels];
            for (int i = 0; i < levels; ++i) {
                if (order[i].length() == NodeSortRecordFactory.DESCENDING) {
                    iOrder[i] = 1;
                }
                if (type[i].length() == NodeSortRecordFactory.NUMBER) {
                    iType[i] = 1;
                }
            }
            String[] emptyStringArray = null;
            if (lang == null || caseOrder == null) {
                final int numSortKeys = order.length;
                emptyStringArray = new String[numSortKeys];
                for (int j = 0; j < numSortKeys; ++j) {
                    emptyStringArray[j] = "";
                }
            }
            if (lang == null) {
                lang = emptyStringArray;
            }
            if (caseOrder == null) {
                caseOrder = emptyStringArray;
            }
            final int length = lang.length;
            final Locale[] locales = new Locale[length];
            final Collator[] collators = new Collator[length];
            for (int k = 0; k < length; ++k) {
                locales[k] = LocaleUtility.langToLocale(lang[k]);
                collators[k] = Collator.getInstance(locales[k]);
            }
            this._sortSettings = new SortSettings((AbstractTranslet)translet, iOrder, iType, locales, collators, caseOrder);
        }
        catch (final ClassNotFoundException e) {
            throw new TransletException(e);
        }
    }
    
    public NodeSortRecord makeNodeSortRecord(final int node, final int last) throws ExceptionInInitializerError, LinkageError, IllegalAccessException, InstantiationException, SecurityException, TransletException {
        final NodeSortRecord sortRecord = this._class.newInstance();
        sortRecord.initialize(node, last, this._dom, this._sortSettings);
        return sortRecord;
    }
    
    public String getClassName() {
        return this._className;
    }
    
    private final void setLang(final String[] lang) {
    }
    
    static {
        NodeSortRecordFactory.DESCENDING = "descending".length();
        NodeSortRecordFactory.NUMBER = "number".length();
    }
}
