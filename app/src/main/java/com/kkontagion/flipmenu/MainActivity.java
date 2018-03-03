package com.kkontagion.flipmenu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int READ_REQUEST_CODE = 200;
    private static final int WRITE_REQUEST_CODE = 201;
    private static final int RequestPermissionCode = 7;

    private String imagePath;
    private String filename;
    private boolean camera_request = false;
    private boolean read_request = false;
    private boolean write_request = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button clickButton = (Button) findViewById(R.id.bt_translate);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckingPermissionIsEnabledOrNot())
                {
//                    Toast.makeText(MainActivity.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
                    accessCamera();
                }
                else {
                    RequestMultiplePermission();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void accessCamera() {
            File mainDirectory = new File( Environment.getExternalStorageDirectory() + "/fancaidan");
            if (!mainDirectory.exists()) {
                if (mainDirectory.mkdirs()) {
                    Log.d("Main Directory Created", "Successfully created the parent dir:" + mainDirectory.getName());
                } else {
                    Log.d("Directory Not Created", "Failed to create the parent dir:" + mainDirectory.getName());
                }
            }

            File photosFileDirectory = new File(Environment.getExternalStorageDirectory() + "/fancaidan/photos");
            if (!photosFileDirectory.exists()) {
                if (photosFileDirectory.mkdirs()) {
                    Log.d("Photos Dir Created", "Successfully created the parent dir:" + photosFileDirectory.getName());
                } else {
                    Log.d("Photos Dir Not Created", "Failed to create the parent dir:" + photosFileDirectory.getName());
                }
            }

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            filename = ts+".png";

            imagePath = photosFileDirectory.getAbsolutePath() + "/" + filename;
            File imageFile = new File(imagePath);

            Uri outputFileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",imageFile);

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, RequestPermissionCode);

//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CAMERA},MY_CAMERA_REQUEST_CODE);
//            camera_request = true;
//        }
//
//        else if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},WRITE_REQUEST_CODE);
//            write_request = true;
//        }
//
//        else if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{READ_EXTERNAL_STORAGE},READ_REQUEST_CODE);
//            read_request = true;
//        }
//
//        else{
//            System.out.print("in");
//            File mainDirectory = new File( Environment.getExternalStorageDirectory() + "/fancaidan");
//            if (!mainDirectory.exists()) {
//                if (mainDirectory.mkdirs()) {
//                    Log.d("Main Directory Created", "Successfully created the parent dir:" + mainDirectory.getName());
//                } else {
//                    Log.d("Directory Not Created", "Failed to create the parent dir:" + mainDirectory.getName());
//                }
//            }
//
//            File photosFileDirectory = new File(Environment.getExternalStorageDirectory() + "/fancaidan/photos");
//            if (!photosFileDirectory.exists()) {
//                if (photosFileDirectory.mkdirs()) {
//                    Log.d("Photos Dir Created", "Successfully created the parent dir:" + photosFileDirectory.getName());
//                } else {
//                    Log.d("Photos Dir Not Created", "Failed to create the parent dir:" + photosFileDirectory.getName());
//                }
//            }
//
//            Long tsLong = System.currentTimeMillis()/1000;
//            String ts = tsLong.toString();
//            filename = ts+".png";
//
//            imagePath = photosFileDirectory.getAbsolutePath() + "/" + filename;
//            File imageFile = new File(imagePath);
//
//            Uri outputFileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",imageFile);
//
//            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, outputFileUri);
//            startActivityForResult(intent, MY_CAMERA_REQUEST_CODE);
//        }
    }

    private void RequestMultiplePermission() {
    // Creating String Array with Permissions.
    ActivityCompat.requestPermissions(MainActivity.this, new String[]
            {
                    Manifest.permission.CAMERA,
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
            }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == RequestPermissionCode) {
            if (grantResults.length > 0) {
                boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean ReadPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean WritePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (CameraPermission && ReadPermission && WritePermission) {
//                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    accessCamera();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();

                }
            }

        }
//        if (requestCode == RequestPermissionCode) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
//            }
//        }
//        if (requestCode == WRITE_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "WRITE Permission Granted", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "WRITE Permission Denied", Toast.LENGTH_LONG).show();
//            }
//        }
//        if (requestCode == READ_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "WRITE Permission Granted", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "WRITE Permission Denied", Toast.LENGTH_LONG).show();
//            }
//        }
    }

    public boolean CheckingPermissionIsEnabledOrNot() {
        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == RequestPermissionCode)
        {
            if(resultCode == RESULT_OK) {
                File image = new File(imagePath);
                Toast.makeText(this, imagePath, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, ConfirmActivity.class);
                intent.putExtra("filepath", image.getAbsolutePath());
                startActivity(intent);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}