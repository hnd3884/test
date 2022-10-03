package sun.security.jgss.wrapper;

import java.security.Provider;
import sun.security.jgss.spi.GSSContextSpi;
import java.io.UnsupportedEncodingException;
import sun.security.jgss.GSSExceptionImpl;
import org.ietf.jgss.Oid;
import java.util.Vector;
import org.ietf.jgss.GSSException;
import sun.security.jgss.spi.GSSCredentialSpi;
import sun.security.jgss.spi.GSSNameSpi;
import sun.security.jgss.GSSUtil;
import sun.security.jgss.GSSCaller;
import sun.security.jgss.spi.MechanismFactory;

public final class NativeGSSFactory implements MechanismFactory
{
    GSSLibStub cStub;
    private final GSSCaller caller;
    
    private GSSCredElement getCredFromSubject(final GSSNameElement gssNameElement, final boolean b) throws GSSException {
        final Vector<GSSCredentialSpi> searchSubject = GSSUtil.searchSubject((GSSNameSpi)gssNameElement, this.cStub.getMech(), b, (Class<? extends GSSCredentialSpi>)GSSCredElement.class);
        if (searchSubject != null && searchSubject.isEmpty() && GSSUtil.useSubjectCredsOnly(this.caller)) {
            throw new GSSException(13);
        }
        final GSSCredElement gssCredElement = (searchSubject == null || searchSubject.isEmpty()) ? null : searchSubject.firstElement();
        if (gssCredElement != null) {
            gssCredElement.doServicePermCheck();
        }
        return gssCredElement;
    }
    
    public NativeGSSFactory(final GSSCaller caller) {
        this.cStub = null;
        this.caller = caller;
    }
    
    public void setMech(final Oid oid) throws GSSException {
        this.cStub = GSSLibStub.getInstance(oid);
    }
    
    @Override
    public GSSNameSpi getNameElement(final String s, final Oid oid) throws GSSException {
        try {
            return new GSSNameElement((byte[])((s == null) ? null : s.getBytes("UTF-8")), oid, this.cStub);
        }
        catch (final UnsupportedEncodingException ex) {
            throw new GSSExceptionImpl(11, ex);
        }
    }
    
    @Override
    public GSSNameSpi getNameElement(final byte[] array, final Oid oid) throws GSSException {
        return new GSSNameElement(array, oid, this.cStub);
    }
    
    @Override
    public GSSCredentialSpi getCredentialElement(final GSSNameSpi gssNameSpi, final int n, final int n2, int n3) throws GSSException {
        GSSNameElement def_ACCEPTOR;
        if (gssNameSpi != null && !(gssNameSpi instanceof GSSNameElement)) {
            def_ACCEPTOR = (GSSNameElement)this.getNameElement(gssNameSpi.toString(), gssNameSpi.getStringNameType());
        }
        else {
            def_ACCEPTOR = (GSSNameElement)gssNameSpi;
        }
        if (n3 == 0) {
            n3 = 1;
        }
        GSSCredElement credFromSubject = this.getCredFromSubject(def_ACCEPTOR, n3 == 1);
        if (credFromSubject == null) {
            if (n3 == 1) {
                credFromSubject = new GSSCredElement(def_ACCEPTOR, n, n3, this.cStub);
            }
            else {
                if (n3 != 2) {
                    throw new GSSException(11, -1, "Unknown usage mode requested");
                }
                if (def_ACCEPTOR == null) {
                    def_ACCEPTOR = GSSNameElement.DEF_ACCEPTOR;
                }
                credFromSubject = new GSSCredElement(def_ACCEPTOR, n2, n3, this.cStub);
            }
        }
        return credFromSubject;
    }
    
    @Override
    public GSSContextSpi getMechanismContext(GSSNameSpi gssNameSpi, GSSCredentialSpi credFromSubject, final int n) throws GSSException {
        if (gssNameSpi == null) {
            throw new GSSException(3);
        }
        if (!(gssNameSpi instanceof GSSNameElement)) {
            gssNameSpi = this.getNameElement(gssNameSpi.toString(), gssNameSpi.getStringNameType());
        }
        if (credFromSubject == null) {
            credFromSubject = this.getCredFromSubject(null, true);
        }
        else if (!(credFromSubject instanceof GSSCredElement)) {
            throw new GSSException(13);
        }
        return new NativeGSSContext((GSSNameElement)gssNameSpi, (GSSCredElement)credFromSubject, n, this.cStub);
    }
    
    @Override
    public GSSContextSpi getMechanismContext(GSSCredentialSpi credFromSubject) throws GSSException {
        if (credFromSubject == null) {
            credFromSubject = this.getCredFromSubject(null, false);
        }
        else if (!(credFromSubject instanceof GSSCredElement)) {
            throw new GSSException(13);
        }
        return new NativeGSSContext((GSSCredElement)credFromSubject, this.cStub);
    }
    
    @Override
    public GSSContextSpi getMechanismContext(final byte[] array) throws GSSException {
        return this.cStub.importContext(array);
    }
    
    @Override
    public final Oid getMechanismOid() {
        return this.cStub.getMech();
    }
    
    @Override
    public Provider getProvider() {
        return SunNativeProvider.INSTANCE;
    }
    
    @Override
    public Oid[] getNameTypes() throws GSSException {
        return this.cStub.inquireNamesForMech();
    }
}
