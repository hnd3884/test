package com.zoho.security.validator.zip;

import java.io.IOException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.io.File;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;

class ZSec7ZipOutputFile
{
    static void putEntry(final SevenZOutputFile sevenZipOutputFile, final String zipEntry) throws IOException {
        sevenZipOutputFile.putArchiveEntry((ArchiveEntry)sevenZipOutputFile.createArchiveEntry(new File(zipEntry), zipEntry));
    }
}
