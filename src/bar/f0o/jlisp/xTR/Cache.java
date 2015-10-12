package bar.f0o.jlisp.xTR;

import java.net.DatagramSocket;
import java.util.HashMap;

public class Cache {
	
	private HashMap<byte[],byte[]> mappings = new HashMap<byte[],byte[]>();
	private DatagramSocket requestSocket;
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
		byte[] rloc = {(byte)134,2,11,(byte)145};
		return rloc;
	}
}
