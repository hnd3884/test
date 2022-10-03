package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_it extends ResourceBundle
{
    private static final String[] table;
    
    public Object handleGetObject(final String msgid) throws MissingResourceException {
        final int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 397 << 1;
        final Object found = messages_it.table[idx];
        if (found == null) {
            return null;
        }
        if (msgid.equals(found)) {
            return messages_it.table[idx + 1];
        }
        final int incr = hash_val % 395 + 1 << 1;
        while (true) {
            idx += incr;
            if (idx >= 794) {
                idx -= 794;
            }
            final Object found2 = messages_it.table[idx];
            if (found2 == null) {
                return null;
            }
            if (msgid.equals(found2)) {
                return messages_it.table[idx + 1];
            }
        }
    }
    
    @Override
    public Enumeration getKeys() {
        return new Enumeration() {
            private int idx = 0;
            
            {
                while (this.idx < 794 && messages_it.table[this.idx] == null) {
                    this.idx += 2;
                }
            }
            
            @Override
            public boolean hasMoreElements() {
                return this.idx < 794;
            }
            
            @Override
            public Object nextElement() {
                final Object key = messages_it.table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 794 && messages_it.table[this.idx] == null);
                return key;
            }
        };
    }
    
    public ResourceBundle getParent() {
        return this.parent;
    }
    
    static {
        final String[] t = table = new String[] { "", "Project-Id-Version: PostgreSQL JDBC Driver 8.2\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2006-06-23 17:25+0200\nLast-Translator: Giuseppe Sacco <eppesuig@debian.org>\nLanguage-Team: Italian <tp@lists.linux.it>\nLanguage: it\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\n", null, null, "DataSource has been closed.", "Questo �DataSource� \u00e8 stato chiuso.", null, null, null, null, null, null, null, null, null, null, null, null, "Where: {0}", "Dove: {0}", null, null, null, null, null, null, "The connection attempt failed.", "Il tentativo di connessione \u00e8 fallito.", "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.", "La posizione attuale \u00e8 successiva alla fine del ResultSet. Non \u00e8 possibile invocare �deleteRow()� qui.", null, null, "Can''t use query methods that take a query string on a PreparedStatement.", "Non si possono utilizzare i metodi \"query\" che hanno come argomento una stringa nel caso di �PreparedStatement�.", null, null, "Multiple ResultSets were returned by the query.", "La query ha restituito �ResultSet� multipli.", null, null, null, null, null, null, null, null, null, null, null, null, "Too many update results were returned.", "Sono stati restituiti troppi aggiornamenti.", null, null, null, null, null, null, "Illegal UTF-8 sequence: initial byte is {0}: {1}", "Sequenza UTF-8 illegale: il byte iniziale \u00e8 {0}: {1}", null, null, null, null, null, null, "The column name {0} was not found in this ResultSet.", "Colonna denominata �{0}� non \u00e8 presente in questo �ResultSet�.", null, null, "Fastpath call {0} - No result was returned and we expected an integer.", "Chiamata Fastpath �{0}�: Nessun risultato restituito mentre ci si aspettava un intero.", null, null, "Protocol error.  Session setup failed.", "Errore di protocollo. Impostazione della sessione fallita.", "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.", "\u00c8 stato definito un �CallableStatement� ma non \u00e8 stato invocato il metodo �registerOutParameter(1, <tipo>)�.", "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.", "I �ResultSet� in modalit\u00e0 CONCUR_READ_ONLY non possono essere aggiornati.", null, null, null, null, null, null, null, null, null, null, "LOB positioning offsets start at 1.", "L''offset per la posizione dei LOB comincia da 1.", "Internal Position: {0}", "Posizione interna: {0}", null, null, null, null, null, null, "Cannot change transaction read-only property in the middle of a transaction.", "Non \u00e8 possibile modificare la propriet\u00e0 �read-only� delle transazioni nel mezzo di una transazione.", "The JVM claims not to support the {0} encoding.", "La JVM sostiene di non supportare la codifica {0}.", null, null, null, null, "{0} function doesn''t take any argument.", "Il metodo �{0}� non accetta argomenti.", null, null, "xid must not be null", "xid non pu\u00f2 essere NULL", "Connection has been closed.", "Questo �Connection� \u00e8 stato chiuso.", null, null, null, null, null, null, "The server does not support SSL.", "Il server non supporta SSL.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}", "Sequenza UTF-8 illegale: il byte {0} di una sequenza di {1} byte non \u00e8 10xxxxxx: {2}", null, null, null, null, null, null, "Hint: {0}", "Suggerimento: {0}", null, null, "Unable to find name datatype in the system catalogs.", "Non \u00e8 possibile trovare il datatype �name� nel catalogo di sistema.", null, null, "Unsupported Types value: {0}", "Valore di tipo �{0}� non supportato.", "Unknown type {0}.", "Tipo sconosciuto {0}.", null, null, null, null, null, null, "{0} function takes two and only two arguments.", "Il metodo �{0}� accetta due e solo due argomenti.", null, null, "Finalizing a Connection that was never closed:", "Finalizzazione di una �Connection� che non \u00e8 stata chiusa.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "PostgreSQL LOBs can only index to: {0}", "Il massimo valore per l''indice dei LOB di PostgreSQL \u00e8 {0}. ", null, null, null, null, null, null, "Method {0} is not yet implemented.", "Il metodo �{0}� non \u00e8 stato ancora implementato.", null, null, "Error loading default settings from driverconfig.properties", "Si \u00e8 verificato un errore caricando le impostazioni predefinite da �driverconfig.properties�.", null, null, "Large Objects may not be used in auto-commit mode.", "Non \u00e8 possibile impostare i �Large Object� in modalit\u00e0 �auto-commit�.", null, null, null, null, "Expected command status BEGIN, got {0}.", "Lo stato del comando avrebbe dovuto essere BEGIN, mentre invece \u00e8 {0}.", null, null, null, null, null, null, null, null, "Invalid fetch direction constant: {0}.", "Costante per la direzione dell''estrazione non valida: {0}.", null, null, "{0} function takes three and only three arguments.", "Il metodo �{0}� accetta tre e solo tre argomenti.", null, null, "Error during recover", "Errore durante il ripristino", "Cannot update the ResultSet because it is either before the start or after the end of the results.", "Non \u00e8 possibile aggiornare il �ResultSet� perch\u00e9 la posizione attuale \u00e8 precedente all''inizio o successiva alla file dei risultati.", null, null, "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.", "\u00c8 stato definito il parametro di tipo �{0}�, ma poi \u00e8 stato invocato il metodo �get{1}()� (sqltype={2}).", null, null, null, null, null, null, "Cannot establish a savepoint in auto-commit mode.", "Non \u00e8 possibile impostare i punti di ripristino in modalit\u00e0 �auto-commit�.", "Cannot retrieve the id of a named savepoint.", "Non \u00e8 possibile trovare l''id del punto di ripristino indicato.", "The column index is out of range: {0}, number of columns: {1}.", "Indice di colonna, {0}, \u00e8 maggiore del numero di colonne {1}.", null, null, null, null, "Something unusual has occurred to cause the driver to fail. Please report this exception.", "Qualcosa di insolito si \u00e8 verificato causando il fallimento del driver. Per favore riferire all''autore del driver questa eccezione.", null, null, null, null, null, null, null, null, "Cannot cast an instance of {0} to type {1}", "Non \u00e8 possibile fare il cast di una istanza di �{0}� al tipo �{1}�.", null, null, "Unknown Types value.", "Valore di tipo sconosciuto.", "Invalid stream length {0}.", "La dimensione specificata, {0}, per lo �stream� non \u00e8 valida.", null, null, null, null, "Cannot retrieve the name of an unnamed savepoint.", "Non \u00e8 possibile trovare il nome di un punto di ripristino anonimo.", "Unable to translate data into the desired encoding.", "Impossibile tradurre i dati nella codifica richiesta.", "Expected an EOF from server, got: {0}", "Ricevuto dal server �{0}� mentre era atteso un EOF", "Bad value for type {0} : {1}", "Il valore �{1}� non \u00e8 adeguato al tipo �{0}�.", "The server requested password-based authentication, but no password was provided.", "Il server ha richiesto l''autenticazione con password, ma tale password non \u00e8 stata fornita.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "This PooledConnection has already been closed.", "Questo �PooledConnection� \u00e8 stato chiuso.", null, null, null, null, null, null, "Fetch size must be a value greater to or equal to 0.", "La dimensione dell''area di �fetch� deve essere maggiore o eguale a 0.", null, null, null, null, "A connection could not be made using the requested protocol {0}.", "Non \u00e8 stato possibile attivare la connessione utilizzando il protocollo richiesto {0}.", null, null, null, null, null, null, null, null, "There are no rows in this ResultSet.", "Non ci sono righe in questo �ResultSet�.", "Unexpected command status: {0}.", "Stato del comando non previsto: {0}.", null, null, null, null, null, null, null, null, "Not on the insert row.", "Non si \u00e8 in una nuova riga.", null, null, null, null, null, null, null, null, "Server SQLState: {0}", "SQLState del server: {0}", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "The driver currently does not support COPY operations.", "Il driver non supporta al momento l''operazione �COPY�.", null, null, "The array index is out of range: {0}, number of elements: {1}.", "L''indice dell''array \u00e8 fuori intervallo: {0}, numero di elementi: {1}.", null, null, null, null, null, null, null, null, "suspend/resume not implemented", "�suspend�/�resume� non implementato", null, null, "Not implemented: one-phase commit must be issued using the same connection that was used to start it", "Non implementato: il commit \"one-phase\" deve essere invocato sulla stessa connessione che ha iniziato la transazione.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Cannot call cancelRowUpdates() when on the insert row.", "Non \u00e8 possibile invocare �cancelRowUpdates()� durante l''inserimento di una riga.", "Cannot reference a savepoint after it has been released.", "Non \u00e8 possibile utilizzare un punto di ripristino successivamente al suo rilascio.", "You must specify at least one column value to insert a row.", "Per inserire un record si deve specificare almeno il valore di una colonna.", "Unable to determine a value for MaxIndexKeys due to missing system catalog data.", "Non \u00e8 possibile trovare il valore di �MaxIndexKeys� nel catalogo si sistema.", null, null, null, null, null, null, "The JVM claims not to support the encoding: {0}", "La JVM sostiene di non supportare la codifica: {0}.", "{0} function takes two or three arguments.", "Il metodo �{0}� accetta due o tre argomenti.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Unexpected error writing large object to database.", "Errore inatteso inviando un �large object� al database.", "Zero bytes may not occur in string parameters.", "Byte con valore zero non possono essere contenuti nei parametri stringa.", "A result was returned when none was expected.", "\u00c8 stato restituito un valore nonostante non ne fosse atteso nessuno.", null, null, null, null, "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.", "Il �ResultSet� non \u00e8 aggiornabile. La query che lo genera deve selezionare una sola tabella e deve selezionarne tutti i campi che ne compongono la chiave primaria. Si vedano le specifiche dell''API JDBC 2.1, sezione 5.6, per ulteriori dettagli.", null, null, "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.", "Il messaggio di �bind� \u00e8 troppo lungo ({0}). Questo pu\u00f2 essere causato da una dimensione eccessiva o non corretta dei parametri dell''�InputStream�.", null, null, null, null, "Statement has been closed.", "Questo �Statement� \u00e8 stato chiuso.", "No value specified for parameter {0}.", "Nessun valore specificato come parametro {0}.", null, null, null, null, "The array index is out of range: {0}", "Indice di colonna fuori dall''intervallo ammissibile: {0}", null, null, null, null, "Unable to bind parameter values for statement.", "Impossibile fare il �bind� dei valori passati come parametri per lo statement.", "Can''t refresh the insert row.", "Non \u00e8 possibile aggiornare la riga in inserimento.", null, null, "No primary key found for table {0}.", "Non \u00e8 stata trovata la chiave primaria della tabella �{0}�.", "Cannot change transaction isolation level in the middle of a transaction.", "Non \u00e8 possibile cambiare il livello di isolamento delle transazioni nel mezzo di una transazione.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Provided InputStream failed.", "L''�InputStream� fornito \u00e8 fallito.", "The parameter index is out of range: {0}, number of parameters: {1}.", "Il parametro indice \u00e8 fuori intervallo: {0}, numero di elementi: {1}.", "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.", "Il parametro del server �DateStyle� \u00e8 stato cambiato in {0}. Il driver JDBC richiede che �DateStyle� cominci con �ISO� per un corretto funzionamento.", null, null, null, null, "Connection attempt timed out.", "Il tentativo di connessione \u00e8 scaduto.", null, null, "Internal Query: {0}", "Query interna: {0}", null, null, null, null, "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.", "L''autenticazione di tipo {0} non \u00e8 supportata. Verificare che nel file di configurazione pg_hba.conf sia presente l''indirizzo IP o la sottorete del client, e che lo schema di autenticazione utilizzato sia supportato dal driver.", null, null, null, null, null, null, "Interval {0} not yet implemented", "L''intervallo �{0}� non \u00e8 stato ancora implementato.", null, null, null, null, "Conversion of interval failed", "Fallita la conversione di un �interval�.", null, null, null, null, null, null, "Query timeout must be a value greater than or equals to 0.", "Il timeout relativo alle query deve essere maggiore o eguale a 0.", "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.", "La �Connection� \u00e8 stata chiusa automaticamente perch\u00e9 una nuova l''ha sostituita nello stesso �PooledConnection�, oppure il �PooledConnection� \u00e8 stato chiuso.", "ResultSet not positioned properly, perhaps you need to call next.", "Il �ResultSet� non \u00e8 correttamente posizionato; forse \u00e8 necessario invocare �next()�.", null, null, null, null, "This statement has been closed.", "Questo statement \u00e8 stato chiuso.", "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.", "Non \u00e8 possibile identificare il tipo SQL da usare per l''istanza di tipo �{0}�. Usare �setObject()� specificando esplicitamente il tipo da usare per questo valore.", "Cannot call updateRow() when on the insert row.", "Non \u00e8 possibile invocare �updateRow()� durante l''inserimento di una riga.", null, null, null, null, null, null, "Detail: {0}", "Dettaglio: {0}", null, null, "Cannot call deleteRow() when on the insert row.", "Non \u00e8 possibile invocare �deleteRow()� durante l''inserimento di una riga.", "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.", "La posizione attuale \u00e8 precedente all''inizio del ResultSet. Non \u00e8 possibile invocare �deleteRow()� qui.", null, null, null, null, null, null, "Illegal UTF-8 sequence: final value is a surrogate value: {0}", "Sequenza UTF-8 illegale: il valore \u00e8 finale \u00e8 un surrogato: {0}", "Unknown Response Type {0}.", "Risposta di tipo sconosciuto {0}.", null, null, "Unsupported value for stringtype parameter: {0}", "Il valore per il parametro di tipo string �{0}� non \u00e8 supportato.", "Conversion to type {0} failed: {1}.", "Conversione al tipo {0} fallita: {1}.", "Conversion of money failed.", "Fallita la conversione di un �money�.", null, null, null, null, null, null, null, null, null, null, null, null, "Unable to load the class {0} responsible for the datatype {1}", "Non \u00e8 possibile caricare la class �{0}� per gestire il tipo �{1}�.", null, null, "The fastpath function {0} is unknown.", "La funzione fastpath �{0}� \u00e8 sconosciuta.", null, null, "Malformed function or procedure escape syntax at offset {0}.", "Sequenza di escape definita erroneamente nella funzione o procedura all''offset {0}.", null, null, "Provided Reader failed.", "Il �Reader� fornito \u00e8 fallito.", "Maximum number of rows must be a value grater than or equal to 0.", "Il numero massimo di righe deve essere maggiore o eguale a 0.", "Failed to create object for: {0}.", "Fallita la creazione dell''oggetto per: {0}.", null, null, null, null, "Premature end of input stream, expected {0} bytes, but only read {1}.", "Il flusso di input \u00e8 stato interrotto, sono arrivati {1} byte al posto dei {0} attesi.", null, null, "An unexpected result was returned by a query.", "Un risultato inaspettato \u00e8 stato ricevuto dalla query.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "An error occurred while setting up the SSL connection.", "Si \u00e8 verificato un errore impostando la connessione SSL.", null, null, null, null, null, null, "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}", "Sequenza UTF-8 illegale: {0} byte utilizzati per codificare un valore di {1} byte: {2}", null, null, "The SSLSocketFactory class provided {0} could not be instantiated.", "La classe �SSLSocketFactory� specificata, �{0}�, non pu\u00f2 essere istanziata.", null, null, null, null, null, null, null, null, null, null, "Position: {0}", "Posizione: {0}", null, null, null, null, "Location: File: {0}, Routine: {1}, Line: {2}", "Individuazione: file: \"{0}\", routine: {1}, linea: {2}", null, null, null, null, null, null, "Cannot tell if path is open or closed: {0}.", "Impossibile stabilire se il percorso \u00e8 aperto o chiuso: {0}.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Cannot convert an instance of {0} to type {1}", "Non \u00e8 possibile convertire una istanza di �{0}� nel tipo �{1}�", null, null, null, null, null, null, null, null, "{0} function takes four and only four argument.", "Il metodo �{0}� accetta quattro e solo quattro argomenti.", null, null, null, null, null, null, "Interrupted while attempting to connect.", "Si \u00e8 verificata una interruzione durante il tentativo di connessione.", null, null, "Illegal UTF-8 sequence: final value is out of range: {0}", "Sequenza UTF-8 illegale: il valore finale \u00e8 fuori dall''intervallo permesso: {0}", null, null, null, null, null, null, null, null, null, null, null, null, "{0} function takes one and only one argument.", "Il metodo �{0}� accetta un ed un solo argomento.", null, null, null, null, null, null, "This ResultSet is closed.", "Questo �ResultSet� \u00e8 chiuso.", "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.", "Sono stati trovati caratteri non validi tra i dati. Molto probabilmente sono stati memorizzati dei caratteri che non sono validi per la codifica dei caratteri impostata alla creazione del database. Il caso pi\u00f9 diffuso \u00e8 quello nel quale si memorizzano caratteri a 8bit in un database con codifica SQL_ASCII.", null, null, "An I/O error occurred while sending to the backend.", "Si \u00e8 verificato un errore di I/O nella spedizione di dati al server.", null, null, "Ran out of memory retrieving query results.", "Fine memoria scaricando i risultati della query.", "Returning autogenerated keys is not supported.", "La restituzione di chiavi autogenerate non \u00e8 supportata.", null, null, "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.", "L''operazione richiete un �ResultSet� scorribile mentre questo \u00e8 �FORWARD_ONLY�.", "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.", "\u00c8 stato eseguito un �CallableStatement� ma il parametro in uscita �{0}� era di tipo �{1}� al posto di �{2}�, che era stato dichiarato.", null, null, null, null, "Unknown ResultSet holdability setting: {0}.", "Il parametro �holdability� per il �ResultSet� \u00e8 sconosciuto: {0}.", null, null, "Transaction isolation level {0} not supported.", "Il livello di isolamento delle transazioni �{0}� non \u00e8 supportato.", null, null, "No results were returned by the query.", "Nessun risultato \u00e8 stato restituito dalla query.", "A CallableStatement was executed with nothing returned.", "Un �CallableStatement� \u00e8 stato eseguito senza produrre alcun risultato. ", "The maximum field size must be a value greater than or equal to 0.", "La dimensione massima del campo deve essere maggiore o eguale a 0.", null, null, null, null, "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.", "Questo statement non dichiara il parametro in uscita. Usare �{ ?= call ... }� per farlo.", "Can''t use relative move methods while on the insert row.", "Non \u00e8 possibile utilizzare gli spostamenti relativi durante l''inserimento di una riga.", null, null, "Connection is busy with another transaction", "La connessione \u00e8 utilizzata da un''altra transazione" };
    }
}
