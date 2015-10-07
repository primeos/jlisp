package bar.f0o.jlisp.xTR;

import java.util.HashMap;

public class Cache {
	
	private static synchronized boolean tryToLock(byte[] eid){
		return false;
	}
	
	public static byte[] getRLocForEid(byte[] eid){
		return null;
	}

}
