package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Mensajes {
	
	/*enum Kind{
		NONE, ERROR, OK, ADD, DELETE, ADDSUBJECT, ADDDAY, DELETESUBJECT, LSSUBJECT, LSDAY
	};
	*/
	
	public static final byte RERROR = 0;				//respuesta KO
	public static final byte ROK = 1;					//respuesta OK
	public static final byte TAGREGARPERSONA = 2;		//agregar una persona
	public static final byte TBORRARPERSONA = 3;		//borrar una persona
	public static final byte TLISTARCARRERAS = 4;		//peticion para listar todas las carreras
	public static final byte RLISTARCARRERAS = 5;		//respuesta de listar todas las carreras
	public static final byte TBORRARCARRERA = 7;		//borrar una carrera a una persona
	public static final byte TLISTARASIGNATURAS = 8;	//peticion para listar todas las carreras
	public static final byte RLISTARASIGNATURAS = 9;	//respuesta de listar todas las carreras
	public static final byte TAGREGARASIGNATURA = 10;	//agregar una asignatura a una persona
	public static final byte TBORRARASIGNATURA = 11;	//borrar una asignatura a una persona
	public static final byte TLISTARMISCARRERAS = 12;	//peticion para listar las carreras de una persona
	public static final byte RLISTARMISCARRERAS = 13;	//respuesta de listar las carreras de una persona
	public static final byte TLISTARMISASIGNATURAS = 14;//peticion para listar las asignaturas de una persona
	public static final byte RLISTARMISASIGNATURAS = 15;//respuesta de listar las asignaturas de una persona
	public static final byte TLOGIN = 16;				//peticion de login
	public static final byte RLOGIN = 17;				//respuesta de login
	public static final byte TCREARCLASE = 18;
	public static final byte RCREARCLASE = 19;
	public static final byte TAGREGARCLASE = 20;
	public static final byte RAGREGARCLASE = 21;
	public static final byte TPING = 99;				//PRUEBA DE CONEXION
	
	private byte kind;
	
	protected static final int LENGTH_SIZE = 4;
	
	public Mensajes(byte kind){
		this.kind = kind;
	}
	
	public byte getKind(){
		return kind;
	}
	
	public static String kindName(byte kind){
		switch(kind){
		case RERROR:
			return "rerror";
		case ROK:
			return "rok";
		case TAGREGARPERSONA:
			return "tagregarpersona";
		case TBORRARPERSONA:
			return "tborrarpersona";
		case TLISTARCARRERAS:
			return "tlistarcarreras";
		case RLISTARCARRERAS:
			return "rlistarcarreras";
		case TBORRARCARRERA:
			return "tborrarcarrera";
		case TLISTARASIGNATURAS:
			return "tlistarasignaturas";
		case RLISTARASIGNATURAS:
			return "rlistarasignaturas";
		case TAGREGARASIGNATURA:
			return "tagregarasignatura";
		case TBORRARASIGNATURA:
			return "tborrarasignatura";
		case TLISTARMISCARRERAS:
			return "tlistarmiscarreras";
		case RLISTARMISCARRERAS:
			return "rlistarmiscarreras";
		case TLISTARMISASIGNATURAS:
			return "tlistarmisasignaturas";
		case RLISTARMISASIGNATURAS:
			return "rlistarmisasignaturas";
		case TLOGIN:
			return "tlogin";
		case RLOGIN:
			return "rlogin";
		case TCREARCLASE:
			return "tcrearclase";
		case RCREARCLASE:
			return "rcrearclase";
		case TAGREGARCLASE:
			return "tagregarclase";
		case RAGREGARCLASE:
			return "ragregarclase";
		case TPING:
			return "tping";
		default:
			throw new RuntimeException("kindname: bad message kind");
		}
	}
	
	public String toString(){
		return String.format("[%s]", kindName(kind));
	}
	
	protected void writeBytes(DataOutputStream dos, byte[] bytes){
		try{
			int n = bytes.length;
			byte[] nBytes = Packer.packInt(n);
			dos.write(nBytes);
			if(n > 0){
				dos.write(bytes);
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	protected byte[] readBytes(DataInputStream dis){
		try{
			byte[] nBytes = new byte[LENGTH_SIZE];
			int bytesRead = dis.read(nBytes);
			if(bytesRead == LENGTH_SIZE){
				int n = Packer.unpackInt(nBytes);
				if (n > 8*1024*1024) {
					throw new RuntimeException("readbytes: message is > 8M");
				}
				byte[] bytes = new byte[n];
				if(n > 0){
					dis.readFully(bytes);
				}
				return bytes;
			}else{
				throw new RuntimeException("readbytes: invalid bytes length");
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	protected void writeString(DataOutputStream dos, String string){
		try{
			byte[] bytes = string.getBytes("UTF-8");
			writeBytes(dos, bytes);
		}catch(Exception e){
			throw new RuntimeException("writeString: UTF-8 not supported");
		}
	}
	
	protected String readString(DataInputStream dis){
		try{
			byte[] bytes = readBytes(dis);
			return new String(bytes, "UTF-8");
		}catch(Exception e){
			throw new RuntimeException("readString; UTF-8 not supported");
		}
	}
	
	public void sendTo(DataOutputStream out){
		try{
			out.writeByte(kind);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

}
