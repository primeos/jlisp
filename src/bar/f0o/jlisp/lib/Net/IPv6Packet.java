/**
 * 
 */
package bar.f0o.jlisp.lib.Net;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * @author schmidtm
 *
 */
public class IPv6Packet extends IPPacket {

	private IPv6Packet() {
    }

    public IPv6Packet(DataInputStream stream) throws IOException {
    	
    }
	
	public IPv6Packet(byte[] sourceAddress, byte[] destinationAddress) {
		
	}
	
	public IPv6Packet(byte[] packet) {
		
	}
	
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPayload(IPPayload payload) {
		// TODO Auto-generated method stub

	}

	public byte[] getSrcIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getDstIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setTTL(byte ttl) {
		// TODO Auto-generated method stub

	}

	public byte getToS() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setToS(byte tos) {
		// TODO Auto-generated method stub

	}

}
