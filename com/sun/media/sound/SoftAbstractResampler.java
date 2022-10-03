package com.sun.media.sound;

import java.io.IOException;
import java.util.Arrays;
import javax.sound.midi.VoiceStatus;
import javax.sound.midi.MidiChannel;

public abstract class SoftAbstractResampler implements SoftResampler
{
    public abstract int getPadding();
    
    public abstract void interpolate(final float[] p0, final float[] p1, final float p2, final float[] p3, final float p4, final float[] p5, final int[] p6, final int p7);
    
    @Override
    public final SoftResamplerStreamer openStreamer() {
        return new ModelAbstractResamplerStream();
    }
    
    private class ModelAbstractResamplerStream implements SoftResamplerStreamer
    {
        AudioFloatInputStream stream;
        boolean stream_eof;
        int loopmode;
        boolean loopdirection;
        float loopstart;
        float looplen;
        float target_pitch;
        float[] current_pitch;
        boolean started;
        boolean eof;
        int sector_pos;
        int sector_size;
        int sector_loopstart;
        boolean markset;
        int marklimit;
        int streampos;
        int nrofchannels;
        boolean noteOff_flag;
        float[][] ibuffer;
        boolean ibuffer_order;
        float[] sbuffer;
        int pad;
        int pad2;
        float[] ix;
        int[] ox;
        float samplerateconv;
        float pitchcorrection;
        
        ModelAbstractResamplerStream() {
            this.stream_eof = false;
            this.loopdirection = true;
            this.current_pitch = new float[1];
            this.sector_pos = 0;
            this.sector_size = 400;
            this.sector_loopstart = -1;
            this.markset = false;
            this.marklimit = 0;
            this.streampos = 0;
            this.nrofchannels = 2;
            this.noteOff_flag = false;
            this.ibuffer_order = true;
            this.ix = new float[1];
            this.ox = new int[1];
            this.samplerateconv = 1.0f;
            this.pitchcorrection = 0.0f;
            this.pad = SoftAbstractResampler.this.getPadding();
            this.pad2 = SoftAbstractResampler.this.getPadding() * 2;
            this.ibuffer = new float[2][this.sector_size + this.pad2];
            this.ibuffer_order = true;
        }
        
        @Override
        public void noteOn(final MidiChannel midiChannel, final VoiceStatus voiceStatus, final int n, final int n2) {
        }
        
        @Override
        public void noteOff(final int n) {
            this.noteOff_flag = true;
        }
        
        @Override
        public void open(final ModelWavetable modelWavetable, final float n) throws IOException {
            this.eof = false;
            this.nrofchannels = modelWavetable.getChannels();
            if (this.ibuffer.length < this.nrofchannels) {
                this.ibuffer = new float[this.nrofchannels][this.sector_size + this.pad2];
            }
            this.stream = modelWavetable.openStream();
            this.streampos = 0;
            this.stream_eof = false;
            this.pitchcorrection = modelWavetable.getPitchcorrection();
            this.samplerateconv = this.stream.getFormat().getSampleRate() / n;
            this.looplen = modelWavetable.getLoopLength();
            this.loopstart = modelWavetable.getLoopStart();
            this.sector_loopstart = (int)(this.loopstart / this.sector_size);
            --this.sector_loopstart;
            this.sector_pos = 0;
            if (this.sector_loopstart < 0) {
                this.sector_loopstart = 0;
            }
            this.started = false;
            this.loopmode = modelWavetable.getLoopType();
            if (this.loopmode != 0) {
                this.markset = false;
                this.marklimit = this.nrofchannels * (int)(this.looplen + this.pad2 + 1.0f);
            }
            else {
                this.markset = true;
            }
            this.target_pitch = this.samplerateconv;
            this.current_pitch[0] = this.samplerateconv;
            this.ibuffer_order = true;
            this.loopdirection = true;
            this.noteOff_flag = false;
            for (int i = 0; i < this.nrofchannels; ++i) {
                Arrays.fill(this.ibuffer[i], this.sector_size, this.sector_size + this.pad2, 0.0f);
            }
            this.ix[0] = (float)this.pad;
            this.eof = false;
            this.ix[0] = (float)(this.sector_size + this.pad);
            this.sector_pos = -1;
            this.streampos = -this.sector_size;
            this.nextBuffer();
        }
        
        @Override
        public void setPitch(final float n) {
            this.target_pitch = (float)Math.exp((this.pitchcorrection + n) * (Math.log(2.0) / 1200.0)) * this.samplerateconv;
            if (!this.started) {
                this.current_pitch[0] = this.target_pitch;
            }
        }
        
        public void nextBuffer() throws IOException {
            if (this.ix[0] < this.pad && this.markset) {
                this.stream.reset();
                final float[] ix = this.ix;
                final int n = 0;
                ix[n] += this.streampos - this.sector_loopstart * this.sector_size;
                this.sector_pos = this.sector_loopstart;
                this.streampos = this.sector_pos * this.sector_size;
                final float[] ix2 = this.ix;
                final int n2 = 0;
                ix2[n2] += this.sector_size;
                --this.sector_pos;
                this.streampos -= this.sector_size;
                this.stream_eof = false;
            }
            if (this.ix[0] >= this.sector_size + this.pad && this.stream_eof) {
                this.eof = true;
                return;
            }
            if (this.ix[0] >= this.sector_size * 4 + this.pad) {
                final int n3 = (int)((this.ix[0] - this.sector_size * 4 + this.pad) / this.sector_size);
                final float[] ix3 = this.ix;
                final int n4 = 0;
                ix3[n4] -= this.sector_size * n3;
                this.sector_pos += n3;
                this.streampos += this.sector_size * n3;
                this.stream.skip(this.sector_size * n3);
            }
            while (this.ix[0] >= this.sector_size + this.pad) {
                if (!this.markset && this.sector_pos + 1 == this.sector_loopstart) {
                    this.stream.mark(this.marklimit);
                    this.markset = true;
                }
                final float[] ix4 = this.ix;
                final int n5 = 0;
                ix4[n5] -= this.sector_size;
                ++this.sector_pos;
                this.streampos += this.sector_size;
                for (int i = 0; i < this.nrofchannels; ++i) {
                    final float[] array = this.ibuffer[i];
                    for (int j = 0; j < this.pad2; ++j) {
                        array[j] = array[j + this.sector_size];
                    }
                }
                int read;
                if (this.nrofchannels == 1) {
                    read = this.stream.read(this.ibuffer[0], this.pad2, this.sector_size);
                }
                else {
                    final int n6 = this.sector_size * this.nrofchannels;
                    if (this.sbuffer == null || this.sbuffer.length < n6) {
                        this.sbuffer = new float[n6];
                    }
                    final int read2 = this.stream.read(this.sbuffer, 0, n6);
                    if (read2 == -1) {
                        read = -1;
                    }
                    else {
                        read = read2 / this.nrofchannels;
                        for (int k = 0; k < this.nrofchannels; ++k) {
                            final float[] array2 = this.ibuffer[k];
                            for (int n7 = k, nrofchannels = this.nrofchannels, pad2 = this.pad2, l = 0; l < read; ++l, n7 += nrofchannels, ++pad2) {
                                array2[pad2] = this.sbuffer[n7];
                            }
                        }
                    }
                }
                if (read == -1) {
                    this.stream_eof = true;
                    for (int n8 = 0; n8 < this.nrofchannels; ++n8) {
                        Arrays.fill(this.ibuffer[n8], this.pad2, this.pad2 + this.sector_size, 0.0f);
                    }
                    return;
                }
                if (read != this.sector_size) {
                    for (int n9 = 0; n9 < this.nrofchannels; ++n9) {
                        Arrays.fill(this.ibuffer[n9], this.pad2 + read, this.pad2 + this.sector_size, 0.0f);
                    }
                }
                this.ibuffer_order = true;
            }
        }
        
        public void reverseBuffers() {
            this.ibuffer_order = !this.ibuffer_order;
            for (int i = 0; i < this.nrofchannels; ++i) {
                final float[] array = this.ibuffer[i];
                final int n = array.length - 1;
                for (int n2 = array.length / 2, j = 0; j < n2; ++j) {
                    final float n3 = array[j];
                    array[j] = array[n - j];
                    array[n - j] = n3;
                }
            }
        }
        
        @Override
        public int read(final float[][] array, final int n, final int n2) throws IOException {
            if (this.eof) {
                return -1;
            }
            if (this.noteOff_flag && (this.loopmode & 0x2) != 0x0 && this.loopdirection) {
                this.loopmode = 0;
            }
            final float n3 = (this.target_pitch - this.current_pitch[0]) / n2;
            final float[] current_pitch = this.current_pitch;
            this.started = true;
            final int[] ox = this.ox;
            ox[0] = n;
            final int n4 = n2 + n;
            float n5 = (float)(this.sector_size + this.pad);
            if (!this.loopdirection) {
                n5 = (float)this.pad;
            }
            while (ox[0] != n4) {
                this.nextBuffer();
                if (!this.loopdirection) {
                    if (this.streampos < this.loopstart + this.pad) {
                        n5 = this.loopstart - this.streampos + this.pad2;
                        if (this.ix[0] <= n5) {
                            if ((this.loopmode & 0x4) != 0x0) {
                                this.loopdirection = true;
                                n5 = (float)(this.sector_size + this.pad);
                                continue;
                            }
                            final float[] ix = this.ix;
                            final int n6 = 0;
                            ix[n6] += this.looplen;
                            n5 = (float)this.pad;
                            continue;
                        }
                    }
                    if (this.ibuffer_order != this.loopdirection) {
                        this.reverseBuffers();
                    }
                    this.ix[0] = this.sector_size + this.pad2 - this.ix[0];
                    final float n7 = this.sector_size + this.pad2 - n5 + 1.0f;
                    final float n8 = this.ix[0];
                    final int n9 = ox[0];
                    final float n10 = current_pitch[0];
                    for (int i = 0; i < this.nrofchannels; ++i) {
                        if (array[i] != null) {
                            this.ix[0] = n8;
                            ox[0] = n9;
                            current_pitch[0] = n10;
                            SoftAbstractResampler.this.interpolate(this.ibuffer[i], this.ix, n7, current_pitch, n3, array[i], ox, n4);
                        }
                    }
                    this.ix[0] = this.sector_size + this.pad2 - this.ix[0];
                    n5 = this.sector_size + this.pad2 - (n7 - 1.0f);
                    if (this.eof) {
                        current_pitch[0] = this.target_pitch;
                        return ox[0] - n;
                    }
                    continue;
                }
                else {
                    if (this.loopmode != 0 && this.streampos + this.sector_size > this.looplen + this.loopstart + this.pad) {
                        n5 = this.loopstart + this.looplen - this.streampos + this.pad2;
                        if (this.ix[0] >= n5) {
                            if ((this.loopmode & 0x4) != 0x0 || (this.loopmode & 0x8) != 0x0) {
                                this.loopdirection = false;
                                n5 = (float)this.pad;
                                continue;
                            }
                            n5 = (float)(this.sector_size + this.pad);
                            final float[] ix2 = this.ix;
                            final int n11 = 0;
                            ix2[n11] -= this.looplen;
                            continue;
                        }
                    }
                    if (this.ibuffer_order != this.loopdirection) {
                        this.reverseBuffers();
                    }
                    final float n12 = this.ix[0];
                    final int n13 = ox[0];
                    final float n14 = current_pitch[0];
                    for (int j = 0; j < this.nrofchannels; ++j) {
                        if (array[j] != null) {
                            this.ix[0] = n12;
                            ox[0] = n13;
                            current_pitch[0] = n14;
                            SoftAbstractResampler.this.interpolate(this.ibuffer[j], this.ix, n5, current_pitch, n3, array[j], ox, n4);
                        }
                    }
                    if (this.eof) {
                        current_pitch[0] = this.target_pitch;
                        return ox[0] - n;
                    }
                    continue;
                }
            }
            current_pitch[0] = this.target_pitch;
            return n2;
        }
        
        @Override
        public void close() throws IOException {
            this.stream.close();
        }
    }
}
