package javax.validation;

import java.util.List;

public interface Path extends Iterable<Node>
{
    String toString();
    
    public interface ContainerElementNode extends Node
    {
        Class<?> getContainerClass();
        
        Integer getTypeArgumentIndex();
    }
    
    public interface Node
    {
        String getName();
        
        boolean isInIterable();
        
        Integer getIndex();
        
        Object getKey();
        
        ElementKind getKind();
        
         <T extends Node> T as(final Class<T> p0);
        
        String toString();
    }
    
    public interface PropertyNode extends Node
    {
        Class<?> getContainerClass();
        
        Integer getTypeArgumentIndex();
    }
    
    public interface BeanNode extends Node
    {
        Class<?> getContainerClass();
        
        Integer getTypeArgumentIndex();
    }
    
    public interface CrossParameterNode extends Node
    {
    }
    
    public interface ParameterNode extends Node
    {
        int getParameterIndex();
    }
    
    public interface ReturnValueNode extends Node
    {
    }
    
    public interface ConstructorNode extends Node
    {
        List<Class<?>> getParameterTypes();
    }
    
    public interface MethodNode extends Node
    {
        List<Class<?>> getParameterTypes();
    }
}
