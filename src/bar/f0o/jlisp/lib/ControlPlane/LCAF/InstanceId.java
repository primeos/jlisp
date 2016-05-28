package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

/*
 *  0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 2    | IID mask-len  |             4 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                         Instance ID                           |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |         Address  ...          |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class InstanceId implements LCAFType {

	private byte iidMaskLen;
	private short lengthValue;
	private int instanceId;
	private AfiType type;
	private byte[] address;

	public InstanceId(DataInputStream stream) throws IOException {
		iidMaskLen = stream.readByte();
		lengthValue = (short) (stream.readShort() - 4);
		instanceId = stream.readInt();
		type = AfiType.fromInt(stream.readShort());
		address = new byte[AfiType.length(type)];
		stream.read(address);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(iidMaskLen);
		stream.writeShort((short)(lengthValue+4));
		stream.writeInt(instanceId);
		stream.writeByte(type.getVal());
		stream.write(address);
		return byteStream.toByteArray();
	}

}
