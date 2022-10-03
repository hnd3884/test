package javax.smartcardio;

import java.util.Collections;
import java.util.List;
import java.security.Security;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import sun.security.jca.GetInstance;
import java.security.Provider;

public final class TerminalFactory
{
    private static final String PROP_NAME = "javax.smartcardio.TerminalFactory.DefaultType";
    private static final String defaultType;
    private static final TerminalFactory defaultFactory;
    private final TerminalFactorySpi spi;
    private final Provider provider;
    private final String type;
    
    private TerminalFactory(final TerminalFactorySpi spi, final Provider provider, final String type) {
        this.spi = spi;
        this.provider = provider;
        this.type = type;
    }
    
    public static String getDefaultType() {
        return TerminalFactory.defaultType;
    }
    
    public static TerminalFactory getDefault() {
        return TerminalFactory.defaultFactory;
    }
    
    public static TerminalFactory getInstance(final String s, final Object o) throws NoSuchAlgorithmException {
        final GetInstance.Instance instance = GetInstance.getInstance("TerminalFactory", TerminalFactorySpi.class, s, o);
        return new TerminalFactory((TerminalFactorySpi)instance.impl, instance.provider, s);
    }
    
    public static TerminalFactory getInstance(final String s, final Object o, final String s2) throws NoSuchAlgorithmException, NoSuchProviderException {
        final GetInstance.Instance instance = GetInstance.getInstance("TerminalFactory", TerminalFactorySpi.class, s, o, s2);
        return new TerminalFactory((TerminalFactorySpi)instance.impl, instance.provider, s);
    }
    
    public static TerminalFactory getInstance(final String s, final Object o, final Provider provider) throws NoSuchAlgorithmException {
        final GetInstance.Instance instance = GetInstance.getInstance("TerminalFactory", TerminalFactorySpi.class, s, o, provider);
        return new TerminalFactory((TerminalFactorySpi)instance.impl, instance.provider, s);
    }
    
    public Provider getProvider() {
        return this.provider;
    }
    
    public String getType() {
        return this.type;
    }
    
    public CardTerminals terminals() {
        return this.spi.engineTerminals();
    }
    
    @Override
    public String toString() {
        return "TerminalFactory for type " + this.type + " from provider " + this.provider.getName();
    }
    
    static {
        String trim = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("javax.smartcardio.TerminalFactory.DefaultType", "PC/SC")).trim();
        TerminalFactory defaultFactory2 = null;
        try {
            defaultFactory2 = getInstance(trim, null);
        }
        catch (final Exception ex) {}
        if (defaultFactory2 == null) {
            try {
                trim = "PC/SC";
                Provider provider = Security.getProvider("SunPCSC");
                if (provider == null) {
                    provider = (Provider)Class.forName("sun.security.smartcardio.SunPCSC").newInstance();
                }
                defaultFactory2 = getInstance(trim, null, provider);
            }
            catch (final Exception ex2) {}
        }
        if (defaultFactory2 == null) {
            trim = "None";
            defaultFactory2 = new TerminalFactory(NoneFactorySpi.INSTANCE, NoneProvider.INSTANCE, "None");
        }
        defaultType = trim;
        defaultFactory = defaultFactory2;
    }
    
    private static final class NoneProvider extends Provider
    {
        private static final long serialVersionUID = 2745808869881593918L;
        static final Provider INSTANCE;
        
        private NoneProvider() {
            super("None", 1.0, "none");
        }
        
        static {
            INSTANCE = new NoneProvider();
        }
    }
    
    private static final class NoneFactorySpi extends TerminalFactorySpi
    {
        static final TerminalFactorySpi INSTANCE;
        
        @Override
        protected CardTerminals engineTerminals() {
            return NoneCardTerminals.INSTANCE;
        }
        
        static {
            INSTANCE = new NoneFactorySpi();
        }
    }
    
    private static final class NoneCardTerminals extends CardTerminals
    {
        static final CardTerminals INSTANCE;
        
        @Override
        public List<CardTerminal> list(final State state) throws CardException {
            if (state == null) {
                throw new NullPointerException();
            }
            return Collections.emptyList();
        }
        
        @Override
        public boolean waitForChange(final long n) throws CardException {
            throw new IllegalStateException("no terminals");
        }
        
        static {
            INSTANCE = new NoneCardTerminals();
        }
    }
}
