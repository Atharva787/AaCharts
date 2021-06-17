package com.example.aacharts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button b1,b2,b3,b4, logging,clear,export;
    Context context;
    static GraphView gv;
    static LineGraphSeries<DataPoint> lg = new LineGraphSeries<>();
    static boolean running=false, is_log = true;
    static final ArrayList<GraphPoint> coordinates = new ArrayList<GraphPoint>();
    static int maxY = 20;
    static int minY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("In", "onCreate of MainActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printPoints(coordinates);

        context = getApplicationContext();
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        logging = findViewById(R.id.log);
        clear = findViewById(R.id.clear);
        export = findViewById(R.id.export);

        gv = findViewById(R.id.graph);
        gv.addSeries(lg);

        gv.getViewport().setXAxisBoundsManual(true);
        gv.getViewport().setMinX(2);
        gv.getViewport().setMaxX(50);
        gv.getViewport().setYAxisBoundsManual(true);
        gv.getViewport().setMinY(minY);
        gv.getViewport().setMaxY(maxY);

        gv.getViewport().setScalable(true);
        gv.getViewport().setScalableY(true);

        gv.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        gv.getViewport().setDrawBorder(true);


        if (is_log)
            logging.setText("Start Logging");
        else
            logging.setText("Stop Logging");

        b1.setOnClickListener(view -> {
            gv.getViewport().setMaxX(4);
            gv.addSeries(lg);
        });

        b2.setOnClickListener(view -> {
            gv.getViewport().setMaxX(20);
            gv.addSeries(lg);
        });

        b3.setOnClickListener(view -> {
            gv.getViewport().setMaxX(120);
            gv.addSeries(lg);
        });

        b4.setOnClickListener(view -> {
            gv.getViewport().setMaxX(240);
            gv.addSeries(lg);
        });

        logging.setOnClickListener(view -> {
            if (is_log) {
                is_log = false;
                logging.setText("Stop Logging");
                Intent mIntent = new Intent(context, ForegroundService.class);
                ForegroundService.enqueueWork(context, mIntent);
                clear.setVisibility(View.INVISIBLE);
                export.setVisibility(View.INVISIBLE);
            } else {
                logging.setText("Start Logging");
                is_log = true;
                if (running) {
                    clear.setVisibility(View.VISIBLE);
                    export.setVisibility(View.VISIBLE);
                }
                running = false;
            }
        });

        clear.setOnClickListener(view -> clear_graph());

        export.setOnClickListener(view -> {
            CSVWriter writer;
            String csv = (context.getExternalFilesDir(null).getAbsolutePath() + "/Logs.csv");
            try {
                writer = new CSVWriter(new FileWriter(csv));

                List<String[]> data = new ArrayList<String[]>();
                data.add(new String[]{"X", "Y"});
                for(int i=0; i<coordinates.size(); i++)
                {
                    String[] s = {String.valueOf(coordinates.get(i).x), String.valueOf(coordinates.get(i).y)};
                    data.add(s);
                }
                writer.writeAll(data); // data is adding to csv
                Toast.makeText(context,"Exported",Toast.LENGTH_LONG).show();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
        public static void clear_graph()
    {
        coordinates.clear();
        running = true;
        gv.removeAllSeries();
        lg = new LineGraphSeries<>();
        gv.addSeries(lg);
    }

    public static void logging()
    {
        Log.e("In","Logging Start");
        MainActivity.clear_graph();
        for(int i=0;i<500;i++)
        {
            Log.e("In","Running " + i);
            int y = (int) (Math.ceil(Math.random()*100));
            GraphPoint dp = new GraphPoint(i,y);
            MainActivity.coordinates.add(dp);
            MainActivity.addPoint(i,y);
            if(y > maxY) {
                maxY = y;
                gv.getViewport().setMinY(minY);
                gv.getViewport().setMaxY(maxY);
            }
            if(y < minY)
            {
                minY = y;
                gv.getViewport().setMinY(minY);
                gv.getViewport().setMaxY(maxY);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(!MainActivity.running)
                break;
        }
        Log.e("In","Logging End");
    }

    public static void addPoint(int x, int y)
    {
        lg.appendData(new DataPoint(x,y), false, 10000);
    }

    public static void printPoints(ArrayList<GraphPoint> coordinates)
    {
        Log.e("Point", "Start");
        for(int i = 0; i< coordinates.size(); i++)
            Log.e("Point","" + coordinates.get(i).x + " " + coordinates.get(i).y);
        Log.e("Point", "End");
    }
}

class GraphPoint
{
    final int x;
    final int y;

    GraphPoint(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}