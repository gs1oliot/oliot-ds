/*************************************************************************************************
 * EventRegistration
 *
 * @desc	Register events generated from EPCIS
 * @author  Kiwoong Kwon         
 *************************************************************************************************/          


package dsClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import dsClient.RegistrationFormat;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.gson.Gson;

public class EventRegistration {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if(args.length!=4)
		{
			System.out.println("Arguments: gs1 ID, EPCIS address, latitude, and longitude");
			return ;
		}
		
		Event_Registration(args[0], args[1], new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), 
				Double.parseDouble(args[2]), Double.parseDouble(args[3]));
	}
	public static void Event_Registration(String EPC, String EPC_Addr, String cur_time, double lat, double lon) {
		//make client connection
		Client client =  new TransportClient()
		//'localhost' should be changed to actual address of discovery service
			.addTransportAddress(new InetSocketTransportAddress("localhost", 9300)); 
		
		//make registration message
		RegistrationFormat reg = new RegistrationFormat();
		reg.EPC = EPC;
		reg.EPCIS = EPC_Addr;
		reg.timestamp = cur_time;
		reg.location.lat = lat;
		reg.location.lon = lon;
		
		//make elasticsearch query
		Gson gson = new Gson();
		String json = gson.toJson(reg);
		System.out.println(json);
		client.prepareIndex("discoveryservice", "geo")
		        .setSource(json)
		        .execute()
		        .actionGet();
		client.close();
	}
}
