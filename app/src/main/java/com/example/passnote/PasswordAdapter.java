package com.example.passnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public  class PasswordAdapter extends BaseAdapter {

    private Context context;
    private List<Password> passwordList;
    private List<Password> passwords;

    public PasswordAdapter(Context context, List<Password> passwordList) {
        this.context = context;
        this.passwordList = passwordList;

    }

    @Override
    public int getCount() {
        return passwordList.size();
    }

    @Override
    public Object getItem(int i) {
        return passwordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.password_list_item, null);
        }

        TextView siteTextView = view.findViewById(R.id.siteTextView);
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        TextView passwordTextView = view.findViewById(R.id.passwordTextView);

        Password password = passwordList.get(i);

        siteTextView.setText(password.getSite());
        usernameTextView.setText(password.getUsername());
        passwordTextView.setText(password.getPassword());

        return view;
    }
    public void updateList(List<Password> passwords) {
        this.passwords = passwords;
        notifyDataSetChanged();

    }

}






