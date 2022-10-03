package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_zh_TW extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "error.bad.cflag", "'c' \u65d7\u6a19\u8981\u6c42\u6307\u5b9a\u8cc7\u8a0a\u6e05\u55ae\u6216\u8f38\u5165\u6a94\u6848\uff01" }, { "error.bad.eflag", "\u7121\u6cd5\u540c\u6642\u6307\u5b9a 'e' \u65d7\u6a19\u548c\u5177\u6709 'Main-Class' \u5c6c\u6027\u7684\n\u8cc7\u8a0a\u6e05\u55ae\uff01" }, { "error.bad.option", "\u5176\u4e2d\u4e00\u500b\u9078\u9805 -{ctxu} \u5fc5\u9808\u52a0\u4ee5\u6307\u5b9a\u3002" }, { "error.bad.uflag", "'u' \u65d7\u6a19\u8981\u6c42\u6307\u5b9a\u8cc7\u8a0a\u6e05\u55ae\u3001'e' \u65d7\u6a19\u6216\u8f38\u5165\u6a94\u6848\uff01" }, { "error.cant.open", "\u7121\u6cd5\u958b\u555f: {0} " }, { "error.create.dir", "{0} : \u7121\u6cd5\u5efa\u7acb\u76ee\u9304" }, { "error.create.tempfile", "\u7121\u6cd5\u5efa\u7acb\u66ab\u5b58\u6a94\u6848" }, { "error.illegal.option", "\u7121\u6548\u7684\u9078\u9805: {0}" }, { "error.incorrect.length", "\u8655\u7406 {0} \u6642\u9577\u5ea6\u4e0d\u6b63\u78ba" }, { "error.nosuch.fileordir", "{0} : \u6c92\u6709\u9019\u985e\u6a94\u6848\u6216\u76ee\u9304" }, { "error.write.file", "\u5beb\u5165\u73fe\u6709\u7684 jar \u6a94\u6848\u6642\u767c\u751f\u932f\u8aa4" }, { "out.added.manifest", "\u5df2\u65b0\u589e\u8cc7\u8a0a\u6e05\u55ae" }, { "out.adding", "\u65b0\u589e: {0}" }, { "out.create", "  \u5efa\u7acb: {0}" }, { "out.deflated", "(\u58d3\u7e2e {0}%)" }, { "out.extracted", "\u64f7\u53d6: {0}" }, { "out.ignore.entry", "\u5ffd\u7565\u9805\u76ee {0}" }, { "out.inflated", " \u64f4\u5c55: {0}" }, { "out.size", " (\u8b80={0})(\u5beb={1})" }, { "out.stored", "(\u5132\u5b58 0%)" }, { "out.update.manifest", "\u5df2\u66f4\u65b0\u8cc7\u8a0a\u6e05\u55ae" }, { "usage", "\u7528\u6cd5: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] \u6a94\u6848 ...\n\u9078\u9805:\n    -c  \u5efa\u7acb\u65b0\u7684\u6b78\u6a94\n    -t  \u5217\u51fa\u6b78\u6a94\u7684\u76ee\u9304\n    -x  \u5f9e\u6b78\u6a94\u4e2d\u64f7\u53d6\u6307\u5b9a (\u6216\u6240\u6709) \u6a94\u6848\n    -u  \u66f4\u65b0\u73fe\u6709\u6b78\u6a94\n    -v  \u5728\u6a19\u6e96\u8f38\u51fa\u4e2d\u7522\u751f\u8a73\u7d30\u8f38\u51fa\n    -f  \u6307\u5b9a\u6b78\u6a94\u6a94\u6848\u540d\u7a31\n    -m  \u5305\u542b\u6307\u5b9a\u8cc7\u8a0a\u6e05\u55ae\u4e2d\u7684\u8cc7\u8a0a\u6e05\u55ae\u8cc7\u8a0a\n    -n  \u5728\u5efa\u7acb\u65b0\u6b78\u6a94\u4e4b\u5f8c\u57f7\u884c Pack200 \u6b63\u898f\u5316\n    -e  \u70ba\u5df2\u96a8\u9644\u65bc\u53ef\u57f7\u884c jar \u6a94\u6848\u4e2d\u7684\u7368\u7acb\u61c9\u7528\u7a0b\u5f0f\n        \u6307\u5b9a\u61c9\u7528\u7a0b\u5f0f\u9032\u5165\u9ede\n    -0  \u50c5\u5132\u5b58; \u4e0d\u4f7f\u7528 ZIP \u58d3\u7e2e\u65b9\u5f0f\n    -P  \u4fdd\u7559\u6a94\u6848\u540d\u7a31\u524d\u9762\u7684 '/' (\u7d55\u5c0d\u8def\u5f91) \u548c \"..\" (\u4e0a\u5c64\u76ee\u9304) \u5143\u4ef6\n    -M  \u4e0d\u70ba\u9805\u76ee\u5efa\u7acb\u8cc7\u8a0a\u6e05\u55ae\u6a94\u6848\n    -i  \u70ba\u6307\u5b9a\u7684 jar \u6a94\u6848\u7522\u751f\u7d22\u5f15\u8cc7\u8a0a\n    -C  \u8b8a\u66f4\u81f3\u6307\u5b9a\u76ee\u9304\u4e26\u5305\u542b\u5f8c\u9762\u6240\u5217\u7684\u6a94\u6848\n\u5982\u679c\u6709\u4efb\u4f55\u6a94\u6848\u662f\u76ee\u9304\uff0c\u5247\u6703\u5c0d\u5176\u9032\u884c\u905e\u8ff4\u8655\u7406\u3002\n\u8cc7\u8a0a\u6e05\u55ae\u6a94\u6848\u540d\u7a31\u3001\u6b78\u6a94\u6a94\u6848\u540d\u7a31\u548c\u9032\u5165\u9ede\u540d\u7a31\n\u7684\u6307\u5b9a\u9806\u5e8f\u8207\u6307\u5b9a 'm' \u65d7\u6a19\u3001'f' \u65d7\u6a19\u548c 'e' \u65d7\u6a19\u7684\u9806\u5e8f\u76f8\u540c\u3002\n\n\u7bc4\u4f8b 1: \u5c07\u5169\u500b\u985e\u5225\u6a94\u6848\u6b78\u6a94\u81f3\u540d\u70ba classes.jar \u7684\u6b78\u6a94\u4e2d: \n       jar cvf classes.jar Foo.class Bar.class\n\u7bc4\u4f8b 2: \u4f7f\u7528\u73fe\u6709\u8cc7\u8a0a\u6e05\u55ae\u6a94\u6848 'mymanifest' \u4e26\u5c07\n           foo/ \u76ee\u9304\u4e2d\u7684\u6240\u6709\u6a94\u6848\u6b78\u6a94\u81f3 'classes.jar' \u4e2d: \n       jar cvfm classes.jar mymanifest -C foo/ .\n" } };
    }
}
