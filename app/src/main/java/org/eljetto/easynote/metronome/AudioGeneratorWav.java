package org.eljetto.easynote.metronome;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import org.eljetto.easynote.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class AudioGeneratorWav {

    private AudioTrack audioTrack;
    private WaveInfo waveInfo;
    private Context context;
    private byte[] ding;
    private byte[] dong;

    public AudioGeneratorWav(Context context) throws IOException {
        waveInfo = new WaveInfo();
        this.context = context;
        createPlayer();
    }

    private static final int HEADER_SIZE = 44;

    private void readSound() throws IOException {
        //pay attention to sound length, in some cases exceptions can occur
        //proper fix is to use somehow two different sound infos
        InputStream inDong = context.getResources().openRawResource(R.raw.defaultclick);//16 bit with any sample rate
        readHeader(inDong);
        dong = readWavPcm(inDong);
        inDong.close();
        InputStream inDing = context.getResources().openRawResource(R.raw.cowbell);//16 bit with any sample rate
        readHeader(inDing);
        ding = readWavPcm(inDing);
        inDing.close();
    }

    private void readHeader(InputStream wavStream)
            throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());

        buffer.rewind();
        buffer.position(buffer.position() + 20);
        waveInfo.setFormat(buffer.getShort());
        waveInfo.setChannels(buffer.getShort());
        waveInfo.setRate(buffer.getInt());
        buffer.position(buffer.position() + 6);
        waveInfo.setBits(buffer.getShort());
        while (buffer.getInt() != 0x61746164) {
            int size = buffer.getInt();
            wavStream.skip(size);
            buffer.rewind();
            wavStream.read(buffer.array(), buffer.arrayOffset(), 8);
            buffer.rewind();
        }
        setHackDataSize(buffer.getInt());
    }

    private void setHackDataSize(int dataSize) {
        if(waveInfo.getDataSize() == 0 || dataSize < waveInfo.getDataSize()){
            waveInfo.setDataSize(dataSize);
        }
    }

    private byte[] readWavPcm(InputStream stream) throws IOException {
        byte[] data = new byte[waveInfo.getDataSize()];
        stream.read(data, 0, data.length);
        return data;
    }


    public void createPlayer() throws IOException {
        readSound();
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                waveInfo.getRate(), AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, waveInfo.getRate(),
                AudioTrack.MODE_STREAM);
        audioTrack.play();
    }

    public void writeSound(byte[] play) {
        audioTrack.write(play, 0, play.length);
    }

    public void destroyAudioTrack() {
        audioTrack.stop();
        audioTrack.release();
    }

    public WaveInfo getWaveInfo() {
        return waveInfo;
    }

    public byte[] getDing() {
        return ding;
    }

    public byte[] getDong() {
        return dong;
    }
}

