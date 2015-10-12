/******************************************************************************
 * Copyright (c) 2015 by                                                      *
 * Andreas Stockmayer <stockmay@f0o.bar> and                                  *
 * Mark Schmidt <schmidtm@f0o.bar>                                            *
 * Andreas Srockmayer <stockmay@f0o.bar>                                      *
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
    private final static int XTR = 0;
    private final static int MS = 1;


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
                int component = Integer.parseInt(prop.getProperty("app.component", "0"));
                switch(component) {
                    case JLISP.XTR:
                        break;
                    case JLISP.MS:
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
