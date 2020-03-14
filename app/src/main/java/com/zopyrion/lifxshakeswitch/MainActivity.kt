package com.zopyrion.lifxshakeswitch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import io.github.rybalkinsd.kohttp.dsl.async.httpPostAsync
import io.github.rybalkinsd.kohttp.dsl.httpPost
import io.github.rybalkinsd.kohttp.dsl.httpPut
import io.github.rybalkinsd.kohttp.ext.url
import io.github.rybalkinsd.kohttp.util.json
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import android.hardware.SensorEventListener



class MainActivity : AppCompatActivity(), SensorEventListener  {


    private var isLightOn = false

    private lateinit var mSensorManager: SensorManager
    private var mSensors: Sensor? = null
    private var mAccel: Float = 0.toFloat()
    private var mAccelCurrent: Float = 0.toFloat()
    private var mAccelLast: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensors = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(this, mSensors, SensorManager.SENSOR_DELAY_NORMAL)
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


        //


    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(p0: SensorEvent) {
        val x = p0.values[0]
        val y = p0.values[1]
        val z = p0.values[2]
        mAccelLast = mAccelCurrent
        mAccelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = mAccelCurrent - mAccelLast
        mAccel = mAccel * 0.9f + delta

        if (mAccel > 12) {
            toggle()
        }

    }


    private fun toggle(){
        if(isLightOn){
            switch("off")
            isLightOn = false
        } else {
            switch("on")
            isLightOn = true
        }

    }


    private fun switch(state: String){

        val response = httpPut   {
            url ("https://api.lifx.com/v1/lights/all/state")
            header {
                "Authorization" to "Bearer {token}"
            }
            body {
                json {
                    "power" to state
                }
            }
        }
    }
}
