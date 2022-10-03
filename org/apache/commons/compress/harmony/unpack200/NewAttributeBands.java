package org.apache.commons.compress.harmony.unpack200;

import java.util.Collections;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInterfaceMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPMethodRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFieldRef;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPNameAndType;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPUTF8;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPClass;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPString;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPDouble;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPFloat;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPLong;
import org.apache.commons.compress.harmony.unpack200.bytecode.CPInteger;
import org.apache.commons.compress.harmony.pack200.Codec;
import org.apache.commons.compress.harmony.pack200.BHSDCodec;
import java.util.Iterator;
import java.io.StringReader;
import org.apache.commons.compress.harmony.unpack200.bytecode.NewAttribute;
import org.apache.commons.compress.harmony.unpack200.bytecode.Attribute;
import java.util.ArrayList;
import org.apache.commons.compress.harmony.pack200.Pack200Exception;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public class NewAttributeBands extends BandSet
{
    private final AttributeLayout attributeLayout;
    private int backwardsCallCount;
    protected List attributeLayoutElements;
    
    public NewAttributeBands(final Segment segment, final AttributeLayout attributeLayout) throws IOException {
        super(segment);
        this.attributeLayout = attributeLayout;
        this.parseLayout();
        attributeLayout.setBackwardsCallCount(this.backwardsCallCount);
    }
    
    @Override
    public void read(final InputStream in) throws IOException, Pack200Exception {
    }
    
    public List parseAttributes(final InputStream in, final int occurrenceCount) throws IOException, Pack200Exception {
        for (int i = 0; i < this.attributeLayoutElements.size(); ++i) {
            final AttributeLayoutElement element = this.attributeLayoutElements.get(i);
            element.readBands(in, occurrenceCount);
        }
        final List attributes = new ArrayList(occurrenceCount);
        for (int j = 0; j < occurrenceCount; ++j) {
            attributes.add(this.getOneAttribute(j, this.attributeLayoutElements));
        }
        return attributes;
    }
    
    private Attribute getOneAttribute(final int index, final List elements) {
        final NewAttribute attribute = new NewAttribute(this.segment.getCpBands().cpUTF8Value(this.attributeLayout.getName()), this.attributeLayout.getIndex());
        for (int i = 0; i < elements.size(); ++i) {
            final AttributeLayoutElement element = elements.get(i);
            element.addToAttribute(index, attribute);
        }
        return attribute;
    }
    
    private void parseLayout() throws IOException {
        if (this.attributeLayoutElements == null) {
            this.attributeLayoutElements = new ArrayList();
            final StringReader stream = new StringReader(this.attributeLayout.getLayout());
            AttributeLayoutElement e;
            while ((e = this.readNextAttributeElement(stream)) != null) {
                this.attributeLayoutElements.add(e);
            }
            this.resolveCalls();
        }
    }
    
    private void resolveCalls() {
        int backwardsCalls = 0;
        for (int i = 0; i < this.attributeLayoutElements.size(); ++i) {
            final AttributeLayoutElement element = this.attributeLayoutElements.get(i);
            if (element instanceof Callable) {
                final Callable callable = (Callable)element;
                if (i == 0) {
                    callable.setFirstCallable(true);
                }
                final List body = callable.body;
                for (int iIndex = 0; iIndex < body.size(); ++iIndex) {
                    final LayoutElement layoutElement = body.get(iIndex);
                    backwardsCalls += this.resolveCallsForElement(i, callable, layoutElement);
                }
            }
        }
        this.backwardsCallCount = backwardsCalls;
    }
    
    private int resolveCallsForElement(final int i, final Callable currentCallable, final LayoutElement layoutElement) {
        int backwardsCalls = 0;
        if (layoutElement instanceof Call) {
            final Call call = (Call)layoutElement;
            int index = call.callableIndex;
            if (index == 0) {
                ++backwardsCalls;
                call.setCallable(currentCallable);
            }
            else if (index > 0) {
                for (int k = i + 1; k < this.attributeLayoutElements.size(); ++k) {
                    final AttributeLayoutElement el = this.attributeLayoutElements.get(k);
                    if (el instanceof Callable && --index == 0) {
                        call.setCallable((Callable)el);
                        break;
                    }
                }
            }
            else {
                ++backwardsCalls;
                for (int k = i - 1; k >= 0; --k) {
                    final AttributeLayoutElement el = this.attributeLayoutElements.get(k);
                    if (el instanceof Callable && ++index == 0) {
                        call.setCallable((Callable)el);
                        break;
                    }
                }
            }
        }
        else if (layoutElement instanceof Replication) {
            final List children = ((Replication)layoutElement).layoutElements;
            for (final LayoutElement object : children) {
                backwardsCalls += this.resolveCallsForElement(i, currentCallable, object);
            }
        }
        return backwardsCalls;
    }
    
    private AttributeLayoutElement readNextAttributeElement(final StringReader stream) throws IOException {
        stream.mark(1);
        final int nextChar = stream.read();
        if (nextChar == -1) {
            return null;
        }
        if (nextChar == 91) {
            final List body = this.readBody(this.getStreamUpToMatchingBracket(stream));
            return new Callable(body);
        }
        stream.reset();
        return this.readNextLayoutElement(stream);
    }
    
    private LayoutElement readNextLayoutElement(final StringReader stream) throws IOException {
        final int nextChar = stream.read();
        if (nextChar == -1) {
            return null;
        }
        switch (nextChar) {
            case 66:
            case 72:
            case 73:
            case 86: {
                return new Integral(new String(new char[] { (char)nextChar }));
            }
            case 70:
            case 83: {
                return new Integral(new String(new char[] { (char)nextChar, (char)stream.read() }));
            }
            case 80: {
                stream.mark(1);
                if (stream.read() != 79) {
                    stream.reset();
                    return new Integral("P" + (char)stream.read());
                }
                return new Integral("PO" + (char)stream.read());
            }
            case 79: {
                stream.mark(1);
                if (stream.read() != 83) {
                    stream.reset();
                    return new Integral("O" + (char)stream.read());
                }
                return new Integral("OS" + (char)stream.read());
            }
            case 78: {
                final char uint_type = (char)stream.read();
                stream.read();
                final String str = this.readUpToMatchingBracket(stream);
                return new Replication("" + uint_type, str);
            }
            case 84: {
                String int_type = "" + (char)stream.read();
                if (int_type.equals("S")) {
                    int_type += (char)stream.read();
                }
                final List unionCases = new ArrayList();
                UnionCase c;
                while ((c = this.readNextUnionCase(stream)) != null) {
                    unionCases.add(c);
                }
                stream.read();
                stream.read();
                stream.read();
                List body = null;
                stream.mark(1);
                final char next = (char)stream.read();
                if (next != ']') {
                    stream.reset();
                    body = this.readBody(this.getStreamUpToMatchingBracket(stream));
                }
                return new Union(int_type, unionCases, body);
            }
            case 40: {
                final int number = this.readNumber(stream);
                stream.read();
                return new Call(number);
            }
            case 75:
            case 82: {
                final StringBuilder string = new StringBuilder("").append((char)nextChar).append((char)stream.read());
                final char nxt = (char)stream.read();
                string.append(nxt);
                if (nxt == 'N') {
                    string.append((char)stream.read());
                }
                return new Reference(string.toString());
            }
            default: {
                return null;
            }
        }
    }
    
    private UnionCase readNextUnionCase(final StringReader stream) throws IOException {
        stream.mark(2);
        stream.read();
        char next = (char)stream.read();
        if (next == ')') {
            stream.reset();
            return null;
        }
        stream.reset();
        stream.read();
        final List tags = new ArrayList();
        Integer nextTag;
        do {
            nextTag = this.readNumber(stream);
            if (nextTag != null) {
                tags.add(nextTag);
                stream.read();
            }
        } while (nextTag != null);
        stream.read();
        stream.mark(1);
        next = (char)stream.read();
        if (next == ']') {
            return new UnionCase(tags);
        }
        stream.reset();
        return new UnionCase(tags, this.readBody(this.getStreamUpToMatchingBracket(stream)));
    }
    
    private StringReader getStreamUpToMatchingBracket(final StringReader stream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int foundBracket = -1;
        while (foundBracket != 0) {
            final char c = (char)stream.read();
            if (c == ']') {
                ++foundBracket;
            }
            if (c == '[') {
                --foundBracket;
            }
            if (foundBracket != 0) {
                sb.append(c);
            }
        }
        return new StringReader(sb.toString());
    }
    
    public BHSDCodec getCodec(final String layoutElement) {
        if (layoutElement.indexOf(79) >= 0) {
            return Codec.BRANCH5;
        }
        if (layoutElement.indexOf(80) >= 0) {
            return Codec.BCI5;
        }
        if (layoutElement.indexOf(83) >= 0 && layoutElement.indexOf("KS") < 0 && layoutElement.indexOf("RS") < 0) {
            return Codec.SIGNED5;
        }
        if (layoutElement.indexOf(66) >= 0) {
            return Codec.BYTE1;
        }
        return Codec.UNSIGNED5;
    }
    
    private String readUpToMatchingBracket(final StringReader stream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        int foundBracket = -1;
        while (foundBracket != 0) {
            final char c = (char)stream.read();
            if (c == ']') {
                ++foundBracket;
            }
            if (c == '[') {
                --foundBracket;
            }
            if (foundBracket != 0) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    private Integer readNumber(final StringReader stream) throws IOException {
        stream.mark(1);
        final char first = (char)stream.read();
        final boolean negative = first == '-';
        if (!negative) {
            stream.reset();
        }
        stream.mark(100);
        int length = 0;
        int i;
        while ((i = stream.read()) != -1 && Character.isDigit((char)i)) {
            ++length;
        }
        stream.reset();
        if (length == 0) {
            return null;
        }
        final char[] digits = new char[length];
        final int read = stream.read(digits);
        if (read != digits.length) {
            throw new IOException("Error reading from the input stream");
        }
        return Integer.parseInt((negative ? "-" : "") + new String(digits));
    }
    
    private List readBody(final StringReader stream) throws IOException {
        final List layoutElements = new ArrayList();
        LayoutElement e;
        while ((e = this.readNextLayoutElement(stream)) != null) {
            layoutElements.add(e);
        }
        return layoutElements;
    }
    
    public int getBackwardsCallCount() {
        return this.backwardsCallCount;
    }
    
    public void setBackwardsCalls(final int[] backwardsCalls) throws IOException {
        int index = 0;
        this.parseLayout();
        for (int i = 0; i < this.attributeLayoutElements.size(); ++i) {
            final AttributeLayoutElement element = this.attributeLayoutElements.get(i);
            if (element instanceof Callable && ((Callable)element).isBackwardsCallable()) {
                ((Callable)element).addCount(backwardsCalls[index]);
                ++index;
            }
        }
    }
    
    @Override
    public void unpack() throws IOException, Pack200Exception {
    }
    
    private abstract class LayoutElement implements AttributeLayoutElement
    {
        protected int getLength(final char uint_type) {
            int length = 0;
            switch (uint_type) {
                case 'B': {
                    length = 1;
                    break;
                }
                case 'H': {
                    length = 2;
                    break;
                }
                case 'I': {
                    length = 4;
                    break;
                }
                case 'V': {
                    length = 0;
                    break;
                }
            }
            return length;
        }
    }
    
    public class Integral extends LayoutElement
    {
        private final String tag;
        private int[] band;
        
        public Integral(final String tag) {
            this.tag = tag;
        }
        
        @Override
        public void readBands(final InputStream in, final int count) throws IOException, Pack200Exception {
            this.band = NewAttributeBands.this.decodeBandInt(NewAttributeBands.this.attributeLayout.getName() + "_" + this.tag, in, NewAttributeBands.this.getCodec(this.tag), count);
        }
        
        @Override
        public void addToAttribute(final int n, final NewAttribute attribute) {
            long value = this.band[n];
            if (this.tag.equals("B") || this.tag.equals("FB")) {
                attribute.addInteger(1, value);
            }
            else if (this.tag.equals("SB")) {
                attribute.addInteger(1, (byte)value);
            }
            else if (this.tag.equals("H") || this.tag.equals("FH")) {
                attribute.addInteger(2, value);
            }
            else if (this.tag.equals("SH")) {
                attribute.addInteger(2, (short)value);
            }
            else if (this.tag.equals("I") || this.tag.equals("FI")) {
                attribute.addInteger(4, value);
            }
            else if (this.tag.equals("SI")) {
                attribute.addInteger(4, (int)value);
            }
            else if (!this.tag.equals("V") && !this.tag.equals("FV")) {
                if (!this.tag.equals("SV")) {
                    if (this.tag.startsWith("PO")) {
                        final char uint_type = this.tag.substring(2).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        attribute.addBCOffset(length, (int)value);
                    }
                    else if (this.tag.startsWith("P")) {
                        final char uint_type = this.tag.substring(1).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        attribute.addBCIndex(length, (int)value);
                    }
                    else if (this.tag.startsWith("OS")) {
                        final char uint_type = this.tag.substring(2).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        if (length == 1) {
                            value = (byte)value;
                        }
                        else if (length == 2) {
                            value = (short)value;
                        }
                        else if (length == 4) {
                            value = (int)value;
                        }
                        attribute.addBCLength(length, (int)value);
                    }
                    else if (this.tag.startsWith("O")) {
                        final char uint_type = this.tag.substring(1).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        attribute.addBCLength(length, (int)value);
                    }
                }
            }
        }
        
        long getValue(final int index) {
            return this.band[index];
        }
        
        public String getTag() {
            return this.tag;
        }
    }
    
    public class Replication extends LayoutElement
    {
        private final Integral countElement;
        private final List layoutElements;
        
        public Replication(final String tag, final String contents) throws IOException {
            this.layoutElements = new ArrayList();
            this.countElement = new Integral(tag);
            final StringReader stream = new StringReader(contents);
            LayoutElement e;
            while ((e = NewAttributeBands.this.readNextLayoutElement(stream)) != null) {
                this.layoutElements.add(e);
            }
        }
        
        @Override
        public void readBands(final InputStream in, final int count) throws IOException, Pack200Exception {
            this.countElement.readBands(in, count);
            int arrayCount = 0;
            for (int i = 0; i < count; ++i) {
                arrayCount += (int)this.countElement.getValue(i);
            }
            for (int i = 0; i < this.layoutElements.size(); ++i) {
                final LayoutElement element = this.layoutElements.get(i);
                element.readBands(in, arrayCount);
            }
        }
        
        @Override
        public void addToAttribute(final int index, final NewAttribute attribute) {
            this.countElement.addToAttribute(index, attribute);
            int offset = 0;
            for (int i = 0; i < index; ++i) {
                offset += (int)this.countElement.getValue(i);
            }
            final long numElements = this.countElement.getValue(index);
            for (int j = offset; j < offset + numElements; ++j) {
                for (int it = 0; it < this.layoutElements.size(); ++it) {
                    final LayoutElement element = this.layoutElements.get(it);
                    element.addToAttribute(j, attribute);
                }
            }
        }
        
        public Integral getCountElement() {
            return this.countElement;
        }
        
        public List getLayoutElements() {
            return this.layoutElements;
        }
    }
    
    public class Union extends LayoutElement
    {
        private final Integral unionTag;
        private final List unionCases;
        private final List defaultCaseBody;
        private int[] caseCounts;
        private int defaultCount;
        
        public Union(final String tag, final List unionCases, final List body) {
            this.unionTag = new Integral(tag);
            this.unionCases = unionCases;
            this.defaultCaseBody = body;
        }
        
        @Override
        public void readBands(final InputStream in, final int count) throws IOException, Pack200Exception {
            this.unionTag.readBands(in, count);
            final int[] values = this.unionTag.band;
            this.caseCounts = new int[this.unionCases.size()];
            for (int i = 0; i < this.caseCounts.length; ++i) {
                final UnionCase unionCase = this.unionCases.get(i);
                for (int j = 0; j < values.length; ++j) {
                    if (unionCase.hasTag(values[j])) {
                        final int[] caseCounts = this.caseCounts;
                        final int n = i;
                        ++caseCounts[n];
                    }
                }
                unionCase.readBands(in, this.caseCounts[i]);
            }
            for (int i = 0; i < values.length; ++i) {
                boolean found = false;
                for (int it = 0; it < this.unionCases.size(); ++it) {
                    final UnionCase unionCase2 = this.unionCases.get(it);
                    if (unionCase2.hasTag(values[i])) {
                        found = true;
                    }
                }
                if (!found) {
                    ++this.defaultCount;
                }
            }
            if (this.defaultCaseBody != null) {
                for (int i = 0; i < this.defaultCaseBody.size(); ++i) {
                    final LayoutElement element = this.defaultCaseBody.get(i);
                    element.readBands(in, this.defaultCount);
                }
            }
        }
        
        @Override
        public void addToAttribute(final int n, final NewAttribute attribute) {
            this.unionTag.addToAttribute(n, attribute);
            int offset = 0;
            final int[] tagBand = this.unionTag.band;
            final long tag = this.unionTag.getValue(n);
            boolean defaultCase = true;
            for (int i = 0; i < this.unionCases.size(); ++i) {
                final UnionCase element = this.unionCases.get(i);
                if (element.hasTag(tag)) {
                    defaultCase = false;
                    for (int j = 0; j < n; ++j) {
                        if (element.hasTag(tagBand[j])) {
                            ++offset;
                        }
                    }
                    element.addToAttribute(offset, attribute);
                }
            }
            if (defaultCase) {
                int defaultOffset = 0;
                for (int k = 0; k < n; ++k) {
                    boolean found = false;
                    for (int l = 0; l < this.unionCases.size(); ++l) {
                        final UnionCase element2 = this.unionCases.get(l);
                        if (element2.hasTag(tagBand[k])) {
                            found = true;
                        }
                    }
                    if (!found) {
                        ++defaultOffset;
                    }
                }
                if (this.defaultCaseBody != null) {
                    for (int m = 0; m < this.defaultCaseBody.size(); ++m) {
                        final LayoutElement element3 = this.defaultCaseBody.get(m);
                        element3.addToAttribute(defaultOffset, attribute);
                    }
                }
            }
        }
        
        public Integral getUnionTag() {
            return this.unionTag;
        }
        
        public List getUnionCases() {
            return this.unionCases;
        }
        
        public List getDefaultCaseBody() {
            return this.defaultCaseBody;
        }
    }
    
    public class Call extends LayoutElement
    {
        private final int callableIndex;
        private Callable callable;
        
        public Call(final int callableIndex) {
            this.callableIndex = callableIndex;
        }
        
        public void setCallable(final Callable callable) {
            this.callable = callable;
            if (this.callableIndex < 1) {
                callable.setBackwardsCallable();
            }
        }
        
        @Override
        public void readBands(final InputStream in, final int count) {
            if (this.callableIndex > 0) {
                this.callable.addCount(count);
            }
        }
        
        @Override
        public void addToAttribute(final int n, final NewAttribute attribute) {
            this.callable.addNextToAttribute(attribute);
        }
        
        public int getCallableIndex() {
            return this.callableIndex;
        }
        
        public Callable getCallable() {
            return this.callable;
        }
    }
    
    public class Reference extends LayoutElement
    {
        private final String tag;
        private Object band;
        private final int length;
        
        public Reference(final String tag) {
            this.tag = tag;
            this.length = this.getLength(tag.charAt(tag.length() - 1));
        }
        
        @Override
        public void readBands(final InputStream in, final int count) throws IOException, Pack200Exception {
            if (this.tag.startsWith("KI")) {
                this.band = NewAttributeBands.this.parseCPIntReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("KJ")) {
                this.band = NewAttributeBands.this.parseCPLongReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("KF")) {
                this.band = NewAttributeBands.this.parseCPFloatReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("KD")) {
                this.band = NewAttributeBands.this.parseCPDoubleReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("KS")) {
                this.band = NewAttributeBands.this.parseCPStringReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RC")) {
                this.band = NewAttributeBands.this.parseCPClassReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RS")) {
                this.band = NewAttributeBands.this.parseCPSignatureReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RD")) {
                this.band = NewAttributeBands.this.parseCPDescriptorReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RF")) {
                this.band = NewAttributeBands.this.parseCPFieldRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RM")) {
                this.band = NewAttributeBands.this.parseCPMethodRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RI")) {
                this.band = NewAttributeBands.this.parseCPInterfaceMethodRefReferences(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
            else if (this.tag.startsWith("RU")) {
                this.band = NewAttributeBands.this.parseCPUTF8References(NewAttributeBands.this.attributeLayout.getName(), in, Codec.UNSIGNED5, count);
            }
        }
        
        @Override
        public void addToAttribute(final int n, final NewAttribute attribute) {
            if (this.tag.startsWith("KI")) {
                attribute.addToBody(this.length, ((CPInteger[])this.band)[n]);
            }
            else if (this.tag.startsWith("KJ")) {
                attribute.addToBody(this.length, ((CPLong[])this.band)[n]);
            }
            else if (this.tag.startsWith("KF")) {
                attribute.addToBody(this.length, ((CPFloat[])this.band)[n]);
            }
            else if (this.tag.startsWith("KD")) {
                attribute.addToBody(this.length, ((CPDouble[])this.band)[n]);
            }
            else if (this.tag.startsWith("KS")) {
                attribute.addToBody(this.length, ((CPString[])this.band)[n]);
            }
            else if (this.tag.startsWith("RC")) {
                attribute.addToBody(this.length, ((CPClass[])this.band)[n]);
            }
            else if (this.tag.startsWith("RS")) {
                attribute.addToBody(this.length, ((CPUTF8[])this.band)[n]);
            }
            else if (this.tag.startsWith("RD")) {
                attribute.addToBody(this.length, ((CPNameAndType[])this.band)[n]);
            }
            else if (this.tag.startsWith("RF")) {
                attribute.addToBody(this.length, ((CPFieldRef[])this.band)[n]);
            }
            else if (this.tag.startsWith("RM")) {
                attribute.addToBody(this.length, ((CPMethodRef[])this.band)[n]);
            }
            else if (this.tag.startsWith("RI")) {
                attribute.addToBody(this.length, ((CPInterfaceMethodRef[])this.band)[n]);
            }
            else if (this.tag.startsWith("RU")) {
                attribute.addToBody(this.length, ((CPUTF8[])this.band)[n]);
            }
        }
        
        public String getTag() {
            return this.tag;
        }
    }
    
    public static class Callable implements AttributeLayoutElement
    {
        private final List body;
        private boolean isBackwardsCallable;
        private boolean isFirstCallable;
        private int count;
        private int index;
        
        public Callable(final List body) throws IOException {
            this.body = body;
        }
        
        public void addNextToAttribute(final NewAttribute attribute) {
            for (int i = 0; i < this.body.size(); ++i) {
                final LayoutElement element = this.body.get(i);
                element.addToAttribute(this.index, attribute);
            }
            ++this.index;
        }
        
        public void addCount(final int count) {
            this.count += count;
        }
        
        @Override
        public void readBands(final InputStream in, int count) throws IOException, Pack200Exception {
            if (this.isFirstCallable) {
                count += this.count;
            }
            else {
                count = this.count;
            }
            for (int i = 0; i < this.body.size(); ++i) {
                final LayoutElement element = this.body.get(i);
                element.readBands(in, count);
            }
        }
        
        @Override
        public void addToAttribute(final int n, final NewAttribute attribute) {
            if (this.isFirstCallable) {
                for (int i = 0; i < this.body.size(); ++i) {
                    final LayoutElement element = this.body.get(i);
                    element.addToAttribute(this.index, attribute);
                }
                ++this.index;
            }
        }
        
        public boolean isBackwardsCallable() {
            return this.isBackwardsCallable;
        }
        
        public void setBackwardsCallable() {
            this.isBackwardsCallable = true;
        }
        
        public void setFirstCallable(final boolean isFirstCallable) {
            this.isFirstCallable = isFirstCallable;
        }
        
        public List getBody() {
            return this.body;
        }
    }
    
    public class UnionCase extends LayoutElement
    {
        private List body;
        private final List tags;
        
        public UnionCase(final List tags) {
            this.tags = tags;
        }
        
        public boolean hasTag(final long l) {
            return this.tags.contains((int)l);
        }
        
        public UnionCase(final List tags, final List body) throws IOException {
            this.tags = tags;
            this.body = body;
        }
        
        @Override
        public void readBands(final InputStream in, final int count) throws IOException, Pack200Exception {
            if (this.body != null) {
                for (int i = 0; i < this.body.size(); ++i) {
                    final LayoutElement element = this.body.get(i);
                    element.readBands(in, count);
                }
            }
        }
        
        @Override
        public void addToAttribute(final int index, final NewAttribute attribute) {
            if (this.body != null) {
                for (int i = 0; i < this.body.size(); ++i) {
                    final LayoutElement element = this.body.get(i);
                    element.addToAttribute(index, attribute);
                }
            }
        }
        
        public List getBody() {
            return (this.body == null) ? Collections.EMPTY_LIST : this.body;
        }
    }
    
    private interface AttributeLayoutElement
    {
        void readBands(final InputStream p0, final int p1) throws IOException, Pack200Exception;
        
        void addToAttribute(final int p0, final NewAttribute p1);
    }
}
