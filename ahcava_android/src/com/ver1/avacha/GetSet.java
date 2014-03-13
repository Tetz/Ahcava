package com.ver1.avacha;
import android.content.Context;
import android.util.Log;
public class GetSet {
    Context con;
    private String uni_id;
	
    public GetSet(Context context)
    {
    	con = context;
    }
   // GetUUID
    public String getUuid(){
		Installation uuid = new Installation();
		uni_id = uuid.id(con);
		Log.d("getuuid", uni_id);
		return uni_id;
    }
}
