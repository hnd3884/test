package org.json.simple;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.EnumSet;
import java.io.Reader;

public class Jsoner
{
    private Jsoner() {
    }
    
    public static Object deserialize(final Reader readableDeserializable) throws DeserializationException {
        return deserialize(readableDeserializable, EnumSet.of(DeserializationOptions.ALLOW_JSON_ARRAYS, DeserializationOptions.ALLOW_JSON_OBJECTS, DeserializationOptions.ALLOW_JSON_DATA)).get(0);
    }
    
    private static JsonArray deserialize(final Reader deserializable, final Set<DeserializationOptions> flags) throws DeserializationException {
        final Yylex lexer = new Yylex(deserializable);
        int returnCount = 1;
        final LinkedList<States> stateStack = new LinkedList<States>();
        final LinkedList<Object> valueStack = new LinkedList<Object>();
        stateStack.addLast(States.INITIAL);
        States currentState;
        Yytoken token;
        do {
            currentState = popNextState(stateStack);
            token = lexNextToken(lexer);
            switch (currentState) {
                case DONE: {
                    if (!flags.contains(DeserializationOptions.ALLOW_CONCATENATED_JSON_VALUES)) {
                        continue;
                    }
                    if (Yytoken.Types.END.equals(token.getType())) {
                        continue;
                    }
                    ++returnCount;
                }
                case INITIAL: {
                    switch (token.getType()) {
                        case DATUM: {
                            if (flags.contains(DeserializationOptions.ALLOW_JSON_DATA)) {
                                valueStack.addLast(token.getValue());
                                stateStack.addLast(States.DONE);
                                continue;
                            }
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.DISALLOWED_TOKEN, token);
                        }
                        case LEFT_BRACE: {
                            if (flags.contains(DeserializationOptions.ALLOW_JSON_OBJECTS)) {
                                valueStack.addLast(new JsonObject());
                                stateStack.addLast(States.PARSING_OBJECT);
                                continue;
                            }
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.DISALLOWED_TOKEN, token);
                        }
                        case LEFT_SQUARE: {
                            if (flags.contains(DeserializationOptions.ALLOW_JSON_ARRAYS)) {
                                valueStack.addLast(new JsonArray());
                                stateStack.addLast(States.PARSING_ARRAY);
                                continue;
                            }
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.DISALLOWED_TOKEN, token);
                        }
                        default: {
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.UNEXPECTED_TOKEN, token);
                        }
                    }
                    break;
                }
                case PARSED_ERROR: {
                    throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.UNEXPECTED_TOKEN, token);
                }
                case PARSING_ARRAY: {
                    switch (token.getType()) {
                        case COMMA: {
                            stateStack.addLast(currentState);
                            continue;
                        }
                        case DATUM: {
                            final JsonArray val = valueStack.getLast();
                            val.add(token.getValue());
                            stateStack.addLast(currentState);
                            continue;
                        }
                        case LEFT_BRACE: {
                            final JsonArray val = valueStack.getLast();
                            final JsonObject object = new JsonObject();
                            ((ArrayList<JsonObject>)val).add(object);
                            valueStack.addLast(object);
                            stateStack.addLast(currentState);
                            stateStack.addLast(States.PARSING_OBJECT);
                            continue;
                        }
                        case LEFT_SQUARE: {
                            final JsonArray val = valueStack.getLast();
                            final JsonArray array = new JsonArray();
                            ((ArrayList<JsonArray>)val).add(array);
                            valueStack.addLast(array);
                            stateStack.addLast(currentState);
                            stateStack.addLast(States.PARSING_ARRAY);
                            continue;
                        }
                        case RIGHT_SQUARE: {
                            if (valueStack.size() > returnCount) {
                                valueStack.removeLast();
                                continue;
                            }
                            stateStack.addLast(States.DONE);
                            continue;
                        }
                        default: {
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.UNEXPECTED_TOKEN, token);
                        }
                    }
                    break;
                }
                case PARSING_OBJECT: {
                    switch (token.getType()) {
                        case COMMA: {
                            stateStack.addLast(currentState);
                            continue;
                        }
                        case DATUM: {
                            if (token.getValue() instanceof String) {
                                final String key = (String)token.getValue();
                                valueStack.addLast(key);
                                stateStack.addLast(currentState);
                                stateStack.addLast(States.PARSING_ENTRY);
                                continue;
                            }
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.UNEXPECTED_TOKEN, token);
                        }
                        case RIGHT_BRACE: {
                            if (valueStack.size() > returnCount) {
                                valueStack.removeLast();
                                continue;
                            }
                            stateStack.addLast(States.DONE);
                            continue;
                        }
                        default: {
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.UNEXPECTED_TOKEN, token);
                        }
                    }
                    break;
                }
                case PARSING_ENTRY: {
                    switch (token.getType()) {
                        case COLON: {
                            stateStack.addLast(currentState);
                            continue;
                        }
                        case DATUM: {
                            final String key = valueStack.removeLast();
                            final JsonObject parent = valueStack.getLast();
                            parent.put(key, token.getValue());
                            continue;
                        }
                        case LEFT_BRACE: {
                            final String key = valueStack.removeLast();
                            final JsonObject parent = valueStack.getLast();
                            final JsonObject object2 = new JsonObject();
                            ((HashMap<String, JsonObject>)parent).put(key, object2);
                            valueStack.addLast(object2);
                            stateStack.addLast(States.PARSING_OBJECT);
                            continue;
                        }
                        case LEFT_SQUARE: {
                            final String key = valueStack.removeLast();
                            final JsonObject parent = valueStack.getLast();
                            final JsonArray array2 = new JsonArray();
                            ((HashMap<String, JsonArray>)parent).put(key, array2);
                            valueStack.addLast(array2);
                            stateStack.addLast(States.PARSING_ARRAY);
                            continue;
                        }
                        default: {
                            throw new DeserializationException(lexer.getPosition(), DeserializationException.Problems.UNEXPECTED_TOKEN, token);
                        }
                    }
                    break;
                }
                default: {
                    continue;
                }
            }
        } while (!States.DONE.equals(currentState) || !Yytoken.Types.END.equals(token.getType()));
        return new JsonArray(valueStack);
    }
    
    public static Object deserialize(final String deserializable) throws DeserializationException {
        StringReader readableDeserializable = null;
        Object returnable;
        try {
            readableDeserializable = new StringReader(deserializable);
            returnable = deserialize(readableDeserializable);
        }
        catch (final NullPointerException caught) {
            returnable = null;
        }
        finally {
            if (readableDeserializable != null) {
                readableDeserializable.close();
            }
        }
        return returnable;
    }
    
    public static JsonArray deserialize(final String deserializable, final JsonArray defaultValue) {
        StringReader readable = null;
        JsonArray returnable;
        try {
            readable = new StringReader(deserializable);
            returnable = deserialize(readable, EnumSet.of(DeserializationOptions.ALLOW_JSON_ARRAYS)).getCollection(0);
        }
        catch (final NullPointerException | DeserializationException caught) {
            returnable = defaultValue;
        }
        finally {
            if (readable != null) {
                readable.close();
            }
        }
        return returnable;
    }
    
    public static JsonObject deserialize(final String deserializable, final JsonObject defaultValue) {
        StringReader readable = null;
        JsonObject returnable;
        try {
            readable = new StringReader(deserializable);
            returnable = deserialize(readable, EnumSet.of(DeserializationOptions.ALLOW_JSON_OBJECTS)).getMap(0);
        }
        catch (final NullPointerException | DeserializationException caught) {
            returnable = defaultValue;
        }
        finally {
            if (readable != null) {
                readable.close();
            }
        }
        return returnable;
    }
    
    public static JsonArray deserializeMany(final Reader deserializable) throws DeserializationException {
        return deserialize(deserializable, EnumSet.of(DeserializationOptions.ALLOW_JSON_ARRAYS, DeserializationOptions.ALLOW_JSON_OBJECTS, DeserializationOptions.ALLOW_JSON_DATA, DeserializationOptions.ALLOW_CONCATENATED_JSON_VALUES));
    }
    
    public static String escape(final String escapable) {
        final StringBuilder builder = new StringBuilder();
        for (int characters = escapable.length(), i = 0; i < characters; ++i) {
            final char character = escapable.charAt(i);
            switch (character) {
                case '\"': {
                    builder.append("\\\"");
                    break;
                }
                case '\\': {
                    builder.append("\\\\");
                    break;
                }
                case '\b': {
                    builder.append("\\b");
                    break;
                }
                case '\f': {
                    builder.append("\\f");
                    break;
                }
                case '\n': {
                    builder.append("\\n");
                    break;
                }
                case '\r': {
                    builder.append("\\r");
                    break;
                }
                case '\t': {
                    builder.append("\\t");
                    break;
                }
                case '/': {
                    builder.append("\\/");
                    break;
                }
                default: {
                    if ((character >= '\0' && character <= '\u001f') || (character >= '\u007f' && character <= '\u009f') || (character >= '\u2000' && character <= '\u20ff')) {
                        final String characterHexCode = Integer.toHexString(character);
                        builder.append("\\u");
                        for (int k = 0; k < 4 - characterHexCode.length(); ++k) {
                            builder.append("0");
                        }
                        builder.append(characterHexCode.toUpperCase());
                        break;
                    }
                    builder.append(character);
                    break;
                }
            }
        }
        return builder.toString();
    }
    
    private static Yytoken lexNextToken(final Yylex lexer) throws DeserializationException {
        Yytoken returnable;
        try {
            returnable = lexer.yylex();
        }
        catch (final IOException caught) {
            throw new DeserializationException(-1, DeserializationException.Problems.UNEXPECTED_EXCEPTION, caught);
        }
        if (returnable == null) {
            returnable = new Yytoken(Yytoken.Types.END, null);
        }
        return returnable;
    }
    
    public static JsonKey mintJsonKey(final String key, final Object value) {
        return new JsonKey() {
            @Override
            public String getKey() {
                return key;
            }
            
            @Override
            public Object getValue() {
                return value;
            }
        };
    }
    
    private static States popNextState(final LinkedList<States> stateStack) {
        if (stateStack.size() > 0) {
            return stateStack.removeLast();
        }
        return States.PARSED_ERROR;
    }
    
    public static String prettyPrint(final String printable) {
        return prettyPrint(printable, "\t");
    }
    
    public static String prettyPrint(final String printable, final int spaces) {
        if (spaces > 10 || spaces < 2) {
            throw new IllegalArgumentException("Indentation with spaces must be between 2 and 10.");
        }
        final StringBuilder indentation = new StringBuilder("");
        for (int i = 0; i < spaces; ++i) {
            indentation.append(" ");
        }
        return prettyPrint(printable, indentation.toString());
    }
    
    private static String prettyPrint(final String printable, final String indentation) {
        final Yylex lexer = new Yylex(new StringReader(printable));
        final StringBuilder returnable = new StringBuilder();
        int level = 0;
        try {
            Yytoken lexed;
            do {
                lexed = lexNextToken(lexer);
                switch (lexed.getType()) {
                    case COLON: {
                        returnable.append(":");
                        continue;
                    }
                    case COMMA: {
                        returnable.append(lexed.getValue());
                        returnable.append("\n");
                        for (int i = 0; i < level; ++i) {
                            returnable.append(indentation);
                        }
                        continue;
                    }
                    case END: {
                        continue;
                    }
                    case LEFT_BRACE:
                    case LEFT_SQUARE: {
                        returnable.append(lexed.getValue());
                        returnable.append("\n");
                        ++level;
                        for (int i = 0; i < level; ++i) {
                            returnable.append(indentation);
                        }
                        continue;
                    }
                    case RIGHT_SQUARE:
                    case RIGHT_BRACE: {
                        returnable.append("\n");
                        --level;
                        for (int i = 0; i < level; ++i) {
                            returnable.append(indentation);
                        }
                        returnable.append(lexed.getValue());
                        continue;
                    }
                    default: {
                        if (lexed.getValue() instanceof String) {
                            returnable.append("\"");
                            returnable.append(escape((String)lexed.getValue()));
                            returnable.append("\"");
                            continue;
                        }
                        returnable.append(lexed.getValue());
                        continue;
                    }
                }
            } while (!lexed.getType().equals(Yytoken.Types.END));
        }
        catch (final DeserializationException caught) {
            return null;
        }
        return returnable.toString();
    }
    
    public static String serialize(final Object jsonSerializable) {
        final StringWriter writableDestination = new StringWriter();
        try {
            serialize(jsonSerializable, writableDestination);
        }
        catch (final IOException ex) {}
        return writableDestination.toString();
    }
    
    public static void serialize(final Object jsonSerializable, final Writer writableDestination) throws IOException {
        serialize(jsonSerializable, writableDestination, EnumSet.of(SerializationOptions.ALLOW_JSONABLES, SerializationOptions.ALLOW_FULLY_QUALIFIED_ENUMERATIONS));
    }
    
    private static void serialize(final Object jsonSerializable, final Writer writableDestination, final Set<SerializationOptions> flags) throws IOException {
        if (jsonSerializable == null) {
            writableDestination.write("null");
        }
        else if (jsonSerializable instanceof Jsonable && flags.contains(SerializationOptions.ALLOW_JSONABLES)) {
            writableDestination.write(((Jsonable)jsonSerializable).toJson());
        }
        else if (jsonSerializable instanceof Enum && flags.contains(SerializationOptions.ALLOW_FULLY_QUALIFIED_ENUMERATIONS)) {
            final Enum e = (Enum)jsonSerializable;
            writableDestination.write(34);
            writableDestination.write(e.getDeclaringClass().getName());
            writableDestination.write(46);
            writableDestination.write(e.name());
            writableDestination.write(34);
        }
        else if (jsonSerializable instanceof String) {
            writableDestination.write(34);
            writableDestination.write(escape((String)jsonSerializable));
            writableDestination.write(34);
        }
        else if (jsonSerializable instanceof Character) {
            writableDestination.write(escape(jsonSerializable.toString()));
        }
        else if (jsonSerializable instanceof Double) {
            if (((Double)jsonSerializable).isInfinite() || ((Double)jsonSerializable).isNaN()) {
                writableDestination.write("null");
            }
            else {
                writableDestination.write(jsonSerializable.toString());
            }
        }
        else if (jsonSerializable instanceof Float) {
            if (((Float)jsonSerializable).isInfinite() || ((Float)jsonSerializable).isNaN()) {
                writableDestination.write("null");
            }
            else {
                writableDestination.write(jsonSerializable.toString());
            }
        }
        else if (jsonSerializable instanceof Number) {
            writableDestination.write(jsonSerializable.toString());
        }
        else if (jsonSerializable instanceof Boolean) {
            writableDestination.write(jsonSerializable.toString());
        }
        else if (jsonSerializable instanceof Map) {
            boolean isFirstEntry = true;
            final Iterator entries = ((Map)jsonSerializable).entrySet().iterator();
            writableDestination.write(123);
            while (entries.hasNext()) {
                if (isFirstEntry) {
                    isFirstEntry = false;
                }
                else {
                    writableDestination.write(44);
                }
                final Map.Entry entry = entries.next();
                serialize(entry.getKey(), writableDestination, flags);
                writableDestination.write(58);
                serialize(entry.getValue(), writableDestination, flags);
            }
            writableDestination.write(125);
        }
        else if (jsonSerializable instanceof Collection) {
            boolean isFirstElement = true;
            final Iterator elements = ((Collection)jsonSerializable).iterator();
            writableDestination.write(91);
            while (elements.hasNext()) {
                if (isFirstElement) {
                    isFirstElement = false;
                }
                else {
                    writableDestination.write(44);
                }
                serialize(elements.next(), writableDestination, flags);
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof byte[]) {
            final byte[] writableArray = (byte[])jsonSerializable;
            final int numberOfElements = writableArray.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof short[]) {
            final short[] writableArray2 = (short[])jsonSerializable;
            final int numberOfElements = writableArray2.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray2[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray2[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof int[]) {
            final int[] writableArray3 = (int[])jsonSerializable;
            final int numberOfElements = writableArray3.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray3[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray3[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof long[]) {
            final long[] writableArray4 = (long[])jsonSerializable;
            final int numberOfElements = writableArray4.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray4[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray4[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof float[]) {
            final float[] writableArray5 = (float[])jsonSerializable;
            final int numberOfElements = writableArray5.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray5[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray5[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof double[]) {
            final double[] writableArray6 = (double[])jsonSerializable;
            final int numberOfElements = writableArray6.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray6[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray6[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof boolean[]) {
            final boolean[] writableArray7 = (boolean[])jsonSerializable;
            final int numberOfElements = writableArray7.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray7[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray7[i], writableDestination, flags);
                    writableDestination.write(44);
                }
            }
            writableDestination.write(93);
        }
        else if (jsonSerializable instanceof char[]) {
            final char[] writableArray8 = (char[])jsonSerializable;
            final int numberOfElements = writableArray8.length;
            writableDestination.write("[\"");
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray8[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray8[i], writableDestination, flags);
                    writableDestination.write("\",\"");
                }
            }
            writableDestination.write("\"]");
        }
        else if (jsonSerializable instanceof Object[]) {
            final Object[] writableArray9 = (Object[])jsonSerializable;
            final int numberOfElements = writableArray9.length;
            writableDestination.write(91);
            for (int i = 0; i < numberOfElements; ++i) {
                if (i == numberOfElements - 1) {
                    serialize(writableArray9[i], writableDestination, flags);
                }
                else {
                    serialize(writableArray9[i], writableDestination, flags);
                    writableDestination.write(",");
                }
            }
            writableDestination.write(93);
        }
        else {
            if (!flags.contains(SerializationOptions.ALLOW_INVALIDS)) {
                throw new IllegalArgumentException("Encountered a: " + jsonSerializable.getClass().getName() + " as: " + jsonSerializable.toString() + "  that isn't JSON serializable.\n  Try:\n    1) Implementing the Jsonable interface for the object to return valid JSON. If it already does it probably has a bug.\n    2) If you cannot edit the source of the object or couple it with this library consider wrapping it in a class that does implement the Jsonable interface.\n    3) Otherwise convert it to a boolean, null, number, JsonArray, JsonObject, or String value before serializing it.\n    4) If you feel it should have serialized you could use a more tolerant serialization for debugging purposes.");
            }
            writableDestination.write(jsonSerializable.toString());
        }
    }
    
    public static void serializeCarelessly(final Object jsonSerializable, final Writer writableDestination) throws IOException {
        serialize(jsonSerializable, writableDestination, EnumSet.of(SerializationOptions.ALLOW_JSONABLES, SerializationOptions.ALLOW_INVALIDS));
    }
    
    public static void serializeStrictly(final Object jsonSerializable, final Writer writableDestination) throws IOException {
        serialize(jsonSerializable, writableDestination, EnumSet.noneOf(SerializationOptions.class));
    }
    
    private enum DeserializationOptions
    {
        ALLOW_CONCATENATED_JSON_VALUES, 
        ALLOW_JSON_ARRAYS, 
        ALLOW_JSON_DATA, 
        ALLOW_JSON_OBJECTS;
    }
    
    private enum SerializationOptions
    {
        @Deprecated
        ALLOW_FULLY_QUALIFIED_ENUMERATIONS, 
        ALLOW_INVALIDS, 
        ALLOW_JSONABLES, 
        @Deprecated
        ALLOW_UNDEFINEDS;
    }
    
    private enum States
    {
        DONE, 
        INITIAL, 
        PARSED_ERROR, 
        PARSING_ARRAY, 
        PARSING_ENTRY, 
        PARSING_OBJECT;
    }
}
