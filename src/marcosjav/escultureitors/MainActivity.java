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
import android.support.v4.app.FragmentActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {
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

		nombreEscultura = (TextView) findViewById(R.id.title);

		dialog = new Dialog(this, R.style.MapDialog);
		dialog.setContentView(R.layout.fragment_map);

		new Runnable() {

			@Override
			public void run() {
				setUpMapIfNeeded();
				getDatos();
			}
		}.run();

	}

	public void click(View v) {
		switch (v.getId()) {
		case R.id.siguiente_1:
			getEscultura();
			final ScrollView scroll = (ScrollView) findViewById(R.id.scrollView1);
			scroll.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					scroll.fullScroll(ScrollView.FOCUS_UP);
				}
			});
			break;
		case R.id.back:
			mostrarMapa(false);
			break;
		case R.id.ver_mapa:
			((TextView) findViewById(R.id.txtDireccion)).setText(escultura
					.getDireccion());
			mMap.clear();
			mMap.addMarker(new MarkerOptions()
					.position(escultura.getUbicacion().target)
					.title(nombreEscultura.getText().toString())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(escultura
					.getUbicacion()));

			if (findViewById(R.id.layout_mapa).getVisibility() == View.VISIBLE)
				mostrarMapa(false);
			else
				mostrarMapa(true);
		}

	}

	private void mostrarMapa(boolean mostrar) {
		LinearLayout layoutEscultura = (LinearLayout) findViewById(R.id.layout_escultura);
		LinearLayout layoutMapa = (LinearLayout) findViewById(R.id.layout_mapa);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			Animation animFadeIn, animFadeOut;
			animFadeIn = AnimationUtils.loadAnimation(this,
					android.R.anim.slide_in_left);
			animFadeOut = AnimationUtils.loadAnimation(this,
					android.R.anim.slide_out_right);
			if (mostrar) {
				layoutMapa.setVisibility(View.VISIBLE);
				layoutMapa.startAnimation(animFadeIn);
				layoutEscultura.startAnimation(animFadeOut);
				layoutEscultura.setVisibility(View.GONE);
			} else {
				layoutEscultura.setVisibility(View.VISIBLE);
				layoutEscultura.startAnimation(animFadeIn);
				layoutMapa.startAnimation(animFadeOut);
				layoutMapa.setVisibility(View.GONE);
			}
		} else {
			layoutMapa.setVisibility(mostrar ? View.VISIBLE : View.GONE);
			layoutEscultura.setVisibility(mostrar ? View.GONE : View.VISIBLE);
		}

	}

	private void getEscultura() {
		if (isNetworkAvailable()) {
			// index++;
			if (index < 20 && jsonArray.length() > 0) {
				FrameLayout frame = (FrameLayout) findViewById(R.id.layout_principal);
				try {
					jsonObject = (JSONObject) jsonArray.get(index);
					new EsculturaDownlader(this, frame, jsonObject).execute();

				} catch (JSONException e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			} else {
				index = 0;
				getDatos();
				// getEscultura();
			}
		} else
			Toast.makeText(this, "Sin internet", Toast.LENGTH_SHORT).show();

		index++;
	}

	private void getDatos() {
		if (isNetworkAvailable()) {
			try {
				jsonArray = new JSONArrayDownloader()
						.execute(
								"http://dev.resistenciarte.org/api/v1/node?page="
										+ page
										+ "?parameters%5Btype%5D=escultura&fields=nid,title")
						.get();
				page++;
				getEscultura();
			} catch (InterruptedException | ExecutionException e) {
				Toast.makeText(this, "no se pudo bajar el array",
						Toast.LENGTH_LONG).show();
			}
		} else
			Toast.makeText(this, "Sin internet", Toast.LENGTH_SHORT).show();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			try {
				mMap = ((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map)).getMap();

			} catch (Exception e) {
				Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
			}

		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
