package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoListarMisCarreras extends Comandos{
	
	private static final String STR_COMMAND = "LS_MIS_CARRERAS";
	private static final int LENGTH = 1;
	
	private String dni;

	public ComandoListarMisCarreras(String dni){
		super(TLISTARMISCARRERAS);
		this.dni = dni;
	}
	
	public ComandoListarMisCarreras(DataInputStream dis){
		super(TLISTARMISCARRERAS);
		this.dni = readString(dis); 
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}
	
	public static boolean comandoListarCarrerasCorrecto(String[] strTokens){
		return (strTokens.length == LENGTH) && (strTokens[COMMAND_INDEX].toUpperCase().equals(STR_COMMAND));
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos){
			super.sendTo(dos);
			try {
				byte[] dniBytes = this.dni.getBytes("UTF-8");
				writeBytes(dos, dniBytes);
				dos.flush();
			} catch (Exception e) {
				throw new RuntimeException("TLISTARMISCARRERAS send: " + e);
			}
		}
	}

}
