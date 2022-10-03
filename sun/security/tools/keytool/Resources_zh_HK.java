package sun.security.tools.keytool;

import java.util.ListResourceBundle;

public class Resources_zh_HK extends ListResourceBundle
{
    private static final Object[][] contents;
    
    public Object[][] getContents() {
        return Resources_zh_HK.contents;
    }
    
    static {
        contents = new Object[][] { { "NEWLINE", "\n" }, { "STAR", "*******************************************" }, { "STARNN", "*******************************************\n\n" }, { ".OPTION.", " [\u9078\u9805]..." }, { "Options.", "\u9078\u9805:" }, { "Use.keytool.help.for.all.available.commands", "\u4f7f\u7528 \"keytool -help\" \u53d6\u5f97\u6240\u6709\u53ef\u7528\u7684\u547d\u4ee4" }, { "Key.and.Certificate.Management.Tool", "\u91d1\u9470\u8207\u6191\u8b49\u7ba1\u7406\u5de5\u5177" }, { "Commands.", "\u547d\u4ee4:" }, { "Use.keytool.command.name.help.for.usage.of.command.name", "\u4f7f\u7528 \"keytool -command_name -help\" \u53d6\u5f97 command_name \u7684\u7528\u6cd5" }, { "Generates.a.certificate.request", "\u7522\u751f\u6191\u8b49\u8981\u6c42" }, { "Changes.an.entry.s.alias", "\u8b8a\u66f4\u9805\u76ee\u7684\u5225\u540d" }, { "Deletes.an.entry", "\u522a\u9664\u9805\u76ee" }, { "Exports.certificate", "\u532f\u51fa\u6191\u8b49" }, { "Generates.a.key.pair", "\u7522\u751f\u91d1\u9470\u7d44" }, { "Generates.a.secret.key", "\u7522\u751f\u79d8\u5bc6\u91d1\u9470" }, { "Generates.certificate.from.a.certificate.request", "\u5f9e\u6191\u8b49\u8981\u6c42\u7522\u751f\u6191\u8b49" }, { "Generates.CRL", "\u7522\u751f CRL" }, { "Imports.entries.from.a.JDK.1.1.x.style.identity.database", "\u5f9e JDK 1.1.x-style \u8b58\u5225\u8cc7\u6599\u5eab\u532f\u5165\u9805\u76ee" }, { "Imports.a.certificate.or.a.certificate.chain", "\u532f\u5165\u6191\u8b49\u6216\u6191\u8b49\u93c8" }, { "Imports.one.or.all.entries.from.another.keystore", "\u5f9e\u5176\u4ed6\u91d1\u9470\u5132\u5b58\u5eab\u532f\u5165\u4e00\u500b\u6216\u5168\u90e8\u9805\u76ee" }, { "Clones.a.key.entry", "\u8907\u88fd\u91d1\u9470\u9805\u76ee" }, { "Changes.the.key.password.of.an.entry", "\u8b8a\u66f4\u9805\u76ee\u7684\u91d1\u9470\u5bc6\u78bc" }, { "Lists.entries.in.a.keystore", "\u5217\u793a\u91d1\u9470\u5132\u5b58\u5eab\u4e2d\u7684\u9805\u76ee" }, { "Prints.the.content.of.a.certificate", "\u5217\u5370\u6191\u8b49\u7684\u5167\u5bb9" }, { "Prints.the.content.of.a.certificate.request", "\u5217\u5370\u6191\u8b49\u8981\u6c42\u7684\u5167\u5bb9" }, { "Prints.the.content.of.a.CRL.file", "\u5217\u5370 CRL \u6a94\u6848\u7684\u5167\u5bb9" }, { "Generates.a.self.signed.certificate", "\u7522\u751f\u81ea\u884c\u7c3d\u7f72\u7684\u6191\u8b49" }, { "Changes.the.store.password.of.a.keystore", "\u8b8a\u66f4\u91d1\u9470\u5132\u5b58\u5eab\u7684\u5132\u5b58\u5bc6\u78bc" }, { "alias.name.of.the.entry.to.process", "\u8981\u8655\u7406\u9805\u76ee\u7684\u5225\u540d\u540d\u7a31" }, { "destination.alias", "\u76ee\u7684\u5730\u5225\u540d" }, { "destination.key.password", "\u76ee\u7684\u5730\u91d1\u9470\u5bc6\u78bc" }, { "destination.keystore.name", "\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u540d\u7a31" }, { "destination.keystore.password.protected", "\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc\u4fdd\u8b77" }, { "destination.keystore.provider.name", "\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u63d0\u4f9b\u8005\u540d\u7a31" }, { "destination.keystore.password", "\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc" }, { "destination.keystore.type", "\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u985e\u578b" }, { "distinguished.name", "\u8fa8\u5225\u540d\u7a31" }, { "X.509.extension", "X.509 \u64f4\u5145\u5957\u4ef6" }, { "output.file.name", "\u8f38\u51fa\u6a94\u6848\u540d\u7a31" }, { "input.file.name", "\u8f38\u5165\u6a94\u6848\u540d\u7a31" }, { "key.algorithm.name", "\u91d1\u9470\u6f14\u7b97\u6cd5\u540d\u7a31" }, { "key.password", "\u91d1\u9470\u5bc6\u78bc" }, { "key.bit.size", "\u91d1\u9470\u4f4d\u5143\u5927\u5c0f" }, { "keystore.name", "\u91d1\u9470\u5132\u5b58\u5eab\u540d\u7a31" }, { "new.password", "\u65b0\u5bc6\u78bc" }, { "do.not.prompt", "\u4e0d\u8981\u63d0\u793a" }, { "password.through.protected.mechanism", "\u7d93\u7531\u4fdd\u8b77\u6a5f\u5236\u7684\u5bc6\u78bc" }, { "provider.argument", "\u63d0\u4f9b\u8005\u5f15\u6578" }, { "provider.class.name", "\u63d0\u4f9b\u8005\u985e\u5225\u540d\u7a31" }, { "provider.name", "\u63d0\u4f9b\u8005\u540d\u7a31" }, { "provider.classpath", "\u63d0\u4f9b\u8005\u985e\u5225\u8def\u5f91" }, { "output.in.RFC.style", "\u4ee5 RFC \u6a23\u5f0f\u8f38\u51fa" }, { "signature.algorithm.name", "\u7c3d\u7ae0\u6f14\u7b97\u6cd5\u540d\u7a31" }, { "source.alias", "\u4f86\u6e90\u5225\u540d" }, { "source.key.password", "\u4f86\u6e90\u91d1\u9470\u5bc6\u78bc" }, { "source.keystore.name", "\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u540d\u7a31" }, { "source.keystore.password.protected", "\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc\u4fdd\u8b77" }, { "source.keystore.provider.name", "\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u63d0\u4f9b\u8005\u540d\u7a31" }, { "source.keystore.password", "\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc" }, { "source.keystore.type", "\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u985e\u578b" }, { "SSL.server.host.and.port", "SSL \u4f3a\u670d\u5668\u4e3b\u6a5f\u8207\u9023\u63a5\u57e0" }, { "signed.jar.file", "\u7c3d\u7f72\u7684 jar \u6a94\u6848" }, { "certificate.validity.start.date.time", "\u6191\u8b49\u6709\u6548\u6027\u958b\u59cb\u65e5\u671f/\u6642\u9593" }, { "keystore.password", "\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc" }, { "keystore.type", "\u91d1\u9470\u5132\u5b58\u5eab\u985e\u578b" }, { "trust.certificates.from.cacerts", "\u4f86\u81ea cacerts \u7684\u4fe1\u4efb\u6191\u8b49" }, { "verbose.output", "\u8a73\u7d30\u8cc7\u8a0a\u8f38\u51fa" }, { "validity.number.of.days", "\u6709\u6548\u6027\u65e5\u6578" }, { "Serial.ID.of.cert.to.revoke", "\u8981\u64a4\u92b7\u6191\u8b49\u7684\u5e8f\u5217 ID" }, { "keytool.error.", "\u91d1\u9470\u5de5\u5177\u932f\u8aa4: " }, { "Illegal.option.", "\u7121\u6548\u7684\u9078\u9805:" }, { "Illegal.value.", "\u7121\u6548\u503c: " }, { "Unknown.password.type.", "\u4e0d\u660e\u7684\u5bc6\u78bc\u985e\u578b: " }, { "Cannot.find.environment.variable.", "\u627e\u4e0d\u5230\u74b0\u5883\u8b8a\u6578: " }, { "Cannot.find.file.", "\u627e\u4e0d\u5230\u6a94\u6848: " }, { "Command.option.flag.needs.an.argument.", "\u547d\u4ee4\u9078\u9805 {0} \u9700\u8981\u5f15\u6578\u3002" }, { "Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value.", "\u8b66\u544a: PKCS12 \u91d1\u9470\u5132\u5b58\u5eab\u4e0d\u652f\u63f4\u4e0d\u540c\u7684\u5132\u5b58\u5eab\u548c\u91d1\u9470\u5bc6\u78bc\u3002\u5ffd\u7565\u4f7f\u7528\u8005\u6307\u5b9a\u7684 {0} \u503c\u3002" }, { ".keystore.must.be.NONE.if.storetype.is.{0}", "\u5982\u679c -storetype \u70ba {0}\uff0c\u5247 -keystore \u5fc5\u9808\u70ba NONE" }, { "Too.many.retries.program.terminated", "\u91cd\u8a66\u6b21\u6578\u592a\u591a\uff0c\u7a0b\u5f0f\u5df2\u7d42\u6b62" }, { ".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}", "\u5982\u679c -storetype \u70ba {0}\uff0c\u5247\u4e0d\u652f\u63f4 -storepasswd \u548c -keypasswd \u547d\u4ee4" }, { ".keypasswd.commands.not.supported.if.storetype.is.PKCS12", "\u5982\u679c -storetype \u70ba PKCS12\uff0c\u5247\u4e0d\u652f\u63f4 -keypasswd \u547d\u4ee4" }, { ".keypass.and.new.can.not.be.specified.if.storetype.is.{0}", "\u5982\u679c -storetype \u70ba {0}\uff0c\u5247\u4e0d\u80fd\u6307\u5b9a -keypass \u548c -new" }, { "if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified", "\u5982\u679c\u6307\u5b9a -protected\uff0c\u5247\u4e0d\u80fd\u6307\u5b9a -storepass\u3001-keypass \u548c -new" }, { "if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified", "\u5982\u679c\u6307\u5b9a -srcprotected\uff0c\u5247\u4e0d\u80fd\u6307\u5b9a -srcstorepass \u548c -srckeypass" }, { "if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified", "\u5982\u679c\u91d1\u9470\u5132\u5b58\u5eab\u4e0d\u53d7\u5bc6\u78bc\u4fdd\u8b77\uff0c\u5247\u4e0d\u80fd\u6307\u5b9a -storepass\u3001-keypass \u548c -new" }, { "if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified", "\u5982\u679c\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u4e0d\u53d7\u5bc6\u78bc\u4fdd\u8b77\uff0c\u5247\u4e0d\u80fd\u6307\u5b9a -srcstorepass \u548c -srckeypass" }, { "Illegal.startdate.value", "\u7121\u6548\u7684 startdate \u503c" }, { "Validity.must.be.greater.than.zero", "\u6709\u6548\u6027\u5fc5\u9808\u5927\u65bc\u96f6" }, { "provName.not.a.provider", "{0} \u4e0d\u662f\u4e00\u500b\u63d0\u4f9b\u8005" }, { "Usage.error.no.command.provided", "\u7528\u6cd5\u932f\u8aa4: \u672a\u63d0\u4f9b\u547d\u4ee4" }, { "Source.keystore.file.exists.but.is.empty.", "\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u6a94\u6848\u5b58\u5728\uff0c\u4f46\u70ba\u7a7a: " }, { "Please.specify.srckeystore", "\u8acb\u6307\u5b9a -srckeystore" }, { "Must.not.specify.both.v.and.rfc.with.list.command", " 'list' \u547d\u4ee4\u4e0d\u80fd\u540c\u6642\u6307\u5b9a -v \u53ca -rfc" }, { "Key.password.must.be.at.least.6.characters", "\u91d1\u9470\u5bc6\u78bc\u5fc5\u9808\u81f3\u5c11\u70ba 6 \u500b\u5b57\u5143" }, { "New.password.must.be.at.least.6.characters", "\u65b0\u7684\u5bc6\u78bc\u5fc5\u9808\u81f3\u5c11\u70ba 6 \u500b\u5b57\u5143" }, { "Keystore.file.exists.but.is.empty.", "\u91d1\u9470\u5132\u5b58\u5eab\u6a94\u6848\u5b58\u5728\uff0c\u4f46\u70ba\u7a7a\u767d: " }, { "Keystore.file.does.not.exist.", "\u91d1\u9470\u5132\u5b58\u5eab\u6a94\u6848\u4e0d\u5b58\u5728: " }, { "Must.specify.destination.alias", "\u5fc5\u9808\u6307\u5b9a\u76ee\u7684\u5730\u5225\u540d" }, { "Must.specify.alias", "\u5fc5\u9808\u6307\u5b9a\u5225\u540d" }, { "Keystore.password.must.be.at.least.6.characters", "\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc\u5fc5\u9808\u81f3\u5c11\u70ba 6 \u500b\u5b57\u5143" }, { "Enter.keystore.password.", "\u8f38\u5165\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc:  " }, { "Enter.source.keystore.password.", "\u8acb\u8f38\u5165\u4f86\u6e90\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc: " }, { "Enter.destination.keystore.password.", "\u8acb\u8f38\u5165\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc: " }, { "Keystore.password.is.too.short.must.be.at.least.6.characters", "\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc\u592a\u77ed - \u5fc5\u9808\u81f3\u5c11\u70ba 6 \u500b\u5b57\u5143" }, { "Unknown.Entry.Type", "\u4e0d\u660e\u7684\u9805\u76ee\u985e\u578b" }, { "Too.many.failures.Alias.not.changed", "\u592a\u591a\u932f\u8aa4\u3002\u672a\u8b8a\u66f4\u5225\u540d" }, { "Entry.for.alias.alias.successfully.imported.", "\u5df2\u6210\u529f\u532f\u5165\u5225\u540d {0} \u7684\u9805\u76ee\u3002" }, { "Entry.for.alias.alias.not.imported.", "\u672a\u532f\u5165\u5225\u540d {0} \u7684\u9805\u76ee\u3002" }, { "Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported.", "\u532f\u5165\u5225\u540d {0} \u7684\u9805\u76ee\u6642\u51fa\u73fe\u554f\u984c: {1}\u3002\n\u672a\u532f\u5165\u5225\u540d {0} \u7684\u9805\u76ee\u3002" }, { "Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled", "\u5df2\u5b8c\u6210\u532f\u5165\u547d\u4ee4: \u6210\u529f\u532f\u5165 {0} \u500b\u9805\u76ee\uff0c{1} \u500b\u9805\u76ee\u5931\u6557\u6216\u5df2\u53d6\u6d88" }, { "Warning.Overwriting.existing.alias.alias.in.destination.keystore", "\u8b66\u544a: \u6b63\u5728\u8986\u5beb\u76ee\u7684\u5730\u91d1\u9470\u5132\u5b58\u5eab\u4e2d\u7684\u73fe\u6709\u5225\u540d {0}" }, { "Existing.entry.alias.alias.exists.overwrite.no.", "\u73fe\u6709\u9805\u76ee\u5225\u540d {0} \u5b58\u5728\uff0c\u662f\u5426\u8986\u5beb\uff1f[\u5426]:  " }, { "Too.many.failures.try.later", "\u592a\u591a\u932f\u8aa4 - \u8acb\u7a0d\u5f8c\u518d\u8a66" }, { "Certification.request.stored.in.file.filename.", "\u8a8d\u8b49\u8981\u6c42\u5132\u5b58\u5728\u6a94\u6848 <{0}>" }, { "Submit.this.to.your.CA", "\u5c07\u6b64\u9001\u51fa\u81f3\u60a8\u7684 CA" }, { "if.alias.not.specified.destalias.srckeypass.and.destkeypass.must.not.be.specified", "\u5982\u679c\u672a\u6307\u5b9a\u5225\u540d\uff0c\u5247\u4e0d\u80fd\u6307\u5b9a destalias\u3001srckeypass \u53ca destkeypass" }, { "Certificate.stored.in.file.filename.", "\u6191\u8b49\u5132\u5b58\u5728\u6a94\u6848 <{0}>" }, { "Certificate.reply.was.installed.in.keystore", "\u6191\u8b49\u56de\u8986\u5df2\u5b89\u88dd\u5728\u91d1\u9470\u5132\u5b58\u5eab\u4e2d" }, { "Certificate.reply.was.not.installed.in.keystore", "\u6191\u8b49\u56de\u8986\u672a\u5b89\u88dd\u5728\u91d1\u9470\u5132\u5b58\u5eab\u4e2d" }, { "Certificate.was.added.to.keystore", "\u6191\u8b49\u5df2\u65b0\u589e\u81f3\u91d1\u9470\u5132\u5b58\u5eab\u4e2d" }, { "Certificate.was.not.added.to.keystore", "\u6191\u8b49\u672a\u65b0\u589e\u81f3\u91d1\u9470\u5132\u5b58\u5eab\u4e2d" }, { ".Storing.ksfname.", "[\u5132\u5b58 {0}]" }, { "alias.has.no.public.key.certificate.", "{0} \u6c92\u6709\u516c\u958b\u91d1\u9470 (\u6191\u8b49)" }, { "Cannot.derive.signature.algorithm", "\u7121\u6cd5\u53d6\u5f97\u7c3d\u7ae0\u6f14\u7b97\u6cd5" }, { "Alias.alias.does.not.exist", "\u5225\u540d <{0}> \u4e0d\u5b58\u5728" }, { "Alias.alias.has.no.certificate", "\u5225\u540d <{0}> \u6c92\u6709\u6191\u8b49" }, { "Key.pair.not.generated.alias.alias.already.exists", "\u6c92\u6709\u5efa\u7acb\u91d1\u9470\u7d44\uff0c\u5225\u540d <{0}> \u5df2\u7d93\u5b58\u5728" }, { "Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for", "\u91dd\u5c0d {4} \u7522\u751f\u6709\u6548\u671f {3} \u5929\u7684 {0} \u4f4d\u5143 {1} \u91d1\u9470\u7d44\u4ee5\u53ca\u81ea\u6211\u7c3d\u7f72\u6191\u8b49 ({2})\n\t" }, { "Enter.key.password.for.alias.", "\u8f38\u5165 <{0}> \u7684\u91d1\u9470\u5bc6\u78bc" }, { ".RETURN.if.same.as.keystore.password.", "\t(RETURN \u5982\u679c\u548c\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc\u76f8\u540c):  " }, { "Key.password.is.too.short.must.be.at.least.6.characters", "\u91d1\u9470\u5bc6\u78bc\u592a\u77ed - \u5fc5\u9808\u81f3\u5c11\u70ba 6 \u500b\u5b57\u5143" }, { "Too.many.failures.key.not.added.to.keystore", "\u592a\u591a\u932f\u8aa4 - \u91d1\u9470\u672a\u65b0\u589e\u81f3\u91d1\u9470\u5132\u5b58\u5eab" }, { "Destination.alias.dest.already.exists", "\u76ee\u7684\u5730\u5225\u540d <{0}> \u5df2\u7d93\u5b58\u5728" }, { "Password.is.too.short.must.be.at.least.6.characters", "\u5bc6\u78bc\u592a\u77ed - \u5fc5\u9808\u81f3\u5c11\u70ba 6 \u500b\u5b57\u5143" }, { "Too.many.failures.Key.entry.not.cloned", "\u592a\u591a\u932f\u8aa4\u3002\u672a\u8907\u88fd\u91d1\u9470\u9805\u76ee" }, { "key.password.for.alias.", "<{0}> \u7684\u91d1\u9470\u5bc6\u78bc" }, { "Keystore.entry.for.id.getName.already.exists", "<{0}> \u7684\u91d1\u9470\u5132\u5b58\u5eab\u9805\u76ee\u5df2\u7d93\u5b58\u5728" }, { "Creating.keystore.entry.for.id.getName.", "\u5efa\u7acb <{0}> \u7684\u91d1\u9470\u5132\u5b58\u5eab\u9805\u76ee..." }, { "No.entries.from.identity.database.added", "\u6c92\u6709\u65b0\u589e\u4f86\u81ea\u8b58\u5225\u8cc7\u6599\u5eab\u7684\u9805\u76ee" }, { "Alias.name.alias", "\u5225\u540d\u540d\u7a31: {0}" }, { "Creation.date.keyStore.getCreationDate.alias.", "\u5efa\u7acb\u65e5\u671f: {0,date}" }, { "alias.keyStore.getCreationDate.alias.", "{0}, {1,date}, " }, { "alias.", "{0}, " }, { "Entry.type.type.", "\u9805\u76ee\u985e\u578b: {0}" }, { "Certificate.chain.length.", "\u6191\u8b49\u93c8\u9577\u5ea6: " }, { "Certificate.i.1.", "\u6191\u8b49 [{0,number,integer}]:" }, { "Certificate.fingerprint.SHA1.", "\u6191\u8b49\u6307\u7d0b (SHA1): " }, { "Keystore.type.", "\u91d1\u9470\u5132\u5b58\u5eab\u985e\u578b: " }, { "Keystore.provider.", "\u91d1\u9470\u5132\u5b58\u5eab\u63d0\u4f9b\u8005: " }, { "Your.keystore.contains.keyStore.size.entry", "\u60a8\u7684\u91d1\u9470\u5132\u5b58\u5eab\u5305\u542b {0,number,integer} \u9805\u76ee" }, { "Your.keystore.contains.keyStore.size.entries", "\u60a8\u7684\u91d1\u9470\u5132\u5b58\u5eab\u5305\u542b {0,number,integer} \u9805\u76ee" }, { "Failed.to.parse.input", "\u7121\u6cd5\u5256\u6790\u8f38\u5165" }, { "Empty.input", "\u7a7a\u8f38\u5165" }, { "Not.X.509.certificate", "\u975e X.509 \u6191\u8b49" }, { "alias.has.no.public.key", "{0} \u7121\u516c\u958b\u91d1\u9470" }, { "alias.has.no.X.509.certificate", "{0} \u7121 X.509 \u6191\u8b49" }, { "New.certificate.self.signed.", "\u65b0\u6191\u8b49 (\u81ea\u6211\u7c3d\u7f72): " }, { "Reply.has.no.certificates", "\u56de\u8986\u4e0d\u542b\u6191\u8b49" }, { "Certificate.not.imported.alias.alias.already.exists", "\u6191\u8b49\u672a\u8f38\u5165\uff0c\u5225\u540d <{0}> \u5df2\u7d93\u5b58\u5728" }, { "Input.not.an.X.509.certificate", "\u8f38\u5165\u7684\u4e0d\u662f X.509 \u6191\u8b49" }, { "Certificate.already.exists.in.keystore.under.alias.trustalias.", "\u91d1\u9470\u5132\u5b58\u5eab\u4e2d\u7684 <{0}> \u5225\u540d\u4e4b\u4e0b\uff0c\u6191\u8b49\u5df2\u7d93\u5b58\u5728" }, { "Do.you.still.want.to.add.it.no.", "\u60a8\u4ecd\u7136\u60f3\u8981\u5c07\u4e4b\u65b0\u589e\u55ce\uff1f [\u5426]:  " }, { "Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias.", "\u6574\u500b\u7cfb\u7d71 CA \u91d1\u9470\u5132\u5b58\u5eab\u4e2d\u7684 <{0}> \u5225\u540d\u4e4b\u4e0b\uff0c\u6191\u8b49\u5df2\u7d93\u5b58\u5728" }, { "Do.you.still.want.to.add.it.to.your.own.keystore.no.", "\u60a8\u4ecd\u7136\u60f3\u8981\u5c07\u4e4b\u65b0\u589e\u81f3\u81ea\u5df1\u7684\u91d1\u9470\u5132\u5b58\u5eab\u55ce\uff1f [\u5426]:  " }, { "Trust.this.certificate.no.", "\u4fe1\u4efb\u9019\u500b\u6191\u8b49\uff1f [\u5426]:  " }, { "YES", "\u662f" }, { "New.prompt.", "\u65b0 {0}: " }, { "Passwords.must.differ", "\u5fc5\u9808\u662f\u4e0d\u540c\u7684\u5bc6\u78bc" }, { "Re.enter.new.prompt.", "\u91cd\u65b0\u8f38\u5165\u65b0 {0}: " }, { "Re.enter.new.password.", "\u91cd\u65b0\u8f38\u5165\u65b0\u5bc6\u78bc: " }, { "They.don.t.match.Try.again", "\u5b83\u5011\u4e0d\u76f8\u7b26\u3002\u8acb\u91cd\u8a66" }, { "Enter.prompt.alias.name.", "\u8f38\u5165 {0} \u5225\u540d\u540d\u7a31:  " }, { "Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry.", "\u8acb\u8f38\u5165\u65b0\u7684\u5225\u540d\u540d\u7a31\t(RETURN \u4ee5\u53d6\u6d88\u532f\u5165\u6b64\u9805\u76ee):" }, { "Enter.alias.name.", "\u8f38\u5165\u5225\u540d\u540d\u7a31:  " }, { ".RETURN.if.same.as.for.otherAlias.", "\t(RETURN \u5982\u679c\u548c <{0}> \u7684\u76f8\u540c)" }, { ".PATTERN.printX509Cert", "\u64c1\u6709\u8005: {0}\n\u767c\u51fa\u8005: {1}\n\u5e8f\u865f: {2}\n\u6709\u6548\u671f\u81ea: {3} \u5230: {4}\n\u6191\u8b49\u6307\u7d0b:\n\t MD5:  {5}\n\t SHA1: {6}\n\t SHA256: {7}\n\t \u7c3d\u7ae0\u6f14\u7b97\u6cd5\u540d\u7a31: {8}\n\t \u7248\u672c: {9}" }, { "What.is.your.first.and.last.name.", "\u60a8\u7684\u540d\u5b57\u8207\u59d3\u6c0f\u70ba\u4f55\uff1f" }, { "What.is.the.name.of.your.organizational.unit.", "\u60a8\u7684\u7d44\u7e54\u55ae\u4f4d\u540d\u7a31\u70ba\u4f55\uff1f" }, { "What.is.the.name.of.your.organization.", "\u60a8\u7684\u7d44\u7e54\u540d\u7a31\u70ba\u4f55\uff1f" }, { "What.is.the.name.of.your.City.or.Locality.", "\u60a8\u6240\u5728\u7684\u57ce\u5e02\u6216\u5730\u5340\u540d\u7a31\u70ba\u4f55\uff1f" }, { "What.is.the.name.of.your.State.or.Province.", "\u60a8\u6240\u5728\u7684\u5dde\u53ca\u7701\u4efd\u540d\u7a31\u70ba\u4f55\uff1f" }, { "What.is.the.two.letter.country.code.for.this.unit.", "\u6b64\u55ae\u4f4d\u7684\u5169\u500b\u5b57\u6bcd\u570b\u5225\u4ee3\u78bc\u70ba\u4f55\uff1f" }, { "Is.name.correct.", "{0} \u6b63\u78ba\u55ce\uff1f" }, { "no", "\u5426" }, { "yes", "\u662f" }, { "y", "y" }, { ".defaultValue.", "  [{0}]:  " }, { "Alias.alias.has.no.key", "\u5225\u540d <{0}> \u6c92\u6709\u91d1\u9470" }, { "Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key", "\u5225\u540d <{0}> \u6240\u53c3\u7167\u7684\u9805\u76ee\u4e0d\u662f\u79c1\u5bc6\u91d1\u9470\u985e\u578b\u3002-keyclone \u547d\u4ee4\u50c5\u652f\u63f4\u79c1\u5bc6\u91d1\u9470\u9805\u76ee\u7684\u8907\u88fd" }, { ".WARNING.WARNING.WARNING.", "*****************  \u8b66\u544a \u8b66\u544a \u8b66\u544a  *****************" }, { "Signer.d.", "\u7c3d\u7f72\u8005 #%d:" }, { "Timestamp.", "\u6642\u6233:" }, { "Signature.", "\u7c3d\u7ae0:" }, { "CRLs.", "CRL:" }, { "Certificate.owner.", "\u6191\u8b49\u64c1\u6709\u8005: " }, { "Not.a.signed.jar.file", "\u4e0d\u662f\u7c3d\u7f72\u7684 jar \u6a94\u6848" }, { "No.certificate.from.the.SSL.server", "\u6c92\u6709\u4f86\u81ea SSL \u4f3a\u670d\u5668\u7684\u6191\u8b49" }, { ".The.integrity.of.the.information.stored.in.your.keystore.", "* \u5c1a\u672a\u9a57\u8b49\u5132\u5b58\u65bc\u91d1\u9470\u5132\u5b58\u5eab\u4e2d\u8cc7\u8a0a  *\n* \u7684\u5b8c\u6574\u6027\uff01\u82e5\u8981\u9a57\u8b49\u5176\u5b8c\u6574\u6027\uff0c*\n* \u60a8\u5fc5\u9808\u63d0\u4f9b\u60a8\u7684\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc\u3002                  *" }, { ".The.integrity.of.the.information.stored.in.the.srckeystore.", "* \u5c1a\u672a\u9a57\u8b49\u5132\u5b58\u65bc srckeystore \u4e2d\u8cc7\u8a0a*\n* \u7684\u5b8c\u6574\u6027\uff01\u82e5\u8981\u9a57\u8b49\u5176\u5b8c\u6574\u6027\uff0c\u60a8\u5fc5\u9808 *\n* \u63d0\u4f9b srckeystore \u5bc6\u78bc\u3002          *" }, { "Certificate.reply.does.not.contain.public.key.for.alias.", "\u6191\u8b49\u56de\u8986\u4e26\u672a\u5305\u542b <{0}> \u7684\u516c\u958b\u91d1\u9470" }, { "Incomplete.certificate.chain.in.reply", "\u56de\u8986\u6642\u7684\u6191\u8b49\u93c8\u4e0d\u5b8c\u6574" }, { "Certificate.chain.in.reply.does.not.verify.", "\u56de\u8986\u6642\u7684\u6191\u8b49\u93c8\u672a\u9a57\u8b49: " }, { "Top.level.certificate.in.reply.", "\u56de\u8986\u6642\u7684\u6700\u9ad8\u7d1a\u6191\u8b49:\\n" }, { ".is.not.trusted.", "... \u662f\u4e0d\u88ab\u4fe1\u4efb\u7684\u3002" }, { "Install.reply.anyway.no.", "\u9084\u662f\u8981\u5b89\u88dd\u56de\u8986\uff1f [\u5426]:  " }, { "NO", "\u5426" }, { "Public.keys.in.reply.and.keystore.don.t.match", "\u56de\u8986\u6642\u7684\u516c\u958b\u91d1\u9470\u8207\u91d1\u9470\u5132\u5b58\u5eab\u4e0d\u7b26" }, { "Certificate.reply.and.certificate.in.keystore.are.identical", "\u6191\u8b49\u56de\u8986\u8207\u91d1\u9470\u5132\u5b58\u5eab\u4e2d\u7684\u6191\u8b49\u662f\u76f8\u540c\u7684" }, { "Failed.to.establish.chain.from.reply", "\u7121\u6cd5\u5f9e\u56de\u8986\u4e2d\u5c07\u93c8\u5efa\u7acb\u8d77\u4f86" }, { "n", "n" }, { "Wrong.answer.try.again", "\u932f\u8aa4\u7684\u7b54\u6848\uff0c\u8acb\u518d\u8a66\u4e00\u6b21" }, { "Secret.key.not.generated.alias.alias.already.exists", "\u672a\u7522\u751f\u79d8\u5bc6\u91d1\u9470\uff0c\u5225\u540d <{0}> \u5df2\u5b58\u5728" }, { "Please.provide.keysize.for.secret.key.generation", "\u8acb\u63d0\u4f9b -keysize \u4ee5\u7522\u751f\u79d8\u5bc6\u91d1\u9470" }, { "Extensions.", "\u64f4\u5145\u5957\u4ef6: " }, { ".Empty.value.", "(\u7a7a\u767d\u503c)" }, { "Extension.Request.", "\u64f4\u5145\u5957\u4ef6\u8981\u6c42:" }, { "PKCS.10.Certificate.Request.Version.1.0.Subject.s.Public.Key.s.format.s.key.", "PKCS #10 \u6191\u8b49\u8981\u6c42 (\u7248\u672c 1.0)\n\u4e3b\u9ad4: %s\n\u516c\u7528\u91d1\u9470: %s \u683c\u5f0f %s \u91d1\u9470\n" }, { "Unknown.keyUsage.type.", "\u4e0d\u660e\u7684 keyUsage \u985e\u578b: " }, { "Unknown.extendedkeyUsage.type.", "\u4e0d\u660e\u7684 extendedkeyUsage \u985e\u578b: " }, { "Unknown.AccessDescription.type.", "\u4e0d\u660e\u7684 AccessDescription \u985e\u578b: " }, { "Unrecognized.GeneralName.type.", "\u7121\u6cd5\u8fa8\u8b58\u7684 GeneralName \u985e\u578b: " }, { "This.extension.cannot.be.marked.as.critical.", "\u6b64\u64f4\u5145\u5957\u4ef6\u7121\u6cd5\u6a19\u793a\u70ba\u95dc\u9375\u3002" }, { "Odd.number.of.hex.digits.found.", "\u627e\u5230\u5341\u516d\u9032\u4f4d\u6578\u5b57\u7684\u5947\u6578: " }, { "Unknown.extension.type.", "\u4e0d\u660e\u7684\u64f4\u5145\u5957\u4ef6\u985e\u578b: " }, { "command.{0}.is.ambiguous.", "\u547d\u4ee4 {0} \u4e0d\u660e\u78ba:" } };
    }
}