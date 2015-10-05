package bar.f0o.jlisp.lib.Net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IPv4Packet extends IPPacket {

    private byte headerLength = 5;
    private byte    tos;
    private short   totalLength;
    private short   identification;
    private boolean dfFlag;
    private boolean nfFlag;
    private short   fragmentOffset;
    private byte ttl      = 16;
    private byte protocol = 17;
    private short checksum;
    private byte[]   sourceAddress = new byte[4];
    private byte[]   destinationAddress = new byte[4];
    private byte[] optionHeaders;
    private IPPayload payload;

    @SuppressWarnings("unused")
	private IPv4Packet() {
    }

    public IPv4Packet(DataInputStream stream) throws IOException {
        byte lengthTmp = stream.readByte();
        this.headerLength = (byte) (lengthTmp & 0x0F);
        this.tos = stream.readByte();
        this.totalLength = stream.readShort();
        this.identification = stream.readShort();
        short tmpOffset = stream.readShort();
        this.dfFlag = (tmpOffset & 0b0100000000000000) != 0;
        this.nfFlag = (tmpOffset & 0b0010000000000000) != 0;
        this.fragmentOffset = (short) (tmpOffset & 0x0FFF);
        this.ttl = stream.readByte();
        this.protocol = stream.readByte();
        this.checksum = stream.readShort();
        stream.read(sourceAddress);
        stream.read(destinationAddress);
        for (int i = 5; i < this.headerLength; i++) {
            stream.readInt();
        }
        switch(this.protocol){
        case IPPayload.UDP:
        	payload = new UDPPacket(stream);
        	break;
        case IPPayload.ICMP:
        	payload = new ICMPPacket(stream);
        	break;
        default:
        	throw new IOException("Unexpected Protocol");
        }
    }

    public IPv4Packet(byte[] sourceAddress, byte[] destinationAddress) {
        this.totalLength = 33;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
    }

    public IPv4Packet(byte[] packet) {
        this.headerLength = (byte) (packet[0] & 0x0F);
        this.tos = packet[1];
        this.totalLength = (short) ((packet[2] << 8) + packet[3]);
        this.identification = (short) ((packet[4] << 8) + packet[5]);
        short tmpOffset = (short) ((packet[6] << 8) + packet[7]);
        this.dfFlag = (tmpOffset & 0b0100000000000000) != 0;
        this.nfFlag = (tmpOffset & 0b0010000000000000) != 0;
        this.fragmentOffset = (short) (tmpOffset & 0x0FFF);
        this.ttl = packet[8];
        this.protocol = packet[9];
        this.checksum = (short) ((packet[10] << 8) + packet[11]);
        this.sourceAddress[0] = packet[12];
        this.sourceAddress[1] = packet[13];
        this.sourceAddress[2] = packet[14];
        this.sourceAddress[3] = packet[15];
        this.destinationAddress[0] = packet[16];
        this.destinationAddress[1] = packet[17];
        this.destinationAddress[2] = packet[18];
        this.destinationAddress[3] = packet[19];
        this.optionHeaders = new byte[this.headerLength*4 - 20];
        for (int i = 20; i < this.headerLength*4; i++) {
            this.optionHeaders[i-20] = packet[i];
        }
        
        byte[] tmpPayload = new byte[packet.length - this.headerLength*4];
        for (int i = this.headerLength*4 - 1; i < packet.length; i++) {
            tmpPayload[i] = packet[i];
        }
        this.payload = new GenericPayload(tmpPayload);

    }

    private void checksum() {
        byte[] tmp = toByteArray();
        short sum = 0;
        for (int i = 0; i < tmp.length; i += 2) {
            short tmpSum = (short) (tmp[i + 1] + (tmp[i] << 8));
            tmpSum ^= 0xFFFF;
            sum += tmpSum;
        }
        checksum = sum ^= 0xFFFF;
    }

    public void addPayload(IPPayload payload) {
    	this.protocol = payload.getProtocol();
        this.payload = payload;
        this.totalLength = (short) ((this.headerLength + payload.getLength()) * 4);
        System.out.println(totalLength + " "+ payload.getLength());
        this.checksum();
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        try {
        	stream.writeByte((headerLength | 0b01000000));
        	stream.writeByte(tos);
            stream.writeShort(totalLength);
            stream.writeShort(identification);
            short tmpOffset = fragmentOffset;
            if(dfFlag)
            	tmpOffset |= 0b0100000000000000;
            if(nfFlag)
            	tmpOffset |= 0b0010000000000000;
            stream.writeShort(tmpOffset);
            stream.writeByte(ttl);
            stream.writeByte(protocol);
            stream.writeShort(checksum);
            stream.write(sourceAddress);
            stream.write(destinationAddress);
            stream.write(payload.toByteArray());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteStream.toByteArray();
    }

        public byte[] getSrcIP() {
        	return this.sourceAddress;
        }
        public byte[] getDstIP() {
        	return this.destinationAddress;
        }

        public byte getTTL() {
        	return this.ttl;
        }
        public void setTTL(byte ttl) {
        	this.ttl = ttl;
        }

        public byte getToS() {
        	return this.tos;
        }
        public void setToS(byte tos) {
        	this.tos = tos;
        }

}
