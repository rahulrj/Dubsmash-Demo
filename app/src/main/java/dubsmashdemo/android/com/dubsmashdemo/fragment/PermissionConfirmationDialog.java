package dubsmashdemo.android.com.dubsmashdemo.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.utils.Constants;

/**
 * Created by rahul.raja on 5/14/16.
 */
public class PermissionConfirmationDialog extends DialogFragment {


    public static final String FRAGMENT_DIALOG = "dialog";

    @TargetApi(23)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.permission_request)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getParentFragment().requestPermissions(Constants.VIDEO_PERMISSIONS,
                                Constants.REQUEST_VIDEO_PERMISSIONS);
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getDialog().dismiss();
                            }
                        })
                .create();
    }

}
