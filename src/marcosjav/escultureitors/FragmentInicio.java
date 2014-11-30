package marcosjav.escultureitors;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FragmentInicio extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_inicio);

		findViewById(R.id.btn_back).setOnClickListener(
				new Button.OnClickListener() {

					@Override
					public void onClick(View v) {

						Intent intent = new Intent(FragmentInicio.this,
								MainActivity.class);
						FragmentInicio.this.startActivity(intent);
						overridePendingTransition(android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);

					}
				});
	}

}
