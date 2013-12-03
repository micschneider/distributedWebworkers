var waitWorkerWs = null;
connectToWaitWebsocket();
function connectToWaitWebsocket()
{
	var waitWsUri = "ws://localhost:8080/fhws.masterarbeit.distributedWebworkers/waitWebsocket";
	waitWorkerWs = new WebSocket(waitWsUri);
		
	waitWorkerWs.onopen = function()
					  {
					  }; // end function
	waitWorkerWs.onmessage = function(e)
					     {
							alert(e.data);
							//neuen BlobWorker erzeugen
							//im Blobworker eval e.data
							//Ergebnis zurückschicken
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
	var parentWorker = new Worker("worker.js");
	parentWorker.postMessage({type: 'filename', content: file});
					   
	this.addEventListener = function()
							{
								Worker.prototype.addEventListener.apply(parentWorker, arguments);
							};
	this.postMessage = function(message)
					   {
							parentWorker.postMessage(message);
					   };
}

window.addEventListener("unload", closeWaitWs, false);





