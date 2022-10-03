package org.apache.axiom.core;

public interface DetachPolicy
{
    public static final DetachPolicy NEW_DOCUMENT = new DetachPolicy() {
        public CoreDocument getNewOwnerDocument(final CoreParentNode owner) {
            return null;
        }
    };
    public static final DetachPolicy SAME_DOCUMENT = new DetachPolicy() {
        public CoreDocument getNewOwnerDocument(final CoreParentNode owner) {
            return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$coreGetOwnerDocument(owner, true);
        }
    };
    
    CoreDocument getNewOwnerDocument(final CoreParentNode p0);
}
