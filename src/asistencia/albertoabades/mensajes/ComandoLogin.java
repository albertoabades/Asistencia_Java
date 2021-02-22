package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoLogin extends Comandos{
	
	private static final int LENGTH = 5;
	private static final String STR_COMMAND = "LOGIN";
	
	public String dni;
	public String password;

	public ComandoLogin(String dni, String password) {
		super(TLOGIN);
		this.dni = dni;
		this.password = password;
	}
	
	public ComandoLogin(DataInputStream dis) {
		super(TLOGIN);
		this.dni = readString(dis);
		this.password = readString(dis);
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
				byte[] passwordBytes = this.dni.getBytes("UTF-8");
				writeBytes(dos, dniBytes);
				writeBytes(dos, passwordBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TLOGIN send: " + e);
			}
		}
	}

}