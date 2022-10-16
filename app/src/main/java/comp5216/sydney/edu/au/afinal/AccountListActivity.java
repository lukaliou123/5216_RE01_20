package comp5216.sydney.edu.au.afinal;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AccountListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> accountNames;
    ArrayList<Integer> accountIDs;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        Intent intent = getIntent();
        accountIDs = intent.getIntegerArrayListExtra("AccountIDs");
        accountNames = intent.getStringArrayListExtra("AccountNames");

        listView = (ListView) findViewById(R.id.listView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, (String[]) accountNames.toArray(new String[accountNames.size()]));
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer accountID = accountIDs.get(position);
                Intent outputIntent = new Intent();
                outputIntent.putExtra("AccountID", accountID);
                setResult(RESULT_OK, outputIntent);
                finish();
            }
        });

    }
}