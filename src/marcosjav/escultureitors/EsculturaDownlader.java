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

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EsculturaDownlader extends AsyncTask<String, JSONObject, Escultura> {
	LinearLayout layout, linLayout; 
	JSONObject jsonObject; JSONArray jsonArray;
	
	private Context context;
	private ProgressBar progress;

	public EsculturaDownlader(Context context, LinearLayout layout, JSONObject jsonObject){
		this.layout =  layout;
		this.jsonObject = jsonObject;
		this.context = context;
		progress = new ProgressBar(context);
		linLayout = (LinearLayout)layout.findViewById(R.id.layout_secundario);
	}
	
	@Override
	protected void onPreExecute() {
		linLayout.removeAllViews();
		linLayout.addView(progress, 0);
	}

	@Override
	protected void onPostExecute(Escultura result) {
		TextView nombre = (TextView)layout.findViewById(R.id.nombre_escultura);
		TextView autor = (TextView)layout.findViewById(R.id.nombre_autor);
		
		nombre.setText(result.getNombre());
		autor.setText(result.getAutor());
		
		linLayout.removeViewAt(0);
		ImageView imagen = new ImageView(context);
		TextView texto = new TextView(context);
		
		imagen.setImageBitmap(result.getFoto());
		texto.setText(result.getDescripcion());
		
		linLayout.addView(imagen);
		linLayout.addView(texto);
		
		
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
//			jsonObject = new JSONObjectDownloader().execute(urlAutor).get();
			jsonObject = new JSONObject(downloadHTML(urlAutor));
			escultura.setAutor(jsonObject.getString("title"));
			
			jsonObject = jsonAux;   // Obtenemos el JSON de la escultura de nuevo
			
			jsonObject = (JSONObject) jsonObject.get("body");   // buscamos los datos del cuerpo
			jArr = (JSONArray)jsonObject.get("und");
			jsonObject = (JSONObject)jArr.get(0);
			escultura.setDescripcion(Html.fromHtml(jsonObject.getString("value")).toString());
			
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

}
