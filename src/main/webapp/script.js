
// Once the webpage finished loading...
document.addEventListener("DOMContentLoaded", function() { 

// Create a pointer to our form
   var myForm = document.getElementById('myForm');

// If someone submits the form, override that to call a javascript function instead.
   myForm.addEventListener('submit', function(event) {
     getData();
     event.preventDefault(); // cancels form submission
   });
});

// Take the state and county entered, and send to the java backend to request covid data
async function getData() {
    document.getElementById("results").innerHTML = "Loading...";

    const state  = document.getElementById("state").value;
    const county = document.getElementById("county").value;

    const params = new URLSearchParams();
    params.append('state', state);
    params.append('county', county);

    fetch('/getData', {
       method: 'POST',
       body: params
    })
       .then(response => response.json())
       .then(data => display(data, state, county));
}

// A function to Print / Display the data we got
function display(data, state, county) {
    var results = document.getElementById("results");

    if(data.hasOwnProperty('error')) {
        results.innerHTML = "Location not found.";
    }
    else {
        var cases       = Math.round(Number(data.newCases));
        var totalCases  = Math.round(Number(data.avgCases)*7);

        var deaths      = Math.round(Number(data.newDeaths));
        var totalDeaths = Math.round(Number(data.avgDeaths)*7);

        results.innerHTML  = "Today In <b>" + county + "</b>, <b>" + state + "</b>,<br>there were...<br><br>";

        results.innerHTML += "<b>" + cases.toLocaleString()  + "</b> New Cases of Covid-19.<br>" 
        results.innerHTML += "<b>" + totalCases.toLocaleString()  + "</b> cases this past week.<br><br>";

        results.innerHTML += "<b>" + deaths.toLocaleString() + "</b> New Covid-19 related deaths.<br>";
        results.innerHTML += "<b>" + totalDeaths.toLocaleString() + "</b> deaths this past week.";
    }
} 