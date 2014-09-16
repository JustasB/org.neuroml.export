package org.lemsml.export.base;


import java.io.IOException;

import org.lemsml.jlems.core.sim.LEMSException;
import org.lemsml.jlems.core.type.Lems;

public abstract class BaseWriter {

	protected Lems lems;
	protected final String format;

	public BaseWriter(Lems lems, String format){
		this.lems = lems;
		this.format = format;
	}
	

	public abstract String getMainScript() throws LEMSException, IOException;

	public class CompInfo
	{
		public StringBuilder stateVars = new StringBuilder();
		public StringBuilder params = new StringBuilder();
		public StringBuilder eqns = new StringBuilder();
		public StringBuilder initInfo = new StringBuilder();
	}


}

