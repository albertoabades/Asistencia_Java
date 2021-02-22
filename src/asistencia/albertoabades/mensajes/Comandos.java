package asistencia.albertoabades.mensajes;

import java.io.DataInputStream;

//import asistencia.albertoabades.mensajes.ComandoAgregarAsignatura;
//import asistencia.albertoabades.mensajes.ComandoAgregarPersona;
//import asistencia.albertoabades.mensajes.ComandoCrearClase;
//import asistencia.albertoabades.mensajes.ComandoListarAsignaturas;
//import asistencia.albertoabades.mensajes.ComandoListarCarreras;
//import asistencia.albertoabades.mensajes.ComandoListarMisAsignaturas;
//import asistencia.albertoabades.mensajes.ComandoListarMisCarreras;
//import asistencia.albertoabades.mensajes.ComandoLogin;
//import asistencia.albertoabades.mensajes.ComandoPing;
//import asistencia.albertoabades.mensajes.Comandos;
//import asistencia.albertoabades.mensajes.Mensajes;

public abstract class Comandos extends Mensajes{
	
	protected static final int COMMAND_INDEX = 0;
	
	/*enum Kind{
		NONE, ADD, DELETE, ADDSUBJECT, ADDDAY, DELETESUBJECT, LSSUBJECT, LSDAY, LSCARRERAS
	};
	*/
	
	public Comandos(byte kind){
		super(kind);
	}
	
	public static Comandos receiveFrom(DataInputStream dis){
		byte kind = 0;
		try{
			kind = dis.readByte();
			switch(kind){
			case TAGREGARPERSONA:
				//return new AddCommand(dis);
				return new ComandoAgregarPersona(dis);
			case TBORRARPERSONA:
				//return new DeleteCommand(dis);
				//return new ComandoBorrarPersona(dis);
			case TLISTARCARRERAS:
				//return new AddSubjectCommand(dis);
				return new ComandoListarCarreras(dis);
			case TAGREGARCLASE:
				//return new AddDayCommand(dis);
				//return new ComandoAgregarCarrera(dis);
			case TBORRARCARRERA:
				//return new DeleteSubjectCommand(dis);
				//return new ComandoBorrarCarrera(dis);
			case TLISTARASIGNATURAS:
				//return new LsSubjectsCommand(dis);
				return new ComandoListarAsignaturas(dis);
			case TAGREGARASIGNATURA:
				//return new LsDaysCommand(dis);
				return new ComandoAgregarAsignatura(dis);
			case TBORRARASIGNATURA:
				//return new LsCarrerasCommand(dis);
				//return new ComandoBorrarAsignatura(dis);
			case TLISTARMISCARRERAS:
				//return new LsCarrerasCommand(dis);
				return new ComandoListarMisCarreras(dis);
			case TLISTARMISASIGNATURAS:
				//return new LsCarrerasCommand(dis);
				return new ComandoListarMisAsignaturas(dis);
			case TLOGIN:
				return new ComandoLogin(dis);
			case TCREARCLASE:
				return new ComandoCrearClase(dis);
			case TPING:
				return new ComandoPing(dis);
			default:
				throw new RuntimeException("receivefrom: unknown command receive");
			}
		}catch(Exception e){
			throw new RuntimeException("receivefrom: " + e);
		}
	}

}
