package com.maverick.events;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Hashtable;

public final class J2SSHEventMessages
{
    public static Hashtable messageCodes;
    public static Hashtable messageAttributes;
    
    static {
        J2SSHEventMessages.messageCodes = new Hashtable();
        J2SSHEventMessages.messageAttributes = new Hashtable();
        final Field[] fields = J2SSHEventCodes.class.getFields();
        for (int i = 0; i < fields.length; ++i) {
            final int modifiers = fields[i].getModifiers();
            if (Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)) {
                try {
                    final String name = fields[i].getName();
                    if (name.startsWith("EVENT_")) {
                        J2SSHEventMessages.messageCodes.put(fields[i].get(null), name.substring(6));
                    }
                    else {
                        J2SSHEventMessages.messageAttributes.put(fields[i].get(null), name.substring(10));
                    }
                }
                catch (final IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
                catch (final IllegalAccessException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }
}
