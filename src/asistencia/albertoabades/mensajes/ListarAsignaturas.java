package asistencia.albertoabades.mensajes;

//import asistencia.albertoabades.mensajes.ListarAsignaturas;
//import asistencia.albertoabades.mensajes.Packer;

public class ListarAsignaturas {
	
	private static final int RAW_INT_LENGTH = 4;
	private static final int RAW_LONG_LENGTH = 8;
	
	private long id_asignatura;
	private String nombre;
	private long id_carrera;
	
	public ListarAsignaturas(long id_asignatura, String nombre, long id_carrera){
		this.id_asignatura = id_asignatura;
		this.nombre = nombre;
		this.id_carrera = id_carrera;
	}

	public long getId_carrera() {
		return id_carrera;
	}

	public void setId_carrera(int id_carrera) {
		this.id_carrera = id_carrera;
	}
	
	public long getId_asignatura() {
		return id_asignatura;
	}

	public void setId_asignatura(int id_asignatura) {
		this.id_asignatura = id_asignatura;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String toString(){
		return Long.toString(this.id_asignatura) + "|" + this.nombre + "|" + Long.toString(this.id_carrera);
	}
	
	public byte[] toRaw(){
		try{
			int length = 0;
			byte[] idAsignaturaToRaw = Packer.packLong(this.id_asignatura);
			length += idAsignaturaToRaw.length;
			byte[] nombreAsignaturaToRaw = this.nombre.getBytes("UTF-8");
			byte[] nombreAsignaturaToRawLength = Packer.packInt(nombreAsignaturaToRaw.length);
			length += nombreAsignaturaToRaw.length + nombreAsignaturaToRawLength.length;
			byte[] idCarreraToRaw = Packer.packLong(this.id_carrera);
			length += idCarreraToRaw.length;
			byte[] totalInfoRaw = new byte[length];
			int index = 0;
			//Agregar el número de asignatura
			System.arraycopy(idAsignaturaToRaw, 0, totalInfoRaw, index, idAsignaturaToRaw.length);
			index += idAsignaturaToRaw.length;
			//Agregar el nombre y la longitud del nombre de la asignatura
			System.arraycopy(nombreAsignaturaToRawLength, 0, totalInfoRaw, index, nombreAsignaturaToRawLength.length);
			index += nombreAsignaturaToRawLength.length;
			System.arraycopy(nombreAsignaturaToRaw, 0, totalInfoRaw, index, nombreAsignaturaToRaw.length);
			index += nombreAsignaturaToRaw.length;
			//Agregar el número de la carrera
			System.arraycopy(idCarreraToRaw, 0, totalInfoRaw, index, idCarreraToRaw.length);
			return totalInfoRaw;
		}catch(Exception e){
			throw new RuntimeException("getrawdata: UTF-8 not supported");
		}
	}
	
	public static ListarAsignaturas fromRaw(byte[] rawData){
		try{
			int index = 0;
			//Extraer el código de asigntura
			byte[] idAsignaturaRaw = new byte[RAW_LONG_LENGTH];
			System.arraycopy(rawData, index, idAsignaturaRaw, 0, idAsignaturaRaw.length);
			index += idAsignaturaRaw.length;
			long idAsignatura = Packer.unpackLong(idAsignaturaRaw);
			//Extraer el nombre y la longitud del nombre de la asignatura
			byte[] nombreAsignaturaRawLength = new byte[RAW_INT_LENGTH];
			System.arraycopy(rawData, index, nombreAsignaturaRawLength, 0, nombreAsignaturaRawLength.length);
			index += nombreAsignaturaRawLength.length;
			int nombreAsignaturaLength = Packer.unpackInt(nombreAsignaturaRawLength);
			byte[] nombreAsignaturaRaw = new byte[nombreAsignaturaLength];
			System.arraycopy(rawData, index, nombreAsignaturaRaw, 0, nombreAsignaturaRaw.length);
			index += nombreAsignaturaRaw.length;
			String nombreAsignatura = new String(nombreAsignaturaRaw, "UTF-8");
			//Extraer el código de carrera
			byte[] idCarreraRaw = new byte[RAW_LONG_LENGTH];
			System.arraycopy(rawData, index, idCarreraRaw, 0, idCarreraRaw.length);
			index += idCarreraRaw.length;
			long idCarrera = Packer.unpackLong(idCarreraRaw);
			return new ListarAsignaturas(idAsignatura, nombreAsignatura, idCarrera);
		}catch(Exception e){
			throw new RuntimeException("setrawdata: UTF-8 not supported");
		}
	}

}
