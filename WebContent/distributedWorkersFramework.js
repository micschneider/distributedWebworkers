var waitWorkerWs = null;
var waiterId = "";
var localWorkerMap = new HashMap();
var foreignWorkerMap = new HashMap();
var sendSocketArr = new Array();

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
				var message = {};
		        message.type = type;
		        message.content = content;
		        message.recipientId = (typeof recipientId === "undefined") ? "0" : recipientId;
		        var data = JSON.stringify(message);
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
	var sendWorkerWs = null;
	var distributedWorker = this;
	var callback = null;
	var code = null;
	var postMessageArray = new Array();
	
	(function()
	{
		var req = new XMLHttpRequest();
		req.open('GET',file,false);     // http-Methode, url, synchron
		req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		req.setRequestHeader("Cache-Control", "no-cache");
		req.setRequestHeader("Pragma", "no-cache");
		req.setRequestHeader("If-Modified-Since", "Sat, 1 Jan 2000 00:00:00 GMT");
		req.send(null);
		code = req.responseText.replace(/\"/g,"'");
		connectToWs();	
	}());

	function connectToWs()
	{
		var wsUri = "ws://192.168.0.103:8080/fhws.masterarbeit.distributedWebworkers/sendWebsocket";
			
		try
		{
			sendWorkerWs = new WebSocket(wsUri);
			sendSocketArr.push(sendWorkerWs);
			sendWorkerWs.onopen = function()
			{
				sendToSendWebsocket("code", code);	
				for(var i=0; i<postMessageArray.length; i++)
		        {
		          sendToSendWebsocket("post", postMessageArray[i]);
		        }	
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
						if(distributedWorker.callback != null)
							distributedWorker.callback(cbo);
						break;
					}
					case ("NO_WAITER_MESSAGE"):
					{
						var localWorker = new Worker(file);
						localWorker.onmessage = function(e) 
						{
							var cbo = {"data":e.data};
							if(distributedWorker.callback != null)
								distributedWorker.callback(cbo);
						};
						localWorkerMap.put(json.content, localWorker);
						break;
					}
					case ("WORKER_DOWN_MESSAGE"):
					{
						sendToSendWebsocket("code", code);
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
		var message = {};
	    message.type = type;
	    message.content = content;
	    message.waiterId = (typeof waiterId == null) ? "0" : waiterId;
	    var data = JSON.stringify(message);
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
			postMessageArray.push(message);
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
							for (var i=0; i<sendSocketArr.length;i++)
								sendSocketArr[i].close();
						}, 
						false);