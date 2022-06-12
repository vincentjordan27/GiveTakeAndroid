package com.vincent.givetake.ui.activity.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.vincent.givetake.R
import com.vincent.givetake.databinding.ActivityMapDirectionBinding
import com.vincent.givetake.utils.Constant
import com.vincent.givetake.utils.decodePolyline

class MapDirectionActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapDirectionBinding
    private lateinit var mapDirectionData: MapDirectionData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapDirectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapDirectionData = intent.getParcelableExtra(Constant.MAP_DIRECTION_DATA)!!

        binding.tvMyAddress.text = mapDirectionData.addressFrom
        binding.tvGoalAddress.text = mapDirectionData.addressTo
        binding.tvBack.setOnClickListener {
            onBackPressed()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val apiKey = getString(R.string.api_key)
        val url = getDirectionURL(mapDirectionData.from, mapDirectionData.to, apiKey)
        binding.pg.visibility = View.VISIBLE
        GetDirection(url).execute()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body().string()
            Log.d("DEBUGS", data.toString())
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,MapData::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLACK)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
            mMap.addMarker(
                MarkerOptions()
                    .position(mapDirectionData.from)
            )
            mMap.addMarker(
                MarkerOptions()
                    .position(mapDirectionData.to)
            )
            binding.pg.visibility = View.GONE
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapDirectionData.from, 14F))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}