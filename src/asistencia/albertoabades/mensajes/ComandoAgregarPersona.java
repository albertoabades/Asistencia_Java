package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoAgregarPersona extends Comandos{
	
	private static final int LENGTH = 5;
	private static final String STR_COMMAND = "ALTA";
	
	private String dni;
	private String nombre;
	private String apellido;
	private String password;
	private String profesor;
	
	public ComandoAgregarPersona(String dni, String nombre, String apellido, String password, String profesor){
		super(TAGREGARPERSONA);
		this.dni = dni;
		this.nombre = nombre;
		this.apellido = apellido;
		this.password = password;
		this.profesor = profesor;
	}
	
	public ComandoAgregarPersona(DataInputStream dis){
		super(TAGREGARPERSONA);
		try{
			this.dni = readString(dis);
			this.nombre = readString(dis);
			this.apellido = readString(dis);
			this.password = readString(dis);
			this.profesor = readString(dis);
		}catch(Exception e){
			throw new RuntimeException("TAGREGARPERSONA receive: " + e);
		}
	}
	
	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getProfesor() {
		return profesor;
	}

	public void setProfesor(String profesor) {
		this.profesor = profesor;
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("%s", dni);
	}
	
	public static boolean correctPassword(String password){
		return password.length() >= 5 && password.length() <= 12;
	}
	
	public static boolean correctAddCommand(String[] strTokens){
		return (strTokens.length == LENGTH) && (strTokens[COMMAND_INDEX].toUpperCase().equals(STR_COMMAND)) && correctPassword(strTokens[COMMAND_INDEX + 4]);
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos){
			super.sendTo(dos);
			try{
				byte[] dniBytes = this.dni.getBytes("UTF-8");
				byte[] nombreBytes = this.nombre.getBytes("UTF-8");
				byte[] apellido1Bytes = this.apellido.getBytes("UTF-8");
				byte[] passwordBytes = this.password.getBytes("UTF-8");
				byte[] profesorBytes = this.profesor.getBytes("UTF-8");
				writeBytes(dos, dniBytes);
				writeBytes(dos, nombreBytes);
				writeBytes(dos, apellido1Bytes);
				writeBytes(dos, passwordBytes);
				writeBytes(dos, profesorBytes);
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("TAGREGARPERSONA send: " + e);
			}
		}
	}

}
