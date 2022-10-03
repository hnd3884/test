package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_it extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "error.bad.cflag", "Per il flag 'c' \u00e8 necessario specificare file manifest o di input." }, { "error.bad.eflag", "Il flag 'e' e il manifest con l'attributo 'Main-Class' non possono essere specificati\ninsieme." }, { "error.bad.option", "\u00c8 necessario specificare una delle opzioni -{ctxu}." }, { "error.bad.uflag", "Per il flag 'u' \u00e8 necessario specificare il flag 'e' oppure file manifest o di input." }, { "error.cant.open", "impossibile aprire: {0} " }, { "error.create.dir", "{0} : impossibile creare la directory" }, { "error.create.tempfile", "Impossibile creare il file temporaneo." }, { "error.illegal.option", "Opzione non valida: {0}" }, { "error.incorrect.length", "lunghezza non valida durante l''elaborazione: {0}" }, { "error.nosuch.fileordir", "{0} : file o directory inesistente" }, { "error.write.file", "Errore durante la scrittura del file jar esistente" }, { "out.added.manifest", "aggiunto manifest" }, { "out.adding", "aggiunta in corso di: {0}" }, { "out.create", "     creato: {0}" }, { "out.deflated", "(compresso {0}%)" }, { "out.extracted", "estratto: {0}" }, { "out.ignore.entry", "la voce {0} sar\u00e0 ignorata" }, { "out.inflated", " decompresso: {0}" }, { "out.size", "(in = {0}) (out = {1})" }, { "out.stored", "(memorizzato 0%)" }, { "out.update.manifest", "aggiornato manifest" }, { "usage", "Uso: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] files ...\nOpzioni:\n    -c  crea un nuovo archivio\n    -t  visualizza l'indice dell'archivio\n    -x  estrae i file con nome (o tutti i file) dall'archivio\n    -u  aggiorna l'archivio esistente\n    -v  genera output commentato dall'output standard\n    -f  specifica il nome file dell'archivio\n    -m  include informazioni manifest dal file manifest specificato\n    -n  esegue normalizzazione Pack200 dopo la creazione di un nuovo archivio\n    -e  specifica il punto di ingresso per l'applicazione stand-alone \n        inclusa nel file jar eseguibile\n    -0  solo memorizzazione; senza compressione ZIP\n    -P  conserva i componenti iniziali '/' (percorso assoluto) e \\\"..\\\" (directory padre) dai nomi file\n    -M  consente di non creare un file manifest per le voci\n    -i  genera informazioni sull'indice per i file jar specificati\n    -C  imposta la directory specificata e include il file seguente\nSe un file \u00e8 una directory, verr\u00e0 elaborato in modo ricorsivo.\nIl nome del file manifest, del file di archivio e del punto di ingresso devono\nessere specificati nello stesso ordine dei flag 'm', 'f' ed 'e'.\n\nEsempio 1: archiviazione di due file di classe in un archivio con il nome classes.jar: \n       jar cvf classes.jar Foo.class Bar.class \nEsempio 2: utilizzo del file manifest esistente 'mymanifest' e archiviazione di tutti i\n           file della directory foo/ in 'classes.jar': \n       jar cvfm classes.jar mymanifest -C foo/ .\n" } };
    }
}
