package bar.f0o.jlisp.xTR;

//Aussen -> Innen

/*
 An ETR is a router that accepts an IP
 packet where the destination address in the "outer" IP header is
 one of its own RLOCs.  The router strips the "outer" header and
 forwards the packet based on the next IP header found.  In
 general, an ETR receives LISP-encapsulated IP packets from the
 Internet on one side and sends decapsulated IP packets to site
 end-systems on the other side.  ETR functionality does not have to
 be limited to a router device.  A server host can be the endpoint
 of a LISP tunnel as well.
 */

public class ETRWorker {

}
