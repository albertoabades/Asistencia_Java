package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoCrearClase extends Comandos{
	
	//private String dni;
	private String horario;
	//private int idMiCarrera;
	private long idMiAsignatura;
	
	public ComandoCrearClase(
			//String dni, 
			String horario, 
			//int idMiCarrera, 
			long idMiAsignatura
			){
		super(TCREARCLASE);
		//this.dni = dni;
		this.horario = horario;
		//this.idMiCarrera = idMiCarrera;
		this.idMiAsignatura = idMiAsignatura;
	}
	
	public ComandoCrearClase(DataInputStream dis){
		super(TCREARCLASE);
		//this.dni = readString(dis);
		this.horario = readString(dis);
		//byte[] idMiCarreraBytes = readBytes(dis);
		//this.idMiCarrera = Packer.unpackInt(idMiCarreraBytes);
		byte[] idMiAsignaturaBytes = readBytes(dis);
		this.idMiAsignatura = Packer.unpackLong(idMiAsignaturaBytes);
	}

	/*
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}
	*/
	
	public String getHorario() {
		return horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	/*
	public int getIdMiCarrera() {
		return idMiCarrera;
	}

	public void setIdMiCarrera(int idMiCarrera) {
		this.idMiCarrera = idMiCarrera;
	}
	*/

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
				//byte[] dniBytes = this.dni.getBytes("UTF-8");
				byte[] horarioBytes = this.horario.getBytes("UTF-8");
				//byte[] idMiCarreraBytes = Packer.packInt(this.idMiCarrera);
				byte[] idMiAsignaturaBytes = Packer.packLong(this.idMiAsignatura);
				//writeBytes(dos, dniBytes);
				writeBytes(dos, horarioBytes);
				//writeBytes(dos, idMiCarreraBytes);
				writeBytes(dos, idMiAsignaturaBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TCREARCLASE send: " + e);
			}
		}
	}

}
