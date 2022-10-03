package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;
import com.sun.org.apache.bcel.internal.Repository;
import com.sun.org.apache.bcel.internal.generic.ArrayType;
import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.Method;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.bcel.internal.classfile.Visitor;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.io.OutputStream;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import java.io.PrintWriter;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import com.sun.org.apache.bcel.internal.classfile.EmptyVisitor;

public class BCELifier extends EmptyVisitor
{
    private JavaClass _clazz;
    private PrintWriter _out;
    private ConstantPoolGen _cp;
    
    public BCELifier(final JavaClass clazz, final OutputStream out) {
        this._clazz = clazz;
        this._out = new PrintWriter(out);
        this._cp = new ConstantPoolGen(this._clazz.getConstantPool());
    }
    
    public void start() {
        this.visitJavaClass(this._clazz);
        this._out.flush();
    }
    
    @Override
    public void visitJavaClass(final JavaClass clazz) {
        String class_name = clazz.getClassName();
        final String super_name = clazz.getSuperclassName();
        final String package_name = clazz.getPackageName();
        final String inter = Utility.printArray(clazz.getInterfaceNames(), false, true);
        if (!"".equals(package_name)) {
            class_name = class_name.substring(package_name.length() + 1);
            this._out.println("package " + package_name + ";\n");
        }
        this._out.println("import com.sun.org.apache.bcel.internal.generic.*;");
        this._out.println("import com.sun.org.apache.bcel.internal.classfile.*;");
        this._out.println("import com.sun.org.apache.bcel.internal.*;");
        this._out.println("import java.io.*;\n");
        this._out.println("public class " + class_name + "Creator implements Constants {");
        this._out.println("  private InstructionFactory _factory;");
        this._out.println("  private ConstantPoolGen    _cp;");
        this._out.println("  private ClassGen           _cg;\n");
        this._out.println("  public " + class_name + "Creator() {");
        this._out.println("    _cg = new ClassGen(\"" + ("".equals(package_name) ? class_name : (package_name + "." + class_name)) + "\", \"" + super_name + "\", \"" + clazz.getSourceFileName() + "\", " + printFlags(clazz.getAccessFlags(), true) + ", new String[] { " + inter + " });\n");
        this._out.println("    _cp = _cg.getConstantPool();");
        this._out.println("    _factory = new InstructionFactory(_cg, _cp);");
        this._out.println("  }\n");
        this.printCreate();
        final Field[] fields = clazz.getFields();
        if (fields.length > 0) {
            this._out.println("  private void createFields() {");
            this._out.println("    FieldGen field;");
            for (int i = 0; i < fields.length; ++i) {
                fields[i].accept(this);
            }
            this._out.println("  }\n");
        }
        final Method[] methods = clazz.getMethods();
        for (int j = 0; j < methods.length; ++j) {
            this._out.println("  private void createMethod_" + j + "() {");
            methods[j].accept(this);
            this._out.println("  }\n");
        }
        this.printMain();
        this._out.println("}");
    }
    
    private void printCreate() {
        this._out.println("  public void create(OutputStream out) throws IOException {");
        final Field[] fields = this._clazz.getFields();
        if (fields.length > 0) {
            this._out.println("    createFields();");
        }
        final Method[] methods = this._clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            this._out.println("    createMethod_" + i + "();");
        }
        this._out.println("    _cg.getJavaClass().dump(out);");
        this._out.println("  }\n");
    }
    
    private void printMain() {
        final String class_name = this._clazz.getClassName();
        this._out.println("  public static void _main(String[] args) throws Exception {");
        this._out.println("    " + class_name + "Creator creator = new " + class_name + "Creator();");
        this._out.println("    creator.create(new FileOutputStream(\"" + class_name + ".class\"));");
        this._out.println("  }");
    }
    
    @Override
    public void visitField(final Field field) {
        this._out.println("\n    field = new FieldGen(" + printFlags(field.getAccessFlags()) + ", " + printType(field.getSignature()) + ", \"" + field.getName() + "\", _cp);");
        final ConstantValue cv = field.getConstantValue();
        if (cv != null) {
            final String value = cv.toString();
            this._out.println("    field.setInitValue(" + value + ")");
        }
        this._out.println("    _cg.addField(field.getField());");
    }
    
    @Override
    public void visitMethod(final Method method) {
        final MethodGen mg = new MethodGen(method, this._clazz.getClassName(), this._cp);
        final Type result_type = mg.getReturnType();
        final Type[] arg_types = mg.getArgumentTypes();
        this._out.println("    InstructionList il = new InstructionList();");
        this._out.println("    MethodGen method = new MethodGen(" + printFlags(method.getAccessFlags()) + ", " + printType(result_type) + ", " + printArgumentTypes(arg_types) + ", new String[] { " + Utility.printArray(mg.getArgumentNames(), false, true) + " }, \"" + method.getName() + "\", \"" + this._clazz.getClassName() + "\", il, _cp);\n");
        final BCELFactory factory = new BCELFactory(mg, this._out);
        factory.start();
        this._out.println("    method.setMaxStack();");
        this._out.println("    method.setMaxLocals();");
        this._out.println("    _cg.addMethod(method.getMethod());");
        this._out.println("    il.dispose();");
    }
    
    static String printFlags(final int flags) {
        return printFlags(flags, false);
    }
    
    static String printFlags(final int flags, final boolean for_class) {
        if (flags == 0) {
            return "0";
        }
        final StringBuffer buf = new StringBuffer();
        int i = 0;
        int pow = 1;
        while (i <= 2048) {
            if ((flags & pow) != 0x0) {
                if (pow == 32 && for_class) {
                    buf.append("ACC_SUPER | ");
                }
                else {
                    buf.append("ACC_" + Constants.ACCESS_NAMES[i].toUpperCase() + " | ");
                }
            }
            pow <<= 1;
            ++i;
        }
        final String str = buf.toString();
        return str.substring(0, str.length() - 3);
    }
    
    static String printArgumentTypes(final Type[] arg_types) {
        if (arg_types.length == 0) {
            return "Type.NO_ARGS";
        }
        final StringBuffer args = new StringBuffer();
        for (int i = 0; i < arg_types.length; ++i) {
            args.append(printType(arg_types[i]));
            if (i < arg_types.length - 1) {
                args.append(", ");
            }
        }
        return "new Type[] { " + args.toString() + " }";
    }
    
    static String printType(final Type type) {
        return printType(type.getSignature());
    }
    
    static String printType(final String signature) {
        final Type type = Type.getType(signature);
        final byte t = type.getType();
        if (t <= 12) {
            return "Type." + Constants.TYPE_NAMES[t].toUpperCase();
        }
        if (type.toString().equals("java.lang.String")) {
            return "Type.STRING";
        }
        if (type.toString().equals("java.lang.Object")) {
            return "Type.OBJECT";
        }
        if (type.toString().equals("java.lang.StringBuffer")) {
            return "Type.STRINGBUFFER";
        }
        if (type instanceof ArrayType) {
            final ArrayType at = (ArrayType)type;
            return "new ArrayType(" + printType(at.getBasicType()) + ", " + at.getDimensions() + ")";
        }
        return "new ObjectType(\"" + Utility.signatureToString(signature, false) + "\")";
    }
    
    public static void _main(final String[] argv) throws Exception {
        final String name = argv[0];
        JavaClass java_class;
        if ((java_class = Repository.lookupClass(name)) == null) {
            java_class = new ClassParser(name).parse();
        }
        final BCELifier bcelifier = new BCELifier(java_class, System.out);
        bcelifier.start();
    }
}
