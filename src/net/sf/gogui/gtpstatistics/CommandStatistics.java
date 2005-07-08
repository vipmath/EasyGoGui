//----------------------------------------------------------------------------
// $Id$
// $Source$
//----------------------------------------------------------------------------

package net.sf.gogui.gtpstatistics;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.text.DecimalFormat;
import net.sf.gogui.utils.ErrorMessage;
import net.sf.gogui.utils.FileUtils;
import net.sf.gogui.utils.Histogram;
import net.sf.gogui.utils.Statistics;
import net.sf.gogui.utils.Table;
import net.sf.gogui.utils.TableUtils;

//----------------------------------------------------------------------------

public final class CommandStatistics
{
    public final PositionStatistics m_statisticsAll;

    public final PositionStatistics m_statisticsFinal;

    /** Vector<PositionStatistics> */
    public final Vector m_statisticsAtMove;

    public final Table m_tableAtMove;

    public CommandStatistics(String command, Table table, Table tableFinal,
                             File histoFile, File histoFileFinal,
                             Color color, int precision)
        throws Exception
    {
        m_statisticsAll = new PositionStatistics(command, table, false, 0, 0);
        double min = m_statisticsAll.getMin();
        double max = m_statisticsAll.getMax();
        m_statisticsFinal
            = new PositionStatistics(command, tableFinal, true, min, max);
        m_statisticsAtMove = new Vector();
        Vector columnTitles = new Vector();
        columnTitles.add("Move");
        columnTitles.add("Mean");
        columnTitles.add("Error");
        m_tableAtMove = new Table(columnTitles);
        Table tableAtMove;
        int maxMove = (int)(TableUtils.getMax(table, "Move") + 1);
        for (int move = 1; move <= maxMove; ++move)
        {
            tableAtMove = TableUtils.selectIntRange(table, "Move", move,
                                                    move);
            PositionStatistics statisticsAtMove
                = new PositionStatistics(command, tableAtMove, true, min,
                                         max);
            m_statisticsAtMove.add(statisticsAtMove);
            m_tableAtMove.startRow();
            m_tableAtMove.set("Move", move);
            if (statisticsAtMove.getCount() > 0)
            {
                m_tableAtMove.set("Mean", statisticsAtMove.getMean());
                m_tableAtMove.set("Error", statisticsAtMove.getError());
            }
        }
        if (getCount() > 0)
        {
            Histogram histogram = m_statisticsAll.m_histogram;
            Table histoTable = TableUtils.fromHistogram(histogram, command);
            Plot plot = new Plot(150, 150, color, precision);
            setHistogramProperties(plot);
            plot.plot(histoFile, histoTable, command, "Count", null);
            histogram = m_statisticsFinal.m_histogram;
            if (m_statisticsFinal.getCount() > 0)
            {
                histoTable = TableUtils.fromHistogram(histogram, command);
                plot = new Plot(150, 150, color, precision);
                setHistogramProperties(plot);
                plot.plot(histoFileFinal, histoTable, command, "Count", null);
            }
        }
    }

    public int getCount()
    {
        return m_statisticsAll.getCount();
    }

    public PositionStatistics getStatistics(int moveInterval)
    {
        return (PositionStatistics)m_statisticsAtMove.get(moveInterval);
    }

    public boolean onlyBoolValues()
    {
        return m_statisticsAll.m_onlyBoolValues;
    }

    public void setHistogramProperties(Plot plot)
    {
        Histogram histogram = m_statisticsAll.m_histogram;
        double step = histogram.getStep();
        plot.setPlotStyleBars(step);
        plot.setYMin(0);
        if (onlyBoolValues())
        {
            plot.setXMin(-5);
            plot.setXMax(5);
        }
        else
        {
            plot.setXMin(histogram.getMin() - step / 2);
            plot.setXMax(histogram.getMax() + step / 2);
        }
    }
}

//----------------------------------------------------------------------------