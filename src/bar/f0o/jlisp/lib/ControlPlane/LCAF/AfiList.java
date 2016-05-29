/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (AfiList.java) is part of jlisp.                                 *
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
import java.util.ArrayList;

import bar.f0o.jlisp.lib.ControlPlane.ControlMessage.AfiType;
import bar.f0o.jlisp.lib.ControlPlane.IPv4Locator;
import bar.f0o.jlisp.lib.ControlPlane.IPv6Locator;
import bar.f0o.jlisp.lib.ControlPlane.Locator;

/*
 *  Address Binding LISP Canonical Address Format Example:

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |           AFI = 16387         |     Rsvd1     |     Flags     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Type = 1    |     Rsvd2     |         2 + 4 + 2 + 16        |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |            AFI = 1            |       IPv4 Address ...        |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |     ...  IPv4 Address         |            AFI = 2            |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                          IPv6 Address ...                     |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

 */

public class AfiList implements LCAFType {

	private short len;
	private ArrayList<AfiType> locatorTypes = new ArrayList<>();
	private ArrayList<Locator> locators = new ArrayList<>();

	public AfiList(DataInputStream stream) throws IOException {
		stream.readByte();
		len = (stream.readShort());
		while (stream.available() > 0) {
			AfiType type = AfiType.fromInt(stream.readShort());
			locatorTypes.add(type);
			switch (type) {
			case IPv4:
				locators.add(new IPv4Locator(stream));
				break;
			case IPv6:
				locators.add(new IPv6Locator(stream));
				break;
			case LCAF:
				locators.add(new LCAFLocator(stream));
				break;
			default:
				break;
			}
		}
	}

	public AfiList(ArrayList<AfiType> locatorTypes, ArrayList<Locator> locators) {
		super();
		this.locatorTypes = locatorTypes;
		this.locators = locators;
		this.len = (short) (locatorTypes.size() * 2);
		for (AfiType t : locatorTypes)
			len += (short) (AfiType.length(t));
	}

	@Override
	public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(byteStream);
		stream.writeByte(0);
		for (int i = 0; i < locators.size(); i++) {
			stream.write(locatorTypes.get(i).getVal());
			stream.write(locators.get(i).toByteArray());
		}
		return byteStream.toByteArray();
	}

}
