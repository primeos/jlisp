/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (IPv4Locator.java) is part of JLISP.                             *
 *                                                                            *
 * JLISP is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * JLISP is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with JLISP.  If not, see <http://www.gnu.org/licenses/>.             *
 ******************************************************************************/
package bar.f0o.jlisp.xTR;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import bar.f0o.jlisp.lib.ControlPlane.EncapsulatedControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.MapReply;
import bar.f0o.jlisp.lib.ControlPlane.MapRequest;
import bar.f0o.jlisp.lib.ControlPlane.Rec;
import bar.f0o.jlisp.lib.ControlPlane.Record;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;


public class Cache {
	
	private HashMap<EidPrefix,CacheEntry> mappings = new HashMap<EidPrefix,CacheEntry>();
	byte[] mappingSystemIP;
	
	private static Cache cache;
	
	public static Cache getCache(){
		if(cache == null) cache = new Cache();
		return cache;
	}
	
	
	
	
	private Cache(){
		this.mappingSystemIP = Config.getMS();

	}
	
	public synchronized byte[] getRLocForEid(byte[] eid){
		
		int longestPrefix = 0;
		CacheEntry mapping = null;
		
		for(EidPrefix pre : mappings.keySet()){
			if(pre.match(eid) && pre.getPrefixLength() > longestPrefix)
			{
				mapping = mappings.get(pre);
				longestPrefix = pre.getPrefixLength();
			}
		}
		if(mapping == null){
			new Thread(new MapRequester(mappingSystemIP, eid, mappings));
			return null;
		}
		return Config.useV4()?mapping.getFirstV4Rloc():mapping.getFirstV6Rloc();
	}




	//Perform Map Request, no nonce yet
	class MapRequester implements Runnable{
		
		byte[] eidRequest;
		HashMap<EidPrefix,CacheEntry> mappingCache;
		byte[] mappingSystemIP;
		
		public MapRequester(byte[] mappingSystemIP, byte[] eidRequest, HashMap<EidPrefix,CacheEntry> mappingCache){
			this.eidRequest = eidRequest;
			this.mappingCache = mappingCache;
			this.mappingSystemIP = mappingSystemIP;
		}
		
		
		
		@Override
		public void run() {
			
			//Generate Message
			Rec r = new Rec((byte)(eidRequest.length*8), eidRequest.length==4?ControlMessage.AfiType.IPv4:ControlMessage.AfiType.IPv6, this.eidRequest);
			ArrayList<Rec> recs = new ArrayList<Rec>();
			recs.add(r);
			HashMap<Short,byte[]> itrs = new HashMap<Short, byte[]>();
			
			itrs.put((short) 1, Config.getOwnRloc());
			
			byte src[] = {};
			MapRequest req = new MapRequest( false,false, false, false, false,false 
					, new Random().nextLong(), 
					AfiType.NONE, src,
					itrs, recs, null);
					
			EncapsulatedControlMessage message = new EncapsulatedControlMessage(Config.getOwnRloc(),
					eidRequest,(short)60573,(short)4342,req);	
			
			byte[] ligBytes = message.toByteArray();
			
			
			//Send Message
			DatagramSocket sock;
			try{
			DatagramPacket ligPacket = new DatagramPacket(ligBytes, ligBytes.length, InetAddress.getByAddress(Config.getMS()), 4342);
			sock = new DatagramSocket(60573);
			sock.send(ligPacket);
			byte[] answer = new byte[Config.getMTU()];
			DatagramPacket ligAnswer = new DatagramPacket(answer, answer.length);;
			sock.receive(ligAnswer);
			sock.close();
			byte[] answerRightSize = new byte[ligAnswer.getLength()];
			System.arraycopy(answer, 0, answerRightSize, 0, answerRightSize.length);
			DataInputStream answerStream = new DataInputStream(new ByteArrayInputStream(answerRightSize));

			MapReply rep = new MapReply(answerStream);
			ArrayList<Record> records = rep.getRecords();

			//Parse each record
			for(Record record : records){
				EidPrefix result = new EidPrefix(record.getEidPrefix(), record.getEidMaskLen());

				if(record.getLocatorCount() == 0){
					mappingCache.put(result, null);
					return;
				}
				
				CacheEntry resultEntry = new CacheEntry();
				for(Loc loc : record.getLocs()){
					if(loc.getLocAFI()==ControlMessage.AfiType.IPv4)
							resultEntry.addV4Rloc(loc);
					else 
						resultEntry.addV6Rloc(loc);
				}
				mappingCache.put(result, resultEntry);
			}
			
			}
			catch(Exception e){
				e.printStackTrace();
			}

			
			
			
		}
	}
}
