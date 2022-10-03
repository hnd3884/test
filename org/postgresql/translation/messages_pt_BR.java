package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_pt_BR extends ResourceBundle
{
    private static final String[] table;
    
    public Object handleGetObject(final String msgid) throws MissingResourceException {
        final int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 397 << 1;
        final Object found = messages_pt_BR.table[idx];
        if (found == null) {
            return null;
        }
        if (msgid.equals(found)) {
            return messages_pt_BR.table[idx + 1];
        }
        final int incr = hash_val % 395 + 1 << 1;
        while (true) {
            idx += incr;
            if (idx >= 794) {
                idx -= 794;
            }
            final Object found2 = messages_pt_BR.table[idx];
            if (found2 == null) {
                return null;
            }
            if (msgid.equals(found2)) {
                return messages_pt_BR.table[idx + 1];
            }
        }
    }
    
    @Override
    public Enumeration getKeys() {
        return new Enumeration() {
            private int idx = 0;
            
            {
                while (this.idx < 794 && messages_pt_BR.table[this.idx] == null) {
                    this.idx += 2;
                }
            }
            
            @Override
            public boolean hasMoreElements() {
                return this.idx < 794;
            }
            
            @Override
            public Object nextElement() {
                final Object key = messages_pt_BR.table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 794 && messages_pt_BR.table[this.idx] == null);
                return key;
            }
        };
    }
    
    public ResourceBundle getParent() {
        return this.parent;
    }
    
    static {
        final String[] t = table = new String[] { "", "Project-Id-Version: PostgreSQL 8.4\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2004-10-31 20:48-0300\nLast-Translator: Euler Taveira de Oliveira <euler@timbira.com>\nLanguage-Team: Brazilian Portuguese <pgbr-dev@listas.postgresql.org.br>\nLanguage: pt_BR\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\n", "Not implemented: 2nd phase commit must be issued using an idle connection. commit xid={0}, currentXid={1}, state={2}, transactionState={3}", "N\u00e3o est\u00e1 implementado: efetiva\u00e7\u00e3o da segunda fase deve ser executada utilizado uma conex\u00e3o ociosa. commit xid={0}, currentXid={1}, state={2}, transactionState={3}", "DataSource has been closed.", "DataSource foi fechado.", null, null, "Invalid flags {0}", "Marcadores={0} inv\u00e1lidos", null, null, null, null, null, null, null, null, "Where: {0}", "Onde: {0}", null, null, null, null, "Unknown XML Source class: {0}", "Classe XML Source desconhecida: {0}", "The connection attempt failed.", "A tentativa de conex\u00e3o falhou.", "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.", "Posicionado depois do fim do ResultSet.  Voc\u00ea n\u00e3o pode chamar deleteRow() aqui.", null, null, "Can''t use query methods that take a query string on a PreparedStatement.", "N\u00e3o pode utilizar m\u00e9todos de consulta que pegam uma consulta de um comando preparado.", null, null, "Multiple ResultSets were returned by the query.", "ResultSets m\u00faltiplos foram retornados pela consulta.", null, null, null, null, null, null, null, null, null, null, null, null, "Too many update results were returned.", "Muitos resultados de atualiza\u00e7\u00e3o foram retornados.", null, null, null, null, null, null, "Illegal UTF-8 sequence: initial byte is {0}: {1}", "Sequ\u00eancia UTF-8 ilegal: byte inicial \u00e9 {0}: {1}", null, null, null, null, null, null, "The column name {0} was not found in this ResultSet.", "A nome da coluna {0} n\u00e3o foi encontrado neste ResultSet.", null, null, "Fastpath call {0} - No result was returned and we expected an integer.", "Chamada ao Fastpath {0} - Nenhum resultado foi retornado e n\u00f3s esper\u00e1vamos um inteiro.", null, null, "Protocol error.  Session setup failed.", "Erro de Protocolo. Configura\u00e7\u00e3o da sess\u00e3o falhou.", "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.", "Uma fun\u00e7\u00e3o foi declarada mas nenhuma chamada a registerOutParameter (1, <algum_tipo>) foi feita.", "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.", "ResultSets com CONCUR_READ_ONLY concorrentes n\u00e3o podem ser atualizados.", null, null, null, null, null, null, null, null, null, null, "LOB positioning offsets start at 1.", "Deslocamentos da posi\u00e7\u00e3o de LOB come\u00e7am em 1.", "Internal Position: {0}", "Posi\u00e7\u00e3o Interna: {0}", null, null, "free() was called on this LOB previously", "free() j\u00e1 foi chamado neste LOB", null, null, "Cannot change transaction read-only property in the middle of a transaction.", "N\u00e3o pode mudar propriedade somente-leitura da transa\u00e7\u00e3o no meio de uma transa\u00e7\u00e3o.", "The JVM claims not to support the {0} encoding.", "A JVM reclamou que n\u00e3o suporta a codifica\u00e7\u00e3o {0}.", null, null, null, null, "{0} function doesn''t take any argument.", "fun\u00e7\u00e3o {0} n\u00e3o recebe nenhum argumento.", null, null, "xid must not be null", "xid n\u00e3o deve ser nulo", "Connection has been closed.", "Conex\u00e3o foi fechada.", null, null, null, null, null, null, "The server does not support SSL.", "O servidor n\u00e3o suporta SSL.", "Custom type maps are not supported.", "Mapeamento de tipos personalizados n\u00e3o s\u00e3o suportados.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}", "Sequ\u00eancia UTF-8 ilegal: byte {0} da sequ\u00eancia de bytes {1} n\u00e3o \u00e9 10xxxxxx: {2}", null, null, null, null, null, null, "Hint: {0}", "Dica: {0}", null, null, "Unable to find name datatype in the system catalogs.", "N\u00e3o foi poss\u00edvel encontrar tipo de dado name nos cat\u00e1logos do sistema.", null, null, "Unsupported Types value: {0}", "Valor de Types n\u00e3o \u00e9 suportado: {0}", "Unknown type {0}.", "Tipo desconhecido {0}.", null, null, null, null, null, null, "{0} function takes two and only two arguments.", "fun\u00e7\u00e3o {0} recebe somente dois argumentos.", null, null, "Finalizing a Connection that was never closed:", "Fechando uma Conex\u00e3o que n\u00e3o foi fechada:", null, null, null, null, null, null, null, null, "The maximum field size must be a value greater than or equal to 0.", "O tamanho m\u00e1ximo de um campo deve ser um valor maior ou igual a 0.", null, null, null, null, "PostgreSQL LOBs can only index to: {0}", "LOBs do PostgreSQL s\u00f3 podem indexar at\u00e9: {0}", null, null, null, null, null, null, "Method {0} is not yet implemented.", "M\u00e9todo {0} ainda n\u00e3o foi implementado.", null, null, "Error loading default settings from driverconfig.properties", "Erro ao carregar configura\u00e7\u00f5es padr\u00e3o do driverconfig.properties", "Results cannot be retrieved from a CallableStatement before it is executed.", "Resultados n\u00e3o podem ser recuperados de uma fun\u00e7\u00e3o antes dela ser executada.", "Large Objects may not be used in auto-commit mode.", "Objetos Grandes n\u00e3o podem ser usados no modo de efetiva\u00e7\u00e3o autom\u00e1tica (auto-commit).", null, null, null, null, "Expected command status BEGIN, got {0}.", "Status do comando BEGIN esperado, recebeu {0}.", null, null, null, null, null, null, null, null, "Invalid fetch direction constant: {0}.", "Constante de dire\u00e7\u00e3o da busca \u00e9 inv\u00e1lida: {0}.", null, null, "{0} function takes three and only three arguments.", "fun\u00e7\u00e3o {0} recebe tr\u00eas e somente tr\u00eas argumentos.", null, null, "This SQLXML object has already been freed.", "Este objeto SQLXML j\u00e1 foi liberado.", "Cannot update the ResultSet because it is either before the start or after the end of the results.", "N\u00e3o pode atualizar o ResultSet porque ele est\u00e1 antes do in\u00edcio ou depois do fim dos resultados.", "The JVM claims not to support the encoding: {0}", "A JVM reclamou que n\u00e3o suporta a codifica\u00e7\u00e3o: {0}", "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.", "Par\u00e2metro do tipo {0} foi registrado, mas uma chamada a get{1} (tiposql={2}) foi feita.", null, null, null, null, null, null, "Cannot establish a savepoint in auto-commit mode.", "N\u00e3o pode estabelecer um savepoint no modo de efetiva\u00e7\u00e3o autom\u00e1tica (auto-commit).", "Cannot retrieve the id of a named savepoint.", "N\u00e3o pode recuperar o id de um savepoint com nome.", "The column index is out of range: {0}, number of columns: {1}.", "O \u00edndice da coluna est\u00e1 fora do intervalo: {0}, n\u00famero de colunas: {1}.", null, null, null, null, "Something unusual has occurred to cause the driver to fail. Please report this exception.", "Alguma coisa n\u00e3o usual ocorreu para causar a falha do driver. Por favor reporte esta exce\u00e7\u00e3o.", null, null, null, null, null, null, null, null, "Cannot cast an instance of {0} to type {1}", "N\u00e3o pode converter uma inst\u00e2ncia de {0} para tipo {1}", null, null, "Unknown Types value.", "Valor de Types desconhecido.", "Invalid stream length {0}.", "Tamanho de dado {0} \u00e9 inv\u00e1lido.", null, null, null, null, "Cannot retrieve the name of an unnamed savepoint.", "N\u00e3o pode recuperar o nome de um savepoint sem nome.", "Unable to translate data into the desired encoding.", "N\u00e3o foi poss\u00edvel traduzir dado para codifica\u00e7\u00e3o desejada.", "Expected an EOF from server, got: {0}", "Esperado um EOF do servidor, recebido: {0}", "Bad value for type {0} : {1}", "Valor inv\u00e1lido para tipo {0} : {1}", "The server requested password-based authentication, but no password was provided.", "O servidor pediu autentica\u00e7\u00e3o baseada em senha, mas nenhuma senha foi fornecida.", null, null, null, null, "Unable to create SAXResult for SQLXML.", "N\u00e3o foi poss\u00edvel criar SAXResult para SQLXML.", null, null, null, null, "Error during recover", "Erro durante recupera\u00e7\u00e3o", "tried to call end without corresponding start call. state={0}, start xid={1}, currentXid={2}, preparedXid={3}", "tentou executar end sem a chamada ao start correspondente. state={0}, start xid={1}, currentXid={2}, preparedXid={3}", "Truncation of large objects is only implemented in 8.3 and later servers.", "Truncar objetos grandes s\u00f3 \u00e9 implementado por servidores 8.3 ou superiores.", "This PooledConnection has already been closed.", "Este PooledConnection j\u00e1 foi fechado.", null, null, "ClientInfo property not supported.", "propriedade ClientInfo n\u00e3o \u00e9 suportada.", null, null, "Fetch size must be a value greater to or equal to 0.", "Tamanho da busca deve ser um valor maior ou igual a 0.", null, null, null, null, "A connection could not be made using the requested protocol {0}.", "A conex\u00e3o n\u00e3o pode ser feita usando protocolo informado {0}.", null, null, null, null, "Unknown XML Result class: {0}", "Classe XML Result desconhecida: {0}", null, null, "There are no rows in this ResultSet.", "N\u00e3o h\u00e1 nenhum registro neste ResultSet.", "Unexpected command status: {0}.", "Status do comando inesperado: {0}.", null, null, null, null, "Heuristic commit/rollback not supported. forget xid={0}", "Efetiva\u00e7\u00e3o/Cancelamento heur\u00edstico n\u00e3o \u00e9 suportado. forget xid={0}", null, null, "Not on the insert row.", "N\u00e3o est\u00e1 inserindo um registro.", "This SQLXML object has already been initialized, so you cannot manipulate it further.", "Este objeto SQLXML j\u00e1 foi inicializado, ent\u00e3o voc\u00ea n\u00e3o pode manipul\u00e1-lo depois.", null, null, null, null, null, null, "Server SQLState: {0}", "SQLState: {0}", null, null, "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.", "O par\u00e2metro do servidor standard_conforming_strings foi definido como {0}. O driver JDBC espera que seja on ou off.", null, null, null, null, null, null, null, null, null, null, "The driver currently does not support COPY operations.", "O driver atualmente n\u00e3o suporta opera\u00e7\u00f5es COPY.", null, null, "The array index is out of range: {0}, number of elements: {1}.", "O \u00edndice da matriz est\u00e1 fora do intervalo: {0}, n\u00famero de elementos: {1}.", null, null, null, null, null, null, null, null, "suspend/resume not implemented", "suspender/recome\u00e7ar n\u00e3o est\u00e1 implementado", null, null, "Not implemented: one-phase commit must be issued using the same connection that was used to start it", "N\u00e3o est\u00e1 implementado: efetivada da primeira fase deve ser executada utilizando a mesma conex\u00e3o que foi utilizada para inici\u00e1-la", "Error during one-phase commit. commit xid={0}", "Erro durante efetiva\u00e7\u00e3o de uma fase. commit xid={0}", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Cannot call cancelRowUpdates() when on the insert row.", "N\u00e3o pode chamar cancelRowUpdates() quando estiver inserindo registro.", "Cannot reference a savepoint after it has been released.", "N\u00e3o pode referenciar um savepoint ap\u00f3s ele ser descartado.", "You must specify at least one column value to insert a row.", "Voc\u00ea deve especificar pelo menos uma coluna para inserir um registro.", "Unable to determine a value for MaxIndexKeys due to missing system catalog data.", "N\u00e3o foi poss\u00edvel determinar um valor para MaxIndexKeys por causa de falta de dados no cat\u00e1logo do sistema.", null, null, null, null, "commit called before end. commit xid={0}, state={1}", "commit executado antes do end. commit xid={0}, state={1}", "Illegal UTF-8 sequence: final value is out of range: {0}", "Sequ\u00eancia UTF-8 ilegal: valor final est\u00e1 fora do intervalo: {0}", "{0} function takes two or three arguments.", "fun\u00e7\u00e3o {0} recebe dois ou tr\u00eas argumentos.", null, null, null, null, null, null, null, null, null, null, null, null, "Unable to convert DOMResult SQLXML data to a string.", "N\u00e3o foi poss\u00edvel converter dado SQLXML do DOMResult para uma cadeia de caracteres.", null, null, null, null, "Unable to decode xml data.", "N\u00e3o foi poss\u00edvel decodificar dado xml.", null, null, null, null, "Unexpected error writing large object to database.", "Erro inesperado ao escrever objeto grande no banco de dados.", "Zero bytes may not occur in string parameters.", "Zero bytes n\u00e3o podem ocorrer em par\u00e2metros de cadeia de caracteres.", "A result was returned when none was expected.", "Um resultado foi retornado quando nenhum era esperado.", null, null, null, null, "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.", "ResultSet n\u00e3o \u00e9 atualiz\u00e1vel. A consulta que gerou esse conjunto de resultados deve selecionar somente uma tabela, e deve selecionar todas as chaves prim\u00e1rias daquela tabela. Veja a especifica\u00e7\u00e3o na API do JDBC 2.1, se\u00e7\u00e3o 5.6 para obter mais detalhes.", null, null, "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.", "Tamanho de mensagem de liga\u00e7\u00e3o {0} \u00e9 muito longo. Isso pode ser causado por especifica\u00e7\u00f5es de tamanho incorretas ou muito grandes nos par\u00e2metros do InputStream.", null, null, null, null, "Statement has been closed.", "Comando foi fechado.", "No value specified for parameter {0}.", "Nenhum valor especificado para par\u00e2metro {0}.", null, null, null, null, "The array index is out of range: {0}", "O \u00edndice da matriz est\u00e1 fora do intervalo: {0}", null, null, null, null, "Unable to bind parameter values for statement.", "N\u00e3o foi poss\u00edvel ligar valores de par\u00e2metro ao comando.", "Can''t refresh the insert row.", "N\u00e3o pode renovar um registro inserido.", null, null, "No primary key found for table {0}.", "Nenhuma chave prim\u00e1ria foi encontrada para tabela {0}.", "Cannot change transaction isolation level in the middle of a transaction.", "N\u00e3o pode mudar n\u00edvel de isolamento da transa\u00e7\u00e3o no meio de uma transa\u00e7\u00e3o.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Provided InputStream failed.", "InputStream fornecido falhou.", "The parameter index is out of range: {0}, number of parameters: {1}.", "O \u00edndice de par\u00e2metro est\u00e1 fora do intervalo: {0}, n\u00famero de par\u00e2metros: {1}.", "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.", "O par\u00e2metro do servidor DateStyle foi alterado para {0}. O driver JDBC requer que o DateStyle come\u00e7e com ISO para opera\u00e7\u00e3o normal.", null, null, null, null, "Connection attempt timed out.", "Tentativa de conex\u00e3o falhou.", null, null, "Internal Query: {0}", "Consulta Interna: {0}", "Error preparing transaction. prepare xid={0}", "Erro ao preparar transa\u00e7\u00e3o. prepare xid={0}", null, null, "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.", "O tipo de autentica\u00e7\u00e3o {0} n\u00e3o \u00e9 suportado. Verifique se voc\u00ea configurou o arquivo pg_hba.conf incluindo a subrede ou endere\u00e7o IP do cliente, e se est\u00e1 utilizando o esquema de autentica\u00e7\u00e3o suportado pelo driver.", null, null, null, null, null, null, "Interval {0} not yet implemented", "Intervalo {0} ainda n\u00e3o foi implementado", null, null, null, null, "Conversion of interval failed", "Convers\u00e3o de interval falhou", null, null, null, null, null, null, "Query timeout must be a value greater than or equals to 0.", "Tempo de espera da consulta deve ser um valor maior ou igual a 0.", "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.", "Conex\u00e3o foi fechada automaticamente porque uma nova conex\u00e3o foi aberta pelo mesmo PooledConnection ou o PooledConnection foi fechado.", "ResultSet not positioned properly, perhaps you need to call next.", "ResultSet n\u00e3o est\u00e1 posicionado corretamente, talvez voc\u00ea precise chamar next.", "Prepare called before end. prepare xid={0}, state={1}", "Prepare executado antes do end. prepare xid={0}, state={1}", "Invalid UUID data.", "dado UUID \u00e9 inv\u00e1lido.", "This statement has been closed.", "Este comando foi fechado.", "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.", "N\u00e3o pode inferir um tipo SQL a ser usado para uma inst\u00e2ncia de {0}. Use setObject() com um valor de Types expl\u00edcito para especificar o tipo a ser usado.", "Cannot call updateRow() when on the insert row.", "N\u00e3o pode chamar updateRow() quando estiver inserindo registro.", null, null, null, null, null, null, "Detail: {0}", "Detalhe: {0}", null, null, "Cannot call deleteRow() when on the insert row.", "N\u00e3o pode chamar deleteRow() quando estiver inserindo registro.", "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.", "Posicionado antes do in\u00edcio do ResultSet.  Voc\u00ea n\u00e3o pode chamar deleteRow() aqui.", null, null, null, null, null, null, "Illegal UTF-8 sequence: final value is a surrogate value: {0}", "Sequ\u00eancia UTF-8 ilegal: valor final \u00e9 um valor suplementar: {0}", "Unknown Response Type {0}.", "Tipo de Resposta Desconhecido {0}.", null, null, "Unsupported value for stringtype parameter: {0}", "Valor do par\u00e2metro stringtype n\u00e3o \u00e9 suportado: {0}", "Conversion to type {0} failed: {1}.", "Convers\u00e3o para tipo {0} falhou: {1}.", "This SQLXML object has not been initialized, so you cannot retrieve data from it.", "Este objeto SQLXML n\u00e3o foi inicializado, ent\u00e3o voc\u00ea n\u00e3o pode recuperar dados dele.", null, null, null, null, null, null, null, null, null, null, null, null, "Unable to load the class {0} responsible for the datatype {1}", "N\u00e3o foi poss\u00edvel carregar a classe {0} respons\u00e1vel pelo tipo de dado {1}", null, null, "The fastpath function {0} is unknown.", "A fun\u00e7\u00e3o do fastpath {0} \u00e9 desconhecida.", null, null, "Malformed function or procedure escape syntax at offset {0}.", "Sintaxe de escape mal formada da fun\u00e7\u00e3o ou do procedimento no deslocamento {0}.", null, null, "Provided Reader failed.", "Reader fornecido falhou.", "Maximum number of rows must be a value grater than or equal to 0.", "N\u00famero m\u00e1ximo de registros deve ser um valor maior ou igual a 0.", "Failed to create object for: {0}.", "Falhou ao criar objeto para: {0}.", null, null, "Conversion of money failed.", "Convers\u00e3o de money falhou.", "Premature end of input stream, expected {0} bytes, but only read {1}.", "Fim de entrada prematuro, eram esperados {0} bytes, mas somente {1} foram lidos.", null, null, "An unexpected result was returned by a query.", "Um resultado inesperado foi retornado pela consulta.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}", "Intercala\u00e7\u00e3o de transa\u00e7\u00e3o n\u00e3o est\u00e1 implementado. xid={0}, currentXid={1}, state={2}, flags={3}", "An error occurred while setting up the SSL connection.", "Um erro ocorreu ao estabelecer uma conex\u00e3o SSL.", null, null, null, null, null, null, "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}", "Sequ\u00eancia UTF-8 ilegal: {0} bytes utilizados para codificar um valor de {1} bytes: {2}", "Not implemented: Prepare must be issued using the same connection that started the transaction. currentXid={0}, prepare xid={1}", "N\u00e3o est\u00e1 implementado: Prepare deve ser executado utilizando a mesma conex\u00e3o que iniciou a transa\u00e7\u00e3o. currentXid={0}, prepare xid={1}", "The SSLSocketFactory class provided {0} could not be instantiated.", "A classe SSLSocketFactory forneceu {0} que n\u00e3o p\u00f4de ser instanciado.", null, null, "Failed to convert binary xml data to encoding: {0}.", "Falhou ao converter dados xml bin\u00e1rios para codifica\u00e7\u00e3o: {0}.", null, null, null, null, null, null, "Position: {0}", "Posi\u00e7\u00e3o: {0}", null, null, null, null, "Location: File: {0}, Routine: {1}, Line: {2}", "Local: Arquivo: {0}, Rotina: {1}, Linha: {2}", null, null, null, null, null, null, "Cannot tell if path is open or closed: {0}.", "N\u00e3o pode dizer se caminho est\u00e1 aberto ou fechado: {0}.", null, null, null, null, "Unable to create StAXResult for SQLXML", "N\u00e3o foi poss\u00edvel criar StAXResult para SQLXML", null, null, null, null, null, null, null, null, "Cannot convert an instance of {0} to type {1}", "N\u00e3o pode converter uma inst\u00e2ncia de {0} para tipo {1}", null, null, null, null, null, null, null, null, "{0} function takes four and only four argument.", "fun\u00e7\u00e3o {0} recebe somente quatro argumentos.", null, null, null, null, "Error disabling autocommit", "Erro ao desabilitar autocommit", "Interrupted while attempting to connect.", "Interrompido ao tentar se conectar.", null, null, "Your security policy has prevented the connection from being attempted.  You probably need to grant the connect java.net.SocketPermission to the database server host and port that you wish to connect to.", "Sua pol\u00edtica de seguran\u00e7a impediu que a conex\u00e3o pudesse ser estabelecida. Voc\u00ea provavelmente precisa conceder permiss\u00e3o em java.net.SocketPermission para a m\u00e1quina e a porta do servidor de banco de dados que voc\u00ea deseja se conectar.", null, null, null, null, null, null, null, null, null, null, "No function outputs were registered.", "Nenhum sa\u00edda de fun\u00e7\u00e3o foi registrada.", "{0} function takes one and only one argument.", "fun\u00e7\u00e3o {0} recebe somente um argumento.", null, null, null, null, null, null, "This ResultSet is closed.", "Este ResultSet est\u00e1 fechado.", "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.", "Caracter inv\u00e1lido foi encontrado. Isso \u00e9 mais comumente causado por dado armazenado que cont\u00e9m caracteres que s\u00e3o inv\u00e1lidos para a codifica\u00e7\u00e3o que foi criado o banco de dados. O exemplo mais comum disso \u00e9 armazenar dados de 8 bits em um banco de dados SQL_ASCII.", null, null, null, null, "GSS Authentication failed", "Autentica\u00e7\u00e3o GSS falhou", "Ran out of memory retrieving query results.", "Mem\u00f3ria insuficiente ao recuperar resultados da consulta.", "Returning autogenerated keys is not supported.", "Retorno de chaves geradas automaticamente n\u00e3o \u00e9 suportado.", null, null, "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.", "Opera\u00e7\u00e3o requer um ResultSet rol\u00e1vel, mas este ResultSet \u00e9 FORWARD_ONLY (somente para frente).", "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.", "Uma fun\u00e7\u00e3o foi executada e o par\u00e2metro de retorno {0} era do tipo {1} contudo tipo {2} foi registrado.", "Unable to find server array type for provided name {0}.", "N\u00e3o foi poss\u00edvel encontrar tipo matriz para nome fornecido {0}.", null, null, "Unknown ResultSet holdability setting: {0}.", "Defini\u00e7\u00e3o de durabilidade do ResultSet desconhecida: {0}.", null, null, "Transaction isolation level {0} not supported.", "N\u00edvel de isolamento da transa\u00e7\u00e3o {0} n\u00e3o \u00e9 suportado.", "Zero bytes may not occur in identifiers.", "Zero bytes n\u00e3o podem ocorrer em identificadores.", "No results were returned by the query.", "Nenhum resultado foi retornado pela consulta.", "A CallableStatement was executed with nothing returned.", "Uma fun\u00e7\u00e3o foi executada e nada foi retornado.", "wasNull cannot be call before fetching a result.", "wasNull n\u00e3o pode ser chamado antes de obter um resultado.", null, null, "Returning autogenerated keys by column index is not supported.", "Retorno de chaves geradas automaticamente por \u00edndice de coluna n\u00e3o \u00e9 suportado.", "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.", "Este comando n\u00e3o declara um par\u00e2metro de sa\u00edda. Utilize '{' ?= chamada ... '}' para declarar um)", "Can''t use relative move methods while on the insert row.", "N\u00e3o pode utilizar m\u00e9todos de movimenta\u00e7\u00e3o relativos enquanto estiver inserindo registro.", "A CallableStatement was executed with an invalid number of parameters", "Uma fun\u00e7\u00e3o foi executada com um n\u00famero inv\u00e1lido de par\u00e2metros", "Connection is busy with another transaction", "Conex\u00e3o est\u00e1 ocupada com outra transa\u00e7\u00e3o" };
    }
}
