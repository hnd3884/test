package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.config.management.policy.ManagementPrefixMapper;
import com.sun.xml.internal.ws.encoding.policy.EncodingPrefixMapper;
import com.sun.xml.internal.ws.addressing.policy.AddressingPrefixMapper;
import java.util.Collection;
import java.util.Arrays;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;

public class SourceModel extends PolicySourceModel
{
    private static final PrefixMapper[] JAXWS_PREFIX_MAPPERS;
    
    private SourceModel(final NamespaceVersion nsVersion) {
        this(nsVersion, null, null);
    }
    
    private SourceModel(final NamespaceVersion nsVersion, final String policyId, final String policyName) {
        super(nsVersion, policyId, policyName, Arrays.asList(SourceModel.JAXWS_PREFIX_MAPPERS));
    }
    
    public static PolicySourceModel createSourceModel(final NamespaceVersion nsVersion) {
        return new SourceModel(nsVersion);
    }
    
    public static PolicySourceModel createSourceModel(final NamespaceVersion nsVersion, final String policyId, final String policyName) {
        return new SourceModel(nsVersion, policyId, policyName);
    }
    
    static {
        JAXWS_PREFIX_MAPPERS = new PrefixMapper[] { new AddressingPrefixMapper(), new EncodingPrefixMapper(), new ManagementPrefixMapper() };
    }
}
