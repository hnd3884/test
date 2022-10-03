package com.sun.media.sound;

import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import javax.sound.midi.Patch;

public final class SoftTuning
{
    private String name;
    private final double[] tuning;
    private Patch patch;
    
    public SoftTuning() {
        this.name = null;
        this.tuning = new double[128];
        this.patch = null;
        this.name = "12-TET";
        for (int i = 0; i < this.tuning.length; ++i) {
            this.tuning[i] = i * 100;
        }
    }
    
    public SoftTuning(final byte[] array) {
        this.name = null;
        this.tuning = new double[128];
        this.patch = null;
        for (int i = 0; i < this.tuning.length; ++i) {
            this.tuning[i] = i * 100;
        }
        this.load(array);
    }
    
    public SoftTuning(final Patch patch) {
        this.name = null;
        this.tuning = new double[128];
        this.patch = null;
        this.patch = patch;
        this.name = "12-TET";
        for (int i = 0; i < this.tuning.length; ++i) {
            this.tuning[i] = i * 100;
        }
    }
    
    public SoftTuning(final Patch patch, final byte[] array) {
        this.name = null;
        this.tuning = new double[128];
        this.patch = null;
        this.patch = patch;
        for (int i = 0; i < this.tuning.length; ++i) {
            this.tuning[i] = i * 100;
        }
        this.load(array);
    }
    
    private boolean checksumOK(final byte[] array) {
        int n = array[1] & 0xFF;
        for (int i = 2; i < array.length - 2; ++i) {
            n ^= (array[i] & 0xFF);
        }
        return (array[array.length - 2] & 0xFF) == (n & 0x7F);
    }
    
    public void load(final byte[] array) {
        Label_1169: {
            if ((array[1] & 0xFF) == 0x7E || (array[1] & 0xFF) == 0x7F) {
                switch (array[3] & 0xFF) {
                    case 8: {
                        switch (array[4] & 0xFF) {
                            case 1: {
                                try {
                                    this.name = new String(array, 6, 16, "ascii");
                                }
                                catch (final UnsupportedEncodingException ex) {
                                    this.name = null;
                                }
                                int n = 22;
                                for (int i = 0; i < 128; ++i) {
                                    final int n2 = array[n++] & 0xFF;
                                    final int n3 = array[n++] & 0xFF;
                                    final int n4 = array[n++] & 0xFF;
                                    if (n2 != 127 || n3 != 127 || n4 != 127) {
                                        this.tuning[i] = 100.0 * ((n2 * 16384 + n3 * 128 + n4) / 16384.0);
                                    }
                                }
                                break Label_1169;
                            }
                            case 2: {
                                final int n5 = array[6] & 0xFF;
                                int n6 = 7;
                                for (int j = 0; j < n5; ++j) {
                                    final int n7 = array[n6++] & 0xFF;
                                    final int n8 = array[n6++] & 0xFF;
                                    final int n9 = array[n6++] & 0xFF;
                                    final int n10 = array[n6++] & 0xFF;
                                    if (n8 != 127 || n9 != 127 || n10 != 127) {
                                        this.tuning[n7] = 100.0 * ((n8 * 16384 + n9 * 128 + n10) / 16384.0);
                                    }
                                }
                                break Label_1169;
                            }
                            case 4: {
                                if (!this.checksumOK(array)) {
                                    break Label_1169;
                                }
                                try {
                                    this.name = new String(array, 7, 16, "ascii");
                                }
                                catch (final UnsupportedEncodingException ex2) {
                                    this.name = null;
                                }
                                int n11 = 23;
                                for (int k = 0; k < 128; ++k) {
                                    final int n12 = array[n11++] & 0xFF;
                                    final int n13 = array[n11++] & 0xFF;
                                    final int n14 = array[n11++] & 0xFF;
                                    if (n12 != 127 || n13 != 127 || n14 != 127) {
                                        this.tuning[k] = 100.0 * ((n12 * 16384 + n13 * 128 + n14) / 16384.0);
                                    }
                                }
                                break Label_1169;
                            }
                            case 5: {
                                if (!this.checksumOK(array)) {
                                    break Label_1169;
                                }
                                try {
                                    this.name = new String(array, 7, 16, "ascii");
                                }
                                catch (final UnsupportedEncodingException ex3) {
                                    this.name = null;
                                }
                                final int[] array2 = new int[12];
                                for (int l = 0; l < 12; ++l) {
                                    array2[l] = (array[l + 23] & 0xFF) - 64;
                                }
                                for (int n15 = 0; n15 < this.tuning.length; ++n15) {
                                    this.tuning[n15] = n15 * 100 + array2[n15 % 12];
                                }
                                break Label_1169;
                            }
                            case 6: {
                                if (!this.checksumOK(array)) {
                                    break Label_1169;
                                }
                                try {
                                    this.name = new String(array, 7, 16, "ascii");
                                }
                                catch (final UnsupportedEncodingException ex4) {
                                    this.name = null;
                                }
                                final double[] array3 = new double[12];
                                for (int n16 = 0; n16 < 12; ++n16) {
                                    array3[n16] = (((array[n16 * 2 + 23] & 0xFF) * 128 + (array[n16 * 2 + 24] & 0xFF)) / 8192.0 - 1.0) * 100.0;
                                }
                                for (int n17 = 0; n17 < this.tuning.length; ++n17) {
                                    this.tuning[n17] = n17 * 100 + array3[n17 % 12];
                                }
                                break Label_1169;
                            }
                            case 7: {
                                final int n18 = array[7] & 0xFF;
                                int n19 = 8;
                                for (int n20 = 0; n20 < n18; ++n20) {
                                    final int n21 = array[n19++] & 0xFF;
                                    final int n22 = array[n19++] & 0xFF;
                                    final int n23 = array[n19++] & 0xFF;
                                    final int n24 = array[n19++] & 0xFF;
                                    if (n22 != 127 || n23 != 127 || n24 != 127) {
                                        this.tuning[n21] = 100.0 * ((n22 * 16384 + n23 * 128 + n24) / 16384.0);
                                    }
                                }
                                break Label_1169;
                            }
                            case 8: {
                                final int[] array4 = new int[12];
                                for (int n25 = 0; n25 < 12; ++n25) {
                                    array4[n25] = (array[n25 + 8] & 0xFF) - 64;
                                }
                                for (int n26 = 0; n26 < this.tuning.length; ++n26) {
                                    this.tuning[n26] = n26 * 100 + array4[n26 % 12];
                                }
                                break Label_1169;
                            }
                            case 9: {
                                final double[] array5 = new double[12];
                                for (int n27 = 0; n27 < 12; ++n27) {
                                    array5[n27] = (((array[n27 * 2 + 8] & 0xFF) * 128 + (array[n27 * 2 + 9] & 0xFF)) / 8192.0 - 1.0) * 100.0;
                                }
                                for (int n28 = 0; n28 < this.tuning.length; ++n28) {
                                    this.tuning[n28] = n28 * 100 + array5[n28 % 12];
                                }
                                break Label_1169;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
    
    public double[] getTuning() {
        return Arrays.copyOf(this.tuning, this.tuning.length);
    }
    
    public double getTuning(final int n) {
        return this.tuning[n];
    }
    
    public Patch getPatch() {
        return this.patch;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
