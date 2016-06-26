/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (NonceLocator.java) is part of jlisp.                               *
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
 * Nonce Locator Canonical Address Format:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 8    |     Rsvd2     |             4 + n             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Reserved    |                  Nonce                        |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              AFI = x          |         Address  ...          |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class NonceLocator implements LCAFType {

	private short len;
	private int nonce;
	private AfiType type;
	private byte[] locator;

	public NonceLocator(DataInputStream stream) throws IOException {
		stream.read();
		len = (short) (stream.readShort() - 4);
		nonce = stream.readInt();
		type = AfiType.fromInt(stream.readShort());
		locator = new byte[AfiType.length(type)];
		stream.read(locator);
	}

	public NonceLocator(int nonce, AfiType type, byte[] locator) {
		super();
		this.nonce = nonce;
		this.type = type;
		this.locator = locator;
		this.len = (short) (2 + locator.length);
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		stream.writeShort(len + 4);
		stream.writeInt(nonce);
		stream.writeShort(type.getVal());
		stream.write(locator);
		return byteStream.toByteArray();
	}
	
	public byte[] getRloc(){
		return null;
	}
}
