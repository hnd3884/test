package com.sun.org.apache.xerces.internal.impl.validation;

import java.util.ArrayList;
import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class ValidationState implements ValidationContext
{
    private boolean fExtraChecking;
    private boolean fFacetChecking;
    private boolean fNormalize;
    private boolean fNamespaces;
    private EntityState fEntityState;
    private NamespaceContext fNamespaceContext;
    private SymbolTable fSymbolTable;
    private Locale fLocale;
    private ArrayList<String> fIdList;
    private ArrayList<String> fIdRefList;
    
    public ValidationState() {
        this.fExtraChecking = true;
        this.fFacetChecking = true;
        this.fNormalize = true;
        this.fNamespaces = true;
        this.fEntityState = null;
        this.fNamespaceContext = null;
        this.fSymbolTable = null;
        this.fLocale = null;
    }
    
    public void setExtraChecking(final boolean newValue) {
        this.fExtraChecking = newValue;
    }
    
    public void setFacetChecking(final boolean newValue) {
        this.fFacetChecking = newValue;
    }
    
    public void setNormalizationRequired(final boolean newValue) {
        this.fNormalize = newValue;
    }
    
    public void setUsingNamespaces(final boolean newValue) {
        this.fNamespaces = newValue;
    }
    
    public void setEntityState(final EntityState state) {
        this.fEntityState = state;
    }
    
    public void setNamespaceSupport(final NamespaceContext namespace) {
        this.fNamespaceContext = namespace;
    }
    
    public void setSymbolTable(final SymbolTable sTable) {
        this.fSymbolTable = sTable;
    }
    
    public String checkIDRefID() {
        if (this.fIdList == null && this.fIdRefList != null) {
            return this.fIdRefList.get(0);
        }
        if (this.fIdRefList != null) {
            for (int i = 0; i < this.fIdRefList.size(); ++i) {
                final String key = this.fIdRefList.get(i);
                if (!this.fIdList.contains(key)) {
                    return key;
                }
            }
        }
        return null;
    }
    
    public void reset() {
        this.fExtraChecking = true;
        this.fFacetChecking = true;
        this.fNamespaces = true;
        this.fIdList = null;
        this.fIdRefList = null;
        this.fEntityState = null;
        this.fNamespaceContext = null;
        this.fSymbolTable = null;
    }
    
    public void resetIDTables() {
        this.fIdList = null;
        this.fIdRefList = null;
    }
    
    @Override
    public boolean needExtraChecking() {
        return this.fExtraChecking;
    }
    
    @Override
    public boolean needFacetChecking() {
        return this.fFacetChecking;
    }
    
    @Override
    public boolean needToNormalize() {
        return this.fNormalize;
    }
    
    @Override
    public boolean useNamespaces() {
        return this.fNamespaces;
    }
    
    @Override
    public boolean isEntityDeclared(final String name) {
        return this.fEntityState != null && this.fEntityState.isEntityDeclared(this.getSymbol(name));
    }
    
    @Override
    public boolean isEntityUnparsed(final String name) {
        return this.fEntityState != null && this.fEntityState.isEntityUnparsed(this.getSymbol(name));
    }
    
    @Override
    public boolean isIdDeclared(final String name) {
        return this.fIdList != null && this.fIdList.contains(name);
    }
    
    @Override
    public void addId(final String name) {
        if (this.fIdList == null) {
            this.fIdList = new ArrayList<String>();
        }
        this.fIdList.add(name);
    }
    
    @Override
    public void addIdRef(final String name) {
        if (this.fIdRefList == null) {
            this.fIdRefList = new ArrayList<String>();
        }
        this.fIdRefList.add(name);
    }
    
    @Override
    public String getSymbol(final String symbol) {
        if (this.fSymbolTable != null) {
            return this.fSymbolTable.addSymbol(symbol);
        }
        return symbol.intern();
    }
    
    @Override
    public String getURI(final String prefix) {
        if (this.fNamespaceContext != null) {
            return this.fNamespaceContext.getURI(prefix);
        }
        return null;
    }
    
    public void setLocale(final Locale locale) {
        this.fLocale = locale;
    }
    
    @Override
    public Locale getLocale() {
        return this.fLocale;
    }
}
