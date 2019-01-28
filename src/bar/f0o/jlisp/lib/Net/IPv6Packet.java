/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (IPv6Packet.java) is part of jlisp.                              *
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * Not implemented yet
 *
 */
public class IPv6Packet extends IPPacket {

	private byte trafficClass = 0;
	private int flowLabel = 0; //20 Bit
	private short payloadLength = 0;
	private byte nextHeader = 17;
	private byte hopLimit = 16;
	private byte[] sourceAddress = new byte[16];
	private byte[] destAddress = new byte[16];
    private IPPayload payload;


	private IPv6Packet() {
    }

    public IPv6Packet(DataInputStream stream) throws IOException {
		int firstLineTmp = stream.readInt();
		trafficClass = (byte) ((firstLineTmp >> 20) & 0x00F);
		flowLabel = firstLineTmp & 0x000FFFFF;
		payloadLength = stream.readShort();
		nextHeader = stream.readByte();
		hopLimit = stream.readByte();
		stream.read(sourceAddress);
		stream.read(destAddress);
		this.payload = new GenericPayload(stream,this.payloadLength);
    }
	
	public IPv6Packet(byte[] sourceAddress, byte[] destinationAddress) {
		this.sourceAddress = sourceAddress;
		this.destAddress = destinationAddress;
	}
	
	public IPv6Packet(byte[] packet) {
		this.trafficClass = (packet[0] & 0x0F << 4) + (packet[1] >> 4);
		this.flowLabel = (packet[1] & 0x0F << 16) + packet[2] << 8 + packet[3];
		this.payloadLength = (short) (packet[4] << 8 + packet[5]);
		this.nextHeader = (byte) (packet[6]);
		this.hopLimit = (byte) (packet[7]);
		for(int i =0;i<16;i++){
			sourceAddress[i] = packet[8+i];
			destAddress[i] = packet[24+i];
		}
		byte[] tmpPayload = new byte[this.payloadLength];
        for (int i = 40; i < packet.length; i++) {
            tmpPayload[i-40] = packet[i];
        }
        this.payload = new GenericPayload(tmpPayload);

	}
	
	public byte[] toByteArray() {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        try {
			int firstLine = this.trafficClass << 28 + this.flowLabel;        	
			stream.writeInt((firstLine | 0b01100000000000000000000000000000));
			stream.writeShort(this.payloadLength);
			stream.writeByte(this.nextHeader);
			stream.writeByte(this.hopLimit);
            stream.write(sourceAddress);
            stream.write(destAddress);
            stream.write(payload.toByteArray());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byteStream.toByteArray();
	}

	public void addPayload(IPPayload payload) {
		this.nextHeader = payload.getProtocol();
        this.payload = payload;
        this.payloadLength = (short) (payload.getLength());
	}

	public byte[] getSrcIP() {
		return this.sourceAddress;
	}

	public byte[] getDstIP() {
		return this.destAddress;
	}

	public byte getTTL() {
		return this.hopLimit;
	}

	public void setTTL(byte ttl) {
		this.hopLimit = ttl;
	}

	public byte getToS() {
		return this.trafficClass;
	}

	public void setToS(byte tos) {
		this.trafficClass = tos;
	}

}
