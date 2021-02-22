package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoPing extends Comandos{
	
	private static final int LENGTH = 2;
	private static final String STR_COMMAND = "PING";
	
	private String nombre;
	
	public ComandoPing(String nombre){
		super(TPING);
		this.nombre = nombre;
	}
	
	public ComandoPing(DataInputStream dis){
		super(TPING);
		try{
			this.nombre = readString(dis);
		}catch(Exception e){
			throw new RuntimeException("TPING receive: " + e);
		}
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String toString(){
		return super.toString();
	}
	
	public static boolean correctPingCommand(String[] strTokens){
		return (strTokens.length == LENGTH) && (strTokens[COMMAND_INDEX].toUpperCase().equals(STR_COMMAND));
	}
	
	public void sendTo(DataOutputStream dos){
		synchronized(dos){
			super.sendTo(dos);
			try{
				byte[] nombreBytes = this.nombre.getBytes("UTF-8");
				writeBytes(dos, nombreBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TPING send: " + e);
			}
		}
	}

}
