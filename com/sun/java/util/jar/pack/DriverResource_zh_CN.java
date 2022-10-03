package com.sun.java.util.jar.pack;

import java.util.ListResourceBundle;

public class DriverResource_zh_CN extends ListResourceBundle
{
    public static final String VERSION = "VERSION";
    public static final String BAD_ARGUMENT = "BAD_ARGUMENT";
    public static final String BAD_OPTION = "BAD_OPTION";
    public static final String BAD_REPACK_OUTPUT = "BAD_REPACK_OUTPUT";
    public static final String DETECTED_ZIP_COMMENT = "DETECTED_ZIP_COMMENT";
    public static final String SKIP_FOR_REPACKED = "SKIP_FOR_REPACKED";
    public static final String WRITE_PACK_FILE = "WRITE_PACK_FILE";
    public static final String WRITE_PACKGZ_FILE = "WRITE_PACKGZ_FILE";
    public static final String SKIP_FOR_MOVE_FAILED = "SKIP_FOR_MOVE_FAILED";
    public static final String PACK_HELP = "PACK_HELP";
    public static final String UNPACK_HELP = "UNPACK_HELP";
    public static final String MORE_INFO = "MORE_INFO";
    public static final String DUPLICATE_OPTION = "DUPLICATE_OPTION";
    public static final String BAD_SPEC = "BAD_SPEC";
    private static final Object[][] resource;
    
    @Override
    protected Object[][] getContents() {
        return DriverResource_zh_CN.resource;
    }
    
    static {
        resource = new Object[][] { { "VERSION", "{0}\u7248\u672c {1}" }, { "BAD_ARGUMENT", "\u9519\u8bef\u53c2\u6570: {0}" }, { "BAD_OPTION", "\u9519\u8bef\u9009\u9879: {0}={1}" }, { "BAD_REPACK_OUTPUT", "--repack \u8f93\u51fa\u9519\u8bef: {0}" }, { "DETECTED_ZIP_COMMENT", "\u68c0\u6d4b\u5230 ZIP \u6ce8\u91ca: {0}" }, { "SKIP_FOR_REPACKED", "\u7531\u4e8e\u5df2\u91cd\u65b0\u6253\u5305\u800c\u8df3\u8fc7: {0}" }, { "WRITE_PACK_FILE", "\u8981\u5199\u5165 *.pack \u6587\u4ef6, \u8bf7\u6307\u5b9a --no-gzip: {0}" }, { "WRITE_PACKGZ_FILE", "\u8981\u5199\u5165 *.pack.gz \u6587\u4ef6, \u8bf7\u6307\u5b9a --gzip: {0}" }, { "SKIP_FOR_MOVE_FAILED", "\u7531\u4e8e\u79fb\u52a8\u5931\u8d25\u800c\u8df3\u8fc7\u91cd\u65b0\u6253\u5305: {0}" }, { "PACK_HELP", { "\u7528\u6cd5:  pack200 [-opt... | --option=value]... x.pack[.gz] y.jar", "", "\u6253\u5305\u9009\u9879", "  -g, --no-gzip                   \u8f93\u51fa\u65e0\u683c\u5f0f\u7684 *.pack \u6587\u4ef6, \u4e0d\u538b\u7f29", "  --gzip                          (\u9ed8\u8ba4\u503c) \u4f7f\u7528 gzip \u5bf9\u6253\u5305\u8fdb\u884c\u540e\u5904\u7406", "  -G, --strip-debug               \u6253\u5305\u65f6\u5220\u9664\u8c03\u8bd5\u5c5e\u6027", "  -O, --no-keep-file-order        \u4e0d\u4f20\u8f93\u6587\u4ef6\u6392\u5e8f\u4fe1\u606f", "  --keep-file-order               (\u9ed8\u8ba4\u503c) \u4fdd\u7559\u8f93\u5165\u6587\u4ef6\u6392\u5e8f", "  -S{N}, --segment-limit={N}      \u8f93\u51fa\u6bb5\u9650\u5236 (\u9ed8\u8ba4\u503c N=1Mb)", "  -E{N}, --effort={N}             \u6253\u5305\u6548\u679c (\u9ed8\u8ba4\u503c N=5)", "  -H{h}, --deflate-hint={h}       \u4f20\u8f93\u538b\u7f29\u63d0\u793a: true, false \u6216 keep (\u9ed8\u8ba4\u503c)", "  -m{V}, --modification-time={V}  \u4f20\u8f93 modtimes: latest \u6216 keep (\u9ed8\u8ba4\u503c)", "  -P{F}, --pass-file={F}          \u4f20\u8f93\u672a\u89e3\u538b\u7f29\u7684\u7ed9\u5b9a\u8f93\u5165\u5143\u7d20", "  -U{a}, --unknown-attribute={a}  \u672a\u77e5\u5c5e\u6027\u64cd\u4f5c: error, strip \u6216 pass (\u9ed8\u8ba4\u503c)", "  -C{N}={L}, --class-attribute={N}={L}  (\u7528\u6237\u5b9a\u4e49\u7684\u5c5e\u6027)", "  -F{N}={L}, --field-attribute={N}={L}  (\u7528\u6237\u5b9a\u4e49\u7684\u5c5e\u6027)", "  -M{N}={L}, --method-attribute={N}={L} (\u7528\u6237\u5b9a\u4e49\u7684\u5c5e\u6027)", "  -D{N}={L}, --code-attribute={N}={L}   (\u7528\u6237\u5b9a\u4e49\u7684\u5c5e\u6027)", "  -f{F}, --config-file={F}        \u8bfb\u53d6\u6587\u4ef6 F \u7684 Pack200.Packer \u5c5e\u6027", "  -v, --verbose                   \u63d0\u9ad8\u7a0b\u5e8f\u8be6\u7ec6\u7a0b\u5ea6", "  -q, --quiet                     \u5c06\u8be6\u7ec6\u7a0b\u5ea6\u8bbe\u7f6e\u4e3a\u6700\u4f4e\u7ea7\u522b", "  -l{F}, --log-file={F}           \u8f93\u51fa\u5230\u7ed9\u5b9a\u65e5\u5fd7\u6587\u4ef6, \u6216\u5bf9\u4e8e System.out \u6307\u5b9a '-'", "  -?, -h, --help                  \u8f93\u51fa\u6b64\u6d88\u606f", "  -V, --version                   \u8f93\u51fa\u7a0b\u5e8f\u7248\u672c", "  -J{X}                           \u5c06\u9009\u9879 X \u4f20\u9012\u7ed9\u57fa\u7840 Java VM", "", "\u6ce8:", "  -P, -C, -F, -M \u548c -D \u9009\u9879\u7d2f\u8ba1\u3002", "  \u793a\u4f8b\u5c5e\u6027\u5b9a\u4e49:  -C SourceFile=RUH\u3002", "  Config. \u6587\u4ef6\u5c5e\u6027\u7531 Pack200 API \u5b9a\u4e49\u3002", "  \u6709\u5173 -S, -E, -H-, -m, -U \u503c\u7684\u542b\u4e49, \u8bf7\u53c2\u9605 Pack200 API\u3002", "  \u5e03\u5c40\u5b9a\u4e49 (\u4f8b\u5982 RUH) \u7531 JSR 200 \u5b9a\u4e49\u3002", "", "\u91cd\u65b0\u6253\u5305\u6a21\u5f0f\u901a\u8fc7\u6253\u5305/\u89e3\u5305\u5468\u671f\u66f4\u65b0 JAR \u6587\u4ef6:", "    pack200 [-r|--repack] [-opt | --option=value]... [repackedy.jar] y.jar\n" } }, { "UNPACK_HELP", { "\u7528\u6cd5:  unpack200 [-opt... | --option=value]... x.pack[.gz] y.jar\n", "", "\u89e3\u5305\u9009\u9879", "  -H{h}, --deflate-hint={h}     \u8986\u76d6\u5df2\u4f20\u8f93\u7684\u538b\u7f29\u63d0\u793a: true, false \u6216 keep (\u9ed8\u8ba4\u503c)", "  -r, --remove-pack-file        \u89e3\u5305\u4e4b\u540e\u5220\u9664\u8f93\u5165\u6587\u4ef6", "  -v, --verbose                   \u63d0\u9ad8\u7a0b\u5e8f\u8be6\u7ec6\u7a0b\u5ea6", "  -q, --quiet                     \u5c06\u8be6\u7ec6\u7a0b\u5ea6\u8bbe\u7f6e\u4e3a\u6700\u4f4e\u7ea7\u522b", "  -l{F}, --log-file={F}         \u8f93\u51fa\u5230\u7ed9\u5b9a\u65e5\u5fd7\u6587\u4ef6, \u6216\u5bf9\u4e8e System.out \u6307\u5b9a '-'", "  -?, -h, --help                \u8f93\u51fa\u6b64\u6d88\u606f", "  -V, --version                 \u8f93\u51fa\u7a0b\u5e8f\u7248\u672c", "  -J{X}                         \u5c06\u9009\u9879 X \u4f20\u9012\u7ed9\u57fa\u7840 Java VM" } }, { "MORE_INFO", "(\u6709\u5173\u8be6\u7ec6\u4fe1\u606f, \u8bf7\u8fd0\u884c {0} --help\u3002)" }, { "DUPLICATE_OPTION", "\u91cd\u590d\u7684\u9009\u9879: {0}" }, { "BAD_SPEC", "{0}\u7684\u89c4\u8303\u9519\u8bef: {1}" } };
    }
}
