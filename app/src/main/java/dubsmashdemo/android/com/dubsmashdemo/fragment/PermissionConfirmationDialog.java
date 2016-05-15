package dubsmashdemo.android.com.dubsmashdemo.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class PermissionConfirmationDialog extends DialogFragment {


    public static final String FRAGMENT_DIALOG = "dialog";
    private String mMessage;
    private int mRequestCode;
    private String[] mPermissions;

    public PermissionConfirmationDialog newInstance(String message, int requestCode, String[] permissions) {
        mMessage = message;
        mRequestCode = requestCode;
        mPermissions = permissions;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(mMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                        getParentFragment().requestPermissions(mPermissions, mRequestCode);

                    }
                });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        setCancelable(false);

        return alert;

    }

}
