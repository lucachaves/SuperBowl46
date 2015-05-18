package asim.processing.learning;
import processing.core.*;

import java.io.*;

/*
 * This processing sketch visualizes the twitter map of the super bowl 46, 2012
 * During the game, over half a million tweets were cached and processed to generate a file that 
 * contained over 18000 geotagged tweets
 * 
 * The tweets were than filtered further based on various tags to determine loyalties to a particular
 * team (NY Giants or the NE Patriots). These tweets are marked on the map in either RED or BLUE
 */

public class SuperBowlFanBase extends PApplet{
	
	final int MAP_WIDTH = 1024;
	final int MAP_HEIGHT = 572;
	
	String strMap = "usa_light.png";											//this is the path to the map of the USA
	String strPathFile = "final.dat";											//this is the path to the file that has all tweet info
	PImage imgMap;																//a PImage object that will be used to store the map
	MercatorMap mercatorMap;													//a mercator map that will be used to translate between earth coords and cartesian coords
	
	FileInputStream fStream;													//these objects are needed to read the file
	DataInputStream dStream;													//an input stream reader
	BufferedReader bReader;														//a buffered reader - actually reads the file
	
	int countLinesRead = 0;														//counts the number of entries in the file before no more data can be read
	boolean toRead = true;														//this flag determines if the reader should continue reading a file or not
	enum Team {PATS,GIANTS,NONE};												//enum to designate a team
	
	int countGiants = 0, countPats = 0, countGeneral = 0;
	int countUSA = 0, countIntl = 0;
	final int radiusTeams = 6, radiusGeneric = 4;
		
	PVector indianapolis = new PVector((float)38.65,(float)-88.72);
	/************************************************************************
	 * this routine will initialize the file reader 
	 ************************************************************************/
	public void initFileReader(){												
		
		try
		{
			fStream = new FileInputStream(strPathFile);
			dStream = new DataInputStream(fStream);
			bReader = new BufferedReader(new InputStreamReader(dStream));
		}
		catch(Exception e){
			System.out.println("Error opening input stream");
		}
	}
	
	/************************************************************************
	 * This method will return a PVector that contains the latitude and longitude
	 * in the given string
	 * @param line - string of format <team>%<lat>; <long>
	 * @return a vector containing <lat>,<long> values for the above string
	 ************************************************************************/
	private PVector getCoordinates(String line){
		
		final String delimLat = "%";
		final String delimLon = "; ";
		
		PVector toReturn = null;
		
		int indexBegLat = line.indexOf(delimLat) + 1;
		int indexEndLat = line.indexOf(delimLon);
		int indexBegLon = line.indexOf(delimLon) + 1;
		int indexEndLon = line.length()-1;
		
		String strLat = line.substring(indexBegLat, indexEndLat);
		String strLon = line.substring(indexBegLon, indexEndLon);
		Float fLat = new Float(strLat);
		Float fLon = new Float(strLon);
		
		toReturn = new PVector(fLat,fLon);
		return toReturn;
	}
	
	/************************************************************************
	 * Returns the team represented by the line
	 * @param line - string of format <team>%<lat>; <long>
	 * @return an enum representing the team indicated in the string argument
	 ************************************************************************/
	private Team getTeam(String line){
		char cTeam = line.charAt(0);
		switch(cTeam){
			case 'g': return Team.GIANTS;
			case 'p': return Team.PATS;
			default : return Team.NONE;
		}
		
	}
	
	/************************************************************************
	 * setup routine for sketch
	 ************************************************************************/
	public void setup(){
		
		initFileReader();
		size(MAP_WIDTH,MAP_HEIGHT,P3D);
		smooth();
		frameRate(100);
		//the mercator object needs to know the dimensions of the X,Y plane and corresponding boundary in earth coords
		mercatorMap = new MercatorMap(	
										MAP_WIDTH,								//Width of MAP image
										MAP_HEIGHT,								//Height of MAP image
									 	(float)(49.7245),						//top Latitude
									 	(float)(23.4027),						//bot Latitude
									 	(float)(-125.8155),						//left Longitude
									 	(float)(-66.0498)						//right Longitude
									 );
		
		
		imgMap = loadImage(strMap);
		background(imgMap);		
	}
	
	/************************************************************************
	 * Draw routine for the sketch
	 ************************************************************************/
	public void draw(){		
		
		//do this only if there is more data to read from the file
		if(toRead == true){
			
			try{
					countLinesRead++;											//count the line
					int radius = 4;
					String line = bReader.readLine();							//try to read the line
					PVector coords = getCoordinates(line);						//grab the lat,long values
					PVector pointToPlot = mercatorMap.getScreenLocation(coords);//convert lat,long values to x,y
					Team team = getTeam(line);									//grab the team name from this line
					float zIndex = 0;
					
					fill(250,0,120,150);										//set the color to light pink
					
					if(pointToPlot != null){									//
						strokeWeight(0);
						ellipse(pointToPlot.x,pointToPlot.y,radius,radius);
					}					
			}					
			catch(IOException eof){
				toRead = false;				
				
			}
			catch(Exception misc){
			}
			
		} //end outer most if block
		
	}
		
}
