package curt.droid;


import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MakeActivity extends ListActivity {
	
	HttpClient client;
	String year = "";
	final static String api_url = "http://docs.curthitch.biz/API/";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make);
        
        client = new DefaultHttpClient();
        
        Bundle extras = getIntent().getExtras();
        if(extras != null){
        	year = extras.getString("year");
        	ArrayList<String> makes = new ArrayList<String>();
        	try {
				makes = GetMakes(year);
				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, makes));
				
				final ListView make_list = getListView();
				make_list.setTextFilterEnabled(true);
				
				make_list.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> a, View v, int position, long id) {
						Intent model_intent = new Intent(MakeActivity.this,ModelActivity.class);
						model_intent.putExtra("make", make_list.getItemAtPosition(position).toString());
						model_intent.putExtra("year", year);
						try{
							startActivity(model_intent);
						}catch(ActivityNotFoundException e){
							e.printStackTrace();
						}
					}
				});
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
    
    public ArrayList<String> GetMakes(String year) throws ClientProtocolException, IOException {
    	StringBuilder url = new StringBuilder(api_url);
    	url.append("GetMake?year="+year+"&dataType=JSON");
    	HttpGet get = new HttpGet(url.toString());
    	HttpResponse resp = null;
    	try{
    		resp = client.execute(get);
    	}catch(UnknownHostException e){
    		e.printStackTrace();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	int status = resp.getStatusLine().getStatusCode();
    	if(status == 200){
    		HttpEntity entity = resp.getEntity();
    		String data = EntityUtils.toString(entity);
    		JSONArray makes = null;
			try {
				makes = new JSONArray(data);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		ArrayList<String> makeArray = new ArrayList<String>();
    		for(int i = 0; i < makes.length(); i++){
    			try {
					String m = makes.getString(i);
					try{
						makeArray.add(m);
					}catch(Exception e){
						e.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    		return makeArray;
    	}
    	Toast.makeText(this, status, Toast.LENGTH_SHORT);
    	return null;
    }
}
