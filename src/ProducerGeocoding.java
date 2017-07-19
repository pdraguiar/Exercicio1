import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class ProducerGeocoding {
	
	//inserir o caminho e nome do txt que será criado com os registros do taxi desejado
	public static final String CAMINHO_TXT_TAXI_ESPECIFICO = "<<whatever>>/taxi-7659.txt";
	
	//Gerar google maps key - https://developers.google.com/maps/documentation/javascript/get-api-key?hl=pt-br#key
	public static final String GOOGLE_KEY = "<<inserir seu google key>>";

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
			Destination destination = session.createQueue("ROTA-7659");
			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			int i = 1;
			try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_TXT_TAXI_ESPECIFICO))) {
				String line = br.readLine();

				while (line != null) {
					String[] split = line.split(",");

					if (split != null && split.length == 4) {
						Integer taxiID = (split[0] != null) ? Integer.valueOf(split[0]) : null;
						String longitude = (split[2] != null) ? split[2] : null;
						String latitude = (split[3] != null) ? split[3] : null;

						if (taxiID != null && latitude != null && longitude != null) {
							// Essa parte, me baseei no código disponível aqui: http://www.smarttutorials.net/google-maps-reverse-geocoding-in-java
							
							//URL onde o serviço do google maps fica disponivel
							String strUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + ","
									+ longitude + "&key="+GOOGLE_KEY;
							URL url = new URL(strUrl);
							
							// making connection (faz requisição http - GET)
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();
							conn.setRequestMethod("GET");
							conn.setRequestProperty("Accept", "application/json");
							if (conn.getResponseCode() != 200) {
								throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
							}

							// Reading data's from url
							BufferedReader brGoogle = new BufferedReader(new InputStreamReader((conn.getInputStream())));

							String output;
							String out = "";
							
							while ((output = brGoogle.readLine()) != null) {
								out += output;
							}
							
							// Converting Json formatted string into JSON object
							JSONObject json = (JSONObject) JSONSerializer.toJSON(out);
							JSONArray results=json.getJSONArray("results");
							JSONObject rec = results.getJSONObject(0);
							
							if (rec != null && rec.size() > 0) {
								String formatted_address = rec.getString("formatted_address");
								String textoFinal = "Data/Hora: "+ split[1] + "\n" + "Endereço:" + formatted_address+"\n";
								Thread.sleep(3000);
								TextMessage msg = session.createTextMessage(textoFinal);
								producer.send(msg);
							}
							
							conn.disconnect();
						}

					}

					line = br.readLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}