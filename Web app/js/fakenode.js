client = new Paho.MQTT.Client("io.adafruit.com", Number(443) , "clientId");

// set callback handlers
client.onConnectionLost = function (responseObject) {
    console.log("Connection Lost: "+responseObject.errorMessage);
}

// Called when the connection is made
function onConnect(){
    console.log("Connected!");
    client.subscribe("anhlaga06/feeds/thanhtai");
}

// Connect the client, providing an onConnect callback
// Connect the client, with a Username and Password
client.connect({
    onSuccess: onConnect, 
    userName : "anhlaga06",
    password : "aio_klDy98W4KK11yweUSpakQ8sCHSta"
});

setInterval(function(){
  temp = Math.floor(Math.random() * 25 + 20);
  hum = Math.floor(Math.random() * 30 + 20);
  strtemp = temp.toString();
  strhum = hum.toString();
  data = {"id":"2", "temp":strtemp,"hum": strhum }; 
  console.log(JSON.stringify(data));
  message = new Paho.MQTT.Message(JSON.stringify(data));
  message.destinationName = "anhlaga06/feeds/thanhtai";
  client.send(message);
  document.getElementById("temp").innerHTML = "temp: " + temp;
  document.getElementById("hum").innerHTML ="hum: " + hum;
}, 10000);