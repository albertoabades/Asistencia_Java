package asistencia.albertoabades.servidor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.sqlite.SQLiteConfig;

import asistencia.albertoabades.mensajes.ComandoCrearClase;
import asistencia.albertoabades.mensajes.ComandoAgregarAsignatura;
import asistencia.albertoabades.mensajes.ComandoAgregarPersona;
//import asistencia.albertoabades.mensajes.AddDayCommand;
import asistencia.albertoabades.mensajes.ComandoBorrarAsignatura;
import asistencia.albertoabades.mensajes.ComandoBorrarCarrera;
import asistencia.albertoabades.mensajes.ComandoListarMisAsignaturas;
import asistencia.albertoabades.mensajes.ComandoListarMisCarreras;
import asistencia.albertoabades.mensajes.ComandoLogin;
import asistencia.albertoabades.mensajes.ComandoPing;
import asistencia.albertoabades.mensajes.Comandos;
import asistencia.albertoabades.mensajes.ComandoBorrarPersona;
import asistencia.albertoabades.mensajes.ListarCarreras;
//import asistencia.albertoabades.mensajes.LsDays;
//import asistencia.albertoabades.mensajes.LsDaysCommand;
//import asistencia.albertoabades.mensajes.LsDaysResponse;
import asistencia.albertoabades.mensajes.ListarAsignaturas;
import asistencia.albertoabades.mensajes.ComandoListarAsignaturas;
import asistencia.albertoabades.mensajes.RespuestaListarAsignaturas;
import asistencia.albertoabades.mensajes.RespuestaError;
import asistencia.albertoabades.mensajes.RespuestaListarCarreras;
import asistencia.albertoabades.mensajes.RespuestaOK;
import asistencia.albertoabades.mensajes.Respuestas;
import asistencia.albertoabades.funciones.Cifrado_RSA;

public class Servidor {
	//Its ok
	private static final int CLIENT_PORT = 9090;
	private static final int HANDLER_THREADS_NUMBER = 1;
	private static final int WAIT_SECONDS = 10;
	private static final int NANOSECONDS = 1000000;
	
	private static final String TADD_ROK_RESPONSE = "Added";
	private static final String TADD_RERROR_RESPONSE = "Error";
	private static final String TDELETE_ROK_RESPONSE = "Deleted";
	private static final String TDELETE_RERROR_RESPONSE = "Error in delete";
	private static final String TADDSUBJECT_ROK_RESPONSE = "Subject added";
	private static final String TADDSUBJECT_RERROR_RESPONSE = "Error to add subject";
	private static final String TADDDAY_ROK_RESPONSE = "Day added";
	private static final String TADDDAY_RERROR_RESPONSE = "Error to add day";
	private static final String TDELETESUBJECT_ROK_RESPONSE = "Subject deleted";
	private static final String TDELETESUBJECT_RERROR_RESPONSE = "Error to delete subject";
	private static final String TPING_ROK_RESPONSE = "OK";
	
	private ServerSocket clientSk;
	private Thread clientThread;
	private ExecutorService handlersPool;
	private ExecutorService threadsPool;
	
	
	public Servidor(){
	}
	
	public void start(){
		if(this.clientThread != null){
			throw new RuntimeException("You're kidding");
		}
		
		try{
			this.clientSk = new ServerSocket(CLIENT_PORT);
		}catch(Exception e){
			throw new RuntimeException(this + ": " + e);
		}
		
		this.handlersPool = Executors.newFixedThreadPool(HANDLER_THREADS_NUMBER);
		
		this.clientThread = new Thread(){
			public void run(){
				clientControl();
			}
			
			public void interrupt(){
				super.interrupt();
			}
		};
		
		this.handlersPool.execute(clientThread);
		
		this.threadsPool = Executors.newCachedThreadPool();
		
		System.out.println("started");
	}
	
	public void close(){
		try{
			this.threadsPool.shutdown();
			try{
				if (!this.threadsPool.awaitTermination(WAIT_SECONDS, TimeUnit.MILLISECONDS)) {
					this.threadsPool.shutdownNow();
				}
			}catch(Exception e){
				this.threadsPool.shutdownNow();
				Thread.currentThread().interrupt();
			}
			this.handlersPool.shutdown();
			try{
				if (!this.handlersPool.awaitTermination(WAIT_SECONDS, TimeUnit.SECONDS)) {
					this.handlersPool.shutdownNow();
				}
			}catch(Exception e){
				this.handlersPool.shutdownNow();
				this.clientThread.interrupt();;
				Thread.currentThread().interrupt();
			}
			if(this.clientSk != null){
				this.clientSk.close();
			}
			System.out.println("closed");
		}catch(Exception e){
			//It's OK
		}
	}
	
	public String toString(){
		return "[" + CLIENT_PORT + "]";
	}
	
	public static class ControlClientService implements ClienteDeServicio{
		
		private Servidor server;
		
		public ControlClientService(Servidor server){
			this.server = server;
		}

		@Override
		public void serveClient(DataInputStream dis, DataOutputStream dos) {
			try{
				Respuestas response = null;
				//ruta a la base de datos donde se guardará toda la información
				String dbURL = "jdbc:sqlite:/Users/alberto/Desktop/database_def.db";
				Connection conexion = null;
				PreparedStatement consulta = null;
				PreparedStatement consultaSize = null;
				PreparedStatement consultaClave = null;
				ResultSet rs = null;
				ResultSet rsSize = null;
				int contador = 0;
				int count = 0;
				int total = 0;
				//PRAGMA foreign_keys = ON;
				//Comandos propios de sqlite para acceder a la base de datos
				SQLiteConfig config = new SQLiteConfig();  
		        config.enforceForeignKeys(true);
		        //Comando que envia la aplicación
				Comandos comando = Comandos.receiveFrom(dis);
				//byte del comando para luego acceder al switch
				byte kind = comando.getKind();
				System.out.println("CCCCCCCCC" + kind);
				switch(kind){
				/*
				 * Comando para crear un nuevo alumno/profesor
				 */
				case Comandos.TAGREGARPERSONA:
					//Se convierte el comando al tipo correspondiente para extraer los campos necesarios
					ComandoAgregarPersona cap = (ComandoAgregarPersona)comando;
					//Campos necesarios para guardar/buscar en la base de datos
					String capDni = cap.getDni();
					String capNombre = cap.getNombre();
					String capApellido = cap.getApellido();
					String capPassword = cap.getPassword();
					String capProfesor= cap.getProfesor();
					//Cifra la password para guardarla
					Cifrado_RSA rsa = new Cifrado_RSA();
					rsa.genKeyPair(512);
					String capPasswordRSA = rsa.Encrypt(capPassword);
					//Saco la clave publica y la privada para gurdarlas en la base de datos
					String capPublica = rsa.getPublicKeyString();
					String capPrivada = rsa.getPrivateKeyString();
					//Comandos propios de sql para la base de datos
					try {
						Class.forName("org.sqlite.JDBC");
					} catch (Exception e) {
					}
					conexion = DriverManager.getConnection(dbURL,config.toProperties());
					consulta = conexion.prepareStatement("INSERT INTO PERSONA (DNI, NOMBRE, APELLIDO, PASSWORD, PROFESOR) VALUES (?,?,?,?,?)");
					consulta.setString(1, capDni);
					consulta.setString(2, capNombre);
					consulta.setString(3, capApellido);
					//Password cifrada
					consulta.setString(4, capPasswordRSA);
					consulta.setString(5, capProfesor);
					count = consulta.executeUpdate();
					//Todos los System.out.println son trazas
					System.out.println("Se han insertado "+count+" filas");
					//Respuesta que se enviará a la aplicación en este caso OK/KO
					if(count != 0){
						response = new RespuestaOK(TADD_ROK_RESPONSE);
					}else{
						response = new RespuestaError(TADD_RERROR_RESPONSE);
					}
					if(count != 0){
						consultaClave = conexion.prepareStatement("INSERT INTO PERSONA (DNI, PUBLICA, PRIVADA) VALUES (?,?,?)");
						consultaClave.setString(1, capDni);
						consultaClave.setString(2, capPublica);
						consultaClave.setString(3, capPrivada);
						count = consultaClave.executeUpdate();
					}
					response.sendTo(dos);
					consulta.close();
					consultaClave.close();
					conexion.close();
					break;
				/*
				 *Comando para logarse en la aplicación una vez que se ha creado el alumno/profesor
				 */
				case Comandos.TLOGIN:
					//Se convierte el comando al tipo correspondiente para extraer los campos necesarios
					ComandoLogin cl = (ComandoLogin)comando;
					//Campos necesarios para guardar/buscar en la base de datos
					String clDni = cl.getDni();
					//Todos los System.out.println son trazas
					System.out.println("Se ha recibido el dni: " +clDni);
					String clPassword = cl.getPassword();
					System.out.println("Se ha recibido la pass: " +clPassword);
					//Comandos propios de sql para la base de datos
					try {
						Class.forName("org.sqlite.JDBC");
					} catch (Exception e) {
					}
					conexion = DriverManager.getConnection(dbURL,config.toProperties());
					consulta = conexion.prepareStatement("SELECT * FROM PERSONA WHERE DNI = ? AND PASSWORD = ?");
					consulta.setString(1, clDni);
					consulta.setString(2, clPassword);
					rs = consulta.executeQuery();
					while(rs.next()){
						contador++;
					}
					//Respuesta que se enviará a la aplicación en este caso OK/KO
					if(contador != 0){
						response = new RespuestaOK(TADD_ROK_RESPONSE);
						System.out.println(TADD_ROK_RESPONSE);
					}else{
						response = new RespuestaError(TADD_RERROR_RESPONSE);
						System.out.println(TADD_RERROR_RESPONSE);
					}
					response.sendTo(dos);
					consulta.close();
					conexion.close();
					break;
				/*
				 * Se listan todas las carreras que hay disponibles en la universidad
				 */
				case Comandos.TLISTARCARRERAS:
					//En este caso no se necesitan parámetros de entrada ya que se buscará en plano sobre la base de datos
					//Comandos propios de sql para la base de datos
					try{
						Class.forName("org.sqlite.JDBC");
					}catch(Exception e){
						///OOOOOK
					}
					conexion = DriverManager.getConnection(dbURL, config.toProperties());
					//Esta devuelve los registros
					consulta = conexion.prepareStatement("SELECT * FROM CARRERA");
					rs = consulta.executeQuery();
					//Esta devuelve el total para crear un array de un tamaño determinado
					consultaSize = conexion.prepareStatement("SELECT COUNT(*) AS TOTAL FROM CARRERA");
					rsSize = consultaSize.executeQuery();
					while(rsSize.next()){
						contador = rsSize.getInt("TOTAL");
					}
					//Se prepara la respuesta, que en este caso será un array
					ListarCarreras[] carreras = new ListarCarreras[contador];
					ListarCarreras carrera;
					while(rs.next()){
						carrera = new ListarCarreras(rs.getInt(1), rs.getString(2));
						carreras[total] = carrera;
						total++;
					}
					//Trazas
					System.out.println("+++++++  " + total + "  +++++++");
					//Envío de la respuesta
					response = new RespuestaListarCarreras(carreras);
					response.sendTo(dos);
					rs.close();
					rsSize.close();
					conexion.close();
					break;
				/*
				 * Se listan las asignaturas correspondientes a una carrera
				 */
				case Comandos.TLISTARASIGNATURAS:
					//Se convierte el comando al tipo correspondiente para extraer los campos necesarios
					ComandoListarAsignaturas cla = (ComandoListarAsignaturas)comando;
					//Campos necesarios para guardar/buscar en la base de datos
					Long claIdCarrera = cla.getIdCarrera();
					//Comandos propios de sql para la base de datos
					try{
						Class.forName("org.sqlite.JDBC");
					}catch(Exception e){
						///OOOOOK
					}
					conexion = DriverManager.getConnection(dbURL, config.toProperties());
					//Esta devuelve los registros
					consulta = conexion.prepareStatement("SELECT ID_ASIGNATURA, NOMBRE FROM ASIGNATURA WHERE ID_CARRERA = ?");
					consulta.setLong(1, claIdCarrera);
					rs = consulta.executeQuery();
					//Esta devuelve el total para crear un array de un tamaño determinado
					consultaSize = conexion.prepareStatement("SELECT COUNT(*) AS TOTAL FROM ASIGNATURA WHERE ID_CARRERA = ?");
					consultaSize.setLong(1, claIdCarrera);
					rsSize = consultaSize.executeQuery();
					while(rsSize.next()){
						contador = rsSize.getInt("TOTAL");
					}
					//Se prepara la respuesta, que en este caso será un array
					ListarAsignaturas[] asignaturas = new ListarAsignaturas[contador];
					ListarAsignaturas asignatura;
					total = 0;
					while(rs.next()){
						asignatura = new ListarAsignaturas(rs.getLong(1), rs.getString(2), claIdCarrera);
						asignaturas[total] = asignatura;
						System.out.println(asignatura);
						total++;
					}
					//Trazas
					System.out.println("%%%%%%%%  " + total + "  %%%%%%%%");
					//Envío de la respuesta
					response = new RespuestaListarAsignaturas(asignaturas);
					response.sendTo(dos);
					rs.close();
					rsSize.close();
					conexion.close();
					break;
				/*
				 * Se agrega la dupla de carrera y asignatura seleccionada
				 */
				case Comandos.TAGREGARASIGNATURA:
					//Se convierte el comando al tipo correspondiente para extraer los campos necesarios
					ComandoAgregarAsignatura caa = (ComandoAgregarAsignatura)comando;
					//Campos necesarios para guardar/buscar en la base de datos
					String caaDni = caa.getDni();
					int caaIdCarrera = caa.getIdCarrera();
					int caaIdAsignatura = caa.getIdAsignatura();
					//Comandos propios de sql para la base de datos
					try{
						Class.forName("org.sqlite.JDBC");
					}catch(Exception e){
						///OOOOOK
					}
					conexion = DriverManager.getConnection(dbURL, config.toProperties());
					consulta = conexion.prepareStatement("INSERT INTO ASIG_PERS (DNI, ID_CARRERA, ID_ASIGNATURA) VALUES (?,?,?)");
					consulta.setString(1, caaDni);
					consulta.setInt(2, caaIdCarrera);
					consulta.setInt(3, caaIdAsignatura);
					count = consulta.executeUpdate();
					//Trazas
					System.out.println("Se han insertado "+count+" filas");
					//Respuesta que se enviará a la aplicación en este caso OK/KO
					if(count != 0){
						response = new RespuestaOK(TADD_ROK_RESPONSE);
					}else{
						response = new RespuestaError(TADD_RERROR_RESPONSE);
					}
					response.sendTo(dos);
					consulta.close();
					conexion.close();
					break;
				/*
				 * Se listan las carreras que tiene un alumno/profesor
				 */
				case Comandos.TLISTARMISCARRERAS:
					//Se convierte el comando al tipo correspondiente para extraer los campos necesarios
					ComandoListarMisCarreras clmc = (ComandoListarMisCarreras)comando;
					//Campos necesarios para guardar/buscar en la base de datos
					String clmcDni = clmc.getDni();
					//Comandos propios de sql para la base de datos
					try{
						Class.forName("org.sqlite.JDBC");
					}catch(Exception e){
						///OOOOOK
					}
					conexion = DriverManager.getConnection(dbURL, config.toProperties());
					//Esta devuelve los registros
					consulta = conexion.prepareStatement("SELECT DISTINCT C.ID_CARRERA, C.NOMBRE FROM CARRERA C, ASIG_PERS AP WHERE C.ID_CARRERA = AP.ID_CARRERA AND AP.DNI = ?");
					consulta.setString(1, clmcDni);
					rs = consulta.executeQuery();
					//Esta devuelve el total para crear un array de un tamaño determinado
					consultaSize = conexion.prepareStatement("SELECT DISTINCT C.ID_CARRERA, C.NOMBRE FROM CARRERA C, ASIG_PERS AP WHERE C.ID_CARRERA = AP.ID_CARRERA AND AP.DNI = ?");
					consultaSize.setString(1, clmcDni);
					rsSize = consultaSize.executeQuery();
					while(rsSize.next()){
						contador++;
					}
					//Se prepara la respuesta, que en este caso será un array
					ListarCarreras[] misCarreras = new ListarCarreras[contador];
					ListarCarreras miCarrera;
					while(rs.next()){
						miCarrera = new ListarCarreras(rs.getInt(1), rs.getString(2));
						misCarreras[total] = miCarrera;
						total++;
					}
					//Envío de la respuesta
					response = new RespuestaListarCarreras(misCarreras);
					response.sendTo(dos);
					rs.close();
					rsSize.close();
					conexion.close();
					break;
				/*
				 * Se listan las asignaturas que tiene un alumno/profesor
				 */
				case Comandos.TLISTARMISASIGNATURAS:
					//Se convierte el comando al tipo correspondiente para extraer los campos necesarios
					ComandoListarMisAsignaturas clma = (ComandoListarMisAsignaturas)comando;
					//Campos necesarios para guardar/buscar en la base de datos
					String clmaDni = clma.getDni();
					Long clmaIdMiCarrera = clma.getIdCarrera();
					//Comandos propios de sql para la base de datos
					try{
						Class.forName("org.sqlite.JDBC");
					}catch(Exception e){
						///OOOOOK
					}
					conexion = DriverManager.getConnection(dbURL, config.toProperties());
					//Esta devuelve los registros
					consulta = conexion.prepareStatement("SELECT DISTINCT A.ID_ASIGNATURA, A.NOMBRE FROM ASIGNATURA A, ASIG_PERS AP WHERE A.ID_ASIGNATURA = AP.ID_ASIGNATURA AND AP.DNI = ? AND AP.ID_CARRERA = ?");
					consulta.setString(1, clmaDni);
					consulta.setLong(2, clmaIdMiCarrera);
					rs = consulta.executeQuery();
					//Esta devuelve el total para crear un array de un tamaño determinado
					consultaSize = conexion.prepareStatement("SELECT DISTINCT A.ID_ASIGNATURA, A.NOMBRE FROM ASIGNATURA A, ASIG_PERS AP WHERE A.ID_ASIGNATURA = AP.ID_ASIGNATURA AND AP.DNI = ? AND AP.ID_CARRERA = ?");
					consultaSize.setString(1, clmaDni);
					consultaSize.setLong(2, clmaIdMiCarrera);
					rsSize = consultaSize.executeQuery();
					while(rsSize.next()){
						contador++;
					}
					//Se prepara la respuesta, que en este caso será un array
					ListarAsignaturas[] misAsignaturas = new ListarAsignaturas[contador];
					ListarAsignaturas miAsignatura;
					total = 0;
					while(rs.next()){
						miAsignatura = new ListarAsignaturas(rs.getLong(1), rs.getString(2), clmaIdMiCarrera);
						misAsignaturas[total] = miAsignatura;
						System.out.println(miAsignatura);
						total++;
					}
					//Envío de la respuesta
					response = new RespuestaListarAsignaturas(misAsignaturas);
					response.sendTo(dos);
					rs.close();
					rsSize.close();
					conexion.close();
					break;
				/*
				 * Se crea una clase para controlar la asistencia
				 */
				case Comandos.TCREARCLASE:
					ComandoCrearClase ccc = (ComandoCrearClase)comando;
					String cccHorario = ccc.getHorario();
					Long cccIdMiAsignatura = ccc.getIdMiAsignatura();
					try{
						Class.forName("org.sqlite.JDBC");
					}catch(Exception e){
						///OOOOOK
					}
					conexion = DriverManager.getConnection(dbURL, config.toProperties());
					consulta = conexion.prepareStatement("");
					rs = consulta.executeQuery();
					int numeroUltimaClase = rs.getInt(1);
					int numeroClaseActual = numeroUltimaClase + 1;
					StringBuilder sb = new StringBuilder();
					sb.append(cccIdMiAsignatura);
					sb.append("_");
					sb.append(numeroClaseActual);
					break;
				case Comandos.TPING:
					System.out.println("BBBBBBBBBBB");
					ComandoPing cp = (ComandoPing)comando;
					String cpNombre = cp.getNombre();
					System.out.println("Recibido: " + cpNombre);
					response = new RespuestaOK(TPING_ROK_RESPONSE);
					response.sendTo(dos);
					break;
				default:
					throw new RuntimeException(this + "[error]: command not recognized");
				}
			}catch(Exception e){
				System.err.printf("%s: serveclient: %s\n", this, e);
				Respuestas response = new RespuestaError(TADD_RERROR_RESPONSE);
				response.sendTo(dos);
			}
		}
	}
	
	public static class ControlClientThread implements Runnable{
		
		private Socket fd;
		private DataInputStream dis;
		private DataOutputStream dos;
		private ControlClientService service;
		
		public ControlClientThread(Servidor server, Socket fd){
			try{
				this.fd = fd;
				this.dis = new DataInputStream(new BufferedInputStream(this.fd.getInputStream()));
				this.dos = new DataOutputStream(new BufferedOutputStream(this.fd.getOutputStream()));
				this.service = new ControlClientService(server);
			}catch(Exception e){
				System.err.printf("%s: run: %s\n", this, e);
				close();
			}
		}

		@Override
		public void run() {
			service.serveClient(dis, dos);
			close();
		}
		
		public void close(){
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
	}
	
	private void clientControl(){
		while(!Thread.interrupted()){
			try{
				Socket clientChannel = this.clientSk.accept();
				this.threadsPool.execute(new ControlClientThread(this, clientChannel));
			}catch(SocketException e){
				if(!Thread.interrupted()){
					System.err.printf("%s: accept: %s\n", this, e);
				}else{
					return;
				}
			}catch(Exception e){
				System.err.printf("%s: accept: %s\n", this, e);
			}
		}
	}
	
	public static void main(String[] args){
		try{
			if (args.length == 0){
				Servidor server = new Servidor();
				server.start();
			}else{
				System.err.printf("%s[error]: Invalid arguments number\n", Servidor.class);
			}
		}catch(Exception e){
			System.err.printf("%s[error]: %s", Servidor.class, e);
		}
	}

}
