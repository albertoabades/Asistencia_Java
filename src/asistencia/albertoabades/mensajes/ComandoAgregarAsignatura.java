package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoAgregarAsignatura extends Comandos{
	
	private static final int LENGTH = 5;
	private static final String STR_COMMAND = "ALTA_ASIGNATURA";
	
	private String dni;
	private int idCarrera;
	private int idAsignatura;
	
	public ComandoAgregarAsignatura(String dni, int idCarrera, int idAsignatura) {
		super(TAGREGARASIGNATURA);
		this.dni = dni;
		this.idCarrera = idCarrera;
		this.idAsignatura = idAsignatura;
	}

	public ComandoAgregarAsignatura(DataInputStream dis) {
		super(TAGREGARASIGNATURA);
		this.dni = readString(dis);
		byte[] idCarreraBytes = readBytes(dis);
		this.idCarrera = Packer.unpackInt(idCarreraBytes);
		byte[] idAsignaturaBytes = readBytes(dis);
		this.idAsignatura = Packer.unpackInt(idAsignaturaBytes);
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

	public int getIdAsignatura() {
		return idAsignatura;
	}

	public void setIdAsignatura(int idAsignatura) {
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
				byte[] idCarreraBytes = Packer.packInt(this.idCarrera);
				byte[] idAsignaturaBytes = Packer.packInt(this.idAsignatura);
				writeBytes(dos, dniBytes);
				writeBytes(dos, idCarreraBytes);
				writeBytes(dos, idAsignaturaBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TAGREGARASIGNATURA send: " + e);
			}
		}
	}

}