let quote = document.getElementById("quote");
let short_quote = document.getElementById("short_quote");
let button = document.getElementById("button");


/*
container_darker.style.display = "none";
img_response.style.display="none";
response.style.display = "none";
time_response.display = "none";
*/

const url = "https://api.quotable.io/random";

//var short_quote;
var password=document.getElementById("password");
var max_words = 10;

let getQuote = () => {
  fetch(url)
    .then((data) => data.json())
    .then((item) => {
      quote.innerText = item.content;
      short_quote.innerText = item.content.substring(0,max_words) + "...";
    });
}; 

button.addEventListener("click", getQuote);

function time(){
    var timestamp = Date.now();     
    var tmp = new Date(timestamp);
    document.getElementById("time").innerHTML = tmp;

}

function random_generator(){
  var chars = "0123456789abcdefghijklmnopqrstuvwxyz!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  var passwordLength = 12;
  var password = "";
  for (var i = 0; i <= passwordLength; i++) {
    var randomNumber = Math.floor(Math.random() * chars.length);
    password += chars.substring(randomNumber, randomNumber +1);
  }
  document.getElementById("password").value = password;
  document.getElementById("confirmPassword").value = password;
}

function response_message(){
  var container_response = document.getElementById("container_response");
  let div = document.createElement("div");
  div.className = "container darker";

  var img_response = document.createElement("img");
  img_response.src = "https://docs.htmlcsstoimage.com/assets/images/dog.jpg";
  img_response.className = "right";
  div.appendChild(img_response);
  
  var response = document.createElement("p");
  response.innerHTML = document.getElementById("text").value;
  div.appendChild(response);

  var time_response = document.createElement("span");
  time_response.className = "time-left";
  time_response.innerHTML = "11:01";
  div.appendChild(time_response);

  container_response.appendChild(div);

  short_quote.innerText = document.getElementById("text").value.substring(0,max_words) + "...";

}
document.getElementById("response_message").addEventListener("click",response_message);