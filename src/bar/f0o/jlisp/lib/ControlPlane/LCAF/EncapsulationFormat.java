/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (EncapsulationFormat.java) is part of jlisp.                     *
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
 * Encapsulation Format Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 16   |     Rsvd2     |             4 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |        Reserved-for-Future-Encapsulations       |U|G|N|v|V|l|L|
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |          Address ...          |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class EncapsulationFormat implements LCAFType {

	private short len;
	private boolean l3Bit, l2Bit, vxlanBit, vxlanGpeBit, nvGreBit, geneveBit, gueBit;
	private AfiType type;
	private byte[] address;

	public EncapsulationFormat(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short) (stream.readShort() - 4);
		int enc = stream.readInt();
		l3Bit = (enc & 1) != 0;
		l2Bit = (enc & 2) != 0;
		vxlanBit = (enc & 4) != 0;
		vxlanGpeBit = (enc & 8) != 0;
		nvGreBit = (enc & 16) != 0;
		geneveBit = (enc & 32) != 0;
		gueBit = (enc & 64) != 0;
		type = AfiType.fromInt(stream.readShort());
		address = new byte[AfiType.length(type)];
		stream.read(address);
	}

	public EncapsulationFormat(boolean l3Bit, boolean l2Bit, boolean vxlanBit, boolean vxlanGpeBit, boolean nvGreBit,
			boolean geneveBit, boolean gueBit, AfiType type, byte[] address) {
		super();
		this.l3Bit = l3Bit;
		this.l2Bit = l2Bit;
		this.vxlanBit = vxlanBit;
		this.vxlanGpeBit = vxlanGpeBit;
		this.nvGreBit = nvGreBit;
		this.geneveBit = geneveBit;
		this.gueBit = gueBit;
		this.type = type;
		this.address = address;
		this.len = (short) (2 + address.length);
	}

	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 4);
		int enc = 0;
		enc += l3Bit ? 1 : 0;
		enc += l2Bit ? 2 : 0;
		enc += vxlanBit ? 4 : 0;
		enc += vxlanGpeBit ? 8 : 0;
		enc += nvGreBit ? 16 : 0;
		enc += geneveBit ? 32 : 0;
		enc += gueBit ? 64 : 0;
		stream.writeInt(enc);
		stream.write(type.getVal());
		stream.write(address);
		return byteStream.toByteArray();
	}

}
