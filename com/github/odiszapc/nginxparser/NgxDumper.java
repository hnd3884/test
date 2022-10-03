package com.github.odiszapc.nginxparser;

import java.util.Iterator;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;

public class NgxDumper
{
    private NgxConfig config;
    private static final int PAD_SIZE = 2;
    private static final String PAD_SYMBOL = "  ";
    private static final String LBRACE = "{";
    private static final String RBRACE = "}";
    private static final String LF = "\n";
    private static final String CRLF = "\r\n";
    
    public NgxDumper(final NgxConfig config) {
        this.config = config;
    }
    
    public String dump() {
        final StringWriter stringWriter = new StringWriter();
        this.writeToStream(this.config, new PrintWriter(stringWriter), 0);
        return stringWriter.toString();
    }
    
    public void dump(final OutputStream outputStream) {
        this.writeToStream(this.config, new PrintWriter(outputStream), 0);
    }
    
    private void writeToStream(final NgxBlock ngxBlock, final PrintWriter printWriter, final int n) {
        for (final NgxEntry ngxEntry : ngxBlock) {
            switch (NgxEntryType.fromClass(((NgxIfBlock)ngxEntry).getClass())) {
                case BLOCK: {
                    final NgxBlock ngxBlock2 = (NgxBlock)ngxEntry;
                    printWriter.append(this.getOffset(n)).append(ngxBlock2.toString()).append(this.getLineEnding());
                    this.writeToStream(ngxBlock2, printWriter, n + 1);
                    printWriter.append(this.getOffset(n)).append("}").append(this.getLineEnding());
                    continue;
                }
                case IF: {
                    final NgxIfBlock ngxIfBlock = (NgxIfBlock)ngxEntry;
                    printWriter.append(this.getOffset(n)).append(ngxIfBlock.toString()).append(this.getLineEnding());
                    this.writeToStream(ngxIfBlock, printWriter, n + 1);
                    printWriter.append(this.getOffset(n)).append("}").append(this.getLineEnding());
                    continue;
                }
                case COMMENT:
                case PARAM: {
                    printWriter.append(this.getOffset(n)).append(ngxEntry.toString()).append(this.getLineEnding());
                    continue;
                }
            }
        }
        printWriter.flush();
    }
    
    public String getOffset(final int n) {
        String string = "";
        for (int i = 0; i < n; ++i) {
            string += "  ";
        }
        return string;
    }
    
    public String getLineEnding() {
        return "\n";
    }
}
