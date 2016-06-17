package br.ramp.dcc888.nullanalyzer.display;

import java.io.IOException;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

import soot.Value;

public class GraphSingleton extends MultiGraph {

	private static GraphSingleton INSTANCE;
	private double x = 500.0;
	private double y = 890.0;
	private boolean flag = true;
	private int indexColor = 0;
	private String[] colors = new String[]{"fill-color: rgb(255,140,83);",
												  "fill-color: rgb(95,188,255);",
												  "fill-color: rgb(50,254,64);",
												  "fill-color: rgb(219,145,255);",
												  "fill-color: rgb(253,254,81);",
												  "fill-color: rgb(255,73,43);"};
	
	private GraphSingleton(String id) {
		super(id);		
	}
	
	public synchronized static GraphSingleton instance(){
		if(INSTANCE == null){
			System.setProperty("org.graphstream.ui.renderer",//gs.ui.renderer
					"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
			
			INSTANCE = new GraphSingleton("Nullness Analysis Graph");
			
			INSTANCE.addAttribute("ui.quality");
			INSTANCE.addAttribute("ui.antialias");
			
			//Trocar para stylesheet externo
			
			String styleSheet = 
					"node { "
						+ "stroke-mode: plain; "
						+ "fill-mode: dyn-plain; "
						+ "fill-color: rgb(255,140,83), rgb(95,188,255), rgb(50,254,64), rgb(219,145,255), rgb(253,254,81), rgb(255,73,43); "
						+ "shape: rounded-box;"
						+ "size-mode: fit;"
						+ "text-size:13; "
						+ "padding: 10px, 10px; "
						+ "text-alignment: above;"
						+ "text-visibility-mode: under-zoom;"
						+ "text-visibility: 0;"
					+ "}"
					+ "node.states { "
						+ "stroke-mode: plain; "
						+ "fill-mode: dyn-plain; "
						+ "fill-color: red; "
						+ "shape: rounded-box;"
						+ "size-mode: fit;"
						+ "text-size:13; "
						+ "padding: 10px, 10px;"
					+ "}"
				  + "edge {"
						+ "size: 1px;"
				  		+ "arrow-shape: arrow; "
				  		+ "arrow-size: 10px, 10px;"
				  	+ "}";

			INSTANCE.addAttribute("ui.stylesheet", styleSheet);
					
		
//			Viewer viewer = myInstance.display();
//			
//			DefaultView view = (DefaultView) viewer.getDefaultView();
//			viewer.disableAutoLayout();
			//view.resizeFrame(800, 600);
			//view.getCamera().setViewCenter(2, 3, 4);
//			view.getCamera().setViewPercent(0.5);
		//	System.out.println(view);
		}
		return INSTANCE;
	}
	
	public synchronized String getColor(){
		if(indexColor >= 6){			
			indexColor = 0;
		}
		return colors[indexColor++];						
	}
	
	public void saveImagePNG(String imageName){

		if (INSTANCE!=null) {
			FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.VGA);
			pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
			pic.setOutputPolicy(OutputPolicy.ON_RUNNER);
			pic.setRenderer(RendererType.SCALA);
			try {
				pic.writeAll(INSTANCE, imageName + ".png");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public synchronized void createEdges(Node localNode, List<Value> values, String prefixFull, String prefixShort, String currentColor) {
		if (values != null) {
			for (Value value : values) {
				Node valueNode = createNode(value, prefixFull, prefixShort, currentColor);
				createEdge(localNode, valueNode);
			}
		}
	}

	public synchronized void createEdge(Node localNode, Node dependencyNode) {
		String idEdge = localNode.getId() + "_" + dependencyNode.getId();
		Edge edge = INSTANCE.getEdge(idEdge);
		if (edge == null) {
			INSTANCE.addEdge(idEdge, dependencyNode, localNode, true).addAttribute("layout.weight", 18.358586);
		}
	}

	public synchronized Node createNode(Value leftOp, String prefixFull, String prefixShort, String currentColor) {
		String idNode = leftOp.toString();
		return createNode(idNode, prefixFull, prefixShort, currentColor);		
	}
	
	public synchronized Node createNode(String leftOp, String prefixFull, String prefixShort, String currentColor) {		
		String leftOpFullName = prefixFull +": "+ leftOp;
		String leftOpShortName = prefixShort +": "+ leftOp;
		Node localNode = INSTANCE.getNode(leftOpFullName);
		if (localNode == null) {
			localNode = INSTANCE.addNode(leftOpFullName);
			localNode.addAttribute("label", leftOpShortName);
			localNode.addAttribute("ui.style", currentColor);
			localNode.addAttribute("layout.weight", 80);
			localNode.addAttribute("repE", 10);			
			
		}
		return localNode;
	}
	

}
