this.addEventListener("message", init, false);

function init(e)
{
	var arrayString = e.data;
	var toCalculate = arrayString.split(";");
	toCalculate[0] = parseInt(toCalculate[0]);
	toCalculate[1] = parseInt(toCalculate[1]);
	var result = calculate(toCalculate[0], toCalculate[1]);
	this.postMessage(result);
}// end function init

function calculate(start, to)
{
	var acc = 0.0;
	for(var i = start; i < (start+to); i++)
	{
		acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1);
	}// end for
	return acc;
}// end function calculate