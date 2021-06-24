google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawVisualization);

function drawVisualization() {
  // Some raw data (not necessarily accurate)
  var table = google.visualization.arrayToDataTable([
    ['Time', 'Humidity', 'Temperature'],
    ['0/0',  0, 0]
  ]);
  var table2 = google.visualization.arrayToDataTable([
    ['Time', 'Humidity', 'Temperature'],
    ['0/0',  0, 0]
  ]);
  var subdata = [];
  var subdata2 = [];
  $.getJSON('https://io.adafruit.com/api/v2/anhlaga06/feeds/thanhtai/data', function(data) {
    data.forEach(element => {
      date = element.created_at.substring(0,10);
      time = element.created_at.substring(11,19);
      dati = time + "(" +date+")";
      const obj = JSON.parse(element.value);
      if(obj.id == 1){
        subdata.push([dati, parseFloat(obj.hum),parseFloat(obj.temp)]);
      }
      if(obj.id == 2){
        subdata2.push([dati, parseFloat(obj.hum),parseFloat(obj.temp)]);
      }
    });
    subdata = subdata.reverse();
    subdata2 = subdata2.reverse();
    console.log("get data done!");
    subdata.forEach(element =>{
      table.addRow(element);
    });
    subdata2.forEach(element =>{
        table2.addRow(element);
      });
    var options = {
      title : 'Node sensor 1',
      vAxis: {title: 'C(%)'},
      hAxis: {title: 'Time(date)'},
      seriesType: 'bars',
    };

    var options2 = {
        title : 'Node sensor 2',
        vAxis: {title: 'C(%)'},
        hAxis: {title: 'Time(date)'},
        seriesType: 'bars',
      };
  
    var chart = new google.visualization.ComboChart(document.getElementById('chart_div'));
    var chart2 =new google.visualization.ComboChart(document.getElementById('chart_div2'));
    chart.draw(table, options);
    chart2.draw(table2, options2);
  });
}