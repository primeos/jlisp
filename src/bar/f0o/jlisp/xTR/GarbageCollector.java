package bar.f0o.jlisp.xTR;

public class GarbageCollector implements Runnable {

	public GarbageCollector(){
		
	}
	
	@Override
	public void run() {
		while(true){
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Cache.getCache().garbateCollection();
		}
	}

}
