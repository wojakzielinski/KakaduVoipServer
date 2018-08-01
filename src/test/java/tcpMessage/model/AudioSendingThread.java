package tcpMessage.model;

import com.google.protobuf.ByteString;
import protocols.Protocols;
import tcpMessage.UdpClient;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Created by Szymon on 23.07.2018.
 */
public class AudioSendingThread implements Runnable {
    private UdpClient udpClient;
    private String username, roomName;
    private byte[] tempBuffer;
    private Protocols.UdpPacket.Builder udpPacketBuilder;
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private static volatile boolean running = true;

    public AudioSendingThread(UdpClient udpClient, String username, String roomName) {
        this.udpClient = udpClient;
        this.username = username;
        this.roomName = roomName;
        udpPacketBuilder = Protocols.UdpPacket.newBuilder();
        udpPacketBuilder.setRoomName(roomName);
        udpPacketBuilder.setUsername(username);
        tempBuffer = new byte[4000];
    }

    @Override
    public void run() {
        try {
            //Get everything set up for capture
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine)
                    AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            while (running) {

                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                if (cnt > 0) {
                    ByteString voiceBytes = ByteString.copyFrom(tempBuffer);

                    udpPacketBuilder.setVoiceBytes(voiceBytes);
                    this.udpClient.send(udpPacketBuilder.build());
                    System.out.println("UdpClient packet send time = " + System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            System.out.println("Niespodziewany błąd podczas przechwytywania dźwięku: ");
            e.printStackTrace();
        }
    }


    public static void terminate(){
        running = false;
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
