package org.msgpack.template.builder;

import org.msgpack.MessageTypeException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.CannotCompileException;
import org.msgpack.template.Template;

public class BeansBuildContext extends BuildContext<BeansFieldEntry>
{
    protected BeansFieldEntry[] entries;
    protected Class<?> origClass;
    protected String origName;
    protected Template<?>[] templates;
    
    public BeansBuildContext(final JavassistTemplateBuilder director) {
        super(director);
    }
    
    public Template buildTemplate(final Class<?> targetClass, final BeansFieldEntry[] entries, final Template[] templates) {
        this.entries = entries;
        this.templates = templates;
        this.origClass = targetClass;
        this.origName = this.origClass.getName();
        return this.build(this.origName);
    }
    
    @Override
    protected void setSuperClass() throws CannotCompileException, NotFoundException {
        this.tmplCtClass.setSuperclass(this.director.getCtClass(JavassistTemplateBuilder.JavassistTemplate.class.getName()));
    }
    
    @Override
    protected void buildConstructor() throws CannotCompileException, NotFoundException {
        final CtConstructor newCtCons = CtNewConstructor.make(new CtClass[] { this.director.getCtClass(Class.class.getName()), this.director.getCtClass(Template.class.getName() + "[]") }, new CtClass[0], this.tmplCtClass);
        this.tmplCtClass.addConstructor(newCtCons);
    }
    
    @Override
    protected Template buildInstance(final Class<?> c) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        final Constructor<?> cons = c.getConstructor(Class.class, Template[].class);
        final Object tmpl = cons.newInstance(this.origClass, this.templates);
        return (Template)tmpl;
    }
    
    @Override
    protected void buildMethodInit() {
    }
    
    @Override
    protected String buildWriteMethodBody() {
        this.resetStringBuilder();
        this.buildString("{");
        this.buildString("if($2 == null) {");
        this.buildString("  if($3) {");
        this.buildString("    throw new %s(\"Attempted to write null\");", MessageTypeException.class.getName());
        this.buildString("  }");
        this.buildString("  $1.writeNil();");
        this.buildString("  return;");
        this.buildString("}");
        this.buildString("%s _$$_t = (%s)$2;", this.origName, this.origName);
        this.buildString("$1.writeArrayBegin(%d);", this.entries.length);
        for (int i = 0; i < this.entries.length; ++i) {
            final BeansFieldEntry e = this.entries[i];
            if (!e.isAvailable()) {
                this.buildString("$1.writeNil();");
            }
            else {
                final Class<?> type = e.getType();
                if (type.isPrimitive()) {
                    this.buildString("$1.%s(_$$_t.%s());", this.primitiveWriteName(type), e.getGetterName());
                }
                else {
                    this.buildString("if(_$$_t.%s() == null) {", e.getGetterName());
                    if (e.isNotNullable()) {
                        this.buildString("throw new %s();", MessageTypeException.class.getName());
                    }
                    else {
                        this.buildString("$1.writeNil();");
                    }
                    this.buildString("} else {");
                    this.buildString("  this.templates[%d].write($1, _$$_t.%s());", i, e.getGetterName());
                    this.buildString("}");
                }
            }
        }
        this.buildString("$1.writeArrayEnd();");
        this.buildString("}");
        return this.getBuiltString();
    }
    
    @Override
    protected String buildReadMethodBody() {
        this.resetStringBuilder();
        this.buildString("{ ");
        this.buildString("if(!$3 && $1.trySkipNil()) {");
        this.buildString("  return null;");
        this.buildString("}");
        this.buildString("%s _$$_t;", this.origName);
        this.buildString("if($2 == null) {");
        this.buildString("  _$$_t = new %s();", this.origName);
        this.buildString("} else {");
        this.buildString("  _$$_t = (%s)$2;", this.origName);
        this.buildString("}");
        this.buildString("$1.readArrayBegin();");
        for (int i = 0; i < this.entries.length; ++i) {
            final BeansFieldEntry e = this.entries[i];
            if (!e.isAvailable()) {
                this.buildString("$1.skip();");
            }
            else {
                if (e.isOptional()) {
                    this.buildString("if($1.trySkipNil()) {");
                    this.buildString("_$$_t.%s(null);", e.getSetterName());
                    this.buildString("} else {");
                }
                final Class<?> type = e.getType();
                if (type.isPrimitive()) {
                    this.buildString("_$$_t.%s( $1.%s() );", e.getSetterName(), this.primitiveReadName(type));
                }
                else {
                    this.buildString("_$$_t.%s( (%s)this.templates[%d].read($1, _$$_t.%s()) );", e.getSetterName(), e.getJavaTypeName(), i, e.getGetterName());
                }
                if (e.isOptional()) {
                    this.buildString("}");
                }
            }
        }
        this.buildString("$1.readArrayEnd();");
        this.buildString("return _$$_t;");
        this.buildString("}");
        return this.getBuiltString();
    }
    
    public void writeTemplate(final Class<?> targetClass, final BeansFieldEntry[] entries, final Template[] templates, final String directoryName) {
        throw new UnsupportedOperationException(targetClass.getName());
    }
    
    public Template loadTemplate(final Class<?> targetClass, final BeansFieldEntry[] entries, final Template[] templates) {
        return null;
    }
}
