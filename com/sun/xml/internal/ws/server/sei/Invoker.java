package com.sun.xml.internal.ws.server.sei;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;

public abstract class Invoker
{
    public abstract Object invoke(@NotNull final Packet p0, @NotNull final Method p1, @NotNull final Object... p2) throws InvocationTargetException, IllegalAccessException;
}
