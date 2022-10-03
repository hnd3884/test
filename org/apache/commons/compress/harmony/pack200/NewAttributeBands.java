package org.apache.commons.compress.harmony.pack200;

import java.util.Collections;
import org.objectweb.asm.Label;
import java.util.Map;
import java.io.StringReader;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class NewAttributeBands extends BandSet
{
    protected List attributeLayoutElements;
    private int[] backwardsCallCounts;
    private final CpBands cpBands;
    private final AttributeDefinitionBands.AttributeDefinition def;
    private boolean usedAtLeastOnce;
    private Integral lastPIntegral;
    
    public NewAttributeBands(final int effort, final CpBands cpBands, final SegmentHeader header, final AttributeDefinitionBands.AttributeDefinition def) throws IOException {
        super(effort, header);
        this.def = def;
        this.cpBands = cpBands;
        this.parseLayout();
    }
    
    public void addAttribute(final NewAttribute attribute) {
        this.usedAtLeastOnce = true;
        final InputStream stream = new ByteArrayInputStream(attribute.getBytes());
        for (final AttributeLayoutElement layoutElement : this.attributeLayoutElements) {
            layoutElement.addAttributeToBand(attribute, stream);
        }
    }
    
    @Override
    public void pack(final OutputStream out) throws IOException, Pack200Exception {
        for (final AttributeLayoutElement layoutElement : this.attributeLayoutElements) {
            layoutElement.pack(out);
        }
    }
    
    public String getAttributeName() {
        return this.def.name.getUnderlyingString();
    }
    
    public int getFlagIndex() {
        return this.def.index;
    }
    
    public int[] numBackwardsCalls() {
        return this.backwardsCallCounts;
    }
    
    public boolean isUsedAtLeastOnce() {
        return this.usedAtLeastOnce;
    }
    
    private void parseLayout() throws IOException {
        final String layout = this.def.layout.getUnderlyingString();
        if (this.attributeLayoutElements == null) {
            this.attributeLayoutElements = new ArrayList();
            final StringReader stream = new StringReader(layout);
            AttributeLayoutElement e;
            while ((e = this.readNextAttributeElement(stream)) != null) {
                this.attributeLayoutElements.add(e);
            }
            this.resolveCalls();
        }
    }
    
    private void resolveCalls() {
        for (int i = 0; i < this.attributeLayoutElements.size(); ++i) {
            final AttributeLayoutElement element = this.attributeLayoutElements.get(i);
            if (element instanceof Callable) {
                final Callable callable = (Callable)element;
                final List body = callable.body;
                for (int iIndex = 0; iIndex < body.size(); ++iIndex) {
                    final LayoutElement layoutElement = body.get(iIndex);
                    this.resolveCallsForElement(i, callable, layoutElement);
                }
            }
        }
        int backwardsCallableIndex = 0;
        for (int j = 0; j < this.attributeLayoutElements.size(); ++j) {
            final AttributeLayoutElement element2 = this.attributeLayoutElements.get(j);
            if (element2 instanceof Callable) {
                final Callable callable2 = (Callable)element2;
                if (callable2.isBackwardsCallable) {
                    callable2.setBackwardsCallableIndex(backwardsCallableIndex);
                    ++backwardsCallableIndex;
                }
            }
        }
        this.backwardsCallCounts = new int[backwardsCallableIndex];
    }
    
    private void resolveCallsForElement(final int i, final Callable currentCallable, final LayoutElement layoutElement) {
        if (layoutElement instanceof Call) {
            final Call call = (Call)layoutElement;
            int index = call.callableIndex;
            if (index == 0) {
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
                this.resolveCallsForElement(i, currentCallable, object);
            }
        }
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
                    return this.lastPIntegral = new Integral("P" + (char)stream.read());
                }
                return this.lastPIntegral = new Integral("PO" + (char)stream.read(), this.lastPIntegral);
            }
            case 79: {
                stream.mark(1);
                if (stream.read() != 83) {
                    stream.reset();
                    return new Integral("O" + (char)stream.read(), this.lastPIntegral);
                }
                return new Integral("OS" + (char)stream.read(), this.lastPIntegral);
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
    
    private int readInteger(final int i, final InputStream stream) {
        int result = 0;
        for (int j = 0; j < i; ++j) {
            try {
                result = (result << 8 | stream.read());
            }
            catch (final IOException e) {
                throw new RuntimeException("Error reading unknown attribute");
            }
        }
        if (i == 1) {
            result = (byte)result;
        }
        if (i == 2) {
            result = (short)result;
        }
        return result;
    }
    
    private BHSDCodec getCodec(final String layoutElement) {
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
    
    public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
        for (final AttributeLayoutElement element : this.attributeLayoutElements) {
            element.renumberBci(bciRenumbering, labelsToOffsets);
        }
    }
    
    public abstract class LayoutElement implements AttributeLayoutElement
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
        private final List band;
        private final BHSDCodec defaultCodec;
        private Integral previousIntegral;
        private int previousPValue;
        
        public Integral(final String tag) {
            this.band = new ArrayList();
            this.tag = tag;
            this.defaultCodec = NewAttributeBands.this.getCodec(tag);
        }
        
        public Integral(final String tag, final Integral previousIntegral) {
            this.band = new ArrayList();
            this.tag = tag;
            this.defaultCodec = NewAttributeBands.this.getCodec(tag);
            this.previousIntegral = previousIntegral;
        }
        
        public String getTag() {
            return this.tag;
        }
        
        @Override
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            Object val = null;
            int value = 0;
            if (this.tag.equals("B") || this.tag.equals("FB")) {
                value = (NewAttributeBands.this.readInteger(1, stream) & 0xFF);
            }
            else if (this.tag.equals("SB")) {
                value = NewAttributeBands.this.readInteger(1, stream);
            }
            else if (this.tag.equals("H") || this.tag.equals("FH")) {
                value = (NewAttributeBands.this.readInteger(2, stream) & 0xFFFF);
            }
            else if (this.tag.equals("SH")) {
                value = NewAttributeBands.this.readInteger(2, stream);
            }
            else if (this.tag.equals("I") || this.tag.equals("FI")) {
                value = NewAttributeBands.this.readInteger(4, stream);
            }
            else if (this.tag.equals("SI")) {
                value = NewAttributeBands.this.readInteger(4, stream);
            }
            else if (!this.tag.equals("V") && !this.tag.equals("FV")) {
                if (!this.tag.equals("SV")) {
                    if (this.tag.startsWith("PO") || this.tag.startsWith("OS")) {
                        final char uint_type = this.tag.substring(2).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        value = NewAttributeBands.this.readInteger(length, stream);
                        value += this.previousIntegral.previousPValue;
                        val = attribute.getLabel(value);
                        this.previousPValue = value;
                    }
                    else if (this.tag.startsWith("P")) {
                        final char uint_type = this.tag.substring(1).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        value = NewAttributeBands.this.readInteger(length, stream);
                        val = attribute.getLabel(value);
                        this.previousPValue = value;
                    }
                    else if (this.tag.startsWith("O")) {
                        final char uint_type = this.tag.substring(1).toCharArray()[0];
                        final int length = this.getLength(uint_type);
                        value = NewAttributeBands.this.readInteger(length, stream);
                        value += this.previousIntegral.previousPValue;
                        val = attribute.getLabel(value);
                        this.previousPValue = value;
                    }
                }
            }
            if (val == null) {
                val = value;
            }
            this.band.add(val);
        }
        
        @Override
        public void pack(final OutputStream out) throws IOException, Pack200Exception {
            PackingUtils.log("Writing new attribute bands...");
            final byte[] encodedBand = NewAttributeBands.this.encodeBandInt(this.tag, NewAttributeBands.this.integerListToArray(this.band), this.defaultCodec);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + this.tag + "[" + this.band.size() + "]");
        }
        
        public int latestValue() {
            return this.band.get(this.band.size() - 1);
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
            if (this.tag.startsWith("O") || this.tag.startsWith("PO")) {
                this.renumberOffsetBci(this.previousIntegral.band, bciRenumbering, labelsToOffsets);
            }
            else if (this.tag.startsWith("P")) {
                for (int i = this.band.size() - 1; i >= 0; --i) {
                    final Object label = this.band.get(i);
                    if (label instanceof Integer) {
                        break;
                    }
                    if (label instanceof Label) {
                        this.band.remove(i);
                        final Integer bytecodeIndex = labelsToOffsets.get(label);
                        this.band.add(i, bciRenumbering.get(bytecodeIndex));
                    }
                }
            }
        }
        
        private void renumberOffsetBci(final List relative, final IntList bciRenumbering, final Map labelsToOffsets) {
            for (int i = this.band.size() - 1; i >= 0; --i) {
                final Object label = this.band.get(i);
                if (label instanceof Integer) {
                    break;
                }
                if (label instanceof Label) {
                    this.band.remove(i);
                    final Integer bytecodeIndex = labelsToOffsets.get(label);
                    final Integer renumberedOffset = bciRenumbering.get(bytecodeIndex) - relative.get(i);
                    this.band.add(i, renumberedOffset);
                }
            }
        }
    }
    
    public class Replication extends LayoutElement
    {
        private final Integral countElement;
        private final List layoutElements;
        
        public Integral getCountElement() {
            return this.countElement;
        }
        
        public List getLayoutElements() {
            return this.layoutElements;
        }
        
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
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            this.countElement.addAttributeToBand(attribute, stream);
            for (int count = this.countElement.latestValue(), i = 0; i < count; ++i) {
                for (final AttributeLayoutElement layoutElement : this.layoutElements) {
                    layoutElement.addAttributeToBand(attribute, stream);
                }
            }
        }
        
        @Override
        public void pack(final OutputStream out) throws IOException, Pack200Exception {
            this.countElement.pack(out);
            for (final AttributeLayoutElement layoutElement : this.layoutElements) {
                layoutElement.pack(out);
            }
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
            for (final AttributeLayoutElement layoutElement : this.layoutElements) {
                layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
            }
        }
    }
    
    public class Union extends LayoutElement
    {
        private final Integral unionTag;
        private final List unionCases;
        private final List defaultCaseBody;
        
        public Union(final String tag, final List unionCases, final List body) {
            this.unionTag = new Integral(tag);
            this.unionCases = unionCases;
            this.defaultCaseBody = body;
        }
        
        @Override
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            this.unionTag.addAttributeToBand(attribute, stream);
            final long tag = this.unionTag.latestValue();
            boolean defaultCase = true;
            for (int i = 0; i < this.unionCases.size(); ++i) {
                final UnionCase element = this.unionCases.get(i);
                if (element.hasTag(tag)) {
                    defaultCase = false;
                    element.addAttributeToBand(attribute, stream);
                }
            }
            if (defaultCase) {
                for (int i = 0; i < this.defaultCaseBody.size(); ++i) {
                    final LayoutElement element2 = this.defaultCaseBody.get(i);
                    element2.addAttributeToBand(attribute, stream);
                }
            }
        }
        
        @Override
        public void pack(final OutputStream out) throws IOException, Pack200Exception {
            this.unionTag.pack(out);
            for (final UnionCase unionCase : this.unionCases) {
                unionCase.pack(out);
            }
            for (final AttributeLayoutElement layoutElement : this.defaultCaseBody) {
                layoutElement.pack(out);
            }
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
            for (final UnionCase unionCase : this.unionCases) {
                unionCase.renumberBci(bciRenumbering, labelsToOffsets);
            }
            for (final AttributeLayoutElement layoutElement : this.defaultCaseBody) {
                layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
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
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            this.callable.addAttributeToBand(attribute, stream);
            if (this.callableIndex < 1) {
                this.callable.addBackwardsCall();
            }
        }
        
        @Override
        public void pack(final OutputStream out) {
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
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
        private List band;
        private boolean nullsAllowed;
        
        public Reference(final String tag) {
            this.nullsAllowed = false;
            this.tag = tag;
            this.nullsAllowed = (tag.indexOf(78) != -1);
        }
        
        @Override
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            final int index = NewAttributeBands.this.readInteger(4, stream);
            if (this.tag.startsWith("RC")) {
                this.band.add(NewAttributeBands.this.cpBands.getCPClass(attribute.readClass(index)));
            }
            else if (this.tag.startsWith("RU")) {
                this.band.add(NewAttributeBands.this.cpBands.getCPUtf8(attribute.readUTF8(index)));
            }
            else if (this.tag.startsWith("RS")) {
                this.band.add(NewAttributeBands.this.cpBands.getCPSignature(attribute.readUTF8(index)));
            }
            else {
                this.band.add(NewAttributeBands.this.cpBands.getConstant(attribute.readConst(index)));
            }
        }
        
        public String getTag() {
            return this.tag;
        }
        
        @Override
        public void pack(final OutputStream out) throws IOException, Pack200Exception {
            int[] ints;
            if (this.nullsAllowed) {
                ints = NewAttributeBands.this.cpEntryOrNullListToArray(this.band);
            }
            else {
                ints = NewAttributeBands.this.cpEntryListToArray(this.band);
            }
            final byte[] encodedBand = NewAttributeBands.this.encodeBandInt(this.tag, ints, Codec.UNSIGNED5);
            out.write(encodedBand);
            PackingUtils.log("Wrote " + encodedBand.length + " bytes from " + this.tag + "[" + ints.length + "]");
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
        }
    }
    
    public class Callable implements AttributeLayoutElement
    {
        private final List body;
        private boolean isBackwardsCallable;
        private int backwardsCallableIndex;
        
        public Callable(final List body) throws IOException {
            this.body = body;
        }
        
        public void setBackwardsCallableIndex(final int backwardsCallableIndex) {
            this.backwardsCallableIndex = backwardsCallableIndex;
        }
        
        public void addBackwardsCall() {
            final int[] access$800 = NewAttributeBands.this.backwardsCallCounts;
            final int backwardsCallableIndex = this.backwardsCallableIndex;
            ++access$800[backwardsCallableIndex];
        }
        
        public boolean isBackwardsCallable() {
            return this.isBackwardsCallable;
        }
        
        public void setBackwardsCallable() {
            this.isBackwardsCallable = true;
        }
        
        @Override
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            for (final AttributeLayoutElement layoutElement : this.body) {
                layoutElement.addAttributeToBand(attribute, stream);
            }
        }
        
        @Override
        public void pack(final OutputStream out) throws IOException, Pack200Exception {
            for (final AttributeLayoutElement layoutElement : this.body) {
                layoutElement.pack(out);
            }
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
            for (final AttributeLayoutElement layoutElement : this.body) {
                layoutElement.renumberBci(bciRenumbering, labelsToOffsets);
            }
        }
        
        public List getBody() {
            return this.body;
        }
    }
    
    public class UnionCase extends LayoutElement
    {
        private final List body;
        private final List tags;
        
        public UnionCase(final List tags) {
            this.tags = tags;
            this.body = Collections.EMPTY_LIST;
        }
        
        public boolean hasTag(final long l) {
            return this.tags.contains((int)l);
        }
        
        public UnionCase(final List tags, final List body) throws IOException {
            this.tags = tags;
            this.body = body;
        }
        
        @Override
        public void addAttributeToBand(final NewAttribute attribute, final InputStream stream) {
            for (int i = 0; i < this.body.size(); ++i) {
                final LayoutElement element = this.body.get(i);
                element.addAttributeToBand(attribute, stream);
            }
        }
        
        @Override
        public void pack(final OutputStream out) throws IOException, Pack200Exception {
            for (int i = 0; i < this.body.size(); ++i) {
                final LayoutElement element = this.body.get(i);
                element.pack(out);
            }
        }
        
        @Override
        public void renumberBci(final IntList bciRenumbering, final Map labelsToOffsets) {
            for (int i = 0; i < this.body.size(); ++i) {
                final LayoutElement element = this.body.get(i);
                element.renumberBci(bciRenumbering, labelsToOffsets);
            }
        }
        
        public List getBody() {
            return this.body;
        }
    }
    
    public interface AttributeLayoutElement
    {
        void addAttributeToBand(final NewAttribute p0, final InputStream p1);
        
        void pack(final OutputStream p0) throws IOException, Pack200Exception;
        
        void renumberBci(final IntList p0, final Map p1);
    }
}
