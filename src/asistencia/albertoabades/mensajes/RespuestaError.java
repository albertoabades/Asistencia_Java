package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RespuestaError extends RespuestaResultado{
	
	public RespuestaError(String message){
		super(RERROR);
		this.message = message;
	}
	
	public RespuestaError(DataInputStream dis){
		super(RERROR);
		try{
			message = readString(dis);
		}catch(Exception e){
			throw new RuntimeException("RERROR receive: " + e);
		}
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos) {
			super.sendTo(dos);
			try {
				writeString(dos, message);
				dos.flush();
			} catch(Exception e) {
				throw new RuntimeException("RERROR send: " + e);
			}
		}
	}

}
