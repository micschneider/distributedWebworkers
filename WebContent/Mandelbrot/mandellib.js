/**
 * Canvas Objekt
 */
var canvas;
/**
 * Context (2d)
 */
var ctx;

/**
 * y-Achse
 */
var i_max = 1.5;
var i_min = -1.5;
/**
 * x-Achse
 */
var r_min = -2.5;
var r_max = 1.5;

/**
 * Maximale Iterationen (Sprünge)
 */
var max_iter = 1024;
/**
 * Zahl, bis zu der überprüft wird, ob in Mandelbrotmenge oder nicht (ob gegen unendlich)
 */
var escape = 100;

/**
 * Farbpalette
 */
var palette = [];

/**
 * Strukturiert die Daten, die zum Worker geschickt werden
 * @param row Die Zeile, die verpackt werden soll
 */
function createTask(row)
{
	// Task-Objekt
	var task =
	{
		row : row, // Zeilennummer, auf der gearbeitet wird
		width : rowData.width, // Breite des ImageData-Objekts, das audgefüllt werden muss
		generation : generation, // Aktuelle Generation
		r_min : r_min,
		r_max : r_max,
		i : i_max + (i_min - i_max) * row / canvas.height,
		max_iter : max_iter,
		escape : escape
	};
	return task;
}
/**
 * Mapped alle Werte zwischen 0 und max_iter auf 256
 * Erstellt eine Palette, die garantiert, dass Werte, die nah beieinander liegen, 
 * farblich ähnlich gezeichnet werden
 */
function makePalette()
{
	function wrap(x)
	{
		x = ((x + 256) & 0x1ff) - 256;
		if (x < 0)
			x = -x;
		return x;
	}// end function wrap
	for ( var i = 0; i <= this.max_iter; i++)
	{
		palette.push([wrap(7 * i), wrap(5 * i), wrap(11 * i)]);
	}// end for
}// end function makePalette

/** 
 * Zeichnet eine Zeile für die vom Worker zurückgelieferten Werte mit Hilfe der Palette
 * @param workerResults Die Ergebnisse, die der Worker geschickt hat
 */
function drawRow(workerResults)
{
	// values auf vom Worker geschickte Werte setzen
	var values = workerResults.values;
	
	// Änderungen in pixelData ändern auch rowData
	var pixelData = rowData.data;
	
	// Für jeden Pixel in der Zeile
	for ( var i = 0; i < rowData.width; i++)
	{
		// ImageData.data[0] = Rot
		// ImageData.data[1] = Grün
		// ImageData.data[2] = Blau
		// ImageData.data[3] = Alpha (Transparenz)
		var red = i * 4;
		var green = i * 4 + 1;
		var blue = i * 4 + 2;
		var alpha = i * 4 + 3;

		// Keine Transparenz
		pixelData[alpha] = 255;

		// Falls der i-Wert im Array negativ ist, Farbe auf schwarz setzen
		if (values[i] < 0)
		{
			pixelData[red] = pixelData[green] = pixelData[blue] = 0;
		}// end if
		else
		{
			// Ansonsten die einzelnen i-Werte auf eine Farbe mappen
			var color = this.palette[values[i]];

			//RGB-Werte setzen
			pixelData[red] = color[0];
			pixelData[green] = color[1];
			pixelData[blue] = color[2];
		}// end else
	}// end for
	
	// Malt die aktuelle Zeile
	ctx.putImageData(this.rowData, 0, workerResults.row);
}

/**
 * Canvas Variablen werden gesetzt
 */
function setupGraphics()
{
	canvas = document.getElementById("fractal");
	ctx = canvas.getContext("2d");
	canvas.width = window.innerWidth;
	canvas.height = window.innerHeight;
	
	var width = ((i_max - i_min) * canvas.width / canvas.height);
	var r_mid = (r_max + r_min) / 2;
	r_min = r_mid - width / 2;
	r_max = r_mid + width / 2;

	// Zeile erstellen
	rowData = ctx.createImageData(canvas.width, 1);

	makePalette();
}// end function setupGraphics