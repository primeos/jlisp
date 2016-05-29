/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (SecurityKey.java) is part of jlisp.                             *
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
 *  Security Key Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 11   |      Rsvd2    |             6 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Key Count   |      Rsvd3    | Key Algorithm |   Rsvd4     |R|
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           Key Length          |       Key Material ...        |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                        ... Key Material                       |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |       Locator Address ...     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+


 */

public class SecurityKey implements LCAFType {

	private short len;
	private byte keyCount;
	private byte keyAlgorithm;
	private boolean rBit;
	private short keyLength;
	private byte[] keyMaterial;
	private AfiType type;
	private byte[] locatorAddress;

	public SecurityKey(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (short) (stream.readShort() - 6);
		keyCount = stream.readByte();
		stream.readByte();
		keyAlgorithm = stream.readByte();
		rBit = stream.readByte() != 0;
		keyLength = stream.readShort();
		keyMaterial = new byte[keyLength];
		stream.read(keyMaterial);
		type = AfiType.fromInt(stream.readShort());
		locatorAddress = new byte[AfiType.length(type)];
		stream.read(locatorAddress);
	}

	public SecurityKey(byte keyCount, byte keyAlgorithm, boolean rBit, short keyLength, byte[] keyMaterial,
			AfiType type, byte[] locatorAddress) {
		super();
		this.keyCount = keyCount;
		this.keyAlgorithm = keyAlgorithm;
		this.rBit = rBit;
		this.keyLength = keyLength;
		this.keyMaterial = keyMaterial;
		this.type = type;
		this.locatorAddress = locatorAddress;
		this.len = (short) (keyMaterial.length + 2 + locatorAddress.length);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 6);
		stream.writeByte(keyCount);
		stream.writeByte(0);
		stream.writeByte(keyAlgorithm);
		byte rsvd4 = (byte) (rBit ? 1 : 0);
		stream.writeByte(rsvd4);
		stream.writeShort(keyLength);
		stream.write(keyMaterial);
		stream.writeShort(type.getVal());
		stream.write(locatorAddress);
		return byteStream.toByteArray();
	}

}
