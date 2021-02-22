package asistencia.albertoabades.cliente;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import asistencia.albertoabades.mensajes.ComandoAgregarPersona;
//import asistencia.albertoabades.mensajes.AddDayCommand;
import asistencia.albertoabades.mensajes.ComandoListarCarreras;
import asistencia.albertoabades.mensajes.ComandoPing;
//import tfg2.aabades.mensajes.AddSubjectCommand;
import asistencia.albertoabades.mensajes.Comandos;
//import asistencia.albertoabades.mensajes.ComandoBorrarPersona;
//import tfg2.aabades.mensajes.DeleteSubjectCommand;
//import asistencia.albertoabades.mensajes.LsDaysCommand;
import asistencia.albertoabades.mensajes.ComandoListarAsignaturas;
import asistencia.albertoabades.mensajes.Respuestas;

public class Cliente {
//itst
	private String host;
	private static final int PORT = 9090;
	private Comandos command;
	private Socket fd;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public Cliente(String host, Comandos command){
		this.host = host;
		this.command = command;
	}
	
	public void start(){
		try{
			System.out.println("conectado");
			this.fd = new Socket(this.host, PORT);
			this.dis = new DataInputStream(new BufferedInputStream(this.fd.getInputStream()));
			this.dos = new DataOutputStream(new BufferedOutputStream(this.fd.getOutputStream()));
			this.command.sendTo(this.dos);
			Respuestas response = Respuestas.receiveFrom(this.dis);
			/////CAMBIAR EN ANDROID PARA NO IMPRIMIR LA RESPUESTA Y CREAR EL SPINNER DEPENDENDIENDO DEL COMANDO
			System.out.println(response);
		}catch(Exception e){
			throw new RuntimeException(this + ": " + e);
		}finally{
			close();
		}
	}
	
	private void close(){
		try{
			if(this.fd != null){
				this.fd.close();
			}
			if(this.dis != null){
				this.dis.close();
			}
			if(this.dos != null){
				this.dos.close();
			}
		}catch(Exception e){
			//It's OK
		}
	}
	
	public static void main(String[] args){
		try{
			if((args.length >= 1 ) && (args.length <= 7)){
				Comandos command = null;
				if(ComandoAgregarPersona.correctAddCommand(args)){
					//command = new ComandoAgregarPersona(args[1], args[2] ,args[3], args[4]);
				}else if(ComandoPing.correctPingCommand(args)){
					command = new ComandoPing(args[1]);
				}else if(ComandoListarCarreras.comandoListarCarrerasCorrecto(args)){
					command = new ComandoListarCarreras();
				}else if(ComandoListarAsignaturas.comandoListarAsignaturasCorrecto(args)){
					command = new ComandoListarAsignaturas(2039);
				//}else if(ComandoBorrarPersona.correctDeleteCommand(args)){
					//command = new ComandoBorrarPersona(args[1],Integer.parseInt(args[2]),args[3]);
				//}else if(AddSubjectCommand.correctAddSubjectCommand(args)){
					//command = new AddSubjectCommand(args[1],Integer.parseInt(args[2]),args[3],args[4]);
				//}else if(AddDayCommand.correctAddDayCommand(args)){
					//command = new AddDayCommand(args[1],Integer.parseInt(args[2]),args[3],args[4]);
				//}else if(DeleteSubjectCommand.correctDeleteSubjectCommand(args)){
					//command = new DeleteSubjectCommand(args[1],Integer.parseInt(args[2]),args[3],args[4]);
				//}else if(ComandoListarAsignaturas.correctLsSubjectsCommand(args)){
					//command = new ComandoListarAsignaturas(args[1],Integer.parseInt(args[2]),args[3]);
				//}else if(LsDaysCommand.correctLsDaysCommand(args)){
					//command = new LsDaysCommand(args[1],Integer.parseInt(args[2]),args[3],args[4]);
				}else{
					System.err.printf("%s[error]: Invalid command\n", Cliente.class);
					return;
				}
				//String host = "localhost";
				String host = "192.168.1.103";
				Cliente client = new Cliente(host, command);
				client.start();
			}else{
				System.err.printf("%s[error]: Invalid arguments number\n", Cliente.class);
			}
		}catch(Exception e){
			System.err.printf("%s[error]: %s", Cliente.class, e);
		}
	}

}
