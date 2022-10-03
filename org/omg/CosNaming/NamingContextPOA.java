package org.omg.CosNaming;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.InputStream;
import java.util.Hashtable;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.PortableServer.Servant;

public abstract class NamingContextPOA extends Servant implements NamingContextOperations, InvokeHandler
{
    private static Hashtable _methods;
    private static String[] __ids;
    
    @Override
    public OutputStream _invoke(final String s, final InputStream inputStream, final ResponseHandler responseHandler) {
        final Integer n = NamingContextPOA._methods.get(s);
        if (n == null) {
            throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
        }
        OutputStream outputStream = null;
        switch (n) {
            case 0: {
                try {
                    this.bind(NameHelper.read(inputStream), ObjectHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final NotFound notFound) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound);
                }
                catch (final CannotProceed cannotProceed) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed);
                }
                catch (final InvalidName invalidName) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName);
                }
                catch (final AlreadyBound alreadyBound) {
                    outputStream = responseHandler.createExceptionReply();
                    AlreadyBoundHelper.write(outputStream, alreadyBound);
                }
                break;
            }
            case 1: {
                try {
                    this.bind_context(NameHelper.read(inputStream), NamingContextHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final NotFound notFound2) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound2);
                }
                catch (final CannotProceed cannotProceed2) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed2);
                }
                catch (final InvalidName invalidName2) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName2);
                }
                catch (final AlreadyBound alreadyBound2) {
                    outputStream = responseHandler.createExceptionReply();
                    AlreadyBoundHelper.write(outputStream, alreadyBound2);
                }
                break;
            }
            case 2: {
                try {
                    this.rebind(NameHelper.read(inputStream), ObjectHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final NotFound notFound3) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound3);
                }
                catch (final CannotProceed cannotProceed3) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed3);
                }
                catch (final InvalidName invalidName3) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName3);
                }
                break;
            }
            case 3: {
                try {
                    this.rebind_context(NameHelper.read(inputStream), NamingContextHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final NotFound notFound4) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound4);
                }
                catch (final CannotProceed cannotProceed4) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed4);
                }
                catch (final InvalidName invalidName4) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName4);
                }
                break;
            }
            case 4: {
                try {
                    final org.omg.CORBA.Object resolve = this.resolve(NameHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    ObjectHelper.write(outputStream, resolve);
                }
                catch (final NotFound notFound5) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound5);
                }
                catch (final CannotProceed cannotProceed5) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed5);
                }
                catch (final InvalidName invalidName5) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName5);
                }
                break;
            }
            case 5: {
                try {
                    this.unbind(NameHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                }
                catch (final NotFound notFound6) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound6);
                }
                catch (final CannotProceed cannotProceed6) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed6);
                }
                catch (final InvalidName invalidName6) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName6);
                }
                break;
            }
            case 6: {
                final int read_ulong = inputStream.read_ulong();
                final BindingListHolder bindingListHolder = new BindingListHolder();
                final BindingIteratorHolder bindingIteratorHolder = new BindingIteratorHolder();
                this.list(read_ulong, bindingListHolder, bindingIteratorHolder);
                outputStream = responseHandler.createReply();
                BindingListHelper.write(outputStream, bindingListHolder.value);
                BindingIteratorHelper.write(outputStream, bindingIteratorHolder.value);
                break;
            }
            case 7: {
                final NamingContext new_context = this.new_context();
                outputStream = responseHandler.createReply();
                NamingContextHelper.write(outputStream, new_context);
                break;
            }
            case 8: {
                try {
                    final NamingContext bind_new_context = this.bind_new_context(NameHelper.read(inputStream));
                    outputStream = responseHandler.createReply();
                    NamingContextHelper.write(outputStream, bind_new_context);
                }
                catch (final NotFound notFound7) {
                    outputStream = responseHandler.createExceptionReply();
                    NotFoundHelper.write(outputStream, notFound7);
                }
                catch (final AlreadyBound alreadyBound3) {
                    outputStream = responseHandler.createExceptionReply();
                    AlreadyBoundHelper.write(outputStream, alreadyBound3);
                }
                catch (final CannotProceed cannotProceed7) {
                    outputStream = responseHandler.createExceptionReply();
                    CannotProceedHelper.write(outputStream, cannotProceed7);
                }
                catch (final InvalidName invalidName7) {
                    outputStream = responseHandler.createExceptionReply();
                    InvalidNameHelper.write(outputStream, invalidName7);
                }
                break;
            }
            case 9: {
                try {
                    this.destroy();
                    outputStream = responseHandler.createReply();
                }
                catch (final NotEmpty notEmpty) {
                    outputStream = responseHandler.createExceptionReply();
                    NotEmptyHelper.write(outputStream, notEmpty);
                }
                break;
            }
            default: {
                throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
            }
        }
        return outputStream;
    }
    
    @Override
    public String[] _all_interfaces(final POA poa, final byte[] array) {
        return NamingContextPOA.__ids.clone();
    }
    
    public NamingContext _this() {
        return NamingContextHelper.narrow(super._this_object());
    }
    
    public NamingContext _this(final ORB orb) {
        return NamingContextHelper.narrow(super._this_object(orb));
    }
    
    static {
        (NamingContextPOA._methods = new Hashtable()).put("bind", new Integer(0));
        NamingContextPOA._methods.put("bind_context", new Integer(1));
        NamingContextPOA._methods.put("rebind", new Integer(2));
        NamingContextPOA._methods.put("rebind_context", new Integer(3));
        NamingContextPOA._methods.put("resolve", new Integer(4));
        NamingContextPOA._methods.put("unbind", new Integer(5));
        NamingContextPOA._methods.put("list", new Integer(6));
        NamingContextPOA._methods.put("new_context", new Integer(7));
        NamingContextPOA._methods.put("bind_new_context", new Integer(8));
        NamingContextPOA._methods.put("destroy", new Integer(9));
        NamingContextPOA.__ids = new String[] { "IDL:omg.org/CosNaming/NamingContext:1.0" };
    }
}
