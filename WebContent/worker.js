var req = null;
var file = "";
var code = "";
var waiterId = null;
var distributedWorker = this;
var sendWorkerWs = null;
var sendWorkerStat = 0;

distributedWorker.addEventListener("message", distributedWorker.incomingMessage, false);
distributedWorker.connectToSendWebsocket();

function incomingMessage(event)
{
	var json = JSON.parse(event.data);
	switch(json.type)
	{
		case ("filename"):
		{
			distributedWorker.file = json.content;
		
			if(distributedWorker.sendWorkerStat > 0)
			{
				distributedWorker.sendRequest(distributedWorker.file);
			}
			break;
		}
		case ("waiterId"):
		{
			distributedWorker.waiterId = json.content;
			
			if(distributedWorker.code != "")
			{
				distributedWorker.sendToSendWebsocket("code", distributedWorker.code);	
			}
			break;
		}
		default:
		{
			distributedWorker.postMessage("Unknown message type");
		}
	}
}

function sendRequest(url) 
{
	distributedWorker.req = initXMLHTTPRequest();
	if (distributedWorker.req) 
	{
		distributedWorker.req.onreadystatechange = onReadyState;
		distributedWorker.req.open('GET',url,true);     // http-Methode, url, asynchron
		distributedWorker.req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		distributedWorker.req.setRequestHeader("Cache-Control", "no-cache");
		distributedWorker.req.setRequestHeader("Pragma", "no-cache");
		distributedWorker.req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
		distributedWorker.req.send(null);
	 }
}

function initXMLHTTPRequest() 
{
	return new XMLHttpRequest();
}

function onReadyState() 
{
	if (distributedWorker.req.readyState == 4) 
	{
		distributedWorker.code = req.responseText;
		if(distributedWorker.waiterId != null)
		{
			distributedWorker.sendToSendWebsocket("code", distributedWorker.code);
		}
	}
}

function connectToSendWebsocket()
{
	var wsUri = "ws://192.168.0.103:8080/fhws.masterarbeit.distributedWebworkers/sendWebsocket";
	
	distributedWorker.sendWorkerWs = new WebSocket(wsUri);
	
	distributedWorker.sendWorkerWs.onopen = function()
	{
		distributedWorker.sendWorkerStat = 1;
		if(distributedWorker.file != "")
		{
			distributedWorker.sendRequest(distributedWorker.file);
		}
	}; // end function
	
	distributedWorker.sendWorkerWs.onmessage = function(event)
	{
		var json = JSON.parse(event.data);
		distributedWorker.postMessage(json.type);
		switch(json.type)
		{
			case ("RESULT_MESSAGE"): 
			{
				distributedWorker.postMessage(json.content);
				//localWorker.terminate();
				break;
			}
			case ("NOWAITER_MESSAGE"):
			{
				var worker = new Worker(file);
				worker.onmessage = function(e) 
				{
					distributedWorker.postMessage(e.data);
				};
				break;
			}
			default:
			{
				;
			}
		}
	};
		
	sendWorkerWs.onerror = function(event)
	{
		distributedWorker.postMessage("ERROR");
	}; //end function
}

function sendToSendWebsocket(type, content)
{
	var data = content.replace(/\r|\n/g, " ");
	if(distributedWorker.waiterId == null)
		distributedWorker.waiterId = "0";
	data = '{"type":"' + type + '","content":"' + data + '","waiterId":"' + distributedWorker.waiterId + '"}';
	distributedWorker.sendWorkerWs.send(data);
}

							