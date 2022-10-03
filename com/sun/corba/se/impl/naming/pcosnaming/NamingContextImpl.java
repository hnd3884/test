package com.sun.corba.se.impl.naming.pcosnaming;

import java.util.Enumeration;
import com.sun.corba.se.impl.naming.namingutil.INSURLHandler;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.PortableServer.POAManager;
import org.omg.PortableServer.ServantRetentionPolicyValue;
import org.omg.PortableServer.IdAssignmentPolicyValue;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.CORBA.Policy;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.PortableServer.Servant;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.CompletionStatus;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.CosNaming.BindingTypeHolder;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.BindingType;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.PortableServer.POA;
import com.sun.corba.se.impl.logging.NamingSystemException;
import com.sun.corba.se.impl.naming.cosnaming.InterOperableNamingImpl;
import java.util.Hashtable;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import com.sun.corba.se.impl.naming.cosnaming.NamingContextDataStore;
import org.omg.CosNaming.NamingContextExtPOA;

public class NamingContextImpl extends NamingContextExtPOA implements NamingContextDataStore, Serializable
{
    private transient ORB orb;
    private final String objKey;
    private final Hashtable theHashtable;
    private transient NameService theNameServiceHandle;
    private transient ServantManagerImpl theServantManagerImplHandle;
    private transient InterOperableNamingImpl insImpl;
    private transient NamingSystemException readWrapper;
    private transient NamingSystemException updateWrapper;
    private static POA biPOA;
    private static boolean debug;
    
    public NamingContextImpl(final ORB orb, final String objKey, final NameService theNameServiceHandle, final ServantManagerImpl theServantManagerImplHandle) throws Exception {
        this.theHashtable = new Hashtable();
        this.orb = orb;
        this.readWrapper = NamingSystemException.get(orb, "naming.read");
        this.updateWrapper = NamingSystemException.get(orb, "naming.update");
        NamingContextImpl.debug = true;
        this.objKey = objKey;
        this.theNameServiceHandle = theNameServiceHandle;
        this.theServantManagerImplHandle = theServantManagerImplHandle;
        this.insImpl = new InterOperableNamingImpl();
    }
    
    InterOperableNamingImpl getINSImpl() {
        if (this.insImpl == null) {
            this.insImpl = new InterOperableNamingImpl();
        }
        return this.insImpl;
    }
    
    public void setRootNameService(final NameService theNameServiceHandle) {
        this.theNameServiceHandle = theNameServiceHandle;
    }
    
    public void setORB(final ORB orb) {
        this.orb = orb;
    }
    
    public void setServantManagerImpl(final ServantManagerImpl theServantManagerImplHandle) {
        this.theServantManagerImplHandle = theServantManagerImplHandle;
    }
    
    @Override
    public POA getNSPOA() {
        return this.theNameServiceHandle.getNSPOA();
    }
    
    @Override
    public void bind(final NameComponent[] array, final org.omg.CORBA.Object object) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        if (object == null) {
            throw this.updateWrapper.objectIsNull();
        }
        if (NamingContextImpl.debug) {
            dprint("bind " + nameToString(array) + " to " + object);
        }
        this.doBind(this, array, object, false, BindingType.nobject);
    }
    
    @Override
    public void bind_context(final NameComponent[] array, final NamingContext namingContext) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
        if (namingContext == null) {
            throw this.updateWrapper.objectIsNull();
        }
        this.doBind(this, array, namingContext, false, BindingType.ncontext);
    }
    
    @Override
    public void rebind(final NameComponent[] array, final org.omg.CORBA.Object object) throws NotFound, CannotProceed, InvalidName {
        if (object == null) {
            throw this.updateWrapper.objectIsNull();
        }
        try {
            if (NamingContextImpl.debug) {
                dprint("rebind " + nameToString(array) + " to " + object);
            }
            this.doBind(this, array, object, true, BindingType.nobject);
        }
        catch (final AlreadyBound alreadyBound) {
            throw this.updateWrapper.namingCtxRebindAlreadyBound(alreadyBound);
        }
    }
    
    @Override
    public void rebind_context(final NameComponent[] array, final NamingContext namingContext) throws NotFound, CannotProceed, InvalidName {
        try {
            if (NamingContextImpl.debug) {
                dprint("rebind_context " + nameToString(array) + " to " + namingContext);
            }
            this.doBind(this, array, namingContext, true, BindingType.ncontext);
        }
        catch (final AlreadyBound alreadyBound) {
            throw this.updateWrapper.namingCtxRebindAlreadyBound(alreadyBound);
        }
    }
    
    @Override
    public org.omg.CORBA.Object resolve(final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        if (NamingContextImpl.debug) {
            dprint("resolve " + nameToString(array));
        }
        return doResolve(this, array);
    }
    
    @Override
    public void unbind(final NameComponent[] array) throws NotFound, CannotProceed, InvalidName {
        if (NamingContextImpl.debug) {
            dprint("unbind " + nameToString(array));
        }
        doUnbind(this, array);
    }
    
    @Override
    public void list(final int n, final BindingListHolder bindingListHolder, final BindingIteratorHolder bindingIteratorHolder) {
        if (NamingContextImpl.debug) {
            dprint("list(" + n + ")");
        }
        synchronized (this) {
            this.List(n, bindingListHolder, bindingIteratorHolder);
        }
        if (NamingContextImpl.debug && bindingListHolder.value != null) {
            dprint("list(" + n + ") -> bindings[" + bindingListHolder.value.length + "] + iterator: " + bindingIteratorHolder.value);
        }
    }
    
    @Override
    public synchronized NamingContext new_context() {
        if (NamingContextImpl.debug) {
            dprint("new_context()");
        }
        synchronized (this) {
            return this.NewContext();
        }
    }
    
    @Override
    public NamingContext bind_new_context(final NameComponent[] array) throws NotFound, AlreadyBound, CannotProceed, InvalidName {
        NamingContext new_context = null;
        NamingContext namingContext = null;
        try {
            if (NamingContextImpl.debug) {
                dprint("bind_new_context " + nameToString(array));
            }
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
        return namingContext;
    }
    
    @Override
    public void destroy() throws NotEmpty {
        if (NamingContextImpl.debug) {
            dprint("destroy ");
        }
        synchronized (this) {
            if (!this.IsEmpty()) {
                throw new NotEmpty();
            }
            this.Destroy();
        }
    }
    
    private void doBind(final NamingContextDataStore namingContextDataStore, final NameComponent[] array, final org.omg.CORBA.Object object, final boolean b, final BindingType bindingType) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
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
                    throw this.updateWrapper.namingCtxBadBindingtype();
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
            return resolveFirstAsContext.resolve(array2);
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
    
    @Override
    public void Bind(final NameComponent nameComponent, final org.omg.CORBA.Object object, final BindingType bindingType) {
        if (object == null) {
            return;
        }
        final InternalBindingKey internalBindingKey = new InternalBindingKey(nameComponent);
        try {
            InternalBindingValue internalBindingValue;
            if (bindingType.value() == 0) {
                internalBindingValue = new InternalBindingValue(bindingType, this.orb.object_to_string(object));
                internalBindingValue.setObjectRef(object);
            }
            else {
                internalBindingValue = new InternalBindingValue(bindingType, this.theNameServiceHandle.getObjectKey(object));
                internalBindingValue.setObjectRef(object);
            }
            if (this.theHashtable.put(internalBindingKey, internalBindingValue) != null) {
                throw this.updateWrapper.namingCtxRebindAlreadyBound();
            }
            try {
                this.theServantManagerImplHandle.updateContext(this.objKey, this);
            }
            catch (final Exception ex) {
                throw this.updateWrapper.bindUpdateContextFailed(ex);
            }
        }
        catch (final Exception ex2) {
            throw this.updateWrapper.bindFailure(ex2);
        }
    }
    
    @Override
    public org.omg.CORBA.Object Resolve(final NameComponent nameComponent, final BindingTypeHolder bindingTypeHolder) throws SystemException {
        if (nameComponent.id.length() == 0 && nameComponent.kind.length() == 0) {
            bindingTypeHolder.value = BindingType.ncontext;
            return this.theNameServiceHandle.getObjectReferenceFromKey(this.objKey);
        }
        final InternalBindingValue internalBindingValue = this.theHashtable.get(new InternalBindingKey(nameComponent));
        if (internalBindingValue == null) {
            return null;
        }
        bindingTypeHolder.value = internalBindingValue.theBindingType;
        org.omg.CORBA.Object objectRef;
        try {
            if (internalBindingValue.strObjectRef.startsWith("NC")) {
                bindingTypeHolder.value = BindingType.ncontext;
                return this.theNameServiceHandle.getObjectReferenceFromKey(internalBindingValue.strObjectRef);
            }
            objectRef = internalBindingValue.getObjectRef();
            if (objectRef == null) {
                try {
                    objectRef = this.orb.string_to_object(internalBindingValue.strObjectRef);
                    internalBindingValue.setObjectRef(objectRef);
                }
                catch (final Exception ex) {
                    throw this.readWrapper.resolveConversionFailure(CompletionStatus.COMPLETED_MAYBE, ex);
                }
            }
        }
        catch (final Exception ex2) {
            throw this.readWrapper.resolveFailure(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
        return objectRef;
    }
    
    @Override
    public org.omg.CORBA.Object Unbind(final NameComponent nameComponent) throws SystemException {
        try {
            final InternalBindingKey internalBindingKey = new InternalBindingKey(nameComponent);
            InternalBindingValue internalBindingValue = null;
            try {
                internalBindingValue = this.theHashtable.remove(internalBindingKey);
            }
            catch (final Exception ex) {}
            this.theServantManagerImplHandle.updateContext(this.objKey, this);
            if (internalBindingValue == null) {
                return null;
            }
            if (internalBindingValue.strObjectRef.startsWith("NC")) {
                this.theServantManagerImplHandle.readInContext(internalBindingValue.strObjectRef);
                return this.theNameServiceHandle.getObjectReferenceFromKey(internalBindingValue.strObjectRef);
            }
            org.omg.CORBA.Object object = internalBindingValue.getObjectRef();
            if (object == null) {
                object = this.orb.string_to_object(internalBindingValue.strObjectRef);
            }
            return object;
        }
        catch (final Exception ex2) {
            throw this.updateWrapper.unbindFailure(CompletionStatus.COMPLETED_MAYBE, ex2);
        }
    }
    
    @Override
    public void List(final int n, final BindingListHolder bindingListHolder, final BindingIteratorHolder bindingIteratorHolder) throws SystemException {
        if (NamingContextImpl.biPOA == null) {
            this.createbiPOA();
        }
        try {
            final PersistentBindingIterator persistentBindingIterator = new PersistentBindingIterator(this.orb, (Hashtable)this.theHashtable.clone(), NamingContextImpl.biPOA);
            persistentBindingIterator.list(n, bindingListHolder);
            bindingIteratorHolder.value = BindingIteratorHelper.narrow(NamingContextImpl.biPOA.id_to_reference(NamingContextImpl.biPOA.activate_object(persistentBindingIterator)));
        }
        catch (final SystemException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw this.readWrapper.transNcListGotExc(ex2);
        }
    }
    
    private synchronized void createbiPOA() {
        if (NamingContextImpl.biPOA != null) {
            return;
        }
        try {
            final POA poa = (POA)this.orb.resolve_initial_references("RootPOA");
            poa.the_POAManager().activate();
            int n = 0;
            final Policy[] array = new Policy[3];
            array[n++] = poa.create_lifespan_policy(LifespanPolicyValue.TRANSIENT);
            array[n++] = poa.create_id_assignment_policy(IdAssignmentPolicyValue.SYSTEM_ID);
            array[n++] = poa.create_servant_retention_policy(ServantRetentionPolicyValue.RETAIN);
            NamingContextImpl.biPOA = poa.create_POA("BindingIteratorPOA", null, array);
            NamingContextImpl.biPOA.the_POAManager().activate();
        }
        catch (final Exception ex) {
            throw this.readWrapper.namingCtxBindingIteratorCreate(ex);
        }
    }
    
    @Override
    public NamingContext NewContext() throws SystemException {
        try {
            return this.theNameServiceHandle.NewContext();
        }
        catch (final SystemException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw this.updateWrapper.transNcNewctxGotExc(ex2);
        }
    }
    
    @Override
    public void Destroy() throws SystemException {
    }
    
    @Override
    public String to_string(final NameComponent[] array) throws InvalidName {
        if (array == null || array.length == 0) {
            throw new InvalidName();
        }
        final String convertToString = this.getINSImpl().convertToString(array);
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
        final NameComponent[] convertToNameComponent = this.getINSImpl().convertToNameComponent(s);
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
        String urlBasedAddress;
        try {
            urlBasedAddress = this.getINSImpl().createURLBasedAddress(s, s2);
        }
        catch (final Exception ex) {
            urlBasedAddress = null;
        }
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
        final NameComponent[] convertToNameComponent = this.getINSImpl().convertToNameComponent(s);
        if (convertToNameComponent == null || convertToNameComponent.length == 0) {
            throw new InvalidName();
        }
        return this.resolve(convertToNameComponent);
    }
    
    @Override
    public boolean IsEmpty() {
        return this.theHashtable.isEmpty();
    }
    
    public void printSize() {
        System.out.println("Hashtable Size = " + this.theHashtable.size());
        final Enumeration keys = this.theHashtable.keys();
        while (keys.hasMoreElements()) {
            final InternalBindingValue internalBindingValue = this.theHashtable.get(keys.nextElement());
            if (internalBindingValue != null) {
                System.out.println("value = " + internalBindingValue.strObjectRef);
            }
        }
    }
    
    static {
        NamingContextImpl.biPOA = null;
    }
}
