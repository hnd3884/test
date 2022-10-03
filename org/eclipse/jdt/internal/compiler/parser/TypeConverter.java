package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public abstract class TypeConverter
{
    int namePos;
    protected ProblemReporter problemReporter;
    protected boolean has1_5Compliance;
    private char memberTypeSeparator;
    
    protected TypeConverter(final ProblemReporter problemReporter, final char memberTypeSeparator) {
        this.problemReporter = problemReporter;
        this.has1_5Compliance = (problemReporter.options.originalComplianceLevel >= 3211264L);
        this.memberTypeSeparator = memberTypeSeparator;
    }
    
    private void addIdentifiers(final String typeSignature, final int start, final int endExclusive, final int identCount, final ArrayList fragments) {
        if (identCount == 1) {
            final char[] identifier;
            typeSignature.getChars(start, endExclusive, identifier = new char[endExclusive - start], 0);
            fragments.add(identifier);
        }
        else {
            fragments.add(this.extractIdentifiers(typeSignature, start, endExclusive - 1, identCount));
        }
    }
    
    protected ImportReference createImportReference(final String[] importName, final int start, final int end, final boolean onDemand, final int modifiers) {
        final int length = importName.length;
        final long[] positions = new long[length];
        final long position = ((long)start << 32) + end;
        final char[][] qImportName = new char[length][];
        for (int i = 0; i < length; ++i) {
            qImportName[i] = importName[i].toCharArray();
            positions[i] = position;
        }
        return new ImportReference(qImportName, positions, onDemand, modifiers);
    }
    
    protected TypeParameter createTypeParameter(final char[] typeParameterName, final char[][] typeParameterBounds, final int start, final int end) {
        final TypeParameter parameter = new TypeParameter();
        parameter.name = typeParameterName;
        parameter.sourceStart = start;
        parameter.sourceEnd = end;
        if (typeParameterBounds != null) {
            final int length = typeParameterBounds.length;
            if (length > 0) {
                parameter.type = this.createTypeReference(typeParameterBounds[0], start, end);
                if (length > 1) {
                    parameter.bounds = new TypeReference[length - 1];
                    for (int i = 1; i < length; ++i) {
                        final TypeReference typeReference;
                        final TypeReference bound = typeReference = this.createTypeReference(typeParameterBounds[i], start, end);
                        typeReference.bits |= 0x10;
                        parameter.bounds[i - 1] = bound;
                    }
                }
            }
        }
        return parameter;
    }
    
    protected TypeReference createTypeReference(final char[] typeName, final int start, final int end, final boolean includeGenericsAnyway) {
        final int length = typeName.length;
        this.namePos = 0;
        return this.decodeType2(typeName, length, start, end, true);
    }
    
    protected TypeReference createTypeReference(final char[] typeName, final int start, final int end) {
        final int length = typeName.length;
        this.namePos = 0;
        return this.decodeType2(typeName, length, start, end, false);
    }
    
    protected TypeReference createTypeReference(final String typeSignature, final int start, final int end) {
        final int length = typeSignature.length();
        this.namePos = 0;
        return this.decodeType(typeSignature, length, start, end);
    }
    
    private TypeReference decodeType(final String typeSignature, final int length, final int start, final int end) {
        int identCount = 1;
        int dim = 0;
        int nameFragmentStart = this.namePos;
        int nameFragmentEnd = -1;
        boolean nameStarted = false;
        ArrayList fragments = null;
    Label_1127:
        while (this.namePos < length) {
            final char currentChar = typeSignature.charAt(this.namePos);
            switch (currentChar) {
                case 'Z': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, ((long)start << 32) + end);
                }
                case 'B': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.BYTE.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, ((long)start << 32) + end);
                }
                case 'C': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.CHAR.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, ((long)start << 32) + end);
                }
                case 'D': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, ((long)start << 32) + end);
                }
                case 'F': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.FLOAT.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, ((long)start << 32) + end);
                }
                case 'I': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.INT.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, ((long)start << 32) + end);
                }
                case 'J': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.LONG.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, ((long)start << 32) + end);
                }
                case 'S': {
                    if (nameStarted) {
                        break;
                    }
                    ++this.namePos;
                    if (dim == 0) {
                        return new SingleTypeReference(TypeBinding.SHORT.simpleName, ((long)start << 32) + end);
                    }
                    return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, ((long)start << 32) + end);
                }
                case 'V': {
                    if (!nameStarted) {
                        ++this.namePos;
                        return new SingleTypeReference(TypeBinding.VOID.simpleName, ((long)start << 32) + end);
                    }
                    break;
                }
                case 'L':
                case 'Q':
                case 'T': {
                    if (!nameStarted) {
                        nameFragmentStart = this.namePos + 1;
                        nameStarted = true;
                        break;
                    }
                    break;
                }
                case '*': {
                    ++this.namePos;
                    final Wildcard result = new Wildcard(0);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '+': {
                    ++this.namePos;
                    final Wildcard result = new Wildcard(1);
                    result.bound = this.decodeType(typeSignature, length, start, end);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '-': {
                    ++this.namePos;
                    final Wildcard result = new Wildcard(2);
                    result.bound = this.decodeType(typeSignature, length, start, end);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '[': {
                    ++dim;
                    break;
                }
                case ';':
                case '>': {
                    nameFragmentEnd = this.namePos - 1;
                    ++this.namePos;
                    break Label_1127;
                }
                case '$': {
                    if (this.memberTypeSeparator != '$') {
                        break;
                    }
                }
                case '.': {
                    if (!nameStarted) {
                        nameFragmentStart = this.namePos + 1;
                        nameStarted = true;
                        break;
                    }
                    if (this.namePos > nameFragmentStart) {
                        ++identCount;
                        break;
                    }
                    break;
                }
                case '<': {
                    nameFragmentEnd = this.namePos - 1;
                    if (!this.has1_5Compliance) {
                        break Label_1127;
                    }
                    if (fragments == null) {
                        fragments = new ArrayList(2);
                    }
                    this.addIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd + 1, identCount, fragments);
                    ++this.namePos;
                    final TypeReference[] arguments = this.decodeTypeArguments(typeSignature, length, start, end);
                    fragments.add(arguments);
                    identCount = 1;
                    nameStarted = false;
                    break;
                }
            }
            ++this.namePos;
        }
        if (fragments != null) {
            if (nameStarted) {
                this.addIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd + 1, identCount, fragments);
            }
            final int fragmentLength = fragments.size();
            if (fragmentLength == 2) {
                final Object firstFragment = fragments.get(0);
                if (firstFragment instanceof char[]) {
                    return new ParameterizedSingleTypeReference((char[])firstFragment, fragments.get(1), dim, ((long)start << 32) + end);
                }
            }
            identCount = 0;
            for (int i = 0; i < fragmentLength; ++i) {
                final Object element = fragments.get(i);
                if (element instanceof char[][]) {
                    identCount += ((char[][])element).length;
                }
                else if (element instanceof char[]) {
                    ++identCount;
                }
            }
            final char[][] tokens = new char[identCount][];
            final TypeReference[][] arguments2 = new TypeReference[identCount][];
            int index = 0;
            for (int j = 0; j < fragmentLength; ++j) {
                final Object element2 = fragments.get(j);
                if (element2 instanceof char[][]) {
                    final char[][] fragmentTokens = (char[][])element2;
                    final int fragmentTokenLength = fragmentTokens.length;
                    System.arraycopy(fragmentTokens, 0, tokens, index, fragmentTokenLength);
                    index += fragmentTokenLength;
                }
                else if (element2 instanceof char[]) {
                    tokens[index++] = (char[])element2;
                }
                else {
                    arguments2[index - 1] = (TypeReference[])element2;
                }
            }
            final long[] positions = new long[identCount];
            final long pos = ((long)start << 32) + end;
            for (int k = 0; k < identCount; ++k) {
                positions[k] = pos;
            }
            return new ParameterizedQualifiedTypeReference(tokens, arguments2, dim, positions);
        }
        if (identCount == 1) {
            if (dim == 0) {
                final char[] nameFragment = new char[nameFragmentEnd - nameFragmentStart + 1];
                typeSignature.getChars(nameFragmentStart, nameFragmentEnd + 1, nameFragment, 0);
                return new SingleTypeReference(nameFragment, ((long)start << 32) + end);
            }
            final char[] nameFragment = new char[nameFragmentEnd - nameFragmentStart + 1];
            typeSignature.getChars(nameFragmentStart, nameFragmentEnd + 1, nameFragment, 0);
            return new ArrayTypeReference(nameFragment, dim, ((long)start << 32) + end);
        }
        else {
            final long[] positions2 = new long[identCount];
            final long pos2 = ((long)start << 32) + end;
            for (int l = 0; l < identCount; ++l) {
                positions2[l] = pos2;
            }
            final char[][] identifiers = this.extractIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd, identCount);
            if (dim == 0) {
                return new QualifiedTypeReference(identifiers, positions2);
            }
            return new ArrayQualifiedTypeReference(identifiers, dim, positions2);
        }
    }
    
    private TypeReference decodeType2(final char[] typeName, final int length, final int start, final int end, final boolean includeGenericsAnyway) {
        int identCount = 1;
        int dim = 0;
        int nameFragmentStart = this.namePos;
        int nameFragmentEnd = -1;
        ArrayList fragments = null;
    Label_0578:
        while (this.namePos < length) {
            final char currentChar = typeName[this.namePos];
            switch (currentChar) {
                case '?': {
                    ++this.namePos;
                    while (typeName[this.namePos] == ' ') {
                        ++this.namePos;
                    }
                    Label_0364: {
                        switch (typeName[this.namePos]) {
                            case 's': {
                                final int max = TypeConstants.WILDCARD_SUPER.length - 1;
                                for (int ahead = 1; ahead < max; ++ahead) {
                                    if (typeName[this.namePos + ahead] != TypeConstants.WILDCARD_SUPER[ahead + 1]) {
                                        break Label_0364;
                                    }
                                }
                                this.namePos += max;
                                final Wildcard result = new Wildcard(2);
                                result.bound = this.decodeType2(typeName, length, start, end, includeGenericsAnyway);
                                result.sourceStart = start;
                                result.sourceEnd = end;
                                return result;
                            }
                            case 'e': {
                                final int max = TypeConstants.WILDCARD_EXTENDS.length - 1;
                                for (int ahead = 1; ahead < max; ++ahead) {
                                    if (typeName[this.namePos + ahead] != TypeConstants.WILDCARD_EXTENDS[ahead + 1]) {
                                        break Label_0364;
                                    }
                                }
                                this.namePos += max;
                                final Wildcard result = new Wildcard(1);
                                result.bound = this.decodeType2(typeName, length, start, end, includeGenericsAnyway);
                                result.sourceStart = start;
                                result.sourceEnd = end;
                                return result;
                            }
                        }
                    }
                    final Wildcard result2 = new Wildcard(0);
                    result2.sourceStart = start;
                    result2.sourceEnd = end;
                    return result2;
                }
                case '[': {
                    if (dim == 0 && nameFragmentEnd < 0) {
                        nameFragmentEnd = this.namePos - 1;
                    }
                    ++dim;
                }
                case ',':
                case '>': {
                    break Label_0578;
                }
                case '.': {
                    if (nameFragmentStart < 0) {
                        nameFragmentStart = this.namePos + 1;
                    }
                    ++identCount;
                    break;
                }
                case '<': {
                    if ((this.has1_5Compliance || includeGenericsAnyway) && fragments == null) {
                        fragments = new ArrayList(2);
                    }
                    nameFragmentEnd = this.namePos - 1;
                    if (this.has1_5Compliance || includeGenericsAnyway) {
                        final char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, this.namePos);
                        fragments.add(identifiers);
                    }
                    ++this.namePos;
                    final TypeReference[] arguments = this.decodeTypeArguments(typeName, length, start, end, includeGenericsAnyway);
                    if (this.has1_5Compliance || includeGenericsAnyway) {
                        fragments.add(arguments);
                        identCount = 0;
                        nameFragmentStart = -1;
                        nameFragmentEnd = -1;
                        break;
                    }
                    break;
                }
            }
            ++this.namePos;
        }
        return this.decodeType3(typeName, length, start, end, identCount, dim, nameFragmentStart, nameFragmentEnd, fragments);
    }
    
    private TypeReference decodeType3(final char[] typeName, final int length, final int start, final int end, int identCount, final int dim, final int nameFragmentStart, int nameFragmentEnd, final ArrayList fragments) {
        if (nameFragmentEnd < 0) {
            nameFragmentEnd = this.namePos - 1;
        }
        if (fragments != null) {
            if (nameFragmentStart > 0 && nameFragmentStart < length) {
                final char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, nameFragmentEnd + 1);
                fragments.add(identifiers);
            }
            final int fragmentLength = fragments.size();
            if (fragmentLength == 2) {
                final char[][] firstFragment = fragments.get(0);
                if (firstFragment.length == 1) {
                    return new ParameterizedSingleTypeReference(firstFragment[0], fragments.get(1), dim, ((long)start << 32) + end);
                }
            }
            identCount = 0;
            for (int i = 0; i < fragmentLength; ++i) {
                final Object element = fragments.get(i);
                if (element instanceof char[][]) {
                    identCount += ((char[][])element).length;
                }
            }
            final char[][] tokens = new char[identCount][];
            final TypeReference[][] arguments = new TypeReference[identCount][];
            int index = 0;
            for (int j = 0; j < fragmentLength; ++j) {
                final Object element2 = fragments.get(j);
                if (element2 instanceof char[][]) {
                    final char[][] fragmentTokens = (char[][])element2;
                    final int fragmentTokenLength = fragmentTokens.length;
                    System.arraycopy(fragmentTokens, 0, tokens, index, fragmentTokenLength);
                    index += fragmentTokenLength;
                }
                else {
                    arguments[index - 1] = (TypeReference[])element2;
                }
            }
            final long[] positions = new long[identCount];
            final long pos = ((long)start << 32) + end;
            for (int k = 0; k < identCount; ++k) {
                positions[k] = pos;
            }
            return new ParameterizedQualifiedTypeReference(tokens, arguments, dim, positions);
        }
        if (identCount == 1) {
            if (dim == 0) {
                char[] nameFragment;
                if (nameFragmentStart != 0 || nameFragmentEnd >= 0) {
                    final int nameFragmentLength = nameFragmentEnd - nameFragmentStart + 1;
                    System.arraycopy(typeName, nameFragmentStart, nameFragment = new char[nameFragmentLength], 0, nameFragmentLength);
                }
                else {
                    nameFragment = typeName;
                }
                return new SingleTypeReference(nameFragment, ((long)start << 32) + end);
            }
            final int nameFragmentLength2 = nameFragmentEnd - nameFragmentStart + 1;
            final char[] nameFragment2 = new char[nameFragmentLength2];
            System.arraycopy(typeName, nameFragmentStart, nameFragment2, 0, nameFragmentLength2);
            return new ArrayTypeReference(nameFragment2, dim, ((long)start << 32) + end);
        }
        else {
            final long[] positions2 = new long[identCount];
            final long pos2 = ((long)start << 32) + end;
            for (int l = 0; l < identCount; ++l) {
                positions2[l] = pos2;
            }
            final char[][] identifiers2 = CharOperation.splitOn('.', typeName, nameFragmentStart, nameFragmentEnd + 1);
            if (dim == 0) {
                return new QualifiedTypeReference(identifiers2, positions2);
            }
            return new ArrayQualifiedTypeReference(identifiers2, dim, positions2);
        }
    }
    
    private TypeReference[] decodeTypeArguments(final char[] typeName, final int length, final int start, final int end, final boolean includeGenericsAnyway) {
        final ArrayList argumentList = new ArrayList(1);
        int count = 0;
        while (this.namePos < length) {
            final TypeReference argument = this.decodeType2(typeName, length, start, end, includeGenericsAnyway);
            ++count;
            argumentList.add(argument);
            if (this.namePos >= length) {
                break;
            }
            if (typeName[this.namePos] == '>') {
                break;
            }
            ++this.namePos;
        }
        final TypeReference[] typeArguments = new TypeReference[count];
        argumentList.toArray(typeArguments);
        return typeArguments;
    }
    
    private TypeReference[] decodeTypeArguments(final String typeSignature, final int length, final int start, final int end) {
        final ArrayList argumentList = new ArrayList(1);
        int count = 0;
        while (this.namePos < length) {
            final TypeReference argument = this.decodeType(typeSignature, length, start, end);
            ++count;
            argumentList.add(argument);
            if (this.namePos >= length) {
                break;
            }
            if (typeSignature.charAt(this.namePos) == '>') {
                break;
            }
        }
        final TypeReference[] typeArguments = new TypeReference[count];
        argumentList.toArray(typeArguments);
        return typeArguments;
    }
    
    private char[][] extractIdentifiers(final String typeSignature, int start, final int endInclusive, final int identCount) {
        final char[][] result = new char[identCount][];
        int charIndex = start;
        int i = 0;
        while (charIndex < endInclusive) {
            final char currentChar;
            if ((currentChar = typeSignature.charAt(charIndex)) == this.memberTypeSeparator || currentChar == '.') {
                typeSignature.getChars(start, charIndex, result[i++] = new char[charIndex - start], 0);
                start = ++charIndex;
            }
            else {
                ++charIndex;
            }
        }
        typeSignature.getChars(start, charIndex + 1, result[i++] = new char[charIndex - start + 1], 0);
        return result;
    }
}
