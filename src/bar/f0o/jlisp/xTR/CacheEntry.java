/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (CacheEntry.java) is part of jlisp.                              *
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
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.Locator;
import bar.f0o.jlisp.lib.ControlPlane.LCAF.ExplicitLocatorPath;
import bar.f0o.jlisp.lib.ControlPlane.LCAF.LCAFLocator;
import bar.f0o.jlisp.lib.ControlPlane.LCAF.NATTraversal;

public class CacheEntry {
	
	private ArrayList<LocTTLContainer> ipv4Rloc = new ArrayList<>();
	private ArrayList<LocTTLContainer> ipv6Rloc = new ArrayList<>();
	private ArrayList<LocTTLContainer> lcafRloc = new ArrayList<>();
	private static Random rand = new Random();
	
	
	
	public void addV4Rloc(Loc rloc, int ttl){
		ipv4Rloc.add(new LocTTLContainer(rloc, (ttl*60*1000)+System.currentTimeMillis()));

	}
	
	public void addV6Rloc(Loc rloc, int ttl){
		ipv6Rloc.add(new LocTTLContainer(rloc, (ttl*60*1000)+System.currentTimeMillis()));
	}
	
	public void addLCAF(Loc rloc, int ttl) {
		lcafRloc.add(new LocTTLContainer(rloc, (ttl*60*1000)+System.currentTimeMillis()));
	}
	
	public byte[] getFirstV4Rloc() throws IOException{
		if(ipv4Rloc.size()<=0) return null;
		Loc[] locs = new Loc[ipv4Rloc.size()];

		int numOfLocs = 0;
		int minPrio = 254;
		for(LocTTLContainer locC : ipv4Rloc){
			Loc loc = locC.getLocator();
			if(loc.getPriority() < minPrio){
				numOfLocs = 1;
				minPrio = loc.getmPriority();
				locs[0] = loc;
			}else if(loc.getmPriority() == minPrio){
				locs[numOfLocs] = loc;
				numOfLocs++;
			}
		}
		
		
		int weights = 0;
		for(int i=0;i<numOfLocs;i++) weights+=locs[i].getWeight();
		int targetWeight = rand.nextInt(weights);
		for(int i=0;i<numOfLocs;i++){ 
			if(locs[i].getWeight() >= targetWeight) 
				return locs[i].getLocator().toByteArray();
			targetWeight -= locs[i].getWeight();
		}
		return null;
		
	}
	
	public byte[] getFirstV6Rloc() throws IOException{
		if(ipv6Rloc.size()<=0) return null;
		Loc[] locs = new Loc[ipv6Rloc.size()];
		
		int numOfLocs = 0;
		int minPrio = 254;
		for(LocTTLContainer locC : ipv6Rloc){
			Loc loc = locC.getLocator();	
			if(loc.getPriority() < minPrio){
				numOfLocs = 1;
				minPrio = loc.getmPriority();
				locs[0] = loc;
			}else if(loc.getmPriority() == minPrio){
				locs[numOfLocs] = loc;
				numOfLocs++;
			}
		}
		
		int weights = 0;
		int targetWeight = rand.nextInt(255);
		for(int i=0;i<numOfLocs;i++){ 
			if(locs[i].getWeight() < targetWeight - weights) 
				return locs[i].getLocator().toByteArray();
			weights += locs[i].getWeight();
		}
		return null;
	}
	
	
	/*
	 * 
	 Type 0:  Null Body Type				-
     Type 1:  AFI List Type					
     Type 2:  Instance ID Type				"instanceid"
     Type 3:  AS Number Type
     Type 4:  Application Data Type
     Type 5:  Geo Coordinates Type			"north,east,lat,latMin,latSec,lon,lonMin,lonSec"
     Type 6:  Opaque Key Type
     Type 7:  NAT-Traversal Type			"ntrNr"		
     Type 8:  Nonce Locator Type			
     Type 9:  Multicast Info Type	
     Type 10:  Explicit Locator Path Type	"ownRloc"
	 Type 11:  Security Key Type	
     Type 12:  Source/Dest Key Type
     Type 13:  Replication List Entry Type
     Type 14:  JSON Data Model Type
     Type 15:  Key/Value Address Pair Type
     Type 16:  Encapsulation Format Type
	 */
	public byte[] getLCAFRloc() throws IOException{
		;
		ArrayList<Loc> possible = new ArrayList<>();
		LCAFLocator chosen = null;
		int type = 10;
		
		for(LocTTLContainer loc : lcafRloc){
			Loc locator = loc.getLocator();
				if(((LCAFLocator)(locator.getLocator())).getLCAFType() == type)
				possible.add(locator);
		}

		;
		if(possible.size()<=0) return null;
		Loc[] locs = new Loc[possible.size()];
		
		int numOfLocs = 0;
		int minPrio = 254;
		for(Loc loc : possible){
			if(loc.getPriority() < minPrio){
				numOfLocs = 1;
				minPrio = loc.getmPriority();
				locs[0] = loc;
			}else if(loc.getmPriority() == minPrio){
				locs[numOfLocs] = loc;
				numOfLocs++;
			}
		}
		
		int weights = 0;
		int targetWeight = rand.nextInt(255);
		for(int i=0;i<numOfLocs;i++){ 
			if(locs[i].getWeight() < targetWeight - weights) 
				chosen = (LCAFLocator)locs[i].getLocator();
			weights += locs[i].getWeight();
		}
		if(chosen == null && numOfLocs > 0)
			chosen = (LCAFLocator)locs[0].getLocator();
		;
		return chosen==null?null:chosen.getRloc();	
	}	
	

	
	public void deleteExpired(){
		for(LocTTLContainer loc : ipv4Rloc){
			if(loc.expired())
				ipv4Rloc.remove(loc);
		}
		for(LocTTLContainer loc : ipv6Rloc){
			if(loc.expired())
				ipv6Rloc.remove(loc);
		}
		for(LocTTLContainer loc : lcafRloc){
			if(loc.expired())
				lcafRloc.remove(loc);
		}

	}
	
	public boolean entriesLeft(){
		return ipv4Rloc.size()+ipv6Rloc.size()+lcafRloc.size() > 0;
	}


}
