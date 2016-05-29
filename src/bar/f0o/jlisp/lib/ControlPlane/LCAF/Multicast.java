/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (Multicast.java) is part of jlisp.                               *
 *                                                                            *
 * jlisp is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * jlisp is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the                *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with $project.name.If not, see <http://www.gnu.org/licenses/>.       *
 ******************************************************************************/
package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

/*
 * Multicast Info Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 9    |     Rsvd2     |             8 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                         Instance-ID                           |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |            Reserved           | Source MaskLen| Group MaskLen |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |   Source/Subnet Address  ...  |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |       Group Address  ...      |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class Multicast implements LCAFType {

	private short len;
	private int instanceId;
	private byte sourceMaskLen;
	private byte groupMaskLen;
	private AfiType sourceSubnetAddressAfi;
	private byte[] sourceSubnetAddress;
	private AfiType groupAddressAfi;
	private byte[] groupAddress;

	public Multicast(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short) (stream.readShort() - 8);
		instanceId = stream.readInt();
		stream.readShort();
		sourceMaskLen = stream.readByte();
		groupMaskLen = stream.readByte();
		sourceSubnetAddressAfi = AfiType.fromInt(stream.readShort());
		sourceSubnetAddress = new byte[AfiType.length(sourceSubnetAddressAfi)];
		stream.read(sourceSubnetAddress);
		groupAddressAfi = AfiType.fromInt(stream.readShort());
		groupAddress = new byte[AfiType.length(groupAddressAfi)];
		stream.read(groupAddress);
	}

	public Multicast(int instanceId, byte sourceMaskLen, byte groupMaskLen, AfiType sourceSubnetAddressAfi,
			byte[] sourceSubnetAddress, AfiType groupAddressAfi, byte[] groupAddress) {
		super();
		this.instanceId = instanceId;
		this.sourceMaskLen = sourceMaskLen;
		this.groupMaskLen = groupMaskLen;
		this.sourceSubnetAddressAfi = sourceSubnetAddressAfi;
		this.sourceSubnetAddress = sourceSubnetAddress;
		this.groupAddressAfi = groupAddressAfi;
		this.groupAddress = groupAddress;
		this.len = (short) (12 + sourceSubnetAddress.length + groupAddress.length);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 8);
		stream.writeInt(instanceId);
		stream.writeShort(0);
		stream.writeByte(sourceMaskLen);
		stream.writeByte(groupMaskLen);
		stream.writeShort(sourceSubnetAddressAfi.getVal());
		stream.write(sourceSubnetAddress);
		stream.writeShort(groupAddressAfi.getVal());
		stream.write(groupAddress);
		return byteStream.toByteArray();
	}

}
