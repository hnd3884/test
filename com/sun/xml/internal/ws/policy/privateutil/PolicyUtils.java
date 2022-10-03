package com.sun.xml.internal.ws.policy.privateutil;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Queue;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.namespace.QName;
import java.util.Comparator;
import java.util.Arrays;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Closeable;

public final class PolicyUtils
{
    private PolicyUtils() {
    }
    
    public static class Commons
    {
        public static String getStackMethodName(final int methodIndexInStack) {
            final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            String methodName;
            if (stack.length > methodIndexInStack + 1) {
                methodName = stack[methodIndexInStack].getMethodName();
            }
            else {
                methodName = "UNKNOWN METHOD";
            }
            return methodName;
        }
        
        public static String getCallerMethodName() {
            String result = getStackMethodName(5);
            if (result.equals("invoke0")) {
                result = getStackMethodName(4);
            }
            return result;
        }
    }
    
    public static class IO
    {
        private static final PolicyLogger LOGGER;
        
        public static void closeResource(final Closeable resource) {
            if (resource != null) {
                try {
                    resource.close();
                }
                catch (final IOException e) {
                    IO.LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(resource.toString()), e);
                }
            }
        }
        
        public static void closeResource(final XMLStreamReader reader) {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final XMLStreamException e) {
                    IO.LOGGER.warning(LocalizationMessages.WSP_0023_UNEXPECTED_ERROR_WHILE_CLOSING_RESOURCE(reader.toString()), e);
                }
            }
        }
        
        static {
            LOGGER = PolicyLogger.getLogger(IO.class);
        }
    }
    
    public static class Text
    {
        public static final String NEW_LINE;
        
        public static String createIndent(final int indentLevel) {
            final char[] charData = new char[indentLevel * 4];
            Arrays.fill(charData, ' ');
            return String.valueOf(charData);
        }
        
        static {
            NEW_LINE = System.getProperty("line.separator");
        }
    }
    
    public static class Comparison
    {
        public static final Comparator<QName> QNAME_COMPARATOR;
        
        public static int compareBoolean(final boolean b1, final boolean b2) {
            final int i1 = b1 ? 1 : 0;
            final int i2 = b2 ? 1 : 0;
            return i1 - i2;
        }
        
        public static int compareNullableStrings(final String s1, final String s2) {
            return (s1 == null) ? ((s2 == null) ? 0 : -1) : ((s2 == null) ? 1 : s1.compareTo(s2));
        }
        
        static {
            QNAME_COMPARATOR = new Comparator<QName>() {
                @Override
                public int compare(final QName qn1, final QName qn2) {
                    if (qn1 == qn2 || qn1.equals(qn2)) {
                        return 0;
                    }
                    final int result = qn1.getNamespaceURI().compareTo(qn2.getNamespaceURI());
                    if (result != 0) {
                        return result;
                    }
                    return qn1.getLocalPart().compareTo(qn2.getLocalPart());
                }
            };
        }
    }
    
    public static class Collections
    {
        public static <E, T extends Collection<? extends E>, U extends Collection<? extends E>> Collection<Collection<E>> combine(final U initialBase, final Collection<T> options, final boolean ignoreEmptyOption) {
            List<Collection<E>> combinations = null;
            if (options == null || options.isEmpty()) {
                if (initialBase != null) {
                    combinations = new ArrayList<Collection<E>>(1);
                    combinations.add(new ArrayList<E>(initialBase));
                }
                return combinations;
            }
            final Collection<E> base = new LinkedList<E>();
            if (initialBase != null && !initialBase.isEmpty()) {
                base.addAll(initialBase);
            }
            int finalCombinationsSize = 1;
            final Queue<T> optionProcessingQueue = new LinkedList<T>();
            for (final T option : options) {
                final int optionSize = option.size();
                if (optionSize == 0) {
                    if (!ignoreEmptyOption) {
                        return null;
                    }
                    continue;
                }
                else if (optionSize == 1) {
                    base.addAll(option);
                }
                else {
                    optionProcessingQueue.offer(option);
                    finalCombinationsSize *= optionSize;
                }
            }
            combinations = new ArrayList<Collection<E>>(finalCombinationsSize);
            combinations.add(base);
            if (finalCombinationsSize > 1) {
                T processedOption;
                while ((processedOption = optionProcessingQueue.poll()) != null) {
                    final int actualSemiCombinationCollectionSize = combinations.size();
                    final int newSemiCombinationCollectionSize = actualSemiCombinationCollectionSize * processedOption.size();
                    int semiCombinationIndex = 0;
                    for (final E optionElement : processedOption) {
                        for (int i = 0; i < actualSemiCombinationCollectionSize; ++i) {
                            final Collection<E> semiCombination = combinations.get(semiCombinationIndex);
                            if (semiCombinationIndex + actualSemiCombinationCollectionSize < newSemiCombinationCollectionSize) {
                                combinations.add(new LinkedList<E>((Collection<? extends E>)semiCombination));
                            }
                            semiCombination.add(optionElement);
                            ++semiCombinationIndex;
                        }
                    }
                }
            }
            return combinations;
        }
    }
    
    static class Reflection
    {
        private static final PolicyLogger LOGGER;
        
        static <T> T invoke(final Object target, final String methodName, final Class<T> resultClass, final Object... parameters) throws RuntimePolicyUtilsException {
            Class[] parameterTypes;
            if (parameters != null && parameters.length > 0) {
                parameterTypes = new Class[parameters.length];
                int i = 0;
                for (final Object parameter : parameters) {
                    parameterTypes[i++] = parameter.getClass();
                }
            }
            else {
                parameterTypes = null;
            }
            return invoke(target, methodName, resultClass, parameters, parameterTypes);
        }
        
        public static <T> T invoke(final Object target, final String methodName, final Class<T> resultClass, final Object[] parameters, final Class[] parameterTypes) throws RuntimePolicyUtilsException {
            try {
                final Method method = target.getClass().getMethod(methodName, (Class<?>[])parameterTypes);
                final Object result = MethodUtil.invoke(target, method, parameters);
                return resultClass.cast(result);
            }
            catch (final IllegalArgumentException e) {
                throw Reflection.LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), e));
            }
            catch (final InvocationTargetException e2) {
                throw Reflection.LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), e2));
            }
            catch (final IllegalAccessException e3) {
                throw Reflection.LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), e3.getCause()));
            }
            catch (final SecurityException e4) {
                throw Reflection.LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), e4));
            }
            catch (final NoSuchMethodException e5) {
                throw Reflection.LOGGER.logSevereException(new RuntimePolicyUtilsException(createExceptionMessage(target, parameters, methodName), e5));
            }
        }
        
        private static String createExceptionMessage(final Object target, final Object[] parameters, final String methodName) {
            return LocalizationMessages.WSP_0061_METHOD_INVOCATION_FAILED(target.getClass().getName(), methodName, (parameters == null) ? null : Arrays.asList(parameters).toString());
        }
        
        static {
            LOGGER = PolicyLogger.getLogger(Reflection.class);
        }
    }
    
    public static class ConfigFile
    {
        public static String generateFullName(final String configFileIdentifier) throws PolicyException {
            if (configFileIdentifier != null) {
                final StringBuffer buffer = new StringBuffer("wsit-");
                buffer.append(configFileIdentifier).append(".xml");
                return buffer.toString();
            }
            throw new PolicyException(LocalizationMessages.WSP_0080_IMPLEMENTATION_EXPECTED_NOT_NULL());
        }
        
        public static URL loadFromContext(final String configFileName, final Object context) {
            return Reflection.invoke(context, "getResource", URL.class, configFileName);
        }
        
        public static URL loadFromClasspath(final String configFileName) {
            final ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                return ClassLoader.getSystemResource(configFileName);
            }
            return cl.getResource(configFileName);
        }
    }
    
    public static class ServiceProvider
    {
        public static <T> T[] load(final Class<T> serviceClass, final ClassLoader loader) {
            return ServiceFinder.find(serviceClass, loader).toArray();
        }
        
        public static <T> T[] load(final Class<T> serviceClass) {
            return ServiceFinder.find(serviceClass).toArray();
        }
    }
    
    public static class Rfc2396
    {
        private static final PolicyLogger LOGGER;
        
        public static String unquote(final String quoted) {
            if (null == quoted) {
                return null;
            }
            final byte[] unquoted = new byte[quoted.length()];
            int newLength = 0;
            for (int i = 0; i < quoted.length(); ++i) {
                final char c = quoted.charAt(i);
                if ('%' == c) {
                    if (i + 2 >= quoted.length()) {
                        throw Rfc2396.LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted)), false);
                    }
                    final int hi = Character.digit(quoted.charAt(++i), 16);
                    final int lo = Character.digit(quoted.charAt(++i), 16);
                    if (0 > hi || 0 > lo) {
                        throw Rfc2396.LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted)), false);
                    }
                    unquoted[newLength++] = (byte)(hi * 16 + lo);
                }
                else {
                    unquoted[newLength++] = (byte)c;
                }
            }
            try {
                return new String(unquoted, 0, newLength, "utf-8");
            }
            catch (final UnsupportedEncodingException uee) {
                throw Rfc2396.LOGGER.logSevereException(new RuntimePolicyUtilsException(LocalizationMessages.WSP_0079_ERROR_WHILE_RFC_2396_UNESCAPING(quoted), uee));
            }
        }
        
        static {
            LOGGER = PolicyLogger.getLogger(Reflection.class);
        }
    }
}
