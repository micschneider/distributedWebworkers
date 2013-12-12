var waitWorkerWs = null;
var waiterId = "";
var localWorker = null;

connectToWaitWebsocket();

function connectToWaitWebsocket()
{
	var waitWsUri = "ws://localhost:8080/fhws.masterarbeit.distributedWebworkers/waitWebsocket";
	waitWorkerWs = new WebSocket(waitWsUri);
		
	waitWorkerWs.onopen = function()
	{
	}; // end function
	
	waitWorkerWs.onmessage = function(event)
	{
		incomingMessage(event);
	};
			
	waitWorkerWs.onerror = function(evt)
	{
	}; //end function
}

function closeWaitWs()
{
	if(waitWorkerWs != null)
		waitWorkerWs.close();
}

function DistributedWebworker(file) 
{
	localWorker = new Worker("worker.js");
	sendMessageToWorker('filename', file);
	if(waiterId != "")
		sendMessageToWorker('waiterId', waiterId);
					   
	this.addEventListener = function()
	{
		Worker.prototype.addEventListener.apply(localWorker, arguments);
	};
	this.postMessage = function(message)
	{
		localWorker.postMessage(message);
	};
	this.terminate = function()
	{
		Worker.prototype.terminate.apply(localWorker);
	};
}

function incomingMessage(event)
{
	var json = JSON.parse(event.data);
	switch(json.type)
	{
		case ("ID_MESSAGE"):
		{
			waiterId = json.content;
			if(localWorker != null)
				sendMessageToWorker("waiterId", waiterId);
			break;
		}
		default:
		{
			alert("Unknown message type");
		}
	}
}

function sendMessageToWorker(type, content)
{
	var data = '{"type":"' + type + '", "content": "' + content + '"}';
	localWorker.postMessage(data);
}

window.addEventListener("unload", closeWaitWs, false);





