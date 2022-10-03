package java.beans;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.ref.WeakReference;
import java.util.List;

public class MethodDescriptor extends FeatureDescriptor
{
    private final MethodRef methodRef;
    private String[] paramNames;
    private List<WeakReference<Class<?>>> params;
    private ParameterDescriptor[] parameterDescriptors;
    
    public MethodDescriptor(final Method method) {
        this(method, null);
    }
    
    public MethodDescriptor(final Method method, final ParameterDescriptor[] array) {
        this.methodRef = new MethodRef();
        this.setName(method.getName());
        this.setMethod(method);
        this.parameterDescriptors = (ParameterDescriptor[])((array != null) ? ((ParameterDescriptor[])array.clone()) : null);
    }
    
    public synchronized Method getMethod() {
        Method method = this.methodRef.get();
        if (method == null) {
            final Class<?> class0 = this.getClass0();
            final String name = this.getName();
            if (class0 != null && name != null) {
                final Class<?>[] params = this.getParams();
                if (params == null) {
                    for (int i = 0; i < 3; ++i) {
                        method = Introspector.findMethod(class0, name, i, null);
                        if (method != null) {
                            break;
                        }
                    }
                }
                else {
                    method = Introspector.findMethod(class0, name, params.length, params);
                }
                this.setMethod(method);
            }
        }
        return method;
    }
    
    private synchronized void setMethod(final Method method) {
        if (method == null) {
            return;
        }
        if (this.getClass0() == null) {
            this.setClass0(method.getDeclaringClass());
        }
        this.setParams(FeatureDescriptor.getParameterTypes(this.getClass0(), method));
        this.methodRef.set(method);
    }
    
    private synchronized void setParams(final Class<?>[] array) {
        if (array == null) {
            return;
        }
        this.paramNames = new String[array.length];
        this.params = new ArrayList<WeakReference<Class<?>>>(array.length);
        for (int i = 0; i < array.length; ++i) {
            this.paramNames[i] = array[i].getName();
            this.params.add(new WeakReference<Class<?>>(array[i]));
        }
    }
    
    String[] getParamNames() {
        return this.paramNames;
    }
    
    private synchronized Class<?>[] getParams() {
        final Class[] array = new Class[this.params.size()];
        for (int i = 0; i < this.params.size(); ++i) {
            final Class clazz = this.params.get(i).get();
            if (clazz == null) {
                return null;
            }
            array[i] = clazz;
        }
        return array;
    }
    
    public ParameterDescriptor[] getParameterDescriptors() {
        return (ParameterDescriptor[])((this.parameterDescriptors != null) ? ((ParameterDescriptor[])this.parameterDescriptors.clone()) : null);
    }
    
    private static Method resolve(final Method method, final Method method2) {
        if (method == null) {
            return method2;
        }
        if (method2 == null) {
            return method;
        }
        return (!method.isSynthetic() && method2.isSynthetic()) ? method : method2;
    }
    
    MethodDescriptor(final MethodDescriptor methodDescriptor, final MethodDescriptor methodDescriptor2) {
        super(methodDescriptor, methodDescriptor2);
        (this.methodRef = new MethodRef()).set(resolve(methodDescriptor.methodRef.get(), methodDescriptor2.methodRef.get()));
        this.params = methodDescriptor.params;
        if (methodDescriptor2.params != null) {
            this.params = methodDescriptor2.params;
        }
        this.paramNames = methodDescriptor.paramNames;
        if (methodDescriptor2.paramNames != null) {
            this.paramNames = methodDescriptor2.paramNames;
        }
        this.parameterDescriptors = methodDescriptor.parameterDescriptors;
        if (methodDescriptor2.parameterDescriptors != null) {
            this.parameterDescriptors = methodDescriptor2.parameterDescriptors;
        }
    }
    
    MethodDescriptor(final MethodDescriptor methodDescriptor) {
        super(methodDescriptor);
        (this.methodRef = new MethodRef()).set(methodDescriptor.getMethod());
        this.params = methodDescriptor.params;
        this.paramNames = methodDescriptor.paramNames;
        if (methodDescriptor.parameterDescriptors != null) {
            final int length = methodDescriptor.parameterDescriptors.length;
            this.parameterDescriptors = new ParameterDescriptor[length];
            for (int i = 0; i < length; ++i) {
                this.parameterDescriptors[i] = new ParameterDescriptor(methodDescriptor.parameterDescriptors[i]);
            }
        }
    }
    
    @Override
    void appendTo(final StringBuilder sb) {
        FeatureDescriptor.appendTo(sb, "method", this.methodRef.get());
        if (this.parameterDescriptors != null) {
            sb.append("; parameterDescriptors={");
            final ParameterDescriptor[] parameterDescriptors = this.parameterDescriptors;
            for (int length = parameterDescriptors.length, i = 0; i < length; ++i) {
                sb.append(parameterDescriptors[i]).append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append("}");
        }
    }
}
