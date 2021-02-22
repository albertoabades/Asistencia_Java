package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ComandoListarCarreras extends Comandos{
	
	private static final String STR_COMMAND = "LS_CARRERAS";
	private static final int LENGTH = 1;

	public ComandoListarCarreras() {
		super(TLISTARCARRERAS);
	}
	
	public ComandoListarCarreras(DataInputStream dis){
		super(TLISTARCARRERAS);
	}
	
	public static boolean comandoListarCarrerasCorrecto(String[] strTokens){
		return (strTokens.length == LENGTH) && (strTokens[COMMAND_INDEX].toUpperCase().equals(STR_COMMAND));
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos){
			super.sendTo(dos);
			try {
				dos.flush();
			} catch (Exception e) {
				throw new RuntimeException("TLISTARCARRERAS send: " + e);
			}
		}
	}

}
