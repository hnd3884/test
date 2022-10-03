package org.apache.tika.config;

import org.w3c.dom.NamedNodeMap;
import java.util.LinkedHashMap;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Collections;
import java.util.Locale;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Set;
import java.util.HashSet;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.TikaConfigException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.tika.utils.XMLReaderUtils;
import java.io.InputStream;

public abstract class ConfigBase
{
    protected static <T> T buildSingle(final String itemName, final Class<T> itemClass, final InputStream is) throws TikaConfigException, IOException {
        Node properties = null;
        try {
            properties = XMLReaderUtils.buildDOM(is).getDocumentElement();
        }
        catch (final SAXException e) {
            throw new IOException(e);
        }
        catch (final TikaException e2) {
            throw new TikaConfigException("problem loading xml to dom", e2);
        }
        if (!properties.getLocalName().equals("properties")) {
            throw new TikaConfigException("expect properties as root node");
        }
        final NodeList children = properties.getChildNodes();
        T toReturn = null;
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if (child.getNodeType() == 1) {
                if (itemName.equals(child.getLocalName())) {
                    if (toReturn != null) {
                        throw new TikaConfigException("There can only be one " + itemName + " in a config");
                    }
                    final T item = buildClass(child, itemName, itemClass);
                    setParams(item, child, new HashSet<String>());
                    toReturn = item;
                }
            }
        }
        if (toReturn == null) {
            throw new TikaConfigException("could not find " + itemName);
        }
        return toReturn;
    }
    
    protected static <P, T> P buildComposite(final String compositeElementName, final Class<P> compositeClass, final String itemName, final Class<T> itemClass, final InputStream is) throws TikaConfigException, IOException {
        Element properties = null;
        try {
            properties = XMLReaderUtils.buildDOM(is).getDocumentElement();
        }
        catch (final SAXException e) {
            throw new IOException(e);
        }
        catch (final TikaException e2) {
            throw new TikaConfigException("problem loading xml to dom", e2);
        }
        return buildComposite(compositeElementName, compositeClass, itemName, itemClass, properties);
    }
    
    protected static <P, T> P buildComposite(final String compositeElementName, final Class<P> compositeClass, final String itemName, final Class<T> itemClass, final Element properties) throws TikaConfigException, IOException {
        if (!properties.getLocalName().equals("properties")) {
            throw new TikaConfigException("expect properties as root node");
        }
        final NodeList children = properties.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if (child.getNodeType() == 1) {
                if (compositeElementName.equals(child.getLocalName())) {
                    final List<T> components = loadComposite(child, itemName, (Class<? extends T>)itemClass);
                    Constructor constructor = null;
                    try {
                        constructor = compositeClass.getConstructor(List.class);
                        final P composite = constructor.newInstance(components);
                        setParams(composite, child, new HashSet<String>(), itemName);
                        return composite;
                    }
                    catch (final NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        throw new TikaConfigException("can't build composite class", e);
                    }
                }
            }
        }
        throw new TikaConfigException("could not find " + compositeElementName);
    }
    
    private static <T> List<T> loadComposite(final Node composite, final String itemName, final Class<? extends T> itemClass) throws TikaConfigException {
        final NodeList children = composite.getChildNodes();
        final List<T> items = new ArrayList<T>();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if (child.getNodeType() == 1) {
                if (itemName.equals(child.getLocalName())) {
                    final T item = buildClass(child, itemName, itemClass);
                    setParams(item, child, new HashSet<String>());
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    private static <T> T buildClass(final Node node, final String elementName, final Class itemClass) throws TikaConfigException {
        final Node classNameNode = node.getAttributes().getNamedItem("class");
        if (classNameNode == null) {
            throw new TikaConfigException("element " + elementName + " must have a 'class' attribute");
        }
        final String className = classNameNode.getTextContent();
        try {
            final Class clazz = Class.forName(className);
            if (!itemClass.isAssignableFrom(clazz)) {
                throw new TikaConfigException(elementName + " with class name " + className + " must be of type '" + itemClass.getName() + "'");
            }
            return clazz.newInstance();
        }
        catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new TikaConfigException("problem loading " + elementName, e);
        }
    }
    
    private static void setParams(final Object object, final Node targetNode, final Set<String> settings) throws TikaConfigException {
        setParams(object, targetNode, settings, null);
    }
    
    private static void setParams(final Object object, final Node targetNode, final Set<String> settings, final String exceptNodeName) throws TikaConfigException {
        final NodeList children = targetNode.getChildNodes();
        NodeList params = null;
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if ("params".equals(child.getLocalName())) {
                params = child.getChildNodes();
            }
            else if (child.getNodeType() == 1 && !child.getLocalName().equals(exceptNodeName)) {
                final String itemName = child.getLocalName();
                final String setter = "set" + itemName.substring(0, 1).toUpperCase(Locale.US) + itemName.substring(1);
                Class itemClass = null;
                Method setterMethod = null;
                for (final Method method : object.getClass().getMethods()) {
                    if (setter.equals(method.getName())) {
                        final Class<?>[] classes = method.getParameterTypes();
                        if (classes.length == 1) {
                            itemClass = classes[0];
                            setterMethod = method;
                            break;
                        }
                    }
                }
                if (itemClass == null) {
                    throw new TikaConfigException("Couldn't find setter '" + setter + "' for " + itemName);
                }
                final Object item = buildClass(child, itemName, itemClass);
                setParams(itemClass.cast(item), child, new HashSet<String>());
                try {
                    setterMethod.invoke(object, item);
                }
                catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new TikaConfigException("problem creating " + itemName, e);
                }
            }
        }
        if (params != null) {
            for (int i = 0; i < params.getLength(); ++i) {
                final Node param = params.item(i);
                if (param.getNodeType() == 1) {
                    final String localName = param.getLocalName();
                    if (localName != null) {
                        if (!localName.equals(exceptNodeName)) {
                            final String txt = param.getTextContent();
                            if (hasChildNodes(param)) {
                                if (isMap(param)) {
                                    tryToSetMap(object, param);
                                }
                                else {
                                    tryToSetList(object, param);
                                }
                            }
                            else {
                                tryToSet(object, localName, txt);
                            }
                            if (localName != null && txt != null) {
                                settings.add(localName);
                            }
                        }
                    }
                }
            }
        }
        if (object instanceof Initializable) {
            ((Initializable)object).initialize(Collections.EMPTY_MAP);
            ((Initializable)object).checkInitialization(InitializableProblemHandler.THROW);
        }
    }
    
    private static boolean hasChildNodes(final Node param) {
        if (!param.hasChildNodes()) {
            return false;
        }
        final NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node item = nodeList.item(i);
            if (item.getNodeType() == 1) {
                return true;
            }
        }
        return false;
    }
    
    private static void tryToSetList(final Object object, final Node param) throws TikaConfigException {
        final String name = param.getLocalName();
        final List<String> strings = new ArrayList<String>();
        final NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node n = nodeList.item(i);
            if (n.getNodeType() == 1) {
                final String txt = n.getTextContent();
                if (txt != null) {
                    strings.add(txt);
                }
            }
        }
        final String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        try {
            final Method m = object.getClass().getMethod(setter, List.class);
            m.invoke(object, strings);
        }
        catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new TikaConfigException("can't set " + name, e);
        }
    }
    
    private static void tryToSetMap(final Object object, final Node param) throws TikaConfigException {
        final String name = param.getLocalName();
        final Map<String, String> map = new LinkedHashMap<String, String>();
        final NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node n = nodeList.item(i);
            if (n.getNodeType() == 1) {
                final NamedNodeMap m = n.getAttributes();
                String key = null;
                String value = null;
                if (m.getNamedItem("from") != null) {
                    key = m.getNamedItem("from").getTextContent();
                }
                else if (m.getNamedItem("key") != null) {
                    key = m.getNamedItem("key").getTextContent();
                }
                if (m.getNamedItem("to") != null) {
                    value = m.getNamedItem("to").getTextContent();
                }
                else if (m.getNamedItem("value") != null) {
                    value = m.getNamedItem("value").getTextContent();
                }
                if (key == null) {
                    throw new TikaConfigException("must specify a 'key' or 'from' value in a map object : " + param);
                }
                if (value == null) {
                    throw new TikaConfigException("must specify a 'value' or 'to' value in a map object : " + param);
                }
                map.put(key, value);
            }
        }
        final String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        try {
            final Method j = object.getClass().getMethod(setter, Map.class);
            j.invoke(object, map);
        }
        catch (final NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new TikaConfigException("can't set " + name, e);
        }
    }
    
    private static boolean isMap(final Node param) {
        final NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            final Node n = nodeList.item(i);
            if (n.getNodeType() == 1 && n.hasAttributes() && n.getAttributes().getNamedItem("from") != null && n.getAttributes().getNamedItem("to") != null) {
                return true;
            }
        }
        return false;
    }
    
    private static void tryToSet(final Object object, final String name, final String value) throws TikaConfigException {
        final String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        final Class[] array;
        final Class[] types = array = new Class[] { String.class, Boolean.TYPE, Long.TYPE, Integer.TYPE, Double.TYPE, Float.TYPE };
        final int length = array.length;
        int i = 0;
        while (i < length) {
            final Class t = array[i];
            try {
                final Method m = object.getClass().getMethod(setter, t);
                if (t == Integer.TYPE) {
                    try {
                        m.invoke(object, Integer.parseInt(value));
                        return;
                    }
                    catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Long.TYPE) {
                    try {
                        m.invoke(object, Long.parseLong(value));
                        return;
                    }
                    catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Boolean.TYPE) {
                    try {
                        m.invoke(object, Boolean.parseBoolean(value));
                        return;
                    }
                    catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Float.TYPE) {
                    try {
                        m.invoke(object, Float.parseFloat(value));
                        return;
                    }
                    catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Double.TYPE) {
                    try {
                        m.invoke(object, Double.parseDouble(value));
                        return;
                    }
                    catch (final IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                try {
                    m.invoke(object, value);
                    return;
                }
                catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new TikaConfigException("bad parameter " + setter, e);
                }
            }
            catch (final NoSuchMethodException ex) {
                ++i;
                continue;
            }
            break;
        }
        throw new TikaConfigException("Couldn't find setter: " + setter + " for object " + object.getClass());
    }
    
    protected void handleSettings(final Set<String> settings) {
    }
    
    protected Set<String> configure(final String nodeName, final InputStream is) throws TikaConfigException, IOException {
        final Set<String> settings = new HashSet<String>();
        Node properties = null;
        try {
            properties = XMLReaderUtils.buildDOM(is).getDocumentElement();
        }
        catch (final SAXException e) {
            throw new IOException(e);
        }
        catch (final TikaException e2) {
            throw new TikaConfigException("problem loading xml to dom", e2);
        }
        if (!properties.getLocalName().equals("properties")) {
            throw new TikaConfigException("expect properties as root node");
        }
        final NodeList children = properties.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if (nodeName.equals(child.getLocalName())) {
                setParams(this, child, settings);
            }
        }
        return settings;
    }
}
