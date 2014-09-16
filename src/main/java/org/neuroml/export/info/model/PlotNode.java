package org.neuroml.export.info.model;

import java.util.ArrayList;
import java.util.List;

import org.neuroml.export.info.model.pairs.IterablePair;
import org.neuroml.export.info.model.pairs.Pair;

/**
 * @author borismarin
 * 
 */
public class PlotNode
{

	private String _plotTitle;
	private String _xAxisLabel;
	private String _yAxisLabel;
	private List<Data> _data=null;

	public PlotNode(String plotTitle, String xAxisLabel, String yAxisLabel)
	{
		super();
		this._plotTitle = plotTitle;
		this._xAxisLabel = xAxisLabel;
		this._yAxisLabel = yAxisLabel;
	}

	/**
	 * @return the _plotTitle
	 */
	public String getPlotTitle()
	{
		return _plotTitle;
	}

	/**
	 * @return the _xAxisLabel
	 */
	public String getXAxisLabel()
	{
		return _xAxisLabel;
	}

	/**
	 * @return the _yAxisLabel
	 */
	public String getYAxisLabel()
	{
		return _yAxisLabel;
	}


	/**
	 * @return
	 */
	public List<Data> getData()
	{
		if(_data==null)
		{
			_data=new ArrayList<Data>();
		}
		return _data;
	}

	@Override
	public String toString()
	{
		return "PlotNode [Title=" + _plotTitle + ", X=" + _xAxisLabel + ", Y=" + _yAxisLabel + ", Data=" + toTruncatedArray(_data) + "]";
	}
    
	public String toShortString()
	{
		return "PlotNode [Title=" + _plotTitle + ", X=" + _xAxisLabel + ", Y=" + _yAxisLabel + ", Num data points=" + _data.size() + "]";
	}

	private String toTruncatedArray(List<Data> _data) {
		StringBuilder sb = new StringBuilder();
		
		for(Data data : _data)
		{
			IterablePair<Double,Double> listPair = new IterablePair<Double, Double>(data.getXData(),data.getYData());
			for (Pair<Double, Double> d: listPair)
			{
				sb.append(String.format("%.4f %.4f", d.first(), d.second()));
			}
		}
		return sb.toString();

	}
	
	
}
