/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (Locator.java) is part of jlisp.                                 *
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

import java.io.IOException;

import bar.f0o.jlisp.lib.ControlPlane.LCAF.ExplicitLocatorPath;

/**
 *Locators to be used in LISP Messages 
 */
public interface Locator {
	/**
	 * @return AfiType of the Message
	 */
    public ControlMessage.AfiType getType();

    /**
     * 
     * @return raw data ready to send
     * @throws IOException
     */
    public byte[] toByteArray() throws IOException;

    /**
     * 
     * @return pretty printed Version of the Locator
     */
    public String toString();
    
    /**
     * 
     * @return raw Locator data
     */
    public byte[] getRloc();

}
