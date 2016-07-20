/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (IPPayload.java) is part of jlisp.                               *
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

package bar.f0o.jlisp.lib.Net;
/**
 *Payload of IP Packets 
 *
 */
public abstract class IPPayload {

	public static final int UDP=17;
	public static final byte ICMP = 0;

	/**
	 * 
	 * @return Raw data as byte Array
	 */
	public abstract byte[] toByteArray();

	/**
	 * 
	 * @return Lenght of the payload in octets
	 */
	public abstract int getLength();

	/**
	 * 
	 * @return Protocol from the IP Header field
	 */
	public abstract byte getProtocol();
	
}
