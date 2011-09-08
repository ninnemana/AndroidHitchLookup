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
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class YearActivity extends ListActivity {
	
	HttpClient client;
	JSONObject json;
	final static String api_url = "http://docs.curthitch.biz/API/";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ArrayList<String> years = new ArrayList<String>();
        try{
        	client = new DefaultHttpClient();
        	years = GetYears();
        	setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, years));
        	
        	final ListView year_listing = getListView();
			year_listing.setTextFilterEnabled(true);
			
			year_listing.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> a, View v, int position, long id) {
					Intent make_intent = new Intent(YearActivity.this,MakeActivity.class);
					make_intent.putExtra("year", year_listing.getItemAtPosition(position).toString());
					try{
						startActivity(make_intent);
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
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    public ArrayList<String> GetYears() throws ClientProtocolException, IOException {
    	StringBuilder url = new StringBuilder(api_url);
    	url.append("GetYear?dataType=JSON");
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
    		JSONArray years = null;
			try {
				years = new JSONArray(data);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		ArrayList<String> yearArray = new ArrayList<String>();
    		for(int i = 0; i < years.length(); i++){
    			try {
					String y = years.getString(i);
					try{
						yearArray.add(y);
					}catch(Exception e){
						e.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    		return yearArray;
    	}
    	Toast.makeText(this, status, Toast.LENGTH_SHORT);
    	return null;
    }
}