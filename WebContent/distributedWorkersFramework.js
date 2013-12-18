var waitWorkerWs = null;
var waiterId = "";

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
	{
		waitWorkerWs.close();
	}
}

function DistributedWebworker(file) 
{
	var sendWorker = new Worker("worker.js");
	sendMessageToWorker('filename', file);
	if(waiterId != "")
		sendMessageToWorker('waiterId', waiterId);
	
	function sendMessageToWorker(type, content)
	{
		var data = '{"type":"' + type + '", "content": "' + content + '"}';
		sendWorker.postMessage(data);
	}
					   
	this.addEventListener = function()
	{
		Worker.prototype.addEventListener.apply(sendWorker, arguments);
	};
	this.postMessage = function(message)
	{
		sendWorker.postMessage(message);
	};
	this.terminate = function()
	{
		Worker.prototype.terminate.apply(sendWorker);
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
			break;
		}
		case ("CODE_MESSAGE"):
		{
			var bb = new Blob([json.content], {type: 'application/javascript'});

			var blobURL = window.URL.createObjectURL(bb);

			var worker = new Worker(blobURL);
			worker.onmessage = function(e) 
			{
				sendToSendWebsocket("result", e.data);
			};
			break;
		}
		default:
		{
			alert("Unknown message type");
		}
	}
}

function sendToSendWebsocket(type, content)
{
	data = '{"type":"' + type + '","content":"' + content + '","waiterId":"' + waiterId + '"}';
	waitWorkerWs.send(data);
}

window.addEventListener("unload", closeWaitWs, false);





