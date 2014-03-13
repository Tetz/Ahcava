package com.ver1.avacha;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class UpdateDialogFragment extends DialogFragment {
	public UpdateDialogFragment(Context context) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.please_update).setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Uri official_web = Uri.parse("https://play.google.com/store/apps/details?id=com.ver1.avacha");
				Intent i = new Intent(Intent.ACTION_VIEW, official_web);
				startActivity(i);
			}
		}); // Don't remove this semicolon

		// Create the AlertDialog object and return it
		return builder.create();
	}
}
