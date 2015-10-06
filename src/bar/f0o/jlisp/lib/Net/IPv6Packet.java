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

public class IPv6Packet extends IPPacket {

	private IPv6Packet() {
    }

    public IPv6Packet(DataInputStream stream) throws IOException {
    	
    }
	
	public IPv6Packet(byte[] sourceAddress, byte[] destinationAddress) {
		
	}
	
	public IPv6Packet(byte[] packet) {
		
	}
	
	public byte[] toByteArray() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addPayload(IPPayload payload) {
		// TODO Auto-generated method stub

	}

	public byte[] getSrcIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getDstIP() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte getTTL() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setTTL(byte ttl) {
		// TODO Auto-generated method stub

	}

	public byte getToS() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setToS(byte tos) {
		// TODO Auto-generated method stub

	}

}
