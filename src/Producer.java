import java.io.BufferedReader;
import java.io.FileReader;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {
	//inserir o caminho do txt com os 10 milhões de registros
	public static final String CAMINHO_TXT = "<<whatever>>/taxi.txt";

	public static void main(String[] args) {
		try {
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			// Create a Connection
			Connection connection = connectionFactory.createConnection();
			connection.start();
			// Create a Session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue("UPE-POLI");
			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			int i = 1;
			try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_TXT))) {
				String line = br.readLine();

				while (line != null) {
					TextMessage msg = session.createTextMessage(line + " -> linha: " + i);
					producer.send(msg);
					Thread.sleep(1000);
					line = br.readLine();
					i++;
				}
			}
		} catch (Exception e) {

		}
	}

}
