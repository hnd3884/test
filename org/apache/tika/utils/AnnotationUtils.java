package org.apache.tika.utils;

import java.util.HashMap;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Locale;
import java.lang.reflect.InvocationTargetException;
import org.apache.tika.exception.TikaConfigException;
import java.util.HashSet;
import org.apache.tika.config.Field;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.config.Param;
import java.util.Iterator;
import java.security.AccessController;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.reflect.AccessibleObject;
import java.lang.annotation.Annotation;
import org.apache.tika.config.ParamField;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class AnnotationUtils
{
    private static final Logger LOG;
    private static final Map<Class<?>, List<ParamField>> PARAM_INFO;
    
    private static List<AccessibleObject> collectInfo(final Class<?> clazz, final Class<? extends Annotation> annotation) {
        Class superClazz = clazz;
        final List<AccessibleObject> members = new ArrayList<AccessibleObject>();
        final List<AccessibleObject> annotatedMembers = new ArrayList<AccessibleObject>();
        while (superClazz != null && superClazz != Object.class) {
            members.addAll(Arrays.asList(superClazz.getDeclaredFields()));
            members.addAll(Arrays.asList(superClazz.getDeclaredMethods()));
            superClazz = superClazz.getSuperclass();
        }
        for (final AccessibleObject member : members) {
            if (member.isAnnotationPresent(annotation)) {
                AccessController.doPrivileged(() -> {
                    member.setAccessible(true);
                    return null;
                });
                annotatedMembers.add(member);
            }
        }
        return annotatedMembers;
    }
    
    public static void assignFieldParams(final Object bean, final Map<String, Param> params) throws TikaConfigException {
        final Class<?> beanClass = bean.getClass();
        if (!AnnotationUtils.PARAM_INFO.containsKey(beanClass)) {
            synchronized (TikaConfig.class) {
                if (!AnnotationUtils.PARAM_INFO.containsKey(beanClass)) {
                    final List<AccessibleObject> aObjs = collectInfo(beanClass, Field.class);
                    final List<ParamField> fields = new ArrayList<ParamField>(aObjs.size());
                    for (final AccessibleObject aObj : aObjs) {
                        fields.add(new ParamField(aObj));
                    }
                    AnnotationUtils.PARAM_INFO.put(beanClass, fields);
                }
            }
        }
        final List<ParamField> fields2 = AnnotationUtils.PARAM_INFO.get(beanClass);
        final Set<String> validFieldNames = new HashSet<String>();
        for (final ParamField field : fields2) {
            validFieldNames.add(field.getName());
            final Param<?> param = params.get(field.getName());
            if (param != null) {
                if (field.getType().isAssignableFrom(param.getType())) {
                    try {
                        field.assignValue(bean, param.getValue());
                        continue;
                    }
                    catch (final InvocationTargetException e) {
                        AnnotationUtils.LOG.error("Error assigning value '{}' to '{}'", (Object)param.getValue(), (Object)param.getName());
                        final Throwable cause = (e.getCause() == null) ? e : e.getCause();
                        throw new TikaConfigException(cause.getMessage(), cause);
                    }
                    catch (final IllegalAccessException e2) {
                        AnnotationUtils.LOG.error("Error assigning value '{}' to '{}'", (Object)param.getValue(), (Object)param.getName());
                        throw new TikaConfigException(e2.getMessage(), e2);
                    }
                }
                final String msg = String.format(Locale.ROOT, "Value '%s' of type '%s' can't be assigned to field '%s' of defined type '%s'", param.getValue(), param.getValue().getClass(), field.getName(), field.getType());
                throw new TikaConfigException(msg);
            }
            if (field.isRequired()) {
                final String msg = String.format(Locale.ROOT, "Param %s is required for %s, but it is not given in config.", field.getName(), bean.getClass().getName());
                throw new TikaConfigException(msg);
            }
            AnnotationUtils.LOG.debug("Param not supplied, field is not mandatory");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)AnnotationUtils.class);
        PARAM_INFO = new HashMap<Class<?>, List<ParamField>>();
    }
}
