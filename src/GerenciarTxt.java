import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

public class GerenciarTxt {
	
	//inserir o caminho do txt com os 10 milhões de registros
	public static final String CAMINHO_TXT = "<<whatever>>/taxi.txt";
	//inserir o caminho e nome do txt que será criado com os registros do taxi desejado
	public static final String CAMINHO_TXT_TAXI_ESPECIFICO = "<<whatever>>/taxi-7659.txt";

	public static void main(String[] args) {
		try {
			int i = 1;
			Set<String> hashSet = new HashSet<>();
			
			try (BufferedReader br = new BufferedReader(new FileReader(CAMINHO_TXT))) {
				String line = br.readLine();
				
				while (line != null) {
					String[] split = line.split(",");
					
					if (split != null && split.length == 4) {
						Integer taxiID = (split[0] != null) ? Integer.valueOf(split[0]) : null;
						String longitude = (split[2] != null) ? split[2] : null;
						String latitude = (split[3] != null) ? split[3] : null;
						
						if (taxiID != null &&
							latitude != null &&
							longitude != null &&
							taxiID == 7659 &&
							!hashSet.contains(line)) {
							
							System.out.println(split[0]+" > "+i+"\n");
							
							try (FileWriter writer = new FileWriter(CAMINHO_TXT_TAXI_ESPECIFICO, true)) {
							    writer.write(line+"\n");
							    writer.flush();
							 }
							i++;
							
							hashSet.add(line);
						}
						
						line = br.readLine();
					}
				}
			}
		} catch (Exception e) {

		}
	}

}
