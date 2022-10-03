package sun.security.util;

import java.util.ListResourceBundle;

public class Resources_pt_BR extends ListResourceBundle
{
    private static final Object[][] contents;
    
    public Object[][] getContents() {
        return Resources_pt_BR.contents;
    }
    
    static {
        contents = new Object[][] { { "invalid.null.input.s.", "entrada(s) nula(s) inv\u00e1lida(s)" }, { "actions.can.only.be.read.", "as a\u00e7\u00f5es s\u00f3 podem ser 'lidas'" }, { "permission.name.name.syntax.invalid.", "sintaxe inv\u00e1lida do nome da permiss\u00e3o [{0}]: " }, { "Credential.Class.not.followed.by.a.Principal.Class.and.Name", "Classe da Credencial n\u00e3o seguida por um Nome e uma Classe do Principal" }, { "Principal.Class.not.followed.by.a.Principal.Name", "Classe do Principal n\u00e3o seguida por um Nome do Principal" }, { "Principal.Name.must.be.surrounded.by.quotes", "O Nome do Principal deve estar entre aspas" }, { "Principal.Name.missing.end.quote", "Faltam as aspas finais no Nome do Principal" }, { "PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value", "A Classe do Principal PrivateCredentialPermission n\u00e3o pode ser um valor curinga (*) se o Nome do Principal n\u00e3o for um valor curinga (*)" }, { "CredOwner.Principal.Class.class.Principal.Name.name", "CredOwner:\n\tClasse do Principal = {0}\n\tNome do Principal = {1}" }, { "provided.null.name", "nome nulo fornecido" }, { "provided.null.keyword.map", "mapa de palavra-chave nulo fornecido" }, { "provided.null.OID.map", "mapa OID nulo fornecido" }, { "NEWLINE", "\n" }, { "invalid.null.AccessControlContext.provided", "AccessControlContext nulo inv\u00e1lido fornecido" }, { "invalid.null.action.provided", "a\u00e7\u00e3o nula inv\u00e1lida fornecida" }, { "invalid.null.Class.provided", "Classe nula inv\u00e1lida fornecida" }, { "Subject.", "Assunto:\n" }, { ".Principal.", "\tPrincipal: " }, { ".Public.Credential.", "\tCredencial P\u00fablica: " }, { ".Private.Credentials.inaccessible.", "\tCredenciais Privadas inacess\u00edveis\n" }, { ".Private.Credential.", "\tCredencial Privada: " }, { ".Private.Credential.inaccessible.", "\tCredencial Privada inacess\u00edvel\n" }, { "Subject.is.read.only", "O Assunto \u00e9 somente para leitura" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.java.security.Principal.to.a.Subject.s.Principal.Set", "tentativa de adicionar um objeto que n\u00e3o \u00e9 uma inst\u00e2ncia de java.security.Principal a um conjunto de principais do Subject" }, { "attempting.to.add.an.object.which.is.not.an.instance.of.class", "tentativa de adicionar um objeto que n\u00e3o \u00e9 uma inst\u00e2ncia de {0}" }, { "LoginModuleControlFlag.", "LoginModuleControlFlag: " }, { "Invalid.null.input.name", "Entrada nula inv\u00e1lida: nome" }, { "No.LoginModules.configured.for.name", "Nenhum LoginModule configurado para {0}" }, { "invalid.null.Subject.provided", "Subject nulo inv\u00e1lido fornecido" }, { "invalid.null.CallbackHandler.provided", "CallbackHandler nulo inv\u00e1lido fornecido" }, { "null.subject.logout.called.before.login", "Subject nulo - log-out chamado antes do log-in" }, { "unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor", "n\u00e3o \u00e9 poss\u00edvel instanciar LoginModule, {0}, porque ele n\u00e3o fornece um construtor sem argumento" }, { "unable.to.instantiate.LoginModule", "n\u00e3o \u00e9 poss\u00edvel instanciar LoginModule" }, { "unable.to.instantiate.LoginModule.", "n\u00e3o \u00e9 poss\u00edvel instanciar LoginModule: " }, { "unable.to.find.LoginModule.class.", "n\u00e3o \u00e9 poss\u00edvel localizar a classe LoginModule: " }, { "unable.to.access.LoginModule.", "n\u00e3o \u00e9 poss\u00edvel acessar LoginModule: " }, { "Login.Failure.all.modules.ignored", "Falha de Log-in: todos os m\u00f3dulos ignorados" }, { "java.security.policy.error.parsing.policy.message", "java.security.policy: erro durante o parsing de {0}:\n\t{1}" }, { "java.security.policy.error.adding.Permission.perm.message", "java.security.policy: erro ao adicionar a permiss\u00e3o, {0}:\n\t{1}" }, { "java.security.policy.error.adding.Entry.message", "java.security.policy: erro ao adicionar a Entrada:\n\t{0}" }, { "alias.name.not.provided.pe.name.", "nome de alias n\u00e3o fornecido ({0})" }, { "unable.to.perform.substitution.on.alias.suffix", "n\u00e3o \u00e9 poss\u00edvel realizar a substitui\u00e7\u00e3o no alias, {0}" }, { "substitution.value.prefix.unsupported", "valor da substitui\u00e7\u00e3o, {0}, n\u00e3o suportado" }, { "LPARAM", "(" }, { "RPARAM", ")" }, { "type.can.t.be.null", "o tipo n\u00e3o pode ser nulo" }, { "keystorePasswordURL.can.not.be.specified.without.also.specifying.keystore", "keystorePasswordURL n\u00e3o pode ser especificado sem que a \u00e1rea de armazenamento de chaves tamb\u00e9m seja especificada" }, { "expected.keystore.type", "tipo de armazenamento de chaves esperado" }, { "expected.keystore.provider", "fornecedor da \u00e1rea de armazenamento de chaves esperado" }, { "multiple.Codebase.expressions", "v\u00e1rias express\u00f5es CodeBase" }, { "multiple.SignedBy.expressions", "v\u00e1rias express\u00f5es SignedBy" }, { "duplicate.keystore.domain.name", "nome do dom\u00ednio da \u00e1rea de armazenamento de teclas duplicado: {0}" }, { "duplicate.keystore.name", "nome da \u00e1rea de armazenamento de chaves duplicado: {0}" }, { "SignedBy.has.empty.alias", "SignedBy tem alias vazio" }, { "can.not.specify.Principal.with.a.wildcard.class.without.a.wildcard.name", "n\u00e3o \u00e9 poss\u00edvel especificar um principal com uma classe curinga sem um nome curinga" }, { "expected.codeBase.or.SignedBy.or.Principal", "CodeBase ou SignedBy ou Principal esperado" }, { "expected.permission.entry", "entrada de permiss\u00e3o esperada" }, { "number.", "n\u00famero " }, { "expected.expect.read.end.of.file.", "esperado [{0}], lido [fim do arquivo]" }, { "expected.read.end.of.file.", "esperado [;], lido [fim do arquivo]" }, { "line.number.msg", "linha {0}: {1}" }, { "line.number.expected.expect.found.actual.", "linha {0}: esperada [{1}], encontrada [{2}]" }, { "null.principalClass.or.principalName", "principalClass ou principalName nulo" }, { "PKCS11.Token.providerName.Password.", "Senha PKCS11 de Token [{0}]: " }, { "unable.to.instantiate.Subject.based.policy", "n\u00e3o \u00e9 poss\u00edvel instanciar a pol\u00edtica com base em Subject" } };
    }
}