class Tweet:
	""" this class models a tweet """
	
	def __init__(self):
	
		self.message = ""
		self.geo = ""
		self.retweetCnt = 0
		self.timestamp = ""
	
	def setData(self,time,message,geo,retweetCnt):
		
		if time is None: time = "null"
		if message in [None,""]: message = "null"
		if geo is None: geo = "null"
		if retweetCnt is None: retweetCnt = 0
		
		self.timestamp = time
		self.geo = str(geo)
		self.retweetCnt = str(retweetCnt)
		self.message = message
		
	def getXML(self):
		
		strStart = "<tweet>"
		strEnd = "</tweet>"
		
		strFldTime = "<time>"+self.timestamp+"</time>"
		strFldGeo = "<geo>"+self.geo+"</geo>"
		strFldCnt = "<recount>"+self.retweetCnt+"</recount>"
		strFldMsg = "<message>"+self.message+"</message>"
		
		return strStart+strFldTime+strFldGeo+strFldCnt+strFldMsg+strEnd
		
#------------------------------------------------------------------------------

class TwitterStreamGrabber:
	
	""" This class grabs the output from twitter streaming API """
	
	countGeoAvailable = 0
	countTotalTweets = 0

	def __init__(self,lstKeywords,userName,userPwd,pathFile="output.xml"):
		
		import urllib,pycurl
		
		self.tweetObj = Tweet()
		
		strKeywords = (",").join(lstKeywords)
		self.keywords = urllib.quote_plus(strKeywords)
		self.url = "https://stream.twitter.com/1/statuses/filter.json"
		self.uname = userName
		self.passwd = userPwd
		self.outfile = pathFile
		
		self.grabber = pycurl.Curl()
		self.grabber.setopt(pycurl.URL,self.url)
		self.grabber.setopt(pycurl.USERPWD,self.uname+":"+self.passwd)
		self.grabber.setopt(pycurl.POSTFIELDS,"track="+self.keywords)
		self.grabber.setopt(pycurl.WRITEFUNCTION,self.messageHandler)
		self.grabber.perform()
		
	
	def messageHandler(self,data):
		
		import json,time,traceback,codecs				
		
		try:
			jTweet = json.loads(data)
			
			fldTimeStamp = jTweet[u'created_at']
			fldRetweetCnt= jTweet[u'retweet_count']
			fldLocation = jTweet[u'geo']
			fldContent = jTweet[u'text']
			fldPlace = jTweet[u'place']
			
			self.tweetObj.setData(fldTimeStamp, fldContent, fldLocation, fldRetweetCnt)
			
			if(fldLocation <> None):
				self.countGeoAvailable += 1
				print "\a"
			self.countTotalTweets += 1
			
			if(fldLocation<>None):			
				writer = codecs.open(self.outfile,'a',"utf-8")
				writer.write(self.tweetObj.getXML()+"\n")
				writer.close()
				print time.asctime() , "Geo/Total --> " , self.countGeoAvailable , "/" , self.countTotalTweets

						
		except:
			#print time.asctime() + "--> Exception occured " 
			#traceback.print_exc()
			writer = open('log.txt','a')
			writer.write(("<log>\n").replace("%s",time.asctime()))
			writer.write(data + "\n")
			writer.close()
			
			
	def readCounters(self):
		return (countGeoAvailable,countTotalTweets)
			
#------------------------------------------------------------------------------

if __name__ == "__main__":
	
	import time
	
	uName = "asimmittal"
	uPass = "twitterpass"
	outfile = "out_game.xml"
	keywords = ["#superbowl","#Giants", "#Patriots", "#Pats"]	
	
	clearLogFile = open("log.txt","w")
	clearLogFile.close()
	clearLogFile = open(outfile,"w")
	clearLogFile.close()
	
	superBowlGrabber = TwitterStreamGrabber(keywords,uName,uPass,outfile);
						
