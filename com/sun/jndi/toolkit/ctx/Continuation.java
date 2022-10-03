package com.sun.jndi.toolkit.ctx;

import javax.naming.LinkRef;
import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.CannotProceedException;
import javax.naming.NamingException;
import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.spi.ResolveResult;

public class Continuation extends ResolveResult
{
    protected Name starter;
    protected Object followingLink;
    protected Hashtable<?, ?> environment;
    protected boolean continuing;
    protected Context resolvedContext;
    protected Name relativeResolvedName;
    private static final long serialVersionUID = 8162530656132624308L;
    
    public Continuation() {
        this.followingLink = null;
        this.environment = null;
        this.continuing = false;
        this.resolvedContext = null;
        this.relativeResolvedName = null;
    }
    
    public Continuation(final Name starter, final Hashtable<?, ?> hashtable) {
        this.followingLink = null;
        this.environment = null;
        this.continuing = false;
        this.resolvedContext = null;
        this.relativeResolvedName = null;
        this.starter = starter;
        this.environment = (Hashtable<?, ?>)((hashtable == null) ? null : hashtable.clone());
    }
    
    public boolean isContinue() {
        return this.continuing;
    }
    
    public void setSuccess() {
        this.continuing = false;
    }
    
    public NamingException fillInException(final NamingException ex) {
        ex.setRemainingName(this.remainingName);
        ex.setResolvedObj(this.resolvedObj);
        if (this.starter == null || this.starter.isEmpty()) {
            ex.setResolvedName(null);
        }
        else if (this.remainingName == null) {
            ex.setResolvedName(this.starter);
        }
        else {
            ex.setResolvedName(this.starter.getPrefix(this.starter.size() - this.remainingName.size()));
        }
        if (ex instanceof CannotProceedException) {
            final CannotProceedException ex2 = (CannotProceedException)ex;
            ex2.setEnvironment((Hashtable<?, ?>)((this.environment == null) ? new Hashtable<Object, Object>(11) : this.environment.clone()));
            ex2.setAltNameCtx(this.resolvedContext);
            ex2.setAltName(this.relativeResolvedName);
        }
        return ex;
    }
    
    public void setErrorNNS(final Object o, final Name name) {
        final Name name2 = (Name)name.clone();
        try {
            name2.add("");
        }
        catch (final InvalidNameException ex) {}
        this.setErrorAux(o, name2);
    }
    
    public void setErrorNNS(final Object o, final String s) {
        final CompositeName compositeName = new CompositeName();
        try {
            if (s != null && !s.equals("")) {
                compositeName.add(s);
            }
            compositeName.add("");
        }
        catch (final InvalidNameException ex) {}
        this.setErrorAux(o, compositeName);
    }
    
    public void setError(final Object o, final Name name) {
        if (name != null) {
            this.remainingName = (Name)name.clone();
        }
        else {
            this.remainingName = null;
        }
        this.setErrorAux(o, this.remainingName);
    }
    
    public void setError(final Object o, final String s) {
        final CompositeName compositeName = new CompositeName();
        if (s != null && !s.equals("")) {
            try {
                compositeName.add(s);
            }
            catch (final InvalidNameException ex) {}
        }
        this.setErrorAux(o, compositeName);
    }
    
    private void setErrorAux(final Object resolvedObj, final Name remainingName) {
        this.remainingName = remainingName;
        this.resolvedObj = resolvedObj;
        this.continuing = false;
    }
    
    private void setContinueAux(final Object resolvedObj, final Name relativeResolvedName, final Context resolvedContext, final Name remainingName) {
        if (resolvedObj instanceof LinkRef) {
            this.setContinueLink(resolvedObj, relativeResolvedName, resolvedContext, remainingName);
        }
        else {
            this.remainingName = remainingName;
            this.resolvedObj = resolvedObj;
            this.relativeResolvedName = relativeResolvedName;
            this.resolvedContext = resolvedContext;
            this.continuing = true;
        }
    }
    
    public void setContinueNNS(final Object o, final Name name, final Context context) {
        final CompositeName compositeName = new CompositeName();
        this.setContinue(o, name, context, PartialCompositeContext._NNS_NAME);
    }
    
    public void setContinueNNS(final Object o, final String s, final Context context) {
        final CompositeName compositeName = new CompositeName();
        try {
            compositeName.add(s);
        }
        catch (final NamingException ex) {}
        this.setContinue(o, compositeName, context, PartialCompositeContext._NNS_NAME);
    }
    
    public void setContinue(final Object o, final Name name, final Context context) {
        this.setContinueAux(o, name, context, (Name)PartialCompositeContext._EMPTY_NAME.clone());
    }
    
    public void setContinue(final Object o, final Name name, final Context context, final Name name2) {
        if (name2 != null) {
            this.remainingName = (Name)name2.clone();
        }
        else {
            this.remainingName = new CompositeName();
        }
        this.setContinueAux(o, name, context, this.remainingName);
    }
    
    public void setContinue(final Object o, final String s, final Context context, final String s2) {
        final CompositeName compositeName = new CompositeName();
        if (!s.equals("")) {
            try {
                compositeName.add(s);
            }
            catch (final NamingException ex) {}
        }
        final CompositeName compositeName2 = new CompositeName();
        if (!s2.equals("")) {
            try {
                compositeName2.add(s2);
            }
            catch (final NamingException ex2) {}
        }
        this.setContinueAux(o, compositeName, context, compositeName2);
    }
    
    @Deprecated
    public void setContinue(final Object o, final Object o2) {
        this.setContinue(o, null, (Context)o2);
    }
    
    private void setContinueLink(final Object followingLink, final Name name, final Context context, final Name remainingName) {
        this.followingLink = followingLink;
        this.remainingName = remainingName;
        this.resolvedObj = context;
        this.relativeResolvedName = PartialCompositeContext._EMPTY_NAME;
        this.resolvedContext = context;
        this.continuing = true;
    }
    
    @Override
    public String toString() {
        if (this.remainingName != null) {
            return this.starter.toString() + "; remainingName: '" + this.remainingName + "'";
        }
        return this.starter.toString();
    }
    
    public String toString(final boolean b) {
        if (!b || this.resolvedObj == null) {
            return this.toString();
        }
        return this.toString() + "; resolvedObj: " + this.resolvedObj + "; relativeResolvedName: " + this.relativeResolvedName + "; resolvedContext: " + this.resolvedContext;
    }
}
