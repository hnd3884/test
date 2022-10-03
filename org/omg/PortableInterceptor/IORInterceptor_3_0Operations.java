package org.omg.PortableInterceptor;

public interface IORInterceptor_3_0Operations extends IORInterceptorOperations
{
    void components_established(final IORInfo p0);
    
    void adapter_manager_state_changed(final int p0, final short p1);
    
    void adapter_state_changed(final ObjectReferenceTemplate[] p0, final short p1);
}
