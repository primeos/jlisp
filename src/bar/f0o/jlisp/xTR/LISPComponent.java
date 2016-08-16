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

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.lib.ControlPlane.IPv4Locator;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister.HmacType;
import bar.f0o.jlisp.lib.ControlPlane.Record;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class LISPComponent {

	protected ExecutorService poolSend = Executors.newFixedThreadPool(50);
	protected ExecutorService poolReceive = Executors.newFixedThreadPool(50);
	protected static int fd;
	protected static InputListenerRaw inputRaw = new InputListenerRaw();
	private static LISPComponent component;
	
	public LISPComponent(){
		component = this;
	}
	
	public abstract void start() throws IOException;
	
	
	public void addSendWorker(Runnable worker){
		poolSend.execute(worker);
	}
	
	public void addReceiveWorker(Runnable worker){
		poolReceive.execute(worker);
	}


	public static int getMTU() {
		return 1500;
	}

	public static int getFd() {
		return fd;
	}
	public static void setFd(int fd){
		LISPComponent.fd = fd;
	}

	public static String getIP() {
		return "10.0.0.1/24";
	}

	public static DatagramSocket getLispSender() {
		return inputRaw.getSender();
	}

	public static LISPComponent getComponent() {
		return component;
	}
	
	
}
