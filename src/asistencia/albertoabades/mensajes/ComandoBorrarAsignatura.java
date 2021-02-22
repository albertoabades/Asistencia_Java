package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoBorrarAsignatura extends Comandos{
	
	private static final int LENGTH = 5;
	private static final String STR_COMMAND = "BAJA_ASIGNATURA";
	
	private String dni;
	private long idAsignatura;

	public ComandoBorrarAsignatura(String dni, long idAsignatura) {
		super(TBORRARASIGNATURA);
		this.dni = dni;
		this.idAsignatura = idAsignatura;
	}
	
	public ComandoBorrarAsignatura(DataInputStream dis) {
		super(TBORRARASIGNATURA);
		this.dni = readString(dis);
		byte[] idAsignaturaBytes = readBytes(dis);
		this.idAsignatura = Packer.unpackLong(idAsignaturaBytes);
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public long getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(long idAsignatura) {
		this.idAsignatura = idAsignatura;
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
				byte[] idAsignaturaBytes = Packer.packLong(this.idAsignatura);
				writeBytes(dos, dniBytes);
				writeBytes(dos, idAsignaturaBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TAGREGARASIGNATURA send: " + e);
			}
		}
	}

}
