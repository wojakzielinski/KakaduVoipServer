package tcpMessage;


import com.google.protobuf.ByteString;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protocols.Protocols;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by Szymon on 22.07.2018.
 */
public class UdpClientHandler extends IoHandlerAdapter {

    boolean stopaudioCapture = false;
    ByteArrayOutputStream byteOutputStream;
    AudioFormat adFormat;
    TargetDataLine targetDataLine;
    AudioInputStream InputStream;
    public static volatile SourceDataLine sourceLine;
    private static volatile Mixer receivingMixer = null;
    private static float gain = 1.0f;

    public static void setReceivingMixer(Mixer mixer){
        receivingMixer = mixer;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if(message instanceof Protocols.UdpPacket){
            System.out.println("UdpClient packet arrived time = "+System.currentTimeMillis());
            //System.out.println(message);
            ByteString voiceBytes = ((Protocols.UdpPacket) message).getVoiceBytes();

            playAudio(voiceBytes);
        }
    }
    @Override
    public void sessionOpened(IoSession session) {
        System.out.println("UDP.sessionOpened");
    }

    public float getGainValue() {
        return gain;
    }
    public static void setGain(float fGain) {

         if (sourceLine != null)
         System.out.println(((FloatControl)
         sourceLine.getControl(FloatControl.Type.MASTER_GAIN)).getValue());

        // Set the value
        gain = fGain;

        // Better type
        if (sourceLine != null && sourceLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
            ( (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN) ).setValue((float) ( 40*  Math.log10(fGain <= 0.0 ? 0.0000 : fGain) ));

        // OR (Math.log(fGain == 0.0 ? 0.0000 : fGain) / Math.log(10.0))

         if (sourceLine != null)
         System.out.println(((FloatControl)
         sourceLine.getControl(FloatControl.Type.MASTER_GAIN)).getValue());
    }
    public void playAudio(ByteString voiceBytes){
        try {
            byte audioData[] = voiceBytes.toByteArray();
            InputStream byteInputStream = new ByteArrayInputStream(audioData);
            AudioFormat adFormat = getAudioFormat();
            InputStream = new AudioInputStream(byteInputStream, adFormat, audioData.length / adFormat.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
            sourceLine = (SourceDataLine) receivingMixer.getLine(dataLineInfo);

            sourceLine.open(adFormat);

            sourceLine.start();
            for(int i=0; i<sourceLine.getControls().length;i++){
                System.out.println(sourceLine.getControls()[i].getType());
            }

            Thread playThread = new Thread(new PlayThread());
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[4000];

        public void run() {
            try {
                int cnt;
                while ((cnt = InputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceLine.write(tempBuffer, 0, cnt);

                        System.out.println(receivingMixer.getMixerInfo().getName());
                    }
                }
                //                sourceLine.drain();
                //             sourceLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
