package sun.reflect.generics.parser;

import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.ReturnType;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.BaseType;
import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.Wildcard;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import java.util.List;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.FieldTypeSignature;
import java.util.ArrayList;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.TypeSignature;
import sun.reflect.generics.tree.MethodTypeSignature;
import sun.reflect.generics.tree.ClassSignature;
import java.lang.reflect.GenericSignatureFormatError;

public class SignatureParser
{
    private char[] input;
    private int index;
    private static final char EOI = ':';
    private static final boolean DEBUG = false;
    
    private SignatureParser() {
        this.index = 0;
    }
    
    private char getNext() {
        assert this.index <= this.input.length;
        try {
            return this.input[this.index++];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return ':';
        }
    }
    
    private char current() {
        assert this.index <= this.input.length;
        try {
            return this.input[this.index];
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            return ':';
        }
    }
    
    private void advance() {
        assert this.index <= this.input.length;
        ++this.index;
    }
    
    private String remainder() {
        return new String(this.input, this.index, this.input.length - this.index);
    }
    
    private boolean matches(final char c, final char... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            if (c == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    private Error error(final String s) {
        return new GenericSignatureFormatError("Signature Parse error: " + s + "\n\tRemaining input: " + this.remainder());
    }
    
    private void progress(final int n) {
        if (this.index <= n) {
            throw this.error("Failure to make progress!");
        }
    }
    
    public static SignatureParser make() {
        return new SignatureParser();
    }
    
    public ClassSignature parseClassSig(final String s) {
        this.input = s.toCharArray();
        return this.parseClassSignature();
    }
    
    public MethodTypeSignature parseMethodSig(final String s) {
        this.input = s.toCharArray();
        return this.parseMethodTypeSignature();
    }
    
    public TypeSignature parseTypeSig(final String s) {
        this.input = s.toCharArray();
        return this.parseTypeSignature();
    }
    
    private ClassSignature parseClassSignature() {
        assert this.index == 0;
        return ClassSignature.make(this.parseZeroOrMoreFormalTypeParameters(), this.parseClassTypeSignature(), this.parseSuperInterfaces());
    }
    
    private FormalTypeParameter[] parseZeroOrMoreFormalTypeParameters() {
        if (this.current() == '<') {
            return this.parseFormalTypeParameters();
        }
        return new FormalTypeParameter[0];
    }
    
    private FormalTypeParameter[] parseFormalTypeParameters() {
        final ArrayList list = new ArrayList(3);
        assert this.current() == '<';
        if (this.current() != '<') {
            throw this.error("expected '<'");
        }
        this.advance();
        list.add(this.parseFormalTypeParameter());
        while (this.current() != '>') {
            final int index = this.index;
            list.add(this.parseFormalTypeParameter());
            this.progress(index);
        }
        this.advance();
        return (FormalTypeParameter[])list.toArray(new FormalTypeParameter[list.size()]);
    }
    
    private FormalTypeParameter parseFormalTypeParameter() {
        return FormalTypeParameter.make(this.parseIdentifier(), this.parseBounds());
    }
    
    private String parseIdentifier() {
        final StringBuilder sb = new StringBuilder();
        while (!Character.isWhitespace(this.current())) {
            final char current = this.current();
            switch (current) {
                case 46:
                case 47:
                case 58:
                case 59:
                case 60:
                case 62:
                case 91: {
                    return sb.toString();
                }
                default: {
                    sb.append(current);
                    this.advance();
                    continue;
                }
            }
        }
        return sb.toString();
    }
    
    private FieldTypeSignature parseFieldTypeSignature() {
        return this.parseFieldTypeSignature(true);
    }
    
    private FieldTypeSignature parseFieldTypeSignature(final boolean b) {
        switch (this.current()) {
            case 'L': {
                return this.parseClassTypeSignature();
            }
            case 'T': {
                return this.parseTypeVariableSignature();
            }
            case '[': {
                if (b) {
                    return this.parseArrayTypeSignature();
                }
                throw this.error("Array signature not allowed here.");
            }
            default: {
                throw this.error("Expected Field Type Signature");
            }
        }
    }
    
    private ClassTypeSignature parseClassTypeSignature() {
        assert this.current() == 'L';
        if (this.current() != 'L') {
            throw this.error("expected a class type");
        }
        this.advance();
        final ArrayList list = new ArrayList(5);
        list.add(this.parsePackageNameAndSimpleClassTypeSignature());
        this.parseClassTypeSignatureSuffix(list);
        if (this.current() != ';') {
            throw this.error("expected ';' got '" + this.current() + "'");
        }
        this.advance();
        return ClassTypeSignature.make(list);
    }
    
    private SimpleClassTypeSignature parsePackageNameAndSimpleClassTypeSignature() {
        String s = this.parseIdentifier();
        if (this.current() == '/') {
            final StringBuilder sb = new StringBuilder(s);
            while (this.current() == '/') {
                this.advance();
                sb.append(".");
                sb.append(this.parseIdentifier());
            }
            s = sb.toString();
        }
        switch (this.current()) {
            case ';': {
                return SimpleClassTypeSignature.make(s, false, new TypeArgument[0]);
            }
            case '<': {
                return SimpleClassTypeSignature.make(s, false, this.parseTypeArguments());
            }
            default: {
                throw this.error("expected '<' or ';' but got " + this.current());
            }
        }
    }
    
    private SimpleClassTypeSignature parseSimpleClassTypeSignature(final boolean b) {
        final String identifier = this.parseIdentifier();
        final char current = this.current();
        switch (current) {
            case 46:
            case 59: {
                return SimpleClassTypeSignature.make(identifier, b, new TypeArgument[0]);
            }
            case 60: {
                return SimpleClassTypeSignature.make(identifier, b, this.parseTypeArguments());
            }
            default: {
                throw this.error("expected '<' or ';' or '.', got '" + current + "'.");
            }
        }
    }
    
    private void parseClassTypeSignatureSuffix(final List<SimpleClassTypeSignature> list) {
        while (this.current() == '.') {
            this.advance();
            list.add(this.parseSimpleClassTypeSignature(true));
        }
    }
    
    private TypeArgument[] parseTypeArgumentsOpt() {
        if (this.current() == '<') {
            return this.parseTypeArguments();
        }
        return new TypeArgument[0];
    }
    
    private TypeArgument[] parseTypeArguments() {
        final ArrayList list = new ArrayList(3);
        assert this.current() == '<';
        if (this.current() != '<') {
            throw this.error("expected '<'");
        }
        this.advance();
        list.add(this.parseTypeArgument());
        while (this.current() != '>') {
            list.add(this.parseTypeArgument());
        }
        this.advance();
        return (TypeArgument[])list.toArray(new TypeArgument[list.size()]);
    }
    
    private TypeArgument parseTypeArgument() {
        final FieldTypeSignature[] array = { null };
        final FieldTypeSignature[] array2 = { null };
        final TypeArgument[] array3 = new TypeArgument[0];
        switch (this.current()) {
            case '+': {
                this.advance();
                array[0] = this.parseFieldTypeSignature();
                array2[0] = BottomSignature.make();
                return Wildcard.make(array, array2);
            }
            case '*': {
                this.advance();
                array[0] = SimpleClassTypeSignature.make("java.lang.Object", false, array3);
                array2[0] = BottomSignature.make();
                return Wildcard.make(array, array2);
            }
            case '-': {
                this.advance();
                array2[0] = this.parseFieldTypeSignature();
                array[0] = SimpleClassTypeSignature.make("java.lang.Object", false, array3);
                return Wildcard.make(array, array2);
            }
            default: {
                return this.parseFieldTypeSignature();
            }
        }
    }
    
    private TypeVariableSignature parseTypeVariableSignature() {
        assert this.current() == 'T';
        if (this.current() != 'T') {
            throw this.error("expected a type variable usage");
        }
        this.advance();
        final TypeVariableSignature make = TypeVariableSignature.make(this.parseIdentifier());
        if (this.current() != ';') {
            throw this.error("; expected in signature of type variable named" + make.getIdentifier());
        }
        this.advance();
        return make;
    }
    
    private ArrayTypeSignature parseArrayTypeSignature() {
        if (this.current() != '[') {
            throw this.error("expected array type signature");
        }
        this.advance();
        return ArrayTypeSignature.make(this.parseTypeSignature());
    }
    
    private TypeSignature parseTypeSignature() {
        switch (this.current()) {
            case 'B':
            case 'C':
            case 'D':
            case 'F':
            case 'I':
            case 'J':
            case 'S':
            case 'Z': {
                return this.parseBaseType();
            }
            default: {
                return this.parseFieldTypeSignature();
            }
        }
    }
    
    private BaseType parseBaseType() {
        switch (this.current()) {
            case 'B': {
                this.advance();
                return ByteSignature.make();
            }
            case 'C': {
                this.advance();
                return CharSignature.make();
            }
            case 'D': {
                this.advance();
                return DoubleSignature.make();
            }
            case 'F': {
                this.advance();
                return FloatSignature.make();
            }
            case 'I': {
                this.advance();
                return IntSignature.make();
            }
            case 'J': {
                this.advance();
                return LongSignature.make();
            }
            case 'S': {
                this.advance();
                return ShortSignature.make();
            }
            case 'Z': {
                this.advance();
                return BooleanSignature.make();
            }
            default: {
                assert false;
                throw this.error("expected primitive type");
            }
        }
    }
    
    private FieldTypeSignature[] parseBounds() {
        final ArrayList list = new ArrayList(3);
        if (this.current() == ':') {
            this.advance();
            switch (this.current()) {
                case ':': {
                    break;
                }
                default: {
                    list.add(this.parseFieldTypeSignature());
                    break;
                }
            }
            while (this.current() == ':') {
                this.advance();
                list.add(this.parseFieldTypeSignature());
            }
        }
        else {
            this.error("Bound expected");
        }
        return (FieldTypeSignature[])list.toArray(new FieldTypeSignature[list.size()]);
    }
    
    private ClassTypeSignature[] parseSuperInterfaces() {
        final ArrayList list = new ArrayList(5);
        while (this.current() == 'L') {
            list.add(this.parseClassTypeSignature());
        }
        return (ClassTypeSignature[])list.toArray(new ClassTypeSignature[list.size()]);
    }
    
    private MethodTypeSignature parseMethodTypeSignature() {
        assert this.index == 0;
        return MethodTypeSignature.make(this.parseZeroOrMoreFormalTypeParameters(), this.parseFormalParameters(), this.parseReturnType(), this.parseZeroOrMoreThrowsSignatures());
    }
    
    private TypeSignature[] parseFormalParameters() {
        if (this.current() != '(') {
            throw this.error("expected '('");
        }
        this.advance();
        final TypeSignature[] zeroOrMoreTypeSignatures = this.parseZeroOrMoreTypeSignatures();
        if (this.current() != ')') {
            throw this.error("expected ')'");
        }
        this.advance();
        return zeroOrMoreTypeSignatures;
    }
    
    private TypeSignature[] parseZeroOrMoreTypeSignatures() {
        final ArrayList list = new ArrayList();
        int i = 0;
        while (i == 0) {
            switch (this.current()) {
                case 'B':
                case 'C':
                case 'D':
                case 'F':
                case 'I':
                case 'J':
                case 'L':
                case 'S':
                case 'T':
                case 'Z':
                case '[': {
                    list.add(this.parseTypeSignature());
                    continue;
                }
                default: {
                    i = 1;
                    continue;
                }
            }
        }
        return (TypeSignature[])list.toArray(new TypeSignature[list.size()]);
    }
    
    private ReturnType parseReturnType() {
        if (this.current() == 'V') {
            this.advance();
            return VoidDescriptor.make();
        }
        return this.parseTypeSignature();
    }
    
    private FieldTypeSignature[] parseZeroOrMoreThrowsSignatures() {
        final ArrayList list = new ArrayList(3);
        while (this.current() == '^') {
            list.add(this.parseThrowsSignature());
        }
        return (FieldTypeSignature[])list.toArray(new FieldTypeSignature[list.size()]);
    }
    
    private FieldTypeSignature parseThrowsSignature() {
        assert this.current() == '^';
        if (this.current() != '^') {
            throw this.error("expected throws signature");
        }
        this.advance();
        return this.parseFieldTypeSignature(false);
    }
}
