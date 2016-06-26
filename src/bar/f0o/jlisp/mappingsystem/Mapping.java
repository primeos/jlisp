package bar.f0o.jlisp.mappingsystem;

import java.util.ArrayList;
import java.util.Random;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.xTR.LocTTLContainer;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.Record;

//Mapping needs to store Locs instead of Records because two or more registers can occur for one eid
//Record TTL

public class Mapping {

	private ArrayList<Record> records = new ArrayList<>();
	private boolean proxyReply = false;
	private int recordTTL = 0;

	private static Random rand = new Random();

	
	public void addMapping(Record r, boolean proxy){
		this.recordTTL = r.getRecordTTL();
		this.proxyReply = proxy;
		this.records.add(r);
	}

	public ArrayList<Record> getRecordsForEID() {
		return records;
	}
	
	public int getRecordTTL(){
		return recordTTL;
	}

	public boolean isProxy() {
		return proxyReply;
	}

	public byte[] getRloc() {
		Loc[] locsTmp = new Loc[records.get(0).getLocs().size()];
		int numOfLocs = 0;
		int minPrio = 254;
		for (Loc loc : records.get(0).getLocs()) {
			if (loc.getPriority() < minPrio) {
				numOfLocs = 1;
				minPrio = loc.getmPriority();
				locsTmp[0] = loc;
			} else if (loc.getmPriority() == minPrio) {
				locsTmp[numOfLocs] = loc;
				numOfLocs++;
			}
		}

		int weights = 0;
		for (int i = 0; i < numOfLocs; i++)
			weights += locsTmp[i].getWeight();
		int targetWeight = rand.nextInt(weights);
		for (int i = 0; i < numOfLocs; i++) {
			if (locsTmp[i].getWeight() >= targetWeight)
				return locsTmp[i].getLocator().getRloc();
			targetWeight -= locsTmp[i].getWeight();
		}
		return null;
	}

}
