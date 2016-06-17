package br.ramp.dcc888.nullanalyzer;

import java.awt.Dimension;
import java.io.File;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;

import soot.Pack;
import soot.PackManager;
import soot.Transform;
import soot.options.Options;
import br.ramp.dcc888.nullanalyzer.display.GraphSingleton;
import br.ramp.dcc888.nullanalyzer.display.NullAnalyzerWin;
import br.ramp.dcc888.nullanalyzer.extra.FixSootXML;
import br.ramp.dcc888.nullanalyzer.extra.NullStateEnum;
import br.ramp.dcc888.nullanalyzer.extra.NullStateHelper;
import br.ramp.dcc888.nullanalyzer.extra.VariableRelationshipHelper;
import br.ramp.dcc888.nullanalyzer.extra.VulnerableMethodsHelper;
import br.ramp.dcc888.nullanalyzer.jtp.NullAnalysisTransformer;
import br.ramp.dcc888.nullanalyzer.jtp.NullnessAnalysisColorer;

public class NullPointerAnalysisMain {

	private static String NAT_TRANSFORMER_PACK = "jtp.nat";

	private static int TEST_CASE = 9;

	private static NullAnalyzerWin graphWindow = new NullAnalyzerWin();

	private final boolean FIX_XML = true;

	public static void main(String[] argv) {

		NullPointerAnalysisMain nullAnalyzer = new NullPointerAnalysisMain();

		nullAnalyzer.setupSootAndRunAnalysis(argv);
		nullAnalyzer.printVariableNullStates();
		// nullAnalyzer.printVariableRelationship();
		nullAnalyzer.printVulnerableMethods();
		// nullAnalyzer.processResultingGraph();
		// nullAnalyzer.buildGraphResultWindow();

	}

	private void buildGraphResultWindow() {
		/* Graph rendering */

		GraphSingleton graph = GraphSingleton.instance();

		Viewer viewer = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

		SpringBox layout = new SpringBox(false, new Random(0));

		viewer.enableAutoLayout(layout);

		DefaultView view = (DefaultView) viewer.addDefaultView(false);

		view.resizeFrame(800, 600);

		view.setPreferredSize(new Dimension(800, 500));

		graphWindow.appendView(view);

		/* -------------- */
	}

	private void printVulnerableMethods() {
		VulnerableMethodsHelper.instance().printVulnerableMethods();
	}

	private void printVariableNullStates() {
		NullStateHelper.instance().printVariableNullStates();
	}

	private void printVariableRelationship() {
		VariableRelationshipHelper.instance().printVariableRelationship();
	}

	private void setupSootAndRunAnalysis(String[] argv) {

		String jarLocation;

		try {
			jarLocation = getJarContainingFolder(this.getClass());
			// throw new Exception();
		} catch (Exception e) {
			jarLocation = new File("").getAbsolutePath();
		}

		jarLocation = jarLocation.replace("\\", "/");

		System.out.println("JAR LOCATION: " + jarLocation);

		/* PARAMETROS DE EXECUÇÃO */
		String fullTestClassName = getTestClassByNumber(TEST_CASE);

		/* ---------------------- */

		Pack jtp = PackManager.v().getPack("jtp");
		jtp.add(new Transform(NAT_TRANSFORMER_PACK,
				new NullAnalysisTransformer()));
		// jtp.add(new Transform("jtp.nac", new NullnessAnalysisColorer()));
		// //Exemplo interprocedural

		// Options.v().set_app(true);
		Options.v().set_src_prec(Options.src_prec_java);
		Options.v().set_output_format(Options.output_format_jimple);
		Options.v().set_output_dir(jarLocation + "/output");
		Options.v().set_soot_classpath(alternativeClassPath);
		Options.v().set_prepend_classpath(true);
		Options.v().set_xml_attributes(true);
		// Options.v().set_print_tags_in_output(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_keep_line_number(true);

		// Options.v().set_print_tags_in_output(true);
		// Options.v().set_verbose(true);
		// Options.v().set_java_version(6);
		// Options.v().set_via_shimple(true);
		// Options.v().set_include_all(true);
		// Options.v().set_oaat(true);
		// Options.v().set_whole_program(true);
		// Options.v().set_full_resolver(true);

		try {
			Class.forName(fullTestClassName);

			if (argv.length < 1) {
				
				System.out.println("USING INTERNAL PARAMETERS");
				
				argv = new String[1];

				// fullTestClassName = "javax.imageio.ImageWriter";
				// fullTestClassName =
				// "br.gov.prodemge.ssc.admin.negocio.usuario.UsuarioRN";
				// fullTestClassName = "X";

				argv[0] = fullTestClassName;
			} else {
				
				System.out.println("USING USER PARAMETERS");
				
				Options.v().set_src_prec(Options.src_prec_class);
				
				if (!Arrays.asList(argv).contains("--cp")) {
					// Just appending basic java library classpath
					String customClassPath = ";"+jarLocation+"/nullanalyzer.jar;${JAVA_HOME}/lib/rt.jar;${JAVA_HOME}/lib/jce.jar";
					Options.v().set_soot_classpath(customClassPath);
				}

				try {
					String testClassFromArg = argv[argv.length - 1];
					int testNumber = Integer.parseInt(testClassFromArg);
					argv[argv.length - 1] = getTestClassByNumber(testNumber);
				} catch (NumberFormatException e) {					
				}

			}

			System.out.println("Running with arguments: "
					+ Arrays.toString(argv));

			soot.Main.main(argv);

			if (FIX_XML) {
				String finalXML = Options.v().output_dir() + "/attributes/"
						+ fullTestClassName + ".xml";
				fixSootXML(finalXML);
			}

			// soot.Main.main(new String[]{"-v"});

			/* See Soot help */
			// soot.Main.main(new String[]{"-h"});

		} catch (ClassNotFoundException e) {
			System.out.println("NOT A VALID TEST CASE!");
			System.exit(1);
		}
	}

	private String getTestClassByNumber(int testCase) {
		String resourcesPath = "br.ramp.dcc888.nullanalyzer.resources";
		String prefixTestClassName = "NPTest";

		String fullTestClassName = String.format("%s.%s%02d", resourcesPath,
				prefixTestClassName, testCase);
		return fullTestClassName;
	}

	private void fixSootXML(String filePath) {
		new FixSootXML(filePath);
	}

	private void processResultingGraph() {
		GraphSingleton graph = GraphSingleton.instance();
		ConcurrentHashMap<String, NullStateEnum> states = NullStateHelper
				.instance().getVariableNullStateMap();
		ConcurrentHashMap<String, String> relations = VariableRelationshipHelper
				.instance().getVariableRelationshipMap();

		graph.addNode("NULL_NODE");
		graph.addNode("NON_NULL_NODE");
		graph.addNode("UNKNOWN_NODE");

		Node nullNode = graph.getNode("NULL_NODE");
		nullNode.setAttribute("ui.label", "Referência Nula");
		nullNode.setAttribute("ui.class", "states");
		Node nonNullNode = graph.getNode("NON_NULL_NODE");
		nonNullNode.setAttribute("ui.label", "Referência Não nula");
		nonNullNode.setAttribute("ui.class", "states");
		Node unknownNode = graph.getNode("UNKNOWN_NODE");
		unknownNode.setAttribute("ui.label", "Referência desconhecida");
		unknownNode.setAttribute("ui.class", "states");

		for (Iterator<Entry<String, NullStateEnum>> iterator = states
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, NullStateEnum> entry = (Entry<String, NullStateEnum>) iterator
					.next();
			String key = entry.getKey();
			NullStateEnum value = entry.getValue();

			Node last = graph.addNode(key + "_NODE");
			last.setAttribute("ui.label", key);

			if (value.equals(NullStateEnum.NULL)) {
				graph.addEdge(key, nullNode.getId(), last.getId());
			} else if (value.equals(NullStateEnum.NON_NULL)) {
				graph.addEdge(key, nonNullNode.getId(), last.getId());
			} else {
				graph.addEdge(key, unknownNode.getId(), last.getId());
			}

		}

		for (Iterator<Entry<String, String>> iterator = relations.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator
					.next();
			String key = entry.getKey();
			String value = entry.getValue();

			Node master = graph.getNode(key + "_NODE");
			Node slave = graph.getNode(value + "_NODE");

			graph.addEdge(key + new Random().nextInt(1000), master.getId(),
					slave.getId());

		}

	}

	public static String getJarContainingFolder(Class aclass) throws Exception {
		CodeSource codeSource = aclass.getProtectionDomain().getCodeSource();

		File jarFile;

		if (codeSource.getLocation() != null) {
			jarFile = new File(codeSource.getLocation().toURI());
		} else {
			String path = aclass.getResource(aclass.getSimpleName() + ".class")
					.getPath();
			String jarFilePath = path.substring(path.indexOf(":") + 1,
					path.indexOf("!"));
			jarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
			jarFile = new File(jarFilePath);
		}
		return jarFile.getParentFile().getAbsolutePath();
	}

	private static String wrongClassPath = "";

	private static String alternativeClassPath = "D:/AmbientePessoal/workspaceDCC888/nullanalyzer/src/main/java;"
			+ "C:/Prodemge/maven/.m2/repository/br/gov/prodemge/ssc/admin/ssc-admin-comum/3.0.39/ssc-admin-comum-3.0.39.jar;"
			+ "C:/Prodemge/maven/.m2/repository/br/gov/prodemge/ssc/admin/ssc-admin-dominio/3.0.39/ssc-admin-dominio-3.0.39.jar;"
			+ "C:/Prodemge/maven/.m2/repository/br/gov/prodemge/ssc/admin/ssc-admin-infraestrutura/3.0.39/ssc-admin-infraestrutura-3.0.39.jar;"
			+ "C:/Prodemge/maven/.m2/repository/br/gov/prodemge/ssc/admin/ssc-admin-negocio/3.0.39/ssc-admin-negocio-3.0.39.jar;"
			+ "C:/Prodemge/maven/.m2/repository/br/gov/prodemge/ssc/admin/ssc-admin-negocio-interface/3.14/ssc-admin-negocio-interface-3.14.jar;";

}
