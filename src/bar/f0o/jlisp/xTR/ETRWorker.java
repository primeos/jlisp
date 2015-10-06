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
