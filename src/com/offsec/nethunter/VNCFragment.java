package com.offsec.nethunter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.DisplayMetrics;

import com.offsec.nethunter.utils.NhPaths;

public class VNCFragment  extends Fragment {

    SharedPreferences sharedpreferences;
    private Context mContext;
    private static final String TAG = "VNCFragment";
    String xwidth;
    String xheight;

    NhPaths nh;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public VNCFragment() {
    }
    public static VNCFragment newInstance(int sectionNumber) {
        VNCFragment fragment = new VNCFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.vnc_setup, container, false);
        sharedpreferences = getActivity().getSharedPreferences("com.offsec.nethunter", Context.MODE_PRIVATE);
        mContext = getActivity().getApplicationContext();

        // Get screen size to pass to VNC
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final int screen_height = displaymetrics.heightPixels;
        final int screen_width = displaymetrics.widthPixels;
        Log.d(TAG, "Real Height: " + screen_height + " Real Width: " + screen_width);

        // Because height and width changes on screen rotation, use the largest as width
        if (screen_height > screen_width){
            xwidth = Integer.toString(screen_height);
            xheight = Integer.toString(screen_width);
        } else {
            xwidth = Integer.toString(screen_width);
            xheight = Integer.toString(screen_height);
        }
        Log.d(TAG, "xHeight: " + xheight + " xWidth: " + xwidth);

        Button SetupVNCButton = (Button) rootView.findViewById(R.id.set_up_vnc);
        Button StartVNCButton = (Button) rootView.findViewById(R.id.start_vnc);
        Button StopVNCButton = (Button) rootView.findViewById(R.id.stop_vnc);
        Button OpenVNCButton = (Button) rootView.findViewById(R.id.vncClientStart);

        addClickListener(SetupVNCButton, new View.OnClickListener() {
            public void onClick(View v) {
                intentClickListener_NH("vncpasswd && exit"); // since is a kali command we can send it as is
            }
        });
        addClickListener(StartVNCButton, new View.OnClickListener() {
            public void onClick(View v) {
                intentClickListener_NH("vncserver :1 -localhost -geometry " + xwidth + "x" + xheight + " && echo \"Now you can exit, we can autoexit like in the vnc setup\""); // since is a kali command we can send it as is
            }
        });
        addClickListener(StopVNCButton, new View.OnClickListener() {
            public void onClick(View v) {
                intentClickListener_NH("vncserver -kill :1 && echo \"Now you can exit, we can autoexit like in the vnc setup\""); // since is a kali command we can send it as is
            }
        });
        addClickListener(OpenVNCButton, new View.OnClickListener() {
            public void onClick(View v) {
                intentClickListener_VNC(); // since is a kali command we can send it as is
            }
        });


        return rootView;
    }

    private void addClickListener(Button _button, View.OnClickListener onClickListener) {
        _button.setOnClickListener(onClickListener);
    }

    private void intentClickListener_VNC() {
        try {
            if(getView() == null){
                return;
            }

            String _R_IP = ((EditText) getView().findViewById(R.id.vnc_R_IP)).getText().toString();
            String _R_PORT = ((EditText) getView().findViewById(R.id.vnc_R_PORT)).getText().toString();
            String _PASSWD = ((EditText) getView().findViewById(R.id.vnc_PASSWD)).getText().toString();
            String _NICK = ((EditText) getView().findViewById(R.id.vnc_CONN_NICK)).getText().toString();
            String _USER = ((EditText) getView().findViewById(R.id.vnc_USER)).getText().toString();

            if(!_R_IP.equals("") && !_R_PORT.equals("") && !_NICK.equals("")){
                Intent intent = getActivity().getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.offsec.nhvnc");
                intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                intent.putExtra("com.offsec.nhvnc.EXTRA_CONN_DATA", true);
                intent.putExtra("R_IP", _R_IP);
                intent.putExtra("R_PORT", _R_PORT);
                intent.putExtra("PASSWD", _PASSWD);
                intent.putExtra("NICK", _NICK);
                intent.putExtra("USER", _USER);
                startActivity(intent);
            }

        } catch (Exception e) {
            Log.d("errorLAinching", e.toString());
            Toast.makeText(getActivity().getApplicationContext(), "NetHunter VNC not found!", Toast.LENGTH_SHORT).show();
        }
    }
    private void intentClickListener_NH(final String command) {
        try {
            Intent intent =
                    new Intent("com.offsec.nhterm.RUN_SCRIPT_NH");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("com.offsec.nhterm.iInitialCommand", command);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.toast_install_terminal), Toast.LENGTH_SHORT).show();

        }
    }
}