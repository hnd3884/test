package com.adventnet.client.components.tpl;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import com.adventnet.client.view.Getter;
import java.util.Arrays;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.client.tpl.TemplateAPI;

public class CSRStateHandler implements TemplateAPI.VariableHandler
{
    public String getVariableValue(final String variableName, final int variablePosition, final Object handlerContext) {
        final ViewContext viewCtx = (ViewContext)handlerContext;
        final String[] variableVal = { null };
        Arrays.stream(viewCtx.getViewState().getClass().getDeclaredMethods()).filter(method -> method.getName().equalsIgnoreCase("get" + variableName) || Optional.ofNullable((Object)method.getAnnotation((Class<T>)Getter.class)).map(annotation -> annotation.paramName().equals(variableName)).orElse(Boolean.FALSE)).findFirst().ifPresent(method -> {
            method.setAccessible(true);
            try {
                variableVal[0] = String.valueOf(method.invoke(viewCtx.getViewState(), new Object[0]));
            }
            catch (final IllegalAccessException | InvocationTargetException e1) {
                throw new RuntimeException(e1);
            }
            return;
        });
        return variableVal[0];
    }
}
