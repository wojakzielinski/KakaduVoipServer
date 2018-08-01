package tcpMessage;


import com.google.protobuf.ByteString;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protocols.Protocols;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by Szymon on 22.07.2018.
 */
public class UdpClientHandler extends IoHandlerAdapter {
    private static volatile boolean running = true;
    private AudioFormat audioFormat;
    private byte[] tempBuffer = new byte[4000];
    private AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if(message instanceof Protocols.UdpPacket){
            System.out.println("UdpClient packet arrived time = "+System.currentTimeMillis());
            System.out.println(message);
            while(running){
                ByteString audioData = ((Protocols.UdpPacket) message).getVoiceBytes();

                InputStream byteArrayInputStream = new ByteArrayInputStream(audioData.toByteArray());
                AudioFormat audioFormat = getAudioFormat();
                audioInputStream  = new AudioInputStream(byteArrayInputStream, audioFormat,audioData.size());
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                sourceDataLine = (SourceDataLine) AudioSystem.getLine((dataLineInfo));
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                try{
                    int cnt;
                    while((cnt = audioInputStream.read(tempBuffer, 0 ,tempBuffer.length)) != -1){
                        if(cnt>0){
                            sourceDataLine.write(tempBuffer, 0 ,cnt);
                        }
                    }
                    sourceDataLine.drain();
                    sourceDataLine.close();
                }catch(Exception e){
                    e.getMessage();
                }
            }
        }
    }
    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("UDP.sessionOpened");
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
