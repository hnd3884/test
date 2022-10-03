package com.sun.nio.zipfs;

import java.util.Collections;
import java.nio.file.Paths;

public class ZipInfo
{
    public static void main(final String[] array) throws Throwable {
        if (array.length < 1) {
            print("Usage: java ZipInfo zfname", new Object[0]);
        }
        else {
            final ZipFileSystem zipFileSystem = (ZipFileSystem)new ZipFileSystemProvider().newFileSystem(Paths.get(array[0], new String[0]), Collections.emptyMap());
            final byte[] cen = zipFileSystem.cen;
            if (cen == null) {
                print("zip file is empty%n", new Object[0]);
                return;
            }
            int n = 0;
            final byte[] array2 = new byte[1024];
            int n2 = 1;
            while (n + 46 < cen.length) {
                print("----------------#%d--------------------%n", n2++);
                printCEN(cen, n);
                final long n3 = 30 + ZipConstants.CENNAM(cen, n) + ZipConstants.CENEXT(cen, n) + 46;
                if (zipFileSystem.readFullyAt(array2, 0, n3, locoff(cen, n)) != n3) {
                    ZipFileSystem.zerror("read loc header failed");
                }
                if (ZipConstants.LOCEXT(array2) > ZipConstants.CENEXT(cen, n) + 46) {
                    final long n4 = 30 + ZipConstants.LOCNAM(array2) + ZipConstants.LOCEXT(array2);
                    if (zipFileSystem.readFullyAt(array2, 0, n4, locoff(cen, n)) != n4) {
                        ZipFileSystem.zerror("read loc header failed");
                    }
                }
                printLOC(array2);
                n += 46 + ZipConstants.CENNAM(cen, n) + ZipConstants.CENEXT(cen, n) + ZipConstants.CENCOM(cen, n);
            }
            zipFileSystem.close();
        }
    }
    
    static void print(final String s, final Object... array) {
        System.out.printf(s, array);
    }
    
    static void printLOC(final byte[] array) {
        print("%n", new Object[0]);
        print("[Local File Header]%n", new Object[0]);
        print("    Signature   :   %#010x%n", ZipConstants.LOCSIG(array));
        if (ZipConstants.LOCSIG(array) != ZipConstants.LOCSIG) {
            print("    Wrong signature!", new Object[0]);
            return;
        }
        print("    Version     :       %#6x    [%d.%d]%n", ZipConstants.LOCVER(array), ZipConstants.LOCVER(array) / 10, ZipConstants.LOCVER(array) % 10);
        print("    Flag        :       %#6x%n", ZipConstants.LOCFLG(array));
        print("    Method      :       %#6x%n", ZipConstants.LOCHOW(array));
        print("    LastMTime   :   %#10x    [%tc]%n", ZipConstants.LOCTIM(array), ZipUtils.dosToJavaTime(ZipConstants.LOCTIM(array)));
        print("    CRC         :   %#10x%n", ZipConstants.LOCCRC(array));
        print("    CSize       :   %#10x%n", ZipConstants.LOCSIZ(array));
        print("    Size        :   %#10x%n", ZipConstants.LOCLEN(array));
        print("    NameLength  :       %#6x    [%s]%n", ZipConstants.LOCNAM(array), new String(array, 30, ZipConstants.LOCNAM(array)));
        print("    ExtraLength :       %#6x%n", ZipConstants.LOCEXT(array));
        if (ZipConstants.LOCEXT(array) != 0) {
            printExtra(array, 30 + ZipConstants.LOCNAM(array), ZipConstants.LOCEXT(array));
        }
    }
    
    static void printCEN(final byte[] array, final int n) {
        print("[Central Directory Header]%n", new Object[0]);
        print("    Signature   :   %#010x%n", ZipConstants.CENSIG(array, n));
        if (ZipConstants.CENSIG(array, n) != ZipConstants.CENSIG) {
            print("    Wrong signature!", new Object[0]);
            return;
        }
        print("    VerMadeby   :       %#6x    [%d, %d.%d]%n", ZipConstants.CENVEM(array, n), ZipConstants.CENVEM(array, n) >> 8, (ZipConstants.CENVEM(array, n) & 0xFF) / 10, (ZipConstants.CENVEM(array, n) & 0xFF) % 10);
        print("    VerExtract  :       %#6x    [%d.%d]%n", ZipConstants.CENVER(array, n), ZipConstants.CENVER(array, n) / 10, ZipConstants.CENVER(array, n) % 10);
        print("    Flag        :       %#6x%n", ZipConstants.CENFLG(array, n));
        print("    Method      :       %#6x%n", ZipConstants.CENHOW(array, n));
        print("    LastMTime   :   %#10x    [%tc]%n", ZipConstants.CENTIM(array, n), ZipUtils.dosToJavaTime(ZipConstants.CENTIM(array, n)));
        print("    CRC         :   %#10x%n", ZipConstants.CENCRC(array, n));
        print("    CSize       :   %#10x%n", ZipConstants.CENSIZ(array, n));
        print("    Size        :   %#10x%n", ZipConstants.CENLEN(array, n));
        print("    NameLen     :       %#6x    [%s]%n", ZipConstants.CENNAM(array, n), new String(array, n + 46, ZipConstants.CENNAM(array, n)));
        print("    ExtraLen    :       %#6x%n", ZipConstants.CENEXT(array, n));
        if (ZipConstants.CENEXT(array, n) != 0) {
            printExtra(array, n + 46 + ZipConstants.CENNAM(array, n), ZipConstants.CENEXT(array, n));
        }
        print("    CommentLen  :       %#6x%n", ZipConstants.CENCOM(array, n));
        print("    DiskStart   :       %#6x%n", ZipConstants.CENDSK(array, n));
        print("    Attrs       :       %#6x%n", ZipConstants.CENATT(array, n));
        print("    AttrsEx     :   %#10x%n", ZipConstants.CENATX(array, n));
        print("    LocOff      :   %#10x%n", ZipConstants.CENOFF(array, n));
    }
    
    static long locoff(final byte[] array, final int n) {
        final long cenoff = ZipConstants.CENOFF(array, n);
        if (cenoff == 4294967295L) {
            int sh2;
            for (int n2 = n + 46 + ZipConstants.CENNAM(array, n); n2 + 4 < n2 + ZipConstants.CENEXT(array, n); n2 += 4 + sh2) {
                final int sh = ZipConstants.SH(array, n2);
                sh2 = ZipConstants.SH(array, n2 + 2);
                if (sh == 1) {
                    n2 += 4;
                    if (ZipConstants.CENLEN(array, n) == 4294967295L) {
                        n2 += 8;
                    }
                    if (ZipConstants.CENSIZ(array, n) == 4294967295L) {
                        n2 += 8;
                    }
                    return ZipConstants.LL(array, n2);
                }
            }
        }
        return cenoff;
    }
    
    static void printExtra(final byte[] array, int n, final int n2) {
        int sh2;
        for (int n3 = n + n2; n + 4 <= n3; n += sh2) {
            final int sh = ZipConstants.SH(array, n);
            sh2 = ZipConstants.SH(array, n + 2);
            print("        [tag=0x%04x, sz=%d, data= ", sh, sh2);
            if (n + sh2 > n3) {
                print("    Error: Invalid extra data, beyond extra length", new Object[0]);
                break;
            }
            n += 4;
            for (int i = 0; i < sh2; ++i) {
                print("%02x ", array[n + i]);
            }
            print("]%n", new Object[0]);
            switch (sh) {
                case 1: {
                    print("         ->ZIP64: ", new Object[0]);
                    for (int n4 = n; n4 + 8 <= n + sh2; n4 += 8) {
                        print(" *0x%x ", ZipConstants.LL(array, n4));
                    }
                    print("%n", new Object[0]);
                    break;
                }
                case 10: {
                    print("         ->PKWare NTFS%n", new Object[0]);
                    if (ZipConstants.SH(array, n + 4) != 1 || ZipConstants.SH(array, n + 6) != 24) {
                        print("    Error: Invalid NTFS sub-tag or subsz", new Object[0]);
                    }
                    print("            mtime:%tc%n", ZipUtils.winToJavaTime(ZipConstants.LL(array, n + 8)));
                    print("            atime:%tc%n", ZipUtils.winToJavaTime(ZipConstants.LL(array, n + 16)));
                    print("            ctime:%tc%n", ZipUtils.winToJavaTime(ZipConstants.LL(array, n + 24)));
                    break;
                }
                case 21589: {
                    print("         ->Info-ZIP Extended Timestamp: flag=%x%n", array[n]);
                    for (int n5 = n + 1; n5 + 4 <= n + sh2; n5 += 4) {
                        print("            *%tc%n", ZipUtils.unixToJavaTime(ZipConstants.LG(array, n5)));
                    }
                    break;
                }
                default: {
                    print("         ->[tag=%x, size=%d]%n", sh, sh2);
                    break;
                }
            }
        }
    }
}
