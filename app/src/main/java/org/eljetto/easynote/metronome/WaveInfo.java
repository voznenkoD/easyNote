package org.eljetto.easynote.metronome;

public class WaveInfo {

    private short format;
    private short channels;
    private int rate;
    private int bits;
    private int dataSize;

    public short getFormat() {
        return format;
    }

    public void setFormat(short format) {
        this.format = format;
    }

    public short getChannels() {
        return channels;
    }

    public void setChannels(short channels) {
        this.channels = channels;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getBits() {
        return bits;
    }

    public void setBits(int bits) {
        this.bits = bits;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
}
