package org.msgpack.template.builder;

import org.msgpack.unpacker.Unpacker;
import org.msgpack.packer.Packer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.msgpack.MessageTypeException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.CannotCompileException;
import org.msgpack.template.Template;

public class DefaultBuildContext extends BuildContext<FieldEntry>
{
    protected FieldEntry[] entries;
    protected Class<?> origClass;
    protected String origName;
    protected Template<?>[] templates;
    
    public DefaultBuildContext(final JavassistTemplateBuilder director) {
        super(director);
    }
    
    public Template buildTemplate(final Class targetClass, final FieldEntry[] entries, final Template[] templates) {
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
        this.buildString("\n{\n");
        this.buildString("  if ($2 == null) {\n");
        this.buildString("    if ($3) {\n");
        this.buildString("      throw new %s(\"Attempted to write null\");\n", MessageTypeException.class.getName());
        this.buildString("    }\n");
        this.buildString("    $1.writeNil();\n");
        this.buildString("    return;\n");
        this.buildString("  }\n");
        this.buildString("  %s _$$_t = (%s) $2;\n", this.origName, this.origName);
        this.buildString("  $1.writeArrayBegin(%d);\n", this.entries.length);
        for (int i = 0; i < this.entries.length; ++i) {
            final FieldEntry e = this.entries[i];
            if (!e.isAvailable()) {
                this.buildString("  $1.writeNil();\n");
            }
            else {
                final DefaultFieldEntry de = (DefaultFieldEntry)e;
                final boolean isPrivate = Modifier.isPrivate(de.getField().getModifiers());
                final Class<?> type = de.getType();
                if (type.isPrimitive()) {
                    if (!isPrivate) {
                        this.buildString("  $1.%s(_$$_t.%s);\n", this.primitiveWriteName(type), de.getName());
                    }
                    else {
                        this.buildString("  %s.writePrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n", DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                    }
                }
                else {
                    if (!isPrivate) {
                        this.buildString("  if (_$$_t.%s == null) {\n", de.getName());
                    }
                    else {
                        this.buildString("  if (%s.readPrivateField(_$$_t, %s.class, \"%s\") == null) {\n", DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName());
                    }
                    if (de.isNotNullable()) {
                        this.buildString("    throw new %s(\"%s cannot be null by @NotNullable\");\n", MessageTypeException.class.getName(), de.getName());
                    }
                    else {
                        this.buildString("    $1.writeNil();\n");
                    }
                    this.buildString("  } else {\n");
                    if (!isPrivate) {
                        this.buildString("    templates[%d].write($1, _$$_t.%s);\n", i, de.getName());
                    }
                    else {
                        this.buildString("    %s.writePrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n", DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                    }
                    this.buildString("  }\n");
                }
            }
        }
        this.buildString("  $1.writeArrayEnd();\n");
        this.buildString("}\n");
        return this.getBuiltString();
    }
    
    public static Object readPrivateField(final Object target, final Class targetClass, final String fieldName) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            final Object valueReference = field.get(target);
            return valueReference;
        }
        catch (final Exception e) {
            throw new MessageTypeException(e);
        }
        finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }
    
    public static void writePrivateField(final Packer packer, final Object target, final Class targetClass, final String fieldName, final Template tmpl) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            final Object valueReference = field.get(target);
            tmpl.write(packer, valueReference);
        }
        catch (final Exception e) {
            throw new MessageTypeException(e);
        }
        finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }
    
    @Override
    protected String buildReadMethodBody() {
        this.resetStringBuilder();
        this.buildString("\n{\n");
        this.buildString("  if (!$3 && $1.trySkipNil()) {\n");
        this.buildString("    return null;\n");
        this.buildString("  }\n");
        this.buildString("  %s _$$_t;\n", this.origName);
        this.buildString("  if ($2 == null) {\n");
        this.buildString("    _$$_t = new %s();\n", this.origName);
        this.buildString("  } else {\n");
        this.buildString("    _$$_t = (%s) $2;\n", this.origName);
        this.buildString("  }\n");
        this.buildString("  $1.readArrayBegin();\n");
        for (int i = 0; i < this.entries.length; ++i) {
            final FieldEntry e = this.entries[i];
            if (!e.isAvailable()) {
                this.buildString("  $1.skip();\n");
            }
            else {
                if (e.isOptional()) {
                    this.buildString("  if ($1.trySkipNil()) {");
                    this.buildString("  } else {\n");
                }
                final DefaultFieldEntry de = (DefaultFieldEntry)e;
                final boolean isPrivate = Modifier.isPrivate(de.getField().getModifiers());
                final Class<?> type = de.getType();
                if (type.isPrimitive()) {
                    if (!isPrivate) {
                        this.buildString("    _$$_t.%s = $1.%s();\n", de.getName(), this.primitiveReadName(type));
                    }
                    else {
                        this.buildString("    %s.readPrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n", DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                    }
                }
                else if (!isPrivate) {
                    this.buildString("    _$$_t.%s = (%s) this.templates[%d].read($1, _$$_t.%s);\n", de.getName(), de.getJavaTypeName(), i, de.getName());
                }
                else {
                    this.buildString("    %s.readPrivateField($1, _$$_t, %s.class, \"%s\", templates[%d]);\n", DefaultBuildContext.class.getName(), de.getField().getDeclaringClass().getName(), de.getName(), i);
                }
                if (de.isOptional()) {
                    this.buildString("  }\n");
                }
            }
        }
        this.buildString("  $1.readArrayEnd();\n");
        this.buildString("  return _$$_t;\n");
        this.buildString("}\n");
        return this.getBuiltString();
    }
    
    public static void readPrivateField(final Unpacker unpacker, final Object target, final Class targetClass, final String fieldName, final Template tmpl) {
        Field field = null;
        try {
            field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            final Object fieldReference = field.get(target);
            final Object valueReference = tmpl.read(unpacker, fieldReference);
            if (valueReference != fieldReference) {
                field.set(target, valueReference);
            }
        }
        catch (final Exception e) {
            throw new MessageTypeException(e);
        }
        finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }
    
    public void writeTemplate(final Class<?> targetClass, final FieldEntry[] entries, final Template[] templates, final String directoryName) {
        this.entries = entries;
        this.templates = templates;
        this.origClass = targetClass;
        this.write(this.origName = this.origClass.getName(), directoryName);
    }
    
    public Template loadTemplate(final Class<?> targetClass, final FieldEntry[] entries, final Template[] templates) {
        this.entries = entries;
        this.templates = templates;
        this.origClass = targetClass;
        this.origName = this.origClass.getName();
        return this.load(this.origName);
    }
}
