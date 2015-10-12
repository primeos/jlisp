/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (InputListenerLISP.java) is part of jlisp.                       *
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
import java.net.SocketException;

public class InputListenerLISP implements Runnable{

	private DatagramSocket receiver;
	
	public InputListenerLISP() throws SocketException {
		receiver =  new DatagramSocket(4341);
	}
	
	@Override
	public void run() {
		while(true){
			byte[] buf = new byte[Controller.getMTU()];
			DatagramPacket p = new DatagramPacket(buf, buf.length);
			try {
				receiver.receive(p);
				Controller.addReceiveWorker(new ETRWorker(p));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
