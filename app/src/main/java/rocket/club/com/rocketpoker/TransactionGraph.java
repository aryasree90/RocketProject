package rocket.club.com.rocketpoker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rocket.club.com.rocketpoker.classes.DetailsListClass;
import rocket.club.com.rocketpoker.utils.AppGlobals;


/**
 * Created by Admin on 1/28/2017.
 */
public class TransactionGraph extends AppCompatActivity {

    private View mChart;
    private Context context = null;
    private AppGlobals appGlobals = null;
    MaterialBetterSpinner filter1, filter2, filter3;
    ArrayAdapter<String> filter1Adapter, filter2Adapter, filter3Adapter;
    List<String> yearList = new ArrayList<>();

    String[] filter1Val = {"Month", "Year"};
    String[] filter2Val = null;
    String[] filter3Val = {"All", "Cash In", "Cash Out"};

    XYSeries cashIn = new XYSeries("Cash In");
    XYSeries cashOut = new XYSeries("Cash Out");

    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
    final SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        initializeWidgets();

        Date mon = new Date(System.currentTimeMillis());
        int curMonth = Integer.parseInt(monthFormat.format(mon));

        filter1.setText(filter1Val[0]);
        filter2.setText(appGlobals.monthList[curMonth]);
        filter3.setText(filter3Val[0]);

        setGraphData(1, curMonth, 1);
    }

    private void initializeWidgets() {

        context = getApplicationContext();
        appGlobals = AppGlobals.getInstance(context);

        Toolbar toolBar = (Toolbar)findViewById(R.id.hometoolbar);
        setSupportActionBar(toolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.graph));
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createYearList();

        filter1Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, filter1Val);
        filter1 = (MaterialBetterSpinner)
                findViewById(R.id.filter1);
        filter1.setAdapter(filter1Adapter);

        filter2 = (MaterialBetterSpinner)
                findViewById(R.id.filter2);
        resetFilter2(appGlobals.monthList);

        filter3Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, filter3Val);
        filter3 = (MaterialBetterSpinner)
                findViewById(R.id.filter3);
        filter3.setAdapter(filter3Adapter);

        createClickListeners();
    }

    private void createClickListeners() {

        filter1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    filter2Val = appGlobals.monthList;
                } else {
                    filter2Val = yearList.toArray(new String[yearList.size()]);
                }
                resetFilter2(filter2Val);
                resetGraph();
            }
        });

        filter2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resetGraph();
            }
        });

        filter3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resetGraph();
            }
        });
    }

    private void resetGraph() {
        String filter1Str = filter1.getText().toString();
        String filter2Str = filter2.getText().toString();
        String filter3Str = filter3.getText().toString();

        int filter1Pos = 0, filter2Pos = 0, filter3Pos = 0;
        for (String str : filter1Val) {
            ++filter1Pos;
            if (str.equals(filter1Str)) {
                break;
            }
        }

        if(filter2Str.isEmpty()) {
            Date curDate = new Date(System.currentTimeMillis());
            if(filter1Pos == 1) {
                filter2Pos = Integer.parseInt(monthFormat.format(curDate));
            } else {
                String yearVal = yearFormat.format(curDate);

                for(String str : filter2Val) {
                    ++filter2Pos;
                    if(str.equals(yearVal)) {
                        break;
                    }
                }
            }

        } else {
            for (String str : filter2Val) {
                ++filter2Pos;
                if (str.equals(filter2Str)) {
                    break;
                }
            }
        }

        if(filter3Str.isEmpty()) {
            filter3Pos = 1;
        } else {
            for (String str : filter3Val) {
                ++filter3Pos;
                if (str.equals(filter3Str)) {
                    break;
                }
            }
        }
        setGraphData(filter1Pos, filter2Pos, filter3Pos);
    }

    private void resetFilter2(String[] val) {
        filter2Val = val;
        filter2Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, val);
        filter2.setAdapter(filter2Adapter);

        filter2.setText(val[0]);
    }

    private void createYearList() {
        yearList.clear();

        Date dateYear = new Date(System.currentTimeMillis());
        int curYear = Integer.parseInt(yearFormat.format(dateYear));
        final int startYear = 2017;
        if(curYear > startYear) {
            int diff = curYear - startYear;
            for(int i=0; i<diff; i++) {
                yearList.add("" + (startYear + i));
            }
        } else {
            yearList.add("" + startYear);
        }
    }

    private void setGraphData(int filter1Pos, int filter2Pos, int filter3Pos) {

        cashIn.clear();
        cashOut.clear();

        int maxAmt = 0, loopCount = 0;

        if(filter1Pos == 1) {
            loopCount = 31;
        } else {
            loopCount = 12;
        }

        for(int i=0; i<loopCount; i++) {
            int inAmt = 0, outAmt = 0;

            for(DetailsListClass detailList : appGlobals.chartList) {
                Date curDate = new Date(Long.parseLong(detailList.getItem3()));

                if(filter1Pos == 1) {
                    int month = Integer.parseInt(monthFormat.format(curDate));
                    if(filter2Pos == month) {
                        int day = Integer.parseInt(dateFormat.format(curDate)) - 1;

                        if (i == day) {
                            String transType = detailList.getItem1();
                            if (transType.equals("Cash In")) {
                                inAmt += Integer.parseInt(detailList.getItem2());
                            } else if (transType.equals("Cash Out")) {
                                outAmt += Integer.parseInt(detailList.getItem2());
                            }
                        }
                    }
                } else {
                    String yearVal = yearFormat.format(curDate);
                    int year = 0;

                    for(String str : filter2Val) {
                        ++year;
                        if(str.equals(yearVal)) {
                            break;
                        }
                    }

                    if(filter2Pos == year) {
                        int month = Integer.parseInt(monthFormat.format(curDate)) - 1;

                        if (i == month) {
                            String transType = detailList.getItem1();
                            if (transType.equals("Cash In")) {
                                inAmt += Integer.parseInt(detailList.getItem2());
                            } else if (transType.equals("Cash Out")) {
                                outAmt += Integer.parseInt(detailList.getItem2());
                            }
                        }
                    }
                }
            }

            cashIn.add(i, inAmt);
            cashOut.add(i, outAmt);

            if(filter3Pos == 2) {
                cashOut.clear();
            } else if(filter3Pos == 3) {
                cashIn.clear();
            }

            if(inAmt > maxAmt) {
                maxAmt = inAmt;
            }

            if(outAmt > maxAmt) {
                maxAmt = outAmt;
            }
        }
        openChart(loopCount, maxAmt);
    }

    private void openChart(int xAxis, int maxAmt){

// Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
// Adding Income Series to the dataset
        dataset.addSeries(cashIn);
// Adding Expense Series to dataset
        dataset.addSeries(cashOut);

// Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.CYAN); //color of the graph set to cyan
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2f);
        incomeRenderer.setDisplayChartValues(true);
//setting chart value distance
        incomeRenderer.setDisplayChartValuesDistance(10);
//setting line graph point style to circle
        incomeRenderer.setPointStyle(PointStyle.CIRCLE);
//setting stroke of the line chart to solid
        incomeRenderer.setStroke(BasicStroke.SOLID);

// Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.GREEN);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2f);
        expenseRenderer.setDisplayChartValues(true);
//setting line graph point style to circle
        expenseRenderer.setPointStyle(PointStyle.SQUARE);
//setting stroke of the line chart to solid
        expenseRenderer.setStroke(BasicStroke.SOLID);

// Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setChartTitle(getString(R.string.trans_graph));
        multiRenderer.setXTitle(filter1.getText().toString());
        multiRenderer.setYTitle(getString(R.string.amount));

/***
 * Customizing graphs
 */
//setting text size of the title
        multiRenderer.setChartTitleTextSize(28);
//setting text size of the axis title
        multiRenderer.setAxisTitleTextSize(24);
//setting text size of the graph lable
        multiRenderer.setLabelsTextSize(24);
//setting zoom buttons visiblity
        multiRenderer.setZoomButtonsVisible(false);
//setting pan enablity which uses graph to move on both axis
        multiRenderer.setPanEnabled(false, false);
//setting click false on graph
        multiRenderer.setClickEnabled(false);
//setting zoom to false on both axis
        multiRenderer.setZoomEnabled(false, false);
//setting lines to display on y axis
        multiRenderer.setShowGridY(true);
//setting lines to display on x axis
        multiRenderer.setShowGridX(true);
//setting legend to fit the screen size
        multiRenderer.setFitLegend(true);
//setting displaying line on grid
        multiRenderer.setShowGrid(true);
//setting zoom to false
        multiRenderer.setZoomEnabled(false);
//setting external zoom functions to false
        multiRenderer.setExternalZoomEnabled(false);
//setting displaying lines on graph to be formatted(like using graphics)
        multiRenderer.setAntialiasing(true);
//setting to in scroll to false
        multiRenderer.setInScroll(false);
//setting to set legend height of the graph
        multiRenderer.setLegendHeight(30);
//setting x axis label align
        multiRenderer.setXLabelsAlign(Align.CENTER);
//setting y axis label to align
        multiRenderer.setYLabelsAlign(Align.LEFT);
//setting text style
        multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
//setting no of values to display in y axis
        multiRenderer.setYLabels(10);
// setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
// if you use dynamic values then get the max y value and set here
        multiRenderer.setYAxisMax(maxAmt);
//setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMin(-0.5);
//setting used to move the graph on xaxiz to .5 to the right
        multiRenderer.setXAxisMax(xAxis);
//setting bar size or space between two bars
//multiRenderer.setBarSpacing(0.5);
//Setting background color of the graph to transparent
        multiRenderer.setBackgroundColor(Color.TRANSPARENT);
//Setting margin color of the graph to transparent
        multiRenderer.setMarginsColor(R.color.transparent_background);
        multiRenderer.setApplyBackgroundColor(true);
        multiRenderer.setScale(2f);
//setting x axis point size
        multiRenderer.setPointSize(4f);
//setting the margin size for the graph in the order top, left, bottom, right
        multiRenderer.setMargins(new int[]{30, 30, 30, 30});

        for(int i=0; i< xAxis;i++){
//            multiRenderer.addXTextLabel(i, appGlobals.monthList[i]);
              multiRenderer.addXTextLabel(i, "" + (i+1));
        }

// Adding incomeRenderer and expenseRenderer to multipleRenderer
// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
// should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

//this part is used to display graph on the xml
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart);
//remove any views before u paint the chart
        chartContainer.removeAllViews();
//drawing bar chart
        mChart = ChartFactory.getLineChartView(TransactionGraph.this, dataset, multiRenderer);
//adding the view to the linearlayout
        chartContainer.addView(mChart);

    }
}


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ChartFragment())
                    .commit();
        }
    }
}*/