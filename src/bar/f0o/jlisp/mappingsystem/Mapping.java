package bar.f0o.jlisp.mappingsystem;

import java.util.ArrayList;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.lib.ControlPlane.Loc;
import bar.f0o.jlisp.lib.ControlPlane.Record;

//Mapping needs to store Locs instead of Records because two or more registers can occur for one eid

public class Mapping {

	private ArrayList<Loc> locs = new ArrayList<>();
	private byte[] rloc;
	private AfiType type;
	
	
	public Mapping(Record r){
		
	}
	
	public Record getRecordForEID(){
		return null;
	}
	
}
