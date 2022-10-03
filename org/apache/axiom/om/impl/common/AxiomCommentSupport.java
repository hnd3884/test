package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.om.impl.intf.AxiomComment;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomCommentSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomCommentSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomCommentSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$getType(final AxiomComment ajc$this_) {
        return 5;
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$getValue(final AxiomComment ajc$this_) {
        return ajc$this_.coreGetCharacterData().toString();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$setValue(final AxiomComment ajc$this_, final String text) {
        ajc$this_.coreSetCharacterData(text, AxiomSemantics.INSTANCE);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$internalSerialize(final AxiomComment ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        serializer.writeComment(ajc$this_.coreGetCharacterData().toString());
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCommentSupport$org_apache_axiom_om_impl_intf_AxiomComment$buildWithAttachments(final AxiomComment ajc$this_) {
    }
    
    public static AxiomCommentSupport aspectOf() {
        if (AxiomCommentSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomCommentSupport", AxiomCommentSupport.ajc$initFailureCause);
        }
        return AxiomCommentSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomCommentSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomCommentSupport();
    }
}
