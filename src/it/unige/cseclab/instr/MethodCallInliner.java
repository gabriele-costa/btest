package it.unige.cseclab.instr;

import org.xmlpull.v1.XmlPullParserException;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.options.Options;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public class MethodCallInliner {

    public final static String TAG = "GACALL";

    public final static String WORK_DIR = "./out";

    public static String[] args = {"-v", "-d", WORK_DIR};

    public static void instrument(String apk, Set<String> api) {

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_dex);

        Options.v().set_android_jars("/home/avalz/Android/Sdk/platforms/");

        Options.v().set_process_dir(Collections.singletonList(apk));

        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);

        // Options.v().set_android_api_version(19); // 4.4.2
        // Options.v().set_android_api_version(22); // 5.1

        // resolve the PrintStream and System soot-classes
        //Scene.v().addBasicClass("android.os.Debug",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        Scene.v().addBasicClass("android.util.Log", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.StringBuilder", SootClass.SIGNATURES);

        ProcessManifest processMan = null;
        try {
            processMan = new ProcessManifest(apk);
        } catch (IOException | XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (processMan != null) {
            processMan.addPermission("android.permission.WRITE_EXTERNAL_STORAGE");
            processMan.getActivities();
            //System.out.println(processMan.targetSdkVersion());
        }

        PackManager.v().getPack("jtp").add(
                new Transform("jtp.instrumentApi", new ApiInstrumenter(api)));

        PackManager.v().getPack("jtp").add(
                new Transform("jtp.instrumentCall", new CallInstrumenter()));

        soot.Main.main(args);
    }

}
