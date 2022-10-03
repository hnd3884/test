package jdk.jfr.consumer;

import java.util.Objects;
import jdk.jfr.internal.consumer.RecordingInput;
import java.util.List;
import jdk.jfr.internal.PrivateAccess;
import jdk.jfr.ValueDescriptor;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Iterator;
import jdk.jfr.EventType;
import jdk.jfr.internal.MetadataDescriptor;
import jdk.jfr.internal.Type;

final class ParserFactory
{
    private final LongMap<Parser> parsers;
    private final TimeConverter timeConverter;
    private final LongMap<Type> types;
    private final LongMap<ConstantMap> constantPools;
    
    public ParserFactory(final MetadataDescriptor metadataDescriptor, final TimeConverter timeConverter) throws IOException {
        this.parsers = new LongMap<Parser>();
        this.types = new LongMap<Type>();
        this.constantPools = new LongMap<ConstantMap>();
        this.timeConverter = timeConverter;
        for (final Type type : metadataDescriptor.getTypes()) {
            this.types.put(type.getId(), type);
        }
        for (final Type type2 : this.types) {
            if (!type2.getFields().isEmpty()) {
                final CompositeParser compositeParser = this.createCompositeParser(type2);
                if (!type2.isSimpleType()) {
                    continue;
                }
                this.parsers.put(type2.getId(), compositeParser.parsers[0]);
            }
        }
        for (final EventType eventType : metadataDescriptor.getEventTypes()) {
            this.parsers.put(eventType.getId(), this.createEventParser(eventType));
        }
    }
    
    public LongMap<Parser> getParsers() {
        return this.parsers;
    }
    
    public LongMap<ConstantMap> getConstantPools() {
        return this.constantPools;
    }
    
    public LongMap<Type> getTypeMap() {
        return this.types;
    }
    
    private EventParser createEventParser(final EventType eventType) throws IOException {
        final ArrayList list = new ArrayList();
        final Iterator<ValueDescriptor> iterator = eventType.getFields().iterator();
        while (iterator.hasNext()) {
            list.add(this.createParser(iterator.next()));
        }
        return new EventParser(this.timeConverter, eventType, (Parser[])list.toArray(new Parser[0]));
    }
    
    private Parser createParser(final ValueDescriptor valueDescriptor) throws IOException {
        final boolean constantPool = PrivateAccess.getInstance().isConstantPool(valueDescriptor);
        if (valueDescriptor.isArray()) {
            return new ArrayParser(this.createParser(PrivateAccess.getInstance().newValueDescriptor(valueDescriptor.getName(), PrivateAccess.getInstance().getType(valueDescriptor), valueDescriptor.getAnnotationElements(), 0, constantPool, null)));
        }
        final long typeId = valueDescriptor.getTypeId();
        final Type type = this.types.get(typeId);
        if (type == null) {
            throw new IOException("Type '" + valueDescriptor.getTypeName() + "' is not defined");
        }
        if (constantPool) {
            ConstantMap constantMap = this.constantPools.get(typeId);
            if (constantMap == null) {
                constantMap = new ConstantMap(ObjectFactory.create(type, this.timeConverter), type.getName());
                this.constantPools.put(typeId, constantMap);
            }
            return new ConstantMapValueParser(constantMap);
        }
        final Parser parser = this.parsers.get(typeId);
        if (parser != null) {
            return parser;
        }
        if (!valueDescriptor.getFields().isEmpty()) {
            return this.createCompositeParser(type);
        }
        return this.registerParserType(type, this.createPrimitiveParser(type));
    }
    
    private Parser createPrimitiveParser(final Type type) throws IOException {
        final String name = type.getName();
        switch (name) {
            case "int": {
                return new IntegerParser();
            }
            case "long": {
                return new LongParser();
            }
            case "float": {
                return new FloatParser();
            }
            case "double": {
                return new DoubleParser();
            }
            case "char": {
                return new CharacterParser();
            }
            case "boolean": {
                return new BooleanParser();
            }
            case "short": {
                return new ShortParser();
            }
            case "byte": {
                return new ByteParser();
            }
            case "java.lang.String": {
                final ConstantMap constantMap = new ConstantMap(ObjectFactory.create(type, this.timeConverter), type.getName());
                this.constantPools.put(type.getId(), constantMap);
                return new StringParser(constantMap);
            }
            default: {
                throw new IOException("Unknown primitive type " + type.getName());
            }
        }
    }
    
    private Parser registerParserType(final Type type, final Parser parser) {
        final Parser parser2 = this.parsers.get(type.getId());
        if (parser2 != null) {
            return parser2;
        }
        this.parsers.put(type.getId(), parser);
        return parser;
    }
    
    private CompositeParser createCompositeParser(final Type type) throws IOException {
        final List<ValueDescriptor> fields = type.getFields();
        final Parser[] array = new Parser[fields.size()];
        final CompositeParser compositeParser = new CompositeParser(array);
        this.registerParserType(type, compositeParser);
        int n = 0;
        final Iterator iterator = fields.iterator();
        while (iterator.hasNext()) {
            array[n++] = this.createParser((ValueDescriptor)iterator.next());
        }
        return compositeParser;
    }
    
    private static final class BooleanParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readBoolean() ? Boolean.TRUE : Boolean.FALSE;
        }
    }
    
    private static final class ByteParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readByte();
        }
    }
    
    private static final class LongParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readLong();
        }
    }
    
    private static final class IntegerParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readInt();
        }
    }
    
    private static final class ShortParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readShort();
        }
    }
    
    private static final class CharacterParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readChar();
        }
    }
    
    private static final class FloatParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readFloat();
        }
    }
    
    private static final class DoubleParser extends Parser
    {
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return recordingInput.readDouble();
        }
    }
    
    private static final class StringParser extends Parser
    {
        private final ConstantMap stringConstantMap;
        private String last;
        
        StringParser(final ConstantMap stringConstantMap) {
            this.stringConstantMap = stringConstantMap;
        }
        
        public Object parse(final RecordingInput recordingInput) throws IOException {
            final String encodedString = this.parseEncodedString(recordingInput);
            if (!Objects.equals(encodedString, this.last)) {
                this.last = encodedString;
            }
            return this.last;
        }
        
        private String parseEncodedString(final RecordingInput recordingInput) throws IOException {
            final byte byte1 = recordingInput.readByte();
            if (byte1 == 2) {
                return (String)this.stringConstantMap.get(recordingInput.readLong());
            }
            return recordingInput.readEncodedString(byte1);
        }
    }
    
    private static final class ArrayParser extends Parser
    {
        private final Parser elementParser;
        
        public ArrayParser(final Parser elementParser) {
            this.elementParser = elementParser;
        }
        
        public Object parse(final RecordingInput recordingInput) throws IOException {
            final int int1 = recordingInput.readInt();
            final Object[] array = new Object[int1];
            for (int i = 0; i < int1; ++i) {
                array[i] = this.elementParser.parse(recordingInput);
            }
            return array;
        }
    }
    
    private static final class CompositeParser extends Parser
    {
        private final Parser[] parsers;
        
        public CompositeParser(final Parser[] parsers) {
            this.parsers = parsers;
        }
        
        public Object parse(final RecordingInput recordingInput) throws IOException {
            final Object[] array = new Object[this.parsers.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = this.parsers[i].parse(recordingInput);
            }
            return array;
        }
    }
    
    private static final class ConstantMapValueParser extends Parser
    {
        private final ConstantMap pool;
        
        ConstantMapValueParser(final ConstantMap pool) {
            this.pool = pool;
        }
        
        public Object parse(final RecordingInput recordingInput) throws IOException {
            return this.pool.get(recordingInput.readLong());
        }
    }
}
