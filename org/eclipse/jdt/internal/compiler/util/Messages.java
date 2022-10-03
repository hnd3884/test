package org.eclipse.jdt.internal.compiler.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.text.MessageFormat;

public final class Messages
{
    private static String[] nlSuffixes;
    private static final String EXTENSION = ".properties";
    private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.compiler.messages";
    public static String compilation_unresolvedProblem;
    public static String compilation_unresolvedProblems;
    public static String compilation_request;
    public static String compilation_loadBinary;
    public static String compilation_process;
    public static String compilation_write;
    public static String compilation_done;
    public static String compilation_units;
    public static String compilation_unit;
    public static String compilation_internalError;
    public static String compilation_beginningToCompile;
    public static String compilation_processing;
    public static String output_isFile;
    public static String output_notValidAll;
    public static String output_notValid;
    public static String problem_noSourceInformation;
    public static String problem_atLine;
    public static String abort_invalidAttribute;
    public static String abort_invalidExceptionAttribute;
    public static String abort_invalidOpcode;
    public static String abort_missingCode;
    public static String abort_againstSourceModel;
    public static String abort_externaAnnotationFile;
    public static String accept_cannot;
    public static String parser_incorrectPath;
    public static String parser_moveFiles;
    public static String parser_syntaxRecovery;
    public static String parser_regularParse;
    public static String parser_missingFile;
    public static String parser_corruptedFile;
    public static String parser_endOfFile;
    public static String parser_endOfConstructor;
    public static String parser_endOfMethod;
    public static String parser_endOfInitializer;
    public static String ast_missingCode;
    public static String constant_cannotCastedInto;
    public static String constant_cannotConvertedTo;
    
    static {
        initializeMessages("org.eclipse.jdt.internal.compiler.messages", Messages.class);
    }
    
    private Messages() {
    }
    
    public static String bind(final String message) {
        return bind(message, null);
    }
    
    public static String bind(final String message, final Object binding) {
        return bind(message, new Object[] { binding });
    }
    
    public static String bind(final String message, final Object binding1, final Object binding2) {
        return bind(message, new Object[] { binding1, binding2 });
    }
    
    public static String bind(final String message, final Object[] bindings) {
        return MessageFormat.format(message, bindings);
    }
    
    private static String[] buildVariants(String root) {
        if (Messages.nlSuffixes == null) {
            String nl = Locale.getDefault().toString();
            final ArrayList result = new ArrayList(4);
            while (true) {
                result.add(String.valueOf('_') + nl + ".properties");
                final int lastSeparator = nl.lastIndexOf(95);
                if (lastSeparator == -1) {
                    break;
                }
                nl = nl.substring(0, lastSeparator);
            }
            result.add(".properties");
            Messages.nlSuffixes = result.toArray(new String[result.size()]);
        }
        root = root.replace('.', '/');
        final String[] variants = new String[Messages.nlSuffixes.length];
        for (int i = 0; i < variants.length; ++i) {
            variants[i] = String.valueOf(root) + Messages.nlSuffixes[i];
        }
        return variants;
    }
    
    public static void initializeMessages(final String bundleName, final Class clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        load(bundleName, clazz.getClassLoader(), fields);
        for (final Field field : fields) {
            if ((field.getModifiers() & 0x19) == 0x9) {
                try {
                    if (field.get(clazz) == null) {
                        final String value = "Missing message: " + field.getName() + " in: " + bundleName;
                        field.set(null, value);
                    }
                }
                catch (final IllegalArgumentException ex) {}
                catch (final IllegalAccessException ex2) {}
            }
        }
    }
    
    public static void load(final String bundleName, final ClassLoader loader, final Field[] fields) {
        final String[] variants = buildVariants(bundleName);
        int i = variants.length;
        while (--i >= 0) {
            final InputStream input = (loader == null) ? ClassLoader.getSystemResourceAsStream(variants[i]) : loader.getResourceAsStream(variants[i]);
            if (input == null) {
                continue;
            }
            try {
                final MessagesProperties properties = new MessagesProperties(fields, bundleName);
                properties.load(input);
            }
            catch (final IOException ex) {
                try {
                    input.close();
                }
                catch (final IOException ex2) {}
            }
            finally {
                try {
                    input.close();
                }
                catch (final IOException ex3) {}
            }
            try {
                input.close();
            }
            catch (final IOException ex4) {}
        }
    }
    
    private static class MessagesProperties extends Properties
    {
        private static final int MOD_EXPECTED = 9;
        private static final int MOD_MASK = 25;
        private static final long serialVersionUID = 1L;
        private final Map fields;
        
        public MessagesProperties(final Field[] fieldArray, final String bundleName) {
            final int len = fieldArray.length;
            this.fields = new HashMap(len * 2);
            for (int i = 0; i < len; ++i) {
                this.fields.put(fieldArray[i].getName(), fieldArray[i]);
            }
        }
        
        @Override
        public synchronized Object put(final Object key, final Object value) {
            try {
                final Field field = this.fields.get(key);
                if (field == null) {
                    return null;
                }
                if ((field.getModifiers() & 0x19) != 0x9) {
                    return null;
                }
                try {
                    field.set(null, value);
                }
                catch (final Exception ex) {}
            }
            catch (final SecurityException ex2) {}
            return null;
        }
    }
}
