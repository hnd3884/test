package com.sun.jndi.ldap;

import java.util.Hashtable;
import javax.naming.ldap.Control;
import javax.naming.directory.Attributes;
import java.util.NoSuchElementException;
import javax.naming.PartialResultException;
import javax.naming.LimitExceededException;
import javax.naming.NamingException;
import java.util.Vector;
import com.sun.jndi.toolkit.ctx.Continuation;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NameClassPair;

abstract class AbstractLdapNamingEnumeration<T extends NameClassPair> implements NamingEnumeration<T>, ReferralEnumeration<T>
{
    protected Name listArg;
    private boolean cleaned;
    private LdapResult res;
    private LdapClient enumClnt;
    private Continuation cont;
    private Vector<LdapEntry> entries;
    private int limit;
    private int posn;
    protected LdapCtx homeCtx;
    private LdapReferralException refEx;
    private NamingException errEx;
    private boolean more;
    private boolean hasMoreCalled;
    
    AbstractLdapNamingEnumeration(final LdapCtx homeCtx, final LdapResult res, final Name listArg, final Continuation cont) throws NamingException {
        this.cleaned = false;
        this.entries = null;
        this.limit = 0;
        this.posn = 0;
        this.refEx = null;
        this.errEx = null;
        this.more = true;
        this.hasMoreCalled = false;
        if (res.status != 0 && res.status != 4 && res.status != 3 && res.status != 11 && res.status != 10 && res.status != 9) {
            throw cont.fillInException(new NamingException(LdapClient.getErrorMessage(res.status, res.errorMessage)));
        }
        this.res = res;
        this.entries = res.entries;
        this.limit = ((this.entries == null) ? 0 : this.entries.size());
        this.listArg = listArg;
        this.cont = cont;
        if (res.refEx != null) {
            this.refEx = res.refEx;
        }
        (this.homeCtx = homeCtx).incEnumCount();
        this.enumClnt = homeCtx.clnt;
    }
    
    @Override
    public final T nextElement() {
        try {
            return this.next();
        }
        catch (final NamingException ex) {
            this.cleanup();
            return null;
        }
    }
    
    @Override
    public final boolean hasMoreElements() {
        try {
            return this.hasMore();
        }
        catch (final NamingException ex) {
            this.cleanup();
            return false;
        }
    }
    
    private void getNextBatch() throws NamingException {
        this.res = this.homeCtx.getSearchReply(this.enumClnt, this.res);
        if (this.res == null) {
            final int n = 0;
            this.posn = n;
            this.limit = n;
            return;
        }
        this.entries = this.res.entries;
        this.limit = ((this.entries == null) ? 0 : this.entries.size());
        this.posn = 0;
        Label_0129: {
            if (this.res.status == 0) {
                if (this.res.status != 0 || this.res.referrals == null) {
                    break Label_0129;
                }
            }
            try {
                this.homeCtx.processReturnCode(this.res, this.listArg);
            }
            catch (final LimitExceededException | PartialResultException namingException) {
                this.setNamingException((NamingException)namingException);
            }
        }
        if (this.res.refEx != null) {
            if (this.refEx == null) {
                this.refEx = this.res.refEx;
            }
            else {
                this.refEx = this.refEx.appendUnprocessedReferrals(this.res.refEx);
            }
            this.res.refEx = null;
        }
        if (this.res.resControls != null) {
            this.homeCtx.respCtls = this.res.resControls;
        }
    }
    
    @Override
    public final boolean hasMore() throws NamingException {
        if (this.hasMoreCalled) {
            return this.more;
        }
        this.hasMoreCalled = true;
        return this.more && (this.more = this.hasMoreImpl());
    }
    
    @Override
    public final T next() throws NamingException {
        if (!this.hasMoreCalled) {
            this.hasMore();
        }
        this.hasMoreCalled = false;
        return this.nextImpl();
    }
    
    private boolean hasMoreImpl() throws NamingException {
        if (this.posn == this.limit) {
            this.getNextBatch();
        }
        if (this.posn < this.limit) {
            return true;
        }
        try {
            return this.hasMoreReferrals();
        }
        catch (final LdapReferralException | LimitExceededException | PartialResultException ex) {
            this.cleanup();
            throw ex;
        }
        catch (final NamingException rootCause) {
            this.cleanup();
            final PartialResultException ex2 = new PartialResultException();
            ex2.setRootCause(rootCause);
            throw ex2;
        }
    }
    
    private T nextImpl() throws NamingException {
        try {
            return this.nextAux();
        }
        catch (final NamingException ex) {
            this.cleanup();
            throw this.cont.fillInException(ex);
        }
    }
    
    private T nextAux() throws NamingException {
        if (this.posn == this.limit) {
            this.getNextBatch();
        }
        if (this.posn >= this.limit) {
            this.cleanup();
            throw new NoSuchElementException("invalid enumeration handle");
        }
        final LdapEntry ldapEntry = this.entries.elementAt(this.posn++);
        return this.createItem(ldapEntry.DN, ldapEntry.attributes, ldapEntry.respCtls);
    }
    
    protected final String getAtom(final String s) {
        try {
            final LdapName ldapName = new LdapName(s);
            return ldapName.get(ldapName.size() - 1);
        }
        catch (final NamingException ex) {
            return s;
        }
    }
    
    protected abstract T createItem(final String p0, final Attributes p1, final Vector<Control> p2) throws NamingException;
    
    @Override
    public void appendUnprocessedReferrals(final LdapReferralException ex) {
        if (this.refEx != null) {
            this.refEx = this.refEx.appendUnprocessedReferrals(ex);
        }
        else {
            this.refEx = ex.appendUnprocessedReferrals(this.refEx);
        }
    }
    
    final void setNamingException(final NamingException errEx) {
        this.errEx = errEx;
    }
    
    protected abstract AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(final LdapReferralContext p0) throws NamingException;
    
    protected final boolean hasMoreReferrals() throws NamingException {
        if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions())) {
            if (this.homeCtx.handleReferrals == 2) {
                throw (NamingException)this.refEx.fillInStackTrace();
            }
            while (true) {
                final LdapReferralContext ldapReferralContext = (LdapReferralContext)this.refEx.getReferralContext(this.homeCtx.envprops, this.homeCtx.reqCtls);
                try {
                    this.update(this.getReferredResults(ldapReferralContext));
                }
                catch (final LdapReferralException refEx) {
                    if (this.errEx == null) {
                        this.errEx = refEx.getNamingException();
                    }
                    this.refEx = refEx;
                    continue;
                }
                finally {
                    ldapReferralContext.close();
                }
                break;
            }
            return this.hasMoreImpl();
        }
        else {
            this.cleanup();
            if (this.errEx != null) {
                throw this.errEx;
            }
            return false;
        }
    }
    
    protected void update(final AbstractLdapNamingEnumeration<? extends NameClassPair> abstractLdapNamingEnumeration) {
        this.homeCtx.decEnumCount();
        this.homeCtx = abstractLdapNamingEnumeration.homeCtx;
        this.enumClnt = abstractLdapNamingEnumeration.enumClnt;
        abstractLdapNamingEnumeration.homeCtx = null;
        this.posn = abstractLdapNamingEnumeration.posn;
        this.limit = abstractLdapNamingEnumeration.limit;
        this.res = abstractLdapNamingEnumeration.res;
        this.entries = abstractLdapNamingEnumeration.entries;
        this.refEx = abstractLdapNamingEnumeration.refEx;
        this.listArg = abstractLdapNamingEnumeration.listArg;
    }
    
    @Override
    protected final void finalize() {
        this.cleanup();
    }
    
    protected final void cleanup() {
        if (this.cleaned) {
            return;
        }
        if (this.enumClnt != null) {
            this.enumClnt.clearSearchReply(this.res, this.homeCtx.reqCtls);
        }
        this.enumClnt = null;
        this.cleaned = true;
        if (this.homeCtx != null) {
            this.homeCtx.decEnumCount();
            this.homeCtx = null;
        }
    }
    
    @Override
    public final void close() {
        this.cleanup();
    }
}
