package se.mau.ag2656.p2worldandfriends;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.ContentValues.TAG;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {

    private ListDialog listDialog;
    private String[] mContent;
    private LayoutInflater inflater;
    private CustomDialogClass.CustomDialogListener listener;

    public RecyclerAdapter(String[] content, Context context, CustomDialogClass.CustomDialogListener listener, ListDialog listDialog) {
        mContent = content;
        this.listener = listener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listDialog = listDialog;
    }

    @NonNull
    @Override
    public RecyclerAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.rv_item, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.Holder holder, int i) {
        String s = mContent[i];
        holder.tv.setText(s);
    }

    @Override
    public int getItemCount() {
        return mContent.length;
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tv;

        public Holder(@NonNull View view) {
            super(view);
            this.tv = view.findViewById(R.id.tvItem);

            if (listDialog.getTitle().contains("Active Groups")) {
                view.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: Clicked on item: " + tv.getText().toString());
            listener.groupClickedCallBack(tv.getText().toString());
            listDialog.dismiss();
        }
    }
}
