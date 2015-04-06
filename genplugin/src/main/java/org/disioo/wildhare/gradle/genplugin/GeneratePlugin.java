package org.disioo.wildhare.gradle.genplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.SourceSet;

/**
 * @author PEigenman
 */
public class GeneratePlugin implements Plugin<Project> {
   public static final String GENERATE_TASK_NAME = "domaindef_generate";
   public static final String GENERATE_CONFIG = "generate_config";
   
   
   public static final String OUTPUTDIR = "outputdir"; 
   public static final String BUILDDIR = "builddir";
   public static final String CONFIGDIR = "configdir";
   public static final String CONFIG = "config"; 
   public static final String BASEDIR = "basedir"; 

   

   @Override
   public void apply(Project prj) {
      // prj.getPlugins().apply(EclipsePlugin.class)
      prj.getPlugins().apply(JavaPlugin.class);
      final DomainDefGenerateTask myOwnTask = prj.getTasks().create(GENERATE_TASK_NAME, DomainDefGenerateTask.class);
      final GeneratePluginExtension generateExt = new GeneratePluginExtension ();
      prj.getExtensions().add (GENERATE_CONFIG, generateExt);
      final JavaPluginConvention javaPluginConvention = prj.getConvention().getPlugin(JavaPluginConvention.class);
      myOwnTask.setDomainDefExt (generateExt);
      // our task runs before the compile task 
      Set<Task> javaCompileTask = prj.getTasksByName(JavaPlugin.COMPILE_JAVA_TASK_NAME,true);
      javaCompileTask.forEach(t -> t.dependsOn(myOwnTask));
      // because the GeneratePluginExtension is not already filled up with the values, run the confing after the evaluation
      prj.afterEvaluate (project -> {
         File generateDirectory = new File (project.getBuildDir(), generateExt.getGenerateDir() + "/java");
         // add the generated sources to the java sources
         javaPluginConvention.getSourceSets().maybeCreate(SourceSet.MAIN_SOURCE_SET_NAME).getJava().srcDir(generateDirectory);
         for (String prjDep : generateExt.getProjectDependence()){
            Project dependenceProject = project.getRootProject().getChildProjects().get(prjDep);
            if (dependenceProject == null){
               String error = "Project for projectDependence property '" + generateExt.getProjectDependence() + "' not found";
               project.getLogger().error(error);
               throw new RuntimeException(error);
            }
            else {
               // if a project dependency is defined execute the task after the build of the dependent project jar
               myOwnTask.dependsOn(dependenceProject.getTasksByName(JavaPlugin.JAR_TASK_NAME,true));
            }
         }
      });
   }
   


   // start the generate task in a new vm 
   // because the needed project dependency (compile result) elsewise not visible
   public static class DomainDefGenerateTask extends JavaExec {
      private GeneratePluginExtension generateExt;
      public DomainDefGenerateTask() {
         super();
      }
      
      public void setDomainDefExt(GeneratePluginExtension domainDefExt) {
         this.generateExt = domainDefExt;
      }

      @Override
      public String getMain() {
         return generateExt.getMain();
      }
      @Override
      public void exec() {
         File baseDir = getProject().getProjectDir();
         File builddir = getProject().getBuildDir();
         File outputdir = new File (builddir, generateExt.getGenerateDir() );
         File configdir = baseDir;
         if (generateExt.getConfigDir() != null){
            configdir = new File (configdir, generateExt.getConfigDir());
         }
//         DefinitionProcessor defProc = new DefinitionProcessor ();
//         defProc.setConfig (domainDefExt.getConfigFileName(), outputdir, builddir, configdir, baseDir);
         if (generateExt.getProjectDependence() != null ){
            List<String> args = new ArrayList<String>();
            args.add (CONFIG + "=" + generateExt.getConfigFileName());
            args.add (OUTPUTDIR + "="  + outputdir.getAbsolutePath());
            args.add (BUILDDIR + "=" + builddir.getAbsolutePath());
            args.add (CONFIGDIR + "=" + configdir.getAbsolutePath());
            args.add (BASEDIR + "=" + baseDir.getAbsolutePath());
            setGenerateClassPath ();
            setArgs(args);
            super.exec();
         }
      }

      private void setGenerateClassPath() {
         JavaPluginConvention javaPlugin = getProject().getConvention().getPlugin(JavaPluginConvention.class);
         FileCollection runtimeClassPath = javaPlugin.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath();
         for (String prjDep : generateExt.getProjectDependence()){
            Project dependenceProject = getProject().getRootProject().getChildProjects().get(prjDep);
            JavaPluginConvention dependenceJavaPlugin = dependenceProject.getConvention().getPlugin(JavaPluginConvention.class);
            FileCollection dependenceRuntimeClassPath = dependenceJavaPlugin.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME).getRuntimeClasspath();
            runtimeClassPath = runtimeClassPath.plus(dependenceRuntimeClassPath);
         }
         setClasspath(runtimeClassPath);
      }
   }
    
   
   public static class GeneratePluginExtension {
      private String main = "com.adcubum.polyserv.domain_generate.modeldef.generate.DefinitionProcessor";
      private String configFileName;
      private String configDir;
      private String generateDir;
      private List<String> projectDependence = new ArrayList<String>();
      
      public String getMain() {
         return main;
      }

      public void setMain(String main) {
         this.main = main;
      }



      public List<String> getProjectDependence() {
         if (projectDependence == null){
            return Collections.emptyList();
         }
         return projectDependence;
      }

      public void setProjectDependence(List<String> projectDependence) {
         this.projectDependence = projectDependence;
         
      }

      public  GeneratePluginExtension (){
         configFileName = "generate.config";
         configDir = null;
         generateDir = "generated/src";
      }

      public String getConfigFileName() {
         return configFileName;
      }

      public String getGenerateDir() {
         return generateDir;
      }

      public void setGenerateDir(String generateDir) {
         this.generateDir = generateDir;
      }

      public void setConfigFileName(String configFileName) {
         this.configFileName = configFileName;
      }

      public String getConfigDir() {
         return configDir;
      }

      public void setConfigDir(String configDir) {
         this.configDir = configDir;
      }
   }
}
