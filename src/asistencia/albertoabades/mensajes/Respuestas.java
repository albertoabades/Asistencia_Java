package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;

//import asistencia.albertoabades.mensajes.Mensajes;
//import asistencia.albertoabades.mensajes.RespuestaError;
//import asistencia.albertoabades.mensajes.RespuestaListarAsignaturas;
//import asistencia.albertoabades.mensajes.RespuestaListarCarreras;
//import asistencia.albertoabades.mensajes.RespuestaOK;
//import asistencia.albertoabades.mensajes.Respuestas;

public abstract class Respuestas extends Mensajes{
	
	/*enum Kind{
		NONE, RERROR, ROK, RLSSUBJECT, RLSDAY
	};*/
	
	public Respuestas(byte kind){
		super(kind);
	}
	
	public static Respuestas receiveFrom(DataInputStream dis){
		byte kind = 0;
		try{
			kind = dis.readByte();
			switch(kind){
			case RERROR:
				//return new ErrorResponse(dis);
				return new RespuestaError(dis);
			case ROK:
				//return new OkResponse(dis);
				return new RespuestaOK(dis);
			case RLISTARCARRERAS:
				//return new LsSubjectsResponse(dis);
				return new RespuestaListarCarreras(dis);
			case RLISTARASIGNATURAS:
				//return new LsDaysResponse(dis);
				return new RespuestaListarAsignaturas(dis);
			case RLISTARMISCARRERAS:
				//return new LsDaysResponse(dis);
				//return new RespuestaListarMisCarreras(dis);
			case RLISTARMISASIGNATURAS:
				//return new LsDaysResponse(dis);
				//return new RespuestaListarMisAsignaturas(dis);
			default:
				throw new RuntimeException("receivefrom: unknown response receive");
			}
		}catch(Exception e){
			throw new RuntimeException("receivefrom: " + e);
		}
	}

}
