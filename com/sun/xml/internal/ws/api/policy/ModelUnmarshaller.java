package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import com.sun.xml.internal.ws.policy.sourcemodel.XmlPolicyModelUnmarshaller;

public class ModelUnmarshaller extends XmlPolicyModelUnmarshaller
{
    private static final ModelUnmarshaller INSTANCE;
    
    private ModelUnmarshaller() {
    }
    
    public static ModelUnmarshaller getUnmarshaller() {
        return ModelUnmarshaller.INSTANCE;
    }
    
    @Override
    protected PolicySourceModel createSourceModel(final NamespaceVersion nsVersion, final String id, final String name) {
        return SourceModel.createSourceModel(nsVersion, id, name);
    }
    
    static {
        INSTANCE = new ModelUnmarshaller();
    }
}
