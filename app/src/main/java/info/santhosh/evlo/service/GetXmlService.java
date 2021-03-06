package info.santhosh.evlo.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.evernote.android.job.Job;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;

import info.santhosh.evlo.common.WriteDb;
import info.santhosh.evlo.service.SOAP.parser.SOAPEnvelope;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by santhoshvai on 12/03/16.
 */
public class GetXmlService  extends IntentService {

    static final String TAG = "GetXmlService";
    private static final String URL = "https://www.dropbox.com/s/tdnc79h29zkpzhk/Date-Wise-Prices-all-Commodity.xml?dl=1";

    public GetXmlService() {
        super("xml-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Job.Result result = synchronousRequest(this);
        if (result.equals(Job.Result.RESCHEDULE)) {
            // TODO: reschedule ASAP
        }
    }

    @WorkerThread
    public static Job.Result synchronousRequest(@NonNull Context context) {
        Log.d(TAG, "START synchronousRequest");
        OkHttpClient client = new OkHttpClient();

//        Request request = new Request.Builder()
//                .url( context.getResources().getString(R.string.price_xml_url) )
//                .build();

        Request request = new Request.Builder()
                .url( URL )
                .build();

        try{
            long startTime = System.currentTimeMillis();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return Job.Result.RESCHEDULE;
            }
            final String responseXml = response.body().string(); // get the xml string
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            Log.d(TAG, "elapsedTime to get xml from network (ms): " + elapsedTime );

            startTime = System.currentTimeMillis();
            Serializer serializer = new Persister(new AnnotationStrategy());
            SOAPEnvelope envelope = serializer.read(SOAPEnvelope.class, responseXml);
//            Log.d(TAG, envelope.getList().getList().get(0).toString());
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            Log.d(TAG, "elapsedTime to parse data (ms): " + elapsedTime);

            startTime = System.currentTimeMillis();
            WriteDb.usingCommoditiesList(context, envelope.getCommodities().getList());
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;
            Log.d(TAG, "elapsedTime to write data (ms): " + elapsedTime);
            Log.d(TAG, "size: " + envelope.getCommodities().getList().size());

            return Job.Result.SUCCESS;
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getClass().getSimpleName()+ ": "+ e.getLocalizedMessage());
            return Job.Result.RESCHEDULE;
        }
    }
}
