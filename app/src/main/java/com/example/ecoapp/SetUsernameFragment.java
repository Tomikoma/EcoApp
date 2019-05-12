package com.example.ecoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SetUsernameFragment extends DialogFragment implements OnEditorActionListener {



    private EditText nameEditText;
    private EditText phoneEditText;

    public SetUsernameFragment() {

    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String name, String phone);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        nameEditText = (EditText) view.findViewById(R.id.nameEditView);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditView);
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        nameEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        phoneEditText.setOnEditorActionListener(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            EditNameDialogListener listener = (EditNameDialogListener) getActivity();
            listener.onFinishEditDialog(nameEditText.getText().toString(), phoneEditText.getText().toString());
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }





    public static SetUsernameFragment newInstance(String title) {
        SetUsernameFragment frag = new SetUsernameFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_setusername, container);
    }
}
