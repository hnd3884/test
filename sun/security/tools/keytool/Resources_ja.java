package sun.security.tools.keytool;

import java.util.ListResourceBundle;

public class Resources_ja extends ListResourceBundle
{
    private static final Object[][] contents;
    
    public Object[][] getContents() {
        return Resources_ja.contents;
    }
    
    static {
        contents = new Object[][] { { "NEWLINE", "\n" }, { "STAR", "*******************************************" }, { "STARNN", "*******************************************\n\n" }, { ".OPTION.", " [OPTION]..." }, { "Options.", "\u30aa\u30d7\u30b7\u30e7\u30f3:" }, { "Use.keytool.help.for.all.available.commands", "\u4f7f\u7528\u53ef\u80fd\u306a\u3059\u3079\u3066\u306e\u30b3\u30de\u30f3\u30c9\u306b\u3064\u3044\u3066\u306f\"keytool -help\"\u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Key.and.Certificate.Management.Tool", "\u30ad\u30fc\u304a\u3088\u3073\u8a3c\u660e\u66f8\u7ba1\u7406\u30c4\u30fc\u30eb" }, { "Commands.", "\u30b3\u30de\u30f3\u30c9:" }, { "Use.keytool.command.name.help.for.usage.of.command.name", "command_name\u306e\u4f7f\u7528\u65b9\u6cd5\u306b\u3064\u3044\u3066\u306f\"keytool -command_name -help\"\u3092\u4f7f\u7528\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Generates.a.certificate.request", "\u8a3c\u660e\u66f8\u30ea\u30af\u30a8\u30b9\u30c8\u3092\u751f\u6210\u3057\u307e\u3059" }, { "Changes.an.entry.s.alias", "\u30a8\u30f3\u30c8\u30ea\u306e\u5225\u540d\u3092\u5909\u66f4\u3057\u307e\u3059" }, { "Deletes.an.entry", "\u30a8\u30f3\u30c8\u30ea\u3092\u524a\u9664\u3057\u307e\u3059" }, { "Exports.certificate", "\u8a3c\u660e\u66f8\u3092\u30a8\u30af\u30b9\u30dd\u30fc\u30c8\u3057\u307e\u3059" }, { "Generates.a.key.pair", "\u9375\u30da\u30a2\u3092\u751f\u6210\u3057\u307e\u3059" }, { "Generates.a.secret.key", "\u79d8\u5bc6\u9375\u3092\u751f\u6210\u3057\u307e\u3059" }, { "Generates.certificate.from.a.certificate.request", "\u8a3c\u660e\u66f8\u30ea\u30af\u30a8\u30b9\u30c8\u304b\u3089\u8a3c\u660e\u66f8\u3092\u751f\u6210\u3057\u307e\u3059" }, { "Generates.CRL", "CRL\u3092\u751f\u6210\u3057\u307e\u3059" }, { "Generated.keyAlgName.secret.key", "{0}\u79d8\u5bc6\u9375\u3092\u751f\u6210\u3057\u307e\u3057\u305f" }, { "Generated.keysize.bit.keyAlgName.secret.key", "{0}\u30d3\u30c3\u30c8{1}\u79d8\u5bc6\u9375\u3092\u751f\u6210\u3057\u307e\u3057\u305f" }, { "Imports.entries.from.a.JDK.1.1.x.style.identity.database", "JDK 1.1.x-style\u30a2\u30a4\u30c7\u30f3\u30c6\u30a3\u30c6\u30a3\u30fb\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u304b\u3089\u30a8\u30f3\u30c8\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u307e\u3059" }, { "Imports.a.certificate.or.a.certificate.chain", "\u8a3c\u660e\u66f8\u307e\u305f\u306f\u8a3c\u660e\u66f8\u30c1\u30a7\u30fc\u30f3\u3092\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u307e\u3059" }, { "Imports.a.password", "\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u307e\u3059" }, { "Imports.one.or.all.entries.from.another.keystore", "\u5225\u306e\u30ad\u30fc\u30b9\u30c8\u30a2\u304b\u30891\u3064\u307e\u305f\u306f\u3059\u3079\u3066\u306e\u30a8\u30f3\u30c8\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u307e\u3059" }, { "Clones.a.key.entry", "\u9375\u30a8\u30f3\u30c8\u30ea\u306e\u30af\u30ed\u30fc\u30f3\u3092\u4f5c\u6210\u3057\u307e\u3059" }, { "Changes.the.key.password.of.an.entry", "\u30a8\u30f3\u30c8\u30ea\u306e\u9375\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5909\u66f4\u3057\u307e\u3059" }, { "Lists.entries.in.a.keystore", "\u30ad\u30fc\u30b9\u30c8\u30a2\u5185\u306e\u30a8\u30f3\u30c8\u30ea\u3092\u30ea\u30b9\u30c8\u3057\u307e\u3059" }, { "Prints.the.content.of.a.certificate", "\u8a3c\u660e\u66f8\u306e\u5185\u5bb9\u3092\u51fa\u529b\u3057\u307e\u3059" }, { "Prints.the.content.of.a.certificate.request", "\u8a3c\u660e\u66f8\u30ea\u30af\u30a8\u30b9\u30c8\u306e\u5185\u5bb9\u3092\u51fa\u529b\u3057\u307e\u3059" }, { "Prints.the.content.of.a.CRL.file", "CRL\u30d5\u30a1\u30a4\u30eb\u306e\u5185\u5bb9\u3092\u51fa\u529b\u3057\u307e\u3059" }, { "Generates.a.self.signed.certificate", "\u81ea\u5df1\u7f72\u540d\u578b\u8a3c\u660e\u66f8\u3092\u751f\u6210\u3057\u307e\u3059" }, { "Changes.the.store.password.of.a.keystore", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30b9\u30c8\u30a2\u30fb\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5909\u66f4\u3057\u307e\u3059" }, { "alias.name.of.the.entry.to.process", "\u51e6\u7406\u3059\u308b\u30a8\u30f3\u30c8\u30ea\u306e\u5225\u540d" }, { "destination.alias", "\u51fa\u529b\u5148\u306e\u5225\u540d" }, { "destination.key.password", "\u51fa\u529b\u5148\u30ad\u30fc\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "destination.keystore.name", "\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u540d" }, { "destination.keystore.password.protected", "\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u4fdd\u8b77\u5bfe\u8c61\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "destination.keystore.provider.name", "\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d7\u30ed\u30d0\u30a4\u30c0\u540d" }, { "destination.keystore.password", "\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "destination.keystore.type", "\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30bf\u30a4\u30d7" }, { "distinguished.name", "\u8b58\u5225\u540d" }, { "X.509.extension", "X.509\u62e1\u5f35" }, { "output.file.name", "\u51fa\u529b\u30d5\u30a1\u30a4\u30eb\u540d" }, { "input.file.name", "\u5165\u529b\u30d5\u30a1\u30a4\u30eb\u540d" }, { "key.algorithm.name", "\u9375\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0\u540d" }, { "key.password", "\u9375\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "key.bit.size", "\u9375\u306e\u30d3\u30c3\u30c8\u30fb\u30b5\u30a4\u30ba" }, { "keystore.name", "\u30ad\u30fc\u30b9\u30c8\u30a2\u540d" }, { "new.password", "\u65b0\u898f\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "do.not.prompt", "\u30d7\u30ed\u30f3\u30d7\u30c8\u3092\u8868\u793a\u3057\u306a\u3044" }, { "password.through.protected.mechanism", "\u4fdd\u8b77\u30e1\u30ab\u30cb\u30ba\u30e0\u306b\u3088\u308b\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "provider.argument", "\u30d7\u30ed\u30d0\u30a4\u30c0\u5f15\u6570" }, { "provider.class.name", "\u30d7\u30ed\u30d0\u30a4\u30c0\u30fb\u30af\u30e9\u30b9\u540d" }, { "provider.name", "\u30d7\u30ed\u30d0\u30a4\u30c0\u540d" }, { "provider.classpath", "\u30d7\u30ed\u30d0\u30a4\u30c0\u30fb\u30af\u30e9\u30b9\u30d1\u30b9" }, { "output.in.RFC.style", "RFC\u30b9\u30bf\u30a4\u30eb\u306e\u51fa\u529b" }, { "signature.algorithm.name", "\u7f72\u540d\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0\u540d" }, { "source.alias", "\u30bd\u30fc\u30b9\u5225\u540d" }, { "source.key.password", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "source.keystore.name", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u540d" }, { "source.keystore.password.protected", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u4fdd\u8b77\u5bfe\u8c61\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "source.keystore.provider.name", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d7\u30ed\u30d0\u30a4\u30c0\u540d" }, { "source.keystore.password", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "source.keystore.type", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30bf\u30a4\u30d7" }, { "SSL.server.host.and.port", "SSL\u30b5\u30fc\u30d0\u30fc\u306e\u30db\u30b9\u30c8\u3068\u30dd\u30fc\u30c8" }, { "signed.jar.file", "\u7f72\u540d\u4ed8\u304dJAR\u30d5\u30a1\u30a4\u30eb" }, { "certificate.validity.start.date.time", "\u8a3c\u660e\u66f8\u306e\u6709\u52b9\u958b\u59cb\u65e5\u6642" }, { "keystore.password", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "keystore.type", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30bf\u30a4\u30d7" }, { "trust.certificates.from.cacerts", "cacerts\u304b\u3089\u306e\u8a3c\u660e\u66f8\u3092\u4fe1\u983c\u3059\u308b" }, { "verbose.output", "\u8a73\u7d30\u51fa\u529b" }, { "validity.number.of.days", "\u59a5\u5f53\u6027\u65e5\u6570" }, { "Serial.ID.of.cert.to.revoke", "\u5931\u52b9\u3059\u308b\u8a3c\u660e\u66f8\u306e\u30b7\u30ea\u30a2\u30ebID" }, { "keytool.error.", "keytool\u30a8\u30e9\u30fc: " }, { "Illegal.option.", "\u4e0d\u6b63\u306a\u30aa\u30d7\u30b7\u30e7\u30f3:  " }, { "Illegal.value.", "\u4e0d\u6b63\u306a\u5024: " }, { "Unknown.password.type.", "\u4e0d\u660e\u306a\u30d1\u30b9\u30ef\u30fc\u30c9\u30fb\u30bf\u30a4\u30d7: " }, { "Cannot.find.environment.variable.", "\u74b0\u5883\u5909\u6570\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: " }, { "Cannot.find.file.", "\u30d5\u30a1\u30a4\u30eb\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093: " }, { "Command.option.flag.needs.an.argument.", "\u30b3\u30de\u30f3\u30c9\u30fb\u30aa\u30d7\u30b7\u30e7\u30f3{0}\u306b\u306f\u5f15\u6570\u304c\u5fc5\u8981\u3067\u3059\u3002" }, { "Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value.", "\u8b66\u544a: PKCS12\u30ad\u30fc\u30b9\u30c8\u30a2\u3067\u306f\u3001\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3068\u9375\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u304c\u7570\u306a\u308b\u72b6\u6cc1\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3002\u30e6\u30fc\u30b6\u30fc\u304c\u6307\u5b9a\u3057\u305f{0}\u306e\u5024\u306f\u7121\u8996\u3057\u307e\u3059\u3002" }, { ".keystore.must.be.NONE.if.storetype.is.{0}", "-storetype\u304c{0}\u306e\u5834\u5408\u3001-keystore\u306fNONE\u3067\u3042\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Too.many.retries.program.terminated", "\u518d\u8a66\u884c\u304c\u591a\u3059\u304e\u307e\u3059\u3002\u30d7\u30ed\u30b0\u30e9\u30e0\u304c\u7d42\u4e86\u3057\u307e\u3057\u305f" }, { ".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}", "-storetype\u304c{0}\u306e\u5834\u5408\u3001-storepasswd\u30b3\u30de\u30f3\u30c9\u304a\u3088\u3073-keypasswd\u30b3\u30de\u30f3\u30c9\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093" }, { ".keypasswd.commands.not.supported.if.storetype.is.PKCS12", "-storetype\u304cPKCS12\u306e\u5834\u5408\u3001-keypasswd\u30b3\u30de\u30f3\u30c9\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093" }, { ".keypass.and.new.can.not.be.specified.if.storetype.is.{0}", "-storetype\u304c{0}\u306e\u5834\u5408\u3001-keypass\u3068-new\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093" }, { "if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified", "-protected\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u308b\u5834\u5408\u3001-storepass\u3001-keypass\u304a\u3088\u3073-new\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093" }, { "if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified", "-srcprotected\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u308b\u5834\u5408\u3001-srcstorepass\u304a\u3088\u3073-srckeypass\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093" }, { "if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified", "\u30ad\u30fc\u30b9\u30c8\u30a2\u304c\u30d1\u30b9\u30ef\u30fc\u30c9\u3067\u4fdd\u8b77\u3055\u308c\u3066\u3044\u306a\u3044\u5834\u5408\u3001-storepass\u3001-keypass\u304a\u3088\u3073-new\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093" }, { "if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u304c\u30d1\u30b9\u30ef\u30fc\u30c9\u3067\u4fdd\u8b77\u3055\u308c\u3066\u3044\u306a\u3044\u5834\u5408\u3001-srcstorepass\u304a\u3088\u3073-srckeypass\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093" }, { "Illegal.startdate.value", "startdate\u5024\u304c\u7121\u52b9\u3067\u3059" }, { "Validity.must.be.greater.than.zero", "\u59a5\u5f53\u6027\u306f\u30bc\u30ed\u3088\u308a\u5927\u304d\u3044\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "provName.not.a.provider", "{0}\u306f\u30d7\u30ed\u30d0\u30a4\u30c0\u3067\u306f\u3042\u308a\u307e\u305b\u3093" }, { "Usage.error.no.command.provided", "\u4f7f\u7528\u30a8\u30e9\u30fc: \u30b3\u30de\u30f3\u30c9\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u307e\u305b\u3093" }, { "Source.keystore.file.exists.but.is.empty.", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d5\u30a1\u30a4\u30eb\u306f\u3001\u5b58\u5728\u3057\u307e\u3059\u304c\u7a7a\u3067\u3059: " }, { "Please.specify.srckeystore", "-srckeystore\u3092\u6307\u5b9a\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Must.not.specify.both.v.and.rfc.with.list.command", "'list'\u30b3\u30de\u30f3\u30c9\u306b-v\u3068-rfc\u306e\u4e21\u65b9\u3092\u6307\u5b9a\u3059\u308b\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093" }, { "Key.password.must.be.at.least.6.characters", "\u9375\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u306f6\u6587\u5b57\u4ee5\u4e0a\u3067\u3042\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "New.password.must.be.at.least.6.characters", "\u65b0\u898f\u30d1\u30b9\u30ef\u30fc\u30c9\u306f6\u6587\u5b57\u4ee5\u4e0a\u3067\u3042\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Keystore.file.exists.but.is.empty.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d5\u30a1\u30a4\u30eb\u306f\u5b58\u5728\u3057\u307e\u3059\u304c\u3001\u7a7a\u3067\u3059: " }, { "Keystore.file.does.not.exist.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d5\u30a1\u30a4\u30eb\u306f\u5b58\u5728\u3057\u307e\u305b\u3093: " }, { "Must.specify.destination.alias", "\u51fa\u529b\u5148\u306e\u5225\u540d\u3092\u6307\u5b9a\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Must.specify.alias", "\u5225\u540d\u3092\u6307\u5b9a\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Keystore.password.must.be.at.least.6.characters", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u306f6\u6587\u5b57\u4ee5\u4e0a\u3067\u3042\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Enter.the.password.to.be.stored.", "\u4fdd\u5b58\u3059\u308b\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044:  " }, { "Enter.keystore.password.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044:  " }, { "Enter.source.keystore.password.", "\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044:  " }, { "Enter.destination.keystore.password.", "\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044:  " }, { "Keystore.password.is.too.short.must.be.at.least.6.characters", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u304c\u77ed\u3059\u304e\u307e\u3059 - 6\u6587\u5b57\u4ee5\u4e0a\u306b\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Unknown.Entry.Type", "\u4e0d\u660e\u306a\u30a8\u30f3\u30c8\u30ea\u30fb\u30bf\u30a4\u30d7" }, { "Too.many.failures.Alias.not.changed", "\u969c\u5bb3\u304c\u591a\u3059\u304e\u307e\u3059\u3002\u5225\u540d\u306f\u5909\u66f4\u3055\u308c\u307e\u305b\u3093" }, { "Entry.for.alias.alias.successfully.imported.", "\u5225\u540d{0}\u306e\u30a8\u30f3\u30c8\u30ea\u306e\u30a4\u30f3\u30dd\u30fc\u30c8\u306b\u6210\u529f\u3057\u307e\u3057\u305f\u3002" }, { "Entry.for.alias.alias.not.imported.", "\u5225\u540d{0}\u306e\u30a8\u30f3\u30c8\u30ea\u306f\u30a4\u30f3\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002" }, { "Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported.", "\u5225\u540d{0}\u306e\u30a8\u30f3\u30c8\u30ea\u306e\u30a4\u30f3\u30dd\u30fc\u30c8\u4e2d\u306b\u554f\u984c\u304c\u767a\u751f\u3057\u307e\u3057\u305f: {1}\u3002\n\u5225\u540d{0}\u306e\u30a8\u30f3\u30c8\u30ea\u306f\u30a4\u30f3\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002" }, { "Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled", "\u30a4\u30f3\u30dd\u30fc\u30c8\u30fb\u30b3\u30de\u30f3\u30c9\u304c\u5b8c\u4e86\u3057\u307e\u3057\u305f: {0}\u4ef6\u306e\u30a8\u30f3\u30c8\u30ea\u306e\u30a4\u30f3\u30dd\u30fc\u30c8\u304c\u6210\u529f\u3057\u307e\u3057\u305f\u3002{1}\u4ef6\u306e\u30a8\u30f3\u30c8\u30ea\u306e\u30a4\u30f3\u30dd\u30fc\u30c8\u304c\u5931\u6557\u3057\u305f\u304b\u53d6\u308a\u6d88\u3055\u308c\u307e\u3057\u305f" }, { "Warning.Overwriting.existing.alias.alias.in.destination.keystore", "\u8b66\u544a: \u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u5185\u306e\u65e2\u5b58\u306e\u5225\u540d{0}\u3092\u4e0a\u66f8\u304d\u3057\u3066\u3044\u307e\u3059" }, { "Existing.entry.alias.alias.exists.overwrite.no.", "\u65e2\u5b58\u306e\u30a8\u30f3\u30c8\u30ea\u306e\u5225\u540d{0}\u304c\u5b58\u5728\u3057\u3066\u3044\u307e\u3059\u3002\u4e0a\u66f8\u304d\u3057\u307e\u3059\u304b\u3002[\u3044\u3044\u3048]:  " }, { "Too.many.failures.try.later", "\u969c\u5bb3\u304c\u591a\u3059\u304e\u307e\u3059 - \u5f8c\u3067\u5b9f\u884c\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Certification.request.stored.in.file.filename.", "\u8a8d\u8a3c\u30ea\u30af\u30a8\u30b9\u30c8\u304c\u30d5\u30a1\u30a4\u30eb<{0}>\u306b\u4fdd\u5b58\u3055\u308c\u307e\u3057\u305f" }, { "Submit.this.to.your.CA", "\u3053\u308c\u3092CA\u306b\u63d0\u51fa\u3057\u3066\u304f\u3060\u3055\u3044" }, { "if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified", "\u5225\u540d\u3092\u6307\u5b9a\u3057\u306a\u3044\u5834\u5408\u3001\u51fa\u529b\u5148\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u5225\u540d\u304a\u3088\u3073\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093" }, { "The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified.", "\u51fa\u529b\u5148pkcs12\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u3001\u7570\u306a\u308bstorepass\u304a\u3088\u3073keypass\u304c\u3042\u308a\u307e\u3059\u3002-destkeypass\u3092\u6307\u5b9a\u3057\u3066\u518d\u8a66\u884c\u3057\u3066\u304f\u3060\u3055\u3044\u3002" }, { "Certificate.stored.in.file.filename.", "\u8a3c\u660e\u66f8\u304c\u30d5\u30a1\u30a4\u30eb<{0}>\u306b\u4fdd\u5b58\u3055\u308c\u307e\u3057\u305f" }, { "Certificate.reply.was.installed.in.keystore", "\u8a3c\u660e\u66f8\u5fdc\u7b54\u304c\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u30a4\u30f3\u30b9\u30c8\u30fc\u30eb\u3055\u308c\u307e\u3057\u305f" }, { "Certificate.reply.was.not.installed.in.keystore", "\u8a3c\u660e\u66f8\u5fdc\u7b54\u304c\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u30a4\u30f3\u30b9\u30c8\u30fc\u30eb\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f" }, { "Certificate.was.added.to.keystore", "\u8a3c\u660e\u66f8\u304c\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u8ffd\u52a0\u3055\u308c\u307e\u3057\u305f" }, { "Certificate.was.not.added.to.keystore", "\u8a3c\u660e\u66f8\u304c\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u8ffd\u52a0\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f" }, { ".Storing.ksfname.", "[{0}\u3092\u683c\u7d0d\u4e2d]" }, { "alias.has.no.public.key.certificate.", "{0}\u306b\u306f\u516c\u958b\u9375(\u8a3c\u660e\u66f8)\u304c\u3042\u308a\u307e\u305b\u3093" }, { "Cannot.derive.signature.algorithm", "\u7f72\u540d\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0\u3092\u53d6\u5f97\u3067\u304d\u307e\u305b\u3093" }, { "Alias.alias.does.not.exist", "\u5225\u540d<{0}>\u306f\u5b58\u5728\u3057\u307e\u305b\u3093" }, { "Alias.alias.has.no.certificate", "\u5225\u540d<{0}>\u306b\u306f\u8a3c\u660e\u66f8\u304c\u3042\u308a\u307e\u305b\u3093" }, { "Key.pair.not.generated.alias.alias.already.exists", "\u9375\u30da\u30a2\u306f\u751f\u6210\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002\u5225\u540d<{0}>\u306f\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for", "{3}\u65e5\u9593\u6709\u52b9\u306a{0}\u30d3\u30c3\u30c8\u306e{1}\u306e\u9375\u30da\u30a2\u3068\u81ea\u5df1\u7f72\u540d\u578b\u8a3c\u660e\u66f8({2})\u3092\u751f\u6210\u3057\u3066\u3044\u307e\u3059\n\t\u30c7\u30a3\u30ec\u30af\u30c8\u30ea\u540d: {4}" }, { "Enter.key.password.for.alias.", "<{0}>\u306e\u9375\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044" }, { ".RETURN.if.same.as.keystore.password.", "\t(\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3068\u540c\u3058\u5834\u5408\u306fRETURN\u3092\u62bc\u3057\u3066\u304f\u3060\u3055\u3044):  " }, { "Key.password.is.too.short.must.be.at.least.6.characters", "\u9375\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u304c\u77ed\u3059\u304e\u307e\u3059 - 6\u6587\u5b57\u4ee5\u4e0a\u3092\u6307\u5b9a\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Too.many.failures.key.not.added.to.keystore", "\u969c\u5bb3\u304c\u591a\u3059\u304e\u307e\u3059 - \u9375\u306f\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u8ffd\u52a0\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f" }, { "Destination.alias.dest.already.exists", "\u51fa\u529b\u5148\u306e\u5225\u540d<{0}>\u306f\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Password.is.too.short.must.be.at.least.6.characters", "\u30d1\u30b9\u30ef\u30fc\u30c9\u304c\u77ed\u3059\u304e\u307e\u3059 - 6\u6587\u5b57\u4ee5\u4e0a\u3092\u6307\u5b9a\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Too.many.failures.Key.entry.not.cloned", "\u969c\u5bb3\u304c\u591a\u3059\u304e\u307e\u3059\u3002\u9375\u30a8\u30f3\u30c8\u30ea\u306e\u30af\u30ed\u30fc\u30f3\u306f\u4f5c\u6210\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f" }, { "key.password.for.alias.", "<{0}>\u306e\u9375\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "Keystore.entry.for.id.getName.already.exists", "<{0}>\u306e\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30a8\u30f3\u30c8\u30ea\u306f\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Creating.keystore.entry.for.id.getName.", "<{0}>\u306e\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30a8\u30f3\u30c8\u30ea\u3092\u4f5c\u6210\u4e2d..." }, { "No.entries.from.identity.database.added", "\u30a2\u30a4\u30c7\u30f3\u30c6\u30a3\u30c6\u30a3\u30fb\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u304b\u3089\u8ffd\u52a0\u3055\u308c\u305f\u30a8\u30f3\u30c8\u30ea\u306f\u3042\u308a\u307e\u305b\u3093" }, { "Alias.name.alias", "\u5225\u540d: {0}" }, { "Creation.date.keyStore.getCreationDate.alias.", "\u4f5c\u6210\u65e5: {0,date}" }, { "alias.keyStore.getCreationDate.alias.", "{0},{1,date}, " }, { "alias.", "{0}, " }, { "Entry.type.type.", "\u30a8\u30f3\u30c8\u30ea\u30fb\u30bf\u30a4\u30d7: {0}" }, { "Certificate.chain.length.", "\u8a3c\u660e\u66f8\u30c1\u30a7\u30fc\u30f3\u306e\u9577\u3055: " }, { "Certificate.i.1.", "\u8a3c\u660e\u66f8[{0,number,integer}]:" }, { "Certificate.fingerprint.SHA1.", "\u8a3c\u660e\u66f8\u306e\u30d5\u30a3\u30f3\u30ac\u30d7\u30ea\u30f3\u30c8(SHA1): " }, { "Keystore.type.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30bf\u30a4\u30d7: " }, { "Keystore.provider.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u30fb\u30d7\u30ed\u30d0\u30a4\u30c0: " }, { "Your.keystore.contains.keyStore.size.entry", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u306f{0,number,integer}\u30a8\u30f3\u30c8\u30ea\u304c\u542b\u307e\u308c\u307e\u3059" }, { "Your.keystore.contains.keyStore.size.entries", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u306f{0,number,integer}\u30a8\u30f3\u30c8\u30ea\u304c\u542b\u307e\u308c\u307e\u3059" }, { "Failed.to.parse.input", "\u5165\u529b\u306e\u69cb\u6587\u89e3\u6790\u306b\u5931\u6557\u3057\u307e\u3057\u305f" }, { "Empty.input", "\u5165\u529b\u304c\u3042\u308a\u307e\u305b\u3093" }, { "Not.X.509.certificate", "X.509\u8a3c\u660e\u66f8\u3067\u306f\u3042\u308a\u307e\u305b\u3093" }, { "alias.has.no.public.key", "{0}\u306b\u306f\u516c\u958b\u9375\u304c\u3042\u308a\u307e\u305b\u3093" }, { "alias.has.no.X.509.certificate", "{0}\u306b\u306fX.509\u8a3c\u660e\u66f8\u304c\u3042\u308a\u307e\u305b\u3093" }, { "New.certificate.self.signed.", "\u65b0\u3057\u3044\u8a3c\u660e\u66f8(\u81ea\u5df1\u7f72\u540d\u578b):" }, { "Reply.has.no.certificates", "\u5fdc\u7b54\u306b\u306f\u8a3c\u660e\u66f8\u304c\u3042\u308a\u307e\u305b\u3093" }, { "Certificate.not.imported.alias.alias.already.exists", "\u8a3c\u660e\u66f8\u306f\u30a4\u30f3\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002\u5225\u540d<{0}>\u306f\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Input.not.an.X.509.certificate", "\u5165\u529b\u306fX.509\u8a3c\u660e\u66f8\u3067\u306f\u3042\u308a\u307e\u305b\u3093" }, { "Certificate.already.exists.in.keystore.under.alias.trustalias.", "\u8a3c\u660e\u66f8\u306f\u3001\u5225\u540d<{0}>\u306e\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Do.you.still.want.to.add.it.no.", "\u8ffd\u52a0\u3057\u307e\u3059\u304b\u3002[\u3044\u3044\u3048]:  " }, { "Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias.", "\u8a3c\u660e\u66f8\u306f\u3001\u5225\u540d<{0}>\u306e\u30b7\u30b9\u30c6\u30e0\u898f\u6a21\u306eCA\u30ad\u30fc\u30b9\u30c8\u30a2\u5185\u306b\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Do.you.still.want.to.add.it.to.your.own.keystore.no.", "\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u8ffd\u52a0\u3057\u307e\u3059\u304b\u3002 [\u3044\u3044\u3048]:  " }, { "Trust.this.certificate.no.", "\u3053\u306e\u8a3c\u660e\u66f8\u3092\u4fe1\u983c\u3057\u307e\u3059\u304b\u3002 [\u3044\u3044\u3048]:  " }, { "YES", "\u306f\u3044" }, { "New.prompt.", "\u65b0\u898f{0}: " }, { "Passwords.must.differ", "\u30d1\u30b9\u30ef\u30fc\u30c9\u306f\u7570\u306a\u3063\u3066\u3044\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059" }, { "Re.enter.new.prompt.", "\u65b0\u898f{0}\u3092\u518d\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044: " }, { "Re.enter.password.", "\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u518d\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044: " }, { "Re.enter.new.password.", "\u65b0\u898f\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u518d\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044: " }, { "They.don.t.match.Try.again", "\u4e00\u81f4\u3057\u307e\u305b\u3093\u3002\u3082\u3046\u4e00\u5ea6\u5b9f\u884c\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Enter.prompt.alias.name.", "{0}\u306e\u5225\u540d\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044:  " }, { "Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry.", "\u65b0\u3057\u3044\u5225\u540d\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044\t(\u3053\u306e\u30a8\u30f3\u30c8\u30ea\u306e\u30a4\u30f3\u30dd\u30fc\u30c8\u3092\u53d6\u308a\u6d88\u3059\u5834\u5408\u306fRETURN\u3092\u62bc\u3057\u3066\u304f\u3060\u3055\u3044):  " }, { "Enter.alias.name.", "\u5225\u540d\u3092\u5165\u529b\u3057\u3066\u304f\u3060\u3055\u3044:  " }, { ".RETURN.if.same.as.for.otherAlias.", "\t(<{0}>\u3068\u540c\u3058\u5834\u5408\u306fRETURN\u3092\u62bc\u3057\u3066\u304f\u3060\u3055\u3044)" }, { "What.is.your.first.and.last.name.", "\u59d3\u540d\u306f\u4f55\u3067\u3059\u304b\u3002" }, { "What.is.the.name.of.your.organizational.unit.", "\u7d44\u7e54\u5358\u4f4d\u540d\u306f\u4f55\u3067\u3059\u304b\u3002" }, { "What.is.the.name.of.your.organization.", "\u7d44\u7e54\u540d\u306f\u4f55\u3067\u3059\u304b\u3002" }, { "What.is.the.name.of.your.City.or.Locality.", "\u90fd\u5e02\u540d\u307e\u305f\u306f\u5730\u57df\u540d\u306f\u4f55\u3067\u3059\u304b\u3002" }, { "What.is.the.name.of.your.State.or.Province.", "\u90fd\u9053\u5e9c\u770c\u540d\u307e\u305f\u306f\u5dde\u540d\u306f\u4f55\u3067\u3059\u304b\u3002" }, { "What.is.the.two.letter.country.code.for.this.unit.", "\u3053\u306e\u5358\u4f4d\u306b\u8a72\u5f53\u3059\u308b2\u6587\u5b57\u306e\u56fd\u30b3\u30fc\u30c9\u306f\u4f55\u3067\u3059\u304b\u3002" }, { "Is.name.correct.", "{0}\u3067\u3088\u308d\u3057\u3044\u3067\u3059\u304b\u3002" }, { "no", "\u3044\u3044\u3048" }, { "yes", "\u306f\u3044" }, { "y", "y" }, { ".defaultValue.", "  [{0}]:  " }, { "Alias.alias.has.no.key", "\u5225\u540d<{0}>\u306b\u306f\u9375\u304c\u3042\u308a\u307e\u305b\u3093" }, { "Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key", "\u5225\u540d<{0}>\u304c\u53c2\u7167\u3057\u3066\u3044\u308b\u30a8\u30f3\u30c8\u30ea\u30fb\u30bf\u30a4\u30d7\u306f\u79d8\u5bc6\u9375\u30a8\u30f3\u30c8\u30ea\u3067\u306f\u3042\u308a\u307e\u305b\u3093\u3002-keyclone\u30b3\u30de\u30f3\u30c9\u306f\u79d8\u5bc6\u9375\u30a8\u30f3\u30c8\u30ea\u306e\u30af\u30ed\u30fc\u30f3\u4f5c\u6210\u306e\u307f\u3092\u30b5\u30dd\u30fc\u30c8\u3057\u307e\u3059" }, { ".WARNING.WARNING.WARNING.", "*****************  WARNING WARNING WARNING  *****************" }, { "Signer.d.", "\u7f72\u540d\u8005\u756a\u53f7%d:" }, { "Timestamp.", "\u30bf\u30a4\u30e0\u30b9\u30bf\u30f3\u30d7:" }, { "Signature.", "\u7f72\u540d:" }, { "CRLs.", "CRL:" }, { "Certificate.owner.", "\u8a3c\u660e\u66f8\u306e\u6240\u6709\u8005: " }, { "Not.a.signed.jar.file", "\u7f72\u540d\u4ed8\u304dJAR\u30d5\u30a1\u30a4\u30eb\u3067\u306f\u3042\u308a\u307e\u305b\u3093" }, { "No.certificate.from.the.SSL.server", "SSL\u30b5\u30fc\u30d0\u30fc\u304b\u3089\u306e\u8a3c\u660e\u66f8\u304c\u3042\u308a\u307e\u305b\u3093" }, { ".The.integrity.of.the.information.stored.in.your.keystore.", "*\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u4fdd\u5b58\u3055\u308c\u305f\u60c5\u5831\u306e\u6574\u5408\u6027\u306f*\n*\u691c\u8a3c\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u6574\u5408\u6027\u3092\u691c\u8a3c\u3059\u308b\u306b\u306f*\n*\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059\u3002*" }, { ".The.integrity.of.the.information.stored.in.the.srckeystore.", "*\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306b\u4fdd\u5b58\u3055\u308c\u305f\u60c5\u5831\u306e\u6574\u5408\u6027\u306f*\n*\u691c\u8a3c\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002\u6574\u5408\u6027\u3092\u691c\u8a3c\u3059\u308b\u306b\u306f*\n*\u30bd\u30fc\u30b9\u30fb\u30ad\u30fc\u30b9\u30c8\u30a2\u306e\u30d1\u30b9\u30ef\u30fc\u30c9\u3092\u5165\u529b\u3059\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059\u3002*" }, { "Certificate.reply.does.not.contain.public.key.for.alias.", "\u8a3c\u660e\u66f8\u5fdc\u7b54\u306b\u306f\u3001<{0}>\u306e\u516c\u958b\u9375\u306f\u542b\u307e\u308c\u307e\u305b\u3093" }, { "Incomplete.certificate.chain.in.reply", "\u5fdc\u7b54\u3057\u305f\u8a3c\u660e\u66f8\u30c1\u30a7\u30fc\u30f3\u306f\u4e0d\u5b8c\u5168\u3067\u3059" }, { "Certificate.chain.in.reply.does.not.verify.", "\u5fdc\u7b54\u3057\u305f\u8a3c\u660e\u66f8\u30c1\u30a7\u30fc\u30f3\u306f\u691c\u8a3c\u3055\u308c\u3066\u3044\u307e\u305b\u3093: " }, { "Top.level.certificate.in.reply.", "\u5fdc\u7b54\u3057\u305f\u30c8\u30c3\u30d7\u30ec\u30d9\u30eb\u306e\u8a3c\u660e\u66f8:\n" }, { ".is.not.trusted.", "... \u306f\u4fe1\u983c\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002 " }, { "Install.reply.anyway.no.", "\u5fdc\u7b54\u3092\u30a4\u30f3\u30b9\u30c8\u30fc\u30eb\u3057\u307e\u3059\u304b\u3002[\u3044\u3044\u3048]:  " }, { "NO", "\u3044\u3044\u3048" }, { "Public.keys.in.reply.and.keystore.don.t.match", "\u5fdc\u7b54\u3057\u305f\u516c\u958b\u9375\u3068\u30ad\u30fc\u30b9\u30c8\u30a2\u304c\u4e00\u81f4\u3057\u307e\u305b\u3093" }, { "Certificate.reply.and.certificate.in.keystore.are.identical", "\u8a3c\u660e\u66f8\u5fdc\u7b54\u3068\u30ad\u30fc\u30b9\u30c8\u30a2\u5185\u306e\u8a3c\u660e\u66f8\u304c\u540c\u3058\u3067\u3059" }, { "Failed.to.establish.chain.from.reply", "\u5fdc\u7b54\u304b\u3089\u9023\u9396\u3092\u78ba\u7acb\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f" }, { "n", "n" }, { "Wrong.answer.try.again", "\u5fdc\u7b54\u304c\u9593\u9055\u3063\u3066\u3044\u307e\u3059\u3002\u3082\u3046\u4e00\u5ea6\u5b9f\u884c\u3057\u3066\u304f\u3060\u3055\u3044" }, { "Secret.key.not.generated.alias.alias.already.exists", "\u79d8\u5bc6\u9375\u306f\u751f\u6210\u3055\u308c\u307e\u305b\u3093\u3067\u3057\u305f\u3002\u5225\u540d<{0}>\u306f\u3059\u3067\u306b\u5b58\u5728\u3057\u307e\u3059" }, { "Please.provide.keysize.for.secret.key.generation", "\u79d8\u5bc6\u9375\u306e\u751f\u6210\u6642\u306b\u306f -keysize\u3092\u6307\u5b9a\u3057\u3066\u304f\u3060\u3055\u3044" }, { "warning.not.verified.make.sure.keystore.is.correct", "\u8b66\u544a: \u691c\u8a3c\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002-keystore\u304c\u6b63\u3057\u3044\u3053\u3068\u3092\u78ba\u8a8d\u3057\u3066\u304f\u3060\u3055\u3044\u3002" }, { "Extensions.", "\u62e1\u5f35: " }, { ".Empty.value.", "(\u7a7a\u306e\u5024)" }, { "Extension.Request.", "\u62e1\u5f35\u30ea\u30af\u30a8\u30b9\u30c8:" }, { "Unknown.keyUsage.type.", "\u4e0d\u660e\u306akeyUsage\u30bf\u30a4\u30d7: " }, { "Unknown.extendedkeyUsage.type.", "\u4e0d\u660e\u306aextendedkeyUsage\u30bf\u30a4\u30d7: " }, { "Unknown.AccessDescription.type.", "\u4e0d\u660e\u306aAccessDescription\u30bf\u30a4\u30d7: " }, { "Unrecognized.GeneralName.type.", "\u8a8d\u8b58\u3055\u308c\u306a\u3044GeneralName\u30bf\u30a4\u30d7: " }, { "This.extension.cannot.be.marked.as.critical.", "\u3053\u306e\u62e1\u5f35\u306f\u30af\u30ea\u30c6\u30a3\u30ab\u30eb\u3068\u3057\u3066\u30de\u30fc\u30af\u4ed8\u3051\u3067\u304d\u307e\u305b\u3093\u3002 " }, { "Odd.number.of.hex.digits.found.", "\u5947\u6570\u306e16\u9032\u6570\u304c\u898b\u3064\u304b\u308a\u307e\u3057\u305f: " }, { "Unknown.extension.type.", "\u4e0d\u660e\u306a\u62e1\u5f35\u30bf\u30a4\u30d7: " }, { "command.{0}.is.ambiguous.", "\u30b3\u30de\u30f3\u30c9{0}\u306f\u3042\u3044\u307e\u3044\u3067\u3059:" }, { "the.certificate.request", "\u8a3c\u660e\u66f8\u30ea\u30af\u30a8\u30b9\u30c8" }, { "the.issuer", "\u767a\u884c\u8005" }, { "the.generated.certificate", "\u751f\u6210\u3055\u308c\u305f\u8a3c\u660e\u66f8" }, { "the.generated.crl", "\u751f\u6210\u3055\u308c\u305fCRL" }, { "the.generated.certificate.request", "\u751f\u6210\u3055\u308c\u305f\u8a3c\u660e\u66f8\u30ea\u30af\u30a8\u30b9\u30c8" }, { "the.certificate", "\u8a3c\u660e\u66f8" }, { "the.crl", "CRL" }, { "the.tsa.certificate", "TSA\u8a3c\u660e\u66f8" }, { "the.input", "\u5165\u529b" }, { "reply", "\u5fdc\u7b54" }, { "one.in.many", "%1$s #%2$d / %3$d" }, { "alias.in.cacerts", "cacerts\u5185\u306e\u767a\u884c\u8005<%s>" }, { "alias.in.keystore", "\u767a\u884c\u8005<%s>" }, { "with.weak", "%s (\u5f31)" }, { "key.bit", "%1$d\u30d3\u30c3\u30c8%2$s\u9375" }, { "key.bit.weak", "%1$d\u30d3\u30c3\u30c8%2$s\u9375(\u5f31)" }, { ".PATTERN.printX509Cert.with.weak", "\u6240\u6709\u8005: {0}\n\u767a\u884c\u8005: {1}\n\u30b7\u30ea\u30a2\u30eb\u756a\u53f7: {2}\n\u6709\u52b9\u671f\u9593\u306e\u958b\u59cb\u65e5: {3} \u7d42\u4e86\u65e5: {4}\n\u8a3c\u660e\u66f8\u306e\u30d5\u30a3\u30f3\u30ac\u30d7\u30ea\u30f3\u30c8:\n\t MD5:  {5}\n\t SHA1: {6}\n\t SHA256: {7}\n\u7f72\u540d\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0\u540d: {8}\n\u30b5\u30d6\u30b8\u30a7\u30af\u30c8\u516c\u958b\u9375\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0: {9}\n\u30d0\u30fc\u30b8\u30e7\u30f3: {10}" }, { "PKCS.10.with.weak", "PKCS #10\u8a3c\u660e\u66f8\u30ea\u30af\u30a8\u30b9\u30c8(\u30d0\u30fc\u30b8\u30e7\u30f31.0)\n\u30b5\u30d6\u30b8\u30a7\u30af\u30c8: %1$s\n\u30d5\u30a9\u30fc\u30de\u30c3\u30c8: %2$s\n\u516c\u958b\u9375: %3$s\n\u7f72\u540d\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0: %4$s\n" }, { "verified.by.s.in.s.weak", "%2$s\u5185\u306e%1$s\u306b\u3088\u308a%3$s\u3067\u691c\u8a3c\u3055\u308c\u307e\u3057\u305f" }, { "whose.sigalg.risk", "%1$s\u306f%2$s\u7f72\u540d\u30a2\u30eb\u30b4\u30ea\u30ba\u30e0\u3092\u4f7f\u7528\u3057\u3066\u304a\u308a\u3001\u3053\u308c\u306f\u30bb\u30ad\u30e5\u30ea\u30c6\u30a3\u30fb\u30ea\u30b9\u30af\u3068\u307f\u306a\u3055\u308c\u307e\u3059\u3002" }, { "whose.key.risk", "%1$s\u306f%2$s\u3092\u4f7f\u7528\u3057\u3066\u304a\u308a\u3001\u3053\u308c\u306f\u30bb\u30ad\u30e5\u30ea\u30c6\u30a3\u30fb\u30ea\u30b9\u30af\u3068\u307f\u306a\u3055\u308c\u307e\u3059\u3002" }, { "jks.storetype.warning", "%1$s\u30ad\u30fc\u30b9\u30c8\u30a2\u306f\u72ec\u81ea\u306e\u5f62\u5f0f\u3092\u4f7f\u7528\u3057\u3066\u3044\u307e\u3059\u3002\"keytool -importkeystore -srckeystore %2$s -destkeystore %2$s -deststoretype pkcs12\"\u3092\u4f7f\u7528\u3059\u308b\u696d\u754c\u6a19\u6e96\u306e\u5f62\u5f0f\u3067\u3042\u308bPKCS12\u306b\u79fb\u884c\u3059\u308b\u3053\u3068\u3092\u304a\u85a6\u3081\u3057\u307e\u3059\u3002" }, { "migrate.keystore.warning", "\"%1$s\"\u304c%4$s\u306b\u79fb\u884c\u3055\u308c\u307e\u3057\u305f\u3002%2$s\u30ad\u30fc\u30b9\u30c8\u30a2\u306f\"%3$s\"\u3068\u3057\u3066\u30d0\u30c3\u30af\u30a2\u30c3\u30d7\u3055\u308c\u307e\u3059\u3002" }, { "backup.keystore.warning", "\u5143\u306e\u30ad\u30fc\u30b9\u30c8\u30a2\"%1$s\"\u306f\"%3$s\"\u3068\u3057\u3066\u30d0\u30c3\u30af\u30a2\u30c3\u30d7\u3055\u308c\u307e\u3059..." }, { "importing.keystore.status", "\u30ad\u30fc\u30b9\u30c8\u30a2%1$s\u3092%2$s\u306b\u30a4\u30f3\u30dd\u30fc\u30c8\u3057\u3066\u3044\u307e\u3059..." } };
    }
}
