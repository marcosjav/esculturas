package marcosjav.escultureitors;

import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
//	public static int hilos;
	static Dialog dialog;
	ProgressDialog progressDialog;
	TextView nombreAutor, nombreEscultura, descripcion;
	static ImageView imagen;
	Bitmap foto;
	JSONArray jsonArray;
	JSONObject jsonObject;
	Escultura escultura = new Escultura();
	int index, page;
	Button btnMapa;
	GoogleMap mMap;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		nombreEscultura = (TextView)findViewById(R.id.title);
		
		dialog = new Dialog(this,R.style.MapDialog);
		dialog.setContentView(R.layout.fragment_map);
		
		setUpMapIfNeeded();

		getDatos();
		getEscultura();
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.siguiente_1:
			getEscultura();
			final ScrollView scroll = (ScrollView)findViewById(R.id.scrollView1);
			scroll.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					scroll.fullScroll(ScrollView.FOCUS_UP);
				}
			});
			break;
		case R.id.ver_mapa:
			dialog.setTitle("Ubicación");
	        ((TextView)dialog.findViewById(R.id.txtDireccion)).setText(escultura.getDireccion());
			dialog.show();
			mMap.clear();
			mMap.addMarker(new MarkerOptions()
            .position(escultura.getUbicacion().target)
            .title(nombreEscultura.getText().toString())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(escultura.getUbicacion()));
		}

	}
	
	private void getEscultura(){
		index++;
		if(index<20 && jsonArray.length() > 0)
		{
			FrameLayout frame = (FrameLayout)findViewById(R.id.layout_principal);
			try {
				jsonObject = (JSONObject)jsonArray.get(index);
				new EsculturaDownlader(this, frame, jsonObject).execute();
				
			} catch (JSONException e) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			}
		}else{
			index = 0;
			getDatos();
			getEscultura();
		}
	}
	
	private void getDatos(){
		try {
			jsonArray = new JSONArrayDownloader().execute("http://dev.resistenciarte.org/api/v1/node?page="	+ page + "?parameters%5Btype%5D=escultura&fields=nid,title").get();
			page++;
		} catch (InterruptedException | ExecutionException e) {
			Toast.makeText(this, "no se pudo bajar el array", Toast.LENGTH_LONG).show();
		}
	}
	
	private void setUpMapIfNeeded() {
        if (mMap == null) {
        	try {
        		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();

			} catch (Exception e) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
			}
            
        }
    }
}
