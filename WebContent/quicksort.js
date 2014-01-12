this.addEventListener("message", init, false);

function init(e)
{
	var arrayString = e.data;
	var toSort = arrayString.split(";");
	for(var i=0; i<toSort.length; i++)
	{
		toSort[i]=parseInt(toSort[i]);
	}
	sort(toSort);
	this.postMessage(toSort);
}

function swap(array, indexA, indexB) 
{
	var temp = array[indexA];
	array[indexA] = array[indexB];
	array[indexB] = temp;
}
	 
function partition(array, pivot, left, right) 
{
	 var storeIndex = left;
	 var pivotValue = array[pivot];
	 
	 swap(array, pivot, right);

	 for(var v = left; v < right; v++) 
	 {
		 if(array[v] < pivotValue) 
	     {
			 swap(array, v, storeIndex);
			 storeIndex++;
	     }
	  }
	  swap(array, right, storeIndex);
	  return storeIndex;
}
	 
function sort(array, left, right) 
{
	  var pivot = null;
	 
	  if(typeof left !== 'number')
		  left = 0;
	 
	  if(typeof right !== 'number')
		  right = array.length - 1;
	 
	  if(left < right) 
	  {
		  pivot = left + Math.ceil((right - left) * 0.5);
	      newPivot = partition(array, pivot, left, right);
	 
	      sort(array, left, newPivot - 1);
	      sort(array, newPivot + 1, right);
	   }
} 