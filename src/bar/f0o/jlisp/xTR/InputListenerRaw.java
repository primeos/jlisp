package bar.f0o.jlisp.xTR;

import java.io.IOException;
import java.net.DatagramSocket;

import bar.f0o.jlisp.lib.Net.CLibrary;

public class InputListenerRaw implements Runnable{

	private int fd;
	private DatagramSocket sender;
	
	public  InputListenerRaw() throws IOException {
		this.sender = new DatagramSocket();
		byte[] ifr = {108,105,115,112,48,0,0,0,0,0,0,0,0,0,0,0,1,16};
		this.fd = CLibrary.INSTANCE.open("/dev/net/tun", 2);
		Controller.setFd(fd);
		CLibrary.INSTANCE.ioctl(fd,((long)0x400454ca), ifr);
		Runtime.getRuntime().exec("ip a a "+Config.getIP() +" dev lisp0");
		Runtime.getRuntime().exec("ip l s dev lisp0 up");
		Runtime.getRuntime().exec("ip l s dev lisp0 mtu"+Config.getMTU());

	}
	
	
	@Override
	public void run() {
		while(true){
			byte[] incomming = new byte[Config.getMTU()];
			int length = CLibrary.INSTANCE.read(fd, incomming, incomming.length);
			Controller.addSendWorker(new ITRWorker(sender,incomming,length));
		}
	}

}
