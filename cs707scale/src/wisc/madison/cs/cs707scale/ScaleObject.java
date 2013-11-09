package wisc.madison.cs.cs707scale;

import com.google.android.gms.maps.model.Marker;

public class ScaleObject {
	public Marker marker;
	public String text;
	public ScaleObject(Marker m, String t)
	{
		marker = m;
		text = t;
	}
}
