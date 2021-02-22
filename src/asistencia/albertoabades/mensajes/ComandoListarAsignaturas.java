package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoListarAsignaturas extends Comandos{
	
	private static final String STR_COMMAND = "LS_ASIGNATURAS";
	private static final int LENGTH = 2;
	
	private long idCarrera;
	
	public ComandoListarAsignaturas(long idCarrera){
		super(TLISTARASIGNATURAS);
		this.idCarrera = idCarrera;
	}
	
	public ComandoListarAsignaturas(DataInputStream dis){
		super(TLISTARASIGNATURAS);
		byte[] idCarreraBytes = readBytes(dis);
		this.idCarrera = Packer.unpackLong(idCarreraBytes);
	}
	
	public long getIdCarrera() {
		return idCarrera;
	}

	public void setIdCarrera(long idCarrera) {
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
				byte[] idCarreraBytes = Packer.packLong(this.idCarrera);
				writeBytes(dos, idCarreraBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TLISTARASIGNATURAS send: " + e);
			}
		}
	}

}
