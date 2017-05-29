/**
 * Created by piotrek on 24.05.17.
 */
import java.awt.*;
import java.sql.*;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Charter extends JFrame {

    public Charter() {

        initUI();
    }

    private void initUI() {

        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("Currency Diagram");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private CategoryDataset createDataset() {

        Connection c = null;
        Statement stmt = null;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/java", "java", "qwer1234");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT date, usd, euro, gbp FROM java;" );

//            TimeSeries USD = new TimeSeries("USD");
//            TimeSeries EURO = new TimeSeries("EURO");
//            TimeSeries GBP = new TimeSeries("GBP");
            final String series1 = "USD";
            final String series2 = "EURO";
            final String series3 = "GBP";

//            int counter = 0;

            while ( rs.next() ) {
                Date date = rs.getDate("date");
                Format formatter = new SimpleDateFormat("yyyy-MM-dd");
                String d = formatter.format(date);

                Float usd = rs.getFloat("usd");
                String s = Float.toString(usd);
                Float euro = rs.getFloat("euro");
                Float gbp = rs.getFloat("gbp");

//                USD.add(new Day(date), usd);
//                EURO.add(new Day(date), euro);
//                GBP.add(new Day(date), gbp);

//                counter ++;C
            dataset.addValue(usd, series1, d);
            dataset.addValue(euro, series2, d);
            dataset.addValue(gbp, series3, d);

            }

//            dataset.addSeries(USD);
//            dataset.addSeries(EURO);
//            dataset.addSeries(GBP);



            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");
        return dataset;
    }

    private JFreeChart createChart(final CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createLineChart(
                "Currency Diagram",
                "Day",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setForegroundAlpha(0.5f);

        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesShapesVisible(0, false);

        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));
        renderer.setSeriesShapesVisible(1, false);

        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesStroke(2, new BasicStroke(1.0f));
        renderer.setSeriesShapesVisible(2, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.black);

        CategoryAxis axis = chart.getCategoryPlot().getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        Font font = new Font("Dialog", Font.PLAIN, 8);
        axis.setTickLabelFont(font);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Currency Diagram",
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Charter ex = new Charter();
            ex.setVisible(true);
        });
    }
}