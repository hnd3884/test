package javax.validation.spi;

import java.util.Map;
import javax.validation.ClockProvider;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.valueextraction.ValueExtractor;
import java.io.InputStream;
import java.util.Set;
import javax.validation.MessageInterpolator;

public interface ConfigurationState
{
    boolean isIgnoreXmlConfiguration();
    
    MessageInterpolator getMessageInterpolator();
    
    Set<InputStream> getMappingStreams();
    
    Set<ValueExtractor<?>> getValueExtractors();
    
    ConstraintValidatorFactory getConstraintValidatorFactory();
    
    TraversableResolver getTraversableResolver();
    
    ParameterNameProvider getParameterNameProvider();
    
    ClockProvider getClockProvider();
    
    Map<String, String> getProperties();
}
