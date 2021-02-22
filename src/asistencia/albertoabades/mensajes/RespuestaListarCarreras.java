package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

public class RespuestaListarCarreras extends Respuestas{
	
	private ListarCarreras[] carreras;

	public RespuestaListarCarreras(ListarCarreras[] carreras) {
		super(RLISTARCARRERAS);
		this.carreras = carreras;
	}
	
	public RespuestaListarCarreras(DataInputStream dis){
		super(RLISTARCARRERAS);
		try{
			int size = Packer.unpackInt(readBytes(dis));
			this.carreras = new ListarCarreras[size];
			for(int index = 0; index < size; index++){
				this.carreras[index] = ListarCarreras.fromRaw(readBytes(dis));
			}
		}catch(Exception e){
			throw new RuntimeException("RLISTARCARRERAS receive: " + e);
		}
	}
	
	public String toString(){
		String result = super.toString() + "\n";
		for(ListarCarreras carrera: carreras){
			result += carrera + "\n";
		}
		return result;
	}
	
	public ArrayList<String> toArray(){
		ArrayList<String> array = null;
		String carreraString;
		for(ListarCarreras carrera: carreras){
			carreraString = carrera.toString();
			array.add(carreraString);
		}
		return array;
	}
	
	@Override
	public void sendTo(DataOutputStream dos) {
		synchronized(dos){
			super.sendTo(dos);
			try{
				byte[] sizeBytes = Packer.packInt(this.carreras.length);
				writeBytes(dos, sizeBytes);
				if(carreras.length > 0){
					for(ListarCarreras carrera: carreras){
						writeBytes(dos, carrera.toRaw());
					}
				}
				dos.flush();
			}catch(Exception e){
				throw new RuntimeException("RLISTARCARRERAS send: " + e);
			}
		}
	}

}