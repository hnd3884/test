package sun.security.pkcs11;

import java.util.Arrays;
import sun.security.pkcs11.wrapper.Functions;
import java.util.Iterator;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

final class TemplateManager
{
    private static final boolean DEBUG = false;
    static final String O_ANY = "*";
    static final String O_IMPORT = "import";
    static final String O_GENERATE = "generate";
    private final List<KeyAndTemplate> primitiveTemplates;
    private final Map<TemplateKey, Template> compositeTemplates;
    
    TemplateManager() {
        this.primitiveTemplates = new ArrayList<KeyAndTemplate>();
        this.compositeTemplates = new ConcurrentHashMap<TemplateKey, Template>();
    }
    
    void addTemplate(final String s, final long n, final long n2, final CK_ATTRIBUTE[] array) {
        this.primitiveTemplates.add(new KeyAndTemplate(new TemplateKey(s, n, n2), new Template(array)));
    }
    
    private Template getTemplate(final TemplateKey templateKey) {
        Template buildCompositeTemplate = this.compositeTemplates.get(templateKey);
        if (buildCompositeTemplate == null) {
            buildCompositeTemplate = this.buildCompositeTemplate(templateKey);
            this.compositeTemplates.put(templateKey, buildCompositeTemplate);
        }
        return buildCompositeTemplate;
    }
    
    CK_ATTRIBUTE[] getAttributes(final String s, final long n, final long n2, final CK_ATTRIBUTE[] array) {
        return this.getTemplate(new TemplateKey(s, n, n2)).getAttributes(array);
    }
    
    private Template buildCompositeTemplate(final TemplateKey templateKey) {
        final Template template = new Template();
        for (final KeyAndTemplate keyAndTemplate : this.primitiveTemplates) {
            if (keyAndTemplate.key.appliesTo(templateKey)) {
                template.add(keyAndTemplate.template);
            }
        }
        return template;
    }
    
    private static class KeyAndTemplate
    {
        final TemplateKey key;
        final Template template;
        
        KeyAndTemplate(final TemplateKey key, final Template template) {
            this.key = key;
            this.template = template;
        }
    }
    
    private static final class TemplateKey
    {
        final String operation;
        final long keyType;
        final long keyAlgorithm;
        
        TemplateKey(final String operation, final long keyType, final long keyAlgorithm) {
            this.operation = operation;
            this.keyType = keyType;
            this.keyAlgorithm = keyAlgorithm;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TemplateKey)) {
                return false;
            }
            final TemplateKey templateKey = (TemplateKey)o;
            return this.operation.equals(templateKey.operation) && this.keyType == templateKey.keyType && this.keyAlgorithm == templateKey.keyAlgorithm;
        }
        
        @Override
        public int hashCode() {
            return this.operation.hashCode() + (int)this.keyType + (int)this.keyAlgorithm;
        }
        
        boolean appliesTo(final TemplateKey templateKey) {
            return (this.operation.equals("*") || this.operation.equals(templateKey.operation)) && (this.keyType == 2147483427L || this.keyType == templateKey.keyType) && (this.keyAlgorithm == 2147483426L || this.keyAlgorithm == templateKey.keyAlgorithm);
        }
        
        @Override
        public String toString() {
            return "(" + this.operation + "," + Functions.getObjectClassName(this.keyType) + "," + Functions.getKeyName(this.keyAlgorithm) + ")";
        }
    }
    
    private static final class Template
    {
        private static final CK_ATTRIBUTE[] A0;
        private CK_ATTRIBUTE[] attributes;
        
        Template() {
            this.attributes = Template.A0;
        }
        
        Template(final CK_ATTRIBUTE[] attributes) {
            this.attributes = attributes;
        }
        
        void add(final Template template) {
            this.attributes = this.getAttributes(template.attributes);
        }
        
        CK_ATTRIBUTE[] getAttributes(final CK_ATTRIBUTE[] array) {
            return combine(this.attributes, array);
        }
        
        private static CK_ATTRIBUTE[] combine(final CK_ATTRIBUTE[] array, final CK_ATTRIBUTE[] array2) {
            final ArrayList list = new ArrayList();
            for (final CK_ATTRIBUTE ck_ATTRIBUTE : array) {
                if (ck_ATTRIBUTE.pValue != null) {
                    list.add(ck_ATTRIBUTE);
                }
            }
            for (final CK_ATTRIBUTE ck_ATTRIBUTE2 : array2) {
                final long type = ck_ATTRIBUTE2.type;
                for (final CK_ATTRIBUTE ck_ATTRIBUTE3 : array) {
                    if (ck_ATTRIBUTE3.type == type) {
                        list.remove(ck_ATTRIBUTE3);
                    }
                }
                if (ck_ATTRIBUTE2.pValue != null) {
                    list.add(ck_ATTRIBUTE2);
                }
            }
            return (CK_ATTRIBUTE[])list.toArray(Template.A0);
        }
        
        @Override
        public String toString() {
            return Arrays.asList(this.attributes).toString();
        }
        
        static {
            A0 = new CK_ATTRIBUTE[0];
        }
    }
}
