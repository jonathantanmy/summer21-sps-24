
// Once the webpage finished loading...
document.addEventListener("DOMContentLoaded", function() { 

// Create a pointer to our form
   var myForm = document.getElementById('myForm');

// If someone submits the form, override that to call a javascript function instead.
   myForm.addEventListener('submit', function(event) {
     document.getElementById("results").innerHTML = "Loading...";
     getData();
     event.preventDefault(); // cancels form submission
   });
});

// Take the state and county entered, and send to the java backend to request covid data
async function getData() {
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

    var cases  = Number(data.cases);
    var deaths = Number(data.deaths);

    results.innerHTML  = "For <b>" + county + "</b> county in <b>" + state + "</b>, there were...<br><br>";
    results.innerHTML += "<b>" + cases.toLocaleString()  + "</b> Cases of Covid-19<br><b>";
    results.innerHTML += "<b>" + deaths.toLocaleString() + "</b> Covid-19 related deaths.";
} 