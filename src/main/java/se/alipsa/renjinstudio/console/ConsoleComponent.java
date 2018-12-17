package se.alipsa.renjinstudio.console;

import javafx.scene.control.TextArea;
import org.eclipse.aether.repository.RemoteRepository;
import org.renjin.aether.AetherFactory;
import org.renjin.aether.AetherPackageLoader;
import org.renjin.eval.EvalException;
import org.renjin.eval.Session;
import org.renjin.eval.SessionBuilder;
import org.renjin.script.RenjinScriptEngineFactory;
import se.alipsa.renjinstudio.RenjinStudio;
import se.alipsa.renjinstudio.utils.ExceptionAlert;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ConsoleComponent extends TextArea {

    private ScriptEngine engine;

    static RemoteRepository mavenCentral() {
        return new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build();
    }

    static RemoteRepository renjinRepo() {
        return new RemoteRepository.Builder("renjin", "default", "https://nexus.bedatadriven.com/content/groups/public/").build();
    }

    public ConsoleComponent(RenjinStudio gui) {
        setText("Console");
        initRenjin();
    }

    private void initRenjin() {
        RenjinScriptEngineFactory factory = new RenjinScriptEngineFactory();
        List<RemoteRepository> repositories = new ArrayList<>();
        repositories.add(renjinRepo());
        repositories.add(mavenCentral());
        ClassLoader parentClassLoader = getClass().getClassLoader();

        AetherPackageLoader loader = new AetherPackageLoader(parentClassLoader, repositories);

        Session session = new SessionBuilder()
            .withDefaultPackages()
            .setPackageLoader(loader)
            .build();

        engine = factory.getScriptEngine(session);
    }

    public void runScript(String script) {

        StringWriter outputWriter = new StringWriter();
        engine.getContext().setWriter(outputWriter);
        engine.getContext().setErrorWriter(outputWriter);
        try {
            engine.eval(script);
        } catch (ScriptException | EvalException e) {
            ExceptionAlert.showAlert("Error running R code", e);
        }
        setText(getText() + "\n" + outputWriter.toString());
        try {
            outputWriter.close();
        } catch (IOException e) {
            ExceptionAlert.showAlert("Failed to close writer capturing renjin results", e);
        }
    }
}
