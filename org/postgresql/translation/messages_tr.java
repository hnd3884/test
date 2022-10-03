package org.postgresql.translation;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class messages_tr extends ResourceBundle
{
    private static final String[] table;
    
    public Object handleGetObject(final String msgid) throws MissingResourceException {
        final int hash_val = msgid.hashCode() & Integer.MAX_VALUE;
        int idx = hash_val % 397 << 1;
        final Object found = messages_tr.table[idx];
        if (found == null) {
            return null;
        }
        if (msgid.equals(found)) {
            return messages_tr.table[idx + 1];
        }
        final int incr = hash_val % 395 + 1 << 1;
        while (true) {
            idx += incr;
            if (idx >= 794) {
                idx -= 794;
            }
            final Object found2 = messages_tr.table[idx];
            if (found2 == null) {
                return null;
            }
            if (msgid.equals(found2)) {
                return messages_tr.table[idx + 1];
            }
        }
    }
    
    @Override
    public Enumeration getKeys() {
        return new Enumeration() {
            private int idx = 0;
            
            {
                while (this.idx < 794 && messages_tr.table[this.idx] == null) {
                    this.idx += 2;
                }
            }
            
            @Override
            public boolean hasMoreElements() {
                return this.idx < 794;
            }
            
            @Override
            public Object nextElement() {
                final Object key = messages_tr.table[this.idx];
                do {
                    this.idx += 2;
                } while (this.idx < 794 && messages_tr.table[this.idx] == null);
                return key;
            }
        };
    }
    
    public ResourceBundle getParent() {
        return this.parent;
    }
    
    static {
        final String[] t = table = new String[] { "", "Project-Id-Version: jdbc-tr\nReport-Msgid-Bugs-To: \nPO-Revision-Date: 2009-05-31 21:47+0200\nLast-Translator: Devrim G\u00dcND\u00dcZ <devrim@gunduz.org>\nLanguage-Team: Turkish <pgsql-tr-genel@PostgreSQL.org>\nLanguage: tr\nMIME-Version: 1.0\nContent-Type: text/plain; charset=UTF-8\nContent-Transfer-Encoding: 8bit\nX-Generator: KBabel 1.3.1\nX-Poedit-Language: Turkish\nX-Poedit-Country: TURKEY\n", "Not implemented: 2nd phase commit must be issued using an idle connection. commit xid={0}, currentXid={1}, state={2}, transactionState={3}", "Desteklenmiyor: 2nd phase commit, at\u0131l bir ba\u011flant\u0131dan ba\u015flat\u0131lmal\u0131d\u0131r. commit xid={0}, currentXid={1}, state={2}, transactionState={3}", "DataSource has been closed.", "DataSource kapat\u0131ld\u0131.", null, null, "Invalid flags {0}", "Ge\u00e7ersiz se\u00e7enekler {0}", null, null, null, null, null, null, null, null, "Where: {0}", "Where: {0}", null, null, null, null, "Unknown XML Source class: {0}", "Bilinmeyen XML Kaynak S\u0131n\u0131f\u0131: {0}", "The connection attempt failed.", "Ba\u011flant\u0131 denemesi ba\u015far\u0131s\u0131z oldu.", "Currently positioned after the end of the ResultSet.  You cannot call deleteRow() here.", "\u015eu an ResultSet sonucundan sonra konumland\u0131. deleteRow() burada \u00e7a\u011f\u0131rabilirsiniz.", null, null, "Can''t use query methods that take a query string on a PreparedStatement.", "PreparedStatement ile sorgu sat\u0131r\u0131 alan sorgu y\u00f6ntemleri kullan\u0131lamaz.", null, null, "Multiple ResultSets were returned by the query.", "Sorgu taraf\u0131ndan birden fazla ResultSet getirildi.", null, null, null, null, null, null, null, null, null, null, null, null, "Too many update results were returned.", "\u00c7ok fazla g\u00fcncelleme sonucu d\u00f6nd\u00fcr\u00fcld\u00fc.", null, null, null, null, null, null, "Illegal UTF-8 sequence: initial byte is {0}: {1}", "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: ilk bayt {0}: {1}", null, null, null, null, null, null, "The column name {0} was not found in this ResultSet.", "Bu ResultSet i\u00e7inde {0} s\u00fctun ad\u0131 bulunamad\u0131.", null, null, "Fastpath call {0} - No result was returned and we expected an integer.", "Fastpath call {0} - Integer beklenirken hi\u00e7bir sonu\u00e7 getirilmedi.", null, null, "Protocol error.  Session setup failed.", "Protokol hatas\u0131.  Oturum kurulumu ba\u015far\u0131s\u0131z oldu.", "A CallableStatement was declared, but no call to registerOutParameter(1, <some type>) was made.", "CallableStatement bildirildi ancak registerOutParameter(1, < bir tip>) tan\u0131t\u0131m\u0131 yap\u0131lmad\u0131.", "ResultSets with concurrency CONCUR_READ_ONLY cannot be updated.", "E\u015f zamanlama CONCUR_READ_ONLY olan ResultSet''ler de\u011fi\u015ftirilemez", null, null, null, null, null, null, null, null, null, null, "LOB positioning offsets start at 1.", "LOB ba\u011flang\u0131\u00e7 adresi 1Den ba\u015fl\u0131yor", "Internal Position: {0}", "Internal Position: {0}", null, null, "free() was called on this LOB previously", "Bu LOB'da free() daha \u00f6nce \u00e7a\u011f\u0131r\u0131ld\u0131", null, null, "Cannot change transaction read-only property in the middle of a transaction.", "Transaction ortas\u0131nda ge\u00e7erli transactionun read-only \u00f6zell\u011fi de\u011fi\u015ftirilemez.", "The JVM claims not to support the {0} encoding.", "JVM, {0} dil kodlamas\u0131n\u0131 desteklememektedir.", null, null, null, null, "{0} function doesn''t take any argument.", "{0} fonksiyonu parametre almaz.", null, null, "xid must not be null", "xid null olamaz", "Connection has been closed.", "Ba\u011flant\u0131 kapat\u0131ld\u0131.", null, null, null, null, null, null, "The server does not support SSL.", "Sunucu SSL desteklemiyor.", "Custom type maps are not supported.", "\u00d6zel tip e\u015fle\u015ftirmeleri desteklenmiyor.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Illegal UTF-8 sequence: byte {0} of {1} byte sequence is not 10xxxxxx: {2}", "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: {0}/{1} bayt\u0131 10xxxxxx de\u011fildir: {2}", null, null, null, null, null, null, "Hint: {0}", "\u0130pucu: {0}", null, null, "Unable to find name datatype in the system catalogs.", "Sistem kataloglar\u0131nda name veri tipi bulunam\u0131yor.", null, null, "Unsupported Types value: {0}", "Ge\u00e7ersiz Types de\u011feri: {0}", "Unknown type {0}.", "Bilinmeyen tip {0}.", null, null, null, null, null, null, "{0} function takes two and only two arguments.", "{0} fonksiyonunu sadece iki parametre alabilir.", null, null, "Finalizing a Connection that was never closed:", "Kapat\u0131lmam\u0131\u015f ba\u011flant\u0131 sonland\u0131r\u0131l\u0131yor.", null, null, null, null, null, null, null, null, "The maximum field size must be a value greater than or equal to 0.", "En b\u00fcy\u00fck alan boyutu s\u0131f\u0131r ya da s\u0131f\u0131rdan b\u00fcy\u00fck bir de\u011fer olmal\u0131.", null, null, null, null, "PostgreSQL LOBs can only index to: {0}", "PostgreSQL LOB g\u00f6stergeleri sadece {0} referans edebilir", null, null, null, null, null, null, "Method {0} is not yet implemented.", "{0} y\u00f6ntemi hen\u00fcz kodlanmad\u0131.", null, null, "Error loading default settings from driverconfig.properties", "driverconfig.properties dosyas\u0131ndan varsay\u0131lan ayarlar\u0131 y\u00fckleme hatas\u0131", "Results cannot be retrieved from a CallableStatement before it is executed.", "CallableStatement \u00e7al\u0131\u015ft\u0131r\u0131lmadan sonu\u00e7lar ondan al\u0131namaz.", "Large Objects may not be used in auto-commit mode.", "Auto-commit bi\u00e7imde large object kullan\u0131lamaz.", null, null, null, null, "Expected command status BEGIN, got {0}.", "BEGIN komut durumunu beklenirken {0} al\u0131nd\u0131.", null, null, null, null, null, null, null, null, "Invalid fetch direction constant: {0}.", "Getirme y\u00f6n\u00fc de\u011fi\u015fmezi ge\u00e7ersiz: {0}.", null, null, "{0} function takes three and only three arguments.", "{0} fonksiyonunu sadece \u00fc\u00e7 parametre alabilir.", null, null, "This SQLXML object has already been freed.", "Bu SQLXML nesnesi zaten bo\u015falt\u0131lm\u0131\u015f.", "Cannot update the ResultSet because it is either before the start or after the end of the results.", "ResultSet, sonu\u00e7lar\u0131n ilk kayd\u0131ndan \u00f6nce veya son kayd\u0131ndan sonra oldu\u011fu i\u00e7in g\u00fcncelleme yap\u0131lamamaktad\u0131r.", "The JVM claims not to support the encoding: {0}", "JVM, {0} dil kodlamas\u0131n\u0131 desteklememektedir.", "Parameter of type {0} was registered, but call to get{1} (sqltype={2}) was made.", "{0} tipinde parametre tan\u0131t\u0131ld\u0131, ancak {1} (sqltype={2}) tipinde geri getirmek i\u00e7in \u00e7a\u011fr\u0131 yap\u0131ld\u0131.", "Error rolling back prepared transaction. rollback xid={0}, preparedXid={1}, currentXid={2}", "Haz\u0131rlanm\u0131\u015f transaction rollback hatas\u0131. rollback xid={0}, preparedXid={1}, currentXid={2}", null, null, null, null, "Cannot establish a savepoint in auto-commit mode.", "Auto-commit bi\u00e7imde savepoint olu\u015fturulam\u0131yor.", "Cannot retrieve the id of a named savepoint.", "Adland\u0131r\u0131lm\u0131\u015f savepointin id de\u011ferine eri\u015filemiyor.", "The column index is out of range: {0}, number of columns: {1}.", "S\u00fctun g\u00e7stergesi kapsam d\u0131\u015f\u0131d\u0131r: {0}, s\u00fctun say\u0131s\u0131: {1}.", null, null, null, null, "Something unusual has occurred to cause the driver to fail. Please report this exception.", "S\u0131rad\u0131\u015f\u0131 bir durum s\u00fcr\u00fcc\u00fcn\u00fcn hata vermesine sebep oldu. L\u00fctfen bu durumu geli\u015ftiricilere bildirin.", null, null, null, null, null, null, null, null, "Cannot cast an instance of {0} to type {1}", "{0} tipi {1} tipine d\u00f6n\u00fc\u015ft\u00fcr\u00fclemiyor", null, null, "Unknown Types value.", "Ge\u00e7ersiz Types de\u011feri.", "Invalid stream length {0}.", "Ge\u00e7ersiz ak\u0131m uzunlu\u011fu {0}.", null, null, null, null, "Cannot retrieve the name of an unnamed savepoint.", "Ad\u0131 verilmemi\u015f savepointin id de\u011ferine eri\u015filemiyor.", "Unable to translate data into the desired encoding.", "Veri, istenilen dil kodlamas\u0131na \u00e7evrilemiyor.", "Expected an EOF from server, got: {0}", "Sunucudan EOF beklendi; ama {0} al\u0131nd\u0131.", "Bad value for type {0} : {1}", "{0} veri tipi i\u00e7in ge\u00e7ersiz de\u011fer : {1}", "The server requested password-based authentication, but no password was provided.", "Sunucu \u015fifre tabanl\u0131 yetkilendirme istedi; ancak bir \u015fifre sa\u011flanmad\u0131.", null, null, null, null, "Unable to create SAXResult for SQLXML.", "SQLXML i\u00e7in SAXResult yarat\u0131lamad\u0131.", null, null, null, null, "Error during recover", "Kurtarma s\u0131ras\u0131nda hata", "tried to call end without corresponding start call. state={0}, start xid={1}, currentXid={2}, preparedXid={3}", "start \u00e7a\u011f\u0131r\u0131m\u0131 olmadan end \u00e7a\u011f\u0131r\u0131lm\u0131\u015ft\u0131r. state={0}, start xid={1}, currentXid={2}, preparedXid={3}", "Truncation of large objects is only implemented in 8.3 and later servers.", "Large objectlerin temizlenmesi 8.3 ve sonraki s\u00fcr\u00fcmlerde kodlanm\u0131\u015ft\u0131r.", "This PooledConnection has already been closed.", "Ge\u00e7erli PooledConnection zaten \u00f6nceden kapat\u0131ld\u0131.", null, null, "ClientInfo property not supported.", "Clientinfo property'si desteklenememktedir.", null, null, "Fetch size must be a value greater to or equal to 0.", "Fetch boyutu s\u0131f\u0131r veya daha b\u00fcy\u00fck bir de\u011fer olmal\u0131d\u0131r.", null, null, null, null, "A connection could not be made using the requested protocol {0}.", "\u0130stenilen protokol ile ba\u011flant\u0131 kurulamad\u0131 {0}", null, null, null, null, "Unknown XML Result class: {0}", "Bilinmeyen XML Sonu\u00e7 s\u0131n\u0131f\u0131: {0}.", null, null, "There are no rows in this ResultSet.", "Bu ResultSet i\u00e7inde kay\u0131t bulunamad\u0131.", "Unexpected command status: {0}.", "Beklenmeyen komut durumu: {0}.", null, null, null, null, "Heuristic commit/rollback not supported. forget xid={0}", "Heuristic commit/rollback desteklenmiyor. forget xid={0}", null, null, "Not on the insert row.", "Insert kayd\u0131 de\u011fil.", "This SQLXML object has already been initialized, so you cannot manipulate it further.", "Bu SQLXML nesnesi daha \u00f6nceden ilklendirilmi\u015ftir; o y\u00fczden daha fazla m\u00fcdahale edilemez.", null, null, null, null, null, null, "Server SQLState: {0}", "Sunucu SQLState: {0}", null, null, "The server''s standard_conforming_strings parameter was reported as {0}. The JDBC driver expected on or off.", "\u0130stemcinin client_standard_conforming_strings parametresi {0} olarak raporland\u0131. JDBC s\u00fcr\u00fcc\u00fcs\u00fc on ya da off olarak bekliyordu.", null, null, null, null, null, null, null, null, null, null, "The driver currently does not support COPY operations.", "Bu sunucu \u015fu a\u015famada COPY i\u015flemleri desteklememktedir.", null, null, "The array index is out of range: {0}, number of elements: {1}.", "Dizin g\u00f6stergisi kapsam d\u0131\u015f\u0131d\u0131r: {0}, \u00f6\u011fe say\u0131s\u0131: {1}.", null, null, null, null, null, null, null, null, "suspend/resume not implemented", "suspend/resume desteklenmiyor", null, null, "Not implemented: one-phase commit must be issued using the same connection that was used to start it", "Desteklenmiyor: one-phase commit, i\u015flevinde ba\u015flatan ve bitiren ba\u011flant\u0131 ayn\u0131 olmal\u0131d\u0131r", "Error during one-phase commit. commit xid={0}", "One-phase commit s\u0131ras\u0131nda hata. commit xid={0}", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Cannot call cancelRowUpdates() when on the insert row.", "Insert edilmi\u015f kayd\u0131n \u00fczerindeyken cancelRowUpdates() \u00e7a\u011f\u0131r\u0131lamaz.", "Cannot reference a savepoint after it has been released.", "B\u0131rak\u0131ld\u0131ktan sonra savepoint referans edilemez.", "You must specify at least one column value to insert a row.", "Bir sat\u0131r eklemek i\u00e7in en az bir s\u00fctun de\u011ferini belirtmelisiniz.", "Unable to determine a value for MaxIndexKeys due to missing system catalog data.", "Sistem katalo\u011fu olmad\u0131\u011f\u0131ndan MaxIndexKeys de\u011ferini tespit edilememektedir.", null, null, null, null, "commit called before end. commit xid={0}, state={1}", "commit, sondan \u00f6nce \u00e7a\u011f\u0131r\u0131ld\u0131. commit xid={0}, state={1}", "Illegal UTF-8 sequence: final value is out of range: {0}", "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: son de\u011fer s\u0131ra d\u0131\u015f\u0131d\u0131r: {0}", "{0} function takes two or three arguments.", "{0} fonksiyonu yaln\u0131z iki veya \u00fc\u00e7 arg\u00fcman alabilir.", null, null, null, null, null, null, null, null, null, null, null, null, "Unable to convert DOMResult SQLXML data to a string.", "DOMResult SQLXML verisini diziye d\u00f6n\u00fc\u015ft\u00fcr\u00fclemedi.", null, null, null, null, "Unable to decode xml data.", "XML verisinin kodu \u00e7\u00f6z\u00fclemedi.", null, null, null, null, "Unexpected error writing large object to database.", "Large object veritaban\u0131na yaz\u0131l\u0131rken beklenmeyan hata.", "Zero bytes may not occur in string parameters.", "String parametrelerinde s\u0131f\u0131r bayt olamaz.", "A result was returned when none was expected.", "Hi\u00e7bir sonu\u00e7 kebklenimezken sonu\u00e7 getirildi.", null, null, null, null, "ResultSet is not updateable.  The query that generated this result set must select only one table, and must select all primary keys from that table. See the JDBC 2.1 API Specification, section 5.6 for more details.", "ResultSet de\u011fi\u015ftirilemez. Bu sonucu \u00fcreten sorgu tek bir tablodan sorgulamal\u0131 ve tablonun t\u00fcm primary key alanlar\u0131 belirtmelidir. Daha fazla bilgi i\u00e7in bk. JDBC 2.1 API Specification, section 5.6.", null, null, "Bind message length {0} too long.  This can be caused by very large or incorrect length specifications on InputStream parameters.", "Bind mesaj uzunlu\u011fu ({0}) fazla uzun. Bu durum InputStream yaln\u0131\u015f uzunluk belirtimlerden kaynaklanabilir.", null, null, null, null, "Statement has been closed.", "Komut kapat\u0131ld\u0131.", "No value specified for parameter {0}.", "{0} parametresi i\u00e7in hi\u00e7 bir de\u011fer belirtilmedi.", null, null, null, null, "The array index is out of range: {0}", "Dizi g\u00f6stergesi kapsam d\u0131\u015f\u0131d\u0131r: {0}", null, null, null, null, "Unable to bind parameter values for statement.", "Komut i\u00e7in parametre de\u011ferlei ba\u011flanamad\u0131.", "Can''t refresh the insert row.", "Inser sat\u0131r\u0131 yenilenemiyor.", null, null, "No primary key found for table {0}.", "{0} tablosunda primary key yok.", "Cannot change transaction isolation level in the middle of a transaction.", "Transaction ortas\u0131nda ge\u00e7erli transactionun transaction isolation level \u00f6zell\u011fi de\u011fi\u015ftirilemez.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Provided InputStream failed.", "Sa\u011flanm\u0131\u015f InputStream ba\u015far\u0131s\u0131z.", "The parameter index is out of range: {0}, number of parameters: {1}.", "Dizin g\u00f6stergisi kapsam d\u0131\u015f\u0131d\u0131r: {0}, \u00f6\u011fe say\u0131s\u0131: {1}.", "The server''s DateStyle parameter was changed to {0}. The JDBC driver requires DateStyle to begin with ISO for correct operation.", "Sunucunun DateStyle parametresi {0} olarak de\u011fi\u015ftirildi. JDBC s\u00fcr\u00fcc\u00fcs\u00fc do\u011fru i\u015flemesi i\u00e7in DateStyle tan\u0131m\u0131n\u0131n ISO i\u015fle ba\u015flamas\u0131n\u0131 gerekir.", null, null, null, null, "Connection attempt timed out.", "Ba\u011flant\u0131 denemesi zaman a\u015f\u0131m\u0131na u\u011frad\u0131.", null, null, "Internal Query: {0}", "Internal Query: {0}", "Error preparing transaction. prepare xid={0}", "Transaction haz\u0131rlama hatas\u0131. prepare xid={0}", null, null, "The authentication type {0} is not supported. Check that you have configured the pg_hba.conf file to include the client''s IP address or subnet, and that it is using an authentication scheme supported by the driver.", "{0} yetkinlendirme tipi desteklenmemektedir. pg_hba.conf dosyan\u0131z\u0131 istemcinin IP adresini ya da subnetini i\u00e7erecek \u015fekilde ayarlay\u0131p ayarlamad\u0131\u011f\u0131n\u0131z\u0131 ve s\u00fcr\u00fcc\u00fc taraf\u0131ndan desteklenen yetkilendirme y\u00f6ntemlerinden birisini kullan\u0131p kullanmad\u0131\u011f\u0131n\u0131 kontrol ediniz.", null, null, null, null, null, null, "Interval {0} not yet implemented", "{0} aral\u0131\u011f\u0131 hen\u00fcz kodlanmad\u0131.", null, null, null, null, "Conversion of interval failed", "Interval d\u00f6n\u00fc\u015ft\u00fcrmesi ba\u015far\u0131s\u0131z.", null, null, null, null, null, null, "Query timeout must be a value greater than or equals to 0.", "Sorgu zaman a\u015f\u0131m\u0131 de\u011fer s\u0131f\u0131r veya s\u0131f\u0131rdan b\u00fcy\u00fck bir say\u0131 olmal\u0131d\u0131r.", "Connection has been closed automatically because a new connection was opened for the same PooledConnection or the PooledConnection has been closed.", "PooledConnection kapat\u0131ld\u0131\u011f\u0131 i\u00e7in veya ayn\u0131 PooledConnection i\u00e7in yeni bir ba\u011flant\u0131 a\u00e7\u0131ld\u0131\u011f\u0131 i\u00e7in ge\u00e7erli ba\u011flant\u0131 otomatik kapat\u0131ld\u0131.", "ResultSet not positioned properly, perhaps you need to call next.", "ResultSet do\u011fru konumlanmam\u0131\u015ft\u0131r, next i\u015flemi \u00e7a\u011f\u0131rman\u0131z gerekir.", "Prepare called before end. prepare xid={0}, state={1}", "Sondan \u00f6nce prepare \u00e7a\u011f\u0131r\u0131lm\u0131\u015f. prepare xid={0}, state={1}", "Invalid UUID data.", "Ge\u00e7ersiz UUID verisi.", "This statement has been closed.", "Bu komut kapat\u0131ld\u0131.", "Can''t infer the SQL type to use for an instance of {0}. Use setObject() with an explicit Types value to specify the type to use.", "{0}''nin \u00f6rne\u011fi ile kullan\u0131lacak SQL tip bulunamad\u0131. Kullan\u0131lacak tip belirtmek i\u00e7in kesin Types de\u011ferleri ile setObject() kullan\u0131n.", "Cannot call updateRow() when on the insert row.", "Insert  kayd\u0131 \u00fczerinde updateRow() \u00e7a\u011f\u0131r\u0131lamaz.", null, null, null, null, null, null, "Detail: {0}", "Ayr\u0131nt\u0131: {0}", null, null, "Cannot call deleteRow() when on the insert row.", "Insert  kayd\u0131 \u00fczerinde deleteRow() \u00e7a\u011f\u0131r\u0131lamaz.", "Currently positioned before the start of the ResultSet.  You cannot call deleteRow() here.", "\u015eu an ResultSet ba\u015flangc\u0131\u0131ndan \u00f6nce konumland\u0131. deleteRow() burada \u00e7a\u011f\u0131rabilirsiniz.", null, null, null, null, null, null, "Illegal UTF-8 sequence: final value is a surrogate value: {0}", "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: son de\u011fer yapay bir de\u011ferdir: {0}", "Unknown Response Type {0}.", "Bilinmeyen yan\u0131t tipi {0}", null, null, "Unsupported value for stringtype parameter: {0}", "strinftype parametresi i\u00e7in destekleneyen de\u011fer: {0}", "Conversion to type {0} failed: {1}.", "{0} veri tipine d\u00f6n\u00fc\u015ft\u00fcrme hatas\u0131: {1}.", "This SQLXML object has not been initialized, so you cannot retrieve data from it.", "Bu SQLXML nesnesi ilklendirilmemi\u015f; o y\u00fczden ondan veri alamazs\u0131n\u0131z.", null, null, null, null, null, null, null, null, null, null, null, null, "Unable to load the class {0} responsible for the datatype {1}", "{1} veri tipinden sorumlu {0} s\u0131n\u0131f\u0131 y\u00fcklenemedi", null, null, "The fastpath function {0} is unknown.", "{0} fastpath fonksiyonu bilinmemektedir.", null, null, "Malformed function or procedure escape syntax at offset {0}.", "{0} adresinde fonksiyon veya yordamda ka\u00e7\u0131\u015f s\u00f6z dizimi ge\u00e7ersiz.", null, null, "Provided Reader failed.", "Sa\u011flanm\u0131\u015f InputStream ba\u015far\u0131s\u0131z.", "Maximum number of rows must be a value grater than or equal to 0.", "En b\u00fcy\u00fck getirilecek sat\u0131r say\u0131s\u0131 s\u0131f\u0131rdan b\u00fcy\u00fck olmal\u0131d\u0131r.", "Failed to create object for: {0}.", "{0} i\u00e7in nesne olu\u015fturma hatas\u0131.", null, null, "Conversion of money failed.", "Money d\u00f6n\u00fc\u015ft\u00fcrmesi ba\u015far\u0131s\u0131z.", "Premature end of input stream, expected {0} bytes, but only read {1}.", "Giri\u015f ak\u0131m\u0131nda beklenmeyen dosya sonu, {0} bayt beklenirken sadece {1} bayt al\u0131nd\u0131.", null, null, "An unexpected result was returned by a query.", "Sorgu beklenmeyen bir sonu\u00e7 d\u00f6nd\u00fcrd\u00fc.", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "Invalid protocol state requested. Attempted transaction interleaving is not supported. xid={0}, currentXid={1}, state={2}, flags={3}", "Transaction interleaving desteklenmiyor. xid={0}, currentXid={1}, state={2}, flags={3}", "An error occurred while setting up the SSL connection.", "SSL ba\u011flant\u0131s\u0131 ayarlan\u0131rken bir hata olu\u015ftu.", null, null, null, null, null, null, "Illegal UTF-8 sequence: {0} bytes used to encode a {1} byte value: {2}", "Ge\u00e7ersiz UTF-8 \u00e7oklu bayt karakteri: {0} bayt, {1} bayt de\u011feri kodlamak i\u00e7in kullan\u0131lm\u0131\u015f: {2}", "Not implemented: Prepare must be issued using the same connection that started the transaction. currentXid={0}, prepare xid={1}", "Desteklenmiyor: Prepare, transaction ba\u015flatran ba\u011flant\u0131 taraf\u0131ndan \u00e7a\u011f\u0131rmal\u0131d\u0131r. currentXid={0}, prepare xid={1}", "The SSLSocketFactory class provided {0} could not be instantiated.", "SSLSocketFactory {0} ile \u00f6rneklenmedi.", null, null, "Failed to convert binary xml data to encoding: {0}.", "xml verisinin \u015fu dil kodlamas\u0131na \u00e7evirilmesi ba\u015far\u0131s\u0131z oldu: {0}", null, null, null, null, null, null, "Position: {0}", "Position: {0}", null, null, null, null, "Location: File: {0}, Routine: {1}, Line: {2}", "Yer: Dosya: {0}, Yordam: {1}, Sat\u0131r: {2}", null, null, null, null, null, null, "Cannot tell if path is open or closed: {0}.", "Path\u0131n a\u00e7\u0131k m\u0131 kapal\u0131 oldu\u011funu tespit edilemiyor: {0}.", null, null, null, null, "Unable to create StAXResult for SQLXML", "SQLXML i\u00e7in StAXResult yarat\u0131lamad\u0131", null, null, null, null, null, null, null, null, "Cannot convert an instance of {0} to type {1}", "{0} instance, {1} tipine d\u00f6n\u00fc\u015ft\u00fcr\u00fclemiyor", null, null, null, null, null, null, null, null, "{0} function takes four and only four argument.", "{0} fonksiyonunu yaln\u0131z d\u00f6rt parametre alabilir.", null, null, null, null, null, null, "Interrupted while attempting to connect.", "Ba\u011flan\u0131rken kesildi.", null, null, "Your security policy has prevented the connection from being attempted.  You probably need to grant the connect java.net.SocketPermission to the database server host and port that you wish to connect to.", "G\u00fcvenlik politikan\u0131z ba\u011flant\u0131n\u0131n kurulmas\u0131n\u0131 engelledi. java.net.SocketPermission'a veritaban\u0131na ve de ba\u011flanaca\u011f\u0131 porta ba\u011flant\u0131 izni vermelisiniz.", null, null, null, null, null, null, null, null, null, null, "No function outputs were registered.", "Hi\u00e7bir fonksiyon \u00e7\u0131kt\u0131s\u0131 kaydedilmedi.", "{0} function takes one and only one argument.", "{0} fonksiyonunu yaln\u0131z tek bir parametre alabilir.", null, null, null, null, null, null, "This ResultSet is closed.", "ResultSet kapal\u0131d\u0131r.", "Invalid character data was found.  This is most likely caused by stored data containing characters that are invalid for the character set the database was created in.  The most common example of this is storing 8bit data in a SQL_ASCII database.", "Ge\u00e7ersiz karakterler bulunmu\u015ftur. Bunun sebebi, verilerde veritaban\u0131n destekledi\u011fi dil kodlamadaki karakterlerin d\u0131\u015f\u0131nda bir karaktere rastlamas\u0131d\u0131r. Bunun en yayg\u0131n \u00f6rne\u011fi 8 bitlik veriyi SQL_ASCII veritaban\u0131nda saklamas\u0131d\u0131r.", null, null, null, null, "Error disabling autocommit", "autocommit'i devre d\u0131\u015f\u0131 b\u0131rakma s\u0131ras\u0131nda hata", "Ran out of memory retrieving query results.", "Sorgu sonu\u00e7lar\u0131 al\u0131n\u0131rken bellek yetersiz.", "Returning autogenerated keys is not supported.", "Otomatik \u00fcretilen de\u011ferlerin getirilmesi desteklenememktedir.", null, null, "Operation requires a scrollable ResultSet, but this ResultSet is FORWARD_ONLY.", "\u0130\u015flem, kayd\u0131r\u0131labilen ResultSet gerektirir, ancak bu ResultSet FORWARD_ONLYdir.", "A CallableStatement function was executed and the out parameter {0} was of type {1} however type {2} was registered.", "CallableStatement \u00e7al\u0131\u015ft\u0131r\u0131ld\u0131, ancak {2} tipi kaydedilmesine ra\u011fmen d\u00f6nd\u00fcrme parametresi {0} ve tipi {1} idi.", "Unable to find server array type for provided name {0}.", "Belirtilen {0} ad\u0131 i\u00e7in sunucu array tipi bulunamad\u0131.", null, null, "Unknown ResultSet holdability setting: {0}.", "ResultSet tutabilme ayar\u0131 ge\u00e7ersiz: {0}.", null, null, "Transaction isolation level {0} not supported.", "Transaction isolation level {0} desteklenmiyor.", "Zero bytes may not occur in identifiers.", "Belirte\u00e7lerde s\u0131f\u0131r bayt olamaz.", "No results were returned by the query.", "Sorgudan hi\u00e7 bir sonu\u00e7 d\u00f6nmedi.", "A CallableStatement was executed with nothing returned.", "CallableStatement \u00e7al\u0131\u015ft\u0131rma sonucunda veri getirilmedi.", "wasNull cannot be call before fetching a result.", "wasNull sonu\u00e7 \u00e7ekmeden \u00f6nce \u00e7a\u011f\u0131r\u0131lamaz.", null, null, "Returning autogenerated keys by column index is not supported.", "Kolonlar\u0131n indexlenmesi ile otomatik olarak olu\u015fturulan anahtarlar\u0131n d\u00f6nd\u00fcr\u00fclmesi desteklenmiyor.", "This statement does not declare an OUT parameter.  Use '{' ?= call ... '}' to declare one.", "Bu komut OUT parametresi bildirmemektedir.  Bildirmek i\u00e7in '{' ?= call ... '}' kullan\u0131n.", "Can''t use relative move methods while on the insert row.", "Insert kayd\u0131 \u00fczerinde relative move method kullan\u0131lamaz.", "A CallableStatement was executed with an invalid number of parameters", "CallableStatement ge\u00e7ersiz say\u0131da parametre ile \u00e7al\u0131\u015ft\u0131r\u0131ld\u0131.", "Connection is busy with another transaction", "Ba\u011flant\u0131, ba\u015fka bir transaction taraf\u0131ndan me\u015fgul ediliyor" };
    }
}