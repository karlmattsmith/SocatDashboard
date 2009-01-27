package gov.noaa.pmel.tmap.las.client.map;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Polygon;

public class DataBoundsOverlay {
	LatLng[] polygonPoints;
	Polygon polygon;
	String strokeColor = "#FFFFFF";
	int strokeWeight = 4;
	float strokeOpacity = 1.0f;
	String fillColor = "#FFFFFF";
	float fillOpacity = 0.0f;
	double offset = .45;
	public DataBoundsOverlay (LatLngBounds dataBounds) {
		polygonPoints = new LatLng[9];
		LatLng sw = dataBounds.getSouthWest();
		if ( sw.getLatitude() < -88.05 ) {
			sw = LatLng.newInstance(-88.05, sw.getLongitude());
		}
		LatLng ne = dataBounds.getNorthEast();
		if ( ne.getLatitude() > 88.05 ) {
			ne = LatLng.newInstance(88.05, ne.getLongitude());
		}
		polygonPoints[0] = LatLng.newInstance(sw.getLatitude()-offset, sw.getLongitude()-offset);
		polygonPoints[1] = LatLng.newInstance(dataBounds.getCenter().getLatitude(), sw.getLongitude()-offset);
		polygonPoints[2] = LatLng.newInstance(ne.getLatitude()+offset, sw.getLongitude()-offset);
		polygonPoints[3] = LatLng.newInstance(ne.getLatitude()+offset, dataBounds.getCenter().getLongitude());
		polygonPoints[4] = LatLng.newInstance(ne.getLatitude()+offset, ne.getLongitude()+offset);
		polygonPoints[5] = LatLng.newInstance(dataBounds.getCenter().getLatitude(), ne.getLongitude()+offset);
		polygonPoints[6] = LatLng.newInstance(sw.getLatitude()-offset, ne.getLongitude()+offset);
		polygonPoints[7] = LatLng.newInstance(sw.getLatitude()-offset, dataBounds.getCenter().getLongitude());
		polygonPoints[8] = LatLng.newInstance(sw.getLatitude()-offset, sw.getLongitude()-offset);
		polygon = new Polygon(polygonPoints, strokeColor, strokeWeight, strokeOpacity, fillColor, fillOpacity);
	}
	public Polygon getPolygon() {
		return polygon;
	}
	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
}
