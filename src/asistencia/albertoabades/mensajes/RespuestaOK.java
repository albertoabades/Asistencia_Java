package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class RespuestaOK extends RespuestaResultado{
	
	public RespuestaOK(String message){
		super(ROK);
		this.message = message;
	}
	
	public RespuestaOK(DataInputStream dis){
		super(ROK);
		try{
			message = readString(dis);
		}catch(Exception e){
			throw new RuntimeException("ROK receive: " + e);
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
				throw new RuntimeException("ROK send: " + e);
			}
		}
	}

}
