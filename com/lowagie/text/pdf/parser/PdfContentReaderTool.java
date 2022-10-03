package com.lowagie.text.pdf.parser;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfObject;
import java.util.Iterator;
import java.util.List;
import com.lowagie.text.pdf.PdfName;
import java.util.ArrayList;
import com.lowagie.text.pdf.PdfDictionary;

public class PdfContentReaderTool
{
    public static String getDictionaryDetail(final PdfDictionary dic) {
        return getDictionaryDetail(dic, 0);
    }
    
    public static String getDictionaryDetail(final PdfDictionary dic, final int depth) {
        final StringBuffer builder = new StringBuffer();
        builder.append('(');
        final List subDictionaries = new ArrayList();
        for (final PdfName key : dic.getKeys()) {
            final PdfObject val = dic.getDirectObject(key);
            if (val.isDictionary()) {
                subDictionaries.add(key);
            }
            builder.append(key);
            builder.append('=');
            builder.append(val);
            builder.append(", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(')');
        for (final PdfName pdfSubDictionaryName : subDictionaries) {
            builder.append('\n');
            for (int j = 0; j < depth + 1; ++j) {
                builder.append('\t');
            }
            builder.append("Subdictionary ");
            builder.append(pdfSubDictionaryName);
            builder.append(" = ");
            builder.append(getDictionaryDetail(dic.getAsDict(pdfSubDictionaryName), depth + 1));
        }
        return builder.toString();
    }
    
    public static void listContentStreamForPage(final PdfReader reader, final int pageNum, final PrintWriter out) throws IOException {
        out.println("==============Page " + pageNum + "====================");
        out.println("- - - - - Dictionary - - - - - -");
        final PdfDictionary pageDictionary = reader.getPageN(pageNum);
        out.println(getDictionaryDetail(pageDictionary));
        out.println("- - - - - Content Stream - - - - - -");
        final RandomAccessFileOrArray f = reader.getSafeFile();
        final byte[] contentBytes = reader.getPageContent(pageNum, f);
        f.close();
        final InputStream is = new ByteArrayInputStream(contentBytes);
        int ch;
        while ((ch = is.read()) != -1) {
            out.print((char)ch);
        }
        out.println("- - - - - Text Extraction - - - - - -");
        final PdfTextExtractor extractor = new PdfTextExtractor(reader, new MarkedUpTextAssembler(reader));
        final String extractedText = extractor.getTextFromPage(pageNum);
        if (extractedText.length() != 0) {
            out.println(extractedText);
        }
        else {
            out.println("No text found on page " + pageNum);
        }
        out.println();
    }
    
    public static void listContentStream(final File pdfFile, final PrintWriter out) throws IOException {
        final PdfReader reader = new PdfReader(pdfFile.getCanonicalPath());
        for (int maxPageNum = reader.getNumberOfPages(), pageNum = 1; pageNum <= maxPageNum; ++pageNum) {
            listContentStreamForPage(reader, pageNum, out);
        }
    }
    
    public static void listContentStream(final File pdfFile, final int pageNum, final PrintWriter out) throws IOException {
        final PdfReader reader = new PdfReader(pdfFile.getCanonicalPath());
        listContentStreamForPage(reader, pageNum, out);
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length < 1 || args.length > 3) {
                System.out.println("Usage:  PdfContentReaderTool <pdf file> [<output file>|stdout] [<page num>]");
                return;
            }
            PrintWriter writer = new PrintWriter(System.out);
            if (args.length >= 2 && args[1].compareToIgnoreCase("stdout") != 0) {
                System.out.println("Writing PDF content to " + args[1]);
                writer = new PrintWriter(new FileOutputStream(new File(args[1])));
            }
            int pageNum = -1;
            if (args.length >= 3) {
                pageNum = Integer.parseInt(args[2]);
            }
            if (pageNum == -1) {
                listContentStream(new File(args[0]), writer);
            }
            else {
                listContentStream(new File(args[0]), pageNum, writer);
            }
            writer.flush();
            if (args.length >= 2) {
                writer.close();
                System.out.println("Finished writing content to " + args[1]);
            }
        }
        catch (final Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
