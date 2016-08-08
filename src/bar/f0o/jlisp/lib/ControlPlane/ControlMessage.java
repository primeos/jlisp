/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (ControlMessage.java) is part of jlisp.                          *
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

package bar.f0o.jlisp.lib.ControlPlane;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class ControlMessage {
	
	protected byte type;
	
	/**
	 * AFI Types for usage in various packet formats
	 */
    //AFI Type
    public enum AfiType {
        NONE(0),
        IPv4(1),
        IPv6(2),
        LCAF(16387);

        private final int val;

        /**
         * 
         * @param x AFI type as defined by IANA
         */
        private AfiType(int x) {
            this.val = x;
        }

        /**
         * 
         * @return AFI type as defined by IANA
         */
        public int getVal() {
            return val;
        }

        /**
         * 
         * @param x AFI Type as defined by IANA
         * @return
         */
        public static AfiType fromInt(int x) {
            switch (x) {
                case 0:
                    return NONE;
                case 1:
                    return IPv4;
                case 2:
                    return IPv6;
                case 16387:
                    return LCAF;

            }
            return null;
        }


        /**
         * 
         * @param a AFI Type object
         * @return length in octets
         */
        //Get number of octets
        public static int length(AfiType a) {
            switch (a) {
                case NONE:
                    return 0;
                case IPv4:
                    return 4;
                case IPv6:
                    return 16;
                case LCAF:
                    return 16387;
            }
            return -1;
        }

        /**
         * 
         * @param a AFI Type as defined by IANA
         * @return length in octets
         */
        public static int length(int a) {
            return length(fromInt(a));
        }
    }

    /**
     * General constructor for control messages
     * @param stream Stream of raw byte data
     * @return Object of corresponding control message
     * @throws IOException
     */
    public static ControlMessage fromStream(DataInputStream stream) throws IOException{
    	byte version = stream.readByte();
    	int type = ((version >> 4) & 0xF);
    	switch(type){
    	case 1:
    		return new MapRequest(stream,version);
    	case 2:
    		return new MapReply(stream,version);
    	case 3: 
    		return new MapRegister(stream,version);
    	case 8:
    		return new EncapsulatedControlMessage(stream,version);
    	}
    	return null;
    }

    /**
     * 
     * @return Control message as byte Array ready to send
     */
    public abstract byte[] toByteArray();
    
    /**
     * 
     * @return Type of the Message as defined in RFC6830 and LCAF-ID
     */
    public  byte getType() {
        return type;
    }
}
