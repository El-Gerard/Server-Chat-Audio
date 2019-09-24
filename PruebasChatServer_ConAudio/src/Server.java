import java.awt.BorderLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Server {

	//----------------------------------Clase principal
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MarcoServidor mimarco = new MarcoServidor();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}//-------------------------------------------------------**
}

//----------------------------------Clase para ejecutar las ordenes de conexion
class MarcoServidor extends JFrame implements Runnable {

	private JTextArea areatexto;

	//----------------------------------método para dibujar la ventana
	public MarcoServidor() {

		setBounds(1200, 300, 280, 350);
		JPanel milamina = new JPanel();
		milamina.setLayout(new BorderLayout());
		areatexto = new JTextArea();
		milamina.add(areatexto, BorderLayout.CENTER);
		add(milamina);
		setVisible(true);
		//Ejecución del hilo
		Thread mihilo = new Thread(this);
		mihilo.start();
	}//-----------------------------------------------------**

	//---------------------------------------Hilo nativo para intercambio de datos
	@Override
	public void run() {
		// TODO Auto-generated method stub

		try {
			ServerSocket servidor = new ServerSocket(9999);
			String nick, ip, mensaje;
			ArrayList<String> listaIP = new ArrayList<String>();
			Paquete paquete;

			System.out.println("Servidor en línea...");
			
			while (true) {
				
				Socket miSocket = servidor.accept();
				
				ObjectInputStream paquete_datos = new ObjectInputStream(miSocket.getInputStream());
				// Se asigna el objeto recibido a la instancia creada para desglozarla
				paquete = (Paquete) paquete_datos.readObject();
				// Se asigna la información recibida en los strings creados
				nick = paquete.getNick();
				ip = paquete.getIp();
				mensaje = paquete.getMensaje();

				System.out.println("Mensaje que llega" + mensaje);

				//miSocket.close();
				
				//Condicional que determina si la conexión entrante es un mensaje o un nuevo cliente
				
				if (!mensaje.equals(" online")) {

					// Se muestra por pantalla lo recibido en el servidor
					areatexto.append("\n" + nick + ": " + mensaje + " para: " + ip);
					// Se cera un socket de envío de datos
					Socket enviaDestinatario = new Socket(ip, 9090);
					// Se crea un flujo de SALIDA de datos
					ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
					// Se envía entonces el objeto hacia el cliente de destino
					paqueteReenvio.writeObject(paquete);
					// Cierre de flujos y conexion
					enviaDestinatario.close();
					paqueteReenvio.close();
					miSocket.close();

				} else {
					// -------------------------------------Detectar usuarios online

					// Toma la direccion del cliente que abre la aplicación
					InetAddress localizacion = miSocket.getInetAddress();
					// Almacena la direccion del cliente conectado como String
					String ipRemota = localizacion.getHostAddress();
					System.out.println("Nuevo cliente ingresado: " + ipRemota);
					areatexto.append("\nNuevo cliente en línea: " + ipRemota);
					// Añadir la ip de conexión
					listaIP.add(ipRemota);
					// Crea un array con la lista de Ip's en la clase del paquete
					paquete.setIps(listaIP);
					areatexto.append("\nClientes disponibles: ");
					//Se cicla el array con las ip y las envía a los usuarios conectados
					for (String z : listaIP) {
						
						areatexto.append("\n" + z);
						System.out.println("Clientes conectados: " + z);

						Socket enviaDestinatario = new Socket(z, 9090);
						// Se crea un flujo de SALIDA de datos
						ObjectOutputStream paqueteReenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream());
						// Se envía entonces el objeto hacia el cliente de destino
						paqueteReenvio.writeObject(paquete);
						// Cierre de flujos y conexion
						enviaDestinatario.close();
						paqueteReenvio.close();
						miSocket.close();
					}
					// -----------------------------------------------------**
				}
				
			}
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
