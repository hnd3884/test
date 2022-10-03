package com.sun.corba.se.impl.interceptors;

import org.omg.CORBA.BAD_PARAM;
import org.omg.PortableInterceptor.PolicyFactory;
import org.omg.PortableInterceptor.IORInterceptor;
import org.omg.CORBA.PolicyError;
import org.omg.PortableInterceptor.ServerRequestInterceptor;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.CORBA.Policy;
import org.omg.PortableInterceptor.ClientRequestInterceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.IOP.CodecFactory;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.legacy.interceptor.ORBInitInfoExt;
import org.omg.PortableInterceptor.ORBInitInfo;
import org.omg.CORBA.LocalObject;

public final class ORBInitInfoImpl extends LocalObject implements ORBInitInfo, ORBInitInfoExt
{
    private ORB orb;
    private InterceptorsSystemException wrapper;
    private ORBUtilSystemException orbutilWrapper;
    private OMGSystemException omgWrapper;
    private String[] args;
    private String orbId;
    private CodecFactory codecFactory;
    private int stage;
    public static final int STAGE_PRE_INIT = 0;
    public static final int STAGE_POST_INIT = 1;
    public static final int STAGE_CLOSED = 2;
    private static final String MESSAGE_ORBINITINFO_INVALID = "ORBInitInfo object is only valid during ORB_init";
    
    ORBInitInfoImpl(final ORB orb, final String[] args, final String orbId, final CodecFactory codecFactory) {
        this.stage = 0;
        this.orb = orb;
        this.wrapper = InterceptorsSystemException.get(orb, "rpc.protocol");
        this.orbutilWrapper = ORBUtilSystemException.get(orb, "rpc.protocol");
        this.omgWrapper = OMGSystemException.get(orb, "rpc.protocol");
        this.args = args;
        this.orbId = orbId;
        this.codecFactory = codecFactory;
    }
    
    @Override
    public ORB getORB() {
        return this.orb;
    }
    
    void setStage(final int stage) {
        this.stage = stage;
    }
    
    private void checkStage() {
        if (this.stage == 2) {
            throw this.wrapper.orbinitinfoInvalid();
        }
    }
    
    @Override
    public String[] arguments() {
        this.checkStage();
        return this.args;
    }
    
    @Override
    public String orb_id() {
        this.checkStage();
        return this.orbId;
    }
    
    @Override
    public CodecFactory codec_factory() {
        this.checkStage();
        return this.codecFactory;
    }
    
    @Override
    public void register_initial_reference(final String s, final Object object) throws InvalidName {
        this.checkStage();
        if (s == null) {
            this.nullParam();
        }
        if (object == null) {
            throw this.omgWrapper.rirWithNullObject();
        }
        try {
            this.orb.register_initial_reference(s, object);
        }
        catch (final org.omg.CORBA.ORBPackage.InvalidName invalidName) {
            final InvalidName invalidName2 = new InvalidName(invalidName.getMessage());
            invalidName2.initCause(invalidName);
            throw invalidName2;
        }
    }
    
    @Override
    public Object resolve_initial_references(final String s) throws InvalidName {
        this.checkStage();
        if (s == null) {
            this.nullParam();
        }
        if (this.stage == 0) {
            throw this.wrapper.rirInvalidPreInit();
        }
        Object resolve_initial_references;
        try {
            resolve_initial_references = this.orb.resolve_initial_references(s);
        }
        catch (final org.omg.CORBA.ORBPackage.InvalidName invalidName) {
            throw new InvalidName();
        }
        return resolve_initial_references;
    }
    
    public void add_client_request_interceptor_with_policy(final ClientRequestInterceptor clientRequestInterceptor, final Policy[] array) throws DuplicateName {
        this.add_client_request_interceptor(clientRequestInterceptor);
    }
    
    @Override
    public void add_client_request_interceptor(final ClientRequestInterceptor clientRequestInterceptor) throws DuplicateName {
        this.checkStage();
        if (clientRequestInterceptor == null) {
            this.nullParam();
        }
        this.orb.getPIHandler().register_interceptor(clientRequestInterceptor, 0);
    }
    
    public void add_server_request_interceptor_with_policy(final ServerRequestInterceptor serverRequestInterceptor, final Policy[] array) throws DuplicateName, PolicyError {
        this.add_server_request_interceptor(serverRequestInterceptor);
    }
    
    @Override
    public void add_server_request_interceptor(final ServerRequestInterceptor serverRequestInterceptor) throws DuplicateName {
        this.checkStage();
        if (serverRequestInterceptor == null) {
            this.nullParam();
        }
        this.orb.getPIHandler().register_interceptor(serverRequestInterceptor, 1);
    }
    
    public void add_ior_interceptor_with_policy(final IORInterceptor iorInterceptor, final Policy[] array) throws DuplicateName, PolicyError {
        this.add_ior_interceptor(iorInterceptor);
    }
    
    @Override
    public void add_ior_interceptor(final IORInterceptor iorInterceptor) throws DuplicateName {
        this.checkStage();
        if (iorInterceptor == null) {
            this.nullParam();
        }
        this.orb.getPIHandler().register_interceptor(iorInterceptor, 2);
    }
    
    @Override
    public int allocate_slot_id() {
        this.checkStage();
        return ((PICurrent)this.orb.getPIHandler().getPICurrent()).allocateSlotId();
    }
    
    @Override
    public void register_policy_factory(final int n, final PolicyFactory policyFactory) {
        this.checkStage();
        if (policyFactory == null) {
            this.nullParam();
        }
        this.orb.getPIHandler().registerPolicyFactory(n, policyFactory);
    }
    
    private void nullParam() throws BAD_PARAM {
        throw this.orbutilWrapper.nullParam();
    }
}
