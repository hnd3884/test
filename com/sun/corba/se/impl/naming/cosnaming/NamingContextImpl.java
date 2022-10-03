package com.sun.corba.se.impl.naming.cosnaming;

import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import java.util.logging.Level;
import org.omg.CosNaming.BindingType;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.impl.logging.NamingSystemException;
import java.util.logging.Logger;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.NamingContextExtPOA;

public abstract class NamingContextImpl extends NamingContextExtPOA implements NamingContextDataStore
{
    protected POA nsPOA;
    private Logger readLogger;
    private Logger updateLogger;
    private Logger lifecycleLogger;
    private NamingSystemException wrapper;
    private static NamingSystemException staticWrapper;
    private InterOperableNamingImpl insImpl;
    protected transient ORB orb;
    public static final boolean debug = false;
    
    public NamingContextImpl(final ORB orb, final POA nsPOA) throws Exception {
        this.orb = orb;
        this.wrapper = NamingSystemException.get(orb, "naming.update");
        this.insImpl = new InterOperableNamingImpl();
        this.nsPOA = nsPOA;
        this.readLogger = orb.getLogger("naming.read");
        this.updateLogger = orb.getLogger("naming.update");
        this.lifecycleLogger = orb.getLogger("naming.lifecycle");
    }
    
    @Override
    public POA getNSPOA() {
        return this.nsPOA;
    }
    
    @Override
    public void bind(final NameComponent[] array, final org.omg.CORBA.Object object) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        if (object == null) {
            this.updateLogger.warning("<<NAMING BIND>> unsuccessful because NULL Object cannot be Bound ");
            throw this.wrapper.objectIsNull();
        }
        doBind(this, array, object, false, BindingType.nobject);
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(array));
        }
    }
    
    @Override
    public void bind_context(final NameComponent[] array, final NamingContext namingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        if (namingContext == null) {
            this.updateLogger.warning("<<NAMING BIND>><<FAILURE>> NULL Context cannot be Bound ");
            throw new BAD_PARAM("Naming Context should not be null ");
        }
        doBind(this, array, namingContext, false, BindingType.ncontext);
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(array));
        }
    }
    
    @Override
    public void rebind(final NameComponent[] array, final org.omg.CORBA.Object object) throws NotFound, CannotProceed, InvalidName {
        if (object == null) {
            this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Object cannot be Bound ");
            throw this.wrapper.objectIsNull();
        }
        try {
            doBind(this, array, object, true, BindingType.nobject);
        }
        catch (final AlreadyBound alreadyBound) {
            this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(array) + " is already bound to a Naming Context");
            throw this.wrapper.namingCtxRebindAlreadyBound(alreadyBound);
        }
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(array));
        }
    }
    
    @Override
    public void rebind_context(final NameComponent[] array, final NamingContext namingContext) throws NotFound, CannotProceed, InvalidName {
        if (namingContext == null) {
            this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>> NULL Context cannot be Bound ");
            throw this.wrapper.objectIsNull();
        }
        try {
            doBind(this, array, namingContext, true, BindingType.ncontext);
        }
        catch (final AlreadyBound alreadyBound) {
            this.updateLogger.warning("<<NAMING REBIND>><<FAILURE>>" + NamingUtils.getDirectoryStructuredName(array) + " is already bound to a CORBA Object");
            throw this.wrapper.namingCtxRebindctxAlreadyBound(alreadyBound);
        }
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING REBIND>><<SUCCESS>> Name = " + NamingUtils.getDirectoryStructuredName(array));
        }
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        final org.omg.CORBA.Object doResolve = doResolve(this, array);
        if (doResolve != null) {
            if (this.readLogger.isLoggable(Level.FINE)) {
                this.readLogger.fine("<<NAMING RESOLVE>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(array));
            }
        }
        else {
            this.readLogger.warning("<<NAMING RESOLVE>><<FAILURE>> Name: " + NamingUtils.getDirectoryStructuredName(array));
        }
        return doResolve;
    }
    
    @Override
    public void unbind(final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        doUnbind(this, array);
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING UNBIND>><<SUCCESS>> Name: " + NamingUtils.getDirectoryStructuredName(array));
        }
    }
    
    @Override
    public void list(final int n, final BindingListHolder bindingListHolder, final BindingIteratorHolder bindingIteratorHolder) {
        synchronized (this) {
            this.List(n, bindingListHolder, bindingIteratorHolder);
        }
        if (this.readLogger.isLoggable(Level.FINE) && bindingListHolder.value != null) {
            this.readLogger.fine("<<NAMING LIST>><<SUCCESS>>list(" + n + ") -> bindings[" + bindingListHolder.value.length + "] + iterator: " + bindingIteratorHolder.value);
        }
    }
    
    @Override
    public synchronized NamingContext new_context() {
        this.lifecycleLogger.fine("Creating New Naming Context ");
        synchronized (this) {
            final NamingContext newContext = this.NewContext();
            if (newContext != null) {
                this.lifecycleLogger.fine("<<LIFECYCLE CREATE>><<SUCCESS>>");
            }
            else {
                this.lifecycleLogger.severe("<<LIFECYCLE CREATE>><<FAILURE>>");
            }
            return newContext;
        }
    }
    
    @Override
    public NamingContext bind_new_context(final NameComponent[] array) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
        NamingContext new_context = null;
        NamingContext namingContext = null;
        try {
            new_context = this.new_context();
            this.bind_context(array, new_context);
            namingContext = new_context;
            new_context = null;
        }
        finally {
            try {
                if (new_context != null) {
                    new_context.destroy();
                }
            }
            catch (final NotEmpty notEmpty) {}
        }
        if (this.updateLogger.isLoggable(Level.FINE)) {
            this.updateLogger.fine("<<NAMING BIND>>New Context Bound To " + NamingUtils.getDirectoryStructuredName(array));
        }
        return namingContext;
    }
    
    @Override
    public void destroy() throws NotEmpty {
        this.lifecycleLogger.fine("Destroying Naming Context ");
        synchronized (this) {
            if (!this.IsEmpty()) {
                this.lifecycleLogger.warning("<<LIFECYCLE DESTROY>><<FAILURE>> NamingContext children are not destroyed still..");
                throw new NotEmpty();
            }
            this.Destroy();
            this.lifecycleLogger.fine("<<LIFECYCLE DESTROY>><<SUCCESS>>");
        }
    }
    
    public static void doBind(final NamingContextDataStore namingContextDataStore, final NameComponent[] array, final org.omg.CORBA.Object object, final boolean b, final BindingType bindingType) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        if (array.length < 1) {
            throw new InvalidName();
        }
        if (array.length == 1) {
            if (array[0].id.length() == 0 && array[0].kind.length() == 0) {
                throw new InvalidName();
            }
            synchronized (namingContextDataStore) {
                final BindingTypeHolder bindingTypeHolder = new BindingTypeHolder();
                if (b) {
                    if (namingContextDataStore.Resolve(array[0], bindingTypeHolder) != null) {
                        if (bindingTypeHolder.value.value() == BindingType.nobject.value()) {
                            if (bindingType.value() == BindingType.ncontext.value()) {
                                throw new NotFound(NotFoundReason.not_context, array);
                            }
                        }
                        else if (bindingType.value() == BindingType.nobject.value()) {
                            throw new NotFound(NotFoundReason.not_object, array);
                        }
                        namingContextDataStore.Unbind(array[0]);
                    }
                }
                else if (namingContextDataStore.Resolve(array[0], bindingTypeHolder) != null) {
                    throw new AlreadyBound();
                }
                namingContextDataStore.Bind(array[0], object, bindingType);
            }
        }
        else {
            final NamingContext resolveFirstAsContext = resolveFirstAsContext(namingContextDataStore, array);
            final NameComponent[] array2 = new NameComponent[array.length - 1];
            System.arraycopy(array, 1, array2, 0, array.length - 1);
            switch (bindingType.value()) {
                case 0: {
                    if (b) {
                        resolveFirstAsContext.rebind(array2, object);
                        break;
                    }
                    resolveFirstAsContext.bind(array2, object);
                    break;
                }
                case 1: {
                    final NamingContext namingContext = (NamingContext)object;
                    if (b) {
                        resolveFirstAsContext.rebind_context(array2, namingContext);
                    }
                    else {
                        resolveFirstAsContext.bind_context(array2, namingContext);
                    }
                    break;
                }
                default: {
                    throw NamingContextImpl.staticWrapper.namingCtxBadBindingtype();
                }
            }
        }
    }
    
    public static org.omg.CORBA.Object doResolve(final NamingContextDataStore namingContextDataStore, final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        org.omg.CORBA.Object resolve = null;
        final BindingTypeHolder bindingTypeHolder = new BindingTypeHolder();
        if (array.length < 1) {
            throw new InvalidName();
        }
        if (array.length == 1) {
            synchronized (namingContextDataStore) {
                resolve = namingContextDataStore.Resolve(array[0], bindingTypeHolder);
            }
            if (resolve == null) {
                throw new NotFound(NotFoundReason.missing_node, array);
            }
            return resolve;
        }
        else {
            if (array[1].id.length() == 0 && array[1].kind.length() == 0) {
                throw new InvalidName();
            }
            final NamingContext resolveFirstAsContext = resolveFirstAsContext(namingContextDataStore, array);
            final NameComponent[] array2 = new NameComponent[array.length - 1];
            System.arraycopy(array, 1, array2, 0, array.length - 1);
            try {
                return doResolve((NamingContextDataStore)namingContextDataStore.getNSPOA().reference_to_servant(resolveFirstAsContext), array2);
            }
            catch (final Exception ex) {
                return resolveFirstAsContext.resolve(array2);
            }
        }
    }
    
    public static void doUnbind(final NamingContextDataStore namingContextDataStore, final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        if (array.length < 1) {
            throw new InvalidName();
        }
        if (array.length != 1) {
            final NamingContext resolveFirstAsContext = resolveFirstAsContext(namingContextDataStore, array);
            final NameComponent[] array2 = new NameComponent[array.length - 1];
            System.arraycopy(array, 1, array2, 0, array.length - 1);
            resolveFirstAsContext.unbind(array2);
            return;
        }
        if (array[0].id.length() == 0 && array[0].kind.length() == 0) {
            throw new InvalidName();
        }
        org.omg.CORBA.Object unbind = null;
        synchronized (namingContextDataStore) {
            unbind = namingContextDataStore.Unbind(array[0]);
        }
        if (unbind == null) {
            throw new NotFound(NotFoundReason.missing_node, array);
        }
    }
    
    protected static NamingContext resolveFirstAsContext(final NamingContextDataStore namingContextDataStore, final NameComponent[] array) throws NotFound {
        org.omg.CORBA.Object resolve = null;
        final BindingTypeHolder bindingTypeHolder = new BindingTypeHolder();
        synchronized (namingContextDataStore) {
            resolve = namingContextDataStore.Resolve(array[0], bindingTypeHolder);
            if (resolve == null) {
                throw new NotFound(NotFoundReason.missing_node, array);
            }
        }
        if (bindingTypeHolder.value != BindingType.ncontext) {
            throw new NotFound(NotFoundReason.not_context, array);
        }
        NamingContext narrow;
        try {
            narrow = NamingContextHelper.narrow(resolve);
        }
        catch (final BAD_PARAM bad_PARAM) {
            throw new NotFound(NotFoundReason.not_context, array);
        }
        return narrow;
    }
    
    @Override
    public String to_string(final NameComponent[] array) throws InvalidName {
        if (array == null || array.length == 0) {
            throw new InvalidName();
        }
        final String convertToString = this.insImpl.convertToString(array);
        if (convertToString == null) {
            throw new InvalidName();
        }
        return convertToString;
    }
    
    @Override
    public NameComponent[] to_name(final String s) throws InvalidName {
        if (s == null || s.length() == 0) {
            throw new InvalidName();
        }
        final NameComponent[] convertToNameComponent = this.insImpl.convertToNameComponent(s);
        if (convertToNameComponent == null || convertToNameComponent.length == 0) {
            throw new InvalidName();
        }
        for (int i = 0; i < convertToNameComponent.length; ++i) {
            if ((convertToNameComponent[i].id == null || convertToNameComponent[i].id.length() == 0) && (convertToNameComponent[i].kind == null || convertToNameComponent[i].kind.length() == 0)) {
                throw new InvalidName();
            }
        }
        return convertToNameComponent;
    }
    
    @Override
    public String to_url(final String s, final String s2) throws InvalidAddress, InvalidName {
        if (s2 == null || s2.length() == 0) {
            throw new InvalidName();
        }
        if (s == null) {
            throw new InvalidAddress();
        }
        final String urlBasedAddress = this.insImpl.createURLBasedAddress(s, s2);
        try {
            INSURLHandler.getINSURLHandler().parseURL(urlBasedAddress);
        }
        catch (final BAD_PARAM bad_PARAM) {
            throw new InvalidAddress();
        }
        return urlBasedAddress;
    }
    
    @Override
    public org.omg.CORBA.Object resolve_str(final String s) throws NotFound, CannotProceed, InvalidName {
        if (s == null || s.length() == 0) {
            throw new InvalidName();
        }
        final NameComponent[] convertToNameComponent = this.insImpl.convertToNameComponent(s);
        if (convertToNameComponent == null || convertToNameComponent.length == 0) {
            throw new InvalidName();
        }
        return this.resolve(convertToNameComponent);
    }
    
    public static String nameToString(final NameComponent[] array) {
        final StringBuffer sb = new StringBuffer("{");
        if (array != null || array.length > 0) {
            for (int i = 0; i < array.length; ++i) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("[").append(array[i].id).append(",").append(array[i].kind).append("]");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    private static void dprint(final String s) {
        NamingUtils.dprint("NamingContextImpl(" + Thread.currentThread().getName() + " at " + System.currentTimeMillis() + " ems): " + s);
    }
    
    static {
        NamingContextImpl.staticWrapper = NamingSystemException.get("naming.update");
    }
}
