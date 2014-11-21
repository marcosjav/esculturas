package marcosjav.escultureitors;

import com.google.android.gms.maps.model.CameraPosition;

import android.graphics.Bitmap;

public class Escultura {
	private String nombre, autor, descripcion, uri;
	private int nid;
	private Bitmap foto;
	private static CameraPosition ubicacion;
	private static String direccion;
	
	public Escultura(){
		this.nombre = "Sin Nombre";
		this.autor = "Sin autor";
		this.descripcion = "sin descripción";
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getAutor() {
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Bitmap getFoto() {
		return foto;
	}
	public void setFoto(Bitmap foto) {
		this.foto = foto;
	}
	public int getNid() {
		return nid;
	}
	public void setNid(int nid) {
		this.nid = nid;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public CameraPosition getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(CameraPosition ubicacion) {
		Escultura.ubicacion = ubicacion;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		Escultura.direccion = direccion;
	}
	
}
