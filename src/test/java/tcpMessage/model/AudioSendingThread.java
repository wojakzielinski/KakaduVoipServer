package tcpMessage.model;

import com.google.protobuf.ByteString;
import protocols.Protocols;
import tcpMessage.UdpClient;

/**
 * Created by Szymon on 23.07.2018.
 */
public class AudioSendingThread  implements Runnable{
    private UdpClient udpClient;
    private String username, roomName;
    private Protocols.UdpPacket.Builder udpPacketBuilder;
    private volatile boolean running = true;

    public AudioSendingThread(UdpClient udpClient, String username, String roomName) {
        this.udpClient = udpClient;
        this.username = username;
        this.roomName = roomName;
        udpPacketBuilder = Protocols.UdpPacket.newBuilder();
        udpPacketBuilder.setRoomName(roomName);
        udpPacketBuilder.setUsername(username);
    }

    @Override
    public void run() {
        while(running){
            try {
                ByteString voiceBytes = ByteString.copyFrom("WIADOMOSC".getBytes());
                udpPacketBuilder.setVoiceBytes(voiceBytes);
                System.out.println("UdpClient packet send time = "+System.currentTimeMillis());
                this.udpClient.send(udpPacketBuilder.build());
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        running = false;
    }
}
