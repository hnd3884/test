package org.msgpack.template.builder;

import org.msgpack.MessageTypeException;
import org.msgpack.unpacker.Unpacker;
import javassist.CtMethod;
import javassist.CtNewMethod;
import java.io.IOException;
import org.msgpack.packer.Packer;
import java.lang.reflect.InvocationTargetException;
import javassist.NotFoundException;
import javassist.CannotCompileException;
import org.msgpack.template.Template;
import javassist.CtClass;
import java.util.logging.Logger;

public abstract class BuildContext<T extends FieldEntry>
{
    private static Logger LOG;
    protected JavassistTemplateBuilder director;
    protected String tmplName;
    protected CtClass tmplCtClass;
    protected StringBuilder stringBuilder;
    
    protected abstract Template buildTemplate(final Class<?> p0, final T[] p1, final Template[] p2);
    
    protected abstract void setSuperClass() throws CannotCompileException, NotFoundException;
    
    protected abstract void buildConstructor() throws CannotCompileException, NotFoundException;
    
    public BuildContext(final JavassistTemplateBuilder director) {
        this.stringBuilder = null;
        this.director = director;
    }
    
    protected Template build(final String className) {
        try {
            this.reset(className, false);
            BuildContext.LOG.fine(String.format("started generating template class %s for original class %s", this.tmplCtClass.getName(), className));
            this.buildClass();
            this.buildConstructor();
            this.buildMethodInit();
            this.buildWriteMethod();
            this.buildReadMethod();
            BuildContext.LOG.fine(String.format("finished generating template class %s for original class %s", this.tmplCtClass.getName(), className));
            return this.buildInstance(this.createClass());
        }
        catch (final Exception e) {
            final String code = this.getBuiltString();
            if (code != null) {
                BuildContext.LOG.severe("builder: " + code);
                throw new TemplateBuildException("Cannot compile: " + code, e);
            }
            throw new TemplateBuildException(e);
        }
    }
    
    protected void reset(final String className, final boolean isWritten) {
        String tmplName = null;
        if (!isWritten) {
            tmplName = className + "_$$_Template" + "_" + this.director.hashCode() + "_" + this.director.nextSeqId();
        }
        else {
            tmplName = className + "_$$_Template";
        }
        this.tmplCtClass = this.director.makeCtClass(tmplName);
    }
    
    protected void buildClass() throws CannotCompileException, NotFoundException {
        this.setSuperClass();
        this.tmplCtClass.addInterface(this.director.getCtClass(Template.class.getName()));
    }
    
    protected void buildMethodInit() {
    }
    
    protected abstract Template buildInstance(final Class<?> p0) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException;
    
    protected void buildWriteMethod() throws CannotCompileException, NotFoundException {
        BuildContext.LOG.fine(String.format("started generating write method in template class %s", this.tmplCtClass.getName()));
        final String mbody = this.buildWriteMethodBody();
        final int mod = 1;
        final CtClass returnType = CtClass.voidType;
        final String mname = "write";
        final CtClass[] paramTypes = { this.director.getCtClass(Packer.class.getName()), this.director.getCtClass(Object.class.getName()), CtClass.booleanType };
        final CtClass[] exceptTypes = { this.director.getCtClass(IOException.class.getName()) };
        BuildContext.LOG.fine(String.format("compiling write method body: %s", mbody));
        final CtMethod newCtMethod = CtNewMethod.make(mod, returnType, mname, paramTypes, exceptTypes, mbody, this.tmplCtClass);
        this.tmplCtClass.addMethod(newCtMethod);
        BuildContext.LOG.fine(String.format("finished generating write method in template class %s", this.tmplCtClass.getName()));
    }
    
    protected abstract String buildWriteMethodBody();
    
    protected void buildReadMethod() throws CannotCompileException, NotFoundException {
        BuildContext.LOG.fine(String.format("started generating read method in template class %s", this.tmplCtClass.getName()));
        final String mbody = this.buildReadMethodBody();
        final int mod = 1;
        final CtClass returnType = this.director.getCtClass(Object.class.getName());
        final String mname = "read";
        final CtClass[] paramTypes = { this.director.getCtClass(Unpacker.class.getName()), this.director.getCtClass(Object.class.getName()), CtClass.booleanType };
        final CtClass[] exceptTypes = { this.director.getCtClass(MessageTypeException.class.getName()) };
        BuildContext.LOG.fine(String.format("compiling read method body: %s", mbody));
        final CtMethod newCtMethod = CtNewMethod.make(mod, returnType, mname, paramTypes, exceptTypes, mbody, this.tmplCtClass);
        this.tmplCtClass.addMethod(newCtMethod);
        BuildContext.LOG.fine(String.format("finished generating read method in template class %s", this.tmplCtClass.getName()));
    }
    
    protected abstract String buildReadMethodBody();
    
    protected Class<?> createClass() throws CannotCompileException {
        return this.tmplCtClass.toClass((ClassLoader)null, this.getClass().getProtectionDomain());
    }
    
    protected void saveClass(final String directoryName) throws CannotCompileException, IOException {
        this.tmplCtClass.writeFile(directoryName);
    }
    
    protected void resetStringBuilder() {
        this.stringBuilder = new StringBuilder();
    }
    
    protected void buildString(final String str) {
        this.stringBuilder.append(str);
    }
    
    protected void buildString(final String format, final Object... args) {
        this.stringBuilder.append(String.format(format, args));
    }
    
    protected String getBuiltString() {
        if (this.stringBuilder == null) {
            return null;
        }
        return this.stringBuilder.toString();
    }
    
    protected String primitiveWriteName(final Class<?> type) {
        return "write";
    }
    
    protected String primitiveReadName(final Class<?> type) {
        if (type == Boolean.TYPE) {
            return "readBoolean";
        }
        if (type == Byte.TYPE) {
            return "readByte";
        }
        if (type == Short.TYPE) {
            return "readShort";
        }
        if (type == Integer.TYPE) {
            return "readInt";
        }
        if (type == Long.TYPE) {
            return "readLong";
        }
        if (type == Float.TYPE) {
            return "readFloat";
        }
        if (type == Double.TYPE) {
            return "readDouble";
        }
        if (type == Character.TYPE) {
            return "readInt";
        }
        return null;
    }
    
    protected abstract void writeTemplate(final Class<?> p0, final T[] p1, final Template[] p2, final String p3);
    
    protected void write(final String className, final String directoryName) {
        try {
            this.reset(className, true);
            this.buildClass();
            this.buildConstructor();
            this.buildMethodInit();
            this.buildWriteMethod();
            this.buildReadMethod();
            this.saveClass(directoryName);
        }
        catch (final Exception e) {
            final String code = this.getBuiltString();
            if (code != null) {
                BuildContext.LOG.severe("builder: " + code);
                throw new TemplateBuildException("Cannot compile: " + code, e);
            }
            throw new TemplateBuildException(e);
        }
    }
    
    protected abstract Template loadTemplate(final Class<?> p0, final T[] p1, final Template[] p2);
    
    protected Template load(final String className) {
        final String tmplName = className + "_$$_Template";
        try {
            final Class<?> tmplClass = this.getClass().getClassLoader().loadClass(tmplName);
            return this.buildInstance(tmplClass);
        }
        catch (final ClassNotFoundException e) {
            return null;
        }
        catch (final Exception e2) {
            final String code = this.getBuiltString();
            if (code != null) {
                BuildContext.LOG.severe("builder: " + code);
                throw new TemplateBuildException("Cannot compile: " + code, e2);
            }
            throw new TemplateBuildException(e2);
        }
    }
    
    static {
        BuildContext.LOG = Logger.getLogger(BuildContext.class.getName());
    }
}
