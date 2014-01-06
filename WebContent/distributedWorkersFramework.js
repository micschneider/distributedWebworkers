var waitWorkerWs = null;
var waiterId = "";
var browserName = "";

(function()
{
	var agent = navigator.userAgent.toLowerCase();  
	if(agent.indexOf("gecko/") > -1)
		browserName = "firefox";
	else if(agent.indexOf("trident/") > -1 && agent.indexOf("opera" == -1))
		browserName = "ie";
	else if(agent.indexOf("opr/") > -1)
		browserName = "opera";
	else if(agent.indexOf("chrome/") > -1)
		browserName = "chrome";
	else
		browserName = "unknown";
	
	if(browserName == "chrome" || browserName == "firefox" || browserName == "opera")
	{
		var waitWsUri = "ws://192.168.0.103:8080/fhws.masterarbeit.distributedWebworkers/waitWebsocket";
		try 
		{
			waitWorkerWs = new WebSocket(waitWsUri);
			waitWorkerWs.onopen = function()
			{
			}; // end function
		
			waitWorkerWs.onmessage = function(event)
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
							data = '{"type":"result","content":"' + e.data + '","waiterId":"' + waiterId + '"}';
							waitWorkerWs.send(data);
						};
						break;
					}
					default:
					{
						alert("DistributedWorkersFramework meldet: Unbekannten Nachrichtentyp vom WaiterWebsocket erhalten.");
					}
				}
			};
				
			waitWorkerWs.onerror = function(evt)
			{
				alert("DistributedWorkersFramework meldet: Probleme bei der Verbindung zum WaiterWebsocket");
			}; //end function
		}
		catch(e)
		{
			throw "DistributedWorkersFramework meldet: Verbindung zum WaiterWebsocket konnte nicht hergestellt werden. Bitte überprüfen Sie die URL!";
		}
	}
	else
	{
		waiterId = "0";
	}
}());

function DistributedWebworker(file) 
{
	if(browserName == "chrome" || browserName == "ie" || browserName == "opera")
	{
		var sendWorker = new Worker("worker.js");
		sendMessageToWorker('filename', file);
		
		if(waiterId != null)
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
	else
	{
		var worker = new Worker(file);
		this.addEventListener = function()
		{
			Worker.prototype.addEventListener.apply(worker, arguments);
		};
		this.postMessage = function(message)
		{
			worker.postMessage(message);
		};
		this.terminate = function()
		{
			Worker.prototype.terminate.apply(worker);
		};
		
	}
}

window.addEventListener("unload", 
						function()
						{
							if(waitWorkerWs != null)
								waitWorkerWs.close();
						}, 
						false);





