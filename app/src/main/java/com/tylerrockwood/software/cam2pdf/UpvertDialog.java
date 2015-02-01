package com.tylerrockwood.software.cam2pdf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tylerrockwood.software.cam2pdf.backgroundTasks.UpvertService;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rockwotj on 1/31/2015.
 */
public class UpvertDialog extends DialogFragment implements DialogInterface.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String PHOTOS = "PHOTOS";
    private Context mContext;
    private ArrayAdapter<String> mAdapter;
    private String mSelectedFolder;
    private EditText mFileInput;

    public static UpvertDialog createDialog(List<String> photos) {
        UpvertDialog dialog = new UpvertDialog();
        Bundle args = new Bundle();
        args.putStringArray(PHOTOS, photos.toArray(new String[0]));
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContext = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.upvert_dialog_title));
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.dialog_upvert, null);
        mFileInput = (EditText) rootView.findViewById(R.id.filenameInput);
        Spinner folderSpinner = (Spinner) rootView.findViewById(R.id.folderSpinner);
        // TODO: Replace with actual folder names from drive
        List<String> folders = Arrays.asList("/", "/docs", "/docs/taxReturns");
        initializeSpinner(folderSpinner, folders);
        // Set dialog view
        builder.setView(rootView);
        // Set buttons
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(mContext.getString(R.string.upload), this);
        return builder.create();
    }

    private void initializeSpinner(Spinner folderSpinner, List<String> folders) {
        // Create adapter for spinner
        mAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, folders);
        // Specify the layout to use when the list of choices appears
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        folderSpinner.setAdapter(mAdapter);
        // Listen for the selected item
        folderSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        // Start service to upload photos.
        UpvertService.startService(getActivity(), getArguments().getStringArray(PHOTOS), mFileInput.getText().toString(), mSelectedFolder);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mSelectedFolder = mAdapter.getItem(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing
    }
}
