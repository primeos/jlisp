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
            else
                throw new RuntimeException("Illegal IP version number");
        }
}
