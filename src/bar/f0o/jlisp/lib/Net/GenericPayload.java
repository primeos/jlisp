/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ControlMessage.java) is part of JLISP.                          *
 *                                                                            *
 * JLISP is free software: you can redistribute it and/or modify              *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * JLISP is distributed in the hope that it will be useful,                   *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with JLISP.  If not, see <http://www.gnu.org/licenses/>.             *
 ******************************************************************************/

package bar.f0o.jlisp.lib.Net;

import java.io.DataInputStream;
import java.io.IOException;


public class GenericPayload extends IPPayload{
	private byte[] payload;
	
	public GenericPayload(DataInputStream stream){
		payload = new byte[1];
		try {
			stream.readFully(payload);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
