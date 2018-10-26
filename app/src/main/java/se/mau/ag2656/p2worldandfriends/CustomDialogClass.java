package se.mau.ag2656.p2worldandfriends;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class CustomDialogClass extends AppCompatDialogFragment {
    private EditText editText;
    private CustomDialogClassListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.custom_dialog, null);

//        view.setBackgroundColor(Color.TRANSPARENT);
//        view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        view.setBackground(new ColorDrawable(Color.TRANSPARENT));
//        getView().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        builder.setView(view).setTitle("Group Name").setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH || keyCode == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    String editTextString = editText.getText().toString();
                    listener.returnText(editTextString);
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        editText = view.findViewById(R.id.etCustomDialog);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (CustomDialogClassListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CustomDialogClassListener");
        }
    }

    public interface CustomDialogClassListener {
        void returnText(String groupName);
    }

}
