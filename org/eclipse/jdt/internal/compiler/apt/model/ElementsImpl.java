package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.internal.compiler.lookup.Scope;
import java.io.IOException;
import java.io.Writer;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.regex.Matcher;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import javax.lang.model.element.Name;
import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import java.util.Collection;
import java.util.LinkedHashSet;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import java.util.HashMap;
import javax.lang.model.element.TypeElement;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import java.util.ArrayList;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.AnnotationMirror;
import java.util.List;
import javax.lang.model.element.Element;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import java.util.regex.Pattern;
import javax.lang.model.util.Elements;

public class ElementsImpl implements Elements
{
    private static final Pattern INITIAL_DELIMITER;
    private final BaseProcessingEnvImpl _env;
    
    static {
        INITIAL_DELIMITER = Pattern.compile("^\\s*/\\*+");
    }
    
    public ElementsImpl(final BaseProcessingEnvImpl env) {
        this._env = env;
    }
    
    @Override
    public List<? extends AnnotationMirror> getAllAnnotationMirrors(final Element e) {
        if (e.getKind() == ElementKind.CLASS && e instanceof TypeElementImpl) {
            final List<AnnotationBinding> annotations = new ArrayList<AnnotationBinding>();
            final Set<ReferenceBinding> annotationTypes = new HashSet<ReferenceBinding>();
            ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)e)._binding;
            boolean checkIfInherited = false;
            while (binding != null) {
                if (binding instanceof ParameterizedTypeBinding) {
                    binding = ((ParameterizedTypeBinding)binding).genericType();
                }
                AnnotationBinding[] packedAnnotationBindings;
                for (int length = (packedAnnotationBindings = Factory.getPackedAnnotationBindings(binding.getAnnotations())).length, i = 0; i < length; ++i) {
                    final AnnotationBinding annotation = packedAnnotationBindings[i];
                    if (annotation != null) {
                        final ReferenceBinding annotationType = annotation.getAnnotationType();
                        if (!checkIfInherited || (annotationType.getAnnotationTagBits() & 0x1000000000000L) != 0x0L) {
                            if (!annotationTypes.contains(annotationType)) {
                                annotationTypes.add(annotationType);
                                annotations.add(annotation);
                            }
                        }
                    }
                }
                binding = binding.superclass();
                checkIfInherited = true;
            }
            final List<AnnotationMirror> list = new ArrayList<AnnotationMirror>(annotations.size());
            for (final AnnotationBinding annotation2 : annotations) {
                list.add(this._env.getFactory().newAnnotationMirror(annotation2));
            }
            return Collections.unmodifiableList((List<? extends AnnotationMirror>)list);
        }
        return e.getAnnotationMirrors();
    }
    
    @Override
    public List<? extends Element> getAllMembers(final TypeElement type) {
        if (type == null || !(type instanceof TypeElementImpl)) {
            return Collections.emptyList();
        }
        ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)type)._binding;
        final Map<String, ReferenceBinding> types = new HashMap<String, ReferenceBinding>();
        final List<FieldBinding> fields = new ArrayList<FieldBinding>();
        final Map<String, Set<MethodBinding>> methods = new HashMap<String, Set<MethodBinding>>();
        final Set<ReferenceBinding> superinterfaces = new LinkedHashSet<ReferenceBinding>();
        boolean ignoreVisibility = true;
        while (binding != null) {
            this.addMembers(binding, ignoreVisibility, types, fields, methods);
            final Set<ReferenceBinding> newfound = new LinkedHashSet<ReferenceBinding>();
            this.collectSuperInterfaces(binding, superinterfaces, newfound);
            for (final ReferenceBinding superinterface : newfound) {
                this.addMembers(superinterface, false, types, fields, methods);
            }
            superinterfaces.addAll(newfound);
            binding = binding.superclass();
            ignoreVisibility = false;
        }
        final List<Element> allMembers = new ArrayList<Element>();
        for (final ReferenceBinding nestedType : types.values()) {
            allMembers.add(this._env.getFactory().newElement(nestedType));
        }
        for (final FieldBinding field : fields) {
            allMembers.add(this._env.getFactory().newElement(field));
        }
        for (final Set<MethodBinding> sameNamedMethods : methods.values()) {
            for (final MethodBinding method : sameNamedMethods) {
                allMembers.add(this._env.getFactory().newElement(method));
            }
        }
        return allMembers;
    }
    
    private void collectSuperInterfaces(final ReferenceBinding type, final Set<ReferenceBinding> existing, final Set<ReferenceBinding> newfound) {
        ReferenceBinding[] superInterfaces;
        for (int length = (superInterfaces = type.superInterfaces()).length, i = 0; i < length; ++i) {
            final ReferenceBinding superinterface = superInterfaces[i];
            if (!existing.contains(superinterface) && !newfound.contains(superinterface)) {
                newfound.add(superinterface);
                this.collectSuperInterfaces(superinterface, existing, newfound);
            }
        }
    }
    
    private void addMembers(final ReferenceBinding binding, final boolean ignoreVisibility, final Map<String, ReferenceBinding> types, final List<FieldBinding> fields, final Map<String, Set<MethodBinding>> methods) {
        ReferenceBinding[] memberTypes;
        for (int length = (memberTypes = binding.memberTypes()).length, i = 0; i < length; ++i) {
            final ReferenceBinding subtype = memberTypes[i];
            if (ignoreVisibility || !subtype.isPrivate()) {
                final String name = new String(subtype.sourceName());
                if (types.get(name) == null) {
                    types.put(name, subtype);
                }
            }
        }
        FieldBinding[] fields2;
        for (int length2 = (fields2 = binding.fields()).length, j = 0; j < length2; ++j) {
            final FieldBinding field = fields2[j];
            if (ignoreVisibility || !field.isPrivate()) {
                fields.add(field);
            }
        }
        MethodBinding[] methods2;
        for (int length3 = (methods2 = binding.methods()).length, k = 0; k < length3; ++k) {
            final MethodBinding method = methods2[k];
            if (!method.isSynthetic() && (ignoreVisibility || (!method.isPrivate() && !method.isConstructor()))) {
                final String methodName = new String(method.selector);
                Set<MethodBinding> sameNamedMethods = methods.get(methodName);
                if (sameNamedMethods == null) {
                    sameNamedMethods = new HashSet<MethodBinding>(4);
                    methods.put(methodName, sameNamedMethods);
                    sameNamedMethods.add(method);
                }
                else {
                    boolean unique = true;
                    if (!ignoreVisibility) {
                        for (final MethodBinding existing : sameNamedMethods) {
                            final MethodVerifier verifier = this._env.getLookupEnvironment().methodVerifier();
                            if (verifier.doesMethodOverride(existing, method)) {
                                unique = false;
                                break;
                            }
                        }
                    }
                    if (unique) {
                        sameNamedMethods.add(method);
                    }
                }
            }
        }
    }
    
    @Override
    public Name getBinaryName(final TypeElement type) {
        final TypeElementImpl typeElementImpl = (TypeElementImpl)type;
        final ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
        return new NameImpl(CharOperation.replaceOnCopy(referenceBinding.constantPoolName(), '/', '.'));
    }
    
    @Override
    public String getConstantExpression(final Object value) {
        if (!(value instanceof Integer) && !(value instanceof Byte) && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof Long) && !(value instanceof Short) && !(value instanceof Character) && !(value instanceof String) && !(value instanceof Boolean)) {
            throw new IllegalArgumentException("Not a valid wrapper type : " + value.getClass());
        }
        if (value instanceof Character) {
            final StringBuilder builder = new StringBuilder();
            builder.append('\'').append(value).append('\'');
            return String.valueOf(builder);
        }
        if (value instanceof String) {
            final StringBuilder builder = new StringBuilder();
            builder.append('\"').append(value).append('\"');
            return String.valueOf(builder);
        }
        if (value instanceof Float) {
            final StringBuilder builder = new StringBuilder();
            builder.append(value).append('f');
            return String.valueOf(builder);
        }
        if (value instanceof Long) {
            final StringBuilder builder = new StringBuilder();
            builder.append(value).append('L');
            return String.valueOf(builder);
        }
        if (value instanceof Short) {
            final StringBuilder builder = new StringBuilder();
            builder.append("(short)").append(value);
            return String.valueOf(builder);
        }
        if (value instanceof Byte) {
            final StringBuilder builder = new StringBuilder();
            builder.append("(byte)0x");
            final int intValue = (byte)value;
            final String hexString = Integer.toHexString(intValue & 0xFF);
            if (hexString.length() < 2) {
                builder.append('0');
            }
            builder.append(hexString);
            return String.valueOf(builder);
        }
        return String.valueOf(value);
    }
    
    @Override
    public String getDocComment(final Element e) {
        final char[] unparsed = this.getUnparsedDocComment(e);
        return formatJavadoc(unparsed);
    }
    
    private char[] getUnparsedDocComment(final Element e) {
        Javadoc javadoc = null;
        ReferenceContext referenceContext = null;
        switch (e.getKind()) {
            case ENUM:
            case CLASS:
            case ANNOTATION_TYPE:
            case INTERFACE: {
                final TypeElementImpl typeElementImpl = (TypeElementImpl)e;
                final ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
                if (referenceBinding instanceof SourceTypeBinding) {
                    final SourceTypeBinding sourceTypeBinding = (SourceTypeBinding)referenceBinding;
                    referenceContext = sourceTypeBinding.scope.referenceContext;
                    javadoc = ((TypeDeclaration)referenceContext).javadoc;
                    break;
                }
                break;
            }
            case PACKAGE: {
                final PackageElementImpl packageElementImpl = (PackageElementImpl)e;
                final PackageBinding packageBinding = (PackageBinding)packageElementImpl._binding;
                final char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
                final ReferenceBinding type = this._env.getLookupEnvironment().getType(compoundName);
                if (type != null && type.isValidBinding() && type instanceof SourceTypeBinding) {
                    final SourceTypeBinding sourceTypeBinding2 = (SourceTypeBinding)type;
                    referenceContext = sourceTypeBinding2.scope.referenceContext;
                    javadoc = ((TypeDeclaration)referenceContext).javadoc;
                    break;
                }
                break;
            }
            case METHOD:
            case CONSTRUCTOR: {
                final ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)e;
                final MethodBinding methodBinding = (MethodBinding)executableElementImpl._binding;
                final AbstractMethodDeclaration sourceMethod = methodBinding.sourceMethod();
                if (sourceMethod != null) {
                    javadoc = sourceMethod.javadoc;
                    referenceContext = sourceMethod;
                    break;
                }
                break;
            }
            case ENUM_CONSTANT:
            case FIELD: {
                final VariableElementImpl variableElementImpl = (VariableElementImpl)e;
                final FieldBinding fieldBinding = (FieldBinding)variableElementImpl._binding;
                final FieldDeclaration sourceField = fieldBinding.sourceField();
                if (sourceField == null) {
                    break;
                }
                javadoc = sourceField.javadoc;
                if (fieldBinding.declaringClass instanceof SourceTypeBinding) {
                    final SourceTypeBinding sourceTypeBinding3 = (SourceTypeBinding)fieldBinding.declaringClass;
                    referenceContext = sourceTypeBinding3.scope.referenceContext;
                    break;
                }
                break;
            }
            default: {
                return null;
            }
        }
        if (javadoc != null && referenceContext != null) {
            final char[] contents = referenceContext.compilationResult().getCompilationUnit().getContents();
            if (contents != null) {
                return CharOperation.subarray(contents, javadoc.sourceStart, javadoc.sourceEnd - 1);
            }
        }
        return null;
    }
    
    private static String formatJavadoc(final char[] unparsed) {
        if (unparsed == null || unparsed.length < 5) {
            return null;
        }
        final String[] lines = new String(unparsed).split("\n");
        final Matcher delimiterMatcher = ElementsImpl.INITIAL_DELIMITER.matcher(lines[0]);
        if (!delimiterMatcher.find()) {
            return null;
        }
        final int iOpener = delimiterMatcher.end();
        lines[0] = lines[0].substring(iOpener);
        if (lines.length == 1) {
            final StringBuilder sb = new StringBuilder();
            final char[] chars = lines[0].toCharArray();
            boolean startingWhitespaces = true;
            char[] array;
            for (int length = (array = chars).length, k = 0; k < length; ++k) {
                final char c = array[k];
                if (Character.isWhitespace(c)) {
                    if (!startingWhitespaces) {
                        sb.append(c);
                    }
                }
                else {
                    startingWhitespaces = false;
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        final int firstLine = (lines[0].trim().length() <= 0) ? 1 : 0;
        final int lastLine = (lines[lines.length - 1].trim().length() > 0) ? (lines.length - 1) : (lines.length - 2);
        final StringBuilder sb2 = new StringBuilder();
        if (lines[0].length() != 0 && firstLine == 1) {
            sb2.append('\n');
        }
        final boolean preserveLineSeparator = lines[0].length() == 0;
        for (int line = firstLine; line <= lastLine; ++line) {
            final char[] chars2 = lines[line].toCharArray();
            final int starsIndex = getStars(chars2);
            int leadingWhitespaces = 0;
            boolean recordLeadingWhitespaces = true;
            for (int i = 0, max = chars2.length; i < max; ++i) {
                final char c2 = chars2[i];
                switch (c2) {
                    case ' ': {
                        if (starsIndex == -1) {
                            if (recordLeadingWhitespaces) {
                                ++leadingWhitespaces;
                                break;
                            }
                            sb2.append(c2);
                            break;
                        }
                        else {
                            if (i >= starsIndex) {
                                sb2.append(c2);
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    default: {
                        recordLeadingWhitespaces = false;
                        if (leadingWhitespaces != 0) {
                            final int numberOfTabs = leadingWhitespaces / 8;
                            if (numberOfTabs != 0) {
                                for (int j = 0, max2 = numberOfTabs; j < max2; ++j) {
                                    sb2.append("        ");
                                }
                                if (leadingWhitespaces % 8 >= 1) {
                                    sb2.append(' ');
                                }
                            }
                            else if (line != 0) {
                                for (int j = 0, max2 = leadingWhitespaces; j < max2; ++j) {
                                    sb2.append(' ');
                                }
                            }
                            leadingWhitespaces = 0;
                            sb2.append(c2);
                            break;
                        }
                        if (c2 == '\t') {
                            if (i >= starsIndex) {
                                sb2.append(c2);
                                break;
                            }
                            break;
                        }
                        else {
                            if (c2 != '*' || i > starsIndex) {
                                sb2.append(c2);
                                break;
                            }
                            break;
                        }
                        break;
                    }
                }
            }
            final int end = lines.length - 1;
            if (line < end) {
                sb2.append('\n');
            }
            else if (preserveLineSeparator && line == end) {
                sb2.append('\n');
            }
        }
        return sb2.toString();
    }
    
    private static int getStars(final char[] line) {
        int i = 0;
        final int max = line.length;
        while (i < max) {
            final char c = line[i];
            if (!Character.isWhitespace(c)) {
                if (c == '*') {
                    for (int j = i + 1; j < max; ++j) {
                        if (line[j] != '*') {
                            return j;
                        }
                    }
                    return max - 1;
                }
                break;
            }
            else {
                ++i;
            }
        }
        return -1;
    }
    
    @Override
    public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(final AnnotationMirror a) {
        return ((AnnotationMirrorImpl)a).getElementValuesWithDefaults();
    }
    
    @Override
    public Name getName(final CharSequence cs) {
        return new NameImpl(cs);
    }
    
    @Override
    public PackageElement getPackageElement(final CharSequence name) {
        final LookupEnvironment le = this._env.getLookupEnvironment();
        if (name.length() == 0) {
            return new PackageElementImpl(this._env, le.defaultPackage);
        }
        final char[] packageName = name.toString().toCharArray();
        final PackageBinding packageBinding = le.createPackage(CharOperation.splitOn('.', packageName));
        if (packageBinding == null) {
            return null;
        }
        return new PackageElementImpl(this._env, packageBinding);
    }
    
    @Override
    public PackageElement getPackageOf(final Element type) {
        switch (type.getKind()) {
            case ENUM:
            case CLASS:
            case ANNOTATION_TYPE:
            case INTERFACE: {
                final TypeElementImpl typeElementImpl = (TypeElementImpl)type;
                final ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(referenceBinding.fPackage);
            }
            case PACKAGE: {
                return (PackageElement)type;
            }
            case METHOD:
            case CONSTRUCTOR: {
                final ExecutableElementImpl executableElementImpl = (ExecutableElementImpl)type;
                final MethodBinding methodBinding = (MethodBinding)executableElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(methodBinding.declaringClass.fPackage);
            }
            case ENUM_CONSTANT:
            case FIELD: {
                final VariableElementImpl variableElementImpl = (VariableElementImpl)type;
                final FieldBinding fieldBinding = (FieldBinding)variableElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(fieldBinding.declaringClass.fPackage);
            }
            case PARAMETER: {
                final VariableElementImpl variableElementImpl = (VariableElementImpl)type;
                final LocalVariableBinding localVariableBinding = (LocalVariableBinding)variableElementImpl._binding;
                return (PackageElement)this._env.getFactory().newElement(localVariableBinding.declaringScope.classScope().referenceContext.binding.fPackage);
            }
            case LOCAL_VARIABLE:
            case EXCEPTION_PARAMETER:
            case STATIC_INIT:
            case INSTANCE_INIT:
            case TYPE_PARAMETER:
            case OTHER: {
                return null;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public TypeElement getTypeElement(final CharSequence name) {
        final LookupEnvironment le = this._env.getLookupEnvironment();
        final char[][] compoundName = CharOperation.splitOn('.', name.toString().toCharArray());
        ReferenceBinding binding = le.getType(compoundName);
        if (binding == null) {
            ReferenceBinding topLevelBinding = null;
            int topLevelSegments = compoundName.length;
            while (--topLevelSegments > 0) {
                final char[][] topLevelName = new char[topLevelSegments][];
                for (int i = 0; i < topLevelSegments; ++i) {
                    topLevelName[i] = compoundName[i];
                }
                topLevelBinding = le.getType(topLevelName);
                if (topLevelBinding != null) {
                    break;
                }
            }
            if (topLevelBinding == null) {
                return null;
            }
            binding = topLevelBinding;
            for (int j = topLevelSegments; binding != null && j < compoundName.length; binding = binding.getMemberType(compoundName[j]), ++j) {}
        }
        if (binding == null) {
            return null;
        }
        return new TypeElementImpl(this._env, binding, null);
    }
    
    @Override
    public boolean hides(final Element hider, final Element hidden) {
        if (hidden == null) {
            throw new NullPointerException();
        }
        return ((ElementImpl)hider).hides(hidden);
    }
    
    @Override
    public boolean isDeprecated(final Element e) {
        return e instanceof ElementImpl && (((ElementImpl)e)._binding.getAnnotationTagBits() & 0x400000000000L) != 0x0L;
    }
    
    @Override
    public boolean overrides(final ExecutableElement overrider, final ExecutableElement overridden, final TypeElement type) {
        if (overridden == null || type == null) {
            throw new NullPointerException();
        }
        return ((ExecutableElementImpl)overrider).overrides(overridden, type);
    }
    
    @Override
    public void printElements(final Writer w, final Element... elements) {
        final String lineSeparator = System.getProperty("line.separator");
        for (final Element element : elements) {
            try {
                w.write(element.toString());
                w.write(lineSeparator);
            }
            catch (final IOException ex) {}
        }
        try {
            w.flush();
        }
        catch (final IOException ex2) {}
    }
    
    @Override
    public boolean isFunctionalInterface(final TypeElement type) {
        if (type != null && type.getKind() == ElementKind.INTERFACE) {
            final ReferenceBinding binding = (ReferenceBinding)((TypeElementImpl)type)._binding;
            if (binding instanceof SourceTypeBinding) {
                return binding.isFunctionalInterface(((SourceTypeBinding)binding).scope);
            }
        }
        return false;
    }
}
