package se.mau.ag2656.p2worldandfriends;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import mehdi.sakout.fancybuttons.FancyButton;

public class ListDialog extends Dialog {
    private RecyclerView recyclerView;
    private TextView tvListDialogTitle;
    private FancyButton fbJoinGroup;
    private CustomDialogClass.CustomDialogListener listener;
    private FancyButton fbLeaveGroup;

    public ListDialog(@NonNull Context context, String[] content) {
        super(context);
        listener = (CustomDialogClass.CustomDialogListener) context;
        setContentView(R.layout.list_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvListDialogTitle = findViewById(R.id.tvListDialogTitle);
        recyclerView = findViewById(R.id.rvGroups);
        fbJoinGroup = findViewById(R.id.btn_join_group);
        fbLeaveGroup = findViewById(R.id.btn_leave_group);
        recyclerView.setLayoutManager(new LinearLayoutManager(context.getApplicationContext()));
        RecyclerView.Adapter adapter = new RecyclerAdapter(content, context, (CustomDialogClass.CustomDialogListener) context, this);
        recyclerView.setAdapter(adapter);
        initListeners();
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        super.setTitle(title);
        tvListDialogTitle.setText(title);
    }

    public String getTitle() {
        return tvListDialogTitle.getText().toString();
    }

    public void activateButton() {
        fbJoinGroup.setVisibility(View.VISIBLE);
        fbLeaveGroup.setVisibility(View.VISIBLE);
    }

    public void initListeners() {
        fbJoinGroup.setOnClickListener(v -> {
            dismiss();
            listener.joinGroupCallBack(getTitle());
        });

        fbLeaveGroup.setOnClickListener(v -> {
            dismiss();
            listener.leaveGroupCallBack(getTitle());
        });
    }
}
