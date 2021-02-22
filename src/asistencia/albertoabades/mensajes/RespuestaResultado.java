package asistencia.albertoabades.mensajes;

//import asistencia.albertoabades.mensajes.Respuestas;

public class RespuestaResultado extends Respuestas{
	
	protected String message;
	
	public RespuestaResultado(byte kind){
		super(kind);
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("message: %s", message);
	}

}
