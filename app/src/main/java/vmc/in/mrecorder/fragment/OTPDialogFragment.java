package vmc.in.mrecorder.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import vmc.in.mrecorder.R;


public class OTPDialogFragment extends DialogFragment {
    static String DialogboxTitle;
    TextView txtname;
    LinearLayout progress;
    Button btnDone, btnCancel;

    //---empty constructor required
    public OTPDialogFragment() {

    }

    //---set the title of the dialog window
    public void setDialogTitle(String title) {
        DialogboxTitle = title;
    }

    public void setOPT(String otp) {
        if (txtname != null) {
            txtname.setText(otp);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_otpdialog, container);

        //---get the EditText and Button views
        txtname = (TextView) view.findViewById(R.id.txtOTP);
        txtname.setInputType(InputType.TYPE_CLASS_NUMBER);
        btnDone = (Button) view.findViewById(R.id.btnDone);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        //---event handler for the button
        btnDone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //---gets the calling activity
                OTPDialogListener activity = (OTPDialogListener) getActivity();
                activity.onFinishInputDialog(txtname.getText().toString());

                //---dismiss the alert
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //---gets the calling activity


                //---dismiss the alert
                dismiss();
            }
        });

        //---show the keyboard automatically
        txtname.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //---set the title for the dialog
        getDialog().setTitle(DialogboxTitle);
       /* txtname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                btnDone.setEnabled(false);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count >= 4) {
                    txtname.setText(s.toString());
                    btnDone.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        return view;
    }



    public interface OTPDialogListener {
        void onFinishInputDialog(String inputText);
    }
}