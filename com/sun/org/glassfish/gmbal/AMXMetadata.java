package com.sun.org.glassfish.gmbal;

import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface AMXMetadata {
    @DescriptorKey("amx.isSingleton")
    boolean isSingleton() default false;
    
    @DescriptorKey("amx.group")
    String group() default "other";
    
    @DescriptorKey("amx.subTypes")
    String[] subTypes() default {};
    
    @DescriptorKey("amx.genericInterfaceName")
    String genericInterfaceName() default "com.sun.org.glassfish.admin.amx.core.AMXProxy";
    
    @DescriptorKey("immutableInfo")
    boolean immutableInfo() default true;
    
    @DescriptorKey("interfaceName")
    String interfaceClassName() default "";
    
    @DescriptorKey("type")
    String type() default "";
}
