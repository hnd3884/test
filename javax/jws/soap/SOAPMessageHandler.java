package javax.jws.soap;

@Deprecated
public @interface SOAPMessageHandler {
    String name() default "";
    
    String className();
    
    InitParam[] initParams() default {};
    
    String[] roles() default {};
    
    String[] headers() default {};
}
