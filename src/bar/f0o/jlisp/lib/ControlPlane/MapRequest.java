/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (MapRequest.java) is part of jlisp.                              *
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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Map Request Message
 * <p/>
 * 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7 0 1 2 3 4 5 6 7
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |Type=1 |A|M|P|S|p|s|    Reserved     |   IRC   | Record Count  |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         Nonce . . .                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                         . . . Nonce                           |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         ITR-RLOC-AFI 1        |    ITR-RLOC Address 1  ...    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                              ...                              |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |         ITR-RLOC-AFI n        |    ITR-RLOC Address n  ...    |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * / |   Reserved    | EID mask-len  |        EID-Prefix-AFI         |
 * Rec +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * \ |                       EID-Prefix  ...                         |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |                   Map-Reply Record  ...                       |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 */
/**
 * Map Request as defined in RFC6830
 *
 */
public class MapRequest extends ControlMessage {

    /**
     * A: Authoritative bit: 0: UDP based Map Requests bei ITR 1: Destination site should return address
     * M: Map data present set if map reply is included
     * P: if set should be treated as probe with nonce copied from request
     * S: SMR bit
     * p: PITR bit 1 if sent by pitr
     * s: smr invoked 1 if answer to smr
     * IRC: Number of (ITR-RLOC-AFI / ITR-RLOC- Address) pairs -1 max 31
     * Record count: Number of records (only one allowed for sender)
     * Nonce: 64bit random
     * Source-EID-AFI:Address family of the Source EID
     * Source EID Address: Host that originated map request (refreshing or probing AFI 0 and this field length 0)
     * ITR-RLOC-AFI: Address family of ITR
     * ITR-RLOC Address: Give ETR option to choose between address families if more than one type given
     * EID mask len: mask length for the prefix
     * EID-prefix-AFI: prefix address family
     * EID-Prefix: the request prefix
     * Map-Reply: optional reply included in Request
     */

    private boolean aFlag, mFlag, pFlag, smrBit, pitrBit, smrInvoked;
    private byte irc, recordCount;
    private long    nonce;
    private AfiType sourceEidAfi;
    private byte[]  sourceEIDAddress;
    private Map<Short, byte[]> itrRlocPairs = new HashMap<>();
    private ArrayList<Rec>     recs         = new ArrayList<>();
    private MapReply reply;

    @SuppressWarnings("unused")
    private MapRequest() {
    }

    /**
     * 
     * @param stream Raw Byte stream containing the Map Request
     * @throws IOException
     */
    public MapRequest(DataInputStream stream) throws IOException {
    	this(stream,stream.readByte());
    }
    
    /**
     * 
     * @param stream Raw Byte stream containing the Map Request without the first byte
     * @param version The missing first byte
     * @throws IOException
     */
    public MapRequest(DataInputStream stream,byte version) throws IOException {
    	this.type = 1;
        byte flags = version;
        this.aFlag = (flags & 8) != 0;
        this.mFlag = (flags & 4) != 0;
        this.pFlag = (flags & 2) != 0;
        this.smrBit = (flags & 1) != 0;
        byte reserved = stream.readByte();
        this.pitrBit = (reserved & 128) != 0;
        this.smrInvoked = (reserved & 64) != 0;
        this.irc = stream.readByte();
        this.recordCount = stream.readByte();
        this.nonce = stream.readLong();
        this.sourceEidAfi = AfiType.fromInt(stream.readShort());
        byte buffer[];
        buffer = new byte[AfiType.length(this.sourceEidAfi)];
        stream.read(buffer);
        this.sourceEIDAddress = buffer;
        this.itrRlocPairs = new HashMap<>();
        for (int i = 0; i <= this.irc; i++) {
            short t = stream.readShort();
            buffer = new byte[AfiType.length(AfiType.fromInt(t))];
            stream.read(buffer);
            this.itrRlocPairs.put(t, buffer);
        }
        this.recs = new ArrayList<>();
        for (int i = 0; i < this.recordCount; i++)
            this.recs.add(new Rec(stream));
        if (this.mFlag)
            this.reply = new MapReply(stream);
    }

 
    
    /**
     * 
     * @param aFlag Authoritative bit
     * @param mFlag Map Data present flag
     * @param pFlag Probe bit
     * @param smrBit SMR Flag
     * @param pitrBit Sned by PITR bit
     * @param smrInvoked Answer to an SMR bit
     * @param nonce 64bit Nonce
     * @param sourceEidAfi AFI type of the source EID
     * @param sourceEIDAddress Raw source eid
     * @param itrRlocPairs Pairs of ITRs and RLOcs from the sender
     * @param recs Records asked for
     * @param reply Included Map Reply, may be null ich Map Data present flag is 0
     */
    public MapRequest(boolean aFlag, boolean mFlag, boolean pFlag, boolean smrBit, boolean pitrBit,
                      boolean smrInvoked, long nonce, AfiType sourceEidAfi, byte[] sourceEIDAddress,
                      Map<Short, byte[]> itrRlocPairs, ArrayList<Rec> recs, MapReply reply) {
        this.aFlag = aFlag;
        this.mFlag = mFlag;
        this.pFlag = pFlag;
        this.smrBit = smrBit;
        this.pitrBit = pitrBit;
        this.smrInvoked = smrInvoked;
        this.irc = (byte) (itrRlocPairs.size()-1);
        this.recordCount = (byte) recs.size();
        this.nonce = nonce;
        this.sourceEidAfi = sourceEidAfi;
        this.sourceEIDAddress = sourceEIDAddress;
        this.itrRlocPairs = itrRlocPairs;
        this.recs = recs;
        this.reply = reply;
    }

    /**
     * Raw version of the Map Request
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(byteStream);
        try {
            short flagsTypeTmp = 4096; //Type = 1
            if (this.aFlag)
                flagsTypeTmp |= 64;
            if (this.mFlag)
                flagsTypeTmp |= 128;
            if (this.pFlag)
                flagsTypeTmp |= 256;
            if (this.smrBit)
                flagsTypeTmp |= 512;
            if (this.pitrBit)
                flagsTypeTmp |= 1024;
            if (this.smrInvoked)
                flagsTypeTmp |= 2048;

            stream.writeShort(flagsTypeTmp);
            stream.writeByte(this.irc);
            stream.writeByte(this.recordCount);
            stream.writeLong(this.nonce);
            stream.writeShort(this.sourceEidAfi.getVal());
            stream.write(this.sourceEIDAddress);
            for (Map.Entry<Short, byte[]> entry : this.itrRlocPairs.entrySet()) {
                stream.writeShort(entry.getKey());
                stream.write(entry.getValue());
            }
            for (Rec rec : this.recs) {
                stream.write(rec.toByteArray());
            }
            if (reply != null)
                stream.write(this.reply.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteStream.toByteArray();
    }

    
    /**
     * Pretty printed Version of the Map Request Message
     */
    public String toString() {
        String rsvp = "";
        String res = ""; /* String.format("+-+\n" +
                                   "|%032d|\n" +
                                   "-+\n" +
                                   "|%08d|%08d%|\n" +
                                   "+-+\n" +
                                   "|%08d|%08d%|\n" +
                                   "+-+\n" +
                                   "|%s|\n", recordTTL, locatorCount, eidMaskLen, act, aFlag, rsvp, versionNumber,eidPrefixAfi , eidPrefix);
        for(Loc l : locs)
            res += locs.toString();   */
        return res;
    }

    //Getter
  
    /**
     * 
     * @return Authoritative bit
     */
    public boolean isaFlag() {
        return aFlag;
    }

    /**
     * 
     * @return Map data present
     */
    public boolean ismFlag() {
        return mFlag;
    }

    /**
     * 
     * @return Probe Request
     */
    public boolean ispFlag() {
        return pFlag;
    }

    /**
     * 
     * @return SMR Message?
     */
    public boolean isSmrBit() {
        return smrBit;
    }

    /**
     * 
     * @return Is message from PITR
     */
    public boolean isPitrBit() {
        return pitrBit;
    }

    /**
     * 
     * @return Is message answer to SMR
     */
    public boolean isSmrInvoked() {
        return smrInvoked;
    }

    /**
     * 
     * @return Number of ITR/RLOC pairs
     */
    public byte getIrc() {
        return irc;
    }

    /**
     * 
     * @return Number of records in the Map Request
     */
    public byte getRecordCount() {
        return recordCount;
    }

    /**
     * 
     * @return 64bit Nonce
     */
    public long getNonce() {
        return nonce;
    }

    /**
     * 
     * @return AfiType of the Source EID
     */
    public AfiType getSourceEidAfi() {
        return sourceEidAfi;
    }

    /**
     * 
     * @return Raw Source EID
     */
    public byte[] getSourceEIDAddress() {
        return sourceEIDAddress;
    }

    /**
     * 
     * @return Pairs ITR/RLOC
     */
    public Map<Short, byte[]> getItrRlocPairs() {
        return itrRlocPairs;
    }

    /**
     * 
     * @return Records asked for in the Map Request
     */
    public ArrayList<Rec> getRecs() {
        return recs;
    }

    /**
     * 
     * @return Map Reply present in the Request if M Flag set, null otherwise
     */
    public MapReply getReply() {
        return reply;
    }


}