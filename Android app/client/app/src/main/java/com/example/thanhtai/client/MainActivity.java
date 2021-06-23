package com.example.thanhtai.client;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.icu.text.UnicodeSetSpanner;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends Activity {
    LineChartView lineChartView;
    enum typed {
        hour,
        day,
        month
    }
    typed choose_type = typed.hour;
    ProgressDialog pDialog;
    String gResult="";
    Button bt_dv1,bt_dv2,bt_h,bt_d,bt_m,bt_e;
    EditText e_h,e_d,e_m;
    TextView tv_temp,tv_hum,tv_id;
    int device_select=1;
    chartData data=new chartData();
    private LineChartData data1;

    String feedAPI="https://io.adafruit.com/api/v2/anhlaga06/feeds/thanhtai/data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_dv1=findViewById(R.id.button_dv1);
        bt_dv2=findViewById(R.id.button_dv2);
        bt_h=findViewById(R.id.button_h);
        bt_d=findViewById(R.id.button_d);
        bt_m=findViewById(R.id.button_m);
        bt_e=findViewById(R.id.button_submit);
        e_d=findViewById(R.id.edit_d);
        e_h=findViewById(R.id.edit_h);
        e_m=findViewById(R.id.edit_m);
        tv_temp=findViewById(R.id.textView_temp);
        tv_hum=findViewById(R.id.textView_hum);
        tv_id=findViewById(R.id.textView_id);
        lineChartView = findViewById(R.id.chart);
        bt_dv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_dv1.setBackgroundColor(getResources().getColor(R.color.colorChoose));
                bt_dv2.setBackgroundColor(getResources().getColor(R.color.color1));
                device_select=1;
                drawChart(device_select);
            }
        });
        bt_dv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_dv2.setBackgroundColor(getResources().getColor(R.color.colorChoose));
                bt_dv1.setBackgroundColor(getResources().getColor(R.color.color1));
                device_select=2;
                drawChart(device_select);
            }
        });

        bt_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_h.setBackgroundColor(getResources().getColor(R.color.colorChoose));
                bt_d.setBackgroundColor(getResources().getColor(R.color.color1));
                bt_m.setBackgroundColor(getResources().getColor(R.color.color1));
                choose_type=typed.hour;
                getDataFeed(choose_type);
                e_h.setEnabled(true);
                e_d.setEnabled(true);

            }
        });

        bt_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_d.setBackgroundColor(getResources().getColor(R.color.colorChoose));
                bt_h.setBackgroundColor(getResources().getColor(R.color.color1));
                bt_m.setBackgroundColor(getResources().getColor(R.color.color1));
                choose_type=typed.day;
                getDataFeed(choose_type);
                e_h.setEnabled(false);
                e_d.setEnabled(true);

            }
        });

        bt_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_m.setBackgroundColor(getResources().getColor(R.color.colorChoose));
                bt_d.setBackgroundColor(getResources().getColor(R.color.color1));
                bt_h.setBackgroundColor(getResources().getColor(R.color.color1));
                choose_type=typed.month;
                getDataFeed(choose_type);
                e_h.setEnabled(false);
                e_d.setEnabled(false);
            }
        });

        bt_e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    int hour,day,month;
                    switch (choose_type){
                        case month:
                            month=Integer.parseInt(e_m.getText().toString());
                            //valider
                            if(month>12||month<1){
                                Toast.makeText(getApplicationContext(),"Invalid month's number!",Toast.LENGTH_LONG).show();
                                break;
                            }
                            //end valider
                            boolean cont11=getDataFeed_1(0,0,month,choose_type);
                            if (!cont11) {
                                Toast.makeText(getApplicationContext(),"Invalid month and date number",Toast.LENGTH_LONG).show();
                                break;
                            }
                            break;
                        case day:
                            month=Integer.parseInt(e_m.getText().toString());
                            day=Integer.parseInt(e_d.getText().toString());
                            //valider
                            if(month>12||month<1){
                                Toast.makeText(getApplicationContext(),"Invalid month's number!",Toast.LENGTH_LONG).show();
                                break;
                            }
                            if(day>31||day<1){
                                Toast.makeText(getApplicationContext(),"Invalid day's number!",Toast.LENGTH_LONG).show();
                                break;
                            }
                            //end valideer
                            boolean cont111=getDataFeed_1(0,day,month,choose_type);
                            if (!cont111) {
                                Toast.makeText(getApplicationContext(),"Invalid month and date number",Toast.LENGTH_LONG).show();
                                break;
                            }
                            break;
                        case hour:
                            month=Integer.parseInt(e_m.getText().toString());
                            day=Integer.parseInt(e_d.getText().toString());
                            hour=Integer.parseInt(e_h.getText().toString());
                            //valider
                            if(month>12||month<1){
                                Toast.makeText(getApplicationContext(),"Invalid month's number!",Toast.LENGTH_LONG).show();
                                break;
                            }
                            if(day>31||day<1){
                                Toast.makeText(getApplicationContext(),"Invalid date's number!",Toast.LENGTH_LONG).show();
                                break;
                            }
                            if(hour>23||hour<0){
                                Toast.makeText(getApplicationContext(),"Invalid hour's number!",Toast.LENGTH_LONG).show();
                                break;
                            }
                            //endvalider
                            boolean cont1=getDataFeed_1(hour,day,month,choose_type);
                            if (!cont1) {
                                Toast.makeText(getApplicationContext(),"Invalid month and date number",Toast.LENGTH_LONG).show();
                                break;
                            }
                            break;
                        default:
                            break;
                    }
                } catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        });

        startMQTT();
    }

    public class chartData{
        List<Double> temp1= new ArrayList<Double>();
        List<Double> hum1 = new ArrayList<Double>();
        List<String> time1 = new ArrayList<String>();
        List<Double> temp2= new ArrayList<Double>();
        List<Double> hum2 = new ArrayList<Double>();
        List<String> time2 = new ArrayList<String>();

        public void addData1(double temp1, double hum1, String time1){
            this.temp1.add(temp1);
            this.hum1.add(hum1);
            this.time1.add(time1);
        }
        public void addData2(double temp2, double hum2, String time2){
            this.temp2.add(temp2);
            this.hum2.add(hum2);
            this.time2.add(time2);
        }
        public void destroy(){
            if(!temp1.isEmpty()) {
                temp1.removeAll(temp1);
                hum1.removeAll(hum1);
                time1.removeAll(time1);
            }
            if (!temp2.isEmpty()) {
                temp2.removeAll(temp2);
                hum2.removeAll(hum2);
                time2.removeAll(time2);
            }
        }

    }
    private boolean getDataFeed_1(int hour,int day, int month,typed type){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        String start_time;
        String end_time;
        SimpleDateFormat format1;
        String urls;
        ///////////////

        try {
            //switch case
            switch (type){
                case hour:
                    boolean dnw;
                    if(hour<12) dnw=true;
                    else {
                        dnw=false;
                        hour=hour-12;
                    }
                    c.set(Calendar.HOUR,hour);
                    c.set(Calendar.AM_PM,Calendar.PM);
                    c.set(Calendar.MONTH,month-1);
                    c.set(Calendar.DATE,day);
                    //start time
                    if(dnw) c.add(Calendar.HOUR,-19);
                    else c.add(Calendar.HOUR,-7);
                    format1 = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
                    start_time = format1.format(c.getTime());
                    start_time=start_time.replace(" ","T");
                    start_time=start_time+"Z";
                    //end time
                    c.add(Calendar.HOUR,1);
                    end_time = format1.format(c.getTime());
                    end_time=end_time.replace(" ","T");
                    end_time=end_time+"Z";
                    urls=feedAPI+"?start_time="+start_time+"&end_time="+end_time;
                    getLatestInfor(urls);
                    //Toast.makeText(this,urls,Toast.LENGTH_LONG).show();
                    break;
                case day:
                    c.set(Calendar.MONTH,month-1);
                    c.set(Calendar.DATE,day);
                    //start time
                    c.add(Calendar.HOUR,-7);
                    format1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                    start_time = format1.format(c.getTime());
                    start_time=start_time.replace(" ","T");
                    start_time=start_time+"Z";
                    //end time
                    c.add(Calendar.DATE,1);
                    end_time = format1.format(c.getTime());
                    end_time=end_time.replace(" ","T");
                    end_time=end_time+"Z";
                    urls=feedAPI+"?start_time="+start_time+"&end_time="+end_time;
                    getLatestInfor(urls);
                    //Toast.makeText(this,urls,Toast.LENGTH_LONG).show();
                    break;
                case month:
                    c.set(Calendar.MONTH,month-1);
                    //start time
                    c.add(Calendar.HOUR,-7);
                    format1 = new SimpleDateFormat("yyyy-MM-00 00:00:00");
                    start_time = format1.format(c.getTime());
                    start_time=start_time.replace(" ","T");
                    start_time=start_time+"Z";
                    //end time
                    c.add(Calendar.MONTH,1);
                    end_time = format1.format(c.getTime());
                    end_time=end_time.replace(" ","T");
                    end_time=end_time+"Z";
                    urls=feedAPI+"?start_time="+start_time+"&end_time="+end_time;
                    getLatestInfor(urls);
                    //Toast.makeText(this,urls,Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
        catch (Exception e) {
            return false;
        }
        return  true;
    }
    private void getDataFeed(typed type){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String start_time;
        String end_time;
        SimpleDateFormat format1;
        String urls;

        switch (type){
            case hour:
                //start time
                c.add(Calendar.HOUR,-7);
                format1 = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
                start_time = format1.format(c.getTime());
                start_time=start_time.replace(" ","T");
                start_time=start_time+"Z";
                //end time
                c.add(Calendar.HOUR,1);
                end_time = format1.format(c.getTime());
                end_time=end_time.replace(" ","T");
                end_time=end_time+"Z";
                urls=feedAPI+"?start_time="+start_time+"&end_time="+end_time;
                getLatestInfor(urls);
                break;
            case day:
                //start time
                c.add(Calendar.HOUR,-7);
                format1 = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                start_time = format1.format(c.getTime());
                start_time=start_time.replace(" ","T");
                start_time=start_time+"Z";
                //end time
                c.add(Calendar.DATE,1);
                end_time = format1.format(c.getTime());
                end_time=end_time.replace(" ","T");
                end_time=end_time+"Z";
                urls=feedAPI+"?start_time="+start_time+"&end_time="+end_time;
                getLatestInfor(urls);
                break;
            case month:
                //start time
                c.add(Calendar.HOUR,-7);
                format1 = new SimpleDateFormat("yyyy-MM-00 00:00:00");
                start_time = format1.format(c.getTime());
                start_time=start_time.replace(" ","T");
                start_time=start_time+"Z";
                //end time
                c.add(Calendar.MONTH,1);
                end_time = format1.format(c.getTime());
                end_time=end_time.replace(" ","T");
                end_time=end_time+"Z";
                urls=feedAPI+"?start_time="+start_time+"&end_time="+end_time;
                getLatestInfor(urls);
                break;
            default:
                break;
        }
    }
    private void getLatestInfor(String urls){
        pDialog = new ProgressDialog(this);
        // Set progressbar title
        pDialog.setTitle("Getting data from server");
        // Set progressbar message
        pDialog.setMessage("Please wait...");
        pDialog.show();
        threadtest thread = new threadtest(urls);
        thread.start();
    }
    Handler handler_response = new Handler(){
        public void handleMessage(Message msg){
            pDialog.dismiss();
            pDialog.setTitle("Prepare data to draw chart");
            // Set progressbar message
            pDialog.setMessage("Please wait...");
            pDialog.show();
            boolean cont=readJSONString(gResult,choose_type);
            if(cont){
                drawChart(device_select);
            } else {
                Toast.makeText(getApplicationContext(),"Something wrong!!!\n Try to click again!",Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();

        }
    };
    //fixing hour
    private void drawChart(int device_select){

        if(device_select==1){
            if(data.time1.isEmpty()){
                Toast.makeText(this,"No data from server",Toast.LENGTH_LONG).show();
                return;
            }
            if(data.time1.size()==1){
                Toast.makeText(this,"Not enough data",Toast.LENGTH_LONG).show();
                return;
            }
            double[] dA=new double[data.temp1.size()];
            int idx1=0;
            for(int i=data.temp1.size();i>0;i--){
                dA[i-1]=data.temp1.get(idx1++);
            }
            idx1=0;
            double[] dB=new double[data.hum1.size()];
            for(int i=data.hum1.size();i>0;i--){
                dB[i-1]=data.hum1.get(idx1++);
            }
            String[] tA=data.time1.toArray(new String[0]);
            String[] tA1=data.time1.toArray(new String[0]);
            for(int i=0;i<data.time1.size();i++){
                tA[i]=tA1[data.time1.size()-i-1];
            }
            generateTempoData(dA,dB,tA,choose_type);
        }
        else if(device_select==2){
            if(data.time2.isEmpty()){
                Toast.makeText(this,"No data from server",Toast.LENGTH_LONG).show();
                return;
            }
            if(data.time2.size()==1){
                Toast.makeText(this,"Not enough data",Toast.LENGTH_LONG).show();
                return;
            }
            double[] dA=new double[data.temp2.size()];
            int idx1=0;
            for(int i=data.temp2.size();i>0;i--){
                dA[i-1]=data.temp2.get(idx1++);
            }
            idx1=0;
            double[] dB=new double[data.hum2.size()];
            for(int i=data.hum2.size();i>0;i--){
                dB[i-1]=data.hum2.get(idx1++);
            }
            String[] tA=data.time2.toArray(new String[0]);
            String[] tA1=data.time2.toArray(new String[0]);
            for(int i=0;i<data.time2.size();i++){
                tA[i]=tA1[data.time2.size()-i-1];
            }
            generateTempoData(dA,dB,tA,choose_type);
        }
        else {
            Toast.makeText(this,"id not founded!",Toast.LENGTH_LONG).show();
        }
    }

    private class threadtest extends Thread {
        String urls;
        threadtest(String urls){
            this.urls=urls;
        }
        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL(urls);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null) {
                    result.append(line);
                }
                //Toast.makeText(this,result.toString(),Toast.LENGTH_LONG).show();
                gResult=result.toString();
                handler_response.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
        }
    }

    //////////////////////////////////////////
    private void generateTempoData(double[] tem,double[] hum, String[] minus, typed type) {
        // I got speed in range (0-50) and height in meters in range(200 - 300). I want this chart to display both
        // information. Differences between speed and height values are large and chart doesn't look good so I need
        // to modify height values to be in range of speed values.

        // The same for displaying Tempo/Height chart.
        double[] temp1=tem;
        double[] hum1=hum;
        String[] minus1=minus;
        String labelBot="";
        //sorting to set viewport
        double[] stemp=tem;
        double[] shum=hum;
        Arrays.sort(stemp);
        Arrays.sort(shum);
        int minv=(int)stemp[0],maxv=(int)stemp[stemp.length-1];
        if(stemp[0]*2>shum[0]){
            minv=(int)(shum[0]/2);
        }
        if(stemp[stemp.length-1]*2<shum[shum.length-1]){
            maxv=(int)(shum[shum.length-1]/2);
        }
        switch (type){
            case month:
                labelBot="Time: [Day]";
                break;
            case day:
                labelBot="Time: [Hour]";
                break;
            case hour:
                labelBot="Time: [Minute]";
                break;
            default:
                break;
        }

        float minHeight = 0;
        float maxHeight = 100;
        float tempoRange = 50; // from 0min/km to 15min/km

        float scale = tempoRange / maxHeight;
        float sub = (minHeight * scale) / 2;

        if(temp1.length!=minus1.length) return;
        int numValues = temp1.length;

        List axisValues1 = new ArrayList();
        for(int i = 0; i < minus1.length; i++){
            axisValues1.add(i, new AxisValue(i).setLabel(minus1[i]));
        }

        Line line;
        List<PointValue> values;
        List<Line> lines = new ArrayList<Line>();

        // Height line, add it as first line to be drawn in the background.
        values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            // Some random height values, add +200 to make line a little more natural
            float rawHeight = (float) (hum1[i]);
            float normalizedHeight = rawHeight * scale - sub;
            values.add(new PointValue(i, normalizedHeight));
        }

        line = new Line(values);
        line.setColor(Color.GRAY);
        line.setHasPoints(false);
        line.setStrokeWidth(3);
        line.setHasLines(true);
        lines.add(line);

        // Tempo line is a little tricky because worse tempo means bigger value for example 11min per km is worse
        // than 2min per km but the second should be higher on the chart. So you need to know max tempo and
        // tempoRange and set
        // chart values to minTempo - realTempo.
        values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            // Some random raw tempo values.
            float realTempo = (float) temp1[i];
            //float revertedTempo = tempoRange - realTempo;
            values.add(new PointValue(i, realTempo));
        }

        line = new Line(values);
        line.setColor(ChartUtils.COLOR_RED);
        line.setHasPoints(false);
        line.setHasLines(true);
        line.setStrokeWidth(3);
        lines.add(line);

        // Data and axes
        data1 = new LineChartData(lines);

        // Distance axis(bottom X) with formatter that will ad [km] to values, remember to modify max label charts
        // value.
        Axis distanceAxis = new Axis(axisValues1);
        distanceAxis.setName(labelBot);
        distanceAxis.setTextColor(ChartUtils.COLOR_ORANGE);
        distanceAxis.setMaxLabelChars(4);
        //distanceAxis.setFormatter(new SimpleAxisValueFormatter().setAppendedText("km".toCharArray()));
        distanceAxis.setHasTiltedLabels(true);
        distanceAxis.setHasLines(true);
        data1.setAxisXBottom(distanceAxis);

        // Tempo uses minutes so I can't use auto-generated axis because auto-generation works only for decimal
        // system. So generate custom axis values for example every 15 seconds and set custom labels in format
        // minutes:seconds(00:00), you could do it in formatter but here will be faster.
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for (float i = 0; i < tempoRange; i += 0.25f) {
            // I'am translating float to minutes because I don't have data in minutes, if You store some time data
            // you may skip translation.
            axisValues.add(new AxisValue(i).setLabel(formatMinutes(i)));
        }

        Axis tempoAxis = new Axis(axisValues).setName("Temperature [C]").setHasLines(true).setMaxLabelChars(4)
                .setTextColor(ChartUtils.COLOR_RED);
        data1.setAxisYLeft(tempoAxis);

        // *** Same as in Speed/Height chart.
        // Height axis, this axis need custom formatter that will translate values back to real height values.
        data1.setAxisYRight(new Axis().setName("Humidity [%]").setMaxLabelChars(3)
                .setFormatter(new HeightValueFormatter(scale, sub, 0)));

        // Set data

        lineChartView.setLineChartData(data1);

        // Important: adjust viewport, you could skip this step but in this case it will looks better with custom
        // viewport. Set
        // viewport with Y range 0-12;
        Viewport v = lineChartView.getMaximumViewport();
        v.set(v.left, maxv+2, v.right, minv-2);
        lineChartView.setMaximumViewport(v);
        lineChartView.setCurrentViewport(v);

    }

    private String formatMinutes(float value) {
        StringBuilder sb = new StringBuilder();

        // translate value to seconds, for example
        int valueInSeconds = (int) (value * 60);
        int minutes = (int) Math.floor(valueInSeconds / 60);


        sb.append(String.valueOf(minutes));
        return sb.toString();
    }

    /**
     * Recalculated height values to display on axis. For this example I use auto-generated height axis so I
     * override only formatAutoValue method.
     */
    private static class HeightValueFormatter extends SimpleAxisValueFormatter {

        private float scale;
        private float sub;
        private int decimalDigits;

        public HeightValueFormatter(float scale, float sub, int decimalDigits) {
            this.scale = scale;
            this.sub = sub;
            this.decimalDigits = decimalDigits;
        }

        @Override
        public int formatValueForAutoGeneratedAxis(char[] formattedValue, float value, int autoDecimalDigits) {
            float scaledValue = (value + sub) / scale;
            return super.formatValueForAutoGeneratedAxis(formattedValue, scaledValue, this.decimalDigits);
        }
    }
    /////////////////////////////////////////

    private boolean readJSONString(String JSONstring, typed type){
        try {
            data.destroy();

            JSONArray jsonArray = new JSONArray(JSONstring);
            int minus1=0,minus2=0;
            double temp1=0.0,hum1=0.0,temp2=0.0,hum2=0.0;
            String point1="begin",point2="begin";

            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                String valueS = object.getString("value");
                JSONObject valueOb = new JSONObject(valueS);
                String id_data=valueOb.getString("id");
                String minus_point=time_of_type(object.getString("created_at"),choose_type);
                String temp_data=valueOb.getString("temp");
                String hum_data=valueOb.getString("hum");
                if(id_data.equals("1")){
                    if(point1.equals("begin")){
                        point1=minus_point;
                        minus1++;
                        temp1+=Double.parseDouble(temp_data);
                        hum1+=Double.parseDouble(hum_data);
                    } else{
                        if(point1.equals(minus_point)){
                            minus1++;
                            temp1+=Double.parseDouble(temp_data);
                            hum1+=Double.parseDouble(hum_data);
                        } else {
                            data.addData1(temp1/minus1,hum1/minus1,point1);
                            point1=minus_point;
                            minus1=1;
                            temp1=Double.parseDouble(temp_data);
                            hum1=Double.parseDouble(hum_data);
                        }
                    }
                }
                else if(id_data.equals("2")){
                    if(point2.equals("begin")){
                        point2=minus_point;
                        minus2++;
                        temp2+=Double.parseDouble(temp_data);
                        hum2+=Double.parseDouble(hum_data);
                    } else{
                        if(point2.equals(minus_point)){
                            minus2++;
                            temp2+=Double.parseDouble(temp_data);
                            hum2+=Double.parseDouble(hum_data);
                        } else {
                            data.addData2(temp2/minus2,hum2/minus2,point2);
                            point2=minus_point;
                            minus2=1;
                            temp2=Double.parseDouble(temp_data);
                            hum2=Double.parseDouble(hum_data);
                        }
                    }
                }
                if(i==jsonArray.length()-1){
                    if(!point1.equals("begin")){
                        data.addData1(temp1/minus1,hum1/minus1,point1);
                    }
                    if(!point2.equals("begin")){
                        data.addData2(temp2/minus2,hum2/minus2,point1);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String time_of_type(String time, typed type){
        switch (type){
            case hour:
                return time.split(":")[1];
            case day:
                return time.split(":")[0].split("T")[1];
            case month:
                return time.split("T")[0].split("-")[2];
            default:
                break;
        }
        return null;
    }

    //mqtt
    MQTTHelper mqttHelper;
    private void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext());
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.d("MQTT", mqttMessage.toString());
                //textView_data.setText(mqttMessage.toString());
                //textView_topic.setText(topic);
                JSONObject ob = new JSONObject(mqttMessage.toString());
                tv_id.setText(ob.getString("id"));
                String h=ob.getString("hum")+" [%]";
                tv_hum.setText(h);
                String t=ob.getString("temp")+" [C]";
                tv_temp.setText(t);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    private void sendDataToMQTT(String ID, String value){

        MqttMessage msg = new MqttMessage(); msg.setId(1234);
        msg.setQos(0); msg.setRetained(true);

        String data = ID + ":" + value;

        byte[] b = data.getBytes(Charset.forName("UTF-8")); msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish("anhlaga06/feeds/thanhtai", msg);

        }catch (MqttException e){
        }
    }
}
