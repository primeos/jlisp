/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (Controller.java) is part of jlisp.                              *
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

import bar.f0o.jlisp.JLISP;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.lib.ControlPlane.IPv4Locator;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister.HmacType;
import bar.f0o.jlisp.lib.ControlPlane.Record;
import bar.f0o.jlisp.xTR.Config.Rloc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class RTR extends LISPComponent {


	public RTR() throws IOException{
		new Thread(inputRaw).start();
		new Thread(new InputListenerLISP()).start();
		
	}
	
	public void start(){
	}
	

	
	
	
	//Save own nonces send with echo request to another RLOC
	public static synchronized void saveNonceToRloc(byte[] rloc, long nonce){
		
	}
	
	//Get nonce that should be in a packet from another RLOC
	public static synchronized long getNonceEchoToRloc(byte[] rloc){
		return 0;
	}
	
	//Save echo requests from other RLOCs
	public static synchronized void saveNonceFromRloc(byte[] rloc, long nonce){
		
	}
	//Get echo request that has to be sent back
	public static synchronized long getNonceEchoFromRloc(byte[] rloc){
		return 0;
	}	
	
	//Check if Other RLocs Version > saved one
	public static void checkSourceVersionNumber(short srcVersionNumber,byte[] otherRloc) {
		//If other RLOCs Version > then Map Request
	}
	
	//Check if own number > parameter
	public static void checkDestinationVersionNumber(short srcVersionNumber,byte[] otherRloc) {
		//if own number > srcVersionNumber  SMR
	}
		
	public void addSendWorker(Runnable worker){
		poolSend.execute(worker);
	}
	
	public void addReceiveWorker(Runnable worker){
		poolReceive.execute(worker);
	}


	public static int getMTU() {
		return 1300;
	}

	public static int getFd() {
		return fd;
	}
	
	public static void setFd(int fd){
		RTR.fd = fd;
	}

	public static DatagramSocket getLispSender() {
		return inputRaw.getSender();
	}
	
	
}
