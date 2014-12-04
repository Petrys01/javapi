package com.ste0067.gpstracker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ste0067.gpstracker.MainActivity.PlaceholderFragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapsActivity extends PlaceholderFragment {

	GoogleMap googleMap;
	MarkerOptions markerOptions;
	Marker marker;
	PolylineOptions polylineOptions;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_maps, container, false);
		
		
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		markerOptions = new MarkerOptions();
		polylineOptions = new PolylineOptions();
		
        return view;
	}
	
	public void SetPosition(double lat, double lon) {

		LatLng position = new LatLng(lat, lon);

		//googleMap.setMyLocationEnabled(true);
		if (marker != null) {
			marker.remove();
		}
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
		
		markerOptions.title("Moje aktuální Pozice");
		markerOptions.snippet("GPS Tracker");
        markerOptions.anchor(0.0f, 1.0f);
        markerOptions.position(position);
        
        marker = googleMap.addMarker(markerOptions);

	}
	
	public void AddPoint(double lat, double lon) {
		
        polylineOptions.add(new LatLng(lat, lon));
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(30.0f);
        googleMap.addPolyline(polylineOptions);
	}
}
