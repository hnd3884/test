package jdk.nashorn.internal.objects;

import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.PrototypeObject;

final class NativeString$Prototype extends PrototypeObject
{
    private Object toString;
    private Object valueOf;
    private Object charAt;
    private Object charCodeAt;
    private Object concat;
    private Object indexOf;
    private Object lastIndexOf;
    private Object localeCompare;
    private Object match;
    private Object replace;
    private Object search;
    private Object slice;
    private Object split;
    private Object substr;
    private Object substring;
    private Object toLowerCase;
    private Object toLocaleLowerCase;
    private Object toUpperCase;
    private Object toLocaleUpperCase;
    private Object trim;
    private Object trimLeft;
    private Object trimRight;
    private static final PropertyMap $nasgenmap$;
    
    public Object G$toString() {
        return this.toString;
    }
    
    public void S$toString(final Object toString) {
        this.toString = toString;
    }
    
    public Object G$valueOf() {
        return this.valueOf;
    }
    
    public void S$valueOf(final Object valueOf) {
        this.valueOf = valueOf;
    }
    
    public Object G$charAt() {
        return this.charAt;
    }
    
    public void S$charAt(final Object charAt) {
        this.charAt = charAt;
    }
    
    public Object G$charCodeAt() {
        return this.charCodeAt;
    }
    
    public void S$charCodeAt(final Object charCodeAt) {
        this.charCodeAt = charCodeAt;
    }
    
    public Object G$concat() {
        return this.concat;
    }
    
    public void S$concat(final Object concat) {
        this.concat = concat;
    }
    
    public Object G$indexOf() {
        return this.indexOf;
    }
    
    public void S$indexOf(final Object indexOf) {
        this.indexOf = indexOf;
    }
    
    public Object G$lastIndexOf() {
        return this.lastIndexOf;
    }
    
    public void S$lastIndexOf(final Object lastIndexOf) {
        this.lastIndexOf = lastIndexOf;
    }
    
    public Object G$localeCompare() {
        return this.localeCompare;
    }
    
    public void S$localeCompare(final Object localeCompare) {
        this.localeCompare = localeCompare;
    }
    
    public Object G$match() {
        return this.match;
    }
    
    public void S$match(final Object match) {
        this.match = match;
    }
    
    public Object G$replace() {
        return this.replace;
    }
    
    public void S$replace(final Object replace) {
        this.replace = replace;
    }
    
    public Object G$search() {
        return this.search;
    }
    
    public void S$search(final Object search) {
        this.search = search;
    }
    
    public Object G$slice() {
        return this.slice;
    }
    
    public void S$slice(final Object slice) {
        this.slice = slice;
    }
    
    public Object G$split() {
        return this.split;
    }
    
    public void S$split(final Object split) {
        this.split = split;
    }
    
    public Object G$substr() {
        return this.substr;
    }
    
    public void S$substr(final Object substr) {
        this.substr = substr;
    }
    
    public Object G$substring() {
        return this.substring;
    }
    
    public void S$substring(final Object substring) {
        this.substring = substring;
    }
    
    public Object G$toLowerCase() {
        return this.toLowerCase;
    }
    
    public void S$toLowerCase(final Object toLowerCase) {
        this.toLowerCase = toLowerCase;
    }
    
    public Object G$toLocaleLowerCase() {
        return this.toLocaleLowerCase;
    }
    
    public void S$toLocaleLowerCase(final Object toLocaleLowerCase) {
        this.toLocaleLowerCase = toLocaleLowerCase;
    }
    
    public Object G$toUpperCase() {
        return this.toUpperCase;
    }
    
    public void S$toUpperCase(final Object toUpperCase) {
        this.toUpperCase = toUpperCase;
    }
    
    public Object G$toLocaleUpperCase() {
        return this.toLocaleUpperCase;
    }
    
    public void S$toLocaleUpperCase(final Object toLocaleUpperCase) {
        this.toLocaleUpperCase = toLocaleUpperCase;
    }
    
    public Object G$trim() {
        return this.trim;
    }
    
    public void S$trim(final Object trim) {
        this.trim = trim;
    }
    
    public Object G$trimLeft() {
        return this.trimLeft;
    }
    
    public void S$trimLeft(final Object trimLeft) {
        this.trimLeft = trimLeft;
    }
    
    public Object G$trimRight() {
        return this.trimRight;
    }
    
    public void S$trimRight(final Object trimRight) {
        this.trimRight = trimRight;
    }
    
    static {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'jdk/nashorn/internal/objects/NativeString$Prototype.<clinit>:()V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:67)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:729)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:194)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodHandleEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1590)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:299)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:63)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    NativeString$Prototype() {
        // 
        // This method could not be decompiled.
        // 
        // Could not show original bytecode, likely due to the same error.
        // 
        // The error that occurred was:
        // 
        // com.strobel.assembler.metadata.MethodBodyParseException: An error occurred while parsing the bytecode of method 'jdk/nashorn/internal/objects/NativeString$Prototype.<init>:()V'.
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:67)
        //     at com.strobel.assembler.metadata.MethodDefinition.tryLoadBody(MethodDefinition.java:729)
        //     at com.strobel.assembler.metadata.MethodDefinition.getBody(MethodDefinition.java:83)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:194)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createConstructor(AstBuilder.java:799)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:635)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // Caused by: java.lang.ClassCastException: com.strobel.assembler.ir.ConstantPool$MethodHandleEntry cannot be cast to com.strobel.assembler.ir.ConstantPool$ConstantEntry
        //     at com.strobel.assembler.ir.ConstantPool.lookupConstant(ConstantPool.java:120)
        //     at com.strobel.assembler.metadata.ClassFileReader$Scope.lookupConstant(ClassFileReader.java:1590)
        //     at com.strobel.assembler.metadata.MethodReader.readBodyCore(MethodReader.java:299)
        //     at com.strobel.assembler.metadata.MethodReader.readBody(MethodReader.java:63)
        //     ... 16 more
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    @Override
    public String getClassName() {
        return "String";
    }
}
