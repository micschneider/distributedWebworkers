var waitWorkerWs = null;
var waiterId = "";
var localWorkerMap = new HashMap();
var foreignWorkerMap = new HashMap();

(function()
{
	var browserName = "";
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
		var waitWsUri = "ws://localhost:8080/fhws.masterarbeit.distributedWebworkers/waitWebsocket";
		try 
		{
			waitWorkerWs = new WebSocket(waitWsUri);
			waitWorkerWs.onopen = function()
			{
			}; // end function
		
			waitWorkerWs.onmessage = function(event)
			{
				var json = JSON.parse(event.data);
				console.log("WaiterWebsocket erhält:");
				console.log(json);
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
						var foreignWorker = new Worker(blobURL);
				
						foreignWorker.onmessage = function(e) 
						{
							sendToWaitWebsocket("result", e.data, json.from);
							//worker.terminate();
						};
						foreignWorkerMap.put(json.from, foreignWorker);
						break;
					}
					case ("POST_MESSAGE"):
					{
						var foreignWorker = foreignWorkerMap.get(json.from);
						if(foreignWorker!=null)
						{
							foreignWorker.postMessage(json.content);
						}
						else
							console.log("Kein passender Empfänger für die Nachricht gefunden!");
						break;
					}
					case ("TERMINATE_MESSAGE"):
					{
						var foreignWorker = foreignWorkerMap.get(json.from);
						if(foreignWorker!=null)
							foreignWorker.terminate();
						foreignWorkerMap.remove(json.from);
						break;
					}
					default:
					{
						console.log("Unbekannten Nachrichtentyp vom WaiterWebsocket erhalten.");
					}
				}
			};
				
			waitWorkerWs.onerror = function(evt)
			{
				console.log("Problem bei der Verbindung zum WaiterWebsocket");
			}; //end function
			
			function sendToWaitWebsocket(type, content, recipientId)
			{
				recipientId = (typeof recipientId === "undefined") ? "0" : recipientId;
				if(waiterId == null)
					waiterId = "0";
				data = '{"type":"' + type + '","content":"' + content + '","waiterId":"' + waiterId + '"';
				if(recipientId != 0)
					data += ',"recipientId":"' + recipientId + '"';
				data +=	'}';
				console.log("WaiterWebsocket sendet:");
				console.log(data);
				waitWorkerWs.send(data);
			}
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
	var distributedWorker = this;
	var callback;
	var code = null;
	var sendWorkerWs = null;
		
	(function()
	{
		var req = new XMLHttpRequest();
		
		if (req) 
		{
			req.onreadystatechange = function()
			{
				if (req.readyState == 4) 
				{
					code = req.responseText.replace(/\"/g,"'");
					connectToWs();
				}
			};
			req.open('GET',file,true);     // http-Methode, url, asynchron
			req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
			req.setRequestHeader("Cache-Control", "no-cache");
			req.setRequestHeader("Pragma", "no-cache");
			req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
			req.send(null);
		}	
	}());

	function connectToWs()
	{
		var wsUri = "ws://localhost:8080/fhws.masterarbeit.distributedWebworkers/sendWebsocket";
			
		try
		{
			sendWorkerWs = new WebSocket(wsUri);
			sendWorkerWs.onopen = function()
			{
				sendToSendWebsocket("code", code);	
			}; // end function
			
			sendWorkerWs.onmessage = function(event)
			{
				var json = JSON.parse(event.data);
				console.log("SenderWebsocket erhält:");
				console.log(json);

				switch(json.type)
				{
					case ("RESULT_MESSAGE"): 
					{
						var cbo = {"data":json.content};
						//sendWorkerWs.close();
						distributedWorker.callback(cbo);
						break;
					}
					case ("NO_WAITER_MESSAGE"):
					{
						var localWorker = new Worker(file);
						localWorker.onmessage = function(e) 
						{
							var cbo = {"data":e.data};
							distributedWorker.callback(cbo);
						};
						localWorkerMap.put(json.content, localWorker);
						break;
					}
					case ("NO_RECIPIENT_POST_MESSAGE"):
					{
						var localWorker = localWorkerMap.get(json.from);
						if(localWorker!=null)
						{
							localWorker.postMessage(json.content);
						}
						else
							console.log("Der Empfänger der Nachricht konnte nicht gefunden werden.");
						break;
					}
					case("NO_RECIPIENT_TERMINATE_MESSAGE"):
					{
						var localWorker = localWorkerMap.get(json.content);
						if(localWorker!=null)
						{
							localWorker.terminate();
							localWorkerMap.remove(json.content);
						}
						else
							console.log("Der zu beendende Worker konnte nicht gefunden werden");
						break;
					}
					default:
					{
						;
					}
				}
			};
		}
		catch(e)
		{
			throw "DistributedWorkersFramework meldet: Verbindung zum SenderWebsocket konnte nicht hergestellt werden. Bitte überprüfen Sie die URL!";
		}
	}

	function sendToSendWebsocket(type, content)
	{
		var data = content.replace(/\r|\n|\t|\s/g, "");
		if(waiterId == null)
			waiterId = "0";
		data = '{"type":"' + type + '","content":"' + data + '","waiterId":"' + waiterId + '"}';
		console.log("SenderWebsocket sendet:");
		console.log(data);
		sendWorkerWs.send(data);
	}
					   
	this.addEventListener = function(name, callback, syn)
	{
		if(name == "message")
		{
			this.callback = callback;
		}
	};
	
	this.postMessage = function(message)
	{
		if(sendWorkerWs.readyState==1)
			sendToSendWebsocket("post", message);
		else
			console.log("Problem beim Senden der Nachricht");
	};
	
	this.terminate = function()
	{
		if(sendWorkerWs.readyState==1)
			sendToSendWebsocket("terminate", "");
		else
			console.log("Problem beim Beenden des Workers");
	};
}

function HashMap()
{
	this.list = new Array();
	
	this.put = function(key, value)
	{
		this.list[key] = value;
	};
	
	this.get = function(key)
	{
		return this.list[key];
	};
	
	this.remove = function(key)
	{
		this.list[key] = null;
	};
}

window.addEventListener("unload", 
						function()
						{
							if(waitWorkerWs != null)
								waitWorkerWs.close();
						}, 
						false);