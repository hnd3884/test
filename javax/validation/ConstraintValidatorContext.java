package javax.validation;

public interface ConstraintValidatorContext
{
    void disableDefaultConstraintViolation();
    
    String getDefaultConstraintMessageTemplate();
    
    ClockProvider getClockProvider();
    
    ConstraintViolationBuilder buildConstraintViolationWithTemplate(final String p0);
    
     <T> T unwrap(final Class<T> p0);
    
    public interface ConstraintViolationBuilder
    {
        @Deprecated
        NodeBuilderDefinedContext addNode(final String p0);
        
        NodeBuilderCustomizableContext addPropertyNode(final String p0);
        
        LeafNodeBuilderCustomizableContext addBeanNode();
        
        ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
        
        NodeBuilderDefinedContext addParameterNode(final int p0);
        
        ConstraintValidatorContext addConstraintViolation();
        
        public interface ContainerElementNodeContextBuilder
        {
            ContainerElementNodeBuilderDefinedContext atKey(final Object p0);
            
            ContainerElementNodeBuilderDefinedContext atIndex(final Integer p0);
            
            NodeBuilderCustomizableContext addPropertyNode(final String p0);
            
            LeafNodeBuilderCustomizableContext addBeanNode();
            
            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface ContainerElementNodeBuilderDefinedContext
        {
            NodeBuilderCustomizableContext addPropertyNode(final String p0);
            
            LeafNodeBuilderCustomizableContext addBeanNode();
            
            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface NodeBuilderCustomizableContext
        {
            NodeContextBuilder inIterable();
            
            NodeBuilderCustomizableContext inContainer(final Class<?> p0, final Integer p1);
            
            @Deprecated
            NodeBuilderCustomizableContext addNode(final String p0);
            
            NodeBuilderCustomizableContext addPropertyNode(final String p0);
            
            LeafNodeBuilderCustomizableContext addBeanNode();
            
            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface NodeContextBuilder
        {
            NodeBuilderDefinedContext atKey(final Object p0);
            
            NodeBuilderDefinedContext atIndex(final Integer p0);
            
            @Deprecated
            NodeBuilderCustomizableContext addNode(final String p0);
            
            NodeBuilderCustomizableContext addPropertyNode(final String p0);
            
            LeafNodeBuilderCustomizableContext addBeanNode();
            
            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface NodeBuilderDefinedContext
        {
            @Deprecated
            NodeBuilderCustomizableContext addNode(final String p0);
            
            NodeBuilderCustomizableContext addPropertyNode(final String p0);
            
            LeafNodeBuilderCustomizableContext addBeanNode();
            
            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface LeafNodeBuilderCustomizableContext
        {
            LeafNodeContextBuilder inIterable();
            
            LeafNodeBuilderCustomizableContext inContainer(final Class<?> p0, final Integer p1);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface LeafNodeContextBuilder
        {
            LeafNodeBuilderDefinedContext atKey(final Object p0);
            
            LeafNodeBuilderDefinedContext atIndex(final Integer p0);
            
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface LeafNodeBuilderDefinedContext
        {
            ConstraintValidatorContext addConstraintViolation();
        }
        
        public interface ContainerElementNodeBuilderCustomizableContext
        {
            ContainerElementNodeContextBuilder inIterable();
            
            NodeBuilderCustomizableContext addPropertyNode(final String p0);
            
            LeafNodeBuilderCustomizableContext addBeanNode();
            
            ContainerElementNodeBuilderCustomizableContext addContainerElementNode(final String p0, final Class<?> p1, final Integer p2);
            
            ConstraintValidatorContext addConstraintViolation();
        }
    }
}
