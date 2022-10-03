package sun.security.smartcardio;

import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;

public final class SunPCSC extends Provider
{
    private static final long serialVersionUID = 6168388284028876579L;
    
    public SunPCSC() {
        super("SunPCSC", 1.8, "Sun PC/SC provider");
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                SunPCSC.this.put("TerminalFactory.PC/SC", "sun.security.smartcardio.SunPCSC$Factory");
                return null;
            }
        });
    }
    
    public static final class Factory extends TerminalFactorySpi
    {
        public Factory(final Object o) throws PCSCException {
            if (o != null) {
                throw new IllegalArgumentException("SunPCSC factory does not use parameters");
            }
            PCSC.checkAvailable();
            PCSCTerminals.initContext();
        }
        
        @Override
        protected CardTerminals engineTerminals() {
            return new PCSCTerminals();
        }
    }
}
