package marcosjav.escultureitors;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("NewApi")
public class EsculturaDownlader extends AsyncTask<String, JSONObject, Escultura> {
	LinearLayout layout, linLayout;
	FrameLayout frame;
	JSONObject jsonObject; JSONArray jsonArray;
	Context context;

	public EsculturaDownlader(Context context, FrameLayout frame, JSONObject jsonObject){
		this.jsonObject = jsonObject;
		this.frame = frame;
		this.context = context;
		linLayout = (LinearLayout)frame.findViewById(R.id.layout_secundario);
	}
	
	@Override
	protected void onPreExecute() {
		mostrarEscultura(false);
	}

	@Override
	protected void onPostExecute(Escultura result) {
		TextView nombre = (TextView)linLayout.findViewById(R.id.title);
		TextView autor = (TextView)linLayout.findViewById(R.id.autor);
		TextView texto = (TextView)linLayout.findViewById(R.id.descripcion);
		ImageView imagen = (ImageView)linLayout.findViewById(R.id.foto);

		nombre.setText(result.getNombre());
		autor.setText(result.getAutor());
		imagen.setImageBitmap(result.getFoto());
		texto.setText(result.getDescripcion());
		
		mostrarEscultura(true);
	}

	@Override
	protected Escultura doInBackground(String... params) {
		Escultura escultura = new Escultura();
		
		try {
			JSONObject jsonAux;
			escultura.setNid(jsonObject.getInt("nid"));
			escultura.setNombre(jsonObject.getString("title"));
			escultura.setUri(jsonObject.getString("uri"));			
			
			jsonObject = new JSONObject(downloadHTML(escultura.getUri()));
			jsonAux = jsonObject;
			
			jsonObject = (JSONObject) jsonObject.get("field_fotos");   // Buscamos el nombre de la foto y la descargamos   
			JSONArray jArr = (JSONArray)jsonObject.get("und");
			jsonObject = (JSONObject)jArr.get(0);
			String urlFoto = "http://dev.resistenciarte.org/sites/dev.resistenciarte.org/files/" +  jsonObject.getString("uri").substring(9);
			
			InputStream in = new URL(urlFoto).openStream();		// Bajamos la imagen
	    	escultura.setFoto(BitmapFactory.decodeStream(in));
			
			
	    	jsonObject = jsonAux;   // Obtenemos el JSON de la escultura de nuevo
			
			jsonObject = (JSONObject) jsonObject.get("field_autor");   // buscamos los datos del autor
			jArr = (JSONArray)jsonObject.get("und");
			jsonObject = (JSONObject)jArr.get(0);
			String urlAutor = "http://dev.resistenciarte.org/api/v1/node/" +  jsonObject.getString("target_id");
			jsonObject = new JSONObject(downloadHTML(urlAutor));
			escultura.setAutor(jsonObject.getString("title"));
			
			jsonObject = jsonAux;   // Obtenemos el JSON de la escultura de nuevo
			
			jsonObject = (JSONObject) jsonObject.get("body");   // buscamos los datos del cuerpo
			jArr = (JSONArray)jsonObject.get("und");
			jsonObject = (JSONObject)jArr.get(0);
			escultura.setDescripcion(Html.fromHtml(jsonObject.getString("value")).toString());
			
			jsonObject = jsonAux;   // Obtenemos el JSON de la escultura de nuevo      DATOS DEL MAPA
			jsonObject = (JSONObject) jsonObject.get("field_mapa");   // buscamos los datos del mapa
			jArr = (JSONArray)jsonObject.get("und");
			jsonObject = (JSONObject)jArr.get(0);
			CameraPosition ubicacion =  new CameraPosition.Builder().target(new LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lon")))
                    .zoom(jsonObject.getLong("zoom"))
                    .build();
			escultura.setUbicacion(ubicacion);
			
			jsonObject = jsonAux;   // Obtenemos el JSON de la escultura de nuevo      DIRECCION
			jsonObject = (JSONObject) jsonObject.get("field_ubicacion");   // buscamos los datos del mapa
			jArr = (JSONArray)jsonObject.get("und");
			jsonObject = (JSONObject)jArr.get(0);
			escultura.setDireccion(jsonObject.getString("value"));
			
			
		} catch (Exception e) {
		}
		
		return escultura;
	}
	
	private String downloadHTML(String url){
		int timeoutSocket = 5000;
		int timeoutConnection = 5000;

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);

		HttpGet httpget = new HttpGet(url);

		try {
			HttpResponse getResponse = client.execute(httpget);
			final int statusCode = getResponse.getStatusLine()
					.getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("MyApp", "Download Error: " + statusCode
						+ "| for URL: " + url);
				return null;
			}

			String line = "";
			StringBuilder total = new StringBuilder();

			HttpEntity getResponseEntity = getResponse.getEntity();

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(getResponseEntity.getContent()));

			while ((line = reader.readLine()) != null) {
				total.append(line);
			}

			return total.toString();
		} catch (Exception e) {
			Log.w("MyApp", "Download Exception : " + e.toString());
		}
		return "";
	}
	
	private void mostrarEscultura(Boolean mostrar){
		LinearLayout layoutEscultura = (LinearLayout)frame.findViewById(R.id.layout_escultura);
		RelativeLayout layoutCargando = (RelativeLayout)frame.findViewById(R.id.layout_cargando);
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			Animation animFadeIn, animFadeOut;
			// load animations
			animFadeIn = AnimationUtils.loadAnimation(context,
			                R.animator.fade_in);
			animFadeOut = AnimationUtils.loadAnimation(context,
			                R.animator.fade_out);
			if(mostrar)
			{
				// set animation listeners
//				animFadeIn.setAnimationListener(this);
//				animFadeOut.setAnimationListener(this);
				 
				// Make fade in elements Visible first
				layoutEscultura.setVisibility(View.VISIBLE);
				 
				// start fade in animation
				layoutEscultura.startAnimation(animFadeIn);
				                 
				// start fade out animation
				layoutCargando.startAnimation(animFadeOut);
			}else{
				// Make fade in elements Visible first
				layoutCargando.setVisibility(View.VISIBLE);
				 
				// start fade in animation
				layoutCargando.startAnimation(animFadeIn);
				                 
				// start fade out animation
				layoutEscultura.startAnimation(animFadeOut);
			}
		}
		else{
			layoutEscultura.setVisibility(mostrar ? View.VISIBLE : View.GONE);
			layoutCargando.setVisibility(mostrar ? View.GONE : View.VISIBLE);
		}

	}

}
