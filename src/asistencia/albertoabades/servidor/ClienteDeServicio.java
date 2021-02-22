package asistencia.albertoabades.servidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface ClienteDeServicio {
	void serveClient(DataInputStream dis, DataOutputStream dos);
}
