package com.smartparking.amit.parksmart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class BookingConfirmedFragment extends Fragment implements OnMapReadyCallback {

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private Button Navigate,Cancel,Payment;
    private ImageView iv;
    private ProgressBar progress;
    private Bitmap bitmap;
    private int state_save = 0;
    private Double Bill = null;
    private String userId, status,marker,Otp,Slot;
    private String ParkingName,SlotName;
    private TextView address;
    public final static int QRcodeWidth = 700 ;
    GoogleMap mGoogleMap;
    View view;
    MapView mMapView;
    public BookingConfirmedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_confirmed, container, false);
        bottom_sheet = view.findViewById(R.id.booking_confirmed);
        ////////////////////BottomSheet////////////////////////////////////////////////////
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Payment = view.findViewById(R.id.proceedpayment);
        Payment.setEnabled(false);
        ParkingName = this.getArguments().getString("ParkingName");
        Navigate = view.findViewById(R.id.navigate);
        address = view.findViewById(R.id.address);
        Cancel = view.findViewById(R.id.cancel);
        progress = view.findViewById(R.id.progressBar);
        iv = view.findViewById(R.id.iv);
        progress.setVisibility(View.VISIBLE);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        marker = getArguments().getString("ParkingName");
        Bundle bundle = this.getArguments();
        if(bundle!=null){
            if(bundle.getString("status")!=null){
                status = bundle.getString("status");
                Log.d(TAG, "onCreateView: status info available"+ status);
                checkStatus();

            }
            else{
                Log.d(TAG, "onCreateView: "+ "statusInfoLost");
            }
            Log.d(TAG, "onCreateView: "+"Bundle is not null");
        }else {
            Log.d(TAG, "onCreateView: "+"Bundle is null");
        }
        final DatabaseReference bookref = FirebaseDatabase.getInstance().getReference("Parkings").child(marker).child("Slots").child("Booked").child(userId);
        bookref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    //Log.d(TAG, "onDataChange: "+ dataSnapshot.toString());
                    if(dataSnapshot.exists()) {
                        Otp = dataSnapshot.child("OTP").getValue().toString();
                        Slot = dataSnapshot.child("Slot").getValue().toString();
                        //Log.d("Arrived", "onDataChange: "+dataSnapshot.child("Status").getValue().toString() );
                        status=dataSnapshot.child("Status").getValue().toString();
                        //Log.d(TAG, "onDataChange: "+status);
                        if(dataSnapshot.child("Bill").getValue()!=null){
                            Bill = Double.parseDouble(dataSnapshot.child("Bill").getValue().toString()); //To get value(Number) which might be Double or Long as a Double
                        }
                        checkStatus();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    //Log.d(TAG, "onDataChange: "+"Sabka katega");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (marker!=null){
            DatabaseReference mf = FirebaseDatabase.getInstance().getReference("Parkings").child(marker).child("Address");
            Log.d(TAG, "onCreateView: "+mf.toString());
            mf.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: "+marker+": "+ dataSnapshot.toString());
                    try{
                    String add = dataSnapshot.getValue().toString();
                    setAddress(add);
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            getActivity().finish();
        }
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Status", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                /*editor.remove("status");
                editor.remove("ParkingName");
                editor.remove("CurrentLat");
                editor.remove("CurrentLong");
                editor.remove("DestinationLat");
                editor.remove("DestinationLong");*/
                editor.clear();
                editor.commit();
                addtoHistory(marker,(double) 0,"Cancelled");
                Intent intent = new Intent(getActivity(),MapNavActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                getFragmentManager().executePendingTransactions();
                getActivity().finish();
                Toast.makeText(getActivity(),"Booking Cancelled", Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        Payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makepayment(marker);

            }
        });

        Navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        return view;
    }

    private void checkStatus() {
        Log.d(TAG, "checkStatus: "+status);
        if (status.equals("Unpaid")) {
            Log.d(TAG, "checkStatus: "+Bill);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            Navigate.setVisibility(View.GONE);
            Cancel.setVisibility(View.GONE);
            Payment.setEnabled(true);
            makepayment(marker);
        }
        else if(status.equals("Booked")) {
            generateQR(Otp, Slot);
            Log.d(TAG, "checkStatus: "+"Booked");
        }
        else if (status.equals("Arrived") && Bill==null) {
            //Log.d(TAG, "onDataChange: Entered into if "+dataSnapshot.child("Status").getValue().toString());
            Log.d(TAG, "checkStatus: "+"Arrived");
            Slot = Slot + "Arrived";
            generateQR(Otp, Slot);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            Navigate.setVisibility(View.GONE);
            Cancel.setVisibility(View.GONE);

        }
        else{
            Log.d(TAG, "checkStatus: "+"Nintendo");
        }
    }

    private void makepayment(final String marker) {
        cancel();
        addtoHistory(marker,Bill,"Unpaid");
        Intent intent = new Intent(getContext().getApplicationContext(), PaymentActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void addtoHistory(String marker, Double Bill,String status) {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("MyBookings");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = format.format( new Date());
        final customHistory CbookingHistory = new customHistory(marker,date,Bill ,status);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Booking = "Booking"+ Long.toString(dataSnapshot.getChildrenCount());
                mRef.child(Booking).setValue(CbookingHistory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void generateQR(final String otp, final String Slot){
        Thread th = new Thread (new Runnable() {
            @Override
            public void run() {
                String qrString = (userId+"$"+otp+Slot);
                if(qrString.length() == 0){

                }else {
                    try {
                        if(isAdded()){
                        bitmap = TextToImageEncode(qrString);
                        handler.sendEmptyMessage(0);}
                        // String path = saveImage(bitmap);  //give read write permission
                        //  Toast.makeText(MainActivity.this, "QRCode saved to -> "+path, Toast.LENGTH_SHORT).show();
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }
                handler.sendEmptyMessage(1);
            }
        });
        th.start();
    }
    private void setAddress(String Add){
        address.setText(Add);
    }

    private void cancel() {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                state_save = 1;
                final DatabaseReference data = FirebaseDatabase.getInstance().getReference("Parkings")
                        .child(ParkingName)
                        .child("Slots")
                        .child("Booked");
                final DatabaseReference userData = data.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Log.d(TAG, "run: "+userData.toString());
                userData.child("Slot").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String SlotNumber;
                        SlotName = (String) dataSnapshot.getValue();
                        if(SlotName!=null){
                            Log.d(TAG, "run: "+SlotName);
                            SlotNumber = SlotName.substring(4);     //Starting Index of Slot Number Eg. Slot2
                            userData.removeValue();
                            data.getParent().child("Available")
                                    .child(SlotNumber).setValue(SlotName);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
        a.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                iv.setImageBitmap(bitmap);
            }
            if(msg.what==1){
                progress.setVisibility(View.GONE);
            }
        }
    };
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ////////////////////Navigation////////////////////////////////////////////////////
        mMapView = view.findViewById(R.id.navigate_map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }
    private Bitmap TextToImageEncode(String Value) throws WriterException  {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        ContextCompat.getColor(getContext(),R.color.black):ContextCompat.getColor(getContext(),R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 700, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Bundle bundle = this.getArguments();
        Log.d("Values_BCF", "CurrentLat: "+bundle.getDouble("CurrentLat")+ " CurrentLong: "+bundle.getDouble("CurrentLong")+" DestinationLat: "+ bundle.getDouble("DestinationLat")+" DestinationLong: "+bundle.getDouble("DestinationLong"));
        ArrayList<Marker> markers = new ArrayList<Marker>();    //Arraylist to store source and destination marker
        Marker mMarker = mGoogleMap.addMarker(new MarkerOptions()   //Source marker
                .position(new LatLng(bundle.getDouble("CurrentLat"), bundle.getDouble("CurrentLong")))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        markers.add(mMarker);                                   //Adding source marker to Arraylist
        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(bundle.getDouble("DestinationLat"),bundle.getDouble("DestinationLong")))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));  //Destination Marker
        markers.add(mMarker);                                    //Adding Destination marker to Arraylist
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        //Log.d(TAG, "onMapReady: "+markers.get(0).toString()+"marker 2: "+markers.get(1).toString());
        LatLngBounds bounds = builder.build();
        int padding = 45; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        //googleMap.animateCamera(cu);
        //String url = generateURL(bundle);
        //TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        //taskRequestDirections.execute(url);
    }
    private String requestDirection(String reqUrl) throws IOException, IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //Get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private String generateURL(Bundle bundle) {
        String url= null;
        if (bundle != null) {
            String origin = "origin="+bundle.getDouble("CurrentLat")+","+bundle.getDouble("CurrentLong");
            String dest = "destination="+bundle.getDouble("DestinationLat")+","+bundle.getDouble("DestinationLong");
            String sensor = "sensor=flase";
            String mode = "mode=driving";
            String param = origin + "&"+dest+"&"+sensor+"&"+mode;
            url = "https://maps.googleapis.com/maps/api/directions/" +"json"+"?"+param;
            Log.d("directionURL", "generateURL: "+ url);

        }
        return url;
    }
    public class TaskRequestDirections extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return  responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Parse json here
            TaskParser taskParser = new TaskParser();
            taskParser.execute(s);
        }
    }
    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsParser directionsParser = new DirectionsParser();
                routes = directionsParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            //Get list route and display it into the map

            ArrayList points = null;

            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path : lists) {
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLACK);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions!=null) {
                mGoogleMap.addPolyline(polylineOptions);
            } else {
                //Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Bundle bundle = this.getArguments();

        if(state_save == 0){
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("Status", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("status",status);
            editor.putString("ParkingName",marker);
            editor.putFloat("CurrentLat", (float) bundle.getDouble("CurrentLat"));
            editor.putFloat("CurrentLong", (float) bundle.getDouble("CurrentLong"));
            editor.putFloat("DestinationLat", (float) bundle.getDouble("DestinationLat"));
            editor.putFloat("DestinationLong", (float) bundle.getDouble("DestinationLong"));
            editor.putString("status",status);
            editor.commit();
        }
        Log.d(TAG, "onPause: "+ "sp_saved");
    }
}
