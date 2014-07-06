/*************************************************************************************************
 * Oliot Discovery Service
 *
 * @desc	Search the physical location of things in elasticsearch and shows the locations 
 *          on the google maps
 * @author  Kiwoong Kwon         
 *************************************************************************************************/          

package geoDS.client;

import com.google.code.gwt.geolocation.client.Coordinates;
import com.google.code.gwt.geolocation.client.Geolocation;
import com.google.code.gwt.geolocation.client.Position;
import com.google.code.gwt.geolocation.client.PositionCallback;
import com.google.code.gwt.geolocation.client.PositionError;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.Timer;
import com.google.maps.gwt.client.Animation;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OliotDiscoveryService implements EntryPoint {
    

	/*******************************************************************
	* Variables
	******************************************************************/
	
	static SimplePanel map_simplePanel = new SimplePanel();
    static double curLat = 36.350412;
    static double curLon = 127.38454700000001;


	/*******************************************************************
	* functions
	******************************************************************/    
    
	public void onModuleLoad() {
	    
		//make UI using Designer
		LayoutPanel main_layoutPanel = new LayoutPanel();
	    main_layoutPanel.setSize("100%", "100%");
	    

	    RootLayoutPanel.get().add( main_layoutPanel ) ;
	    main_layoutPanel.add(map_simplePanel);
	    main_layoutPanel.setWidgetLeftWidth(map_simplePanel, 20.0, Unit.PCT, 60.0, Unit.PCT);
	    main_layoutPanel.setWidgetTopHeight(map_simplePanel, 30.0, Unit.PCT, 50.0, Unit.PCT);
	    
	    LayoutPanel sub_layoutPanel = new LayoutPanel();
	    main_layoutPanel.add(sub_layoutPanel);
	    main_layoutPanel.setWidgetLeftWidth(sub_layoutPanel, 20.0, Unit.PCT, 60.0, Unit.PCT);
	    main_layoutPanel.setWidgetTopHeight(sub_layoutPanel, 18.0, Unit.PCT, 12.0, Unit.PCT);
	    
	    Label EPCID_label = new Label("GS1 ID");
	    EPCID_label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    sub_layoutPanel.add(EPCID_label);
	    EPCID_label.setHeight("");
	    sub_layoutPanel.setWidgetLeftWidth(EPCID_label, 0.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(EPCID_label, 5.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    final TextBox epc_textBox = new TextBox();
	    //epc_textBox.addClickHandler(new ClickHandler() {
	    //	public void onClick(ClickEvent event) {
	    //		epc_textBox.setText("urn:epc:id:sgtin:0057000.123780.7788");
	    //	}
	    //});
	    epc_textBox.setText("Insert GS1 ID you wish to find");
	    epc_textBox.setAlignment(TextAlignment.CENTER);
	    sub_layoutPanel.add(epc_textBox);
	    sub_layoutPanel.setWidgetLeftWidth(epc_textBox, 0.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(epc_textBox, 54.0, Unit.PCT, 45.0, Unit.PCT);

	    Label StartTime_label = new Label("From\n(yyyymmddhhmmss)");
	    StartTime_label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    sub_layoutPanel.add(StartTime_label);
	    StartTime_label.setHeight("");
	    sub_layoutPanel.setWidgetLeftWidth(StartTime_label, 23.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(StartTime_label, 5.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    final TextBox StartTime_textBox = new TextBox();
	    //StartTime_textBox.addClickHandler(new ClickHandler() {
	    //	public void onClick(ClickEvent event) {
	    //		StartTime_textBox.setText("20130910000000");
	    //	}
	    //});
	    StartTime_textBox.setText("Insert start point of time period");
	    StartTime_textBox.setAlignment(TextAlignment.CENTER);
	    sub_layoutPanel.add(StartTime_textBox);
	    sub_layoutPanel.setWidgetLeftWidth(StartTime_textBox, 23.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(StartTime_textBox, 54.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    Label EndTime_label = new Label("To\n(yyyymmddhhmmss)");
	    EndTime_label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    sub_layoutPanel.add(EndTime_label);
	    EndTime_label.setHeight("");
	    sub_layoutPanel.setWidgetLeftWidth(EndTime_label, 46.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(EndTime_label, 5.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    final TextBox EndTime_textBox = new TextBox();
	    //EndTime_textBox.addClickHandler(new ClickHandler() {
	    //	public void onClick(ClickEvent event) {
	    //		EndTime_textBox.setText("20141030165000");
	    //	}
	    //});
	    EndTime_textBox.setText("Insert end point of time period");
	    EndTime_textBox.setAlignment(TextAlignment.CENTER);
	    sub_layoutPanel.add(EndTime_textBox);
	    sub_layoutPanel.setWidgetLeftWidth(EndTime_textBox, 46.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(EndTime_textBox, 54.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    Label Range_label = new Label("Distance\n(km)");
	    Range_label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    sub_layoutPanel.add(Range_label);
	    Range_label.setHeight("");
	    sub_layoutPanel.setWidgetLeftWidth(Range_label, 69.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(Range_label, 5.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    final TextBox Range_textBox = new TextBox();
	    //Range_textBox.addClickHandler(new ClickHandler() {
	    //	public void onClick(ClickEvent event) {
	    // 		Range_textBox.setText("");
	    //	}
	    //});
	    Range_textBox.setText("Insert search boundary");
	    Range_textBox.setAlignment(TextAlignment.CENTER);
	    sub_layoutPanel.add(Range_textBox);
	    sub_layoutPanel.setWidgetLeftWidth(Range_textBox, 69.0, Unit.PCT, 20.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(Range_textBox, 54.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    Label lblGeodiscoveryService = new Label("Oliot Discovery Service");
	    lblGeodiscoveryService.setStyleName("gwt-h1");
	    lblGeodiscoveryService.setDirectionEstimator(true);
	    lblGeodiscoveryService.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	    main_layoutPanel.add(lblGeodiscoveryService);
	    main_layoutPanel.setWidgetLeftWidth(lblGeodiscoveryService, 10.0, Unit.PCT, 80.1, Unit.PCT);
	    main_layoutPanel.setWidgetTopHeight(lblGeodiscoveryService, 10.0, Unit.PCT, 8.0, Unit.PCT);
	   
	    
	    Button btnSearch = new Button("Search");
	    //This code is executed when the search button is clicked 
	    btnSearch.addClickHandler(new ClickHandler() {
	    	public void onClick(ClickEvent event) {
	    		//find physical locations from elasticsearch
	    		searchLocRunner(epc_textBox.getText(), StartTime_textBox.getText(), EndTime_textBox.getText(),
	    				curLat, curLon, Double.parseDouble(Range_textBox.getText()));
	    		//to wait asynchronous call is finished
	    		Timer t = new Timer() {
	    			@Override
	    			public void run(){
	    				String check= getCheckRunner();
	    	    		if(check.equals("finish"))
	    	    		{
	    	    			//if synchronous call is finished, the timer is canceled
	    	    			cancel();
	    	    			//show physical locations on the google maps
	    	    			show_locations(epc_textBox.getText(),Double.parseDouble(Range_textBox.getText()));
	    	    		}
	    			}
	    		};
	    		t.scheduleRepeating(10);
	    	}
	    });
	    sub_layoutPanel.add(btnSearch);
	    sub_layoutPanel.setWidgetLeftWidth(btnSearch, 92.0, Unit.PCT, 8.0, Unit.PCT);
	    sub_layoutPanel.setWidgetTopHeight(btnSearch, 54.0, Unit.PCT, 45.0, Unit.PCT);
	    
	    
	    if (Geolocation.isSupported()) {
	        // get your current Geo location
	    	Geolocation geo = Geolocation.getGeolocation();
	    	geo.getCurrentPosition(new PositionCallback() {
	    	    public void onFailure(PositionError error) {
	    	        // default location is set when handle fails 
	    	        drawMap(36.350412, 127.384547);
	    	    }
	    	    public void onSuccess(Position position) {
	    	    	// current location is set when handle suceeds
	    	        Coordinates coords = position.getCoords();
	    	        System.out.println(coords.getLatitude()+" "+coords.getLongitude());
	    	        curLat = coords.getLatitude();
	    	        curLon = coords.getLongitude();
	    	        drawMap(curLat,curLon);
	    	    }
	    	});
	    }
	    else
	        drawMap(36.350412, 127.384547);
	    
	    
	}
	

	//show current physical location on the google maps
	public static void drawMap(double lat, double lng)
	{
	    LatLng initLatLng = LatLng.create(lat, lng);
	    //configure map options
	    MapOptions myOptions = MapOptions.create();
	    myOptions.setZoom(15.0);
	    myOptions.setCenter(initLatLng);
	    myOptions.setMapTypeId(MapTypeId.ROADMAP);
	    
	    GoogleMap map = GoogleMap.create(map_simplePanel.getElement(), myOptions);
	    
	    //configure marker options
	    MarkerOptions newMarkerOpts = MarkerOptions.create();
	    newMarkerOpts.setPosition(initLatLng);
	    newMarkerOpts.setMap(map);
	    newMarkerOpts.setTitle("Current Location\n"+"Lat: " + lat + "\n" + "Lon: " + lng);
	    newMarkerOpts.setClickable(true);
	    Marker.create(newMarkerOpts);
		
	};
	
	//show physical location of things on the google maps
	public static void show_locations(String epc, double distance)
	{
	    double zoom = 13.0;
	    
	    //configure zoom size
	    if(distance>5.0 && distance<=8.0)
    	{
    		zoom=12;
    	}
    	else if(distance>8.0 && distance<=15.0)
    	{
    		zoom=11;
    	}
    	else if(distance>15.0 && distance<=20.0)
    	{
    		zoom=10;
    	}
    	else if(distance>20.0)
    	{
    		zoom=9;
    	}
	    
	    
		JsArray<JsArrayString> locations = getLocations();
		
		LatLng initLatLng = LatLng.create(curLat, curLon);
		
		//configure map options
	    MapOptions myOptions = MapOptions.create();
	    myOptions.setZoom(zoom);
	    myOptions.setCenter(initLatLng);
	    myOptions.setMapTypeId(MapTypeId.ROADMAP);
	    
	    GoogleMap map = GoogleMap.create(map_simplePanel.getElement(), myOptions);
	    
	    //configure marker options
	    MarkerOptions newMarkerOpts;
		newMarkerOpts = MarkerOptions.create();
	    newMarkerOpts.setPosition(initLatLng);
	    newMarkerOpts.setMap(map);
	    newMarkerOpts.setTitle("Current Location\n"+"Lat: " + curLat + "\n" + "Lon: " + curLon);
	    newMarkerOpts.setClickable(true);
	    newMarkerOpts.setAnimation(Animation.DROP);
	    Marker.create(newMarkerOpts);
	    locations.get(0);
	    
	    //add markers corresponding to physical locations of things
		int i=0;
		for(i=0;i<locations.length();++i)
		{
			addMarker(i,map,epc);
		}

	}
	
	//show marker on the goolge maps
	public static void addMarker(int i, GoogleMap map, String epc)
	{
		double lat = getPointRunner(i,"lat");
		double lon = getPointRunner(i,"lon");
		LatLng desiredPoint = LatLng.create(lat,lon);
		MarkerOptions newMarkerOpts = MarkerOptions.create();
		newMarkerOpts.setPosition(desiredPoint);
		newMarkerOpts.setMap(map);
		newMarkerOpts.setTitle(epc + "\n" + "Lat: " + lat + "\n" + "Lon: " + lon );
		newMarkerOpts.setClickable(true);
		newMarkerOpts.setAnimation(Animation.DROP);
		Marker.create(newMarkerOpts);
	}
	
	

	/*******************************************************************
	* functions defined in javascript
	******************************************************************/
	
	//search physical locations in elasticsearch
	public static native void searchLocRunner(String epc, String from, String to, double lat, double lon, double distance)/*-{
	 $wnd.search_GeoLocation(epc,from,to,lat,lon,distance)
	}-*/;
	
	//get location array defined in javascript
	public static native JsArray<JsArrayString> getLocations()/*-{
		return $wnd.locations;
	}-*/;
	
	//get latitude and longitude from location array 
	public static native double getPointRunner(int index, String value)/*-{
		return $wnd.getPointFromArray(index, value);
	}-*/;
	
	//get check to know asynchronous call is finished
	public static native String getCheckRunner()/*-{
	return $wnd.check;
	}-*/;
	
}
