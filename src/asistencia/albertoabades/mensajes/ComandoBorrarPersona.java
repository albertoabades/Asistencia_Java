package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoBorrarPersona extends Comandos{
	
	private static final int LENGTH = 2;
	private static final String STR_COMMAND = "BAJA";
	
	private String dni;
	private String password;
	
	public ComandoBorrarPersona(String dni, String password){
		super(TBORRARPERSONA);
		this.dni = dni;
		this.password = password;
	}
	
	public ComandoBorrarPersona(DataInputStream dis){
		super(TBORRARPERSONA);
		try{
			this.dni = readString(dis);
			this.password = readString(dis);
		}catch(Exception e){
			throw new RuntimeException("TBORRARPERSONA receive: " + e);
		}
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
	
	@Override
	public String toString() {
		return super.toString() + String.format("%s", dni);
	}
	
	public static boolean correctDeleteCommand(String[] strTokens){
		return (strTokens.length == LENGTH) && (strTokens[COMMAND_INDEX].toUpperCase().equals(STR_COMMAND));
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos){
			super.sendTo(dos);
			try{
				byte[] dniBytes = this.dni.getBytes("UTF-8");
				byte[] passwordBytes = this.password.getBytes("UTF-8");
				writeBytes(dos, dniBytes);
				writeBytes(dos, passwordBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TBORRARPERSONA send: " + e);
			}
		}
	}

}