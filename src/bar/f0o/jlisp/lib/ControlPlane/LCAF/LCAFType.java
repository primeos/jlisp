package bar.f0o.jlisp.lib.ControlPlane.LCAF;

import java.io.IOException;

public interface LCAFType {

	public byte[] toByteArray() throws IOException;

    public String toString();

}
