package javax.management.remote.rmi;

import org.omg.CORBA.ORB;
import java.rmi.Remote;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.BAD_OPERATION;
import java.io.Serializable;
import javax.rmi.CORBA.Util;
import java.io.IOException;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import javax.rmi.CORBA.Tie;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class _RMIServerImpl_Tie extends ObjectImpl implements Tie
{
    private volatile RMIServerImpl target;
    private static final String[] _type_ids;
    static /* synthetic */ Class class$java$io$IOException;
    static /* synthetic */ Class class$java$lang$String;
    
    static {
        _type_ids = new String[] { "RMI:javax.management.remote.rmi.RMIServer:0000000000000000" };
    }
    
    public _RMIServerImpl_Tie() {
        this.target = null;
    }
    
    public String[] _ids() {
        return _RMIServerImpl_Tie._type_ids.clone();
    }
    
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) throws SystemException {
        try {
            final RMIServerImpl target = this.target;
            if (target == null) {
                throw new IOException();
            }
            final org.omg.CORBA_2_3.portable.InputStream inputStream2 = (org.omg.CORBA_2_3.portable.InputStream)inputStream;
            switch (s.length()) {
                case 9: {
                    if (s.equals("newClient")) {
                        final Object any = Util.readAny(inputStream2);
                        RMIConnection client;
                        try {
                            client = target.newClient(any);
                        }
                        catch (final IOException ex) {
                            final String s2 = "IDL:java/io/IOEx:1.0";
                            final org.omg.CORBA_2_3.portable.OutputStream outputStream = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createExceptionReply();
                            outputStream.write_string(s2);
                            outputStream.write_value(ex, (_RMIServerImpl_Tie.class$java$io$IOException != null) ? _RMIServerImpl_Tie.class$java$io$IOException : (_RMIServerImpl_Tie.class$java$io$IOException = class$("java.io.IOException")));
                            return outputStream;
                        }
                        final OutputStream reply = responseHandler.createReply();
                        Util.writeRemoteObject(reply, client);
                        return reply;
                    }
                }
                case 12: {
                    if (s.equals("_get_version")) {
                        final String version = target.getVersion();
                        final org.omg.CORBA_2_3.portable.OutputStream outputStream2 = (org.omg.CORBA_2_3.portable.OutputStream)responseHandler.createReply();
                        outputStream2.write_value(version, (_RMIServerImpl_Tie.class$java$lang$String != null) ? _RMIServerImpl_Tie.class$java$lang$String : (_RMIServerImpl_Tie.class$java$lang$String = class$("java.lang.String")));
                        return outputStream2;
                    }
                    break;
                }
            }
            throw new BAD_OPERATION();
        }
        catch (final SystemException ex2) {
            throw ex2;
        }
        catch (final Throwable t) {
            throw new UnknownException(t);
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public void deactivate() {
        this._orb().disconnect(this);
        this._set_delegate(null);
        this.target = null;
    }
    
    public Remote getTarget() {
        return this.target;
    }
    
    public ORB orb() {
        return this._orb();
    }
    
    public void orb(final ORB orb) {
        orb.connect(this);
    }
    
    public void setTarget(final Remote remote) {
        this.target = (RMIServerImpl)remote;
    }
    
    public org.omg.CORBA.Object thisObject() {
        return this;
    }
}
