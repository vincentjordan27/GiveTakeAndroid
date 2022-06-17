package com.vincent.givetake.ui.activity.map

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

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
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var data: AddressResult? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private var isMedan : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                if (isMedan) {
                    val intent = Intent()
                    intent.putExtra("data", data)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Snackbar.make(binding.txtErrorMap, "Anda harus berada dimedan untuk menggunakan aplikasi ini", Snackbar.LENGTH_LONG).show()
                }
            }
        }

        binding.btnMyLocation.setOnClickListener {
            getMyLastLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        autocompleteFragment =
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
                if (place.address?.contains("Medan") == true) {
                    isMedan = true
                }
            }

            override fun onError(status: Status) {
                Log.i("DEBUG", "An error occurred: $status")
            }

        })
    }

    private fun checkPermission(permission: String) : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
            else -> {
                showToast("Akses lokasi tidak diberikan")
            }
        }
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            showLoading(true)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    showMarkerMyLocation(location)
                } else {
                    showToast("Lokasi tidak ditemukan. Coba lagi")
                }
            }
        }else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showMarkerMyLocation(location: Location) {
        showLoading(false)
        val myLocation = LatLng(location.latitude, location.longitude)
        val geocoder = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        data = AddressResult(
            myLocation,
            geocoder[0].featureName,
            geocoder[0].getAddressLine(0)
        )
        if (geocoder[0].getAddressLine(0).contains("Medan")) {
            isMedan = true
        }
        mMap.addMarker(
            MarkerOptions()
                .position(myLocation)
                .title("Lokasi Saya")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17F))
        autocompleteFragment.setText("")
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pg.visibility = View.VISIBLE
        } else {
            binding.pg.visibility = View.GONE
        }
    }
}