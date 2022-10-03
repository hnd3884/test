package com.sun.xml.internal.fastinfoset.tools;

import javax.xml.parsers.SAXParser;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;
import javax.xml.parsers.SAXParserFactory;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.ContiguousCharArrayArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;

public class PrintTable
{
    public static void printVocabulary(final ParserVocabulary vocabulary) {
        printArray("Attribute Name Table", vocabulary.attributeName);
        printArray("Attribute Value Table", vocabulary.attributeValue);
        printArray("Character Content Chunk Table", vocabulary.characterContentChunk);
        printArray("Element Name Table", vocabulary.elementName);
        printArray("Local Name Table", vocabulary.localName);
        printArray("Namespace Name Table", vocabulary.namespaceName);
        printArray("Other NCName Table", vocabulary.otherNCName);
        printArray("Other String Table", vocabulary.otherString);
        printArray("Other URI Table", vocabulary.otherURI);
        printArray("Prefix Table", vocabulary.prefix);
    }
    
    public static void printArray(final String title, final StringArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }
    
    public static void printArray(final String title, final PrefixArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getArray()[i]);
        }
    }
    
    public static void printArray(final String title, final CharArrayArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + (Object)a.getArray()[i]);
        }
    }
    
    public static void printArray(final String title, final ContiguousCharArrayArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            System.out.println("" + (i + 1) + ": " + a.getString(i));
        }
    }
    
    public static void printArray(final String title, final QualifiedNameArray a) {
        System.out.println(title);
        for (int i = 0; i < a.getSize(); ++i) {
            final QualifiedName name = a.getArray()[i];
            System.out.println("" + (name.index + 1) + ": {" + name.namespaceName + "}" + name.prefix + ":" + name.localName);
        }
    }
    
    public static void main(final String[] args) {
        try {
            final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            final SAXParser saxParser = saxParserFactory.newSAXParser();
            final ParserVocabulary referencedVocabulary = new ParserVocabulary();
            final VocabularyGenerator vocabularyGenerator = new VocabularyGenerator(referencedVocabulary);
            final File f = new File(args[0]);
            saxParser.parse(f, vocabularyGenerator);
            printVocabulary(referencedVocabulary);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
