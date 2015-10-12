/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 *                                                                            *
 * This file (JLISP.java) is part of jlisp.                                   *
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

package bar.f0o.jlisp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class JLISP {
    private final static int MS = 0;
    private final static int XTR = 1;
    private final static int RTR = 2;
    private final static int PXTR = 3;
    private final static int NTR = 5;
    private final static int HAXTR = 7;
    private final static int HARTR = 8;

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("usage: java JLISP <config_path>");
            System.exit(-1);
        }
        else {
            Properties prop = new Properties();
            InputStream in = null;
            try {
                in = new FileInputStream(args[0]);
                prop.load(in);
                int component = Integer.parseInt(prop.getProperty("app.component", "1"));
                switch(component) {
                    case JLISP.MS:
                        break;
                    case JLISP.XTR:
                        break;
                    case JLISP.RTR:
                        break;
                    case JLISP.PXTR:
                        break;
                    case JLISP.NTR:
                        break;
                    case JLISP.HAXTR:
                        break;
                    case JLISP.HARTR:
                        break;
                    default:
                        System.err.println("no such component");
                        System.exit(-1);
                }
            } catch (FileNotFoundException e) {
                System.err.println("config file not found");
                System.exit(-1);
            } catch (IOException e) {
                System.err.println("cannot read config file");
                System.exit(-1);
            }
        }
    }
}
