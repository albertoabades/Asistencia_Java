package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoAgregarClase extends Comandos{
	
	private String dni;
	private int idMiCarrera;
	private long idMiAsignatura;
	
	public ComandoAgregarClase(String dni, int idMiCarrera, long idMiAsignatura){
		super(TAGREGARCLASE);
		this.dni = dni;
		this.idMiCarrera = idMiCarrera;
		this.idMiAsignatura = idMiAsignatura;
	}
	
	public ComandoAgregarClase(DataInputStream dis){
		super(TAGREGARCLASE);
		this.dni = readString(dis);
		byte[] idMiCarreraBytes = readBytes(dis);
		this.idMiCarrera = Packer.unpackInt(idMiCarreraBytes);
		byte[] idMiAsignaturaBytes = readBytes(dis);
		this.idMiAsignatura = Packer.unpackLong(idMiAsignaturaBytes);
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public int getIdMiCarrera() {
		return idMiCarrera;
	}

	public void setIdMiCarrera(int idMiCarrera) {
		this.idMiCarrera = idMiCarrera;
	}

	public long getIdMiAsignatura() {
		return idMiAsignatura;
	}

	public void setIdMiAsignatura(long idMiAsignatura) {
		this.idMiAsignatura = idMiAsignatura;
	}

	public void sendTo(DataOutputStream dos){
		synchronized(dos){
			super.sendTo(dos);
			try{
				byte[] dniBytes = this.dni.getBytes("UTF-8");
				byte[] idMiCarreraBytes = Packer.packInt(this.idMiCarrera);
				byte[] idMiAsignaturaBytes = Packer.packLong(this.idMiAsignatura);
				writeBytes(dos, dniBytes);
				writeBytes(dos, idMiCarreraBytes);
				writeBytes(dos, idMiAsignaturaBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TAGREGARCLASE send: " + e);
			}
		}
	}
}