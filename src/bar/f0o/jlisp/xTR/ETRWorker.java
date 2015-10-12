/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ETRWorker.java) is part of jlisp.                               *
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

import java.net.DatagramPacket;

import bar.f0o.jlisp.lib.DataPlane.DataMessage;
import bar.f0o.jlisp.lib.Net.CLibrary;
import bar.f0o.jlisp.lib.Net.IPPacket;

//Aussen -> Innen

/*
 An ETR is a router that accepts an IP
 packet where the destination address in the "outer" IP header is
 one of its own RLOCs.  The router strips the "outer" header and
 forwards the packet based on the next IP header found.  In
 general, an ETR receives LISP-encapsulated IP packets from the
 Internet on one side and sends decapsulated IP packets to site
 end-systems on the other side.  ETR functionality does not have to
 be limited to a router device.  A server host can be the endpoint
 of a LISP tunnel as well.
 */

public class ETRWorker implements Runnable {

	//Received Message
	DatagramPacket received;
	
	public ETRWorker(DatagramPacket received){
		this.received = received;
	}
	
	
	@Override
	public void run() {
		//Getting received bytes
		byte[] rec = new byte[received.getLength()];
		System.arraycopy(received.getData(), 0, rec,0, rec.length);
		//Parsing Received bytes
		DataMessage message = new DataMessage(rec);		
		//Extracting IP Packet from LISP Message
		IPPacket innerIP = message.getPayload();
		byte[] otherRloc = received.getAddress().getAddress();

		if(message.isnBit()){
			//Nonce Handling, if n bit is set;
			//Other RLOCs AFI
			//If e bit is set, the nonce has to be saved in order to send it back with the next packet
			//otherwise it is a reply to an echo request and has to be compared to the stored value
			if(message.iseBit()){
				Controller.saveNonceFromRloc(otherRloc, message.getNonce());
			}
			else{
				long nonceOld = Controller.getNonceEchoToRloc(otherRloc);
				if(nonceOld != message.getNonce()) throw new RuntimeException("Wrong nonce echoed");
			}
		}else if(message.isvBit()){
			//Map Version checking if v bit is set
			short srcVersionNumber = (short) (message.getNonce() >> 16);
			short dstVersionNumber = (short) (message.getNonce()&0xFFFF);
			Controller.checkSourceVersionNumber(srcVersionNumber,otherRloc);
			Controller.checkDestinationVersionNumber(dstVersionNumber,otherRloc);
		}
		
		
		
		//Sending inner IP Packet to the tun device
		byte[] toSend = innerIP.toByteArray();
		CLibrary.INSTANCE.write(Controller.getFd(), toSend, toSend.length);
		
	}

}
