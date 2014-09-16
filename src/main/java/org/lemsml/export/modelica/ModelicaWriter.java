package org.lemsml.export.modelica;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.lemsml.export.base.GenerationException;
import org.lemsml.export.dlems.DLemsKeywords;
import org.lemsml.export.dlems.DLemsWriter;
import org.lemsml.jlems.core.logging.E;
import org.lemsml.jlems.core.logging.MinimalMessageHandler;
import org.lemsml.jlems.core.sim.LEMSException;
import org.lemsml.jlems.core.type.Lems;
import org.lemsml.jlems.io.util.FileUtil;
import org.neuroml.export.ModelFeature;
import org.neuroml.export.ModelFeatureSupportException;
import org.neuroml.export.SupportLevelInfo;
import org.neuroml.export.base.BaseWriter;
import org.neuroml.model.util.NeuroMLException;


@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class ModelicaWriter extends BaseWriter {

	private final String classTemplateFile = "modelica/main_class.vm";
	private final String runTemplateFile = "modelica/run.vm";

	String comm = "// ";
	String commPre = "/*";
	String commPost = "*/";

    public ArrayList<File> allGeneratedFiles = new ArrayList<File>();

	public ModelicaWriter(Lems lems) throws ModelFeatureSupportException, LEMSException, NeuroMLException {
		super(lems, "Modelica");
		MinimalMessageHandler.setVeryMinimal(true);
		E.setDebug(false);
        sli.checkAllFeaturesSupported(FORMAT, lems);
	}
    
    
    @Override
    protected void setSupportedFeatures() {
        sli.addSupportInfo(FORMAT, ModelFeature.ABSTRACT_CELL_MODEL, SupportLevelInfo.Level.MEDIUM);
        sli.addSupportInfo(FORMAT, ModelFeature.COND_BASED_CELL_MODEL, SupportLevelInfo.Level.LOW);
        sli.addSupportInfo(FORMAT, ModelFeature.SINGLE_COMP_MODEL, SupportLevelInfo.Level.MEDIUM);
        sli.addSupportInfo(FORMAT, ModelFeature.NETWORK_MODEL, SupportLevelInfo.Level.LOW);
        sli.addSupportInfo(FORMAT, ModelFeature.MULTI_CELL_MODEL, SupportLevelInfo.Level.NONE);
        sli.addSupportInfo(FORMAT, ModelFeature.MULTI_POPULATION_MODEL, SupportLevelInfo.Level.NONE);
        sli.addSupportInfo(FORMAT, ModelFeature.NETWORK_WITH_INPUTS_MODEL, SupportLevelInfo.Level.NONE);
        sli.addSupportInfo(FORMAT, ModelFeature.NETWORK_WITH_PROJECTIONS_MODEL, SupportLevelInfo.Level.NONE);
        sli.addSupportInfo(FORMAT, ModelFeature.MULTICOMPARTMENTAL_CELL_MODEL, SupportLevelInfo.Level.NONE);
        sli.addSupportInfo(FORMAT, ModelFeature.HH_CHANNEL_MODEL, SupportLevelInfo.Level.LOW);
        sli.addSupportInfo(FORMAT, ModelFeature.KS_CHANNEL_MODEL, SupportLevelInfo.Level.NONE);
    }
	
	@Override
	protected void addComment(StringBuilder sb, String comment) {
	
		if (comment.indexOf("\n") < 0)
			sb.append(comm + comment + "\n");
		else
			sb.append(commPre + "\n" + comment + "\n" + commPost + "\n");
	}
	

	@Override
	public String getMainScript() throws GenerationException {
		return generateMainScriptAndCompFiles(null);
	}
		
	public String generateMainScriptAndCompFiles(File dirForFiles) throws GenerationException {

		StringBuilder mainRunScript = new StringBuilder();
		StringBuilder compScript = new StringBuilder();

		addComment(mainRunScript, FORMAT+" simulator compliant export for:\n\n"
		+ lems.textSummary(false, false));
		
		addComment(compScript, FORMAT+" simulator compliant export for:\n\n"
		+ lems.textSummary(false, false));
		
		Velocity.init();
		
		VelocityContext context = new VelocityContext();


		try
		{
            DLemsWriter somw = new DLemsWriter(lems);
            
			String som = somw.getMainScript();
			
			DLemsWriter.putIntoVelocityContext(som, context);
        
			Properties props = new Properties();
			props.put("resource.loader", "class");
			props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			VelocityEngine ve = new VelocityEngine();
			ve.init(props);
			Template template = ve.getTemplate(runTemplateFile);
		   
			StringWriter sw1 = new StringWriter();

			template.merge( context, sw1 );
			
			mainRunScript.append(sw1);
			
			template = ve.getTemplate(classTemplateFile);

			StringWriter sw2 = new StringWriter();

			template.merge( context, sw2 );
			
			compScript.append(sw2);
			
			if (dirForFiles!=null && dirForFiles.exists())
			{
				E.info("Writing Modelica files to: "+dirForFiles);
				String name = (String)context.internalGet(DLemsKeywords.NAME.get());
				File mainScriptFile = new File(dirForFiles, "run_"+name+".mos");
				File compScriptFile = new File(dirForFiles, name+".mo");
	            FileUtil.writeStringToFile(mainRunScript.toString(), mainScriptFile);
	            allGeneratedFiles.add(mainScriptFile);
	            FileUtil.writeStringToFile(compScript.toString(), compScriptFile);
	            allGeneratedFiles.add(compScriptFile);
			}
			else
			{
				E.info("Not writing Modelica scripts to files! Problem with target dir: "+dirForFiles);
			}
			
			
		} 
		catch (IOException e1) {
			throw new GenerationException("Problem converting LEMS to dLEMS",e1);
		} catch( VelocityException e ) {
			throw new GenerationException("Problem using Velocity template",e);
		} catch (LEMSException e) {
			throw new GenerationException("Problem generating the files",e);
        } 
		
		return mainRunScript.toString();	

	}

}