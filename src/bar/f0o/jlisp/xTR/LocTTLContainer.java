package bar.f0o.jlisp.xTR;

import bar.f0o.jlisp.lib.ControlPlane.Loc;

public class LocTTLContainer {

	private Loc locator;
	private long ttl;
	
	public Loc getLocator() {
		return locator;
	}

	
	public LocTTLContainer(Loc locator, long ttl) {
		super();
		this.locator = locator;
		this.ttl = ttl;
	}
	
	public boolean expired(){
		return ttl < System.currentTimeMillis();
	}

	
}
