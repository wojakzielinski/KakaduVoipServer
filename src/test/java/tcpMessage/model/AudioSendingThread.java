package tcpMessage.model;

import com.google.protobuf.ByteString;
import protocols.Protocols;
import tcpMessage.UdpClient;

import javax.sound.sampled.*;

/**
 * Created by Szymon on 23.07.2018.
 */
public class AudioSendingThread implements Runnable {
    private UdpClient udpClient;
    private String username, roomName;
    private byte[] tempBuffer;
    private Protocols.UdpPacket.Builder udpPacketBuilder;
    private AudioFormat audioFormat;
    public static TargetDataLine targetDataLine;
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
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
            System.out.println("Available mixers:");
            Mixer mixer = null;
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                System.out.println(cnt + " " + mixerInfo[cnt].getName());
                mixer = AudioSystem.getMixer(mixerInfo[cnt]);

                Line.Info[] lineInfos = mixer.getTargetLineInfo();
                if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                    System.out.println(cnt + " Mic is supported!");
                    break;
                }
            }

            mixer = AudioSystem.getMixer(mixerInfo[2]);

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
        float sampleRate = 16000.0F;
        int sampleInbits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
    }
}
