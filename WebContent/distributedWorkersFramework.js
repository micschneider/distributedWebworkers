/**
 * This framework enables the client browser to work as an distributedWebworker
 * for other clients and allows him to distribute his own work to other
 * participating clients. 
 */
(function()
{
	/**
	 * The IP address of the servers wait WebSocket endpoint (WaiterEndpoint)
	 */
	var waitWsUri = "ws://192.168.0.169:8080/fhws.masterarbeit.distributedWebworkers/waitWebsocket";

	/**
	 * The IP address of the servers send WebSocket endpoint (SenderEndpoint)
	 */
	var sendWsUri = "ws://192.168.0.169:8080/fhws.masterarbeit.distributedWebworkers/sendWebsocket";

	/**
	 * Represents the waiter/worker WebSocket
	 */
	var waitWorkerWs = null;

	/**
	 * Stores the ID of the WebSocket session
	 */
	var waitWorkerWsId = null;

	/**
	 * Stores a reference to all workers, which are executed for this client
	 * himself
	 */
	var localWorkerMap = new HashMap();

	/**
	 * Stores a reference to all worker, which are executed for foreign clients
	 */
	var foreignWorkerMap = new HashMap();

	/**
	 * Stores a reference to all SendWebSocket on this client. This is needed
	 * for a correct closing.
	 */
	var sendSocketArr = new Array();

	/**
	 * This anonymous function is called immediately when the framework is
	 * included. It established the connection to the waiter WebSocket and
	 * creates so a new potential worker client, who can be used to execute work
	 * packages.
	 */
	(function()
	{
		// Find out the name of the client browser
		var browserName = "";
		var agent = navigator.userAgent.toLowerCase();
		if (agent.indexOf("gecko/") > -1)
			browserName = "firefox";
		else if (agent.indexOf("trident/") > -1 && agent.indexOf("opera" == -1))
			browserName = "ie";
		else if (agent.indexOf("opr/") > -1)
			browserName = "opera";
		else if (agent.indexOf("chrome/") > -1)
			browserName = "chrome";
		else
			browserName = "unknown";

		// Check if the browser is supported for receiving and executing work
		// packages
		if ((browserName == "chrome" || browserName == "firefox"
				|| browserName == "opera") && waitWorkerWs == null)
		{
			try
			{
				// Established the connection to the waiter endpoint
				waitWorkerWs = new WebSocket(waitWsUri);

				// When the connection is open, ...
				waitWorkerWs.onopen = function()
				{
				}; // end function

				// When a message is incoming via the waiter WebSocket
				waitWorkerWs.onmessage = function(event)
				{
					// Parse the incoming data as a JSON object
					var json = JSON.parse(event.data);
					console.log("WaiterWebsocket erhält:");
					console.log(json);

					// Check the type of the incoming message
					switch (json.type)
					{
						// If the message is a IdMessage, store the ID
						case ("ID_MESSAGE"):
						{
							waitWorkerWsId = json.content;
							break;
						}// end case
							// If the message is a CodeMessage
						case ("CODE_MESSAGE"):
						{
							// Create dynamically a new Worker Thread
							var bb = new Blob([json.content],
							{
								type : 'application/javascript'
							});
							var blobURL = window.URL.createObjectURL(bb);
							var foreignWorker = new Worker(blobURL);

							// When this worker sends a message, forward it to
							// the server
							foreignWorker.onmessage = function(e)
							{
								sendToWaitWebsocket("RESULT_MESSAGE", e.data,
										json.from);
							};
							// When this worker sends an error, forward it to
							// the server
							foreignWorker.onerror = function(e)
							{
								sendToWaitWebsocket("WORKER_ERROR_MESSAGE", e,
										json.from);
							};

							// Put the new Worker Thread to the foreignWOrker
							// array
							foreignWorkerMap.put(json.from, foreignWorker);
							break;
						}// end case
						case ("POST_MESSAGE"):
						{
							// Get the worker, which the post message is for
							// If the right one is found, forward the message to
							// it
							var foreignWorker = foreignWorkerMap.get(json.from);
							if (foreignWorker != null)
								foreignWorker.postMessage(json.content);
							else
								console
										.log("Kein passender Empfänger für die Nachricht gefunden!");
							break;
						}// end case
							// If the message is a TerminateMessage
						case ("TERMINATE_MESSAGE"):
						{
							// Get the worker, which the terminate message is
							// for. If the right one is found, terminate it and
							// remove it from the map
							var foreignWorker = foreignWorkerMap.get(json.from);
							if (foreignWorker != null)
							{
								foreignWorker.terminate();
								foreignWorkerMap.remove(json.from);
							}// end if
							break;
						}
							// If the message type is not known
						default:
						{
							console
									.log("Unbekannten Nachrichtentyp vom WaiterWebsocket erhalten.");
						}
					}
				};

				// If the waiter WebSocket throws an error
				waitWorkerWs.onerror = function(evt)
				{
					console
							.log("Problem bei der Verbindung zum WaiterWebsocket. WaiterWebsocket wird geschlossen.");

					// Try to close the connection
					if (waitWorkerWs != null)
						waitWorkerWs.close();
				}; // end function

				/**
				 * Sends a message to the WaiterEndpoint via the waiter
				 * WebSocket
				 * 
				 * @param type
				 *            The message type of the message
				 * @param content
				 *            The content of the message
				 * @param recipientId
				 *            The ID of the recipient of the message, if known
				 */
				function sendToWaitWebsocket(type, content, recipientId)
				{
					// Create a new object, set the attributes and convert it to
					// a JSON string
					var message = {};
					message.type = type;
					message.content = content;
					message.recipientId = (typeof recipientId === "undefined") ? "0"
							: recipientId;
					var data = JSON.stringify(message);
					console.log("WaiterWebsocket sendet:");
					console.log(data);

					// Send the JSON string via WebSocket
					waitWorkerWs.send(data);
				}// end function sendToWaitWebsocket
			}// end try
			// If something is wrong with the connection
			catch (e)
			{
				throw "DistributedWorkersFramework meldet: Verbindung zum WaiterWebsocket konnte nicht hergestellt werden. Bitte überprüfen Sie die URL!";
			}// end catch
		}// end if
		// If the client browser is not supported for receiving work packages
		else
		{
			console
					.log("Sie verwenden den Internet Explorer oder einen anderen Browser, der nicht den vollen Funktionsumfang dieses Frameworks unterstützt.");
		}// end else
	}()); // end anonymous function

	/**
	 * Represents a worker thread. This thread can be executed locally on the
	 * same client PC (if no other waiter client is free) or on a foreign
	 * client.
	 * 
	 * @param file
	 *            The file to be executed in a new thread
	 */
	this.DistributedWebworker = function(file)
	{
		/**
		 * Represents the new DistributedWorker himself
		 */
		var distributedWorker = this;

		/**
		 * Represents the WebSocket connection to the SenderEndpoint
		 */
		var sendWorkerWs = null;

		/**
		 * Stores the function to be executed when a result is incoming
		 */
		var callback = null;

		/**
		 * Stores the function to be executed when an error is incoming
		 */
		var errorCallback = null;

		/**
		 * Stores the code contained in the file
		 */
		var code = null;

		/**
		 * A Buffer for unsent PostMessages. When the connection is established,
		 * all messages in the Buffer will be sent after the CodeMessage
		 */
		var postMessageBuffer = new Array();

		/**
		 * This function is executed immediately when a new DistributedWorker is
		 * created
		 */
		(function()
		{
			// Fetch the requested file via AJAX request from the web server
			// This request has to be synchron, because the program has to wait
			// for the file content
			var req = new XMLHttpRequest();

			// GET the FILE SYNCHRON
			req.open('GET', file, false);

			// Set some headers so the cache is disabled and send the request
			req.setRequestHeader("Content-Type",
					"application/x-www-form-urlencoded");
			req.setRequestHeader("Cache-Control", "no-cache");
			req.setRequestHeader("Pragma", "no-cache");
			req.setRequestHeader("If-Modified-Since",
					"Sat, 1 Jan 2000 00:00:00 GMT");
			req.send(null);

			// Replace all " in the code with '
			code = req.responseText.replace(/\"/g, "'");

			// Connect to the SenderEndpoint
			connectToWs();
		}());

		/**
		 * Connects to the SenderEndpoint via WebSocket
		 */
		function connectToWs()
		{
			try
			{
				// Open a new WebSocket connection to the SenderEndpoint
				sendWorkerWs = new WebSocket(sendWsUri);

				// Store the new WebSocket connection
				sendSocketArr.push(sendWorkerWs);

				// When the connection is established
				sendWorkerWs.onopen = function()
				{
					// Send the code fetched via AJAX request to the server
					sendToSendWebsocket("CODE_MESSAGE", code);

					// If there are any PostMessages in the Buffer, send them to
					// the server, and delete them
					for ( var i = 0; i < postMessageBuffer.length; i++)
					{
						sendToSendWebsocket("POST_MESSAGE",
								postMessageBuffer[i]);
						postMessageBuffer[i] = null;
					} // end for
				};

				// When a new message is incoming via the SenderWebsocket
				sendWorkerWs.onmessage = function(event)
				{
					// Parse the message as a JSON object
					var json = JSON.parse(event.data);
					console.log("SenderWebsocket erhält:");
					console.log(json);

					// Differentiate between message types
					switch (json.type)
					{
						// If the message is a result message
						case ("RESULT_MESSAGE"):
						{
							// Create a new CallBackObject and send it back to
							// the callback function
							var cbo =
							{
								"target" : distributedWorker,
								"data" : json.content
							};
							if (distributedWorker.callback != null)
								distributedWorker.callback(cbo);
							break;
						}// end case
							// If the message is a WorkerErrorMessage
						case ("WORKER_ERROR_MESSAGE"):
						{
							// Create a new CallbackObject and send it back to
							// the errorCallback function
							var cbo =
							{
								"message" : json.content.message,
								"filename" : file,
								"lineno" : json.content.lineno
							};
							if (distributedWorker.errorCallback != null)
								distributedWorker.errorCallback(cbo);
							break;
						}// end case
							// If the message is a NoWaiterMessage
						case ("NO_WAITER_MESSAGE"):
						{
							// No foreign worker is free, so create a local one
							var localWorker = new Worker(file);

							// If this worker sends a message
							localWorker.onmessage = function(e)
							{
								// Create a new CallbackObject and send it to
								// the callback function
								var cbo =
								{
									"target" : distributedWorker,
									"data" : e.data
								};
								if (distributedWorker.callback != null)
									distributedWorker.callback(cbo);
							};
							// If this worker sends an error
							localWorker.onerror = function(e)
							{
								// Create a new CallbackObject and send it to
								// the errorCallback function
								var cbo =
								{
									"message" : e.message,
									"filename" : e.filename,
									"lineno" : e.lineno
								};
								if (distributedWorker.errorCallback != null)
									distributedWorker.errorCallback(cbo);

								// Terminate the local worker
								localWorker.terminate();

								// Remove the worker from the map
								localWorkerMap.remove(json.content);
							};

							// Store the new local worker in the worker map
							localWorkerMap.put(json.content, localWorker);
							break;
						}
							// If the message is a WorkerDownMessage
						case ("WORKER_DOWN_MESSAGE"):
						{
							// Send the code again to the server
							sendToSendWebsocket("CODE_MESSAGE", code);
							break;
						}
							// If the message is a NoRecipientPostMessage
						case ("NO_RECIPIENT_POST_MESSAGE"):
						{
							// Get the regarding worker from the map
							var localWorker = localWorkerMap.get(json.from);

							// If found, send the PostMessage to it
							if (localWorker != null)
								localWorker.postMessage(json.content);
							else
								console
										.log("Der Empfänger der Nachricht konnte nicht gefunden werden.");
							break;
						}// end case
						case ("NO_RECIPIENT_TERMINATE_MESSAGE"):
						{
							// Get the regarding worker from the map
							var localWorker = localWorkerMap.get(json.content);

							// If found, terminate it and remove it from the map
							if (localWorker != null)
							{
								localWorker.terminate();
								localWorkerMap.remove(json.content);
							}// end if
							else
								console
										.log("Der zu beendende Worker konnte nicht gefunden werden");
							break;
						}// end case
							// If the message type is unknown
						default:
						{
							console
									.log("Unbekannten Nachrichtentyp vom SenderWebsocket erhalten");
						}// end default
					}// end switch
				};
				// If an error occurs caused by the sender websocket connection
				sendWorkerWs.onerror = function(evt)
				{
					// try to close the connection
					console
							.log("Problem bei der Verbindung zum SenderWebsocket. SenderWebsocket wird geschlossen.");
					if (sendWorkerWs != null)
						sendWorkerWs.close();
				}; // end function
			}// end try
			catch (e)
			{
				throw "DistributedWorkersFramework meldet: Verbindung zum SenderWebsocket konnte nicht hergestellt werden. Bitte überprüfen Sie die URL!";
			}// end catch
		}// end function connectToWs()

		/**
		 * Sends a message to the SenderEndpoint via the sender WebSocket
		 * 
		 * @param type
		 *            The message type of the message
		 * @param content
		 *            The content of the message
		 */
		function sendToSendWebsocket(type, content)
		{
			// Create a new object, set the attributes and convert it to
			// a JSON string
			var message = {};
			message.type = type;
			message.content = content;
			message.waiterId = (waitWorkerWsId == null) ? "0" : waitWorkerWsId;
			var data = JSON.stringify(message);
			console.log("SenderWebsocket sendet:");
			console.log(data);

			// Sende the JSON string
			sendWorkerWs.send(data);
		}// end function sendToWebsocket()

		/**
		 * Adds an event listener to the DistributedWebworker and stores the
		 * callback functions.
		 */
		this.addEventListener = function(name, callback, syn)
		{
			// Check if the eventListener type is known and store the callback
			// function
			if (name == "message")
				distributedWorker.callback = callback;
			else if (name == "error")
				distributedWorker.errorCallback = callback;
			else
				console.log("addEventListener " + name + " nicht bekannt.");
		};

		/**
		 * Checks if the send WebSocket is ready to send the post message. If
		 * not, store the message in the buffer
		 */
		this.postMessage = function(message)
		{
			// Check the readyState of the sendWebSocket
			if (sendWorkerWs.readyState == 1)
				sendToSendWebsocket("POST_MESSAGE", message);
			else
				postMessageBuffer.push(message);
		};

		/**
		 * Checks if the sendWebSocket is ready to send the terminate message.
		 */
		this.terminate = function()
		{
			if (sendWorkerWs.readyState == 1)
				sendToSendWebsocket("TERMINATE_MESSAGE", "");
			else
				console.log("Problem beim Beenden des Workers");
		};
	};

	/**
	 * Represents a HashMap with some functions
	 */
	function HashMap()
	{
		/**
		 * An array list, which can store objects
		 */
		this.list = new Array();

		/**
		 * Puts a new object to the array
		 * 
		 * @param key
		 *            Contains the key string under which the value can be found
		 * @param value
		 *            Contains the value to be stored
		 */
		this.put = function(key, value)
		{
			this.list[key] = value;
		};

		/**
		 * Returns a value stored under a certain key
		 * 
		 * @param key
		 *            The key of the wanted value
		 * @return The value stored under the key
		 */
		this.get = function(key)
		{
			return this.list[key];
		};

		/**
		 * Removes an object with a certain key
		 * 
		 * @param key
		 *            The key of the object to be removed
		 */
		this.remove = function(key)
		{
			this.list[key] = null;
		};
	}// end function HashMap

	// Adds an EventListener to the window, so all Websocket connection will be
	// close on unloding the page
	window.addEventListener("unload", function()
	{
		// Close the waiterWebSocket
		if (waitWorkerWs != null)
			waitWorkerWs.close();
		// Close all senderWebSockets
		for ( var i = 0; i < sendSocketArr.length; i++)
			sendSocketArr[i].close();
	}, false);
}());// end framework
