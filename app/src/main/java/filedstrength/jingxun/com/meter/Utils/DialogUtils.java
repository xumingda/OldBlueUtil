package filedstrength.jingxun.com.meter.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import filedstrength.jingxun.com.meter.R;


public class DialogUtils {
	public static AlertDialog createDialog(Context context, View view, int titleId, int iconId, final DialogListener listener){
		AlertDialog dialog=new AlertDialog.Builder(context)
		.setIcon(iconId)
		.setTitle(titleId)
		.setView(view)
		.setPositiveButton(R.string.ensure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.sure();
					}
				})
		.setNegativeButton(R.string.remove,null).show();
		return dialog;
	}
	
	public interface DialogListener{
		void sure();
	}
}
