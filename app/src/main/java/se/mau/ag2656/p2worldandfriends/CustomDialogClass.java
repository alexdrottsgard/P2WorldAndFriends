package se.mau.ag2656.p2worldandfriends;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

public class CustomDialogClass extends Dialog {
    private EditText editText;
    private TextView tvTitle;
    private Context context;
    private CustomDialogListener listener;
    private FancyButton btn_OK;

    public CustomDialogClass(@NonNull Context context) {
        super(context);
        this.context = context;


        setContentView(R.layout.custom_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvTitle = findViewById(R.id.tvTitle);
        btn_OK = findViewById(R.id.btn_ok);

        btn_OK.setOnClickListener((v) -> {
                    listener.editTextCallBack(editText.getText().toString());
                    dismiss();
                }
        );

        editText = findViewById(R.id.etCustomDialog);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                listener.editTextCallBack(editText.getText().toString());
                dismiss();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            listener = (CustomDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CustomDialogListener");
        }
    }

    public interface CustomDialogListener {
        void editTextCallBack(String groupName);
        void groupClickedCallBack(String groupName);
        void joinGroupCallBack(String groupName);
        void leaveGroupCallBack(String title);
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
        tvTitle.setText(title);
    }
}
