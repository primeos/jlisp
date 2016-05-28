/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (LCAFLocator.java) is part of jlisp.                             *
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

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage;
import bar.f0o.jlisp.lib.ControlPlane.Locator;
import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;

public class LCAFLocator implements Locator {

	private int lcafType = 0;
	private LCAFType type = null;

	public LCAFLocator(DataInputStream stream) throws IOException {
		// RSVD1
		stream.readByte();
		// FLG
		stream.readByte();

		this.lcafType = stream.readByte();
	}

	@Override
	public ControlMessage.AfiType getType() {
		return ControlMessage.AfiType.LCAF;
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeShort(16387);
		stream.writeInt(0);
		stream.writeByte(lcafType);
		if (this.lcafType != 0)
			stream.write(type.toByteArray());
		return byteStream.toByteArray();
	}
}
