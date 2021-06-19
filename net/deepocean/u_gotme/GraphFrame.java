// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import java.awt.Component;
import hirondelle.date4j.DateTime;
import java.util.ListIterator;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import javax.swing.JPopupMenu;
import org.jfree.chart.ChartPanel;
import javax.swing.JPanel;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.ShadowGenerator;
import org.jfree.chart.util.DefaultShadowGenerator;
import org.jfree.chart.plot.XYPlot;
import java.awt.Paint;
import java.awt.Color;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYDataset;
import java.util.List;
import javax.swing.JFrame;

public class GraphFrame extends JFrame
{
    List<HeartRatePoint> heartRateLog;
    String trackName;
    
    public GraphFrame() {
        this.setSize(800, 600);
        this.setResizable(false);
    }
    
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart chart = ChartFactory.createTimeSeriesChart("", "", "", dataset, false, false, false);
        chart.setBackgroundPaint((Paint)Color.white);
        final XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint((Paint)Color.lightGray);
        plot.setDomainGridlinePaint((Paint)Color.white);
        plot.setRangeGridlinePaint((Paint)Color.white);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.setShadowGenerator((ShadowGenerator)new DefaultShadowGenerator());
        final XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)r;
            renderer.setBaseShapesVisible(false);
            renderer.setBaseShapesFilled(false);
            renderer.setDrawSeriesLineAsPath(true);
        }
        final DateAxis timeAxis = (DateAxis)plot.getDomainAxis();
        timeAxis.setDateFormatOverride((DateFormat)new SimpleDateFormat("HH:mm"));
        return chart;
    }
    
    public JPanel createGraphPanel(final JFreeChart chart) {
        final ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(false);
        panel.setPopupMenu((JPopupMenu)null);
        return (JPanel)panel;
    }
    
    private XYDataset createHeartrateDataset(final List<HeartRatePoint> heartRateLog) {
        final TimeSeries chartData = new TimeSeries((Comparable)"Heartrate");
        final ListIterator<HeartRatePoint> iterator = heartRateLog.listIterator();
        while (iterator.hasNext()) {
            final HeartRatePoint point = iterator.next();
            final DateTime dateTime = point.getDateTime();
            final double heartRateValue = point.getHeartRateValue();
            if (heartRateValue > 0.0) {
                final Second graphTimeStamp = new Second((int)dateTime.getSecond(), (int)dateTime.getMinute(), (int)dateTime.getHour(), (int)dateTime.getDay(), (int)dateTime.getMonth(), (int)dateTime.getYear());
                chartData.addOrUpdate((RegularTimePeriod)graphTimeStamp, (double)point.getHeartRateValue());
            }
        }
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(chartData);
        return (XYDataset)dataset;
    }
    
    public String showTrackHeartRateLog(final String trackName, final List<HeartRatePoint> heartRateLog) {
        final JFreeChart chart = this.createChart(this.createHeartrateDataset(heartRateLog));
        chart.setTitle("Heartrate for " + trackName);
        final XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setRange(0.0, 220.0);
        final JPanel graphPanel = this.createGraphPanel(chart);
        this.add(graphPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        final String resultString = "";
        return resultString;
    }
    
    private XYDataset createSpeedDataset(final TrackSegment trackSegment) {
        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        final TimeSeries chartData = new TimeSeries((Comparable)"Speed");
        final ListIterator<TrackLogPoint> iterator = trackSegment.getTrackPoints().listIterator();
        while (iterator.hasNext()) {
            final TrackLogPoint point = iterator.next();
            final DateTime dateTime = point.getDateTime();
            final double speedValue = point.getSpeed();
            if (speedValue >= 0.0) {
                final Second graphTimeStamp = new Second((int)dateTime.getSecond(), (int)dateTime.getMinute(), (int)dateTime.getHour(), (int)dateTime.getDay(), (int)dateTime.getMonth(), (int)dateTime.getYear());
                chartData.addOrUpdate((RegularTimePeriod)graphTimeStamp, speedValue);
            }
        }
        dataset.addSeries(chartData);
        return (XYDataset)dataset;
    }
    
    public String showTrackSpeed(final String trackName, final Track track) {
        final JFreeChart chart = this.createChart(null);
        chart.setTitle("Speed for " + trackName);
        final XYPlot plot = chart.getXYPlot();
        plot.getDomainAxis().setLabel("Time");
        plot.getRangeAxis().setLabel("Speed (m/s)");
        chart.getXYPlot().getRangeAxis().setAutoRange(true);
        for (int numberOfSegments = track.getNumberOfSegments(), segment = 0; segment < numberOfSegments; ++segment) {
            chart.getXYPlot().setDataset(segment, this.createSpeedDataset(track.getSegment(segment)));
            chart.getXYPlot().setRenderer(segment, (XYItemRenderer)new StandardXYItemRenderer());
        }
        final JPanel graphPanel = this.createGraphPanel(chart);
        this.add(graphPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        final String resultString = "";
        return resultString;
    }
}
