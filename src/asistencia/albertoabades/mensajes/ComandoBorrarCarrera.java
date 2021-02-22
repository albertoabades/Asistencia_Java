package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoBorrarCarrera extends Comandos{

	private static final int LENGTH = 5;
	private static final String STR_COMMAND = "BAJA_CARRERA";
	
	private String dni;
	private int idCarrera;
	
	public ComandoBorrarCarrera(String dni, int idCarrera) {
		super(TBORRARCARRERA);
		this.dni = dni;
		this.idCarrera = idCarrera;
	}
	
	public ComandoBorrarCarrera(DataInputStream dis) {
		super(TBORRARCARRERA);
		this.dni = readString(dis);
		byte[] idCarreraBytes = readBytes(dis);
		this.idCarrera = Packer.unpackInt(idCarreraBytes);
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public int getIdCarrera() {
		return idCarrera;
	}

	public void setIdCarrera(int idCarrera) {
		this.idCarrera = idCarrera;
	}
	
	public static boolean comandoListarAsignaturasCorrecto(String[] strTokens){
		return (strTokens.length == LENGTH) && (strTokens[COMMAND_INDEX].toUpperCase().equals(STR_COMMAND));
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos){
			super.sendTo(dos);
			try{
				byte[] dniBytes = this.dni.getBytes("UTF-8");
				byte[] idCarreraBytes = Packer.packInt(this.idCarrera);
				writeBytes(dos, dniBytes);
				writeBytes(dos, idCarreraBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TBORRARCARRERA send: " + e);
			}
		}
	}

}
