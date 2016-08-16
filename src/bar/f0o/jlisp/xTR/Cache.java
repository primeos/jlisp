/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (Cache.java) is part of jlisp.                                   *
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import bar.f0o.jlisp.lib.ControlPlane.EncapsulatedControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.IPv4Locator;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister.HmacType;
import bar.f0o.jlisp.lib.ControlPlane.MapReply;
import bar.f0o.jlisp.lib.ControlPlane.MapRequest;
import bar.f0o.jlisp.lib.ControlPlane.Rec;
import bar.f0o.jlisp.lib.ControlPlane.Record;
import bar.f0o.jlisp.JLISP;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

public class Cache {

	private ConcurrentHashMap<EidPrefix, CacheEntry> mappings = new ConcurrentHashMap<EidPrefix, CacheEntry>();
	private HashSet<byte[]> lockedEids = new HashSet<>();

	byte[] mappingSystemIP;

	private static Cache cache;

	public static Cache getCache() {
		if (cache == null)
			cache = new Cache();
		return cache;
	}

	private Cache() {
		this.mappingSystemIP = JLISP.getConfig().getMS();

	}

	public void parseRecords(ArrayList<Record> records) {
		// Parse each record
		for (Record record : records) {
			EidPrefix result = new EidPrefix(record.getEidPrefix(), record.getEidMaskLen());

			if (record.getLocatorCount() == 0) {
				addEntry(result, null);
				return;
			}
			// LCAF EXTRAIEREN
			CacheEntry resultEntry = new CacheEntry();
			for (Loc loc : record.getLocs()) {
				if (loc.getLocAFI() == ControlMessage.AfiType.IPv4)
					resultEntry.addV4Rloc(loc, record.getRecordTTL());
				else if (loc.getLocAFI() == ControlMessage.AfiType.IPv6)
					resultEntry.addV6Rloc(loc, record.getRecordTTL());
				else
					resultEntry.addLCAF(loc, record.getRecordTTL());

			}
			;
			addEntry(result, resultEntry);
		}
	}

	private void addEntry(EidPrefix prefix, CacheEntry entry) {
		;
		mappings.put(prefix, entry);
	}

	public synchronized byte[] getRLocForEid(byte[] eid) throws IOException {

		int longestPrefix = 0;
		CacheEntry mapping = null;
		;
		for (EidPrefix pre : mappings.keySet()) {
			if (pre.match(eid) && pre.getPrefixLength() > longestPrefix) {
				mapping = mappings.get(pre);
				longestPrefix = pre.getPrefixLength();
			}
		}
		if (mapping == null) {
			;
			if (!lockedEids.contains(eid)) {
				startMapRequest(eid, (byte) 0);
				lockedEids.add(eid);
			}
			return null;
		}
		lockedEids.remove(eid);
		if (JLISP.getConfig().isRTR()) {
			return mapping.getLCAFRloc();
		}
		// More to do for LCAF
		return mapping.getLCAFRloc();
		//return JLISP.getConfig().useV4() ? mapping.getFirstV4Rloc() : mapping.getFirstV6Rloc();
	}

	public void startMapRequest(byte[] eid, byte smrStaus) {
		new Thread(new MapRequester(mappingSystemIP, eid, mappings, (byte) 0)).start();
	}

	// Perform Map Request, no nonce yet
	class MapRequester implements Runnable {

		byte[] eidRequest;
		ConcurrentHashMap<EidPrefix, CacheEntry> mappingCache;
		byte[] mappingSystemIP;
		// 0 - -
		// 1 S -
		// 2 - s
		// 3 S s
		byte smrStatus;

		public MapRequester(byte[] mappingSystemIP, byte[] eidRequest,
				ConcurrentHashMap<EidPrefix, CacheEntry> mappingCache, byte smrStatus) {
			this.eidRequest = eidRequest;
			this.mappingCache = mappingCache;
			this.mappingSystemIP = mappingSystemIP;
			this.smrStatus = smrStatus;
		}

		@Override
		public void run() {
			;
			// Generate Message
			Rec r = new Rec((byte) (eidRequest.length * 8),
					eidRequest.length == 4 ? ControlMessage.AfiType.IPv4 : ControlMessage.AfiType.IPv6,
					this.eidRequest);
			ArrayList<Rec> recs = new ArrayList<Rec>();
			recs.add(r);
			HashMap<Short, byte[]> itrs = new HashMap<Short, byte[]>();

			itrs.put((short) 1, JLISP.getConfig().getRlocs()[0].getAddress());

			boolean SFlag = smrStatus % 2 == 1;
			boolean sFlag = smrStatus > 0 && smrStatus % 2 == 0;

			byte src[] = {};
			MapRequest req = new MapRequest(false, false, false, SFlag, false, sFlag, new Random().nextLong(),
					AfiType.NONE, src, itrs, recs, null);

			EncapsulatedControlMessage message = new EncapsulatedControlMessage(JLISP.getConfig().getRlocs()[0].getAddress(), eidRequest,
					(short) 60573, (short) 4342, req);

			byte[] ligBytes = message.toByteArray();

			// Send Message
			DatagramSocket sock;
			try {
				;
				DatagramPacket ligPacket = new DatagramPacket(ligBytes, ligBytes.length,
						InetAddress.getByAddress(JLISP.getConfig().getMS()), 4342);
				byte[] all = {0,0,0,0};
				sock = new DatagramSocket(60573,Inet4Address.getByAddress(all));
				
				sock.send(ligPacket);
				;
				byte[] answer = new byte[JLISP.getConfig().getMTU()];
				DatagramPacket ligAnswer = new DatagramPacket(answer, answer.length);
				sock.receive(ligAnswer);
				;
				sock.close();
				;
				byte[] answerRightSize = new byte[ligAnswer.getLength()];
				System.arraycopy(answer, 0, answerRightSize, 0, answerRightSize.length);
				DataInputStream answerStream = new DataInputStream(new ByteArrayInputStream(answerRightSize));

				MapReply rep = new MapReply(answerStream);
				ArrayList<Record> records = rep.getRecords();
				;
				Cache.getCache().parseRecords(records);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void garbateCollection() {
		for(EidPrefix prefix : mappings.keySet()){
			mappings.get(prefix).deleteExpired();
			if(!mappings.get(prefix).entriesLeft())
				mappings.remove(prefix);
		}
	}

}
