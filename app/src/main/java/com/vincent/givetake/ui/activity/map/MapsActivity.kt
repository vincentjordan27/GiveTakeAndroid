package com.vincent.givetake.ui.activity.map

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.api.Status

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.vincent.givetake.R
import com.vincent.givetake.databinding.ActivityMapsBinding
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var data: AddressResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key), Locale.US);
        }

        binding.btnAddressMap.setOnClickListener {
            if (data == null) {
                Snackbar.make(binding.txtErrorMap, "Silahkan pilih lokasi", Snackbar.LENGTH_LONG).show()
            }else{
                val intent = Intent()
                intent.putExtra("data", data)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS_COMPONENTS ,Place.Field.ADDRESS))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val marker = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
                mMap.addMarker(MarkerOptions().position(marker))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15.0f))
                data = AddressResult(
                    place.latLng,
                    place.name,
                    place.address
                )
            }

            override fun onError(status: Status) {
                Log.i("DEBUG", "An error occurred: $status")
            }

        })


    }
}