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
import bar.f0o.jlisp.lib.ControlPlane.LCAF.ExplicitLocatorPath;
import bar.f0o.jlisp.lib.ControlPlane.LCAF.LCAFLocator;
import bar.f0o.jlisp.lib.ControlPlane.IPv4Locator;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.Locator;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister.HmacType;
import bar.f0o.jlisp.lib.ControlPlane.Record;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XTR extends LISPComponent {

	public XTR(){
		super();
	}
	
	public void start() throws SocketException{
		new Thread(inputRaw).start();
		new Thread(new InputListenerLISP()).start();
		this.registerELP();
	}

	//Only v4 At the moment
	private void register(){
		ArrayList<Record> records = new ArrayList<>();
		for(String prefix : JLISP.getConfig().getEIDs()){
			ArrayList<Loc> locators = new ArrayList<>();
			for(Config.Rloc rloc : JLISP.getConfig().getRlocs()){
				try{
					;
				}catch(Exception e){}
				Loc l = new Loc((byte)rloc.getPrio(),(byte)rloc.getWeight(),(byte)rloc.getPrio(),(byte)rloc.getWeight(),true,false,false,AfiType.IPv4,new IPv4Locator(rloc.getAddress()));
				locators.add(l);
			}
			String[] eidPrefix = prefix.split("/");
			String[] eidBytes = eidPrefix[0].split("\\.");
			byte[] eid = new byte[4];
			for(int i=0;i<4;i++){
				eid[i] = Byte.valueOf(eidBytes[i]);
			}
			;
			Record r = new Record((byte)1,Byte.valueOf(eidPrefix[1]),(byte)0,false,(short)1,AfiType.IPv4,eid,locators);
			records.add(r);
		}
		MapRegister reg = new MapRegister(true, true, 1234, HmacType.HMAC_SHA_1_96, JLISP.getConfig().getMSPasswd().getBytes(), records);
		byte[] message = reg.toByteArray();
		//Send Message
		DatagramSocket sock;
		try{
			DatagramPacket ligPacket = new DatagramPacket(message, message.length, InetAddress.getByAddress(JLISP.getConfig().getMS()), 4342);
			sock = new DatagramSocket(60574);
			sock.send(ligPacket);
		}catch(Exception e){e.printStackTrace();};
	}

	//Only v4 At the moment
	private void registerELP(){
		ArrayList<Record> records = new ArrayList<>();
		for(String prefix : JLISP.getConfig().getEIDs()){
			ArrayList<Loc> locators = new ArrayList<>();
			for(Config.Rloc rloc : JLISP.getConfig().getRlocs()){
				try{
					;
				}catch(Exception e){}
				//TODO: GET ELP FROM CONFIG, JUST BAD HACK ATM
				ArrayList<Boolean> bits = new ArrayList<>();
				bits.add(true);
				bits.add(true);
				ArrayList<AfiType> afis = new ArrayList<>();
				afis.add(AfiType.IPv4);
				afis.add(AfiType.IPv4);
				ArrayList<byte[]> reencapHops = new ArrayList<>();
				try{
					reencapHops.add(Inet4Address.getByName("192.168.122.26").getAddress());
				}catch(Exception e){}
				reencapHops.add(rloc.getAddress());
				Locator elp = new LCAFLocator(new ExplicitLocatorPath(bits,bits,bits,afis,reencapHops));
				Loc l = new Loc((byte)rloc.getPrio(),(byte)rloc.getWeight(),(byte)rloc.getPrio(),(byte)rloc.getWeight(),true,false,false,AfiType.LCAF,elp);
				locators.add(l);
			}
			String[] eidPrefix = prefix.split("/");
			String[] eidBytes = eidPrefix[0].split("\\.");
			byte[] eid = new byte[4];
			for(int i=0;i<4;i++){
				eid[i] = Byte.valueOf(eidBytes[i]);
			}
			;
			Record r = new Record((byte)1,Byte.valueOf(eidPrefix[1]),(byte)0,false,(short)1,AfiType.IPv4,eid,locators);
			records.add(r);
		}
		MapRegister reg = new MapRegister(true, true, 1234, HmacType.HMAC_SHA_1_96, JLISP.getConfig().getMSPasswd().getBytes(), records);
		byte[] message = reg.toByteArray();
		//Send Message
		DatagramSocket sock;
		try{
			DatagramPacket ligPacket = new DatagramPacket(message, message.length, InetAddress.getByAddress(JLISP.getConfig().getMS()), 4342);
			sock = new DatagramSocket(60574);
			sock.send(ligPacket);
		}catch(Exception e){e.printStackTrace();};
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


	public static DatagramSocket getLispSender() {
		return inputRaw.getSender();
	}
	
	
}
