package sun.applet.resources;

import java.util.ListResourceBundle;

public class MsgAppletViewer_pt_BR extends ListResourceBundle
{
    public Object[][] getContents() {
        return new Object[][] { { "textframe.button.dismiss", "Rejeitar" }, { "appletviewer.tool.title", "Visualizador do Applet: {0}" }, { "appletviewer.menu.applet", "Applet" }, { "appletviewer.menuitem.restart", "Reiniciar" }, { "appletviewer.menuitem.reload", "Recarregar" }, { "appletviewer.menuitem.stop", "Interromper" }, { "appletviewer.menuitem.save", "Salvar" }, { "appletviewer.menuitem.start", "Iniciar" }, { "appletviewer.menuitem.clone", "Clonar..." }, { "appletviewer.menuitem.tag", "Tag..." }, { "appletviewer.menuitem.info", "Informa\u00e7\u00f5es..." }, { "appletviewer.menuitem.edit", "Editar" }, { "appletviewer.menuitem.encoding", "Codifica\u00e7\u00e3o do Caractere" }, { "appletviewer.menuitem.print", "Imprimir..." }, { "appletviewer.menuitem.props", "Propriedades..." }, { "appletviewer.menuitem.close", "Fechar" }, { "appletviewer.menuitem.quit", "Sair" }, { "appletviewer.label.hello", "Ol\u00e1..." }, { "appletviewer.status.start", "iniciando o applet..." }, { "appletviewer.appletsave.filedialogtitle", "Serializar Applet no Arquivo" }, { "appletviewer.appletsave.err1", "serializando um {0} para {1}" }, { "appletviewer.appletsave.err2", "no appletSave: {0}" }, { "appletviewer.applettag", "Tag mostrada" }, { "appletviewer.applettag.textframe", "Tag HTML do Applet" }, { "appletviewer.appletinfo.applet", "-- nenhuma informa\u00e7\u00e3o do applet --" }, { "appletviewer.appletinfo.param", "-- sem informa\u00e7\u00e3o de par\u00e2metro --" }, { "appletviewer.appletinfo.textframe", "Informa\u00e7\u00f5es do Applet" }, { "appletviewer.appletprint.fail", "Falha na impress\u00e3o." }, { "appletviewer.appletprint.finish", "Impress\u00e3o finalizada." }, { "appletviewer.appletprint.cancel", "Impress\u00e3o cancelada." }, { "appletviewer.appletencoding", "Codifica\u00e7\u00e3o de Caractere: {0}" }, { "appletviewer.parse.warning.requiresname", "Advert\u00eancia: a tag <param name=... value=...> requer um atributo de nome." }, { "appletviewer.parse.warning.paramoutside", "Advert\u00eancia: a tag <param> externa <applet> ... </applet>." }, { "appletviewer.parse.warning.applet.requirescode", "Advert\u00eancia: a tag <applet> requer um atributo de c\u00f3digo." }, { "appletviewer.parse.warning.applet.requiresheight", "Advert\u00eancia: a tag <applet> requer um atributo de altura." }, { "appletviewer.parse.warning.applet.requireswidth", "Advert\u00eancia: a tag <applet> requer um atributo de largura." }, { "appletviewer.parse.warning.object.requirescode", "Advert\u00eancia: a tag <object> requer um atributo de c\u00f3digo." }, { "appletviewer.parse.warning.object.requiresheight", "Advert\u00eancia: a tag <object> requer um atributo de altura." }, { "appletviewer.parse.warning.object.requireswidth", "Advert\u00eancia: a tag <object> requer um atributo de largura." }, { "appletviewer.parse.warning.embed.requirescode", "Advert\u00eancia: a tag <embed> requer um atributo de c\u00f3digo." }, { "appletviewer.parse.warning.embed.requiresheight", "Advert\u00eancia: a tag <embed> requer um atributo de altura." }, { "appletviewer.parse.warning.embed.requireswidth", "Advert\u00eancia: a tag <embed> requer um atributo de largura." }, { "appletviewer.parse.warning.appnotLongersupported", "Advert\u00eancia: a tag <app> n\u00e3o \u00e9 mais suportada; use <applet>:" }, { "appletviewer.usage", "Uso: appletviewer <op\u00e7\u00f5es> url(s)\n\nem que as <op\u00e7\u00f5es> incluem:\n  -debug                  Inicia o visualizador do applet no depurador Java\n  -encoding <codifica\u00e7\u00e3o>    Especifica a codifica\u00e7\u00e3o de caractere usada pelos arquivos HTML\n  -J<flag de runtime>        Informa o argumento ao intepretador java\n\nA op\u00e7\u00e3o -J n\u00e3o \u00e9 padr\u00e3o e est\u00e1 sujeita \u00e0 altera\u00e7\u00e3o sem notifica\u00e7\u00e3o." }, { "appletviewer.main.err.unsupportedopt", "Op\u00e7\u00e3o n\u00e3o suportada: {0}" }, { "appletviewer.main.err.unrecognizedarg", "Argumento n\u00e3o reconhecido: {0}" }, { "appletviewer.main.err.dupoption", "Uso duplicado da op\u00e7\u00e3o: {0}" }, { "appletviewer.main.err.inputfile", "Nenhum arquivo de entrada especificado." }, { "appletviewer.main.err.badurl", "URL Inv\u00e1lido: {0} ( {1} )" }, { "appletviewer.main.err.io", "Exce\u00e7\u00e3o de E/S ao ler: {0}" }, { "appletviewer.main.err.readablefile", "Certifique-se de que {0} seja um arquivo e seja leg\u00edvel." }, { "appletviewer.main.err.correcturl", "O URL {0} est\u00e1 correto?" }, { "appletviewer.main.prop.store", "Propriedades espec\u00edficas do usu\u00e1rio do AppletViewer" }, { "appletviewer.main.err.prop.cantread", "N\u00e3o \u00e9 poss\u00edvel ler o arquivo de propriedades do usu\u00e1rio: {0}" }, { "appletviewer.main.err.prop.cantsave", "N\u00e3o \u00e9 poss\u00edvel salvar o arquivo de propriedades do usu\u00e1rio: {0}" }, { "appletviewer.main.warn.nosecmgr", "Advert\u00eancia: desativando a seguran\u00e7a." }, { "appletviewer.main.debug.cantfinddebug", "N\u00e3o \u00e9 poss\u00edvel localizar o depurador!" }, { "appletviewer.main.debug.cantfindmain", "N\u00e3o \u00e9 poss\u00edvel localizar o m\u00e9todo main no depurador!" }, { "appletviewer.main.debug.exceptionindebug", "Exce\u00e7\u00e3o no depurador!" }, { "appletviewer.main.debug.cantaccess", "N\u00e3o \u00e9 poss\u00edvel acessar o depurador!" }, { "appletviewer.main.nosecmgr", "Advert\u00eancia: SecurityManager n\u00e3o instalado!" }, { "appletviewer.main.warning", "Advert\u00eancia: Nenhum applet iniciado. Certifique-se de que a entrada contenha uma tag <applet>." }, { "appletviewer.main.warn.prop.overwrite", "Advert\u00eancia: Substituindo a propriedade do sistema temporariamente a pedido do usu\u00e1rio: chave: {0} valor antigo: {1} valor novo: {2}" }, { "appletviewer.main.warn.cantreadprops", "Advert\u00eancia: N\u00e3o \u00e9 poss\u00edvel ler o arquivo de propriedades AppletViewer: {0} Usando padr\u00f5es." }, { "appletioexception.loadclass.throw.interrupted", "carregamento de classe interrompido: {0}" }, { "appletioexception.loadclass.throw.notloaded", "classe n\u00e3o carregada: {0}" }, { "appletclassloader.loadcode.verbose", "Fluxo de abertura para: {0} para obter {1}" }, { "appletclassloader.filenotfound", "Arquivo n\u00e3o encontrado ao procurar: {0}" }, { "appletclassloader.fileformat", "Exce\u00e7\u00e3o de formato do arquivo ao carregar: {0}" }, { "appletclassloader.fileioexception", "Exce\u00e7\u00e3o de E/S ao carregar: {0}" }, { "appletclassloader.fileexception", "exce\u00e7\u00e3o de {0} ao carregar: {1}" }, { "appletclassloader.filedeath", "{0} eliminado ao carregar: {1}" }, { "appletclassloader.fileerror", "erro de {0} ao carregar: {1}" }, { "appletclassloader.findclass.verbose.openstream", "Fluxo de abertura para: {0} para obter {1}" }, { "appletclassloader.getresource.verbose.forname", "AppletClassLoader.getResource do nome: {0}" }, { "appletclassloader.getresource.verbose.found", "Recurso encontrado: {0} como um recurso do sistema" }, { "appletclassloader.getresourceasstream.verbose", "Recurso encontrado: {0} como um recurso do sistema" }, { "appletpanel.runloader.err", "Par\u00e2metro de c\u00f3digo ou objeto!" }, { "appletpanel.runloader.exception", "exce\u00e7\u00e3o ao desserializar {0}" }, { "appletpanel.destroyed", "Applet destru\u00eddo." }, { "appletpanel.loaded", "Applet carregado." }, { "appletpanel.started", "Applet iniciado." }, { "appletpanel.inited", "Applet inicializado." }, { "appletpanel.stopped", "Applet interrompido." }, { "appletpanel.disposed", "Applet descartado." }, { "appletpanel.nocode", "A tag APPLET n\u00e3o encontrou o par\u00e2metro CODE." }, { "appletpanel.notfound", "carga: classe {0} n\u00e3o encontrada." }, { "appletpanel.nocreate", "carga: {0} n\u00e3o pode ser instanciada." }, { "appletpanel.noconstruct", "carga: {0} n\u00e3o \u00e9 p\u00fablica ou n\u00e3o tem construtor p\u00fablico." }, { "appletpanel.death", "eliminado" }, { "appletpanel.exception", "exce\u00e7\u00e3o: {0}." }, { "appletpanel.exception2", "exce\u00e7\u00e3o: {0}: {1}." }, { "appletpanel.error", "erro: {0}." }, { "appletpanel.error2", "erro: {0}: {1}." }, { "appletpanel.notloaded", "Inic: applet n\u00e3o carregado." }, { "appletpanel.notinited", "Iniciar: applet n\u00e3o inicializado." }, { "appletpanel.notstarted", "Interromper: applet n\u00e3o inicializado." }, { "appletpanel.notstopped", "Destruir: applet n\u00e3o interrompido." }, { "appletpanel.notdestroyed", "Descartar: applet n\u00e3o destru\u00eddo." }, { "appletpanel.notdisposed", "Carregar: applet n\u00e3o descartado." }, { "appletpanel.bail", "Interrompido: esvaziando." }, { "appletpanel.filenotfound", "Arquivo n\u00e3o encontrado ao procurar: {0}" }, { "appletpanel.fileformat", "Exce\u00e7\u00e3o de formato do arquivo ao carregar: {0}" }, { "appletpanel.fileioexception", "Exce\u00e7\u00e3o de E/S ao carregar: {0}" }, { "appletpanel.fileexception", "exce\u00e7\u00e3o de {0} ao carregar: {1}" }, { "appletpanel.filedeath", "{0} eliminado ao carregar: {1}" }, { "appletpanel.fileerror", "erro de {0} ao carregar: {1}" }, { "appletpanel.badattribute.exception", "Parsing de HTML: valor incorreto do atributo de largura/altura" }, { "appletillegalargumentexception.objectinputstream", "AppletObjectInputStream requer um carregador n\u00e3o nulo" }, { "appletprops.title", "Propriedades do AppletViewer" }, { "appletprops.label.http.server", "Servidor proxy Http:" }, { "appletprops.label.http.proxy", "Porta proxy Http:" }, { "appletprops.label.network", "Acesso de rede:" }, { "appletprops.choice.network.item.none", "Nenhum" }, { "appletprops.choice.network.item.applethost", "Host do Applet" }, { "appletprops.choice.network.item.unrestricted", "Irrestrito" }, { "appletprops.label.class", "Acesso \u00e0 classe:" }, { "appletprops.choice.class.item.restricted", "Restrito" }, { "appletprops.choice.class.item.unrestricted", "Irrestrito" }, { "appletprops.label.unsignedapplet", "Permitir applets n\u00e3o assinados:" }, { "appletprops.choice.unsignedapplet.no", "N\u00e3o" }, { "appletprops.choice.unsignedapplet.yes", "Sim" }, { "appletprops.button.apply", "Aplicar" }, { "appletprops.button.cancel", "Cancelar" }, { "appletprops.button.reset", "Redefinir" }, { "appletprops.apply.exception", "Falha ao salvar as propriedades: {0}" }, { "appletprops.title.invalidproxy", "Entrada Inv\u00e1lida" }, { "appletprops.label.invalidproxy", "A Porta Proxy deve ser um valor inteiro positivo." }, { "appletprops.button.ok", "OK" }, { "appletprops.prop.store", "Propriedades espec\u00edficas do usu\u00e1rio do AppletViewer" }, { "appletsecurityexception.checkcreateclassloader", "Exce\u00e7\u00e3o de Seguran\u00e7a: carregador de classes" }, { "appletsecurityexception.checkaccess.thread", "Exce\u00e7\u00e3o de Seguran\u00e7a: thread" }, { "appletsecurityexception.checkaccess.threadgroup", "Exce\u00e7\u00e3o de Seguran\u00e7a: grupo de threads: {0}" }, { "appletsecurityexception.checkexit", "Exce\u00e7\u00e3o de Seguran\u00e7a: sa\u00edda: {0}" }, { "appletsecurityexception.checkexec", "Exce\u00e7\u00e3o de Seguran\u00e7a: exec.: {0}" }, { "appletsecurityexception.checklink", "Exce\u00e7\u00e3o de Seguran\u00e7a: link: {0}" }, { "appletsecurityexception.checkpropsaccess", "Exce\u00e7\u00e3o de Seguran\u00e7a: propriedades" }, { "appletsecurityexception.checkpropsaccess.key", "Exce\u00e7\u00e3o de Seguran\u00e7a: acesso \u00e0s propriedades {0}" }, { "appletsecurityexception.checkread.exception1", "Exce\u00e7\u00e3o de Seguran\u00e7a: {0}, {1}" }, { "appletsecurityexception.checkread.exception2", "Exce\u00e7\u00e3o de Seguran\u00e7a: file.read: {0}" }, { "appletsecurityexception.checkread", "Exce\u00e7\u00e3o de Seguran\u00e7a: file.read: {0} == {1}" }, { "appletsecurityexception.checkwrite.exception", "Exce\u00e7\u00e3o de Seguran\u00e7a: {0}, {1}" }, { "appletsecurityexception.checkwrite", "Exce\u00e7\u00e3o de Seguran\u00e7a: file.write: {0} == {1}" }, { "appletsecurityexception.checkread.fd", "Exce\u00e7\u00e3o de Seguran\u00e7a: fd.read" }, { "appletsecurityexception.checkwrite.fd", "Exce\u00e7\u00e3o de Seguran\u00e7a: fd.write" }, { "appletsecurityexception.checklisten", "Exce\u00e7\u00e3o de Seguran\u00e7a: socket.listen: {0}" }, { "appletsecurityexception.checkaccept", "Exce\u00e7\u00e3o de Seguran\u00e7a: socket.accept: {0}:{1}" }, { "appletsecurityexception.checkconnect.networknone", "Exce\u00e7\u00e3o de Seguran\u00e7a: socket.connect: {0}->{1}" }, { "appletsecurityexception.checkconnect.networkhost1", "Exce\u00e7\u00e3o de Seguran\u00e7a: N\u00e3o foi poss\u00edvel estabelecer conex\u00e3o com {0} com a origem de {1}." }, { "appletsecurityexception.checkconnect.networkhost2", "Exce\u00e7\u00e3o de Seguran\u00e7a: N\u00e3o foi poss\u00edvel resolver o IP para o host {0} ou para {1}. " }, { "appletsecurityexception.checkconnect.networkhost3", "Exce\u00e7\u00e3o de Seguran\u00e7a: N\u00e3o foi poss\u00edvel resolver o IP para o host {0}. Consulte a propriedade trustProxy." }, { "appletsecurityexception.checkconnect", "Exce\u00e7\u00e3o de Seguran\u00e7a: conectar: {0}->{1}" }, { "appletsecurityexception.checkpackageaccess", "Exce\u00e7\u00e3o de Seguran\u00e7a: n\u00e3o \u00e9 poss\u00edvel acessar o pacote: {0}" }, { "appletsecurityexception.checkpackagedefinition", "Exce\u00e7\u00e3o de Seguran\u00e7a: n\u00e3o \u00e9 poss\u00edvel definir o pacote: {0}" }, { "appletsecurityexception.cannotsetfactory", "Exce\u00e7\u00e3o de Seguran\u00e7a: n\u00e3o \u00e9 poss\u00edvel definir o factory" }, { "appletsecurityexception.checkmemberaccess", "Exce\u00e7\u00e3o de Seguran\u00e7a: verificar acesso do membro" }, { "appletsecurityexception.checkgetprintjob", "Exce\u00e7\u00e3o de Seguran\u00e7a: getPrintJob" }, { "appletsecurityexception.checksystemclipboardaccess", "Exce\u00e7\u00e3o de Seguran\u00e7a: getSystemClipboard" }, { "appletsecurityexception.checkawteventqueueaccess", "Exce\u00e7\u00e3o de Seguran\u00e7a: getEventQueue" }, { "appletsecurityexception.checksecurityaccess", "Exce\u00e7\u00e3o de Seguran\u00e7a: opera\u00e7\u00e3o de seguran\u00e7a: {0}" }, { "appletsecurityexception.getsecuritycontext.unknown", "tipo de carregador de classe desconhecido. n\u00e3o \u00e9 poss\u00edvel verificar getContext" }, { "appletsecurityexception.checkread.unknown", "tipo de carregador de classe desconhecido. n\u00e3o \u00e9 poss\u00edvel verificar a leitura {0}" }, { "appletsecurityexception.checkconnect.unknown", "tipo de carregador de classe desconhecido. n\u00e3o \u00e9 poss\u00edvel verificar a conex\u00e3o" } };
    }
}
