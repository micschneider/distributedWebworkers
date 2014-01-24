var numberOfWorkers = 3;
var workers = [];
var rowData;
var nextRow = 0;
var generation = 0;

window.onload = init;

/**
 * Wird sofort beim Laden des Fensters aufgerufen
 */
function init()
{
	setupGraphics();

	 // Klick-Handler f�r das Canvas-Element
	 // Die Position des Mauszeigers beim Click ist im Event-Object enthalten
	canvas.onclick = function(event)
	{
		handleClick(event.clientX, event.clientY);
	};
	
	// Wenn die Fenstergr��e ge�ndert wird, muss auch die Canvas-Gr��e ge�ndert werden
	window.onresize = function()
	{
		resizeToWindow();
	};

	// Worker erstellen und in einem Array speichern
	// F�r alle Worker
	for ( var i = 0; i < numberOfWorkers; i++)
	{
		// Neuen Worker erstellen
		var worker = new Worker("worker.js");

		// MessageHandler setzen
		worker.addEventListener("message", function(event)
		{
			processWork(event.target, event.data);
		}, false);

		// Worker auf unt�tig setzen
		worker.idle = true;
		
		// Worker zu Array hinzuf�gen
		workers.push(worker);
	}// end for

	// Alle Worker starten
	startWorkers();
}// end method init

/**
 * Alle Worker werden resettet und fangen bei Row 0 an zu arbeiten
 * Jedem freien Worker wird eine Aufgabe zugeteilt
 */
function startWorkers()
{
	generation++;
	nextRow = 0;
	
	// F�r alle Worker
	for ( var i = 0; i < workers.length; i++)
	{
		var worker = workers[i];
		
		// Pr�fen, ob Worker frei ist
		if (worker.idle)
		{
			// Neue Task erstellen
			var task = createTask(nextRow);
			
			// Worker auf besetzt stellen
			worker.idle = false;
			
			// Task an Worker senden
			worker.postMessage(task);
			
			// N�chste zu bearbeitende Zeile einstellen
			nextRow++;
		}// end if
	}// end for
}// end method startWorkers

/**
 * Callback-Methode, die aufgerufen wird, sobald ein Worker ein Ergebnis schickt
 * �berpr�ft, ob der Worker zur momentanen Fractal-Generation geh�rt
 * @param workerResults das Ergebnis des Workers
 */   
function processWork(worker, workerResults)
{
	// �berpr�fe, ob Worker zur aktuellen Generation geh�rt
	if (workerResults.generation == generation)
	{
		// wenn ja, zeichne das Ergebnis
		drawRow(workerResults);
	}
	
	// weise dem Worker eine neue Aufgabe zu
	reassignWorker(worker);
}

/**
 * Einem Worker seine neue Arbeit zuweisen
 * @param worker Der Worker, der neue Arbeit bekommen soll
 */
function reassignWorker(worker)
{
	// n�chste Zeile ist an der Reihe
	var row = nextRow++;
	
	// wenn die H�he der Canvas erreicht ist
	if (row >= canvas.height)
	{
		// Worker idlen lassen
		worker.idle = true;
	}// end if
	else
	{
		// Neue Task erstellen
		var task = createTask(row);
		
		// Worker auf besetzt stellen
		worker.idle = false;
		
		// Arbeit an Worker schicken
		worker.postMessage(task);
	}// end else
}// end method reassignWorker

/**
 * Wird aufgerufen, wenn der User auf die Canvas klickt
 * Resettet die Parameter des Fraktals
 * Der Zoomfaktor bestimmt das Ausma� des neuen Fraktals
 * Die Worker werden �ber dem neuen Fraktalausschnitt neu gestartet
 */
function handleClick(x, y)
{
	var width = r_max - r_min;
	var height = i_min - i_max;
	var click_r = r_min + ((width * x) / canvas.width);
	var click_i = i_max + ((height * y) / canvas.height);

	var zoom = 8;

	r_min = click_r - width / zoom;
	r_max = click_r + width / zoom;
	i_max = click_i - height / zoom;
	i_min = click_i + height / zoom;

	// Worker neu starten
	startWorkers();
}// end method handleClick

/** 
 * Wird aufgerufen, wenn die Fenstergr��e ge�ndert wird
 * Startet die Worker neu
 */
function resizeToWindow()
{
	canvas.width = window.innerWidth;
	canvas.height = window.innerHeight;
	var width = ((i_max - i_min) * canvas.width / canvas.height);
	var r_mid = (r_max + r_min) / 2;
	r_min = r_mid - width / 2;
	r_max = r_mid + width / 2;
	rowData = ctx.createImageData(canvas.width, 1);

	// Worker neu starten
	startWorkers();
}// end method resizeToWindow