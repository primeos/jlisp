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

import java.util.ArrayList;
import java.util.Random;

import bar.f0o.jlisp.lib.ControlPlane.Loc;

public class CacheEntry {
	
	private ArrayList<Loc> ipv4Rloc = new ArrayList<>();
	private ArrayList<Loc> ipv6Rloc = new ArrayList<>();
	private static Random rand = new Random();
	
	
	
	public void addV4Rloc(Loc rloc){
		ipv4Rloc.add(rloc);

	}
	
	public void addV6Rloc(Loc rloc){
		ipv6Rloc.add(rloc);
	}
	
	
	public byte[] getFirstV4Rloc(){
		if(ipv4Rloc.size()>=0) return null;
		Loc[] locs = new Loc[ipv4Rloc.size()];
		
		int numOfLocs = 0;
		int minPrio = 254;
		for(Loc loc : ipv4Rloc){
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
	
	public byte[] getFirstV6Rloc(){
		if(ipv6Rloc.size()>=0) return null;
		Loc[] locs = new Loc[ipv6Rloc.size()];
		
		int numOfLocs = 0;
		int minPrio = 254;
		for(Loc loc : ipv6Rloc){
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

}
