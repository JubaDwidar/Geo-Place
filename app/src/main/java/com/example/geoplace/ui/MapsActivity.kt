package com.example.geoplace.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.geoplace.R.*
import com.example.geoplace.databinding.ActivityMapsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.skyfishjy.library.RippleBackground


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


  private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mFusedLocation: FusedLocationProviderClient
    private lateinit var location: Location
    private lateinit var materialSearchBar: MaterialSearchBar
    private lateinit var rippleBackground: RippleBackground
    private lateinit var locationRequest: LocationRequest
    private val DEFAULT_ZOOM: Float = 15.0F
    private lateinit var locationCallBack: LocationCallback
    private lateinit var placesClient: PlacesClient
    private lateinit var prediction: List<AutocompletePrediction>
    private val TAG = "MapsActivity"
    private lateinit var mapView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this)
        materialSearchBar = binding.searchBar

        val mapFragment = supportFragmentManager.findFragmentById(id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.requireView()

        Places.initialize(this, "AIzaSyAwsEWZavp2cjDnC6u-JHdLy8EayA8TYlg")
        placesClient = Places.createClient(this)
        val token = AutocompleteSessionToken.newInstance()

        materialSearchBar.setOnSearchActionListener(object :
                MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {

                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    return
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch()
                }
            }

            override fun onSearchStateChanged(enabled: Boolean) {
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString(), true, null, true)
            }

        })

        materialSearchBar.setSuggestionsClickListener(object :
                SuggestionsAdapter.OnItemViewClickListener {


            override fun OnItemDeleteListener(position: Int, v: View?) {

            }


            override fun OnItemClickListener(position: Int, v: View?) {
                if (position >= prediction.size) return
                var selectPrediction = prediction[position]
                var suggestion = materialSearchBar.lastSuggestions.get(position).toString()
                materialSearchBar.text = suggestion

                Handler().postDelayed(
                        Runnable
                        {
                            materialSearchBar.clearSuggestions();
                        }, 1000
                )
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                        materialSearchBar.windowToken,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                )
                val placeId = selectPrediction.placeId
                val placeFields = listOf(Place.Field.LAT_LNG)
                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest)
                        .addOnSuccessListener {
                            val place = it.place
                            Log.e(TAG, "Place found: " + place.name);

                            val latLng = place.latLng
                            if (latLng != null) {
                                mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat())
                                )
                            }
                        }
                        .addOnFailureListener {
                            if (it is ApiException) {
                                val apiException = it
                                apiException.printStackTrace()
                                val statusCode = apiException.statusCode
                                Log.i(TAG, "place not found: " + it.message);
                                Log.i(TAG, "status code: $statusCode");
                            }
                        }
            }
        })

        materialSearchBar.addTextChangeListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                        val predctionRequest = FindAutocompletePredictionsRequest.builder()
                        predctionRequest.typeFilter = TypeFilter.ADDRESS
                        predctionRequest.query = s.toString()
                        predctionRequest.sessionToken = token

/*
                        val prhgsaj = FindAutocompletePredictionsRequest.builder()
                        prhgsaj.typeFilter = TypeFilter.ADDRESS
                        prhgsaj.build()


                        val predictionRequest = FindAutocompletePredictionsRequest.builder().apply {
                            this.typeFilter = TypeFilter.ADDRESS
                            this.sessionToken = token
                            this.query = s.toString()
                        }.build()
  */
                        placesClient.findAutocompletePredictions(predctionRequest.build())
                                .addOnCompleteListener(OnCompleteListener {

                                    Log.e(
                                            TAG,
                                            "it is  successful" + it.isSuccessful + it.result.autocompletePredictions.size + it.result.autocompletePredictions[1]
                                    );


                                    if (it.isSuccessful) {

                                        Log.e(TAG, "it is  successful" + it.result.toString());

                                        val predictionsResponse = it.result
                                        predictionsResponse.autocompletePredictions[1]

                                        Log.e(
                                                TAG,
                                                "it is  successful annnnd " + predictionsResponse.autocompletePredictions[1]
                                        )

                                        if (predictionsResponse != null) {
                                            prediction = predictionsResponse.autocompletePredictions
                                            val sugesstionList = ArrayList<String>()
                                            for (sugesstion in 1..sugesstionList.size) {
                                                val prediction = prediction[sugesstion]
                                                sugesstionList.add(prediction.getFullText(null).toString());
                                                Log.e(TAG, "prediction fetching task successful");

                                            }
                                            materialSearchBar.updateLastSuggestions(sugesstionList)
                                            if (!materialSearchBar.isSuggestionsVisible) {
                                                materialSearchBar.showSuggestionsList();
                                            }

                                        }
                                    } else {
                                        Log.e(TAG, "prediction fetching task unsuccessful"+it.exception?.message.toString());
                                    }
                                })
                    }

                })


    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {

        if (googleMap != null) {
            mMap = googleMap
        } else {
            Log.e(TAG, "onMapReady: googleMap is null")
        }

        val locationButton =
                (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                        Integer.parseInt("2")
                )
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30)



        mMap.isMyLocationEnabled = true;
        mMap.uiSettings.isMyLocationButtonEnabled = true;

        //check if gps is enabled or not and then request user to enable it
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        val locationSettingBuilder =
                LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val task = settingsClient.checkLocationSettings(locationSettingBuilder.build())
        task.addOnSuccessListener {
            getLocation()
        }
        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(this, 51)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                    Log.e(TAG, "add on failure")
                }
            }
        }

        mMap.setOnMyLocationButtonClickListener {
            if (materialSearchBar.isSuggestionsVisible)
                materialSearchBar.clearSuggestions();
            if (materialSearchBar.isSearchOpened)
                materialSearchBar.closeSearch()
            false
        }


    }


    @SuppressLint("MissingPermission")
    fun getLocation() {
        mFusedLocation.lastLocation.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                location = it.result
            }

            if (it != null) {
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                LatLng(location.latitude, location.longitude),
                                DEFAULT_ZOOM
                        )
                )

            } else {
                locationRequest = LocationRequest.create()
                locationRequest.interval = 10000
                locationRequest.fastestInterval = 5000
                locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

                locationCallBack = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        location = locationResult.lastLocation
                        mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                                location.latitude,
                                                location.longitude
                                        ), DEFAULT_ZOOM
                                )
                        )
                        mFusedLocation.removeLocationUpdates(locationCallBack)

                    }


                }
                mFusedLocation.requestLocationUpdates(locationRequest, locationCallBack, null)
            }

            Toast.makeText(this, "unable to get last location", Toast.LENGTH_SHORT).show();


        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getLocation()
            }
        }
    }


}