package sun.security.tools.policytool;

import java.util.ListResourceBundle;

public class Resources_zh_HK extends ListResourceBundle
{
    private static final Object[][] contents;
    
    public Object[][] getContents() {
        return Resources_zh_HK.contents;
    }
    
    static {
        contents = new Object[][] { { "NEWLINE", "\n" }, { "Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.", "\u8b66\u544a: \u5225\u540d {0} \u7684\u516c\u958b\u91d1\u9470\u4e0d\u5b58\u5728\u3002\u8acb\u78ba\u5b9a\u91d1\u9470\u5132\u5b58\u5eab\u914d\u7f6e\u6b63\u78ba\u3002" }, { "Warning.Class.not.found.class", "\u8b66\u544a: \u627e\u4e0d\u5230\u985e\u5225 {0}" }, { "Warning.Invalid.argument.s.for.constructor.arg", "\u8b66\u544a: \u7121\u6548\u7684\u5efa\u69cb\u5b50\u5f15\u6578: {0}" }, { "Illegal.Principal.Type.type", "\u7121\u6548\u7684 Principal \u985e\u578b: {0}" }, { "Illegal.option.option", "\u7121\u6548\u7684\u9078\u9805: {0}" }, { "Usage.policytool.options.", "\u7528\u6cd5: policytool [options]" }, { ".file.file.policy.file.location", "  [-file <file>]    \u539f\u5247\u6a94\u6848\u4f4d\u7f6e" }, { "New", "\u65b0\u589e" }, { "Open", "\u958b\u555f" }, { "Save", "\u5132\u5b58" }, { "Save.As", "\u53e6\u5b58\u65b0\u6a94" }, { "View.Warning.Log", "\u6aa2\u8996\u8b66\u544a\u8a18\u9304" }, { "Exit", "\u7d50\u675f" }, { "Add.Policy.Entry", "\u65b0\u589e\u539f\u5247\u9805\u76ee" }, { "Edit.Policy.Entry", "\u7de8\u8f2f\u539f\u5247\u9805\u76ee" }, { "Remove.Policy.Entry", "\u79fb\u9664\u539f\u5247\u9805\u76ee" }, { "Edit", "\u7de8\u8f2f" }, { "Retain", "\u4fdd\u7559" }, { "Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes", "\u8b66\u544a: \u6a94\u6848\u540d\u7a31\u5305\u542b\u9041\u96e2\u53cd\u659c\u7dda\u5b57\u5143\u3002\u4e0d\u9700\u8981\u9041\u96e2\u53cd\u659c\u7dda\u5b57\u5143 (\u64b0\u5beb\u539f\u5247\u5167\u5bb9\u81f3\u6c38\u4e45\u5b58\u653e\u5340\u6642\u9700\u8981\u5de5\u5177\u9041\u96e2\u5b57\u5143)\u3002\n\n\u6309\u4e00\u4e0b\u300c\u4fdd\u7559\u300d\u4ee5\u4fdd\u7559\u8f38\u5165\u7684\u540d\u7a31\uff0c\u6216\u6309\u4e00\u4e0b\u300c\u7de8\u8f2f\u300d\u4ee5\u7de8\u8f2f\u540d\u7a31\u3002" }, { "Add.Public.Key.Alias", "\u65b0\u589e\u516c\u958b\u91d1\u9470\u5225\u540d" }, { "Remove.Public.Key.Alias", "\u79fb\u9664\u516c\u958b\u91d1\u9470\u5225\u540d" }, { "File", "\u6a94\u6848" }, { "KeyStore", "\u91d1\u9470\u5132\u5b58\u5eab" }, { "Policy.File.", "\u539f\u5247\u6a94\u6848: " }, { "Could.not.open.policy.file.policyFile.e.toString.", "\u7121\u6cd5\u958b\u555f\u539f\u5247\u6a94\u6848: {0}: {1}" }, { "Policy.Tool", "\u539f\u5247\u5de5\u5177" }, { "Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.", "\u958b\u555f\u539f\u5247\u8a18\u7f6e\u6642\u767c\u751f\u932f\u8aa4\u3002\u8acb\u6aa2\u8996\u8b66\u544a\u8a18\u9304\u4ee5\u53d6\u5f97\u66f4\u591a\u7684\u8cc7\u8a0a" }, { "Error", "\u932f\u8aa4" }, { "OK", "\u78ba\u5b9a" }, { "Status", "\u72c0\u614b" }, { "Warning", "\u8b66\u544a" }, { "Permission.", "\u6b0a\u9650:                                                       " }, { "Principal.Type.", "Principal \u985e\u578b: " }, { "Principal.Name.", "Principal \u540d\u7a31: " }, { "Target.Name.", "\u76ee\u6a19\u540d\u7a31:                                                    " }, { "Actions.", "\u52d5\u4f5c:                                                             " }, { "OK.to.overwrite.existing.file.filename.", "\u78ba\u8a8d\u8986\u5beb\u73fe\u5b58\u7684\u6a94\u6848 {0}\uff1f" }, { "Cancel", "\u53d6\u6d88" }, { "CodeBase.", "CodeBase:" }, { "SignedBy.", "SignedBy:" }, { "Add.Principal", "\u65b0\u589e Principal" }, { "Edit.Principal", "\u7de8\u8f2f Principal" }, { "Remove.Principal", "\u79fb\u9664 Principal" }, { "Principals.", "Principal:" }, { ".Add.Permission", "  \u65b0\u589e\u6b0a\u9650" }, { ".Edit.Permission", "  \u7de8\u8f2f\u6b0a\u9650" }, { "Remove.Permission", "\u79fb\u9664\u6b0a\u9650" }, { "Done", "\u5b8c\u6210" }, { "KeyStore.URL.", "\u91d1\u9470\u5132\u5b58\u5eab URL: " }, { "KeyStore.Type.", "\u91d1\u9470\u5132\u5b58\u5eab\u985e\u578b:" }, { "KeyStore.Provider.", "\u91d1\u9470\u5132\u5b58\u5eab\u63d0\u4f9b\u8005:" }, { "KeyStore.Password.URL.", "\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc URL: " }, { "Principals", "Principal" }, { ".Edit.Principal.", "  \u7de8\u8f2f Principal: " }, { ".Add.New.Principal.", "  \u65b0\u589e Principal: " }, { "Permissions", "\u6b0a\u9650" }, { ".Edit.Permission.", "  \u7de8\u8f2f\u6b0a\u9650:" }, { ".Add.New.Permission.", "  \u65b0\u589e\u6b0a\u9650:" }, { "Signed.By.", "\u7c3d\u7f72\u4eba: " }, { "Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name", "\u6c92\u6709\u842c\u7528\u5b57\u5143\u540d\u7a31\uff0c\u7121\u6cd5\u6307\u5b9a\u542b\u6709\u842c\u7528\u5b57\u5143\u985e\u5225\u7684 Principal" }, { "Cannot.Specify.Principal.without.a.Name", "\u6c92\u6709\u540d\u7a31\uff0c\u7121\u6cd5\u6307\u5b9a Principal" }, { "Permission.and.Target.Name.must.have.a.value", "\u6b0a\u9650\u53ca\u76ee\u6a19\u540d\u7a31\u5fc5\u9808\u6709\u4e00\u500b\u503c\u3002" }, { "Remove.this.Policy.Entry.", "\u79fb\u9664\u9019\u500b\u539f\u5247\u9805\u76ee\uff1f" }, { "Overwrite.File", "\u8986\u5beb\u6a94\u6848" }, { "Policy.successfully.written.to.filename", "\u539f\u5247\u6210\u529f\u5beb\u5165\u81f3 {0}" }, { "null.filename", "\u7a7a\u503c\u6a94\u540d" }, { "Save.changes.", "\u5132\u5b58\u8b8a\u66f4\uff1f" }, { "Yes", "\u662f" }, { "No", "\u5426" }, { "Policy.Entry", "\u539f\u5247\u9805\u76ee" }, { "Save.Changes", "\u5132\u5b58\u8b8a\u66f4" }, { "No.Policy.Entry.selected", "\u6c92\u6709\u9078\u53d6\u539f\u5247\u9805\u76ee" }, { "Unable.to.open.KeyStore.ex.toString.", "\u7121\u6cd5\u958b\u555f\u91d1\u9470\u5132\u5b58\u5eab: {0}" }, { "No.principal.selected", "\u672a\u9078\u53d6 Principal" }, { "No.permission.selected", "\u6c92\u6709\u9078\u53d6\u6b0a\u9650" }, { "name", "\u540d\u7a31" }, { "configuration.type", "\u7d44\u614b\u985e\u578b" }, { "environment.variable.name", "\u74b0\u5883\u8b8a\u6578\u540d\u7a31" }, { "library.name", "\u7a0b\u5f0f\u5eab\u540d\u7a31" }, { "package.name", "\u5957\u88dd\u7a0b\u5f0f\u540d\u7a31" }, { "policy.type", "\u539f\u5247\u985e\u578b" }, { "property.name", "\u5c6c\u6027\u540d\u7a31" }, { "provider.name", "\u63d0\u4f9b\u8005\u540d\u7a31" }, { "Principal.List", "Principal \u6e05\u55ae" }, { "Permission.List", "\u6b0a\u9650\u6e05\u55ae" }, { "Code.Base", "\u4ee3\u78bc\u57fa\u6e96" }, { "KeyStore.U.R.L.", "\u91d1\u9470\u5132\u5b58\u5eab URL:" }, { "KeyStore.Password.U.R.L.", "\u91d1\u9470\u5132\u5b58\u5eab\u5bc6\u78bc URL:" } };
    }
}
