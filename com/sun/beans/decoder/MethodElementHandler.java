package com.sun.beans.decoder;

import java.lang.reflect.Method;
import sun.reflect.misc.MethodUtil;
import com.sun.beans.finder.MethodFinder;

final class MethodElementHandler extends NewElementHandler
{
    private String name;
    
    @Override
    public void addAttribute(final String s, final String name) {
        if (s.equals("name")) {
            this.name = name;
        }
        else {
            super.addAttribute(s, name);
        }
    }
    
    protected ValueObject getValueObject(final Class<?> clazz, Object[] arguments) throws Exception {
        final Object contextBean = this.getContextBean();
        final Class<?>[] argumentTypes = NewElementHandler.getArgumentTypes(arguments);
        final Method method = (clazz != null) ? MethodFinder.findStaticMethod(clazz, this.name, argumentTypes) : MethodFinder.findMethod(contextBean.getClass(), this.name, argumentTypes);
        if (method.isVarArgs()) {
            arguments = NewElementHandler.getArguments(arguments, method.getParameterTypes());
        }
        final Object invoke = MethodUtil.invoke(method, contextBean, arguments);
        return method.getReturnType().equals(Void.TYPE) ? ValueObjectImpl.VOID : ValueObjectImpl.create(invoke);
    }
}
