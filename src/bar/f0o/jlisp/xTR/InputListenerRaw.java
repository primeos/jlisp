/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (InputListenerRaw.java) is part of jlisp.                        *
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

import com.sun.jna.LastErrorException;

import bar.f0o.jlisp.lib.Net.CLibrary;

public class InputListenerRaw implements Runnable{

	private int fd;
	private DatagramSocket sender;
	
	public  InputListenerRaw()  {
		try{
		this.sender = new DatagramSocket();
		byte[] ifr = {108,105,115,112,48,0,0,0,0,0,0,0,0,0,0,0,1,16};
		try{
		this.fd = CLibrary.INSTANCE.open("/dev/net/tun", 2);
		Controller.setFd(fd);
		CLibrary.INSTANCE.ioctl(fd,((long)0x400454ca), ifr);
		}catch(LastErrorException ex){
			System.out.println(ex);
		}
		Runtime.getRuntime().exec("ip a a "+Controller.getIP() +" dev lisp0");
		Runtime.getRuntime().exec("ip l s dev lisp0 up");
		Runtime.getRuntime().exec("ip l s dev lisp0 mtu 1300");
		}catch(IOException e){
			e.printStackTrace();
		}

	}
	
	
	@Override
	public void run() {
		while(true){
			byte[] incomming = new byte[Controller.getMTU()];
			try{
			//	int length = CLibrary.INSTANCE.read(fd, incomming, incomming.length);
			}catch (LastErrorException ex) {
				System.out.println(ex);
			}
			//Controller.addSendWorker(new ITRWorker(sender,incomming,length));
		}
	}

	public DatagramSocket getSender(){
		return this.sender;
	}
}
