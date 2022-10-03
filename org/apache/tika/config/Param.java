package org.apache.tika.config;

import java.util.Iterator;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import java.net.URL;
import java.net.URI;
import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import javax.xml.transform.TransformerException;
import javax.xml.transform.Transformer;
import org.w3c.dom.Element;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.tika.exception.TikaConfigException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.apache.tika.exception.TikaException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import org.apache.tika.utils.XMLReaderUtils;
import java.io.InputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class Param<T> implements Serializable
{
    private static final String LIST = "list";
    private static final Map<Class<?>, String> map;
    private static final Map<String, Class<?>> reverseMap;
    private static final Map<String, Class<?>> wellKnownMap;
    private final List<String> valueStrings;
    private Class<T> type;
    private String name;
    private T actualValue;
    
    public Param() {
        this.valueStrings = new ArrayList<String>();
    }
    
    public Param(final String name, final Class<T> type, final T value) {
        this.valueStrings = new ArrayList<String>();
        this.name = name;
        this.type = type;
        this.actualValue = value;
        if (List.class.isAssignableFrom(value.getClass())) {
            this.valueStrings.addAll((Collection<? extends String>)value);
        }
        else {
            this.valueStrings.add(value.toString());
        }
        if (this.type == null) {
            this.type = (Class)Param.wellKnownMap.get(name);
        }
    }
    
    public Param(final String name, final T value) {
        this(name, (Class<Object>)value.getClass(), value);
    }
    
    public static <T> Param<T> load(final InputStream stream) throws SAXException, IOException, TikaException {
        final DocumentBuilder db = XMLReaderUtils.getDocumentBuilder();
        final Document document = db.parse(stream);
        return load(document.getFirstChild());
    }
    
    public static <T> Param<T> load(final Node node) throws TikaConfigException {
        final Node nameAttr = node.getAttributes().getNamedItem("name");
        final Node typeAttr = node.getAttributes().getNamedItem("type");
        final Node valueAttr = node.getAttributes().getNamedItem("value");
        Node value = node.getFirstChild();
        if (value instanceof NodeList && valueAttr != null) {
            throw new TikaConfigException("can't specify a value attr _and_ a node list");
        }
        if (valueAttr != null && (value == null || value.getTextContent() == null)) {
            value = valueAttr;
        }
        final Param<T> ret = new Param<T>();
        ret.name = nameAttr.getTextContent();
        if (typeAttr != null) {
            ret.setTypeString(typeAttr.getTextContent());
        }
        else {
            ret.type = (Class)Param.wellKnownMap.get(ret.name);
            if (ret.type == null) {
                throw new TikaConfigException("Must specify a \"type\" in: " + node.getLocalName());
            }
        }
        if (List.class.isAssignableFrom(ret.type)) {
            loadList(ret, node);
        }
        else {
            String textContent = "";
            if (value != null) {
                textContent = value.getTextContent();
            }
            ret.actualValue = (T)getTypedValue((Class<Object>)ret.type, textContent);
            ret.valueStrings.add(textContent);
        }
        return ret;
    }
    
    private static <T> void loadList(final Param<T> ret, final Node root) {
        Node child = root.getFirstChild();
        ret.actualValue = (T)new ArrayList();
        while (child != null) {
            if (child.getNodeType() == 1) {
                final Class type = classFromType(child.getLocalName());
                ((List)ret.actualValue).add(getTypedValue((Class<Object>)type, child.getTextContent()));
                ret.valueStrings.add(child.getTextContent());
            }
            child = child.getNextSibling();
        }
    }
    
    private static <T> Class<T> classFromType(final String type) {
        if (Param.reverseMap.containsKey(type)) {
            return (Class)Param.reverseMap.get(type);
        }
        try {
            return (Class<T>)Class.forName(type);
        }
        catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static <T> T getTypedValue(final Class<T> type, final String value) {
        try {
            if (type.isEnum()) {
                final Object val = Enum.valueOf(type, value);
                return (T)val;
            }
            final Constructor<T> constructor = type.getConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(value);
        }
        catch (final NoSuchMethodException e) {
            throw new RuntimeException(type + " doesnt have a constructor that takes String arg", e);
        }
        catch (final IllegalAccessException | InstantiationException | InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Class<T> getType() {
        return this.type;
    }
    
    public void setType(final Class<T> type) {
        this.type = type;
    }
    
    public String getTypeString() {
        if (this.type == null) {
            return null;
        }
        if (List.class.isAssignableFrom(this.type)) {
            return "list";
        }
        if (Param.map.containsKey(this.type)) {
            return Param.map.get(this.type);
        }
        return this.type.getName();
    }
    
    public void setTypeString(final String type) {
        if (type == null || type.isEmpty()) {
            return;
        }
        this.type = classFromType(type);
        this.actualValue = null;
    }
    
    public T getValue() {
        return this.actualValue;
    }
    
    @Override
    public String toString() {
        return "Param{name='" + this.name + '\'' + ", valueStrings='" + this.valueStrings + '\'' + ", actualValue=" + this.actualValue + '}';
    }
    
    public void save(final OutputStream stream) throws TransformerException, TikaException {
        final DocumentBuilder builder = XMLReaderUtils.getDocumentBuilder();
        final Document doc = builder.newDocument();
        final Element paramEl = doc.createElement("param");
        doc.appendChild(paramEl);
        this.save(doc, paramEl);
        final Transformer transformer = XMLReaderUtils.getTransformer();
        transformer.transform(new DOMSource(paramEl), new StreamResult(stream));
    }
    
    public void save(final Document doc, final Node node) {
        if (!(node instanceof Element)) {
            throw new IllegalArgumentException("Not an Element : " + node);
        }
        final Element el = (Element)node;
        el.setAttribute("name", this.getName());
        el.setAttribute("type", this.getTypeString());
        if (List.class.isAssignableFrom(this.actualValue.getClass())) {
            for (int i = 0; i < this.valueStrings.size(); ++i) {
                final String val = this.valueStrings.get(i);
                final String typeString = Param.map.get(((List)this.actualValue).get(i).getClass());
                final Node item = doc.createElement(typeString);
                item.setTextContent(val);
                el.appendChild(item);
            }
        }
        else {
            el.setTextContent(this.valueStrings.get(0));
        }
    }
    
    static {
        map = new HashMap<Class<?>, String>();
        reverseMap = new HashMap<String, Class<?>>();
        wellKnownMap = new HashMap<String, Class<?>>();
        Param.map.put(Boolean.class, "bool");
        Param.map.put(String.class, "string");
        Param.map.put(Byte.class, "byte");
        Param.map.put(Short.class, "short");
        Param.map.put(Integer.class, "int");
        Param.map.put(Long.class, "long");
        Param.map.put(BigInteger.class, "bigint");
        Param.map.put(Float.class, "float");
        Param.map.put(Double.class, "double");
        Param.map.put(File.class, "file");
        Param.map.put(URI.class, "uri");
        Param.map.put(URL.class, "url");
        Param.map.put(ArrayList.class, "list");
        for (final Map.Entry<Class<?>, String> entry : Param.map.entrySet()) {
            Param.reverseMap.put(entry.getValue(), entry.getKey());
        }
        Param.wellKnownMap.put("metadataPolicy", AbstractMultipleParser.MetadataPolicy.class);
    }
}
