/**
 * 
 */
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
/**
 * @author Rayaan Choudri
 * @File CityWeatherOwl.java
 * @Purpose This program takes a Canadian city as input and takes a weather reading 
 * 			from http://openweathermap.org/api of the next 3 - 6 hours and displays the interpretation on the console. 
 * @Assumptions 6 hours ahead were sufficient. 
 * @Instructions to run in command line, type: java -jar CityWeatherOwl.jar toronto
 * 				 or any other city. The .jar file is in the src folder of this project. 
 */				
public class CityWeatherOwl {

	/**
	 * @param args
	 */
	 
	public static void main(String[] args) {
		String API_URL = "http://api.openweathermap.org/data/2.5/forecast?q=";
		//Retrieve city name from command line arguments 
		if (0 < args.length) {
			String city = new String(args[0]);
			API_URL += city + ",can&appid=c2e8d78c8a3e7c873766e12af9de946c";
		} else {
		   System.err.println("Invalid arguments count:" + args.length);
		}
			try {
				//Open the URL for the weather API
				URL url = new URL(API_URL);
				URLConnection connection = url.openConnection();
				
				//Reading JSON file into a buffer
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inLine;
				StringBuilder inStream=new StringBuilder();
				while ((inLine = in.readLine()) != null)
						inStream.append(inLine);  
				
				//Parse the JSON Buffer
				JSONParser parser = new JSONParser();
				JSONObject root = (JSONObject) parser.parse(inStream.toString());
				JSONArray list = (JSONArray)root.get("list");
				
				//Weather for next 3 hours
				JSONObject listFields1 = (JSONObject) list.get(0);//can test 0-38 of each 3 hour interval here
				JSONObject main1 = (JSONObject) listFields1.get("main");
				JSONArray weather1 = (JSONArray) listFields1.get("weather");
				
				/*JSONObject cloud1 = (JSONObject) listFields1.get("cloud"); we don't compare these since they don't matter according to requirement specs
				JSONObject rain1 = (JSONObject) listFields1.get("rain");
				JSONObject snow1 = (JSONObject) listFields1.get("snow");*/
				
				//Weather for next 6 hours
				JSONObject listFields2 = (JSONObject) list.get(1);//can test 0-38 of each 3 hour interval
				JSONObject main2 = (JSONObject) listFields2.get("main");
				JSONArray weather2 = (JSONArray) listFields2.get("weather");
				JSONObject isclear = (JSONObject) weather2.get(0);
				JSONObject cloud2 = (JSONObject) listFields2.get("clouds");
				JSONObject rain2 = (JSONObject) listFields2.get("rain");
				JSONObject snow2 = (JSONObject) listFields2.get("snow");
				
				//Comparing temperature between next 3-6 hours
				//Assuming even the slightest change in temperature 
				//For a 1 degree threshold, we could add something like:
				//if((double)main1.get("temp")-(double)main2.get("temp")) < abs(1))
				if((double)main1.get("temp")<(double)main2.get("temp"))
					System.out.printf("UP");
				else if((double)main1.get("temp")>(double)main2.get("temp"))
					System.out.printf("DOWN");
				else 
					System.out.printf("SAME");
	
				if(isclear.get("main").equals("Clear"))
					System.out.printf("\tSteady Green\n");
				if((long)cloud2.get("all")!=0)//by interpreting the json file, any value above 0 is cloudy
					System.out.printf("\tSteady Red\n");
				if(rain2!=null){//if rain id not initialized in json, it is not raining{
					if(rain2.get("3h")!=null)
						System.out.printf("\tFlashing Red\n");
				}
				if(snow2!=null){//since snow is not always a field in json doc, json doc is inconsistent
					if(snow2.get("3h")!=null)
						System.out.printf("\tFlashing White\n");
				}
			}
				catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (org.json.simple.parser.ParseException e) {
					e.printStackTrace();
				}   
}
}
