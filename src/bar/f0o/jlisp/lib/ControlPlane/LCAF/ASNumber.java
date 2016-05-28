package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

public class ASNumber implements LCAFType {

	private short length;
	private int asNumber;
	private AfiType type;
	private byte[] address;

	public ASNumber(DataInputStream stream) throws IOException {
		length = (short) (stream.readShort() - 4);
		asNumber = stream.readInt();
		type = AfiType.fromInt(stream.readShort());
		address = new byte[AfiType.length(type)];
		stream.read(address);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(asNumber+4);
		stream.writeInt(asNumber);
		stream.writeShort(type.getVal());
		stream.write(address);
		return byteStream.toByteArray();
	}

}
