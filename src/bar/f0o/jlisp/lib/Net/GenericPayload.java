/**
 * 
 */
package bar.f0o.jlisp.lib.Net;

/**
 * @author schmidtm
 *
 */
public class GenericPayload extends IPPayload {
	private byte[] payload;
	
	public GenericPayload(byte[] payload) {
		this.payload = payload;
	}

	public byte[] toByteArray() {
		return payload;
	}

	public int getLength() {
		return payload.length;
	}

	public byte getProtocol() {
		return 0;
	}

}
