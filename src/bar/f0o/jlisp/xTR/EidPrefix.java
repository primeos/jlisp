package bar.f0o.jlisp.xTR;

public class EidPrefix {

	private byte[] prefix;
	private int prefixLength;
	private long added;	
	
	public EidPrefix(byte[] prefix, int prefixLength) {
		super();
		this.prefix = prefix;
		this.prefixLength = prefixLength;
		this.added = System.currentTimeMillis();
	}

	public boolean match(byte[] eid){
		int actual = 0;
		while(prefixLength - actual >=8){
			if(prefix[actual/8] != eid[actual/8])
				return false;
			actual+=8;
		}
		byte prefixTmp = prefix[prefixLength/8];
		byte eidTmp = eid[prefixLength/8];
		int bytes = prefixLength -actual;

		for(int i=0;i<bytes;i++){
			if((prefixTmp&(0b10000000>>i)) != (eidTmp&(0b10000000>>i)))
				return false;
		}
		return true;
	}
	
	public int getPrefixLength(){
		return this.prefixLength;
	}
	
	
}
