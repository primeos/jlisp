/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ITRWorker.java) is part of jlisp.                               *
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

package bar.f0o.jlisp.xTR;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

import bar.f0o.jlisp.lib.DataPlane.DataMessage;
import bar.f0o.jlisp.lib.Net.IPPacket;

//Innen -> Aussen

/*
 An ITR is a router that resides in a
 LISP site.  Packets sent by sources inside of the LISP site to
 destinations outside of the site are candidates for encapsulation
 by the ITR.  The ITR treats the IP destination address as an EID
 and performs an EID-to-RLOC mapping lookup.  The router then
 prepends an "outer" IP header with one of its globally routable
 RLOCs in the source address field and the result of the mapping
 lookup in the destination address field.  Note that this
 destination RLOC MAY be an intermediate, proxy device that has
 better knowledge of the EID-to-RLOC mapping closer to the
 destination EID.  In general, an ITR receives IP packets from site
 end-systems on one side and sends LISP-encapsulated IP packets
 toward the Internet on the other side.
 */

public class ITRWorker implements Runnable{
	
	private byte[] data;
	private DatagramSocket sender;
	private int length;
	
	public ITRWorker(DatagramSocket sender,byte[] data, int length){
		this.data =  data;
		this.sender = sender;
		this.length = length;
	}

	@Override
	public void run() {
		byte[] dataTrimmed = new byte[this.length];
		System.arraycopy(data, 0, dataTrimmed,0, this.length);
		PluginController.sendRawData(dataTrimmed);
		IPPacket packet = IPPacket.fromByteArray(dataTrimmed);
		
		
		DataMessage message = new DataMessage(true, false, false, false, false, 0, 0, packet);
		PluginController.sendLispData(message);
		byte[] messageBytes = message.toByteArray();
		try {
			;
			byte[] rloc = Cache.getCache().getRLocForEid(packet.getDstIP());
			//No mapping yet: drop the packet
			;
			if(rloc == null) return;
			DatagramPacket UDPPacket = new DatagramPacket(messageBytes, messageBytes.length,InetAddress.getByAddress(rloc),4341);
			sender.send(UDPPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
