$(function(){
    let ss1 = true;
    let ss2 = false;
    let ss3 = false;
    let hum1 = 0;
    let temp1 = 0;
    let hum2 = 0;
    let temp2 = 0;


    function displaySensor1(){
        let lastClasstemp = $('#T').attr('class').split(' ').pop();
        $('#T').removeClass(lastClasstemp);     
        newTemp = "p"+ String(Math.round(temp1));
        $('#T').addClass(newTemp);

        let lastClasshum = $('#H').attr('class').split(' ').pop();
        $('#H').removeClass(lastClasshum);     
        newhum = "p"+ String(Math.round(hum1));
        $('#H').addClass(newhum);
        
        $("#temp").html(temp1);
        $("#humi").html(hum1);
    }

    function displaySensor2() {
        let lastClasstemp = $('#T').attr('class').split(' ').pop();
        $('#T').removeClass(lastClasstemp);     
        newTemp = "p"+ String(Math.round(temp2));
        $('#T').addClass(newTemp);

        let lastClasshum = $('#H').attr('class').split(' ').pop();
        $('#H').removeClass(lastClasshum);     
        newhum = "p"+ String(Math.round(hum2));
        $('#H').addClass(newhum);
        
        $("#temp").html(temp2);
        $("#humi").html(hum2);
    }

    // Create a client instance: Broker, Port, Websocket Path, Client ID
    client = new Paho.MQTT.Client("io.adafruit.com", Number(443) , "clientId");

    // set callback handlers
    client.onConnectionLost = function (responseObject) {
        console.log("Connection Lost: "+responseObject.errorMessage);
    }

    client.onMessageArrived = function (message) {
        console.log("Message Arrived: "+message.payloadString);
        const obj = JSON.parse(message.payloadString); 
        if(obj.id == 1){
            temp1 =obj.temp;
            hum1 = obj.hum;
        }
        if(obj.id == 2){
            temp2 = obj.temp;
            hum2 = obj.hum;
        }
        if(ss1 == true){
            displaySensor1();
        }
        if(ss2 == true){

            displaySensor2();
        }
        if(ss3 == true){
            
        }
    
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

    $('[data-toggle="tooltip"]').tooltip(); 

    $('.nut1').click(function (e) { 
        ss1 = true;
        ss2 = false;
        ss3 = false;
        $('.nut1.btn.btn-default').addClass('active');
        $('.nut2.btn.btn-default').removeClass('active');
        $('.nut3.btn.btn-default').removeClass('active');
        displaySensor1();
    });

    $('.nut2').click(function (e) { 
        ss1 = false;
        ss2 = true;
        ss3 = false;
        $('.nut2.btn.btn-default').addClass('active');
        $('.nut1.btn.btn-default').removeClass('active');
        $('.nut3.btn.btn-default').removeClass('active');
        displaySensor2();
    });

    $('.nut3').click(function (e) { 
        ss1 = false;
        ss2 = false;
        ss3 = true;
        $('.nut3.btn.btn-default').addClass('active');
        $('.nut2.btn.btn-default').removeClass('active');
        $('.nut1.btn.btn-default').removeClass('active');
    });
})  
 