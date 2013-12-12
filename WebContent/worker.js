var req = null;
var file = "";
var code = "";
var waiterId = null;
var localWorker = this;
var sendWorkerWs = null;
var sendWorkerStat = 0;

localWorker.addEventListener("message", localWorker.incomingMessage, false);
localWorker.connectToSendWebsocket();

function incomingMessage(event)
{
	var json = JSON.parse(event.data);
	switch(json.type)
	{
		case ("filename"):
		{
			localWorker.file = json.content;
		
			if(localWorker.sendWorkerStat > 0)
			{
				localWorker.sendRequest(localWorker.file);
			}
			break;
		}
		case ("waiterId"):
		{
			localWorker.waiterId = json.content;
			
			if(localWorker.code != "")
			{
				localWorker.sendToSendWebsocket("code", localWorker.code);	
			}
			break;
		}
		default:
		{
			localWorker.postMessage("Unknown message type");
		}
	}
}

function sendRequest(url) 
{
	localWorker.req = initXMLHTTPRequest();
	if (localWorker.req) 
	{
		localWorker.req.onreadystatechange = onReadyState;
		localWorker.req.open('GET',url,true);     // http-Methode, url, asynchron
		localWorker.req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		localWorker.req.setRequestHeader("Cache-Control", "no-cache");
		localWorker.req.setRequestHeader("Pragma", "no-cache");
		localWorker.req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
		localWorker.req.send(null);
	 }
}

function initXMLHTTPRequest() 
{
	return new XMLHttpRequest();
}

function onReadyState() 
{
	if (localWorker.req.readyState == 4) 
	{
		localWorker.code = req.responseText;
		if(localWorker.waiterId != null)
		{
			localWorker.sendToSendWebsocket("code", localWorker.code);
		}
	}
}

function connectToSendWebsocket()
{
	var wsUri = "ws://localhost:8080/fhws.masterarbeit.distributedWebworkers/sendWebsocket";
	
	localWorker.sendWorkerWs = new WebSocket(wsUri);
	
	localWorker.sendWorkerWs.onopen = function()
	{
		localWorker.sendWorkerStat = 1;
		if(localWorker.file != "")
		{
			localWorker.sendRequest(localWorker.file);
		}
	}; // end function
	
	localWorker.sendWorkerWs.onmessage = function(event)
	{
		localWorker.postMessage(event.data);
	};
		
	sendWorkerWs.onerror = function(event)
	{
	}; //end function
}

function sendToSendWebsocket(type, content)
{
	var data = content.replace(/\r|\n/g, " ");
	data = '{"type":"' + type + '","content":"' + data + '","waiterId":"' + localWorker.waiterId + '"}';
	localWorker.sendWorkerWs.send(data);
}

							