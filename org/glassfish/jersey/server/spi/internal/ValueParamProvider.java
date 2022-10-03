package org.glassfish.jersey.server.spi.internal;

import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface ValueParamProvider
{
    Function<ContainerRequest, ?> getValueProvider(final Parameter p0);
    
    PriorityType getPriority();
    
    public enum Priority implements PriorityType
    {
        LOW(100), 
        NORMAL(200), 
        HIGH(300);
        
        private final int weight;
        
        private Priority(final int weight) {
            this.weight = weight;
        }
        
        @Override
        public int getWeight() {
            return this.weight;
        }
    }
    
    public interface PriorityType
    {
        int getWeight();
    }
}
