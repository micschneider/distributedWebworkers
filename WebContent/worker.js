var req = null;
var file = null;
var parentWorker = this;
var sendWorkerWs = null;
var resp = "LEER";
parentWorker.addEventListener("message", incomingMessage, false);

function incomingMessage(event)
{
	connectToSendWebsocket();
	if(sendWorkerWs != null && event.data.type == "filename")
	{
		file = event.data.content;
	}
	else
	{
		parentWorker.postMessage("NOT READY");
	}
}

function sendRequest(url) 
{
	req = initXMLHTTPRequest();
	if (req) 
	{
		req.onreadystatechange = onReadyState;
		req.open('GET',url,true);     // http-Methode, url, asynchron
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		req.setRequestHeader("Cache-Control", "no-cache");
		req.setRequestHeader("Pragma", "no-cache");
		req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
		req.send(null);
	 }
}

function initXMLHTTPRequest() 
{
	return new XMLHttpRequest();
}

function onReadyState() 
{
	if (req.readyState == 4) 
	{
		resp = req.responseText;
		sendWorkerWs.send(resp);
	}
}

function connectToSendWebsocket()
{
	var wsUri = "ws://localhost:8080/fhws.masterarbeit.distributedWebworkers/sendWebsocket";
	
	sendWorkerWs = new WebSocket(wsUri);
	
	sendWorkerWs.onopen = function()
					  {
							sendRequest(file);
					  }; // end function
	sendWorkerWs.onmessage = function(e)
				     	 {
							parentWorker.postMessage(e.data);
							sendWorkerWs.close();
				     	 };
		
	sendWorkerWs.onerror = function(evt)
				   	   {
							//writeToScreen("ERROR: " + evt.data);
				   	   }; //end function
}


							