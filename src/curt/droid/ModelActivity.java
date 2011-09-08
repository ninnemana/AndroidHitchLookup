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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ModelActivity extends ListActivity {

	HttpClient client;
	final static String api_url = "http://docs.curthitch.biz/api/";
	String make = "";
	String year = "";
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.model);
		
		client = new DefaultHttpClient();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			make = extras.getString("make");
			year = extras.getString("year");
			ArrayList<String> models = new ArrayList<String>();
			try{
				models = GetModels(year, make);
				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,models));
				
				final ListView model_list = getListView();
				model_list.setTextFilterEnabled(true);
				
				model_list.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent style_intent = new Intent(ModelActivity.this,StyleActivity.class);
						style_intent.putExtra("model", model_list.getItemAtPosition(position).toString());
						style_intent.putExtra("year", year);
						style_intent.putExtra("make", make);
						try{
							startActivity(style_intent);
						}catch(ActivityNotFoundException e){
							e.printStackTrace();
						}
					}
				});
			}catch(ClientProtocolException e1){
				e1.printStackTrace();
			}catch(IOException e2){
				e2.printStackTrace();
			}catch(Exception e3){
				e3.printStackTrace();
			}
		}
	}

	public ArrayList<String> GetModels(String year, String make) throws ClientProtocolException, IOException {
    	StringBuilder url = new StringBuilder(api_url);
    	url.append("GetModel?year="+year+"&make="+make+"&dataType=JSON");
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
    		JSONArray models = null;
			try {
				models = new JSONArray(data);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		ArrayList<String> modelArray = new ArrayList<String>();
    		for(int i = 0; i < models.length(); i++){
    			try {
					String m = models.getString(i);
					try{
						modelArray.add(m);
					}catch(Exception e){
						e.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			
    		}
    		return modelArray;
    	}
    	Toast.makeText(this, status, Toast.LENGTH_SHORT);
    	return null;
    }

}
