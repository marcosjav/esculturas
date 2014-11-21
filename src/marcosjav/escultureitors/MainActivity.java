package marcosjav.escultureitors;

import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v7.app.ActionBarActivity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

		nombreAutor = (TextView) findViewById(R.id.nombre_autor);
		nombreEscultura = (TextView) findViewById(R.id.nombre_escultura);
		
		dialog = new Dialog(this);
		dialog.setContentView(R.layout.fragment_map);
//		dialog.setTitle("Dialogo");
//		Button volver = (Button)dialog.findViewById(R.id.btn_volver);  //porque como está en otro layout no puedo hacerlo buscar un método en MainActivity
//		volver.setOnClickListener(new Button.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//		});
		
		setUpMapIfNeeded();

		getDatos();
		getEscultura();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.btnVer:
			getEscultura();
			break;
		case R.id.btnMapa:
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
			LinearLayout layout = (LinearLayout)findViewById(R.id.layout_principal);
			try {
				jsonObject = (JSONObject)jsonArray.get(index);
				new EsculturaDownlader(this, layout, jsonObject).execute();
				
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
