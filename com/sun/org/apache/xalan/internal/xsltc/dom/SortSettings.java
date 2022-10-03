package com.sun.org.apache.xalan.internal.xsltc.dom;

import java.text.Collator;
import java.util.Locale;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;

final class SortSettings
{
    private AbstractTranslet _translet;
    private int[] _sortOrders;
    private int[] _types;
    private Locale[] _locales;
    private Collator[] _collators;
    private String[] _caseOrders;
    
    SortSettings(final AbstractTranslet translet, final int[] sortOrders, final int[] types, final Locale[] locales, final Collator[] collators, final String[] caseOrders) {
        this._translet = translet;
        this._sortOrders = sortOrders;
        this._types = types;
        this._locales = locales;
        this._collators = collators;
        this._caseOrders = caseOrders;
    }
    
    AbstractTranslet getTranslet() {
        return this._translet;
    }
    
    int[] getSortOrders() {
        return this._sortOrders;
    }
    
    int[] getTypes() {
        return this._types;
    }
    
    Locale[] getLocales() {
        return this._locales;
    }
    
    Collator[] getCollators() {
        return this._collators;
    }
    
    String[] getCaseOrders() {
        return this._caseOrders;
    }
}
