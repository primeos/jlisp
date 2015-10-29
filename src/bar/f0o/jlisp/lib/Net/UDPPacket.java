/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (UDPPacket.java) is part of jlisp.                               *
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

public class UDPPacket extends IPPayload {

	private short srcPort;
	private short dstPort;
	private short length;
	private short checksum;
	private byte[] payload;
	
	public UDPPacket(byte[] srcAddresss,short srcPort,byte[] dstAddress,short dstPort,byte[] payload){
		this.srcPort = srcPort;
		this.dstPort = dstPort;
		this.payload = payload;
		this.length = (short) (8 + payload.length);
		if(srcAddresss.length == 4)
			generateChecksumV4(srcAddresss, dstAddress);
		else if(srcAddresss.length == 32)
			generateChecksumV6(srcAddresss, dstAddress);
		else{
			System.err.println("Wrong number of octets in src address");
		}
	}
	
	
	public UDPPacket(DataInputStream stream) throws IOException {
		this.srcPort = stream.readShort();
		this.dstPort = stream.readShort();
		this.length = stream.readShort();
		this.checksum = stream.readShort();
		stream.readFully(payload);
	}

	public UDPPacket(byte[] data){
		this.srcPort = (short)((data[0]<<8)+(data[1]&0xFF));
		this.dstPort = (short)((data[2]<<8)+(data[3]&0xFF));
		this.length = (short)((data[4]<<8)+(data[5]&0xFF));
		this.checksum = (short)((data[6]<<8)+(data[7]&0xFF));
		this.payload = new byte[data.length-8];
		System.arraycopy(data, 8, payload, 0, payload.length);
	}
	
	@Override
	public byte[] toByteArray() {
		 ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	        DataOutputStream stream = new DataOutputStream(byteStream);
	        try {
	        	stream.writeShort(srcPort);
	            stream.writeShort(dstPort);
	            stream.writeShort(length);
	            stream.writeShort(checksum);
	            stream.write(payload);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return byteStream.toByteArray();	
	}

	@Override
	public int getLength() {
		return (payload.length/4)+2;
	}

	@Override
	public byte getProtocol() {
		return IPPayload.UDP;
	}
	
	public void generateChecksumV4(byte[] src,byte[] dst){
		int sum = 0;
		sum += (short) ((src[1] + (src[0] << 8)) ^ 0xFFFF); 
		sum += (short) ((dst[1] + (dst[0] << 8)) ^ 0xFFFF); 
		sum += ((short) 17) ^ 0xFFFF;
		sum += this.length ^ 0xFFFF;
		for(int i=0;i<payload.length;i+=2){
			if(i+1 == payload.length)
				sum += (payload[i] << 16) ^ 0xFFFF;
			else
				sum += ((payload[i] << 16) +payload[i+1]) ^ 0xFFFF;
		}
		while((sum & 0xFFFF0000) != 0){
			int carry = (sum & 0xFFFF0000) >> 16;
		    sum &= 0x0000FFFF;
		    sum += carry;
		}
		
	}
	
	private void generateChecksumV6(byte[] src,byte[] dst){
		
	}


	public short getChecksum() {
		return this.checksum;
	}


	public short getSrcPort() {
		return this.srcPort;
	}
	public short getDstPort() {
		return this.dstPort;
	}
	
	public byte[] getPayload(){
		return this.payload;
	}
}
