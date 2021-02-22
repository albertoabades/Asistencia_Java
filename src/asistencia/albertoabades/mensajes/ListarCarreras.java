package asistencia.albertoabades.mensajes;

//import asistencia.albertoabades.mensajes.ListarCarreras;
//import asistencia.albertoabades.mensajes.Packer;

public class ListarCarreras {
	
	private static final int RAW_INT_LENGTH = 4;
	private static final int RAW_LONG_LENGTH = 8;
	
	private long id_carrera;
	private String nombre;
	
	public ListarCarreras(long id_carrera, String nombre){
		this.id_carrera = id_carrera;
		this.nombre = nombre;
	}

	public long getId_carrera() {
		return id_carrera;
	}

	public void setId_carrera(long id_carrera) {
		this.id_carrera = id_carrera;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String toString(){
		return Long.toString(this.id_carrera) + "|" + this.nombre;
	}
	public byte[] toRaw(){
		try{
			int length = 0;
			byte[] idCarreraToRaw = Packer.packLong(this.id_carrera);
			length += idCarreraToRaw.length;
			byte[] nombreCarreraToRaw = this.nombre.getBytes("UTF-8");
			byte[] nombreCarreraToRawLength = Packer.packInt(nombreCarreraToRaw.length);
			length += nombreCarreraToRaw.length + nombreCarreraToRawLength.length;
			byte[] totalInfoRaw = new byte[length];
			int index = 0;
			//Agregar el número de carrera
			System.arraycopy(idCarreraToRaw, 0, totalInfoRaw, index, idCarreraToRaw.length);
			index += idCarreraToRaw.length;
			//Agregar el nombre y la longitud del nombre de la carrera
			System.arraycopy(nombreCarreraToRawLength, 0, totalInfoRaw, index, nombreCarreraToRawLength.length);
			index += nombreCarreraToRawLength.length;
			System.arraycopy(nombreCarreraToRaw, 0, totalInfoRaw, index, nombreCarreraToRaw.length);
			index += nombreCarreraToRaw.length;
			return totalInfoRaw;
		}catch(Exception e){
			throw new RuntimeException("getrawdata: UTF-8 not supported");
		}
	}
	
	public static ListarCarreras fromRaw(byte[] rawData){
		try{
			int index = 0;
			//Extraer el código de carrera
			byte[] idCarreraRaw = new byte[RAW_LONG_LENGTH];
			System.arraycopy(rawData, index, idCarreraRaw, 0, idCarreraRaw.length);
			index += idCarreraRaw.length;
			long idCarrera = Packer.unpackLong(idCarreraRaw);
			//Extraer el nombre y la longitud del nombre de la carrera
			byte[] nombreCarreraRawLength = new byte[RAW_INT_LENGTH];
			System.arraycopy(rawData, index, nombreCarreraRawLength, 0, nombreCarreraRawLength.length);
			index += nombreCarreraRawLength.length;
			int nombreCarreraLength = Packer.unpackInt(nombreCarreraRawLength);
			byte[] nombreCarreraRaw = new byte[nombreCarreraLength];
			System.arraycopy(rawData, index, nombreCarreraRaw, 0, nombreCarreraRaw.length);
			index += nombreCarreraRaw.length;
			String nombreCarrera = new String(nombreCarreraRaw, "UTF-8");
			return new ListarCarreras(idCarrera, nombreCarrera);
		}catch(Exception e){
			throw new RuntimeException("setrawdata: UTF-8 not supported");
		}
	}

}
