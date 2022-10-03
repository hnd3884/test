package com.github.odiszapc.nginxparser;

import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import com.github.odiszapc.nginxparser.antlr.NginxListenerImpl;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.TokenStream;
import com.github.odiszapc.nginxparser.antlr.NginxParser;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStream;
import com.github.odiszapc.nginxparser.antlr.NginxLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import com.github.odiszapc.nginxparser.javacc.ParseException;
import com.github.odiszapc.nginxparser.javacc.NginxConfigParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

public class NgxConfig extends NgxBlock
{
    public static final Class<? extends NgxEntry> PARAM;
    public static final Class<? extends NgxEntry> COMMENT;
    public static final Class<? extends NgxEntry> BLOCK;
    public static final Class<? extends NgxEntry> IF;
    
    public static NgxConfig read(final String s) throws IOException {
        return read(new FileInputStream(s));
    }
    
    public static NgxConfig read(final InputStream inputStream) throws IOException {
        return readAntlr(inputStream);
    }
    
    public static NgxConfig readJavaCC(final InputStream inputStream) throws IOException, ParseException {
        return new NginxConfigParser(inputStream).parse();
    }
    
    public static NgxConfig readAntlr(final InputStream inputStream) throws IOException {
        final NginxParser nginxParser = new NginxParser((TokenStream)new CommonTokenStream((TokenSource)new NginxLexer((CharStream)new ANTLRInputStream(inputStream))));
        final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        final NginxParser.ConfigContext config = nginxParser.config();
        final NginxListenerImpl nginxListenerImpl = new NginxListenerImpl();
        parseTreeWalker.walk((ParseTreeListener)nginxListenerImpl, (ParseTree)config);
        return nginxListenerImpl.getResult();
    }
    
    @Override
    public Collection<NgxToken> getTokens() {
        throw new IllegalStateException("Not implemented");
    }
    
    @Override
    public void addValue(final NgxToken ngxToken) {
        throw new IllegalStateException("Not implemented");
    }
    
    @Override
    public String toString() {
        return "Nginx Config (" + this.getEntries().size() + " entries)";
    }
    
    static {
        PARAM = NgxParam.class;
        COMMENT = NgxComment.class;
        BLOCK = NgxBlock.class;
        IF = NgxIfBlock.class;
    }
}
