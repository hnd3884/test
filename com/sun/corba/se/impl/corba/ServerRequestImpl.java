package com.sun.corba.se.impl.corba;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Bounds;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import org.omg.CORBA.Any;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.Context;
import org.omg.CORBA.NVList;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.ServerRequest;

public class ServerRequestImpl extends ServerRequest
{
    private ORB _orb;
    private ORBUtilSystemException _wrapper;
    private String _opName;
    private NVList _arguments;
    private Context _ctx;
    private InputStream _ins;
    private boolean _paramsCalled;
    private boolean _resultSet;
    private boolean _exceptionSet;
    private Any _resultAny;
    private Any _exception;
    
    public ServerRequestImpl(final CorbaMessageMediator corbaMessageMediator, final ORB orb) {
        this._orb = null;
        this._wrapper = null;
        this._opName = null;
        this._arguments = null;
        this._ctx = null;
        this._ins = null;
        this._paramsCalled = false;
        this._resultSet = false;
        this._exceptionSet = false;
        this._resultAny = null;
        this._exception = null;
        this._opName = corbaMessageMediator.getOperationName();
        this._ins = (InputStream)corbaMessageMediator.getInputObject();
        this._ctx = null;
        this._orb = orb;
        this._wrapper = ORBUtilSystemException.get(orb, "oa.invocation");
    }
    
    @Override
    public String operation() {
        return this._opName;
    }
    
    @Override
    public void arguments(final NVList arguments) {
        if (this._paramsCalled) {
            throw this._wrapper.argumentsCalledMultiple();
        }
        if (this._exceptionSet) {
            throw this._wrapper.argumentsCalledAfterException();
        }
        if (arguments == null) {
            throw this._wrapper.argumentsCalledNullArgs();
        }
        this._paramsCalled = true;
        for (int i = 0; i < arguments.count(); ++i) {
            NamedValue item;
            try {
                item = arguments.item(i);
            }
            catch (final Bounds bounds) {
                throw this._wrapper.boundsCannotOccur(bounds);
            }
            try {
                if (item.flags() == 1 || item.flags() == 3) {
                    item.value().read_value(this._ins, item.value().type());
                }
            }
            catch (final Exception ex) {
                throw this._wrapper.badArgumentsNvlist(ex);
            }
        }
        this._arguments = arguments;
        this._orb.getPIHandler().setServerPIInfo(this._arguments);
        this._orb.getPIHandler().invokeServerPIIntermediatePoint();
    }
    
    @Override
    public void set_result(final Any resultAny) {
        if (!this._paramsCalled) {
            throw this._wrapper.argumentsNotCalled();
        }
        if (this._resultSet) {
            throw this._wrapper.setResultCalledMultiple();
        }
        if (this._exceptionSet) {
            throw this._wrapper.setResultAfterException();
        }
        if (resultAny == null) {
            throw this._wrapper.setResultCalledNullArgs();
        }
        this._resultAny = resultAny;
        this._resultSet = true;
        this._orb.getPIHandler().setServerPIInfo(this._resultAny);
    }
    
    @Override
    public void set_exception(final Any exception) {
        if (exception == null) {
            throw this._wrapper.setExceptionCalledNullArgs();
        }
        if (exception.type().kind() != TCKind.tk_except) {
            throw this._wrapper.setExceptionCalledBadType();
        }
        this._exception = exception;
        this._orb.getPIHandler().setServerPIExceptionInfo(this._exception);
        if (!this._exceptionSet && !this._paramsCalled) {
            this._orb.getPIHandler().invokeServerPIIntermediatePoint();
        }
        this._exceptionSet = true;
    }
    
    public Any checkResultCalled() {
        if (this._paramsCalled && this._resultSet) {
            return null;
        }
        if (this._paramsCalled && !this._resultSet && !this._exceptionSet) {
            try {
                (this._resultAny = this._orb.create_any()).type(this._orb.get_primitive_tc(TCKind.tk_void));
                this._resultSet = true;
                return null;
            }
            catch (final Exception ex) {
                throw this._wrapper.dsiResultException(CompletionStatus.COMPLETED_MAYBE, ex);
            }
        }
        if (this._exceptionSet) {
            return this._exception;
        }
        throw this._wrapper.dsimethodNotcalled(CompletionStatus.COMPLETED_MAYBE);
    }
    
    public void marshalReplyParams(final OutputStream outputStream) {
        this._resultAny.write_value(outputStream);
        NamedValue item = null;
        for (int i = 0; i < this._arguments.count(); ++i) {
            try {
                item = this._arguments.item(i);
            }
            catch (final Bounds bounds) {}
            if (item.flags() == 2 || item.flags() == 3) {
                item.value().write_value(outputStream);
            }
        }
    }
    
    @Override
    public Context ctx() {
        if (!this._paramsCalled || this._resultSet || this._exceptionSet) {
            throw this._wrapper.contextCalledOutOfOrder();
        }
        throw this._wrapper.contextNotImplemented();
    }
}
