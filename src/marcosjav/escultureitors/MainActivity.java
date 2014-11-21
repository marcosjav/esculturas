package marcosjav.escultureitors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
//	public static int hilos;
	ProgressDialog progressDialog;
	TextView nombreAutor, nombreEscultura, descripcion;
	static ImageView imagen;
	Bitmap foto;
	JSONArray jsonArray;
	JSONObject jsonObject;
	Escultura escultura = new Escultura();
	int index, page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		nombreAutor = (TextView) findViewById(R.id.nombre_autor);
		nombreEscultura = (TextView) findViewById(R.id.nombre_escultura);

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
		case R.id.btn_volver:
			
			break;
		case R.id.btnMapa:
//			LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);  
//		    View popupView = layoutInflater.inflate(R.layout.fragment_map, null);  
//		    final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
//		    popupWindow.showAsDropDown(findViewById(R.id.btn_volver), 50, -30);
		}

	}
	
	private void getEscultura(){
		index++;
		if(index<20)
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
	
	

//	public void setImage(Bitmap imagen){
//		this.imagen.setImageBitmap(imagen);
//	}

//	private void getDatos() {
//		try {
//			jsonObject = (JSONObject)jsonArray.get(index);
//			escultura.setNid(jsonObject.getInt("nid"));
//			
//			escultura.setNombre(jsonObject.getString("title"));
//			nombreEscultura.setText(escultura.getNombre());
//			
//			escultura.setUri(jsonObject.getString("uri"));			
//			jsonObject = new JSONObjectDownloader().execute(escultura.getUri()).get();   // Obtenemos el JSON de la escultura
//			
//			jsonObject = (JSONObject) jsonObject.get("field_fotos");   // Buscamos el nombre de la foto y la descargamos   
//			JSONArray jArr = (JSONArray)jsonObject.get("und");
//			jsonObject = (JSONObject)jArr.get(0);
//			String urlFoto = "http://dev.resistenciarte.org/sites/dev.resistenciarte.org/files/" +  jsonObject.getString("uri").substring(9);
//			InputStream in = new URL(urlFoto).openStream();		// Bajamos la imagen
//	    	foto = BitmapFactory.decodeStream(in);
//			imagen.setImageBitmap(foto);
//			
//			
//			jsonObject = new JSONObjectDownloader().execute(escultura.getUri()).get();   // Obtenemos el JSON de la escultura de nuevo
//			
//			jsonObject = (JSONObject) jsonObject.get("field_autor");   // buscamos los datos del autor
//			jArr = (JSONArray)jsonObject.get("und");
//			jsonObject = (JSONObject)jArr.get(0);
//			String urlAutor = "http://dev.resistenciarte.org/api/v1/node/" +  jsonObject.getString("target_id");
//			jsonObject = new JSONObjectDownloader().execute(urlAutor).get();
//			escultura.setAutor(jsonObject.getString("title"));
//			
//			nombreAutor.setText(escultura.getAutor());
//			
//			jsonObject = new JSONObjectDownloader().execute(escultura.getUri()).get();   // Obtenemos el JSON de la escultura de nuevo
//			
//			jsonObject = (JSONObject) jsonObject.get("body");   // buscamos los datos del cuerpo
//			jArr = (JSONArray)jsonObject.get("und");
//			jsonObject = (JSONObject)jArr.get(0);
//			escultura.setDescripcion(jsonObject.getString("value"));
//			
//			String desc = escultura.getDescripcion()==""?"Sin descripción":escultura.getDescripcion(); 
//			descripcion.setText(Html.fromHtml(desc).toString());
//			
//			
//		} catch (Exception e) {
//		}
//		
//	}

//	private void setNombreEscultura() {
//
//	}

}
