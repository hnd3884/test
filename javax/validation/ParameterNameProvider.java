package javax.validation;

import java.lang.reflect.Method;
import java.util.List;
import java.lang.reflect.Constructor;

public interface ParameterNameProvider
{
    List<String> getParameterNames(final Constructor<?> p0);
    
    List<String> getParameterNames(final Method p0);
}
