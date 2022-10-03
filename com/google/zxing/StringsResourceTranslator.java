package com.google.zxing;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.TreeMap;
import java.io.Reader;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.SortedMap;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.io.FileFilter;
import java.util.Arrays;
import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;
import java.nio.charset.Charset;

public final class StringsResourceTranslator
{
    private static final String API_KEY = "INSERT-YOUR-KEY";
    private static final Charset UTF8;
    private static final Pattern ENTRY_PATTERN;
    private static final Pattern STRINGS_FILE_NAME_PATTERN;
    private static final Pattern TRANSLATE_RESPONSE_PATTERN;
    private static final Pattern VALUES_DIR_PATTERN;
    private static final String APACHE_2_LICENSE = "<!--\n Copyright (C) 2011 ZXing authors\n\n Licensed under the Apache License, Version 2.0 (the \"License\");\n you may not use this file except in compliance with the License.\n You may obtain a copy of the License at\n\n      http://www.apache.org/licenses/LICENSE-2.0\n\n Unless required by applicable law or agreed to in writing, software\n distributed under the License is distributed on an \"AS IS\" BASIS,\n WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n See the License for the specific language governing permissions and\n limitations under the License.\n -->\n";
    private static final Map<String, String> LANGUAGE_CODE_MASSAGINGS;
    
    private StringsResourceTranslator() {
    }
    
    public static void main(final String[] args) throws IOException {
        final File resDir = new File(args[0]);
        final File valueDir = new File(resDir, "values");
        final File stringsFile = new File(valueDir, "strings.xml");
        final Collection<String> forceRetranslation = Arrays.asList(args).subList(1, args.length);
        final File[] arr$;
        final File[] translatedValuesDirs = arr$ = resDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File file) {
                return file.isDirectory() && StringsResourceTranslator.VALUES_DIR_PATTERN.matcher(file.getName()).matches();
            }
        });
        for (final File translatedValuesDir : arr$) {
            final File translatedStringsFile = new File(translatedValuesDir, "strings.xml");
            translate(stringsFile, translatedStringsFile, forceRetranslation);
        }
    }
    
    private static void translate(final File englishFile, final File translatedFile, final Collection<String> forceRetranslation) throws IOException {
        final SortedMap<String, String> english = readLines(englishFile);
        final SortedMap<String, String> translated = readLines(translatedFile);
        final String parentName = translatedFile.getParentFile().getName();
        final Matcher stringsFileNameMatcher = StringsResourceTranslator.STRINGS_FILE_NAME_PATTERN.matcher(parentName);
        stringsFileNameMatcher.find();
        String language = stringsFileNameMatcher.group(1);
        final String massagedLanguage = StringsResourceTranslator.LANGUAGE_CODE_MASSAGINGS.get(language);
        if (massagedLanguage != null) {
            language = massagedLanguage;
        }
        System.out.println("Translating " + language);
        final File resultTempFile = File.createTempFile(parentName, ".xml");
        boolean anyChange = false;
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(resultTempFile), StringsResourceTranslator.UTF8);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<!--\n Copyright (C) 2011 ZXing authors\n\n Licensed under the Apache License, Version 2.0 (the \"License\");\n you may not use this file except in compliance with the License.\n You may obtain a copy of the License at\n\n      http://www.apache.org/licenses/LICENSE-2.0\n\n Unless required by applicable law or agreed to in writing, software\n distributed under the License is distributed on an \"AS IS\" BASIS,\n WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n See the License for the specific language governing permissions and\n limitations under the License.\n -->\n");
            out.write("<resources>\n");
            for (final Map.Entry<String, String> englishEntry : english.entrySet()) {
                final String key = englishEntry.getKey();
                final String value = englishEntry.getValue();
                out.write("  <string name=\"");
                out.write(key);
                out.write(34);
                if (value.contains("%s") || value.contains("%f")) {
                    out.write(" formatted=\"false\"");
                }
                out.write(62);
                String translatedString = translated.get(key);
                if (translatedString == null || forceRetranslation.contains(key)) {
                    anyChange = true;
                    translatedString = translateString(value, language);
                }
                out.write(translatedString);
                out.write("</string>\n");
            }
            out.write("</resources>\n");
            out.flush();
        }
        finally {
            quietClose(out);
        }
        if (anyChange) {
            System.out.println("  Writing translations");
            translatedFile.delete();
            resultTempFile.renameTo(translatedFile);
        }
    }
    
    static String translateString(final String english, String language) throws IOException {
        if ("en".equals(language)) {
            return english;
        }
        final String massagedLanguage = StringsResourceTranslator.LANGUAGE_CODE_MASSAGINGS.get(language);
        if (massagedLanguage != null) {
            language = massagedLanguage;
        }
        System.out.println("  Need translation for " + english);
        final URL translateURL = new URL("https://www.googleapis.com/language/translate/v2?key=INSERT-YOUR-KEY&q=" + URLEncoder.encode(english, "UTF-8") + "&source=en&target=" + language);
        final CharSequence translateResult = fetch(translateURL);
        final Matcher m = StringsResourceTranslator.TRANSLATE_RESPONSE_PATTERN.matcher(translateResult);
        if (!m.find()) {
            System.err.println("No translate result");
            System.err.println(translateResult);
            return english;
        }
        String translation = m.group(1);
        System.out.println("  Got translation " + translation);
        translation = translation.replaceAll("\\\\u0026quot;", "\"");
        translation = translation.replaceAll("\\\\u0026#39;", "'");
        translation = translation.replaceAll("\\\\u200b", "");
        translation = translation.replaceAll("&amp;quot;", "\"");
        translation = translation.replaceAll("&amp;#39;", "'");
        return translation;
    }
    
    private static CharSequence fetch(final URL translateURL) throws IOException {
        final URLConnection connection = translateURL.openConnection();
        connection.connect();
        final StringBuilder translateResult = new StringBuilder(200);
        Reader in = null;
        try {
            in = new InputStreamReader(connection.getInputStream(), StringsResourceTranslator.UTF8);
            final char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = in.read(buffer)) > 0) {
                translateResult.append(buffer, 0, charsRead);
            }
        }
        finally {
            quietClose(in);
        }
        return translateResult;
    }
    
    private static SortedMap<String, String> readLines(final File file) throws IOException {
        final SortedMap<String, String> entries = new TreeMap<String, String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StringsResourceTranslator.UTF8));
            String line;
            while ((line = reader.readLine()) != null) {
                final Matcher m = StringsResourceTranslator.ENTRY_PATTERN.matcher(line);
                if (m.find()) {
                    final String key = m.group(1);
                    final String value = m.group(2);
                    entries.put(key, value);
                }
            }
            return entries;
        }
        finally {
            quietClose(reader);
        }
    }
    
    private static void quietClose(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
        ENTRY_PATTERN = Pattern.compile("<string name=\"([^\"]+)\".*>([^<]+)</string>");
        STRINGS_FILE_NAME_PATTERN = Pattern.compile("values-(.+)");
        TRANSLATE_RESPONSE_PATTERN = Pattern.compile("translatedText\":\\s*\"([^\"]+)\"");
        VALUES_DIR_PATTERN = Pattern.compile("values-[a-z]{2}(-[a-zA-Z]{2,3})?");
        (LANGUAGE_CODE_MASSAGINGS = new HashMap<String, String>(3)).put("zh-rCN", "zh-cn");
        StringsResourceTranslator.LANGUAGE_CODE_MASSAGINGS.put("zh-rTW", "zh-tw");
    }
}
