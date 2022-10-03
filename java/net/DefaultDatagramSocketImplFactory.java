package java.net;

import sun.security.action.GetPropertyAction;
import java.security.AccessController;
import java.security.PrivilegedAction;

class DefaultDatagramSocketImplFactory
{
    private static final Class<?> prefixImplClass;
    private static float version;
    private static boolean preferIPv4Stack;
    private static final boolean useDualStackImpl;
    private static String exclBindProp;
    private static final boolean exclusiveBind;
    
    static DatagramSocketImpl createDatagramSocketImpl(final boolean b) throws SocketException {
        if (DefaultDatagramSocketImplFactory.prefixImplClass != null) {
            try {
                return (DatagramSocketImpl)DefaultDatagramSocketImplFactory.prefixImplClass.newInstance();
            }
            catch (final Exception ex) {
                throw new SocketException("can't instantiate DatagramSocketImpl");
            }
        }
        if (DefaultDatagramSocketImplFactory.useDualStackImpl && !b) {
            return new DualStackPlainDatagramSocketImpl(DefaultDatagramSocketImplFactory.exclusiveBind);
        }
        return new TwoStacksPlainDatagramSocketImpl(DefaultDatagramSocketImplFactory.exclusiveBind && !b);
    }
    
    static {
        DefaultDatagramSocketImplFactory.preferIPv4Stack = false;
        Class<?> forName = null;
        boolean useDualStackImpl2 = false;
        boolean exclusiveBind2 = true;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                DefaultDatagramSocketImplFactory.version = 0.0f;
                try {
                    DefaultDatagramSocketImplFactory.version = Float.parseFloat(System.getProperties().getProperty("os.version"));
                    DefaultDatagramSocketImplFactory.preferIPv4Stack = Boolean.parseBoolean(System.getProperties().getProperty("java.net.preferIPv4Stack"));
                    DefaultDatagramSocketImplFactory.exclBindProp = System.getProperty("sun.net.useExclusiveBind");
                }
                catch (final NumberFormatException ex) {
                    assert false : ex;
                }
                return null;
            }
        });
        if (DefaultDatagramSocketImplFactory.version >= 6.0 && !DefaultDatagramSocketImplFactory.preferIPv4Stack) {
            useDualStackImpl2 = true;
        }
        if (DefaultDatagramSocketImplFactory.exclBindProp != null) {
            exclusiveBind2 = (DefaultDatagramSocketImplFactory.exclBindProp.length() == 0 || Boolean.parseBoolean(DefaultDatagramSocketImplFactory.exclBindProp));
        }
        else if (DefaultDatagramSocketImplFactory.version < 6.0) {
            exclusiveBind2 = false;
        }
        String s = null;
        try {
            s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("impl.prefix", null));
            if (s != null) {
                forName = Class.forName("java.net." + s + "DatagramSocketImpl");
            }
        }
        catch (final Exception ex) {
            System.err.println("Can't find class: java.net." + s + "DatagramSocketImpl: check impl.prefix property");
        }
        prefixImplClass = forName;
        useDualStackImpl = useDualStackImpl2;
        exclusiveBind = exclusiveBind2;
    }
}
