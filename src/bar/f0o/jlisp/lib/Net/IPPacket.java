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

public abstract class IPPacket {

	public abstract byte[] toByteArray();
	public abstract void addPayload(IPPayload payload);

        public abstract byte[] getSrcIP();
        public abstract byte[] getDstIP();

        public abstract byte getTTL();
        public abstract void setTTL(byte ttl);

        public abstract byte getToS();
        public abstract void setToS(byte tos);

        public static IPPacket fromByteArray(byte[] packet) throws RuntimeException {
            if (packet.length < 20) //Minimum packet size of IPv4 is 20 bytes header + 0 bytes payload
                throw new RuntimeException("Payload too short for IP");
            if ((packet[0] >> 4) == 4)
                return new IPv4Packet(packet);
            else if ((packet[0] >> 4) == 6)
                return new IPv6Packet(packet);
            else{
                throw new RuntimeException("Illegal IP version number: "+packet[0]);
            }
        }
}
