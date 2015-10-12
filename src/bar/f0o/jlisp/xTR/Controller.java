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

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {	
		
	private static ExecutorService poolSend = Executors.newFixedThreadPool(50);
	private static ExecutorService poolReceive = Executors.newFixedThreadPool(50);
	private static int fd;
	
	public Controller() throws IOException{
		new Thread(new InputListenerRaw()).start();
		new Thread(new InputListenerLISP()).start();
	}
	
	
	public static void main(String[] args) throws IOException{
		new Controller();
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
	
	
	public static void addSendWorker(Runnable worker){
		poolSend.execute(worker);
	}
	
	public static void addReceiveWorker(Runnable worker){
		poolReceive.execute(worker);
	}


	public static int getMTU() {
		return 1500;
	}


	public static int getFd() {
		return fd;
	}
	public static void setFd(int fd){
		Controller.fd = fd;
	}


	public static String getIP() {
		return "10.0.0.1/24";
	}
	
}
