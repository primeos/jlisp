package bar.f0o.jlisp.xTR;

//Innen -> Aussen

/*
 An ITR is a router that resides in a
 LISP site.  Packets sent by sources inside of the LISP site to
 destinations outside of the site are candidates for encapsulation
 by the ITR.  The ITR treats the IP destination address as an EID
 and performs an EID-to-RLOC mapping lookup.  The router then
 prepends an "outer" IP header with one of its globally routable
 RLOCs in the source address field and the result of the mapping
 lookup in the destination address field.  Note that this
 destination RLOC MAY be an intermediate, proxy device that has
 better knowledge of the EID-to-RLOC mapping closer to the
 destination EID.  In general, an ITR receives IP packets from site
 end-systems on one side and sends LISP-encapsulated IP packets
 toward the Internet on the other side.
 */

public class ITRWorker {

}
