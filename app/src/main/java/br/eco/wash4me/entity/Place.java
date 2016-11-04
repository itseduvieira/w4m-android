package br.eco.wash4me.entity;

import android.location.Location;

import java.util.List;
import java.util.TreeSet;

public class Place implements Comparable<Place> {
	private String title;
	private String description;
	private double latitude;
	private double longitude;
	private String address;
	private String number;
	private String info;
	private String city;
	private String neighbourhood;
	private String state;
	private String zipCode;
	
	private Integer distanceToMyPlace;

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return title + " - " + description;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getNeighbourhood() {
		return neighbourhood;
	}

	public void setNeighbourhood(String neighbourhood) {
		this.neighbourhood = neighbourhood;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public void setDistanceToMyPlace(Integer distanceToMyPlace) {
		this.distanceToMyPlace = distanceToMyPlace;
	}

	public Integer getDistanceToMyPlace() {
		return distanceToMyPlace;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		return true;
	}

	@Override
	public int compareTo(Place another) {		
		return this.getDistanceToMyPlace().compareTo(another.getDistanceToMyPlace());
	}
	
	public static TreeSet<Place> sortByDistance(Location location, List<Place> places) {
		TreeSet<Place> placesSorted = new TreeSet<>();

		Location myPlace = location;
		
		for(Place p : places) {
			Location locPlace = new Location("");
			locPlace.setLatitude(p.getLatitude());
			locPlace.setLongitude(p.getLongitude());
			
			p.setDistanceToMyPlace((int)myPlace.distanceTo(locPlace));
			
			placesSorted.add(p);
		}
		
		return placesSorted;
	}
}
