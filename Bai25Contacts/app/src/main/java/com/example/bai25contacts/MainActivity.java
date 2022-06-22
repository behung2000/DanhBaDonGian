package com.example.bai25contacts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.bai25contacts.Helper.ContactHelper;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;
    SimpleCursorAdapter adapter;

    int PERMISSION_READ_WRITE_CONTACTS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if((checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED))
            {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_CONTACTS,
                        Manifest.permission.READ_CONTACTS
                },PERMISSION_READ_WRITE_CONTACTS);
            }
            else
            {
                showContacts();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_READ_WRITE_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display your contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_refresh:
                showContacts();
                break;
            case R.id.menu_check:
                for(int i=0; i<listView.getAdapter().getCount(); i++)
                    listView.setItemChecked(i,true);
                break;
            case R.id.menu_delete:
                deleteContacts();
                break;
        }
        return true;
    }

    private void deleteContacts() {
        Cursor phones = null;
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        for(int i=0; i<checked.size(); i++)
        {
            if(checked.valueAt(i) == true)
            {
                phones = (Cursor) adapter.getItem(i);
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                ContactHelper.deleteContact(getContentResolver(),phoneNumber);
            }
        }
        phones.close();
        adapter.notifyDataSetChanged();
        Toast.makeText(this,"Contact(s) deleted", Toast.LENGTH_SHORT).show();
    }

    private void showContacts() {
        Cursor cursor = ContactHelper.getContactCursor(getContentResolver(),""); //get all contacts
        String[] fields = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};

        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_multiple_choice, cursor, fields, new int[]{android.R.id.text1});
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initView(){
        listView = (ListView) findViewById(R.id.listView);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Contact Remover");
        setSupportActionBar(toolbar);
    }
}