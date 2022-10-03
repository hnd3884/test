package com.btr.proxy.selector.pac;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

abstract class ScriptAvailability
{
    public static boolean isJavaxScriptingAvailable() {
        Object engine = null;
        try {
            final Class<?> managerClass = Class.forName("javax.script.ScriptEngineManager");
            final Method m = managerClass.getMethod("getEngineByMimeType", String.class);
            engine = m.invoke(managerClass.newInstance(), "text/javascript");
        }
        catch (final ClassNotFoundException e) {}
        catch (final NoSuchMethodException e2) {}
        catch (final IllegalAccessException e3) {}
        catch (final InvocationTargetException e4) {}
        catch (final InstantiationException ex) {}
        return engine != null;
    }
}
