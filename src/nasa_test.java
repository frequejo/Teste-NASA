import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class nasa_test {

    public static void main(String[] args) throws IOException {
        String inputArgs = (args.length > 0) ? args[0] : "";
        try {
            double averageTemperature = get_average_temperature(inputArgs);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("averageTemperature", averageTemperature);
            System.out.println(jsonObject.toString());
        } catch (Exception e) {
            System.out.println("error with api connection");
        }
    }

    private static double get_average_temperature(String SOLEscolhidaString) throws IOException {
        String mars_temperatures = get_mars_data();

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(mars_temperatures);

        JsonArray jsonArr = jsonObject.getAsJsonArray("sol_keys");
        ArrayList<String> SolKeyList = new Gson().fromJson(jsonArr, ArrayList.class);
        double average_temperature = 0.0;
        if (SOLEscolhidaString == null || SOLEscolhidaString == "") {
            for (String sol_key : SolKeyList) {
                average_temperature += jsonObject.getAsJsonObject(sol_key).getAsJsonObject("AT").get("av").getAsFloat();
            }
            average_temperature = average_temperature/SolKeyList.size();
        } else {
            if (jsonObject.getAsJsonObject(SOLEscolhidaString) != null){
                average_temperature = jsonObject.getAsJsonObject(SOLEscolhidaString).getAsJsonObject("AT").get("av").getAsFloat();
            } else{
                average_temperature = 0;
            }
        }
        return average_temperature;
    }

    public static String get_mars_data() throws IOException {
        String api_key = "YGexxLfmpyBLeWM0fp1vzvgeQCC2zR3KR3qABw34";
        StringBuffer content = new StringBuffer();
        URL url = new URL("https://api.nasa.gov/insight_weather/?api_key="+api_key+"&feedtype=json&ver=1.0");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        
        //Get Response  
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine)
            //.append("\n")
            ;
        }
        //System.out.println(content);
        in.close();
        con.disconnect();
        
        return content.toString();
    }
}