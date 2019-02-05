/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (IPv4Packet.java) is part of jlisp.                              *
 *                                                                            *
 * jlisp is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * jlisp is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the                *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with $project.name.If not, see <http://www.gnu.org/licenses/>.       *
 ******************************************************************************/

package bar.f0o.jlisp.lib.Net;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

public class IPv4Packet extends IPPacket {

    private byte headerLength = 5;
    private byte    tos = 0;
    private short   totalLength;
    private short   identification;
    private boolean dfFlag;
    private boolean nfFlag;
    private short   fragmentOffset = 0;
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

    /**
     * 
     * @param stream Byte stream to generate IPv4 Packet
     * @throws IOException
     */
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
        if(this.headerLength > 5)
        { optionHeaders = new byte[this.headerLength*4-20];
        	for (int i = 20; i < this.headerLength*4; i++) {
        		optionHeaders[i-20] = stream.readByte();
        	}
        }
        this.payload = new GenericPayload(stream,(this.totalLength-(this.headerLength*4)));
    }

    /**
     * Simple IPv4 Packet
     * @param sourceAddress Source Address
     * @param destinationAddress Destination Address
     */
    public IPv4Packet(byte[] sourceAddress, byte[] destinationAddress) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.identification = (short) new Random().nextInt();
    }

    /**
     * 
     * @param packet Raw data as byte array
     */
    public IPv4Packet(byte[] packet) {
        this.headerLength = (byte) (packet[0] & 0x0F);
        this.tos = packet[1];
        this.totalLength = (short) ((packet[2] << 8) + (packet[3]&0xFF));
        this.identification = (short) (((packet[4]) << 8) + ((packet[5])&0xFF));
        short tmpOffset = (short) ((packet[6] << 8) + packet[7]);
        this.dfFlag = (tmpOffset & 0b0100000000000000) != 0;
        this.nfFlag = (tmpOffset & 0b0010000000000000) != 0;
        this.fragmentOffset = (short) (tmpOffset & 0x0FFF);
        this.ttl = packet[8];
        this.protocol = packet[9];
        this.checksum = (short) (((packet[10]) << 8) + (packet[11]&0xFF));
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
        int j = 0;
        for (int i = this.headerLength*4; i < packet.length; i++) {
            tmpPayload[j] = packet[i];
            j++;
        }
        this.payload = new GenericPayload(tmpPayload);

    }

    /**
     * Generate IPv4 Checksum
     */
    private void checksum() {
    	this.checksum = 0;
        byte[] tmp = toByteArray();
        int i = 0;
        long sum = 0;
        int length = this.headerLength*4;
        while (length > 0) {
            sum += (tmp[i++]&0xff) << 8;
            if ((--length)==0) break;
            sum += (tmp[i++]&0xff);
            --length;
        }
        checksum = (short)((~((sum & 0xFFFF)+(sum >> 16)))&0xFFFF);
    }
    
   
    /**
     * Add Payload to existing IPv4 Packet
     */
    public void addPayload(IPPayload payload) {
    	this.protocol = payload.getProtocol();
        this.payload = payload;
        this.totalLength = (short) (headerLength*4 + payload.getLength());
        this.checksum();
    }
    
    /**
     * 
     * @return Get payload from IPv4 packet
     */
    public IPPayload getPayload(){
    	return this.payload;
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
            for (int i = 20; i < this.headerLength*4; i++) {
                stream.writeByte(optionHeaders[i-20]);
            }
            stream.write(payload.toByteArray());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteStream.toByteArray();
    }

    	/**
    	 * @return Source IP
    	 */
        public byte[] getSrcIP() {
        	return this.sourceAddress;
        }
        /**
         * 
         * @param ip Source IP
         */
        public void setSrcIP(byte[] ip){
        	this.sourceAddress = ip;
        }
        /**
         * @return Destinatio IP
         */
        public byte[] getDstIP() {
        	return this.destinationAddress;
        }
        /**
         * 
         * @param ip Destination IP
         */
        public void setDstIP(byte[] ip){
        	this.destinationAddress = ip;
        }
        /**
         * @return Time to live in hops
         */
        public byte getTTL() {
        	return this.ttl;
        }
        /**
         * @param ttl Time to live in hops
         */
        public void setTTL(byte ttl) {
        	this.ttl = ttl;
        }

        /**
         * @return Type of Service
         */
        public byte getToS() {
        	return this.tos;
        }
        /**
         * @param tos Type of service
         */
        public void setToS(byte tos) {
        	this.tos = tos;
        }
        
        /**
         * 
         * @return Checksum
         */
        public short getChecksum(){
        	return checksum;
        }
        /**
         * Update checksum after altering the packet
         */
        public void updateChecksum(){
        	this.checksum();
        }
        
        

}
