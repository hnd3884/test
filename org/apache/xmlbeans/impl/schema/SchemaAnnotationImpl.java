package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.XmlCursor;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.SchemaComponent;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.DocumentationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.AppinfoDocument;
import org.apache.xmlbeans.SchemaAnnotation;

public class SchemaAnnotationImpl implements SchemaAnnotation
{
    private SchemaContainer _container;
    private String[] _appInfoAsXml;
    private AppinfoDocument.Appinfo[] _appInfo;
    private String[] _documentationAsXml;
    private DocumentationDocument.Documentation[] _documentation;
    private Attribute[] _attributes;
    private String _filename;
    
    public void setFilename(final String filename) {
        this._filename = filename;
    }
    
    @Override
    public String getSourceName() {
        return this._filename;
    }
    
    @Override
    public XmlObject[] getApplicationInformation() {
        if (this._appInfo == null) {
            final int n = this._appInfoAsXml.length;
            this._appInfo = new AppinfoDocument.Appinfo[n];
            for (int i = 0; i < n; ++i) {
                final String appInfo = this._appInfoAsXml[i];
                try {
                    this._appInfo[i] = AppinfoDocument.Factory.parse(appInfo).getAppinfo();
                }
                catch (final XmlException e) {
                    this._appInfo[i] = AppinfoDocument.Factory.newInstance().getAppinfo();
                }
            }
        }
        return this._appInfo;
    }
    
    @Override
    public XmlObject[] getUserInformation() {
        if (this._documentation == null) {
            final int n = this._documentationAsXml.length;
            this._documentation = new DocumentationDocument.Documentation[n];
            for (int i = 0; i < n; ++i) {
                final String doc = this._documentationAsXml[i];
                try {
                    this._documentation[i] = DocumentationDocument.Factory.parse(doc).getDocumentation();
                }
                catch (final XmlException e) {
                    this._documentation[i] = DocumentationDocument.Factory.newInstance().getDocumentation();
                }
            }
        }
        return this._documentation;
    }
    
    @Override
    public Attribute[] getAttributes() {
        return this._attributes;
    }
    
    @Override
    public int getComponentType() {
        return 8;
    }
    
    @Override
    public SchemaTypeSystem getTypeSystem() {
        return (this._container != null) ? this._container.getTypeSystem() : null;
    }
    
    SchemaContainer getContainer() {
        return this._container;
    }
    
    @Override
    public QName getName() {
        return null;
    }
    
    @Override
    public SchemaComponent.Ref getComponentRef() {
        return null;
    }
    
    public static SchemaAnnotationImpl getAnnotation(final SchemaContainer c, final Annotated elem) {
        final AnnotationDocument.Annotation ann = elem.getAnnotation();
        return getAnnotation(c, elem, ann);
    }
    
    public static SchemaAnnotationImpl getAnnotation(final SchemaContainer c, final XmlObject elem, final AnnotationDocument.Annotation ann) {
        if (StscState.get().noAnn()) {
            return null;
        }
        final SchemaAnnotationImpl result = new SchemaAnnotationImpl(c);
        final ArrayList attrArray = new ArrayList(2);
        addNoSchemaAttributes(elem, attrArray);
        if (ann == null) {
            if (attrArray.size() == 0) {
                return null;
            }
            result._appInfo = new AppinfoDocument.Appinfo[0];
            result._documentation = new DocumentationDocument.Documentation[0];
        }
        else {
            result._appInfo = ann.getAppinfoArray();
            result._documentation = ann.getDocumentationArray();
            addNoSchemaAttributes(ann, attrArray);
        }
        result._attributes = attrArray.toArray(new AttributeImpl[attrArray.size()]);
        return result;
    }
    
    private static void addNoSchemaAttributes(final XmlObject elem, final List attrList) {
        final XmlCursor cursor = elem.newCursor();
        for (boolean hasAttributes = cursor.toFirstAttribute(); hasAttributes; hasAttributes = cursor.toNextAttribute()) {
            final QName name = cursor.getName();
            final String namespaceURI = name.getNamespaceURI();
            if (!"".equals(namespaceURI)) {
                if (!"http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
                    final String attValue = cursor.getTextValue();
                    String prefix;
                    if (attValue.indexOf(58) > 0) {
                        prefix = attValue.substring(0, attValue.indexOf(58));
                    }
                    else {
                        prefix = "";
                    }
                    cursor.push();
                    cursor.toParent();
                    final String valUri = cursor.namespaceForPrefix(prefix);
                    cursor.pop();
                    attrList.add(new AttributeImpl(name, attValue, valUri));
                }
            }
        }
        cursor.dispose();
    }
    
    private SchemaAnnotationImpl(final SchemaContainer c) {
        this._container = c;
    }
    
    SchemaAnnotationImpl(final SchemaContainer c, final String[] aapStrings, final String[] adocStrings, final Attribute[] aat) {
        this._container = c;
        this._appInfoAsXml = aapStrings;
        this._documentationAsXml = adocStrings;
        this._attributes = aat;
    }
    
    static class AttributeImpl implements Attribute
    {
        private QName _name;
        private String _value;
        private String _valueUri;
        
        AttributeImpl(final QName name, final String value, final String valueUri) {
            this._name = name;
            this._value = value;
            this._valueUri = valueUri;
        }
        
        @Override
        public QName getName() {
            return this._name;
        }
        
        @Override
        public String getValue() {
            return this._value;
        }
        
        @Override
        public String getValueUri() {
            return this._valueUri;
        }
    }
}
