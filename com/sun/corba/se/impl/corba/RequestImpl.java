package com.sun.corba.se.impl.corba;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.Bounds;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import org.omg.CORBA.WrongTransaction;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.Context;
import org.omg.CORBA.Environment;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Object;
import org.omg.CORBA.Request;

public class RequestImpl extends Request
{
    protected org.omg.CORBA.Object _target;
    protected String _opName;
    protected NVList _arguments;
    protected ExceptionList _exceptions;
    private NamedValue _result;
    protected Environment _env;
    private Context _ctx;
    private ContextList _ctxList;
    protected ORB _orb;
    private ORBUtilSystemException _wrapper;
    protected boolean _isOneWay;
    private int[] _paramCodes;
    private long[] _paramLongs;
    private Object[] _paramObjects;
    protected boolean gotResponse;
    
    public RequestImpl(final ORB orb, final org.omg.CORBA.Object target, final Context ctx, final String opName, final NVList arguments, final NamedValue result, final ExceptionList exceptions, final ContextList ctxList) {
        this._isOneWay = false;
        this.gotResponse = false;
        this._orb = orb;
        this._wrapper = ORBUtilSystemException.get(orb, "oa.invocation");
        this._target = target;
        this._ctx = ctx;
        this._opName = opName;
        if (arguments == null) {
            this._arguments = new NVListImpl(this._orb);
        }
        else {
            this._arguments = arguments;
        }
        this._result = result;
        if (exceptions == null) {
            this._exceptions = new ExceptionListImpl();
        }
        else {
            this._exceptions = exceptions;
        }
        if (ctxList == null) {
            this._ctxList = new ContextListImpl(this._orb);
        }
        else {
            this._ctxList = ctxList;
        }
        this._env = new EnvironmentImpl();
    }
    
    @Override
    public org.omg.CORBA.Object target() {
        return this._target;
    }
    
    @Override
    public String operation() {
        return this._opName;
    }
    
    @Override
    public NVList arguments() {
        return this._arguments;
    }
    
    @Override
    public NamedValue result() {
        return this._result;
    }
    
    @Override
    public Environment env() {
        return this._env;
    }
    
    @Override
    public ExceptionList exceptions() {
        return this._exceptions;
    }
    
    @Override
    public ContextList contexts() {
        return this._ctxList;
    }
    
    @Override
    public synchronized Context ctx() {
        if (this._ctx == null) {
            this._ctx = new ContextImpl(this._orb);
        }
        return this._ctx;
    }
    
    @Override
    public synchronized void ctx(final Context ctx) {
        this._ctx = ctx;
    }
    
    @Override
    public synchronized Any add_in_arg() {
        return this._arguments.add(1).value();
    }
    
    @Override
    public synchronized Any add_named_in_arg(final String s) {
        return this._arguments.add_item(s, 1).value();
    }
    
    @Override
    public synchronized Any add_inout_arg() {
        return this._arguments.add(3).value();
    }
    
    @Override
    public synchronized Any add_named_inout_arg(final String s) {
        return this._arguments.add_item(s, 3).value();
    }
    
    @Override
    public synchronized Any add_out_arg() {
        return this._arguments.add(2).value();
    }
    
    @Override
    public synchronized Any add_named_out_arg(final String s) {
        return this._arguments.add_item(s, 2).value();
    }
    
    @Override
    public synchronized void set_return_type(final TypeCode typeCode) {
        if (this._result == null) {
            this._result = new NamedValueImpl(this._orb);
        }
        this._result.value().type(typeCode);
    }
    
    @Override
    public synchronized Any return_value() {
        if (this._result == null) {
            this._result = new NamedValueImpl(this._orb);
        }
        return this._result.value();
    }
    
    public synchronized void add_exception(final TypeCode typeCode) {
        this._exceptions.add(typeCode);
    }
    
    @Override
    public synchronized void invoke() {
        this.doInvocation();
    }
    
    @Override
    public synchronized void send_oneway() {
        this._isOneWay = true;
        this.doInvocation();
    }
    
    @Override
    public synchronized void send_deferred() {
        new Thread(new AsynchInvoke(this._orb, this, false)).start();
    }
    
    @Override
    public synchronized boolean poll_response() {
        return this.gotResponse;
    }
    
    @Override
    public synchronized void get_response() throws WrongTransaction {
        while (!this.gotResponse) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    protected void doInvocation() {
        final Delegate delegate = StubAdapter.getDelegate(this._target);
        this._orb.getPIHandler().initiateClientPIRequest(true);
        this._orb.getPIHandler().setClientPIInfo(this);
        InputStream invoke = null;
        try {
            final OutputStream request = delegate.request(null, this._opName, !this._isOneWay);
            try {
                for (int i = 0; i < this._arguments.count(); ++i) {
                    final NamedValue item = this._arguments.item(i);
                    switch (item.flags()) {
                        case 1: {
                            item.value().write_value(request);
                        }
                        case 3: {
                            item.value().write_value(request);
                            break;
                        }
                    }
                }
            }
            catch (final Bounds bounds) {
                throw this._wrapper.boundsErrorInDiiRequest(bounds);
            }
            invoke = delegate.invoke(null, request);
        }
        catch (final ApplicationException ex) {}
        catch (final RemarshalException ex2) {
            this.doInvocation();
        }
        catch (final SystemException ex3) {
            this._env.exception(ex3);
            throw ex3;
        }
        finally {
            delegate.releaseReply(null, invoke);
        }
    }
    
    public void unmarshalReply(final InputStream inputStream) {
        if (this._result != null) {
            final Any value = this._result.value();
            final TypeCode type = value.type();
            if (type.kind().value() != 1) {
                value.read_value(inputStream, type);
            }
        }
        try {
            for (int i = 0; i < this._arguments.count(); ++i) {
                final NamedValue item = this._arguments.item(i);
                switch (item.flags()) {
                    case 2:
                    case 3: {
                        final Any value2 = item.value();
                        value2.read_value(inputStream, value2.type());
                        break;
                    }
                }
            }
        }
        catch (final Bounds bounds) {}
    }
}
