package bar.f0o.jlisp.mappingsystem;

import java.util.ArrayList;
import java.util.HashMap;

import bar.f0o.jlisp.lib.ControlPlane.MapReply;
import bar.f0o.jlisp.lib.ControlPlane.MapRequest;
import bar.f0o.jlisp.lib.ControlPlane.Record;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.MapRegister;
import bar.f0o.jlisp.xTR.CacheEntry;
import bar.f0o.jlisp.xTR.EidPrefix;


public class Mappings {

	HashMap<EidPrefix,Mapping> mappings = new HashMap<>();
	
	public void addMapping(MapRegister reg){
		for(Record rec : reg.getRecords()){
			EidPrefix pre = mappingExists(rec.getEidPrefix(),rec.getEidMaskLen());
			if(pre == null)
			{
				pre = new EidPrefix(rec.getEidPrefix(), rec.getEidMaskLen());
				mappings.put(pre, new Mapping());
			}
			mappings.get(pre).addMapping(rec, reg.ispFlag());
		}
	}
	
	public MapReply getReply(byte[] eid,long nonce){
		EidPrefix pre = getMatch(eid);
		if(pre == null)
			return negativeMapReply(eid,nonce);
		MapReply reply = new MapReply(true,false,false,nonce,mappings.get(pre).getRecordsForEID());
		return reply;
	}
	
	private MapReply negativeMapReply(byte[] eid, long nonce) {
		ArrayList<Loc> locs = new ArrayList<>();
		Record rec = new Record(0, (byte)0, (byte)0, false,(short) 0, ((eid.length==4)?AfiType.IPv4:AfiType.IPv6), eid,locs);
		ArrayList<Record> records = new ArrayList<>();
		records.add(rec);
		MapReply reply = new MapReply(true, false, false, nonce, records);
		return reply;
	}

	//Rloc if proxy byte, null otherwise
	public byte[] getProxy(byte[] eid){
		EidPrefix pre = getMatch(eid);
		return pre==null?null:mappings.get(pre).isProxy()?mappings.get(pre).getRloc():null;
	}
	
	
	private EidPrefix getMatch(byte[] eid){
		int longestPrefix = 0;
		EidPrefix mapping = null;

		for (EidPrefix pre : mappings.keySet()) {
			if (pre.match(eid) && pre.getPrefixLength() > longestPrefix) {
				mapping = pre;
				longestPrefix = pre.getPrefixLength();
			}
		}
		
		return mapping;
	}

	private EidPrefix mappingExists(byte[] eidPrefix, int prefixLength){
		for(EidPrefix pre : mappings.keySet())
		{
			if(pre.getPrefixLength() == prefixLength && pre.match(eidPrefix))
				return pre;
		}
		return null;
	}
}


	